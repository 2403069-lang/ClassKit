package com.example.studentassistant;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyScheduleActivity extends AppCompatActivity {

    // Pass 0 for Today, 1 for Tomorrow
    public static final String EXTRA_DAY_OFFSET = "day_offset";

    private DatabaseHelper databaseHelper;
    private String selectedSemester;

    private TextView tvScheduleTitle;
    private TextView tvScheduleSemester;
    private LinearLayout llScheduleList;
    private TextView tvUpcomingHeader;
    private LinearLayout llUpcomingList;

    // Same 9 time slots used in Class Routine
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

    // Same lab-merge groups used in Class Routine
    private static final int[] GROUP_LEADER = {
            0, 0, 0,
            3, 3,
            5,
            6, 6, 6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_schedule);

        int dayOffset = getIntent().getIntExtra(EXTRA_DAY_OFFSET, 0);

        SharedPreferences preferences =
                getSharedPreferences("StudentAssistant", MODE_PRIVATE);

        selectedSemester =
                preferences.getString("semester", "Select Semester");

        databaseHelper = new DatabaseHelper(this);

        tvScheduleTitle = findViewById(R.id.tvScheduleTitle);
        tvScheduleSemester = findViewById(R.id.tvScheduleSemester);
        llScheduleList = findViewById(R.id.llScheduleList);
        tvUpcomingHeader = findViewById(R.id.tvUpcomingHeader);
        llUpcomingList = findViewById(R.id.llUpcomingList);

        tvScheduleTitle.setText(dayOffset == 0 ? "Today's Schedule" : "Tomorrow's Schedule");
        tvScheduleSemester.setText(selectedSemester);

        loadDaySchedule(dayOffset);
        loadUpcomingReminders();
    }

    // =========================================================
    // LOAD CLASSES FOR THE TARGET DAY (TODAY OR TOMORROW)
    // =========================================================

    private void loadDaySchedule(int dayOffset) {

        llScheduleList.removeAllViews();

        Calendar targetDay = Calendar.getInstance();
        targetDay.add(Calendar.DAY_OF_YEAR, dayOffset);

        // Locale.ENGLISH on purpose: our stored day names ("Saturday" etc.)
        // are always in English regardless of the phone's language setting.
        String dayName = new SimpleDateFormat("EEEE", Locale.ENGLISH)
                .format(targetDay.getTime());

        if (dayName.equals("Thursday") || dayName.equals("Friday")) {
            addPlainLine("Off day — no classes.");
            return;
        }

        String[] slotCodes = new String[9];

        Cursor cursor = databaseHelper.getRoutineForDay(selectedSemester, dayName);

        while (cursor.moveToNext()) {

            String startTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_START_TIME));

            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_COURSE_CODE));

            int index = indexOfTime(startTime);

            if (index != -1) {
                slotCodes[index] = code;
            }
        }

        cursor.close();

        boolean anyClass = false;

        int[] leaders = {0, 3, 5, 6};

        for (int leader : leaders) {

            List<Integer> members = new ArrayList<>();

            for (int i = 0; i < GROUP_LEADER.length; i++) {
                if (GROUP_LEADER[i] == leader) {
                    members.add(i);
                }
            }

            if (members.size() == 1) {

                int idx = members.get(0);
                String code = slotCodes[idx];

                if (code != null) {
                    addScheduleLine(slotStart(idx), slotEnd(idx), code);
                    anyClass = true;
                }

            } else {

                String firstCode = slotCodes[members.get(0)];
                boolean allSame = (firstCode != null);

                if (allSame) {
                    for (int m : members) {
                        if (!firstCode.equals(slotCodes[m])) {
                            allSame = false;
                            break;
                        }
                    }
                }

                if (allSame) {

                    int startIdx = members.get(0);
                    int endIdx = members.get(members.size() - 1);

                    addScheduleLine(slotStart(startIdx), slotEnd(endIdx), firstCode);
                    anyClass = true;

                } else {

                    for (int m : members) {

                        String code = slotCodes[m];

                        if (code != null) {
                            addScheduleLine(slotStart(m), slotEnd(m), code);
                            anyClass = true;
                        }
                    }
                }
            }
        }

        if (!anyClass) {
            addPlainLine("No classes scheduled for this day.");
        }
    }

    private String slotStart(int index) {
        return TIMES[index].split("-")[0];
    }

    private String slotEnd(int index) {
        return TIMES[index].split("-")[1];
    }

    private int indexOfTime(String time) {

        for (int i = 0; i < TIMES.length; i++) {
            if (TIMES[i].equals(time)) {
                return i;
            }
        }

        return -1;
    }

    private void addScheduleLine(String start, String end, String code) {

        String name = getCourseName(code);

        TextView tv = new TextView(this);

        String text = start + " - " + end + "  →  " + code;

        if (!name.isEmpty()) {
            text += " (" + name + ")";
        }

        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(0, 0, 0, dpToPx(10));

        llScheduleList.addView(tv);
    }

    private void addPlainLine(String text) {

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setTextColor(Color.parseColor("#777777"));

        llScheduleList.addView(tv);
    }

    private String getCourseName(String code) {

        Cursor cursor = databaseHelper.getCoursesBySemester(selectedSemester);

        while (cursor.moveToNext()) {

            String c = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE));

            if (c.equals(code)) {

                String n = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));

                cursor.close();
                return n;
            }
        }

        cursor.close();
        return "";
    }

    // =========================================================
    // LOAD ASSIGNMENTS / CLASS TESTS DUE WITHIN THE NEXT 2 DAYS
    // =========================================================

    private void loadUpcomingReminders() {

        llUpcomingList.removeAllViews();

        List<DueItem> items = new ArrayList<>();

        Calendar todayCal = Calendar.getInstance();
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);

        long todayMillis = todayCal.getTimeInMillis();

        SimpleDateFormat storageFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Assignments
        Cursor assignmentCursor = databaseHelper.getAllAssignments();

        while (assignmentCursor.moveToNext()) {

            String raw = assignmentCursor.getString(
                    assignmentCursor.getColumnIndexOrThrow(DatabaseHelper.ASSIGNMENT_DUE_DATE));

            String code = assignmentCursor.getString(
                    assignmentCursor.getColumnIndexOrThrow(DatabaseHelper.ASSIGNMENT_COURSE_CODE));

            String name = assignmentCursor.getString(
                    assignmentCursor.getColumnIndexOrThrow(DatabaseHelper.ASSIGNMENT_COURSE_NAME));

            Integer diff = diffDays(storageFormat, raw, todayMillis);

            if (diff != null && diff >= 0 && diff <= 2) {
                items.add(new DueItem("Assignment", code, name, diff));
            }
        }

        assignmentCursor.close();

        // Class Tests
        Cursor testCursor = databaseHelper.getAllClassTests();

        while (testCursor.moveToNext()) {

            String raw = testCursor.getString(
                    testCursor.getColumnIndexOrThrow(DatabaseHelper.TEST_DATE));

            String code = testCursor.getString(
                    testCursor.getColumnIndexOrThrow(DatabaseHelper.TEST_COURSE_CODE));

            String name = testCursor.getString(
                    testCursor.getColumnIndexOrThrow(DatabaseHelper.TEST_COURSE_NAME));

            Integer diff = diffDays(storageFormat, raw, todayMillis);

            if (diff != null && diff >= 0 && diff <= 2) {
                items.add(new DueItem("Class Test", code, name, diff));
            }
        }

        testCursor.close();

        Collections.sort(items, (a, b) -> Integer.compare(a.diffDays, b.diffDays));

        if (items.isEmpty()) {
            tvUpcomingHeader.setVisibility(View.GONE);
            llUpcomingList.setVisibility(View.GONE);
            return;
        }

        tvUpcomingHeader.setVisibility(View.VISIBLE);
        llUpcomingList.setVisibility(View.VISIBLE);

        for (DueItem item : items) {

            TextView tv = new TextView(this);

            String emoji = item.type.equals("Assignment") ? "📝" : "📋";

            String label;
            if (item.diffDays == 0) {
                label = "Due Today";
            } else if (item.diffDays == 1) {
                label = "Due Tomorrow";
            } else {
                label = "Due in 2 days";
            }

            tv.setText(emoji + " " + item.type + ": " + item.code + " - " + item.name +
                    " (" + label + ")");
            tv.setTextSize(15);
            tv.setPadding(0, 0, 0, dpToPx(10));

            llUpcomingList.addView(tv);
        }
    }

    private Integer diffDays(SimpleDateFormat format, String raw, long todayMillis) {

        try {

            Date parsed = format.parse(raw);

            Calendar dueCal = Calendar.getInstance();
            dueCal.setTime(parsed);
            dueCal.set(Calendar.HOUR_OF_DAY, 0);
            dueCal.set(Calendar.MINUTE, 0);
            dueCal.set(Calendar.SECOND, 0);
            dueCal.set(Calendar.MILLISECOND, 0);

            long dueMillis = dueCal.getTimeInMillis();

            long diffMillis = dueMillis - todayMillis;

            return (int) Math.round(diffMillis / (double) (24L * 60 * 60 * 1000));

        } catch (ParseException e) {

            return null;
        }
    }

    private int dpToPx(int dp) {

        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private static class DueItem {

        String type;
        String code;
        String name;
        int diffDays;

        DueItem(String type, String code, String name, int diffDays) {
            this.type = type;
            this.code = code;
            this.name = name;
            this.diffDays = diffDays;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}