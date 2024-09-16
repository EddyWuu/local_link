package models

import androidx.annotation.DrawableRes

data class User (var profileID: String,
                 var firstName: String,
                 var lastName: String,
                 var email: String,
                 var password: String,
                 var city: String,
                 var country: String,
                 @DrawableRes val profilePicResId: Int,
                 var preferences: String? = null,
                 var volunteerHours: Float = 0.0f,
                 var timeZone: String,
                 var signedIn: Int = 0
)