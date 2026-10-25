package com.example.studentassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClassTestActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private LinearLayout llClassTestList;
    private TextView tvNoClassTests;
    private FloatingActionButton btnAddClassTest;

    private TextView tvSearchIcon;
    private EditText etSearch;

    // What the user typed, trimmed and in lowercase. Empty = show everything.
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_test);

        databaseHelper = new DatabaseHelper(this);

        llClassTestList = findViewById(R.id.llClassTestList);
        tvNoClassTests = findViewById(R.id.tvNoClassTests);
        btnAddClassTest = findViewById(R.id.btnAddClassTest);
        tvSearchIcon = findViewById(R.id.tvSearchIcon);
        etSearch = findViewById(R.id.etSearch);

        btnAddClassTest.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ClassTestActivity.this,
                    AddClassTestActivity.class
            );

            startActivity(intent);

        });

        // ---------------------------------------------------------
        // SEARCH ICON: SHOW / HIDE THE SEARCH BOX
        // ---------------------------------------------------------

        tvSearchIcon.setOnClickListener(v -> toggleSearchBox());

        // ---------------------------------------------------------
        // SEARCH BOX: FILTER THE LIST AS THE USER TYPES
        // ---------------------------------------------------------

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                currentQuery = s.toString().trim().toLowerCase(Locale.getDefault());

                loadClassTests();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Keyboard "search" key just closes the keyboard
        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload every time this screen becomes visible,
        // so a newly added or deleted test always shows up.
        loadClassTests();
    }

    // =========================================================
    // SHOW OR HIDE THE SEARCH BOX
    // =========================================================

    private void toggleSearchBox() {

        if (etSearch.getVisibility() == View.VISIBLE) {

            // Close the box and go back to the full list
            currentQuery = "";
            etSearch.setText("");
            etSearch.setVisibility(View.GONE);
            hideKeyboard();

            loadClassTests();

        } else {

            etSearch.setVisibility(View.VISIBLE);
            etSearch.requestFocus();

            etSearch.post(() -> {

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
    }

    private void hideKeyboard() {

        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    // =========================================================
    // LOAD CLASS TESTS FROM DATABASE AND BUILD CARDS
    // (ONLY THE ONES THAT MATCH THE SEARCH)
    // =========================================================

    private void loadClassTests() {

        llClassTestList.removeAllViews();

        Cursor cursor = databaseHelper.getAllClassTests();

        int totalCount = cursor.getCount();
        int shownCount = 0;

        while (cursor.moveToNext()) {

            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TEST_ID));

            String courseCode = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TEST_COURSE_CODE));

            String courseName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TEST_COURSE_NAME));

            String testDateRaw = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TEST_DATE));

            String description = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TEST_DESCRIPTION));

            String attachmentUri = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TEST_ATTACHMENT_URI));

            if (!matchesSearch(courseCode, courseName, testDateRaw, description)) {
                continue;
            }

            addClassTestCard(id, courseCode, testDateRaw, description, attachmentUri);

            shownCount++;
        }

        cursor.close();

        // Empty-state message
        if (totalCount == 0) {

            tvNoClassTests.setText("No class tests yet.\nTap + to add one.");
            tvNoClassTests.setVisibility(View.VISIBLE);

        } else if (shownCount == 0) {

            tvNoClassTests.setText("No matching class tests.");
            tvNoClassTests.setVisibility(View.VISIBLE);

        } else {

            tvNoClassTests.setVisibility(View.GONE);
        }
    }

    // =========================================================
    // DOES THIS CLASS TEST MATCH WHAT THE USER TYPED?
    // Every word typed must appear somewhere in the card text.
    // =========================================================

    private boolean matchesSearch(
            String courseCode,
            String courseName,
            String testDateRaw,
            String description) {

        if (currentQuery.isEmpty()) {
            return true;
        }

        String safeDescription = (description == null) ? "" : description;

        String searchable = (courseCode + " " +
                courseName + " " +
                formatDate(testDateRaw) + " " +
                testDateRaw + " " +
                safeDescription).toLowerCase(Locale.getDefault());

        String[] words = currentQuery.split("\\s+");

        for (String word : words) {

            if (!searchable.contains(word)) {
                return false;
            }
        }

        return true;
    }

    // =========================================================
    // CREATE AND ADD ONE CLASS TEST CARD VIEW
    // =========================================================

    private void addClassTestCard(
            long id,
            String courseCode,
            String testDateRaw,
            String description,
            String attachmentUri) {

        View card = LayoutInflater.from(this)
                .inflate(R.layout.item_class_test, llClassTestList, false);

        TextView tvCourseCode = card.findViewById(R.id.tvTestCourseCode);
        TextView tvDate = card.findViewById(R.id.tvTestDate);
        TextView tvDescription = card.findViewById(R.id.tvTestDescription);
        TextView tvClose = card.findViewById(R.id.tvTestClose);
        TextView tvAttachment = card.findViewById(R.id.tvTestAttachment);

        tvCourseCode.setText(courseCode);
        tvDate.setText("Test: " + formatDate(testDateRaw));

        if (description != null && !description.trim().isEmpty()) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        if (attachmentUri != null && !attachmentUri.trim().isEmpty()) {

            tvAttachment.setVisibility(View.VISIBLE);

            tvAttachment.setOnClickListener(v -> openAttachment(attachmentUri));

        } else {
            tvAttachment.setVisibility(View.GONE);
        }

        tvClose.setOnClickListener(v -> confirmDelete(id));

        llClassTestList.addView(card);
    }

    // =========================================================
    // CONVERT STORED "yyyy-MM-dd" INTO A READABLE DATE
    // =========================================================

    private String formatDate(String rawDate) {

        try {

            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

            Date date = inputFormat.parse(rawDate);

            return outputFormat.format(date);

        } catch (ParseException e) {

            return rawDate;
        }
    }

    // =========================================================
    // OPEN AN ATTACHED FILE / IMAGE
    // =========================================================

    private void openAttachment(String uriString) {

        try {

            Uri uri = Uri.parse(uriString);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Could not open the attachment",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // CONFIRM AND DELETE A CLASS TEST
    // =========================================================

    private void confirmDelete(long id) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Class Test")
                .setMessage("Do you want to delete this class test?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    databaseHelper.deleteClassTest(id);
                    loadClassTests();

                })
                .setNegativeButton("Cancel", null)
                .show();
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