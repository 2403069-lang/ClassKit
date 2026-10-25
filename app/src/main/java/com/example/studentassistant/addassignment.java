package com.example.studentassistant;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class addassignment extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private String selectedSemester;

    private Spinner spinnerCourse;
    private TextView tvDueDate;
    private EditText etDescription;
    private Button btnAddAttachment;
    private TextView tvAttachmentStatus;
    private Button btnSaveAssignment;

    private final ArrayList<String> courseCodes = new ArrayList<>();
    private final ArrayList<String> courseNames = new ArrayList<>();

    private String dueDateValue = null; // stored internally as "yyyy-MM-dd"

    private Uri selectedAttachmentUri = null;

    private final ActivityResultLauncher<String[]> attachmentPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {

                if (uri != null) {

                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (SecurityException e) {
                        // Some providers don't allow persistable permission; safe to ignore.
                    }

                    selectedAttachmentUri = uri;
                    tvAttachmentStatus.setText(getFileName(uri));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addassignment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---------------------------------------------------------
        // GET SELECTED SEMESTER
        // ---------------------------------------------------------

        SharedPreferences preferences =
                getSharedPreferences("StudentAssistant", MODE_PRIVATE);

        selectedSemester =
                preferences.getString("semester", "Select Semester");

        // ---------------------------------------------------------
        // DATABASE
        // ---------------------------------------------------------

        databaseHelper = new DatabaseHelper(this);

        // ---------------------------------------------------------
        // FIND VIEWS
        // ---------------------------------------------------------

        spinnerCourse = findViewById(R.id.spinnerCourse);
        tvDueDate = findViewById(R.id.tvDueDate);
        etDescription = findViewById(R.id.etDescription);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);
        tvAttachmentStatus = findViewById(R.id.tvAttachmentStatus);
        btnSaveAssignment = findViewById(R.id.btnSaveAssignment);

        // ---------------------------------------------------------
        // LOAD COURSES INTO SPINNER
        // ---------------------------------------------------------

        loadCourseSpinner();

        // ---------------------------------------------------------
        // DUE DATE PICKER
        // ---------------------------------------------------------

        tvDueDate.setOnClickListener(v -> showDatePicker());

        // ---------------------------------------------------------
        // ATTACHMENT PICKER
        // ---------------------------------------------------------

        btnAddAttachment.setOnClickListener(v ->
                attachmentPickerLauncher.launch(new String[]{"*/*"})
        );

        // ---------------------------------------------------------
        // SAVE
        // ---------------------------------------------------------

        btnSaveAssignment.setOnClickListener(v -> saveAssignment());
    }

    // =========================================================
    // LOAD COURSES (CLASS + LAB) FOR THE SELECTED SEMESTER
    // =========================================================

    private void loadCourseSpinner() {

        courseCodes.add("");
        courseNames.add("");

        ArrayList<String> displayList = new ArrayList<>();
        displayList.add("Select Course");

        Cursor cursor = databaseHelper.getCoursesBySemester(selectedSemester);

        while (cursor.moveToNext()) {

            String code = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODE));

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));

            courseCodes.add(code);
            courseNames.add(name);

            displayList.add(code + " - " + name);
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                displayList
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCourse.setAdapter(adapter);
    }

    // =========================================================
    // SHOW DATE PICKER (YEARS 2020 TO 2030)
    // =========================================================

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth);

                    SimpleDateFormat storageFormat =
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    SimpleDateFormat displayFormat =
                            new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

                    dueDateValue = storageFormat.format(picked.getTime());

                    tvDueDate.setText(displayFormat.format(picked.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        Calendar minDate = Calendar.getInstance();
        minDate.set(2020, Calendar.JANUARY, 1);

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2030, Calendar.DECEMBER, 31);

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        dialog.show();
    }

    // =========================================================
    // GET DISPLAY FILE NAME FROM A CONTENT URI
    // =========================================================

    private String getFileName(Uri uri) {

        String result = null;

        try (Cursor cursor = getContentResolver().query(
                uri, null, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {

                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex);
                }
            }

        } catch (Exception e) {
            // Fall back below if this fails
        }

        if (result == null) {
            result = uri.getLastPathSegment();
        }

        if (result == null) {
            result = "1 file attached";
        }

        return result;
    }

    // =========================================================
    // VALIDATE AND SAVE THE ASSIGNMENT
    // =========================================================

    private void saveAssignment() {

        int position = spinnerCourse.getSelectedItemPosition();

        if (position == 0) {

            Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dueDateValue == null) {

            Toast.makeText(this, "Please select a due date", Toast.LENGTH_SHORT).show();
            return;
        }

        String courseCode = courseCodes.get(position);
        String courseName = courseNames.get(position);
        String description = etDescription.getText().toString().trim();

        String attachmentUriString =
                (selectedAttachmentUri != null)
                        ? selectedAttachmentUri.toString()
                        : null;

        databaseHelper.insertAssignment(
                courseCode,
                courseName,
                dueDateValue,
                description,
                attachmentUriString
        );

        Toast.makeText(this, "Assignment Saved", Toast.LENGTH_SHORT).show();

        finish();
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