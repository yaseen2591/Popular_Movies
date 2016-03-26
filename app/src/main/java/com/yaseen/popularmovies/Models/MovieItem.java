package com.yaseen.popularmovies.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pasonet on 19-02-2016.
 */
public class MovieItem implements Parcelable {
    private String id;
    private String title;
    private String rating;
    private String overview;
    private String imageUrl;
    private String releaseDate;
    private String backdropImage;


    public MovieItem() {

    }

    public MovieItem(String title, String rating, String overview, String imageUrl) {
        this.title = title;
        this.rating = rating;
        this.overview = overview;
        this.imageUrl = imageUrl;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBackdropImage() {
        return backdropImage;
    }

    public void setBackdropImage(String backdropImage) {
        this.backdropImage = backdropImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    private MovieItem(Parcel in) {
        id=in.readString();
        title = in.readString();
        rating = in.readString();
        overview = in.readString();
        imageUrl = in.readString();
        releaseDate = in.readString();
        backdropImage = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(rating);
        dest.writeString(overview);
        dest.writeString(imageUrl);
        dest.writeString(releaseDate);
        dest.writeString(backdropImage);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}
