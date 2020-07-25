package com.diptution.fee;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class VariableMethods {

    private static final String TAG = "Result";
    public static final String[] classes = {"Class 6",
        "Class 7", "Class 8", "Class 9",
        "Class 10", "Class 11", "Class 12", "JEE"};

    public static  String[] NAME_OF_MONTHS =
            {"Mar", "Apr", "May", "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec","Jan", "Feb"};


    public static boolean checkIfAllFieldsWereInserted(String[] fields) {
        for (String details : fields) {
            if (details.equals("")) {

                return false;
            }
        }
        return true;
    }

    public static LinkedHashMap<Integer, String>
    convertAccordingToMonth(LinkedHashMap<Integer, String> map, int offSet){
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> months = new ArrayList<>();

        for (Integer key : map.keySet()){
            dates.add(map.get(key));
            key = key - offSet;

            if (key <1){
                key = key + 12;
            }
            months.add(key);
        }

        map.clear();

        for (int i =0; i < months.size(); i++){
            map.put(months.get(i), dates.get(i));
           // Log.d(TAG, "convertAccordingToMonth: " + months.get(i) + " : " + dates.get(i));
        }

        return map;

    }

    static LinkedHashMap<Integer, String> getLinkedHashMapFromString(String s) {
        LinkedHashMap<Integer, String> records = new LinkedHashMap<>();

        String[] eachRecords = s.split(", ");
        if (eachRecords.length > 0) {
            for (String eachRecord : eachRecords) {
             //   Log.d(TAG, "getLinkedHashMapFromString: each record " + eachRecord);
                if (eachRecord.length() > 0) {
                    String[] month_date = eachRecord.split(" : ");
                    records.put(Integer.parseInt(month_date[0]), month_date[1]);
                } else {
                    return records;
                }
            }
        } else {
            Log.d(TAG, "getLinkedHashMapFromString : empty records");
        }
        // Log.d(TAG, "getLinkedHashMapFromString : "+ records.toString());
        return records;
    }

    static String convertHashMapToString(LinkedHashMap<Integer, String> tempList) {
        StringBuilder tempRecords = new StringBuilder();

        if (tempList.size() > 0) {
            for (Integer month : tempList.keySet()) {
                tempRecords.append(month).append(" : ").append(tempList.get(month)).append(", ");
            }
        }
        return tempRecords.toString();
    }

    static int getLastMonth(LinkedHashMap<Integer, String> oldRecords, int theLast) {
        for (Integer key : oldRecords.keySet()) {
            if (key > theLast) {
                theLast = key;
            }
        }
        return theLast;
    }

    static boolean checkForPaymentOfNewJanOrFeb(Student student, int monthNumber) {
        if (monthNumber == 1 || monthNumber == 2) {
            if (!student.getFeeRecords().containsKey(monthNumber)) {
                return false;
            } else if (student.getFeeRecords().containsKey(monthNumber)) {
                String yearOfJanOrFebPaid = student.getFeeRecords().get(monthNumber).split(" / ")[2];
                String yearOfDecember = student.getFeeRecords().get(12).split(" / ")[2];
                //  Log.d(TAG, "getUnpaidStudents: date month " + yearOfJanOrFebPaid);
                return Integer.parseInt(yearOfJanOrFebPaid) > Integer.parseInt(yearOfDecember);
            }
        }
        return false;
    }
}
