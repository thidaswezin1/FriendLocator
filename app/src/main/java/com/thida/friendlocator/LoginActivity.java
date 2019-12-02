package com.thida.friendlocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thida.friendlocator.ui.User;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button btnLogin;
    View emailView,passwordView;
    ProgressBar progressBar;
    String latitude,longitude,image,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initialize();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(view -> {
           /* FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
            DatabaseReference ref =firebaseDatabase.getReference("users");

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user=(User) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/
           if(TextUtils.isEmpty(email.getText().toString())){
               email.setError("Invalid Email!");
           }
           else if (TextUtils.isEmpty(password.getText().toString())){
               password.setError("Invalid Password!");
           }
           else {
               progressBar.setVisibility(View.VISIBLE);
               auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                       .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {

                               if (!task.isSuccessful()) {
                                   progressBar.setVisibility(View.GONE);
                                   Toast.makeText(LoginActivity.this, "Login Fail!\nCheck your email or password", Toast.LENGTH_LONG).show();
                               } else {
                                   updateData();

                               }
                           }
                       });

           }

        });
    }

    private void initialize(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        emailView = findViewById(R.id.email_view);
        passwordView = findViewById(R.id.password_view);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void updateData(){
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference ref =firebaseDatabase.getReference("users");
        Query query = ref.orderByChild("email").equalTo(email.getText().toString());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                progressBar.setVisibility(View.GONE);
               getLocation();
               dataSnapshot.getRef().child("latitude").setValue(latitude);
               dataSnapshot.getRef().child("longitude").setValue(longitude);
               name = String.valueOf(dataSnapshot.child("name").getValue());
              image = String.valueOf(dataSnapshot.child("image").getValue());

              byte[] decodedString = Base64.decode(image, Base64.DEFAULT);


               Log.e("location ",latitude+":"+longitude);
               Log.e("Name ",name);
               Intent intent = new Intent(LoginActivity.this, MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

               intent.putExtra("UserName",name);
               intent.putExtra("Email",email.getText().toString());
               intent.putExtra("Password",password.getText().toString());
               intent.putExtra("Bitmap",decodedString);

               startActivity(intent);
               finish();

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
    }

    private void getLocation(){
        GetCurrentLocation location = new GetCurrentLocation(this);
        if(location.canGetLocation) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }
    }

}
