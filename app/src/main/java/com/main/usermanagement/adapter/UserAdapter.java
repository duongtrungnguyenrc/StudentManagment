package com.main.usermanagement.adapter;//package com.main.usermanagement.adapter;//package com.main.usermanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.usermanagement.R;
import com.main.usermanagement.models.entities.UserProfile;
import com.main.usermanagement.ui.activities.UserListActivity;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserProfile> userProfiles = new ArrayList<>();

    public void setUserProfiles(List<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserProfile userProfile = userProfiles.get(position);
        holder.bind(userProfile);
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {


        private TextView txtUserName;
        private TextView txtPhone;
        private TextView txtStatus;
        private CheckBox cbChecked;
        private CheckBox checkBox;
        private TextView txtRole;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txt_name);
            checkBox = itemView.findViewById(R.id.cb_checked);
            txtPhone = itemView.findViewById(R.id.txt_phone);
            txtStatus = itemView.findViewById(R.id.txt_status);
            txtRole = itemView.findViewById(R.id.txt_role);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        UserProfile userProfile = userProfiles.get(position);
                        ((UserListActivity) itemView.getContext()).onUserItemClick(userProfile);
                    }
                }
            });
        }

        public void bind(UserProfile user) {
            txtUserName.setText(user.getName());
            txtPhone.setText(user.getPhone());
            txtRole.setText(user.getRole().name());
//            txtStatus.setText(user.getStatus().ordinal());

        }
    }
}