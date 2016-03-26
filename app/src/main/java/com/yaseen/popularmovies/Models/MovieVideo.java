package com.yaseen.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pasonet on 26-03-2016.
 */
public class MovieVideo implements Parcelable {
private String key,name,site;

    public MovieVideo(){

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
    }

    protected MovieVideo(Parcel in) {
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
    }

    public static final Parcelable.Creator<MovieVideo> CREATOR = new Parcelable.Creator<MovieVideo>() {
        public MovieVideo createFromParcel(Parcel source) {
            return new MovieVideo(source);
        }

        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };
}
