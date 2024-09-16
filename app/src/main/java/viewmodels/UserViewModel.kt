package viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import models.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern
import services.ApiService
import services.EmailCheck
import services.EmailExistsResponse
import services.LoginRequest
import services.LoginResponse
import services.ProfileIdResponse

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences: SharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _responseMessage = MutableStateFlow("")
    val responseMessage: StateFlow<String> get() = _responseMessage

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> get() = _isProcessing

    private val _loggedInUser = MutableStateFlow<User?>(null)
    val loggedInUser: StateFlow<User?> = _loggedInUser.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut

    private val _userPreferences = MutableStateFlow<String?>("")
    val userPreferences: StateFlow<String?> = _userPreferences.asStateFlow()

    // Local URL for Flask server without SSL
    private val baseUrl = "http://10.0.2.2:5000/"

    private fun createRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder().build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService = createRetrofit().create(ApiService::class.java)

    init {
        loadUserSession()
    }

    fun setResponseMessage(message: String) {
        _responseMessage.value = message
    }

    private val EMAIL_REGEX = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE)
        return pattern.matcher(email).matches()
    }

    private fun validateUserInput(user: User): String? {
        val trimmedFirstName = user.firstName.trim()
        val trimmedLastName = user.lastName.trim()
        val trimmedEmail = user.email.trim()
        val trimmedPassword = user.password.trim()
        val trimmedCity = user.city.trim()
        val trimmedCountry = user.country.trim()

        return when {
            trimmedFirstName.isBlank() -> "First name cannot be empty!"
            trimmedLastName.isBlank() -> "Last name cannot be empty!"
            trimmedEmail.isBlank() -> "Email cannot be empty!"
            !isValidEmail(trimmedEmail) -> "Invalid email format!"
            trimmedPassword.isBlank() -> "Password cannot be empty!"
            trimmedCity.isBlank() -> "City cannot be empty!"
            trimmedCountry.isBlank() -> "Country cannot be empty!"
            else -> null
        }
    }

    fun signUp(user: User) {
        viewModelScope.launch {
            _isProcessing.value = true
            _responseMessage.value = ""
            clearLoginState()

            val trimmedUser = user.copy(
                firstName = user.firstName.trim(),
                lastName = user.lastName.trim(),
                email = user.email.trim(),
                password = user.password.trim(),
                city = user.city.trim(),
                country = user.country.trim()
            )

            val validationError = validateUserInput(trimmedUser)
            if (validationError != null) {
                _responseMessage.value = validationError
                _isProcessing.value = false
                return@launch
            }

            getNewProfileId { profileID ->
                if (profileID != null) {
                    val updatedUser = trimmedUser.copy(profileID = profileID)
                    checkEmailAndRegister(updatedUser)
                } else {
                    _responseMessage.value = "Couldn't generate profile ID."
                    Log.e("UserViewModel", "Profile ID was not generated.")
                    _isProcessing.value = false
                }
            }
        }
    }

    private fun checkEmailAndRegister(user: User) {
        val emailCheck = EmailCheck(user.email)
        val call = apiService.checkEmail(emailCheck)
        call.enqueue(object : Callback<EmailExistsResponse> {
            override fun onResponse(call: Call<EmailExistsResponse>, response: Response<EmailExistsResponse>) {
                if (response.isSuccessful) {
                    val emailExists = response.body()?.exists ?: false
                    if (emailExists) {
                        setResponseMessage("Email already exists")
                        _isProcessing.value = false
                    } else {
                        registerUser(user)
                    }
                } else {
                    val errorMessage = "Failed to check email: ${response.errorBody()?.string()}"
                    setResponseMessage(errorMessage)
                    Log.e("UserViewModel", errorMessage)
                    _isProcessing.value = false
                }
            }

            override fun onFailure(call: Call<EmailExistsResponse>, t: Throwable) {
                val errorMessage = "Error checking email: ${t.message}"
                setResponseMessage(errorMessage)
                Log.e("UserViewModel", errorMessage)
                _isProcessing.value = false
            }
        })
    }


    private fun registerUser(user: User) {
        val call = apiService.createProfile(user)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _responseMessage.value = "User registered successfully"
                    Log.d("UserViewModel", "User registered successfully")
                    saveUserSession(user)
                } else {
                    val errorMessage = "Failed to register user: ${response.errorBody()?.string()}"
                    _responseMessage.value = errorMessage
                    Log.e("UserViewModel", errorMessage)
                }
                _isProcessing.value = false
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Error registering user: ${t.message}"
                _responseMessage.value = errorMessage
                Log.e("UserViewModel", errorMessage)
                _isProcessing.value = false
            }
        })
    }


    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            clearLoginState() // Clear previous login state before making a login attempt

            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()
            if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
                _responseMessage.value = "Email and password cannot be empty!"
                _isProcessing.value = false
                return@launch
            }

            val loginRequest = LoginRequest(trimmedEmail, trimmedPassword)
            val call = apiService.login(loginRequest)
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            _loggedInUser.value = loginResponse.user
                            _responseMessage.value = "Login successful"
                            loginResponse.user?.let {
                                saveUserSession(it)
                                fetchUserPreferences(it.profileID)
                            }
                        } else {
                            _responseMessage.value = loginResponse?.message ?: "Login FAILED!"
                        }
                    } else {
                        _responseMessage.value = "Login failed, please check your email and password"
                    }
                    _isProcessing.value = false
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _responseMessage.value = "Error while logging in: ${t.message}"
                    _isProcessing.value = false
                }
            })
        }
    }


    private fun saveUserSession(user: User) {
        with(preferences.edit()) {
            putString("profileID", user.profileID)
            putString("firstName", user.firstName)
            putString("lastName", user.lastName)
            putString("email", user.email)
            putString("password", user.password)
            putString("city", user.city)
            putString("country", user.country)
            putInt("profilePicResId", user.profilePicResId)
            putString("preferences", user.preferences)
            apply()
        }
        Log.d("UserViewModel", "User session saved: $user")
    }

    private fun loadUserSession() {
        val profileID = preferences.getString("profileID", null)
        if (profileID != null) {
            val user = User(
                profileID = profileID,
                firstName = preferences.getString("firstName", "") ?: "",
                lastName = preferences.getString("lastName", "") ?: "",
                email = preferences.getString("email", "") ?: "",
                password = preferences.getString("password", "") ?: "",
                city = preferences.getString("city", "") ?: "",
                country = preferences.getString("country", "") ?: "",
                profilePicResId = preferences.getInt("profilePicResId", 0),
                preferences = preferences.getString("preferences", "") ?: "",
                timeZone = preferences.getString("timeZone", "") ?: ""
            )
            _loggedInUser.value = user
            _userPreferences.value = user.preferences // Set preferences directly here if loaded from session
            Log.d("UserViewModel", "User session loaded: $user")
        } else {
            Log.d("UserViewModel", "No user session found")
        }
    }

    private fun getNewProfileId(callback: (String?) -> Unit) {
        apiService.generateProfileId().enqueue(object : Callback<ProfileIdResponse> {
            override fun onResponse(call: Call<ProfileIdResponse>, response: Response<ProfileIdResponse>) {
                if (response.isSuccessful) {
                    val profileID = response.body()?.profileID
                    Log.d("UserViewModel", "Profile ID: $profileID")
                    callback(profileID)
                } else {
                    Log.e("UserViewModel", "No profileID. Response code: ${response.code()}, Error body: ${response.errorBody()?.string()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ProfileIdResponse>, t: Throwable) {
                Log.e("UserViewModel", "error in getting the new profileID", t)
                callback(null)
            }
        })
    }

    fun logout() {
        _loggedInUser.value = null
        with(preferences.edit()) {
            clear()
            apply()
        }
        _loggedInUser.value = null
        _responseMessage.value = ""
        _userPreferences.value = ""
        _isProcessing.value = false
        _isLoggedOut.value = true
        Log.d("UserViewModel", "User is logged out and session is cleared")
    }

    fun resetLogoutState() {
        _isLoggedOut.value = false
    }

    fun clearLoginState() {
        _loggedInUser.value = null
        _responseMessage.value = ""
        with(preferences.edit()) {
            clear()
            apply()
        }
    }

     fun fetchUserPreferences(profileID: String) {
        val call = apiService.getUserPreferences(profileID)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        _userPreferences.value = user.preferences
                        Log.d("UserViewModel", "User preferences fetched: ${user.preferences}")
                    } else {
                        Log.e("UserViewModel", "Failed to fetch user preferences: User is null")
                    }
                } else {
                    Log.e("UserViewModel", "Failed to fetch user preferences: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("UserViewModel", "Error fetching user preferences: ${t.message}")
            }
        })
    }

    fun updateUserPreferences(preferences: String) {
        val user = _loggedInUser.value
        if (user != null) {
            val updatedUser = user.copy(preferences = preferences)
            val call = apiService.updateUserPreferences(user.profileID, mapOf("preferences" to preferences))
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _loggedInUser.value = updatedUser
                        _userPreferences.value = preferences // Update the userPreferences state flow
                        Log.d("HomeViewModel", "Preferences updated successfully: ${_userPreferences.value}")
                    } else {
                        Log.e("HomeViewModel", "Failed to update preferences: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("HomeViewModel", "Error: ${t.message}")
                }
            })
        }
    }
}
