package com.main.usermanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.usermanagement.R;
import com.main.usermanagement.models.entities.UserProfile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserProfile> userProfiles = new ArrayList<>();
    private Set<UserProfile> selectedItems = new HashSet<>();
    private OnItemSelectedListener setOnItemSelectedListener;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserProfile userProfile = userProfiles.get(position);
        holder.bind(userProfile);
        holder.checkBox.setChecked(selectedItems.contains(userProfile));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.contains(userProfile)) {
                    selectedItems.remove(userProfile);
                } else {
                    selectedItems.add(userProfile);
                }
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public void setUserProfiles(List<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
        notifyDataSetChanged();
    }

    public void removeUser(UserProfile userProfile) {
        userProfiles.remove(userProfile);
        notifyDataSetChanged();
    }

    public List<UserProfile> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.setOnItemSelectedListener = onItemSelectedListener;
        notifyDataSetChanged();
    }

    public interface OnItemSelectedListener {
        void onItemSelected(boolean hasSelectedItems);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtPhone;
        private TextView txtStatus;
        private CheckBox checkBox;
        private TextView txtRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txt_name);
            checkBox = itemView.findViewById(R.id.cb_checked);
            txtPhone = itemView.findViewById(R.id.txt_phone);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtRole = itemView.findViewById(R.id.txt_role);
        }

        public void bind(UserProfile user) {
            txtUserName.setText(user.getName());
            txtPhone.setText(user.getPhone());
            txtRole.setText(user.getRole().name());

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(selectedItems.contains(user));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedItems.add(user);
                } else {
                    selectedItems.remove(user);
                }
                if (setOnItemSelectedListener != null) {
                    setOnItemSelectedListener.onItemSelected(!selectedItems.isEmpty());
                }
            });
        }
    }
}