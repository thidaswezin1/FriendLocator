package com.thida.friendlocator.ui.home;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.thida.friendlocator.R;
import com.thida.friendlocator.SliderAdapter;
import com.thida.friendlocator.SliderViewItem;
import com.thida.friendlocator.ui.User;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    List<SliderViewItem> allItems = new ArrayList<>();

    public static HomeFragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString("email",email);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        String email = getArguments().getString("email");

        /*SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
           // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

            // Add a marker in yangon and move the camera
            LatLng yangon = new LatLng(16.789488, 96.169069);
           // mMap.addMarker(new MarkerOptions().position(yangon).title("Marker in Yangon"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(yangon));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        });*/

        SliderView sliderView = root.findViewById(R.id.imageSlider);
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference ref =firebaseDatabase.getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                sliderView.setIndicatorSelectedColor(Color.BLUE);
                sliderView.setIndicatorUnselectedColor(Color.CYAN);
                sliderView.setScrollTimeInSec(3);
                sliderView.startAutoCycle();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return root;
    }

}