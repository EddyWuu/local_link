from flask import Flask, request, jsonify, g
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from datetime import datetime
import os
from dotenv import load_dotenv
import requests
from utils.events import parseEventData, parseVolunteerData
from sqlalchemy.dialects.postgresql import ARRAY

# Load environment variables from .env file
load_dotenv()

print(f"DATABASE_URL: {os.getenv('DATABASE_URL')}")

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = os.getenv("DATABASE_URL")
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["SQLALCHEMY_ENGINE_OPTIONS"] = {
    # 'connect_args': {'connect_timeout': 10},  # timeout to 10 secs
}
db = SQLAlchemy(app)
migrate = Migrate(app, db)


class User(db.Model):
    __tablename__ = "users"
    profileID = db.Column(db.String, primary_key=True)
    firstName = db.Column(db.String, nullable=False)
    lastName = db.Column(db.String, nullable=False)
    email = db.Column(db.String, nullable=False, unique=True)
    password = db.Column(db.String, nullable=False)
    city = db.Column(db.String, nullable=False)
    country = db.Column(db.String, nullable=False)
    profilePicResId = db.Column(db.Integer, nullable=True)
    preferences = db.Column(db.String, nullable=False)
    # events = db.Column(db.Integer, default=0)
    # volunteerHours = db.Column(db.Float, default=0.0)
    # timeZone = db.Column(db.String, nullable=True)


class UserEvent(db.Model):
    __tablename__ = "user_events"
    profileID = db.Column(db.String, db.ForeignKey("users.profileID"), primary_key=True)
    eventID = db.Column(db.String, primary_key=True)


class Unavailability(db.Model):
    __tablename__ = "unavailabilities"
    profileID = db.Column(db.String, db.ForeignKey("users.profileID"), primary_key=True)
    date = db.Column(db.String, primary_key=True)
    startTime = db.Column(db.String, nullable=False)
    endTime = db.Column(db.String, nullable=False)


class Event(db.Model):
    __tablename__ = "events"
    eventID = db.Column(db.String, primary_key=True)
    title = db.Column(db.String, nullable=False)
    date = db.Column(db.String, nullable=False)
    time = db.Column(db.String, nullable=False)
    description = db.Column(db.String, nullable=False)
    host = db.Column(db.String, nullable=False)
    imageResId = db.Column(db.Integer, nullable=True)
    duration = db.Column(db.String, nullable=False)
    city = db.Column(db.String, nullable=False)
    country = db.Column(db.String, nullable=False)
    address = db.Column(db.String, nullable=False)
    datePosted = db.Column(db.String, nullable=False, default=datetime.utcnow)
    registered = db.Column(db.Boolean, default=False)
    isVolunteerEvent = db.Column(db.Boolean, default=False)
    label = db.Column(db.String, nullable=True)
    labels = db.Column(ARRAY(db.String), nullable=True)
    updated = db.Column(db.String, nullable=True)
    phq_attendance = db.Column(db.Integer, nullable=True)
    rank = db.Column(db.Integer, nullable=True)
    local_rank = db.Column(db.Integer, nullable=True)


@app.before_request
def before_request():
    if not hasattr(g, "first_request_done"):
        g.first_request_done = True
        create_tables()


def create_tables():
    db.create_all()


@app.route("/")
def index():
    return "Welcome nerds to my domain"


@app.route("/user/login", methods=["POST"])
def login():
    data = request.get_json()
    email = data.get("email")
    password = data.get("password")
    user = User.query.filter_by(email=email).first()

    if user is None:
        return jsonify({"success": False, "message": "Incorrect email address"}), 401

    if user.password != password:
        return jsonify({"success": False, "message": "Incorrect password"}), 401

    return (
        jsonify(
            {
                "success": True,
                "message": "Login successful",
                "user": {
                    "profileID": user.profileID,
                    "firstName": user.firstName,
                    "lastName": user.lastName,
                    "email": user.email,
                    "city": user.city,
                    "country": user.country,
                    "profilePicResId": user.profilePicResId,
                    "preferences": user.preferences,
                },
            }
        ),
        200,
    )


@app.route("/user", methods=["POST"])
def create_user():
    data = request.get_json()
    new_user = User(
        profileID=data["profileID"],
        firstName=data["firstName"],
        lastName=data["lastName"],
        email=data["email"],
        password=data["password"],
        city=data["city"],
        country=data["country"],
        profilePicResId=data.get("profilePicResId"),
        preferences="",
    )
    db.session.add(new_user)
    db.session.commit()
    return jsonify({"message": "User created successfully"}), 201


