package com.example.tabdemoapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tabdemoapplication.Model.UploadModel;
import com.example.tabdemoapplication.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserDetailAdapter extends RecyclerView.Adapter<UserDetailAdapter.ViewHolder> {
    private List<UploadModel> uploadModelList;
    Context context;

    public UserDetailAdapter(List<UploadModel> uploadModelList){
        this.uploadModelList=uploadModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_user_detail, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserDetailAdapter.ViewHolder holder, int position) {
        UploadModel uploadModel = uploadModelList.get(position);
        holder.txtName.setText(uploadModel.getStrName());
        holder.txtDob.setText(uploadModel.getStrDob());
        holder.txtCountry.setText(uploadModel.getStrCountry());
        holder.txtPhone.setText(uploadModel.getStrPhone());

       // Glide.with(holder.itemView.getContext()).load(uploadModel.getStrImgurl()).into(holder.imgProfile);

        Picasso.get()
                .load(uploadModel.getStrImgurl())
                .placeholder(R.drawable.user)
                .fit()
                .centerCrop()
                .into(holder.imgProfile);
    }

    @Override
    public int getItemCount() {
        return uploadModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView txtName,txtPhone,txtDob,txtCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile=(ImageView)itemView.findViewById(R.id.userImg);
            txtName=(TextView)itemView.findViewById(R.id.user_name);
            txtPhone=(TextView)itemView.findViewById(R.id.user_phoneno);
            txtDob=(TextView)itemView.findViewById(R.id.user_birthdate);
            txtCountry=(TextView)itemView.findViewById(R.id.user_country);
        }
    }
}
