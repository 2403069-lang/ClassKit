ClassKit

An offline Android app that helps RUET CSE students manage their semester:
class routine, assignments, class tests and a daily schedule, in one place.

Built with Java, XML and SQLite. No internet or account is needed, and all
data stays on the phone.

Features

- Semester selection:choose from CSE 1-1 to CSE 4-2. The choice is remembered after the app is closed.
- Class routine: a weekly grid (Saturday to Wednesday). Tap a slot to pick a course for your semester, change it, or remove it. Lab sessions merge across their time slots.
- Assignments and class tests: add a course, a date, a description and an optional photo or file. Delete items you no longer need.
- Search: find assignments and class tests by course, date or description.
- Today's and Tomorrow's schedule: the day's classes, plus assignments and tests due within 2 days.
- Home screen widget: today's classes and upcoming items without opening the app.
- Time-based greeting: on the dashboard (morning, afternoon, evening, night).

Tech

- Java and XML layouts
- SQLite through SQLiteOpenHelper (courses, routine, assignments and class tests tables)
- SharedPreferences for the selected semester
- AppWidgetProvider for the home screen widget

How to run

1. Open the project in Android Studio.
2. Connect a phone or start an emulator.
3. Click Run.
