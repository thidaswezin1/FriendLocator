package com.thida.friendlocator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thida.friendlocator.ui.ChangePassword.ChangePassordFragment;
import com.thida.friendlocator.ui.FindFriend.FindFriendFragment;
import com.thida.friendlocator.ui.home.HomeFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView userImage;
    TextView userName;
    String name,email,password;
    byte[] bitmap;

    private AppBarConfiguration mAppBarConfiguration;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        carryData();

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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        View headerView = nvDrawer.getHeaderView(0);
        userImage = headerView.findViewById(R.id.imageView);
        userName = headerView.findViewById(R.id.user_name);
        userName.setText(name);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        userImage.setImageBitmap(decodedByte);



       /* mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_change_password)
                .setDrawerLayout(drawer)
                .build();*/
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

        setUpDrawerContent(nvDrawer);

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
        Class fragmentClass;
        try {
            switch (menuItem.getItemId()) {
                case R.id.pwd_change:
                    fragmentClass = ChangePassordFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();
                    break;
                case R.id.find_friends:
                    fragment =  FindFriendFragment.newInstance(email);
                    break;
                default:
                    fragmentClass = HomeFragment.class;
                    fragment = (Fragment) fragmentClass.newInstance();

            }
        }
       catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
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