@app.route("/user/<profileID>", methods=["GET"])
def get_user(profileID):
    user = User.query.filter_by(profileID=profileID).first()
    if user:
        return jsonify(
            {
                "profileID": user.profileID,
                "firstName": user.firstName,
                "lastName": user.lastName,
                "email": user.email,
                "password": user.password,
                "city": user.city,
                "country": user.country,
                "profilePicResId": user.profilePicResId,
                "preferences": user.preferences,
            }
        )
    else:
        return jsonify({"message": "User not found"}), 404


# TEMP FUNCTION, delete and replace once login works
@app.route("/user/random", methods=["GET"])
def get_user_by_id():
    user = User.query.filter_by(profileID="20000").first()
    if user:
        return jsonify(
            {
                "profileID": user.profileID,
                "firstName": user.firstName,
                "lastName": user.lastName,
                "email": user.email,
                "password": user.password,
                "city": user.city,
                "country": user.country,
                "profilePicResId": user.profilePicResId,
                "preferences": user.preferences,
            }
        )
    else:
        return jsonify({"message": "User not found"}), 404


@app.route("/user/check_email", methods=["POST"])
def check_email():
    try:
        data = request.get_json()
        email = data.get("email")
        user = User.query.filter_by(email=email).first()

        return jsonify({"exists": user is not None}), 200

    except Exception as e:

        app.logger.error(f"Error in checking unqiue email: {str(e)}")

        return jsonify({"error": "Internal server error"}), 500


@app.route("/generate_profile_id", methods=["GET"])
def generate_profile_id():

    try:
        highest_profile_id = db.session.query(db.func.max(User.profileID)).scalar()

        if highest_profile_id is None:
            new_profile_id = "1"
        else:
            new_profile_id = str(int(highest_profile_id) + 1)

        app.logger.info(f"Generated new profile ID: {new_profile_id}")
        return jsonify({"profileID": new_profile_id}), 200
    except Exception as e:

        app.logger.error(f"Error generating profile ID: {str(e)}")
        return jsonify({"error": "Failed to generate profile ID"}), 500


@app.route("/event", methods=["GET"])
def get_events():
    params = {
        # "category": "festivals",
        "country": "CA",
        "saved_location.location_id": "P-Y3AiUOG7NLm7s34wNtbA",
    }
    eventResponse = requests.get(
        url="https://api.predicthq.com/v1/events",
        headers={
            "Authorization": f"Bearer {os.getenv('EVENT_API_KEY')}",
            "Accept": "application/json",
        },
        params=params,
    )

    eventResObj = eventResponse.json()
    eventList = []

    if "results" in eventResObj:
        data = eventResObj["results"]
        for event in data:
            eventList.append(parseEventData(event))

    volunteerUrl = (
        "https://www.volunteerconnector.org/api/search/?pc=N2L&md=10&se=&so=Proximity"
    )
    volunteerResponse = requests.get(url=volunteerUrl)

    volunteerResObj = volunteerResponse.json()

    if "results" in volunteerResObj:
        data = volunteerResObj["results"]
        for volEvent in data[:20]:
            eventList.append(parseVolunteerData(volEvent))

    sorted_events = sorted(
        eventList, key=lambda x: datetime.strptime(x["date"], "%Y-%m-%d")
    )
    return sorted_events


@app.route("/searchEvent", methods=["GET"])
def search_event():
    search = request.args.get("search") or ""
    country = request.args.get("country")
    city = request.args.get("city")
    date = request.args.get("date")
    category = request.args.get("category")

    params = {"saved_location.location_id": "P-Y3AiUOG7NLm7s34wNtbA", "limit": 50}

    if search and search != "":
        params["q"] = search
    if country and country != "":
        params["country"] = country
    if date and date != "":
        params["start.gte"] = date
        params["end.lte"] = date
    if category and category != "":
        params["category"] = category

    response = requests.get(
        url="https://api.predicthq.com/v1/events",
        headers={
            "Authorization": f"Bearer {os.getenv('EVENT_API_KEY')}",
            "Accept": "application/json",
        },
        params=params,
    )

    responseObj = response.json()
    eventList = []

    if "results" in responseObj:
        data = responseObj["results"]
        for event in data:
            eventList.append(parseEventData(event))

    volunteerUrl = f"https://www.volunteerconnector.org/api/search/?pc=N2L&md=15&se={search}&so=Proximity"
    volunteerResponse = requests.get(url=volunteerUrl)

    volunteerResObj = volunteerResponse.json()

    if "results" in volunteerResObj:
        data = volunteerResObj["results"]
        for volEvent in data[:20]:
            eventList.append(parseVolunteerData(volEvent))

    sorted_events = sorted(
        eventList, key=lambda x: datetime.strptime(x["date"], "%Y-%m-%d")
    )
    return sorted_events

