package com.diptution.fee;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class BackUp extends AppCompatActivity {
    private static final String TABLE_NAME = "records"; // change to test for testing // or records
    private static final String TIME_OF_BACK_UP = "timeOfBackUp";
    private static final String COL_CLASS = "class";
    private static final String BASE_URL = "";
    private StudentDatabaseHelper databaseHelper;

    private DatabaseReference recordsReference, rootRef;
    private FirebaseDatabase firebaseDatabase;

    private TextView txtTime;
    private ProgressBar progressBar;

    private String time;
    private Handler handler;
    private static final String TAG = "Result";
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back_up);

        txtTime = findViewById(R.id.txtBackUpTime_BackUp);
        progressBar = findViewById(R.id.progressBar);

        time = "No Back Up Time Found";
        txtTime.setText(time);

        databaseHelper = new StudentDatabaseHelper(BackUp.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference();
        recordsReference = rootRef.child(TABLE_NAME);

        progressBar.setVisibility(View.VISIBLE);
        handler = new Handler(getMainLooper());

        DatabaseReference timeRef = rootRef.child(TIME_OF_BACK_UP);
        timeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                time = dataSnapshot.getValue(String.class);

                if (time == null || time.equals("")) {
                    time = "No BackUp Time Found";
                }
                txtTime.setText(getString(R.string.last_back_up, time));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }


    public void backUpData() {
        progressBar.setVisibility(View.VISIBLE);
        rootRef.child(TABLE_NAME).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        writeNewData();
                    }
                }, 1000);
            }
        });
        recordsReference = firebaseDatabase.getReference(TABLE_NAME);
      //  progressBar.setVisibility(View.INVISIBLE);
    }

    private void writeNewData() {
        progressBar.setVisibility(View.VISIBLE);
        for (String eachClass : VariableMethods.classes) {
            DatabaseReference classReference = recordsReference.child(eachClass);

            for (String name : databaseHelper.getAllNamesByClass(eachClass)) {
                DatabaseReference namesInClass = classReference.child(name);
                StudentDetailsFirebase detailsFirebase = databaseHelper.getStudentForFirebase(eachClass, name);
                namesInClass.setValue(detailsFirebase);
            }
        }
        String currentTime = Calendar.getInstance().getTime().toString().substring(4, 20);

        rootRef.child(TIME_OF_BACK_UP).setValue(currentTime);
        txtTime.setText(getString(R.string.last_back_up, currentTime));
        showSnackBar("Saved To DataBase");
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void restoreData() {
        databaseHelper.clearTable(StudentDatabaseHelper.TABLE_NAME);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                recordsReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String classNameUnderRecords = dataSnapshot.getKey();

                        for (DataSnapshot studentNamesSnapShot : dataSnapshot.getChildren()) {
                   /* Log.d(TAG, "onChildAdded:  for loop class : "
                            + classNameUnderRecords + " : " + studentNamesSnapShot.getKey());*/
                            String  studentName = studentNamesSnapShot.getKey();
                            StudentDetailsFirebase detailsFirebase = studentNamesSnapShot
                                    .getValue(StudentDetailsFirebase.class);

                            if (studentName == null || studentName.length() ==0 || detailsFirebase == null) {
                                continue;
                            }

                            Student student = new Student(classNameUnderRecords, studentName,
                                    detailsFirebase.getFee(), detailsFirebase.getPhoneNumber());
                            long id = databaseHelper.insertStudent(student);
                            if (id >= 0) {
                                databaseHelper.updateRecordsAndLastPayedMonth(
                                        student.getClassOfTheStudent(), studentName,
                                        detailsFirebase.getLastPaidMonth(), detailsFirebase.getRecords());
                            }
                        }
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showSnackBar("Restoration Completed");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public void increaseSession() {
        new AlertDialog.Builder(BackUp.this)
                .setTitle("Increase Session")
                .setMessage("Are you sure about Increase Session? It can not be Undone")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        increaseSessionByOne();
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void increaseSessionByOne() {
        for (int i = VariableMethods.classes.length - 1; i > 0; i--) {
           databaseHelper.replaceValue(
                    COL_CLASS,
                    VariableMethods.classes[i - 1],
                    VariableMethods.classes[i]);
        }
        showSnackBar("Successfully Updated All Session");
    }

    public void chooseMethod(View view) {
        if (LogIn.DEFAULT_VIBRATION) {
            v.vibrate(LogIn.VIBRATION_TIME);
        }
        switch (view.getId()) {
            case R.id.btnBackUp_backup:
                backUpData();
                break;
            case R.id.btnRestore:
                restoreData();
                break;
            case R.id.btnIncreaseSession:
                increaseSession();
                break;
            default:
                break;
        }
    }
}
