package viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adventureaid.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import models.Event
import org.json.JSONArray
import java.io.IOException
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import models.User

class HomeViewModel: ViewModel() {
    private val baseUrl = "http://10.0.2.2:5000/"
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _searchScreenEvents = MutableStateFlow<List<Event>>(emptyList());
    val searchScreenEvents: StateFlow<List<Event>> = _searchScreenEvents.asStateFlow();

    private val _registeredEvents = MutableStateFlow<MutableList<Event>>(mutableListOf());
    val registeredEvents: StateFlow<MutableList<Event>> = _registeredEvents.asStateFlow();

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // an instance of our apiservice
    private val apiService = retrofit.create(ApiService::class.java)

    fun loadAPIEvents() {
        val call = apiService.getEvents()
        call.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body() as List<Event>
                    _events.value = events

                    // Log the entire event objects
                    for (event in events) {
                        Log.d("ProfileViewModel", "Event retrieved: $event")
                    }
                } else {
                    val errorMessage = "Failed to load events: ${response.errorBody()?.string()}"
                    Log.e("ProfileViewModel", errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }



    fun loadSearchAPIEvents(search: String, country: String, city: String, date: String, category: String) {
        val call = apiService.getSearchedEvents(search = search, country = country, city = city, date = date, category = category)
        call.enqueue(object : Callback<List<Event>>{
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    _searchScreenEvents.value = (response.body() as List<Event>)
                } else {
                    val errorMessage = "Failed to create profile: ${response.errorBody()?.string()}"
                    Log.e("ProfileViewModel", errorMessage)
                }
            }
            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }

    fun loadRegisteredEvents(profileID: String) {
        val call = apiService.getRegisteredEvents(profileID = profileID)
        call.enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body() as MutableList<Event>
                    _registeredEvents.value = events

                    Log.d("ProfileViewModel", "Successfully loaded registered events")

                } else {
                    val errorMessage = "Failed to load events: ${response.errorBody()?.string()}"
                    Log.e("ProfileViewModel", errorMessage)
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }

    fun registerEvent(event: Event, user: User) {
        if(event !in _registeredEvents.value){ //already checking to prevent duplicates, good, will prevent issues
            _registeredEvents.value = _registeredEvents.value.toMutableList().also {
                it.add(event)
            }
            registerEventAPI(event = event, user = user)
        }
        //make api call to register an event
        Log.d("HomeViewModel", "Registered Events: ${_registeredEvents.value.size}")
    }

    fun registerEventAPI(event: Event, user: User) {
        val call = apiService.addRegisteredEvent(profileID = user.profileID, event = event) //Change this to new endpoint that retrieves registered events
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("HomeViewModel", "Registered event added successfully")
                } else {
                    Log.e("HomeViewModel", "Failed to update preferences: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("HomeViewModel", "Error: ${t.message}")
            }
        })
    }

    fun removeEvent(event: Event, user: User) {
        _registeredEvents.value = _registeredEvents.value.toMutableList().also {
            it.remove(event)
        }
        //make api call to remove an event
        deleteEventAPI(event = event, user = user)
    }

    fun deleteEventAPI(event: Event, user: User) {
        val call = apiService.deleteRegisteredEvent(eventID = event.eventID, profileID = user.profileID)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("ProfileViewModel", "deleted event successfully")
                } else {
                    val errorMessage = "Failed : ${response.errorBody()?.string()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Error: ${t.message}"
                Log.e("ProfileViewModel", errorMessage)
            }
        })
    }
}