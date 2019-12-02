package com.thida.friendlocator.ui.ChangePassword;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.thida.friendlocator.R;

public class ChangePassordFragment extends Fragment {


    public static ChangePassordFragment newInstance(String email,String password) {
        Bundle args = new Bundle();
        args.putString("email",email);
        args.putString("password",password);
        ChangePassordFragment fragment = new ChangePassordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_changepassword, container, false);

        String email = getArguments().getString("email");
        String password = getArguments().getString("password");
        ProgressDialog dialog = new ProgressDialog(getContext());

        EditText currentPassword = root.findViewById(R.id.current_password);
        EditText newPassword = root.findViewById(R.id.new_password);
        EditText rePassword = root.findViewById(R.id.reEnter_password);
        Button changePassword = root.findViewById(R.id.btn_change_password);
        changePassword.setOnClickListener(view -> {
            if(currentPassword.getText().toString().equals("")) currentPassword.setError("Invalid Password");
            else if(!currentPassword.getText().toString().equals(password)) currentPassword.setError("Old password is wrong.");
            else if(newPassword.getText().toString().equals("")) newPassword.setError("Invalid Password");
            else if(rePassword.getText().toString().equals("")) rePassword.setError("Invalid Password");
            else if(!newPassword.getText().toString().equals(rePassword.getText().toString())) Toast.makeText(getContext(),"Passwords are not equal.",Toast.LENGTH_LONG).show();
            else if(newPassword.getText().toString().length()<6) Toast.makeText(getContext(),"Passwords must be more than 6 characters.",Toast.LENGTH_LONG).show();
            else{
                dialog.show();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user =auth.getCurrentUser();
                String userEmail = user.getEmail();
                Log.e("user email in auth ",userEmail);


                AuthCredential credential = EmailAuthProvider.getCredential(userEmail,currentPassword.getText().toString());

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            user.updatePassword(newPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference ref = firebaseDatabase.getReference("users");
                                                Query query = ref.orderByChild("email").equalTo(email);
                                                query.addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                        dialog.dismiss();
                                                        dataSnapshot.getRef().child("password").setValue(newPassword.getText().toString());

                                                        /*Intent intent = new Intent(getContext(), LoginActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        getActivity().finish();*/
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

                                                Toast.makeText(getContext(),"Password Update Success",Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                dialog.dismiss();
                                                Toast.makeText(getContext(),"Something wrong",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            dialog.dismiss();
                            Toast.makeText(getContext(),"Authentication fail",Toast.LENGTH_LONG).show();
                        }
                    }
                });



            }


        });

        return root;

    }
}
