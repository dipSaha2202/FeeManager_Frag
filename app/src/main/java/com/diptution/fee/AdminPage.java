package com.diptution.fee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class AdminPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static boolean PermissionForSMS = false;
    protected static boolean PermissionForPhoneState = false;
    private DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
/*
        int smsPermission = ContextCompat.checkSelfPermission(AdminPage.this, Manifest.permission.SEND_SMS);
        int statusPermission = ContextCompat.checkSelfPermission(AdminPage.this, Manifest.permission.READ_PHONE_STATE);

        if (smsPermission != PackageManager.PERMISSION_GRANTED ||
                statusPermission != PackageManager.PERMISSION_GRANTED ) {
            PermissionForSMS = false;
            PermissionForPhoneState = false;

            ActivityCompat.requestPermissions(
                    AdminPage.this,
                    new String[] {Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_PHONE_STATE}, 22);
        } else {
            PermissionForSMS = true;
            PermissionForPhoneState = true;
        }
*/
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(AdminPage.this,
                drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 22){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                PermissionForSMS = true;
            }
            if (grantResults.length > 1){
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    PermissionForPhoneState = true;
                }
            }
        }
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut_menu) {
            startActivity(new Intent(AdminPage.this, LogIn.class));
            finish();
        } else if (item.getItemId() == R.id.settingsMenu_admin) {
            startActivity(new Intent(AdminPage.this, Settings.class));
        }else {
           return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences  =
                PreferenceManager.getDefaultSharedPreferences(AdminPage.this);
          LogIn.DEFAULT_VIBRATION = preferences.getBoolean(getString(R.string.vibration_key), true);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_update:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UpdateFee()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_addStudent:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddStudent()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_backUp:
                startActivity(new Intent(AdminPage.this, BackUp.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_viewRecords:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ViewRecords()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_allStudents:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new StudentList()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_unpaidFee:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new UnpaidFees()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

       void showSnackBar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        .show();
    }

     void showToast(String message) {
        Toast.makeText(AdminPage.this, message, Toast.LENGTH_SHORT).show();
    }
}
