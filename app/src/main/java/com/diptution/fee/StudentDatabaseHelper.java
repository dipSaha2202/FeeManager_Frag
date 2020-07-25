package com.diptution.fee;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StudentDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "feeRecords.db";
    protected static final String TABLE_NAME = "records";

    private static final String COL_CLASS = "class";
    private static final String COL_NAME = "studentName";
    private static final String COL_LAST_MONTH = "lastPaidMonth";
    private static final String COL_FEE = "fee";
    private static final String COL_RECORDS = "records";
    private static final String COL_PHONE = "phone";

    private static final int version = 1;
    private static final String TAG = "Result";

    public StudentDatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TABLE_NAME +
                        "("
                        + COL_CLASS + " TEXT, "
                        + COL_NAME + " TEXT, "
                        + COL_FEE + " TEXT, "
                        + COL_LAST_MONTH + " INTEGER, "
                        + COL_RECORDS + " TEXT, "
                        + COL_PHONE +  " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertStudent(Student student) {
        Student studentIfExist = getStudent(student.getClassOfTheStudent(), student.getName());
        if (studentIfExist == null) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COL_CLASS, student.getClassOfTheStudent());
            values.put(COL_NAME, student.getName());
            values.put(COL_FEE, student.getFee());
            values.put(COL_LAST_MONTH, student.getLastPaidMonth());
            values.put(COL_RECORDS, VariableMethods.convertHashMapToString(student.getFeeRecords()));
            values.put(COL_PHONE, student.getPhoneNumber());

            long id = database.insert(TABLE_NAME, null, values);

            database.close();
            return id;
        }
        return -1;
    }

    public ArrayList<Student> getAllStudentsWithoutRecord(String selectedClass) {
        ArrayList<Student> allStudentsList = new ArrayList<>();

        String selectAllQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_CLASS + " = '" + selectedClass + "'" +
                " ORDER BY " + COL_NAME + " ASC";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectAllQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student(
                        cursor.getString(
                                cursor.getColumnIndex(COL_CLASS)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(cursor.getColumnIndex(COL_PHONE)));

                student.setLastPaidMonth(cursor.getInt(3));

                allStudentsList.add(student);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return allStudentsList;
    }

    public ArrayList<String> getAllNamesByClass(String selectedClass) {
        ArrayList<String> allNamesOfTheClass = new ArrayList<>();

        String selectAllQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + COL_CLASS + " = '" + selectedClass + "'" +
                " ORDER BY " + COL_NAME + " ASC";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectAllQuery, null);

        if (cursor.moveToFirst()) {
            do {
                allNamesOfTheClass.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return allNamesOfTheClass;
    }
    public int updateRecordsAndLastPayedMonth(String studentClass, String studentName,
                                              int lastPaidMonth, String recordsAsString) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_LAST_MONTH, lastPaidMonth);
        cv.put(COL_RECORDS, recordsAsString);

        int rowsEffected = database.update(TABLE_NAME, cv,
                COL_NAME + " = " + "'" + studentName + "'" +
                        " AND " + COL_CLASS + " = " + "'" + studentClass + "'", null);

        database.close();
        return rowsEffected;
    }

    public int replaceValue(String columnName, String replace, String byThis) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(columnName, byThis);

        return database.update(TABLE_NAME, values,
                columnName + " = " + "'" + replace + "'", null);
    }

    public void clearTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName);
        db.close();
    }

    public LinkedHashMap<Integer, String> getFeeRecordByName(String selectedClass, String name) {
        LinkedHashMap<Integer, String> recordsForTheStudent;

        String findRecordsQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_CLASS + " = '" + selectedClass + "'" +
                " AND " + COL_NAME + " = '" + name + "'";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(findRecordsQuery, null);
        String s = "";

        if (cursor.moveToFirst()) {
            do {
                s = cursor.getString(cursor.getColumnIndex(COL_RECORDS));
            } while (cursor.moveToNext());
        }

        recordsForTheStudent = VariableMethods.getLinkedHashMapFromString(s);
        cursor.close();
        recordsForTheStudent = sortOrderByMonth(recordsForTheStudent);

        return recordsForTheStudent;
    }

    public LinkedHashMap<Integer, String> sortOrderByMonth(LinkedHashMap<Integer, String> dataSet) {
        if (dataSet.size() > 0) {
            Set<Map.Entry<Integer, String>> monthsSet = dataSet.entrySet();
            List<Map.Entry<Integer, String>> monthsListEntry = new ArrayList<>(monthsSet);
            Collections.sort(monthsListEntry,
                    new Comparator<Map.Entry<Integer, String>>() {
                        @Override
                        public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                            return o1.getKey().toString().compareTo(o2.getKey().toString());
                        }
                    });
            dataSet.clear();
            for (Map.Entry<Integer, String> map : monthsListEntry) {
                dataSet.put(map.getKey(), map.getValue());
            }
        }
        return dataSet;
    }

    public StudentDetailsFirebase getStudentForFirebase(String className, String studentName){
        StudentDetailsFirebase studentDetailsFirebase = null;

        String findRecordsQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_CLASS + " = '" + className + "'" +
                " AND " + COL_NAME + " = '" + studentName + "'";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(findRecordsQuery, null);

        if (cursor.moveToFirst()) {
            do {
                studentDetailsFirebase = new StudentDetailsFirebase(
                        cursor.getString(cursor.getColumnIndex(COL_FEE)),
                        cursor.getString(cursor.getColumnIndex(COL_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COL_RECORDS)),
                        cursor.getInt(cursor.getColumnIndex(COL_LAST_MONTH)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return studentDetailsFirebase;

    }
    public Student getStudent(String selectedClass, String name) {
        Student student = null;

        String findRecordsQuery = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL_CLASS + " = '" + selectedClass + "'" +
                " AND " + COL_NAME + " = '" + name + "'";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(findRecordsQuery, null);

        if (cursor.moveToFirst()) {
            do {
                student = new Student(
                        cursor.getString(
                                cursor.getColumnIndex(COL_CLASS)),
                        cursor.getString(cursor.getColumnIndex(COL_NAME)),
                        cursor.getString(2),
                        cursor.getString(cursor.getColumnIndex(COL_PHONE)));

                student.setLastPaidMonth(cursor.getInt(3));

                String s = cursor.getString(4);
                LinkedHashMap<Integer, String> records = VariableMethods.getLinkedHashMapFromString(s);
                student.setFeeRecords(records);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return student;
    }

    public int deleteAndRewriteFeeRecords(String selectedClass, String selectedName, Integer deletedMonth) {
        LinkedHashMap<Integer, String> oldRecords = this.getFeeRecordByName(
                selectedClass, selectedName);

      //  Log.d(TAG, "deleteAndRewriteFeeRecords: old " + oldRecords.toString());
        oldRecords.remove(deletedMonth);
        int lastMonthNow = VariableMethods.getLastMonth(oldRecords, 0);

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_LAST_MONTH, lastMonthNow);
        values.put(COL_RECORDS, VariableMethods.convertHashMapToString(oldRecords));

        int rowsEffected = database.update(
                TABLE_NAME,
                values,
                COL_NAME + " = " + "'" + selectedName + "'" +
                        " AND " + COL_CLASS + " = " + "'" + selectedClass + "'", null);

        database.close();
        return rowsEffected;
    }

    public int updateRecords(String studentClass, String studentName, String payingMonth, String date) {
        SQLiteDatabase database = this.getWritableDatabase();
        LinkedHashMap<Integer, String> records = this.getFeeRecordByName(
                studentClass, studentName);

        int lastMonth = VariableMethods.getLastMonth(
                records, Integer.parseInt(payingMonth));
        ContentValues cv = new ContentValues();

        cv.put(COL_LAST_MONTH, lastMonth);
        records.put(Integer.parseInt(payingMonth), date);
        cv.put(COL_RECORDS, VariableMethods.convertHashMapToString(records));

        int rowsEffected = database.update(
                TABLE_NAME,
                cv,
                COL_NAME + " = " + "'" + studentName + "'" +
                        " AND " + COL_CLASS + " = " + "'" + studentClass + "'", null);

        database.close();
        return rowsEffected;
    }
    public int updatePhoneAndFee(String studentClass, String studentName,
                             String fee, String phoneNo) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_FEE, fee);
        cv.put(COL_PHONE, phoneNo);

        int rowsEffected = database.update(TABLE_NAME, cv,
                COL_NAME + " = " + "'" + studentName + "'" +
                        " AND " + COL_CLASS + " = " + "'" + studentClass + "'", null);

        database.close();
        return rowsEffected;
    }
    public int clearRecordsForStudent(String className, String studentName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_LAST_MONTH, 0);
        cv.put(COL_RECORDS, "");

        int rowsEffected = database.update(TABLE_NAME, cv,
                COL_NAME + " = " + "'" + studentName + "'"
                        + " AND " + COL_CLASS + " = " + "'" + className + "'", null);

        database.close();
        return rowsEffected;
    }

    public boolean deleteStudent(Student student) {
        SQLiteDatabase database = this.getWritableDatabase();

        int successful = database.delete(
                TABLE_NAME,
                COL_NAME + " = ? and " + COL_CLASS + " = ?",
                new String[]{student.getName(), student.getClassOfTheStudent()});

        database.close();
        return successful > 0;
    }
}