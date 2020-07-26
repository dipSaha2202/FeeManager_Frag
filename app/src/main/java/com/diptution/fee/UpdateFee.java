package com.diptution.fee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

public class UpdateFee extends Fragment {
    private static final String TAG = "Result";

    private TextView txtLastMonth, txtDate;
    private EditText edtFee, edtPhone;
    private Spinner spinnerClass, spinnerNames;
    private Button btnUpdate, btnUpdateDetails;

    private int payingMonth;
    private String selectedClass, selectedName, phoneNumber;

    private Calendar calenderForClass;
    private ArrayAdapter<String> spinnerClassAdapter, spinnerNamesAdapter;
    private ArrayList<String> namesOfTheSelectedClass;
    private ArrayList<Student> studentArrayList;

    private StudentDatabaseHelper databaseHelper;
    private Student student;
    private AdminPage activity;
    private Context context;

    private Button btnIncrease, btnShowCalender;

    private Vibrator v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.update_fee, container, false);
        txtLastMonth = root.findViewById(R.id.txtLastMonth_update);
        txtDate = root.findViewById(R.id.txtPayDate_update);
        edtFee = root.findViewById(R.id.edtFee_update);
        edtPhone = root.findViewById(R.id.edtContact_update);
        btnUpdate = root.findViewById(R.id.btnUpdate);
        btnUpdateDetails = root.findViewById(R.id.btnUpdateDetails);
        spinnerClass = root.findViewById(R.id.spinnerClass_update);
        spinnerNames = root.findViewById(R.id.spinnerName_update);
        btnIncrease = root.findViewById(R.id.btnIncreaseMonth);
        btnShowCalender = root.findViewById(R.id.btnShowCalender);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (AdminPage) getActivity();
        context = getContext();
        activity.toolbar.setTitle("Update Fees");

        studentArrayList = new ArrayList<>();
        namesOfTheSelectedClass = new ArrayList<>();

        spinnerClassAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, VariableMethods.classes);

        spinnerNamesAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, namesOfTheSelectedClass);

        spinnerClass.setAdapter(spinnerClassAdapter);
        spinnerNames.setAdapter(spinnerNamesAdapter);

        databaseHelper = new StudentDatabaseHelper(context);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMethod(v);
            }
        };

        btnShowCalender.setOnClickListener(listener);
        btnIncrease.setOnClickListener(listener);
        btnUpdate.setOnClickListener(listener);
        btnUpdateDetails.setOnClickListener(listener);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = parent.getItemAtPosition(position).toString();

                studentArrayList.clear();
                namesOfTheSelectedClass.clear();
                studentArrayList.addAll(databaseHelper.getAllStudentsWithoutRecord(selectedClass));

                for (int i = 0; i < studentArrayList.size(); i++) {
                    namesOfTheSelectedClass.add(studentArrayList.get(i).getName());
                }
                spinnerNamesAdapter.notifyDataSetChanged();

                if (namesOfTheSelectedClass.size() > 0) {
                    btnUpdate.setEnabled(true);
                    btnUpdateDetails.setEnabled(true);
                    btnUpdate.setAlpha(1f);
                    btnUpdateDetails.setAlpha(1f);
                    selectedName = namesOfTheSelectedClass.get(0);
                    spinnerNames.setSelection(0);
                    updateFieldsAccordingToStudent(selectedClass, selectedName);
                } else {
                    edtFee.setText("");
                    txtDate.setText("");
                    edtPhone.setText("");
                    txtLastMonth.setText("");
                    btnUpdate.setEnabled(false);
                    btnUpdate.setAlpha(0.5f);
                    btnUpdateDetails.setAlpha(0.5f);
                    btnUpdateDetails.setEnabled(false);
                    activity.showToast("No Student in The Class");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerNames.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedName = parent.getItemAtPosition(position).toString();
                updateFieldsAccordingToStudent(selectedClass, selectedName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        calenderForClass = Calendar.getInstance();

        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void showCalender() {
        int mYear = calenderForClass.get(Calendar.YEAR);
        int mMonth = calenderForClass.get(Calendar.MONTH);
        int mDay = calenderForClass.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        txtDate.setText(getString(R.string.date_class,
                                dayOfMonth, monthOfYear + 1, year));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void updateFieldsAccordingToStudent(String classOfStudent, String studentName) {
        student = databaseHelper.getStudent(classOfStudent, studentName);

        int lastMonth = student.getLastPaidMonth();
        phoneNumber = student.getPhoneNumber();

        if (lastMonth > 0 && lastMonth < 13) {
            txtLastMonth.setText(VariableMethods.NAME_OF_MONTHS[lastMonth - 1]);
            payingMonth = lastMonth;
        } else if (lastMonth == 0) {
            txtLastMonth.setText(getString(R.string.month_for_zero));
            payingMonth = 0;
        } else {
            txtLastMonth.setText(getString(R.string.error));
            payingMonth = 0;
        }

        if (student.getFeeRecords().containsKey(lastMonth)) {
            txtDate.setText(student.getFeeRecords().get(lastMonth));
        } else {
            txtDate.setText("");
        }
        edtFee.setText(student.getFee());
        edtPhone.setText(student.getPhoneNumber().substring(3));
    }

    public void increaseMonthByOne() {
        txtDate.setText("");

        if (payingMonth < 12) {
            payingMonth += 1;
            txtLastMonth.setText(VariableMethods.NAME_OF_MONTHS[payingMonth - 1]);
        } else {
            payingMonth = 0;
            txtLastMonth.setText(getString(R.string.month_for_zero));
        }

        if (student != null) {
            LinkedHashMap<Integer, String> recordsForTheSelected = student.getFeeRecords();
            if (recordsForTheSelected.containsKey(payingMonth)) {
                txtDate.setText(recordsForTheSelected.get(payingMonth));
            }
        }
    }


    private void updateFeeRecords() {
        String date = txtDate.getText().toString();
        String month = String.valueOf(payingMonth);

        if (date.isEmpty()) {
            activity.showToast("Enter Date");
            return;
        }

        if (payingMonth != 0) {
            int rowsEffected = databaseHelper.updateRecords(selectedClass, selectedName, month, date);

            if (rowsEffected > 0) {
                activity.showToast("Updated " + selectedClass + " : " + selectedName +
                        " month paid : " + VariableMethods.NAME_OF_MONTHS[payingMonth - 1] +
                        " on date : " + date);

               // String messageForStudent = selectedName + " : Payment done for "
                     //   + VariableMethods.NAME_OF_MONTHS[payingMonth - 1] + " on date : " + date;
                // sendTextMessage(messageForStudent);
            } else {
                activity.showToast("Error Updating");
            }
        } else {
            activity.showToast("No Payment can not be update");
        }
    }

    private void updateDetails() {
        String phone = edtPhone.getText().toString();
        String fee = edtFee.getText().toString();

        if (fee.length() == 0) {
            activity.showToast("Fee is required.");
            return;
        }

        if (phone.length() == 0 || phone.length() == 10) {
            databaseHelper.updatePhoneAndFee(selectedClass, selectedName, fee, "+91" + phone);
            activity.showToast("Updated " + selectedName + " phone " + phone + " fee " + fee);
        } else {
            activity.showToast("Either enter 10 digit or leave blank");
        }
    }

    private void sendTextMessage(String message) {
        if (!AdminPage.PermissionForSMS || !AdminPage.PermissionForPhoneState) {
            activity.showToast("does not have permission for sms");
            return;
        }

        if (phoneNumber.length() < 10) {
            activity.showToast("Student Does not have valid number. message was not sent");
            return;
        }

        SubscriptionManager manager = SubscriptionManager.from(context);

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int slotIndex = Integer.parseInt(preferences.getString("sim_adminSettings", "0"));

        String detailsOfSlot = manager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex).toString();

        try {
            int subId = Integer.parseInt(detailsOfSlot.substring(4, 5));
            // Log.d(TAG, "updateStudent: " + subId);
            SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subId);
            // smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            activity.showToast("message sent to " + selectedName + " on " + phoneNumber);
        } catch (Exception e) {
            Log.d(TAG, "updateStudent: " + "can not parse int");
            activity.showToast("can not fetch sim ID");
        }
    }

    public void chooseMethod(View view) {
        if (LogIn.DEFAULT_VIBRATION) {
            v.vibrate(LogIn.VIBRATION_TIME);
        }
        switch (view.getId()) {
            case R.id.btnUpdate:
                updateFeeRecords();
                break;
            case R.id.btnUpdateDetails:
                updateDetails();
                break;
            case R.id.btnShowCalender:
                showCalender();
                break;
            case R.id.btnIncreaseMonth:
                increaseMonthByOne();
                break;
            default:
                break;
        }
    }
}
