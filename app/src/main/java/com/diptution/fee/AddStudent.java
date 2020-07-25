package com.diptution.fee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddStudent extends Fragment {
    private Spinner spinnerClass;
    private EditText edtName, edtFee, edtPhone;
    private ArrayAdapter<String> spinnerClassAdapter;
    private String selectedClass;
    private StudentDatabaseHelper databaseHelper;
    private Button btnAddStudent;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null || getActivity() == null){
            return;
        }

        AdminPage activity = (AdminPage) getActivity();
        activity.toolbar.setTitle("Add Student");

        spinnerClass = view.findViewById(R.id.spinnerClass_addStudent);
        edtName = view.findViewById(R.id.edtName_addStudent);
        edtFee = view.findViewById(R.id.edtFee_addStudent);
        edtPhone = view.findViewById(R.id.edtPhone_addStudent);
        btnAddStudent = view.findViewById(R.id.btnAddStudent_add);

        spinnerClassAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item,
                VariableMethods.classes);

        databaseHelper = new StudentDatabaseHelper(getContext());

        spinnerClass.setAdapter(spinnerClassAdapter);
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = VariableMethods.classes[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStudent();
            }
        });

    }

    public void addStudent() {
        String name = edtName.getText().toString();
        String fee = edtFee.getText().toString();
        String phone = "+91" + edtPhone.getText().toString();

        if (phone.length() != 3 && phone.length() != 13) {
            showToast("Either 10 digit or leave blank");
            return;
        }
        if (VariableMethods.checkIfAllFieldsWereInserted(new String[]{name, fee})) {
            Student student = new Student(selectedClass, name, fee, phone);

            int id = (int) databaseHelper.insertStudent(student);
            if (id >= 0) {
                edtFee.setText("");
                edtName.setText("");
                edtPhone.setText("");
                showToast("Added-" + selectedClass + "-" + name + " fee: " + fee);
            } else {
                showToast("Error! Check if already exists");
            }
        } else {
            showToast("Enter all fields");
        }
    }
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
