package com.thida.friendlocator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.service.autofill.Sanitizer;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thida.friendlocator.ui.User;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    LinearLayout layout;
    View emailView,nameView,passwordView,reenterPassView;
    EditText name,email,password,reenterPassword;
    Button btn_signup;
    CircleImageView image;
    String latitude,longitude,userId;
    ProgressBar progressBar;
    DatabaseReference firebaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        initialize();
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        firebaseReference = firebaseDatabase.getReference("users");

        firebaseAuth = FirebaseAuth.getInstance();

        btn_signup.setOnClickListener(view -> {
            if(!haveNetworkConnection()){
                Snackbar.make(layout,"Internet Connection is required.",Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                                startActivity(intent);
                            }
                        }).show();
            }
            else{
                if(!isNotEmpty(name.getText().toString())){
                    name.setError("Invalid Name!");
                }
                else if(!isValidEmail(email.getText().toString())){
                    email.setError("Invalid Email!");
                }
                else if(!isNotEmpty(password.getText().toString())){
                    password.setError("Invalid Password!");
                }
                else if (!password.getText().toString().equals(reenterPassword.getText().toString())){
                    Snackbar.make(layout,"Passwords are not match!",Snackbar.LENGTH_LONG).show();
                }
                else if(password.getText().toString().length()<6){
                    Snackbar.make(layout,"Passwords must be more than 6 characters!",Snackbar.LENGTH_LONG).show();
                }
                else{
                    disableView();
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(!task.isSuccessful()){
                                        Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        createUserinDB();
                                        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }

                                }
                            });


                }
            }
        });
        image.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            startActivityForResult(intent,1);
        });

    }

    private void initialize(){
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        nameView = findViewById(R.id.name_view);
        emailView = findViewById(R.id.email_view);
        passwordView = findViewById(R.id.password_view);
        reenterPassView = findViewById(R.id.reenter_pa_view);

        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        password = findViewById(R.id.password);
        reenterPassword = findViewById(R.id.reenter_password);
        btn_signup = findViewById(R.id.btn_signup);
        layout = findViewById(R.id.linearLayout);
        image = findViewById(R.id.profileImage);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isNotEmpty(String password){
        if(!TextUtils.isEmpty(password)){
            return true;
        }
        return false;
    }

    private void createUserinDB() {
        //get current location with GPS
        GetCurrentLocation location = new GetCurrentLocation(this);
        if (location.canGetLocation) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }

        //covert image to Base 64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitmapDrawable imagedrawable = (BitmapDrawable) image.getDrawable();
        Bitmap imagebitmap = imagedrawable.getBitmap();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        //imageString = "data:image/jpg;base64," + imageString;



        //create User
        User user = new User();
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setName(name.getText().toString());
        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());
        user.setImage(imageString);

        userId = firebaseReference.push().getKey();
        firebaseReference.child(userId).setValue(user);

       /* Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        },2000);*/
    }

    private void disableView(){
        name.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        reenterPassword.setVisibility(View.GONE);
        btn_signup.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        nameView.setVisibility(View.GONE);
        emailView.setVisibility(View.GONE);
        passwordView.setVisibility(View.GONE);
        reenterPassView.setVisibility(View.GONE);
    }

}
