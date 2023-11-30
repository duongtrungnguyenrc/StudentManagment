package com.main.usermanagement.models.entities;//package com.main.usermanagement.models.entities;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.main.usermanagement.models.enumerations.ERole;
import com.main.usermanagement.models.enumerations.EStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProfile implements Parcelable {
    private String id;
    private String name;
    private int age;
    private String phone;
    private EStatus status;
    private ERole role;
    private String image;
    private boolean isSelected;
    private List<Date> accessHistory = new ArrayList<>();

    public UserProfile() {}

    public UserProfile(String name, int age, String phone, EStatus status, ERole role, String image) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.status = status;
        this.role = role;
        this.image = image;
    }

    protected UserProfile(Parcel in) {
        name = in.readString();
        age = in.readInt();
        phone = in.readString();
        status = EStatus.valueOf(in.readString());
        role = ERole.valueOf(in.readString());
        image = in.readString();
        accessHistory = new ArrayList<>();
        in.readList(accessHistory, Date.class.getClassLoader());
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeString(phone);
        dest.writeString(status.name());
        dest.writeString(role.name());
        dest.writeString(image);
        dest.writeList(accessHistory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public ERole getRole() {
        return role;
    }

    public void setRole(ERole role) {
        this.role = role;
    }

    public void setAccessHistory(List<Date> accessHistory) {
        this.accessHistory = accessHistory;
    }

    public List<Date> getAccessHistory() {
        return accessHistory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}