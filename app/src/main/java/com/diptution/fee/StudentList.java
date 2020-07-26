package com.diptution.fee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class StudentList extends Fragment {
    private ListView listView;
    private Spinner spinnerClass;
    private TextView txtCount;
    private StudentDatabaseHelper databaseHelper;
    private ArrayList<Student> students;

    private ArrayAdapter<String> spinnerClassAdapter;
    private StudentListAdapter adapter;

    private String[] menuItems;
    private String selectedClass;
    AdminPage activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_list, container, false);

        spinnerClass = view.findViewById(R.id.spinnerClass_studentList);
        listView = view.findViewById(R.id.listStudent_studentList);
        txtCount = view.findViewById(R.id.txtStudentCount);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null || getActivity() == null){
            return;
        }

        activity = (AdminPage) getActivity();
        activity.toolbar.setTitle("All Student");

        students = new ArrayList<>();

        spinnerClassAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, VariableMethods.classes);

        databaseHelper = new StudentDatabaseHelper(getContext());
        adapter = new StudentListAdapter(getContext(), students);

        spinnerClass.setAdapter(spinnerClassAdapter);
        listView.setAdapter(adapter);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = parent.getItemAtPosition(position).toString();

                students.clear();
                students.addAll(databaseHelper.getAllStudentsWithoutRecord(selectedClass));
                adapter.notifyDataSetChanged();

                txtCount.setText(getString(R.string.total_students, students.size()));

                if (students.size() < 1) {
                   activity.showSnackBar("No student in the Class");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        registerForContextMenu(listView);
        menuItems = getResources().getStringArray(R.array.long_click_options);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listStudent_studentList) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(students.get(info.position).getName());
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String menuItemName = menuItems[menuItemIndex];

        if (menuItemName.matches("Delete")) {
            boolean isDeleted = databaseHelper.deleteStudent(students.get(info.position));

            if (isDeleted) {
               activity.showSnackBar(students.get(info.position).getName() + " Deleted");
            }
            students.remove(info.position);
        }
        adapter.notifyDataSetChanged();
        txtCount.setText(getString(R.string.total_students, students.size()));
        return true;
    }
}
