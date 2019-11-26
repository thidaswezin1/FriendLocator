package com.thida.friendlocator.ui.ChangePassword;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thida.friendlocator.R;

public class ChangePassordFragment extends Fragment {


    public static ChangePassordFragment newInstance() {
        return new ChangePassordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_changepassword, container, false);
    }


}
