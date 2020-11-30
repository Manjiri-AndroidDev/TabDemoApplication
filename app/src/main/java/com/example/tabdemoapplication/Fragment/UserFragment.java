package com.example.tabdemoapplication.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tabdemoapplication.Adapter.UserDetailAdapter;
import com.example.tabdemoapplication.Constants;
import com.example.tabdemoapplication.Model.UploadModel;
import com.example.tabdemoapplication.R;
import com.example.tabdemoapplication.ui.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    RecyclerView recyclerView;
    UserDetailAdapter userDetailAdapter;
    //database reference
    private DatabaseReference mDatabase;
    //progress dialog
    private ProgressDialog progressDialog;
    //list to hold all the uploaded images
    private List<UploadModel> uploadModelList = new ArrayList<>();
    public FirebaseStorage mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        progressDialog = new ProgressDialog(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userDetailAdapter = new UserDetailAdapter(uploadModelList);
        recyclerView.setAdapter(userDetailAdapter);
        mStorage = FirebaseStorage.getInstance();
        fetchData();

        return view;
    }

    //retrieve data from storage
    public void fetchData() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                uploadModelList.clear();
                progressDialog.dismiss();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    UploadModel uploadModel = postSnapshot.getValue(UploadModel.class);
                    uploadModelList.add(uploadModel);
                }
                    userDetailAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
}