@app.route("/addRegisteredEvent/<profileID>", methods=["POST"])
def add_registered_event(profileID):
    data = request.get_json()
    new_userEvent = UserEvent(
        profileID=profileID,
        eventID=data["eventID"]
    )
    new_event = Event(
        eventID = data["eventID"],
        title = data["title"],
        date = data["date"],
        time = data["time"],
        description = data["description"],
        host = data["host"],
        imageResId = data["imageResId"],
        duration = data["duration"],
        city = data["city"],
        country = data["country"],
        address = data["address"],
        datePosted = data["datePosted"],
        registered = data["registered"],
        isVolunteerEvent = data["isVolunteerEvent"],
        labels = data["labels"]
    )
    
    db.session.add(new_userEvent)
    db.session.commit()

    existing_event = Event.query.filter_by(eventID=data["eventID"]).first()
    if not existing_event:
        db.session.add(new_event)
        db.session.commit()

    return jsonify({"message": "New registered event added"}), 201

@app.route("/getRegisteredEvents/<profileID>", methods=["GET"])
def get_registered_events(profileID):

    #gets all user event ids associated with a specific user
    user_events = UserEvent.query.filter_by(profileID=profileID).all()
    event_ids = [ue.eventID for ue in user_events]

    #gets the full events based on those ids
    events = Event.query.filter(Event.eventID.in_(event_ids)).all()
    
    events_data = [
        {
            "eventID": event.eventID,
            "title": event.title,
            "date": event.date,
            "time": event.time,
            "description": event.description,
            "host": event.host,
            "imageResId": event.imageResId,
            "duration": event.duration,
            "city": event.city,
            "country": event.country,
            "address": event.address,
            "datePosted": event.datePosted,
            "registered": event.registered,
            "isVolunteerEvent": event.isVolunteerEvent,
            "labels": event.labels
        }
        for event in events
    ]
    return jsonify(events_data), 200
    
@app.route("/deleteUserEvent/<profileID>/<eventID>", methods=["DELETE"])
def delete_user_event(profileID, eventID):
    user_event = UserEvent.query.filter_by(profileID=profileID, eventID=eventID).first()    
    if user_event:
        db.session.delete(user_event)
        db.session.commit()
        return jsonify({"message": "User event deleted successfully"}), 200
    else:
        return jsonify({"message": "User event not found"}), 404

@app.route("/user/<profileID>/preferences", methods=["GET"])
def get_user_preferences(profileID):
    user = User.query.filter_by(profileID=profileID).first()
    if user:
        return jsonify({"preferences": user.preferences})
    else:
        return jsonify({"message": "User not found"}), 404


@app.route("/user/<profileID>/preferences", methods=["PUT"])
def update_preferences(profileID):
    # logging.info(f"Received request to update preferences for profileID: {profileID}")

    data = request.get_json()
    # logging.info(f"Received data: {data}")

    user = User.query.filter_by(profileID=profileID).first()
    if user:
        # logging.info(f"User found: {user}")
        user.preferences = data.get("preferences", user.preferences)
        db.session.commit()
        # logging.info(f"Preferences updated successfully for profileID: {profileID}")
        return jsonify({"message": "Preferences updated successfully"})
    else:
        # logging.warning(f"User not found for profileID: {profileID}")
        return jsonify({"message": "User not found"}), 404


@app.route("/unavailability", methods=["POST"])
def add_unavailability():
    data = request.get_json()
    new_unavailability = Unavailability(
        profileID=data["profileID"],
        date=data["date"],
        startTime=data["startTime"],
        endTime=data["endTime"],
    )
    db.session.add(new_unavailability)
    db.session.commit()
    return jsonify({"message": "Unavailability added successfully"}), 201


@app.route("/user/<profileID>/unavailability", methods=["GET"])
def get_unavailability(profileID):
    unavailabilities = Unavailability.query.filter_by(profileID=profileID).all()
    if unavailabilities:
        return jsonify(
            [
                {
                    "profileID": u.profileID,
                    "date": u.date,
                    "startTime": u.startTime,
                    "endTime": u.endTime,
                }
                for u in unavailabilities
            ]
        )
    else:
        return jsonify({"message": "None"}), 404


@app.route(
    "/unavailability/<profileID>/<date>/<startTime>/<endTime>", methods=["DELETE"]
)
def delete_unavailability(profileID, date, startTime, endTime):
    unavailability = Unavailability.query.filter_by(
        profileID=profileID, date=date, startTime=startTime, endTime=endTime
    ).first()
    if unavailability:
        db.session.delete(unavailability)
        db.session.commit()
        return jsonify({"message": "deleted successfully"}), 200
    else:
        return jsonify({"message": "not found"}), 404


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
