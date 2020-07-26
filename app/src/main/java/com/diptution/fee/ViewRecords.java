package com.diptution.fee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ViewRecords extends Fragment {
    private static final String TAG = "Result";

    private ListView listViewOfRecords;
    private Spinner spinnerClass, spinnerName;

    private StudentDatabaseHelper databaseHelper;
    private RecordListAdapter adapter;

    private String selectedClass, selectedName;
    private LinkedHashMap<Integer, String> records;
    private String[] menuItems;
    private ArrayList<String> names;
    private ArrayAdapter<String> spinnerClassAdapter, spinnerNameAdapter;
    private AdminPage activity;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_records, container, false);

        listViewOfRecords = view.findViewById(R.id.listViewRecords_List);
        spinnerClass = view.findViewById(R.id.spinnerClass_View);
        spinnerName = view.findViewById(R.id.spinnerName_View);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() == null || getActivity() == null){
            return;
        }
        activity = (AdminPage) getActivity();
        context = getContext();
        activity.toolbar.setTitle("View Records");

        names = new ArrayList<>();
        records = new LinkedHashMap<>();

        databaseHelper = new StudentDatabaseHelper(context);

        spinnerClassAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, VariableMethods.classes);
        spinnerNameAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, names);

        adapter = new RecordListAdapter(getContext(), records);

        spinnerClass.setAdapter(spinnerClassAdapter);
        spinnerName.setAdapter(spinnerNameAdapter);
        listViewOfRecords.setAdapter(adapter);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = parent.getItemAtPosition(position).toString();

                names.clear();
                records.clear();
                names.addAll(databaseHelper.getAllNamesByClass(selectedClass));
                spinnerNameAdapter.notifyDataSetChanged();

                if (names.size() > 0) {
                    selectedName = names.get(0);
                    spinnerName.setSelection(0);
                    records.putAll(databaseHelper.getFeeRecordByName(
                            selectedClass, selectedName));
                } else {
                    activity.showSnackBar("No Students in the class");
                }

                adapter.updateList(records);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedName = parent.getItemAtPosition(position).toString();

                records.clear();
                records.putAll(databaseHelper.getFeeRecordByName(
                        selectedClass, selectedName));

                adapter.updateList(records);
                if (records.size() == 0) {
                    activity.showSnackBar("Student does not have any records");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        registerForContextMenu(listViewOfRecords);
        menuItems = getResources().getStringArray(R.array.long_click_options);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_records, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clearRecords_recordsMenu){
            int rowChanged = databaseHelper.clearRecordsForStudent(selectedClass, selectedName);

            if (rowChanged < 0){
                activity.showSnackBar("Error while clearing");
                return true;
            }

            records.clear();
            adapter.updateList(records);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewRecords_List) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            int deletionMonth = adapter.getItem(info.position);

            menu.setHeaderTitle(VariableMethods.NAME_OF_MONTHS[deletionMonth - 1] +
                    " : " + records.get(deletionMonth));

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

        int deletionMonth = adapter.getItem(info.position);
       // Log.d(TAG, "onContextItemSelected: delete request : " + deletionMonth);
        if (menuItemName.matches("Delete")) {

            int rowsDeleted = databaseHelper.deleteAndRewriteFeeRecords(
                    selectedClass, selectedName, deletionMonth);

            if (rowsDeleted < 0) {
                activity.showSnackBar("Error!!");
                return true;
            }
            records.remove(deletionMonth);
            adapter.updateList(records);
        }
        return true;
    }
}
