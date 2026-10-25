package com.example.studentassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "StudentAssistant.db";
    private static final int DATABASE_VERSION = 4;

    // Courses table
    public static final String TABLE_COURSES = "courses";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SEMESTER = "semester";
    public static final String COLUMN_CODE = "course_code";
    public static final String COLUMN_NAME = "course_name";
    public static final String COLUMN_CREDIT = "credit";
    public static final String COLUMN_TYPE = "type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Routine table
    public static final String TABLE_ROUTINE = "routine";

    public static final String ROUTINE_ID = "id";
    public static final String ROUTINE_SEMESTER = "semester";
    public static final String ROUTINE_DAY = "day";
    public static final String ROUTINE_START_TIME = "start_time";
    public static final String ROUTINE_COURSE_CODE = "course_code";

    // Assignments table
    public static final String TABLE_ASSIGNMENTS = "assignments";

    public static final String ASSIGNMENT_ID = "id";
    public static final String ASSIGNMENT_COURSE_CODE = "course_code";
    public static final String ASSIGNMENT_COURSE_NAME = "course_name";
    public static final String ASSIGNMENT_DUE_DATE = "due_date";
    public static final String ASSIGNMENT_DESCRIPTION = "description";
    public static final String ASSIGNMENT_ATTACHMENT_URI = "attachment_uri";

    // Class Tests table
    public static final String TABLE_CLASS_TESTS = "class_tests";

    public static final String TEST_ID = "id";
    public static final String TEST_COURSE_CODE = "course_code";
    public static final String TEST_COURSE_NAME = "course_name";
    public static final String TEST_DATE = "test_date";
    public static final String TEST_DESCRIPTION = "description";
    public static final String TEST_ATTACHMENT_URI = "attachment_uri";

    //database create
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createCoursesTable = "CREATE TABLE " + TABLE_COURSES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SEMESTER + " TEXT NOT NULL, " +
                COLUMN_CODE + " TEXT NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_CREDIT + " REAL NOT NULL, " +
                COLUMN_TYPE + " TEXT NOT NULL" +
                ")";

        db.execSQL(createCoursesTable);
        insertAllCourses(db);
        createRoutineTable(db);
        createAssignmentsTable(db);
        createClassTestsTable(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            createRoutineTable(db);
        }

        if (oldVersion < 3) {
            createAssignmentsTable(db);
        }

        if (oldVersion < 4) {
            createClassTestsTable(db);
        }
    }

    private void insertAllCourses(SQLiteDatabase db) {

        // CSE 1-1

        insertCourse(db, "CSE 1-1",
                "CSE 1100",
                "Computer Fundamentals and Ethics Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 1-1",
                "CSE 1101",
                "Structured Programming",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-1",
                "CSE 1102",
                "Structured Programming Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 1-1",
                "EEE 1151",
                "Basic Electrical Engineering",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-1",
                "EEE 1152",
                "Basic Electrical Engineering Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 1-1",
                "Math 1113",
                "Differential and Integral Calculus",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-1",
                "Hum 1113",
                "Functional English",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-1",
                "Hum 1114",
                "Functional English Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 1-1",
                "Chem 1113",
                "Inorganic and Physical Chemistry",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-1",
                "Chem 1114",
                "Inorganic and Physical Chemistry Sessional",
                0.75,
                "LAB");

        // CSE 1-2

        insertCourse(db, "CSE 1-2",
                "CSE 1200",
                "Competitive Programming Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 1-2",
                "CSE 1201",
                "Data Structure",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-2",
                "CSE 1202",
                "Data Structure Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 1-2",
                "CSE 1203",
                "Object Oriented Programming",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-2",
                "CSE 1204",
                "Object Oriented Programming Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 1-2",
                "EEE 1251",
                "Electronic Devices and Circuits",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-2",
                "EEE 1252",
                "Electronic Devices and Circuits Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 1-2",
                "Math 1213",
                "Coordinate Geometry and Ordinary Differential Equation",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-2",
                "Phy 1213",
                "Physics",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 1-2",
                "Phy 1214",
                "Physics Sessional",
                0.75,
                "LAB");


        insertCourse(db, "CSE 2-1",
                "CSE 2100",
                "Software Development Project I",
                0.75,
                "LAB");

        insertCourse(db, "CSE 2-1",
                "CSE 2101",
                "Discrete Mathematics",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-1",
                "CSE 2102",
                "Discrete Mathematics Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 2-1",
                "CSE 2103",
                "Digital Logic Design",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-1",
                "CSE 2104",
                "Digital Logic Design Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 2-1",
                "EEE 2151",
                "Electrical Drives and Instrumentations",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-1",
                "EEE 2152",
                "Electrical Drives and Instrumentations Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 2-1",
                "Math 2113",
                "Vector Analysis and Linear Algebra",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-1",
                "Hum 2113",
                "Economics, Government and Sociology",
                3.00,
                "CLASS");


        insertCourse(db, "CSE 2-2",
                "CSE 2200",
                "Technical Writing and Presentation Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 2-2",
                "CSE 2201",
                "Algorithm Analysis and Design",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-2",
                "CSE 2202",
                "Algorithm Analysis and Design Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 2-2",
                "CSE 2203",
                "Numerical Methods",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-2",
                "CSE 2204",
                "Numerical Methods Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 2-2",
                "CSE 2205",
                "Microprocessors, Microcontrollers and Assembly Language",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-2",
                "CSE 2206",
                "Microprocessors, Microcontrollers and Assembly Language Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 2-2",
                "Math 2213",
                "Complex Variable, Partial Differential Equation and Harmonic Analysis",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 2-2",
                "Hum 2213",
                "Industrial Management and Accountancy",
                3.00,
                "CLASS");


        insertCourse(db, "CSE 3-1",
                "CSE 3100",
                "Web Based Application Project",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-1",
                "CSE 3101",
                "Database Systems",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-1",
                "CSE 3102",
                "Database Systems Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 3-1",
                "CSE 3103",
                "Theory of Computation",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-1",
                "CSE 3105",
                "Computer Interfacing and Embedded System",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-1",
                "CSE 3106",
                "Computer Interfacing and Embedded System Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-1",
                "CSE 3107",
                "Computer Architecture",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-1",
                "CSE 3108",
                "Computer Architecture Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-1",
                "CSE 3109",
                "Applied Statistics and Queuing Theory",
                3.00,
                "CLASS");


        insertCourse(db, "CSE 3-2",
                "CSE 3200",
                "Software Development Project II",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-2",
                "CSE 3201",
                "Operating Systems",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-2",
                "CSE 3202",
                "Operating Systems Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-2",
                "CSE 3203",
                "Data Communication",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-2",
                "CSE 3204",
                "Data Communication Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-2",
                "CSE 3205",
                "Software Engineering",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-2",
                "CSE 3206",
                "Software Engineering Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-2",
                "CSE 3207",
                "Artificial Intelligence",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-2",
                "CSE 3208",
                "Artificial Intelligence Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 3-2",
                "CSE 3209",
                "Digital Signal Processing",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 3-2",
                "CSE 3210",
                "Digital Signal Processing Sessional",
                0.75,
                "LAB");


        insertCourse(db, "CSE 4-1",
                "CSE 4000",
                "Project/Thesis I",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4101",
                "Compiler Design",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4102",
                "Compiler Design Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4103",
                "Computer Networks",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4104",
                "Computer Networks Sessional",
                1.50,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4105",
                "Digital Image Processing",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4106",
                "Digital Image Processing Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4108",
                "Industrial Attachment",
                0.75,
                "LAB");

        // Optional I
        insertCourse(db, "CSE 4-1",
                "CSE 4109",
                "Information Systems Analysis and Design",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4110",
                "Information Systems Analysis and Design Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4111",
                "Unix Programming",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4112",
                "Unix Programming Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4113",
                "Digital System Design",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4114",
                "Digital System Design Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4115",
                "Simulation and Modeling",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4116",
                "Simulation and Modeling Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4117",
                "Wireless Networks",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4118",
                "Wireless Networks Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4119",
                "Data Mining",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4120",
                "Data Mining Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4121",
                "Computer Vision",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4122",
                "Computer Vision Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-1",
                "CSE 4123",
                "Knowledge Engineering",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-1",
                "CSE 4124",
                "Knowledge Engineering Sessional",
                0.75,
                "LAB");


        insertCourse(db, "CSE 4-2",
                "CSE 4000",
                "Project/Thesis II",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4201",
                "Computer Graphics",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4202",
                "Computer Graphics Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-2",
                "CSE 4203",
                "Machine Learning",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4204",
                "Machine Learning Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-2",
                "CSE 4205",
                "Security and Privacy",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4206",
                "Security and Privacy Sessional",
                0.75,
                "LAB");

        insertCourse(db, "CSE 4-2",
                "CSE 4208",
                "Seminar",
                0.75,
                "LAB");

        // Optional III / IV
        insertCourse(db, "CSE 4-2",
                "CSE 4209",
                "VLSI Design",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4211",
                "Parallel and Distributed Processing",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4213",
                "Impact of Computer on Society",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4215",
                "Decision Support System",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4217",
                "Network Planning",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4219",
                "Human Computer Interaction",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4221",
                "Switching Systems",
                3.00,
                "CLASS");

        insertCourse(db, "CSE 4-2",
                "CSE 4223",
                "Control System Engineering",
                3.00,
                "CLASS");
    }

    // INSERT ONE COURSE

    private void insertCourse(
            SQLiteDatabase db,
            String semester,
            String code,
            String name,
            double credit,
            String type) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_SEMESTER, semester);
        values.put(COLUMN_CODE, code);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CREDIT, credit);
        values.put(COLUMN_TYPE, type);

        db.insert(TABLE_COURSES, null, values);
    }
    //semester er course
    public Cursor getCoursesBySemester(String semester) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_COURSES,
                null,
                COLUMN_SEMESTER + " = ?",
                new String[]{semester},
                null,
                null,
                COLUMN_ID + " ASC"
        );
    }


    public Cursor getClassCourses(String semester) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_COURSES,
                null,
                COLUMN_SEMESTER + " = ? AND " +
                        COLUMN_TYPE + " = ?",
                new String[]{semester, "CLASS"},
                null,
                null,
                COLUMN_ID + " ASC"
        );
    }

    // ---------------------------------------------------------
    // GET LAB COURSES
    // ---------------------------------------------------------

    public Cursor getLabCourses(String semester) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_COURSES,
                null,
                COLUMN_SEMESTER + " = ? AND " +
                        COLUMN_TYPE + " = ?",
                new String[]{semester, "LAB"},
                null,
                null,
                COLUMN_ID + " ASC"
        );
    }
    private void createRoutineTable(SQLiteDatabase db) {

        String createRoutineTable = "CREATE TABLE " + TABLE_ROUTINE + " (" +
                ROUTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ROUTINE_SEMESTER + " TEXT NOT NULL, " +
                ROUTINE_DAY + " TEXT NOT NULL, " +
                ROUTINE_START_TIME + " TEXT NOT NULL, " +
                ROUTINE_COURSE_CODE + " TEXT NOT NULL" +
                ")";

        db.execSQL(createRoutineTable);
    }

    public void saveRoutine(
            String semester,
            String day,
            String startTime,
            String courseCode) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Remove any existing course in this exact slot
        db.delete(
                TABLE_ROUTINE,
                ROUTINE_SEMESTER + " = ? AND " +
                        ROUTINE_DAY + " = ? AND " +
                        ROUTINE_START_TIME + " = ?",
                new String[]{semester, day, startTime}
        );

        ContentValues values = new ContentValues();

        values.put(ROUTINE_SEMESTER, semester);
        values.put(ROUTINE_DAY, day);
        values.put(ROUTINE_START_TIME, startTime);
        values.put(ROUTINE_COURSE_CODE, courseCode);

        db.insert(TABLE_ROUTINE, null, values);
    }


    public Cursor getRoutine(String semester) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_ROUTINE,
                null,
                ROUTINE_SEMESTER + " = ?",
                new String[]{semester},
                null,
                null,
                ROUTINE_ID + " ASC"
        );
    }

    // ---------------------------------------------------------
    // GET ROUTINE FOR A SEMESTER, FILTERED TO ONE DAY
    // ---------------------------------------------------------

    public Cursor getRoutineForDay(String semester, String day) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_ROUTINE,
                null,
                ROUTINE_SEMESTER + " = ? AND " +
                        ROUTINE_DAY + " = ?",
                new String[]{semester, day},
                null,
                null,
                ROUTINE_ID + " ASC"
        );
    }

    public void deleteRoutine(
            String semester,
            String day,
            String startTime) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                TABLE_ROUTINE,
                ROUTINE_SEMESTER + " = ? AND " +
                        ROUTINE_DAY + " = ? AND " +
                        ROUTINE_START_TIME + " = ?",
                new String[]{semester, day, startTime}
        );
    }


    private void createAssignmentsTable(SQLiteDatabase db) {

        String createAssignmentsTable = "CREATE TABLE " + TABLE_ASSIGNMENTS + " (" +
                ASSIGNMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ASSIGNMENT_COURSE_CODE + " TEXT NOT NULL, " +
                ASSIGNMENT_COURSE_NAME + " TEXT NOT NULL, " +
                ASSIGNMENT_DUE_DATE + " TEXT NOT NULL, " +
                ASSIGNMENT_DESCRIPTION + " TEXT, " +
                ASSIGNMENT_ATTACHMENT_URI + " TEXT" +
                ")";

        db.execSQL(createAssignmentsTable);
    }

    // Inseert assignment

    public long insertAssignment(
            String courseCode,
            String courseName,
            String dueDate,
            String description,
            String attachmentUri) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ASSIGNMENT_COURSE_CODE, courseCode);
        values.put(ASSIGNMENT_COURSE_NAME, courseName);
        values.put(ASSIGNMENT_DUE_DATE, dueDate);
        values.put(ASSIGNMENT_DESCRIPTION, description);
        values.put(ASSIGNMENT_ATTACHMENT_URI, attachmentUri);

        return db.insert(TABLE_ASSIGNMENTS, null, values);
    }
    //closest due
    public Cursor getAllAssignments() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_ASSIGNMENTS,
                null,
                null,
                null,
                null,
                null,
                ASSIGNMENT_DUE_DATE + " ASC"
        );
    }


    public void deleteAssignment(long id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                TABLE_ASSIGNMENTS,
                ASSIGNMENT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    private void createClassTestsTable(SQLiteDatabase db) {

        String createClassTestsTable = "CREATE TABLE " + TABLE_CLASS_TESTS + " (" +
                TEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEST_COURSE_CODE + " TEXT NOT NULL, " +
                TEST_COURSE_NAME + " TEXT NOT NULL, " +
                TEST_DATE + " TEXT NOT NULL, " +
                TEST_DESCRIPTION + " TEXT, " +
                TEST_ATTACHMENT_URI + " TEXT" +
                ")";

        db.execSQL(createClassTestsTable);
    }


    public long insertClassTest(
            String courseCode,
            String courseName,
            String testDate,
            String description,
            String attachmentUri) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TEST_COURSE_CODE, courseCode);
        values.put(TEST_COURSE_NAME, courseName);
        values.put(TEST_DATE, testDate);
        values.put(TEST_DESCRIPTION, description);
        values.put(TEST_ATTACHMENT_URI, attachmentUri);

        return db.insert(TABLE_CLASS_TESTS, null, values);
    }


    public Cursor getAllClassTests() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(
                TABLE_CLASS_TESTS,
                null,
                null,
                null,
                null,
                null,
                TEST_DATE + " ASC"
        );
    }


    public void deleteClassTest(long id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                TABLE_CLASS_TESTS,
                TEST_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}