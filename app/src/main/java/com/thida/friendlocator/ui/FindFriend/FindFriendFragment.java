package com.thida.friendlocator.ui.FindFriend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thida.friendlocator.R;
import com.thida.friendlocator.ui.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FindFriendFragment extends Fragment {
     private GoogleMap mMap;

     public static FindFriendFragment newInstance(String email) {

         Bundle args = new Bundle();
         args.putString("email",email);

         FindFriendFragment fragment = new FindFriendFragment();
         fragment.setArguments(args);
         return fragment;
     }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_find_friend, container, false);

        String email = getArguments().getString("email");
        Log.e("Carry Email ",email);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            // mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);

            // Add a marker in yangon and move the camera
           // LatLng yangon = new LatLng(16.789488, 96.169069);
            //mMap.addMarker(new MarkerOptions().position(yangon).title("Marker in Yangon"));
           //
        });
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference ref =firebaseDatabase.getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("data from db ",dataSnapshot.getChildrenCount()+"");
                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    User user = userDataSnapshot.getValue(User.class);
                    Log.e("User email ",user.getEmail());

                    if(user.getEmail().equals(email)){

                        LatLng location = new LatLng(Double.parseDouble(user.getLatitude()),Double.parseDouble(user.getLongitude()));
                        mMap.addMarker(new MarkerOptions().position(location).title("Me"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    }
                    else {

                        LatLng location = new LatLng(Double.parseDouble(user.getLatitude()), Double.parseDouble(user.getLongitude()));
                        mMap.addMarker(new MarkerOptions().position(location).title(user.getName() + " is here!").icon(BitmapDescriptorFactory.fromResource(R.drawable.person)));

                    }
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        return root;
    }
}
