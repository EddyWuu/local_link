package services

import models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import models.Event
import models.Unavailability
import retrofit2.http.DELETE

import retrofit2.http.Query

interface ApiService {

    // the ("user") has to match the post in our flaskframework. these two routes must match
    // Flask framework is listening for a post request at /user, and here we define a post
    // request to the same /user endpoint
    @POST("user")
    fun createProfile(@Body user: User): Call<Void>

    @POST("user/check_email")
    fun checkEmail(@Body emailCheck: EmailCheck): Call<EmailExistsResponse>

    @POST("user/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("generate_profile_id")
    fun generateProfileId(): Call<ProfileIdResponse>

    @GET("event")
    fun getEvents(): Call<List<Event>>

    @POST("/unavailability")
    fun addUnavailability(@Body unavailability: Unavailability): Call<Void>

    @GET("searchEvent")
    fun getSearchedEvents(
        @Query("search") search: String,
        @Query("country") country: String,
        @Query("city") city: String,
        @Query("date") date: String,
        @Query("category") category: String
    ): Call<List<Event>>

    @DELETE("deleteUserEvent/{profileID}/{eventID}")
    fun deleteRegisteredEvent(@Path("profileID") profileID: String, @Path("eventID") eventID: String): Call<Void>

    @GET("getRegisteredEvents/{profileID}")
    fun getRegisteredEvents(@Path("profileID") profileID: String): Call<List<Event>>

    @POST("addRegisteredEvent/{profileID}")
    fun addRegisteredEvent(@Path("profileID") profileID: String, @Body event: Event): Call<Void>

    @GET("user/{profileID}/unavailability")
    fun getUnavailability(@Path("profileID") profileID: String): Call<List<Unavailability>>

    @DELETE("unavailability/{profileID}/{date}/{startTime}/{endTime}")
    fun deleteUnavailability(
        @Path("profileID") profileID: String,
        @Path("date") date: String,
        @Path("startTime") startTime: String,
        @Path("endTime") endTime: String
    ): Call<Void>

    @PUT("user/{id}/preferences")
    fun updateUserPreferences(@Path("id") id: String, @Body preferences: Map<String, String>): Call<Void>

    @GET("user/random")
    fun getRandomUser(): Call<User>

    @GET("users/{profileID}/preferences")
    fun getUserPreferences(@Path("profileID") profileID: String): Call<User>


}

data class EmailCheck(val email: String)
data class EmailExistsResponse(val exists: Boolean)
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val success: Boolean, val message: String, val user: User?)
data class ProfileIdResponse(val profileID: String)

