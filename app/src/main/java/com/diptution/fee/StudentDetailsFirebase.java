package com.diptution.fee;

public class StudentDetailsFirebase {
    private String fee, phoneNumber, records;
    private int lastPaidMonth;
    public StudentDetailsFirebase() {
    }

    public StudentDetailsFirebase(String fee, String phoneNumber, String records, int lastPaidMonth) {
        this.fee = fee;
        this.phoneNumber = phoneNumber;
        this.records = records;
        this.lastPaidMonth = lastPaidMonth;
    }

    public String getFee() {
        return fee;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRecords() {
        return records;
    }

    public int getLastPaidMonth() {
        return lastPaidMonth;
    }

    @Override
    public String toString() {
        return "StudentDetailsFirebase{" +
                "fee='" + fee + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", records='" + records + '\'' +
                ", lastPaidMonth=" + lastPaidMonth +
                '}';
    }
}
