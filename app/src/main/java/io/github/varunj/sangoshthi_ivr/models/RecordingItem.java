/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 12/30/2014.
 */
public class RecordingItem implements Parcelable {
    private String mName; // file name
    private String mFilePath; //file path

    public RecordingItem(String mName, String mFilePath) {
        this.mName = mName;
        this.mFilePath = mFilePath;
    }

    public RecordingItem(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public static final Parcelable.Creator<RecordingItem> CREATOR = new Parcelable.Creator<RecordingItem>() {
        public RecordingItem createFromParcel(Parcel in) {
            return new RecordingItem(in);
        }

        public RecordingItem[] newArray(int size) {
            return new RecordingItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}