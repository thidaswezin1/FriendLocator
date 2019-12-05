package com.thida.friendlocator;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thida.friendlocator.ui.ChangePassword.ChangePassordFragment;
import com.thida.friendlocator.ui.ChangePhoto.ChangePhotoFragment;
import com.thida.friendlocator.ui.FindFriend.FindFriendFragment;
import com.thida.friendlocator.ui.home.HomeFragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName;
    String name,email,password;
    byte[] bitmap;
    List<SliderViewItem> allItems = new ArrayList<>();

    //private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private SwipeRefreshLayout refreshLayout;
    Bitmap decodedByte;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        refreshLayout = findViewById(R.id.swipeToRefresh);

        carryData();


        /*for navigation drawer*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nav_view);

        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer.getMenu().getItem(0).setChecked(true);


        View headerView = nvDrawer.getHeaderView(0);
        userImage = headerView.findViewById(R.id.imageView);
        userName = headerView.findViewById(R.id.user_name);
        userName.setText(name);

        decodedByte = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        userImage.setImageBitmap(decodedByte);

        setUpDrawerContent(nvDrawer);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        refreshLayout.setRefreshing(false);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("users");
                        Query query = reference.orderByChild("email").equalTo(email);
                        query.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String image = String.valueOf(dataSnapshot.child("image").getValue());
                                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                userImage.setImageBitmap(decodedByte);
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
                });
                loadFragment(HomeFragment.newInstance(email));
                setTitle("Home");
    }


       /* mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_change_password)
                .setDrawerLayout(drawer)
                .build();*/
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, fragment);
        transaction.commit();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener((menuItem)->{
            selectDrawerItem(menuItem);
            return true;
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment=null;
        try {
            switch (menuItem.getItemId()) {
                case R.id.home:
                    fragment = HomeFragment.newInstance(email);
                    break;
                case R.id.pwd_change:
                    fragment = ChangePassordFragment.newInstance(email,password);
                    break;
                case R.id.find_friends:
                    CheckConnection connection = new CheckConnection(MainActivity.this);
                    boolean checkGPS = connection.checkGPS();
                    if(checkGPS) fragment =  FindFriendFragment.newInstance(email);
                    else Toast.makeText(getApplicationContext(),"GPS is disabled in your device.",Toast.LENGTH_LONG).show();
                    break;
                case R.id.photo_change:
                    fragment = ChangePhotoFragment.newInstance(email);
                    break;
                case R.id.logout:
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
        }
       catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null) {
            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            mDrawer.closeDrawers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

   /* @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }*/

    private void carryData(){
        Intent intent = getIntent();
        name = intent.getStringExtra("UserName");
        email = intent.getStringExtra("Email");
        password = intent.getStringExtra("Password");
        bitmap = intent.getByteArrayExtra("Bitmap");

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit from this app?")
                .setPositiveButton("Ok", ((dialogInterface,i) ->{
                    finish();
                }))
                .setNegativeButton("Cancel",((dialogInterface, i) -> {

                })).show();


    }
}
