package com.thida.friendlocator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText email,password;
    Button btnLogin;
    View emailView,passwordView,layout;
    ProgressBar progressBar;
    String latitude,longitude,image,name;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 3*1000;
    AlertDialog dialog;
    AlertDialog dialog1;
    CheckConnection check;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        initialize();

        dialog = new AlertDialog.Builder(this).create();
        dialog1 = new AlertDialog.Builder(this).create();
        check = new CheckConnection(LoginActivity.this);

        auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(view -> {
           /* if(!haveNetworkConnection()){
                //Toast.makeText(getApplicationContext(),"Internet Connection is required.",Toast.LENGTH_LONG).show();
                Snackbar.make(layout,"Internet Connection is required.",Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                startActivity(intent);
                            }
                        }).show();
            }*/

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
               getLocation();
               if(!latitude.equals("0.0") || !longitude.equals("0.0")){
                   progressBar.setVisibility(View.VISIBLE);
                   email.setEnabled(false);
                   password.setEnabled(false);
                   btnLogin.setEnabled(false);
                   auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                           .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                               @Override
                               public void onComplete(@NonNull Task<AuthResult> task) {

                                   if (!task.isSuccessful()) {
                                       email.setEnabled(true);
                                       password.setEnabled(true);
                                       btnLogin.setEnabled(true);
                                       progressBar.setVisibility(View.GONE);
                                       Toast.makeText(LoginActivity.this, "Login Fail!\nCheck your email or password", Toast.LENGTH_LONG).show();
                                   } else {
                                       updateData();

                                   }
                               }
                           });

               }
               else {
                   email.setEnabled(true);
                   password.setEnabled(true);
                   btnLogin.setEnabled(true);
                   progressBar.setVisibility(View.GONE);
                   Toast.makeText(this, "Location data is not available now.\nPlease wait and LOGIN again.", Toast.LENGTH_LONG).show();
               }
        }}

        );

    }

    private void initialize(){
        layout = findViewById(R.id.layout);
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
        else {
            new AlertDialog.Builder(this)
                    .setMessage("GPS is disable in your device")
                    .setPositiveButton("Go to GPS Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
        }

    }

    @Override
    protected void onResume() {

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                boolean checkConnection = check.checkNetworkConnection();
                if(!checkConnection){
                    if(!dialog.isShowing()) {
                        Log.e("Dialog ", "is showing");
                        dialog.setMessage("Internet Connection is not available.");
                        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Go to Internet Setting", (dialogInterface, i) -> {
                            dialog.dismiss();
                            startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));

                        });
                        dialog.show();
                    }
                }

                Boolean checkGPS = check.checkGPS();
                if(!checkGPS){
                    if(!dialog1.isShowing()) {
                        Log.e("Dialog1 ", "is showing");
                        dialog1.setMessage("GPS is disabled in your device.");
                        dialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Go to GPS Setting", (dialogInterface, i) -> {
                            dialog1.dismiss();
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        });
                        dialog1.show();
                    }
                }

                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause(){
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

}
