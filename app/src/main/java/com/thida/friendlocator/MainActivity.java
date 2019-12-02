package com.thida.friendlocator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Base64;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.thida.friendlocator.ui.ChangePassword.ChangePassordFragment;
import com.thida.friendlocator.ui.FindFriend.FindFriendFragment;
import com.thida.friendlocator.ui.User;
import com.thida.friendlocator.ui.home.HomeFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

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
    private CardView cardView;
    private ProgressDialog dialog;
    private TextView textViewFriend;

    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        textViewFriend = findViewById(R.id.textFriend);
        carryData();

        SliderView sliderView = findViewById(R.id.imageSlider);
        cardView = findViewById(R.id.cardView);

        dialog = new ProgressDialog(this);
        dialog.show();

        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference ref =firebaseDatabase.getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    User user = userDataSnapshot.getValue(User.class);
                    if(!user.getEmail().equals(email)) {
                        SliderViewItem item = new SliderViewItem();

                        byte[] decodedString = Base64.decode(user.getImage(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        item.setImage(bitmap);
                        item.setUserName(user.getName());

                        allItems.add(item);
                    }
                }
                SliderAdapter adapter = new SliderAdapter(allItems);
                sliderView.setSliderAdapter(adapter);

                /*sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                sliderView.setIndicatorSelectedColor(Color.WHITE);
                sliderView.setIndicatorUnselectedColor(Color.GRAY);
                sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
                sliderView.startAutoCycle();*/
                sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                sliderView.setIndicatorSelectedColor(Color.BLUE);
                sliderView.setIndicatorUnselectedColor(Color.RED);
                sliderView.setScrollTimeInSec(4);
                sliderView.startAutoCycle();

               /* sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
                    @Override
                    public void onIndicatorClicked(int position) {
                        sliderView.setCurrentPagePosition(position);
                    }
                });*/



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

                // Passing each menu ID as a set of Ids because each
                // menu should be considered as top level destinations.

                View headerView = nvDrawer.getHeaderView(0);
                userImage = headerView.findViewById(R.id.imageView);
                userName = headerView.findViewById(R.id.user_name);
                userName.setText(name);

                Bitmap decodedByte = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
                userImage.setImageBitmap(decodedByte);

                setUpDrawerContent(nvDrawer);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






       /* mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_change_password)
                .setDrawerLayout(drawer)
                .build();*/
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/



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
                    textViewFriend.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                    fragment = ChangePassordFragment.newInstance(email,password);
                    break;
                case R.id.find_friends:
                    textViewFriend.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                    fragment =  FindFriendFragment.newInstance(email);
                    break;
                default:
                    textViewFriend.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
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
