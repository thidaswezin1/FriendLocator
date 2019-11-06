package com.thida.friendlocator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseLoginSignup extends AppCompatActivity {
    Button btnLogin,btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup);
        initialize();
    }

    private void initialize(){
        btnLogin = findViewById(R.id.login);
        btnSignUp = findViewById(R.id.signup);
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginSignup.this,LoginActivity.class);
            startActivity(intent);
        });
        btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginSignup.this,SignUpActivity.class);
            startActivity(intent);
        });
    }
}
