package com.diptution.fee;

import java.util.LinkedHashMap;

public class Student {
    private String name, fee, classOfTheStudent, phoneNumber;
    private int lastPaidMonth;
    private LinkedHashMap<Integer, String> feeRecords;

    public Student(String classOfTheStudent, String name, String fee, String phone) {
        this.name = name;
        this.fee = fee;
        this.classOfTheStudent = classOfTheStudent;
        lastPaidMonth = 0;
        feeRecords = new LinkedHashMap<>();
        this.phoneNumber = phone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getFee() {
        return fee;
    }

    public String getClassOfTheStudent() {
        return classOfTheStudent;
    }

    public int getLastPaidMonth() {
        return lastPaidMonth;
    }

    public void setLastPaidMonth(int lastPaidMonth) {
        this.lastPaidMonth = lastPaidMonth;
    }

    public LinkedHashMap<Integer, String> getFeeRecords() {
        return feeRecords;
    }

    public void setFeeRecords(LinkedHashMap<Integer, String> feeRecords) {
        this.feeRecords.clear();
        this.feeRecords.putAll(feeRecords);
    }

    public String getDetails() {
        return "Student{" +
                "name='" + name + '\'' +
                ", fee='" + fee + '\'' +
                ", classOfTheStudent='" + classOfTheStudent + '\'' +
                ", lastPaidMonth=" + lastPaidMonth +
                ", feeRecords=" + feeRecords +
                '}';
    }
}
