package com.andry.videoplayer;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoDetails implements Parcelable {
    private String path;
    private String thumbPath;

    public VideoDetails(String path, String thumbPath) {
        this.path = path;
        this.thumbPath = thumbPath;
    }

    public VideoDetails(Parcel in) {
        this.path = in.readString();
        this.thumbPath = in.readString();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(thumbPath);
    }

    public static final Creator<VideoDetails> CREATOR = new Creator<VideoDetails>() {

        @Override
        public VideoDetails createFromParcel(Parcel source) {
            return new VideoDetails(source);
        }

        @Override
        public VideoDetails[] newArray(int size) {
            return new VideoDetails[size];
        }
    };
}
