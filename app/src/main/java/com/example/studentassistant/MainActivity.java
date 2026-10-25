package com.example.studentassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    TextView tvMenu;
    TextView tvGreeting;
    TextView tvDate;
    TextView tvSelectClassRoutine;

    Button btnAssignment;
    Button btnClassTest;
    Button btnClassRoutine;
    Button btnTodaySchedule;
    Button btnTomorrowSchedule;

    Spinner spinnerSemester;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find view
        drawerLayout = findViewById(R.id.drawerLayout);

        tvMenu = findViewById(R.id.tvMenu);
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);

        tvSelectClassRoutine = findViewById(R.id.tvSelectClassRoutine);

        btnAssignment = findViewById(R.id.btnAssignment);
        btnClassTest = findViewById(R.id.btnClassTest);
        btnClassRoutine = findViewById(R.id.btnClassRoutine);
        btnTodaySchedule = findViewById(R.id.btnTodaySchedule);
        btnTomorrowSchedule = findViewById(R.id.btnTomorrowSchedule);

        spinnerSemester = findViewById(R.id.spinnerSemester);

        // Semester list

        String[] semesters = {
                "Select Semester",
                "CSE 1-1",
                "CSE 1-2",
                "CSE 2-1",
                "CSE 2-2",
                "CSE 3-1",
                "CSE 3-2",
                "CSE 4-1",
                "CSE 4-2"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                semesters
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerSemester.setAdapter(adapter);

        // SAVE SELECTED SEMESTER

        SharedPreferences preferences =
                getSharedPreferences("StudentAssistant", MODE_PRIVATE);

        String savedSemester =
                preferences.getString("semester", "Select Semester");

        for (int i=0;i<semesters.length;i++){
            if (semesters[i].equals(savedSemester)){
                spinnerSemester.setSelection(i);
                break;
            }
        }

        spinnerSemester.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id) {

                        String selectedSemester = semesters[position];

                        preferences.edit()
                                .putString("semester", selectedSemester)
                                .apply();

                        // NEW: refresh the home screen widget for the new semester
                        ScheduleWidget.refreshAll(MainActivity.this);
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );

        // Good mornings

        setGreeting();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        tvDate.setText(currentDate);

        tvMenu.setOnClickListener(v -> {

            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }

        });

        btnAssignment.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AssignmentActivity.class
            );
            startActivity(intent);
        });
        //class test
        btnClassTest.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ClassTestActivity.class
            );

            startActivity(intent);

        });


        //class routine

        tvSelectClassRoutine.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ClassRoutineActivity.class
            );

            startActivity(intent);

        });


        //class routine view
        btnClassRoutine.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    ClassRoutineActivity.class
            );

            intent.putExtra(ClassRoutineActivity.EXTRA_READ_ONLY, true);

            startActivity(intent);

        });


        //schedule

        btnTodaySchedule.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    DailyScheduleActivity.class
            );

            intent.putExtra(DailyScheduleActivity.EXTRA_DAY_OFFSET, 0);

            startActivity(intent);

        });


        //schedule tomorrow

        btnTomorrowSchedule.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    DailyScheduleActivity.class
            );

            intent.putExtra(DailyScheduleActivity.EXTRA_DAY_OFFSET, 1);

            startActivity(intent);

        });

    }

    //greeting according to time

    private void setGreeting() {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;

        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning!";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon!";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Good Evening!";
        } else {
            greeting = "Good Night!";
        }

        tvGreeting.setText(greeting);
    }
}