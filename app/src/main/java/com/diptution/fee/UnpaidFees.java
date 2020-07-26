package com.diptution.fee;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class UnpaidFees extends Fragment {
    private Spinner spinnerClass, spinnerMonth;
    private ListView listViewOfStudent;
    private TextView txtTotalFee, txtUnpaidFee;

    private ArrayAdapter<String> spinnerClassAdapter, spinnerMonthAdapter, unPaidListAdapter;
    private StudentDatabaseHelper databaseHelper;
    private ArrayList<Student> allStudentInClass;
    private ArrayList<String> unPaidNamesAndMonth;
    private String selectedClass;

    private int totalFeeOftheClass, unPaidFeeOftheClass;
    private static final String TAG = "Result";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.unpaid_fee, container, false);

        spinnerClass = root.findViewById(R.id.spinnerClass_unpaidFee);
        spinnerMonth = root.findViewById(R.id.spinnerMonth_unpaidFee);
        listViewOfStudent = root.findViewById(R.id.listStudent_unpaidFee);
        txtTotalFee = root.findViewById(R.id.txtTotalFee_unpaid);
        txtUnpaidFee = root.findViewById(R.id.txtUnpaidFee_unpaid);
        
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null || getActivity() == null){
            return;
        }

        AdminPage activity = (AdminPage) getActivity();
        Context context = getContext();
        activity.toolbar.setTitle("Unpaid Fees");

        allStudentInClass = new ArrayList<>();
        unPaidNamesAndMonth = new ArrayList<>();
        totalFeeOftheClass = 0;
        unPaidFeeOftheClass = 0;

        unPaidListAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, unPaidNamesAndMonth);

        spinnerClassAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, VariableMethods.classes);

        spinnerMonthAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, VariableMethods.NAME_OF_MONTHS);

        databaseHelper = new StudentDatabaseHelper(context);

        spinnerClass.setAdapter(spinnerClassAdapter);
        spinnerMonth.setAdapter(spinnerMonthAdapter);
        listViewOfStudent.setAdapter(unPaidListAdapter);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedClass = VariableMethods.classes[position];
                Log.d(TAG, "onItemSelected: " + selectedClass);

                spinnerMonth.setSelection(0);
                String monthName = spinnerMonth.getItemAtPosition(0).toString();

                getUnpaidStudents(selectedClass, 0, monthName);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getUnpaidStudents(selectedClass, position,
                        parent.getItemAtPosition(position).toString());
                Log.d(TAG, "onItemSelected: " + position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getUnpaidStudents(String selectedClass, int listPositionStartingFromZero, String monthName) {
        allStudentInClass.clear();
        unPaidNamesAndMonth.clear();
        totalFeeOftheClass = 0;
        unPaidFeeOftheClass = 0;
      //  int month = java.util.Arrays.asList(VariableMethods.NAME_OF_MONTHS).indexOf(monthName);

        int month = listPositionStartingFromZero + 1;

        Log.d(TAG, "getUnpaidStudents: monthName index " + month + " " + monthName);

        allStudentInClass.addAll(databaseHelper.getAllStudentsWithoutRecord(selectedClass));

        for (Student student : allStudentInClass) {
            totalFeeOftheClass += Integer.parseInt(student.getFee());

            if (student.getLastPaidMonth() < month) {
                if (student.getLastPaidMonth() == 0) {
                    unPaidFeeOftheClass += Integer.parseInt(student.getFee());
                    unPaidNamesAndMonth.add(student.getName() + " : " + "No Payment");
                    continue;
                }
                unPaidNamesAndMonth.add(student.getName() + " : " +
                        VariableMethods.NAME_OF_MONTHS[student.getLastPaidMonth() - 1]);
                unPaidFeeOftheClass += Integer.parseInt(student.getFee());
            }
        }

        unPaidListAdapter.notifyDataSetChanged();
        txtTotalFee.setText(getString(R.string.totalFee_unpaid, totalFeeOftheClass));
        txtUnpaidFee.setText(getString(R.string.unpaidFee_unpaid, unPaidFeeOftheClass));
    }
}
