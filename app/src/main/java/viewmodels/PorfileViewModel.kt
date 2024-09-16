package viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.adventureaid.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import models.Unavailability
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import services.ApiService
import models.User

class ProfileViewModel: ViewModel() {
    //  URL for Flask server
    private val baseUrl = "http://10.0.2.2:5000/"

    private fun createRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder().build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val _currentPic = MutableStateFlow(R.drawable.profile1) // inital pick
    val currentPic: StateFlow<Int> = _currentPic

    fun updateProfilePic(newPic: Int) {
        _currentPic.value = newPic
    }

    // ApiService
    private val apiService = createRetrofit().create(ApiService::class.java)
    var responseMessage = mutableStateOf("")

    fun createProfileAPIReq(user: User) {
        val call = apiService.createProfile(user)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    responseMessage.value = "Profile created successfully"
                    Log.d("ProfileViewModel", "Profile created successfully")
                } else {
                    val errorMessage = "Failed to create profile: ${response.errorBody()?.string()}"
                    responseMessage.value = errorMessage
                    Log.e("ProfileViewModel", errorMessage)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                responseMessage.value = errorMessage
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }

    fun addUnavailability(unavailability: Unavailability) {
        val call = apiService.addUnavailability(unavailability)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    responseMessage.value = "Unavailability added successfully"
                    Log.d("ProfileViewModel", "Unavailability added successfully")
                } else {
                    val errorMessage = "Failed to add unavailability: ${response.errorBody()?.string()}"
                    responseMessage.value = errorMessage
                    Log.e("ProfileViewModel", errorMessage)
                }
            }


            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                responseMessage.value = errorMessage
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }

    private val _unavailabilities2 = MutableStateFlow<List<Unavailability>>(emptyList())
    val unavailabilities2: StateFlow<List<Unavailability>> get() = _unavailabilities2

    val unavailabilities: SnapshotStateList<Unavailability> = mutableStateListOf()
    fun getUnavailability(profileID: String) {
        val call = apiService.getUnavailability(profileID)
        call.enqueue(object : Callback<List<Unavailability>> {
            override fun onResponse(call: Call<List<Unavailability>>, response: Response<List<Unavailability>>) {
                if (response.isSuccessful) {
                    unavailabilities.clear()
                    unavailabilities.addAll(response.body() ?: emptyList())
                    responseMessage.value = " fetched "
                    Log.d("ProfileViewModel", " fetched ")
                } else {
                    val errorMessage = "Failed : ${response.errorBody()?.string()}"
                    responseMessage.value = errorMessage
                    Log.e("ProfileViewModel", errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Unavailability>>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                responseMessage.value = errorMessage
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }
    fun deleteUnavailability(profileID: String, date: String, startTime: String, endTime: String) {
        val call = apiService.deleteUnavailability(profileID, date, startTime, endTime)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    responseMessage.value = "deleted"
                    Log.d("ProfileViewModel", " deleted successfully")
                    // Refresh the list after deletion
                    getUnavailability(profileID)
                } else {
                    val errorMessage = "Failed : ${response.errorBody()?.string()}"
                    responseMessage.value = errorMessage
                    Log.e("ProfileViewModel", errorMessage)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                responseMessage.value = errorMessage
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }
    fun getUnavailability2(profileID: String) {
        val call = apiService.getUnavailability(profileID)
        call.enqueue(object : Callback<List<Unavailability>> {
            override fun onResponse(call: Call<List<Unavailability>>, response: Response<List<Unavailability>>) {
                if (response.isSuccessful) {
                    _unavailabilities2.value = response.body() ?: emptyList()
                    responseMessage.value = "Unavailabilities2 fetched"
                    Log.d("ProfileViewModel", "Unavailabilities2 fetched")
                } else {
                    val errorMessage = "Failed: ${response.errorBody()?.string()}"
                    responseMessage.value = errorMessage
                    Log.e("ProfileViewModel", errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Unavailability>>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                responseMessage.value = errorMessage
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }

    fun createDummyDataAndSend() {
        val dummyProfile = User(
            profileID = "20000",
            firstName = "lanka",
            lastName = "bunny",
            email = "bunny boy",
            password = "bunny man",
            city = "bunny city",
            country = "Bunny",
            profilePicResId = 0,
            preferences = "",
            timeZone = "EST"
        )
        createProfileAPIReq(dummyProfile)
    }
}
