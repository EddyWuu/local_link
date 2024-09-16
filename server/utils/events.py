import datetime
import random
import geopy.geocoders
from geopy.geocoders import Nominatim
import certifi
import ssl

count = 0


def parseEventData(eventJSON):
    global count
    # we have to use these get statements so that in case any of these keys don't exist in the json, it won't fail
    eventID = eventJSON.get("id") or str(count)
    title = eventJSON.get("title") or f"Community Event {count}"
    date_time = eventJSON.get("start_local") or "12:00:00"
    description = eventJSON.get("description") or "Community Event"
    city = eventJSON.get("geo", {}).get("address", {}).get("locality") or "Waterloo"
    country = (
        eventJSON.get("geo", {}).get("address", {}).get("country_code") or "Canada"
    )
    address = (
        eventJSON.get("geo", {}).get("address", {}).get("formatted_address")
        or "Waterloo, Canada"
    )
    date_posted = eventJSON.get("first_seen") or ""
    event_labels = eventJSON.get("labels") or []

    if date_time:
        date, time = date_time.split("T")
    else:
        date, time = "", ""

    duration_seconds = eventJSON.get("duration") or 0

    if duration_seconds is not None:
        hours, remainder = divmod(duration_seconds, 3600)
        minutes, seconds = divmod(remainder, 60)
        duration = f"{hours}h {minutes}m {seconds}s"
    else:
        duration = 0

    host = "Unknown"

    event_dict = {
        "eventID": eventID,
        "title": title,
        "date": date,
        "time": time,
        "description": description,
        "host": host,
        "imageResId": 0,  # placeholder
        "duration": duration,
        "city": city,
        "country": country,
        "address": address,
        "datePosted": date_posted,
        "registered": False,
        "isVolunteerEvent": False,
        "labels": event_labels,
    }

    count += 1

    return event_dict


def parseVolunteerData(eventJson):
    global count
    eventDates = eventJson.get("dates")

    eventID = str(eventJson.get("id")) or count
    title = eventJson.get("title") or f"Volunteer Event {count}"
    date = (
        ((datetime.datetime.today() + datetime.timedelta(days=1)).strftime("%Y-%m-%d"))
        if eventDates == "Ongoing"
        else parse_volunteer_dates(eventDates)
    )
    time = parse_volunteer_time(eventDates) or "12:00:00"
    description = eventJson.get("description") or "Volunteer Event"
    host = eventJson.get("organization").get("name") or ""
    duration = eventJson.get("duration") or ""
    city, country, address = parse_volunteer_address(eventJson.get("audience"))

    event_dict = {
        "eventID": eventID,
        "title": title,
        "date": date,
        "time": time,
        "description": description.replace("\r", "").replace("\n", ""),
        "host": host,
        "imageResId": 0,  # placeholder
        "duration": duration,
        "city": city,
        "country": country,
        "address": address,
        "datePosted": "",
        "registered": False,
        "isVolunteerEvent": True,
        "labels": [],
    }

    count += 1

    return event_dict


def parse_volunteer_dates(dateObj):
    today = datetime.datetime.today()
    day = (today.replace(day=28) + datetime.timedelta(days=4)).replace(day=1)
    month = (day.replace(day=28) + datetime.timedelta(days=4)).replace(
        day=1
    ) - datetime.timedelta(days=1)

    date = day + datetime.timedelta(days=random.randint(0, (month - day).days))
    return date.strftime("%Y-%m-%d")


def parse_volunteer_time(dateObj):
    minutes = datetime.datetime.combine(datetime.datetime.today(), datetime.time(10))
    hours = datetime.datetime.combine(datetime.datetime.today(), datetime.time(16))
    time = f"{random.randint(10,16)}:15:00"
    return time


def parse_volunteer_address(eventAddrDict):
    if "longitude" and "latitude" in eventAddrDict:
        ctx = ssl.create_default_context(cafile=certifi.where())
        geopy.geocoders.options.default_ssl_context = ctx
        geolocator = Nominatim(user_agent="locallink", scheme="http")
        address = geolocator.reverse(
            (eventAddrDict["latitude"], eventAddrDict["longitude"])
        ).address
        addressList = address.split(",")
        country = addressList[-1]
        city = addressList[-5]
        print(f"Address: {address} [{type(address)}]")
        return city, country, address
    elif "regions" in eventAddrDict:
        country = "Canada"
        city = ""
        address = eventAddrDict["regions"][0]
        return city, country, address
    return "Waterloo", "Canada", "Canada"
