package com.example.android.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrailersCallResult implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private ArrayList<Trailer> trailers = new ArrayList<Trailer>();

    /**
     *
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The trailers
     */
    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    /**
     *
     * @param trailers
     *     The trailers
     */
    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }


    protected TrailersCallResult(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        if (in.readByte() == 0x01) {
            trailers = new ArrayList<Trailer>();
            in.readList(trailers, Trailer.class.getClassLoader());
        } else {
            trailers = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        if (trailers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(trailers);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TrailersCallResult> CREATOR = new Parcelable.Creator<TrailersCallResult>() {
        @Override
        public TrailersCallResult createFromParcel(Parcel in) {
            return new TrailersCallResult(in);
        }

        @Override
        public TrailersCallResult[] newArray(int size) {
            return new TrailersCallResult[size];
        }
    };
}