package models

import androidx.annotation.DrawableRes

data class Event (var eventID: String,
                  var title: String,
                  var date: String,
                  var time: String,
                  var description: String,
                  var host: String,
                  @DrawableRes val imageResId: Int,
                  var duration: String,
                  var city: String,
                  var country: String,
                  var address: String,
                  var datePosted: String,
                  var registered: Boolean,
                  var isVolunteerEvent: Boolean,
                  var labels: List<String>,
)