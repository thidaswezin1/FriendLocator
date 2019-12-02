package com.thida.friendlocator.ui.ChangePhoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thida.friendlocator.R;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ChangePhotoFragment extends Fragment {
    private ImageView image,imageUnuse;
     public static ChangePhotoFragment newInstance(String email) {

         Bundle args = new Bundle();
         args.putString("email",email);

         ChangePhotoFragment fragment = new ChangePhotoFragment();
         fragment.setArguments(args);
         return fragment;
     }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_change_photo, container, false);

        String email = getArguments().getString("email");

        Button changePhoto = root.findViewById(R.id.change_photo);
        Button browsePhoto = root.findViewById(R.id.browse);
        image = root.findViewById(R.id.user_image);
        imageUnuse = root.findViewById(R.id.browse_image);

        browsePhoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
            startActivityForResult(intent,1);
        });
        changePhoto.setOnClickListener(view -> {
           if(image.getDrawable()==null) Toast.makeText(getContext(),"Please choose photo.",Toast.LENGTH_LONG).show();
           else {
               //covert image to Base 64
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
               Bitmap bitmap = drawable.getBitmap();
               bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
               byte[] imageBytes = baos.toByteArray();
               String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);


               FirebaseDatabase database = FirebaseDatabase.getInstance();
               DatabaseReference reference = database.getReference("users");
               Query query = reference.orderByChild("email").equalTo(email);
               query.addChildEventListener(new ChildEventListener() {
                   @Override
                   public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       dataSnapshot.getRef().child("image").setValue(imageString);
                       Toast.makeText(getContext(),"Your Photo is updated",Toast.LENGTH_LONG).show();
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


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == getActivity().RESULT_OK){
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);
            imageUnuse.setVisibility(View.GONE);
        }
    }
}
