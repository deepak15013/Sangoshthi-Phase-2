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
    private String mShowName;
    private String mTopic;

    public RecordingItem(String mName, String mFilePath, String mShowName, String mTopic) {
        this.mName = mName;
        this.mFilePath = mFilePath;
        this.mShowName = mShowName;
        this.mTopic = mTopic;
    }

    public RecordingItem(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
        mShowName = in.readString();
        mTopic = in.readString();
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

    public String getShowName() {
        return mShowName;
    }

    public void setShowName(String mShowName) {
        this.mShowName = mShowName;
    }

    public String getTopic() {
        return mTopic;
    }

    public void setTopic(String mTopic) {
        this.mTopic = mTopic;
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
        dest.writeString(mShowName);
        dest.writeString(mTopic);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}