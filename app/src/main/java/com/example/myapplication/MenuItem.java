package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable {
    public String name;
    public double price;
    public String description;

    public MenuItem(String name, double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    protected MenuItem(Parcel in) {
        name = in.readString();
        price = in.readDouble();
        description = in.readString();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeString(description);
    }
}
