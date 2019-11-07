package com.thida.friendlocator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ChooseLoginSignup extends AppCompatActivity {
    Button btnLogin,btnSignUp;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);
        initialize();
        boolean locationPermission = checkLocationPermission();
        if(locationPermission){
            initialize();
        }
        else{
            //do something
            Snackbar.make(layout,"GPS Permission is required!!!",Snackbar.LENGTH_LONG).show();
        }
    }

    private void initialize(){
        btnLogin = findViewById(R.id.login);
        btnSignUp = findViewById(R.id.signup);
        layout = findViewById(R.id.linearLayout);
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginSignup.this,LoginActivity.class);
            startActivity(intent);
        });
        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginSignup.this,SignUpActivity.class);
            startActivity(intent);
        });
    }

    public boolean checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    boolean b = checkLocationPermission();

                }
                break;
        }
    }
}
