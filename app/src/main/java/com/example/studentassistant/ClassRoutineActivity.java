package com.example.studentassistant;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ClassRoutineActivity extends AppCompatActivity {

    // Pass true with this key ,this screen as VIEW ONLY
    public static final String EXTRA_READ_ONLY = "read_only";

    private boolean isReadOnly = false;

    private DatabaseHelper databaseHelper;

    private String selectedSemester;

    private TableLayout classRoutineTable;

    private Button btnSaveRoutine;

    // Saturday
    private TextView sat1, sat2, sat3, sat4, sat5, sat6, sat7, sat8, sat9;

    // Sunday
    private TextView sun1, sun2, sun3, sun4, sun5, sun6, sun7, sun8, sun9;

    // Monday
    private TextView mon1, mon2, mon3, mon4, mon5, mon6, mon7, mon8, mon9;

    // Tuesday
    private TextView tue1, tue2, tue3, tue4, tue5, tue6, tue7, tue8, tue9;

    // Wednesday
    private TextView wed1, wed2, wed3, wed4, wed5, wed6, wed7, wed8, wed9;

    // Each day's 9 cells grouped into one array, in slot 1 to slot 9)
    private TextView[] satCells;
    private TextView[] sunCells;
    private TextView[] monCells;
    private TextView[] tueCells;
    private TextView[] wedCells;

    // TIME SLOT SETUP


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_class_routine);

        isReadOnly = getIntent().getBooleanExtra(EXTRA_READ_ONLY, false);

        SharedPreferences preferences =
                getSharedPreferences("StudentAssistant", MODE_PRIVATE);

        selectedSemester =
                preferences.getString("semester", "Select Semester");

        databaseHelper = new DatabaseHelper(this);
        classRoutineTable = findViewById(R.id.classRoutineTable);
        btnSaveRoutine = findViewById(R.id.btnSaveRoutine);

        if (isReadOnly) {
            btnSaveRoutine.setVisibility(View.GONE);
        }

        // Show semester

        TextView tvSelectedSemester =
                findViewById(R.id.tvSelectedSemester);

        tvSelectedSemester.setText(selectedSemester);

        sat1 = findViewById(R.id.sat1);
        sat2 = findViewById(R.id.sat2);
        sat3 = findViewById(R.id.sat3);
        sat4 = findViewById(R.id.sat4);
        sat5 = findViewById(R.id.sat5);
        sat6 = findViewById(R.id.sat6);
        sat7 = findViewById(R.id.sat7);
        sat8 = findViewById(R.id.sat8);
        sat9 = findViewById(R.id.sat9);

        sun1 = findViewById(R.id.sun1);
        sun2 = findViewById(R.id.sun2);
        sun3 = findViewById(R.id.sun3);
        sun4 = findViewById(R.id.sun4);
        sun5 = findViewById(R.id.sun5);
        sun6 = findViewById(R.id.sun6);
        sun7 = findViewById(R.id.sun7);
        sun8 = findViewById(R.id.sun8);
        sun9 = findViewById(R.id.sun9);

        mon1 = findViewById(R.id.mon1);
        mon2 = findViewById(R.id.mon2);
        mon3 = findViewById(R.id.mon3);
        mon4 = findViewById(R.id.mon4);
        mon5 = findViewById(R.id.mon5);
        mon6 = findViewById(R.id.mon6);
        mon7 = findViewById(R.id.mon7);
        mon8 = findViewById(R.id.mon8);
        mon9 = findViewById(R.id.mon9);

        tue1 = findViewById(R.id.tue1);
        tue2 = findViewById(R.id.tue2);
        tue3 = findViewById(R.id.tue3);
        tue4 = findViewById(R.id.tue4);
        tue5 = findViewById(R.id.tue5);
        tue6 = findViewById(R.id.tue6);
        tue7 = findViewById(R.id.tue7);
        tue8 = findViewById(R.id.tue8);
        tue9 = findViewById(R.id.tue9);

        wed1 = findViewById(R.id.wed1);
        wed2 = findViewById(R.id.wed2);
        wed3 = findViewById(R.id.wed3);
        wed4 = findViewById(R.id.wed4);
        wed5 = findViewById(R.id.wed5);
        wed6 = findViewById(R.id.wed6);
        wed7 = findViewById(R.id.wed7);
        wed8 = findViewById(R.id.wed8);
        wed9 = findViewById(R.id.wed9);


        satCells = new TextView[]{sat1, sat2, sat3, sat4, sat5, sat6, sat7, sat8, sat9};
        sunCells = new TextView[]{sun1, sun2, sun3, sun4, sun5, sun6, sun7, sun8, sun9};
        monCells = new TextView[]{mon1, mon2, mon3, mon4, mon5, mon6, mon7, mon8, mon9};
        tueCells = new TextView[]{tue1, tue2, tue3, tue4, tue5, tue6, tue7, tue8, tue9};
        wedCells = new TextView[]{wed1, wed2, wed3, wed4, wed5, wed6, wed7, wed8, wed9};


        if (!isReadOnly) {

            setupDayClicks("Saturday", satCells);
            setupDayClicks("Sunday", sunCells);
            setupDayClicks("Monday", monCells);
            setupDayClicks("Tuesday", tueCells);
            setupDayClicks("Wednesday", wedCells);

            btnSaveRoutine.setOnClickListener(v -> {

                Toast.makeText(
                        this,
                        "Routine Saved",
                        Toast.LENGTH_SHORT
                ).show();

            });
        }


        loadRoutine();
    }


    private void setupDayClicks(String day, TextView[] cells) {

        for (int i=0; i<cells.length; i++) {
            final int slotIndex = i;
            cells[i].setOnClickListener(v ->
                    handleCellClick(day, cells, slotIndex)
            );
        }
    }

    private void handleCellClick(String day, TextView[] cells, int tappedIndex) {
        int leader = GROUP_LEADER[tappedIndex];
        boolean groupHasMultipleSlots = countGroupMembers(leader) > 1;
        TextView leaderCell = cells[leader];
        boolean activeLab = "LAB".equals(leaderCell.getTag());
        boolean combinedMode = activeLab || (tappedIndex == leader && groupHasMultipleSlots);
        boolean hasCourse = combinedMode
                ? (leaderCell.getTag() != null)
                : (cells[tappedIndex].getTag() != null);

        openCellDialog(day, cells, leader, tappedIndex, combinedMode, activeLab, hasCourse);
    }

     // COUNT HOW MANY SLOTS BELONG TO A GROUP

    private int countGroupMembers(int leader) {

        int count = 0;
        for (int g : GROUP_LEADER) {
            if (g == leader) {
                count++;
            }
        }

        return count;
    }

    // WHICH CELL IN A LAB GROUP SHOWS THE COURSE TEXT
       private int getLabDisplayIndex(int leader) {

        if (leader == 0) {
            return 1; // middle of 8:00-8:50, 8:50-9:40, 9:40-10:30
        } else if (leader == 6) {
            return 7; // middle of 2:30-3:20, 3:20-4:10, 4:10-5:00
        } else {
            return leader; // 2-slot group (10:50-12:30) has no exact middle
        }
    }


    private void openCellDialog(
            String day,
            TextView[] cells,
            int leader,
            int tappedIndex,
            boolean combinedMode,
            boolean activeLab,
            boolean hasCourse) {

        Cursor classCursor = databaseHelper.getClassCourses(selectedSemester);

        ArrayList<String> codes = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();

        while (classCursor.moveToNext()) {

            codes.add(classCursor.getString(
                    classCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE)));

            names.add(classCursor.getString(
                    classCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));

            types.add("CLASS");
        }

        classCursor.close();

        if (combinedMode) {

            Cursor labCursor = databaseHelper.getLabCourses(selectedSemester);

            while (labCursor.moveToNext()) {

                codes.add(labCursor.getString(
                        labCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE)));

                names.add(labCursor.getString(
                        labCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));

                types.add("LAB");
            }

            labCursor.close();
        }

        if (codes.isEmpty()) {

            String message = combinedMode
                    ? "No CLASS or LAB courses found for " + selectedSemester
                    : "No CLASS courses found for " + selectedSemester;

            new AlertDialog.Builder(this)
                    .setTitle("No Courses")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();

            return;
        }

        String[] display = new String[codes.size()];

        for (int i = 0; i < codes.size(); i++) {

            String prefix = types.get(i).equals("LAB") ? "[LAB] " : "";
            display[i] = prefix + codes.get(i) + "\n" + names.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String dialogTitle = combinedMode
                ? "Select Course ([LAB] merges this block)"
                : "Select Course";

        builder.setTitle(dialogTitle);

        builder.setItems(display, (dialog, which) -> {

            String selectedCode = codes.get(which);
            String selectedName = names.get(which);
            String selectedType = types.get(which);

            if (selectedType.equals("LAB")) {

                clearGroup(day, cells, leader);
                assignLabToGroup(day, cells, leader, selectedCode, selectedName);

            } else if (activeLab) {

                clearGroup(day, cells, leader);
                assignClass(day, cells, leader, selectedCode, selectedName);

            } else if (combinedMode) {

                clearSlot(day, cells, leader);
                assignClass(day, cells, leader, selectedCode, selectedName);

            } else {

                clearSlot(day, cells, tappedIndex);
                assignClass(day, cells, tappedIndex, selectedCode, selectedName);
            }
        });

        builder.setNegativeButton("Cancel", null);

        if (hasCourse) {

            builder.setNeutralButton("Remove", (dialog, which) -> {

                if (activeLab) {
                    clearGroup(day, cells, leader);
                } else if (combinedMode) {
                    clearSlot(day, cells, leader);
                } else {
                    clearSlot(day, cells, tappedIndex);
                }
            });
        }

        builder.show();
    }


    private void assignClass(
            String day,
            TextView[] cells,
            int index,
            String code,
            String name) {

        databaseHelper.saveRoutine(selectedSemester, day, TIMES[index], code);

        TextView cell = cells[index];

        cell.setText(code + "\n" + name);
        cell.setSingleLine(false);
        cell.setGravity(Gravity.CENTER);
        cell.setBackgroundColor(Color.TRANSPARENT);
        cell.setTag("CLASS");
    }


    private void assignLabToGroup(
            String day,
            TextView[] cells,
            int leader,
            String code,
            String name) {

        int displayIndex = getLabDisplayIndex(leader);

        for (int i = 0; i < GROUP_LEADER.length; i++) {

            if (GROUP_LEADER[i] != leader) {
                continue;
            }

            databaseHelper.saveRoutine(selectedSemester, day, TIMES[i], code);

            TextView cell = cells[i];

            cell.setTag("LAB");

            if (i == displayIndex) {
                cell.setText(code + "\n" + name);
                cell.setSingleLine(false);
                cell.setGravity(Gravity.CENTER);
            } else {
                cell.setText("");
                cell.setSingleLine(true);
            }
        }
    }

    // CLEAR ONE SLOT
      private void clearSlot(String day, TextView[] cells, int index) {

        databaseHelper.deleteRoutine(selectedSemester, day, TIMES[index]);
        resetCell(cells[index]);
    }

     // CLEAR A WHOLE GROUP (ALL SLOTS OF A MERGED LAB)
     private void clearGroup(String day, TextView[] cells, int leader) {

        for (int i = 0; i < GROUP_LEADER.length; i++) {

            if (GROUP_LEADER[i] == leader) {
                databaseHelper.deleteRoutine(selectedSemester, day, TIMES[i]);
                resetCell(cells[i]);
            }
        }
    }

    // RESET ONE CELL TO "NO CLASS"
     private void resetCell(TextView cell) {

        cell.setText("No Class");
        cell.setSingleLine(true);
        cell.setGravity(Gravity.CENTER);
        cell.setBackgroundColor(Color.TRANSPARENT);
        cell.setTag(null);
    }

    // LOAD SAVED ROUTINE FROM DATABASE
     private void loadRoutine() {

        Cursor cursor = databaseHelper.getRoutine(selectedSemester);
        while (cursor.moveToNext()) {
            String day = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_DAY));
            String startTime = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_START_TIME));

            String courseCode = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.ROUTINE_COURSE_CODE));

            TextView[] dayCells = getDayCells(day);
            int index = indexOfTime(startTime);

            if (dayCells == null || index == -1) {
                continue;
            }

            TextView cell = dayCells[index];

            String courseName = getCourseName(courseCode);
            String courseType = getCourseType(courseCode);

            int groupLeader = GROUP_LEADER[index];
            boolean isDisplaySlot = (index == getLabDisplayIndex(groupLeader));

            if ("LAB".equals(courseType)) {

                cell.setTag("LAB");

                if (isDisplaySlot) {
                    cell.setText(courseCode + "\n" + courseName);
                    cell.setSingleLine(false);
                    cell.setGravity(Gravity.CENTER);
                } else {
                    cell.setText("");
                }

            } else {

                cell.setTag("CLASS");
                cell.setText(courseCode + "\n" + courseName);
                cell.setSingleLine(false);
                cell.setGravity(Gravity.CENTER);
            }
        }

        cursor.close();
    }

    // GET THE CELL ARRAY FOR A GIVEN DAY

    private TextView[] getDayCells(String day) {

        switch (day) {
            case "Saturday": return satCells;
            case "Sunday": return sunCells;
            case "Monday": return monCells;
            case "Tuesday": return tueCells;
            case "Wednesday": return wedCells;
            default: return null;
        }
    }

     // GET SLOT INDEX (0-8) FOR A GIVEN TIME LABEL
       private int indexOfTime(String time) {

        for (int i = 0; i < TIMES.length; i++) {
            if (TIMES[i].equals(time)) {
                return i;
            }
        }

        return -1;
    }

     // GET COURSE NAME BY CODE
     private String getCourseName(String courseCode) {

        Cursor cursor = databaseHelper.getCoursesBySemester(selectedSemester);

        while (cursor.moveToNext()) {
            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE));
            if (code.equals(courseCode)) {
                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));

                cursor.close();
                return name;
            }
        }
        cursor.close();
        return "";
    }
    // GET COURSE TYPE (CLASS OR LAB) BY CODE
       private String getCourseType(String courseCode) {
        Cursor cursor = databaseHelper.getCoursesBySemester(selectedSemester);
        while (cursor.moveToNext()) {
            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE));
            if (code.equals(courseCode)) {
                String type = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE));
                cursor.close();
                return type;
            }
        }

        cursor.close();
        return "CLASS";
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScheduleWidget.refreshAll(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}