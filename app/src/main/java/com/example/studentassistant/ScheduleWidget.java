package com.example.studentassistant;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleWidget extends AppWidgetProvider {

    // Same time slots and lab-merge groups as Class Routine
    private static final String[] TIMES = {
            "8:00-8:50",
            "8:50-9:40",
            "9:40-10:30",
            "10:50-11:40",
            "11:40-12:30",
            "12:30-1:20",
            "2:30-3:20",
            "3:20-4:10",
            "4:10-5:00"
    };

    private static final int[] GROUP_LEADER = {
            0, 0, 0,
            3, 3,
            5,
            6, 6, 6
    };

    // Show at most this many upcoming items (the widget is small)
    private static final int MAX_UPCOMING = 3;

    // Called by Android when the widget is added or its timer fires
    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int id : appWidgetIds) {
            updateWidget(context, appWidgetManager, id);
        }
    }

    // Also refresh when the date changes at midnight
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (Intent.ACTION_DATE_CHANGED.equals(intent.getAction())) {
            refreshAll(context);
        }
    }

    // Other screens call this to refresh every widget on the home screen
    public static void refreshAll(Context context) {

        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        ComponentName component =
                new ComponentName(context, ScheduleWidget.class);

        int[] ids = manager.getAppWidgetIds(component);

        for (int id : ids) {
            updateWidget(context, manager, id);
        }
    }

    private static void updateWidget(Context context,
                                     AppWidgetManager manager,
                                     int widgetId) {

        RemoteViews views =
                new RemoteViews(context.getPackageName(), R.layout.widget_schedule);

        String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH)
                .format(Calendar.getInstance().getTime());

        SharedPreferences prefs =
                context.getSharedPreferences("StudentAssistant", Context.MODE_PRIVATE);

        String semester = prefs.getString("semester", "Select Semester");

        views.setTextViewText(R.id.widgetTitle, "Today - " + dayName);
        views.setTextViewText(R.id.widgetBody, buildBody(context, semester, dayName));

        // Tapping the widget opens the app
        Intent openApp = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent);

        manager.updateAppWidget(widgetId, views);
    }

    // =========================================================
    // WIDGET TEXT = TODAY'S CLASSES + UPCOMING REMINDERS
    // =========================================================

    private static String buildBody(Context context, String semester, String dayName) {

        StringBuilder sb = new StringBuilder();

        sb.append(buildClasses(context, semester, dayName));

        String upcoming = buildUpcoming(context);

        if (!upcoming.isEmpty()) {
            sb.append("\n\nUpcoming:\n").append(upcoming);
        }

        return sb.toString();
    }

    // ---------------------------------------------------------
    // PART 1: TODAY'S CLASSES (labs merged into one line)
    // ---------------------------------------------------------

    private static String buildClasses(Context context, String semester, String dayName) {

        if (semester.equals("Select Semester")) {
            return "Open the app and select your semester.";
        }

        if (dayName.equals("Thursday") || dayName.equals("Friday")) {
            return "Off day - no classes.";
        }

        String[] slotCodes = new String[9];

        DatabaseHelper db = new DatabaseHelper(context);

        Cursor cursor = db.getRoutineForDay(semester, dayName);

        while (cursor.moveToNext()) {

            String startTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_START_TIME));

            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_COURSE_CODE));

            for (int i = 0; i < TIMES.length; i++) {
                if (TIMES[i].equals(startTime)) {
                    slotCodes[i] = code;
                    break;
                }
            }
        }

        cursor.close();
        db.close();

        StringBuilder sb = new StringBuilder();

        int[] leaders = {0, 3, 5, 6};

        for (int leader : leaders) {

            // Find the last slot that belongs to this block
            int last = leader;

            for (int i = 0; i < GROUP_LEADER.length; i++) {
                if (GROUP_LEADER[i] == leader) {
                    last = i;
                }
            }

            // A block with the same course in every slot is a merged lab
            boolean allSame = (slotCodes[leader] != null);

            for (int i = leader; i <= last && allSame; i++) {
                if (!slotCodes[leader].equals(slotCodes[i])) {
                    allSame = false;
                }
            }

            if (last > leader && allSame) {

                addLine(sb, slotStart(leader), slotEnd(last), slotCodes[leader]);

            } else {

                for (int i = leader; i <= last; i++) {
                    if (slotCodes[i] != null) {
                        addLine(sb, slotStart(i), slotEnd(i), slotCodes[i]);
                    }
                }
            }
        }

        if (sb.length() == 0) {
            return "No classes scheduled.";
        }

        return sb.toString().trim();
    }

    // ---------------------------------------------------------
    // PART 2: ASSIGNMENTS / CLASS TESTS DUE WITHIN 2 DAYS
    // ---------------------------------------------------------

    private static String buildUpcoming(Context context) {

        List<DueItem> items = new ArrayList<>();

        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);

        long todayMillis = todayCal.getTimeInMillis();

        SimpleDateFormat storageFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        DatabaseHelper db = new DatabaseHelper(context);

        // Assignments
        Cursor assignmentCursor = db.getAllAssignments();

        while (assignmentCursor.moveToNext()) {

            String raw = assignmentCursor.getString(
                    assignmentCursor.getColumnIndexOrThrow(DatabaseHelper.ASSIGNMENT_DUE_DATE));

            String code = assignmentCursor.getString(
                    assignmentCursor.getColumnIndexOrThrow(DatabaseHelper.ASSIGNMENT_COURSE_CODE));

            Integer diff = diffDays(storageFormat, raw, todayMillis);

            if (diff != null && diff >= 0 && diff <= 2) {
                items.add(new DueItem("📝", code, "due", diff));
            }
        }

        assignmentCursor.close();

        // Class tests
        Cursor testCursor = db.getAllClassTests();

        while (testCursor.moveToNext()) {

            String raw = testCursor.getString(
                    testCursor.getColumnIndexOrThrow(DatabaseHelper.TEST_DATE));

            String code = testCursor.getString(
                    testCursor.getColumnIndexOrThrow(DatabaseHelper.TEST_COURSE_CODE));

            Integer diff = diffDays(storageFormat, raw, todayMillis);

            if (diff != null && diff >= 0 && diff <= 2) {
                items.add(new DueItem("📋", code, "test", diff));
            }
        }

        testCursor.close();
        db.close();

        if (items.isEmpty()) {
            return "";
        }

        Collections.sort(items, (a, b) -> Integer.compare(a.diffDays, b.diffDays));

        StringBuilder sb = new StringBuilder();

        int shown = Math.min(items.size(), MAX_UPCOMING);

        for (int i = 0; i < shown; i++) {

            DueItem item = items.get(i);

            String when;

            if (item.diffDays == 0) {
                when = "today";
            } else if (item.diffDays == 1) {
                when = "tomorrow";
            } else {
                when = "in 2 days";
            }

            sb.append(item.emoji).append(" ")
                    .append(item.code).append(" - ")
                    .append(item.kind).append(" ").append(when)
                    .append("\n");
        }

        if (items.size() > shown) {
            sb.append("+ ").append(items.size() - shown).append(" more");
        }

        return sb.toString().trim();
    }

    private static Integer diffDays(SimpleDateFormat format, String raw, long todayMillis) {

        try {

            Date parsed = format.parse(raw);

            Calendar dueCal = Calendar.getInstance();
            dueCal.setTime(parsed);
            dueCal.set(Calendar.HOUR_OF_DAY, 0);
            dueCal.set(Calendar.MINUTE, 0);
            dueCal.set(Calendar.SECOND, 0);
            dueCal.set(Calendar.MILLISECOND, 0);

            long diffMillis = dueCal.getTimeInMillis() - todayMillis;

            return (int) Math.round(diffMillis / (double) (24L * 60 * 60 * 1000));

        } catch (ParseException e) {

            return null;
        }
    }

    private static class DueItem {

        String emoji;
        String code;
        String kind;   // "due" for assignments, "test" for class tests
        int diffDays;

        DueItem(String emoji, String code, String kind, int diffDays) {
            this.emoji = emoji;
            this.code = code;
            this.kind = kind;
            this.diffDays = diffDays;
        }
    }

    // ---------------------------------------------------------
    // SMALL HELPERS
    // ---------------------------------------------------------

    private static void addLine(StringBuilder sb, String start, String end, String code) {
        sb.append(start).append(" - ").append(end)
                .append("   ").append(code).append("\n");
    }

    private static String slotStart(int index) {
        return TIMES[index].split("-")[0];
    }

    private static String slotEnd(int index) {
        return TIMES[index].split("-")[1];
    }
}