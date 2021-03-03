package com.example.gbous2065.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserDocFragment implements Parcelable {
    private int id;
    private String title;
    private String dateEnd;
    private String status;
    private String dateStatus;
    private String fileUrl;
    private String subLink;
    private String unsubLink;

    public UserDocFragment(){}

    protected UserDocFragment(Parcel in) {
        id = in.readInt();
        title = in.readString();
        dateEnd = in.readString();
        status = in.readString();
        dateStatus = in.readString();
        fileUrl = in.readString();
        subLink = in.readString();
        unsubLink = in.readString();
    }

    public static final Creator<UserDocFragment> CREATOR = new Creator<UserDocFragment>() {
        @Override
        public UserDocFragment createFromParcel(Parcel in) {
            return new UserDocFragment(in);
        }

        @Override
        public UserDocFragment[] newArray(int size) {
            return new UserDocFragment[size];
        }
    };

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getSubLink() {
        return subLink;
    }

    public void setSubLink(String subLink) {
        this.subLink = subLink;
    }

    public String getUnsubLink() {
        return unsubLink;
    }

    public void setUnsubLink(String unsubLink) {
        this.unsubLink = unsubLink;
    }

    public int getId() {
        return id;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(String dateStatus) {
        this.dateStatus = dateStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(dateEnd);
        dest.writeString(status);
        dest.writeString(dateStatus);
        dest.writeString(fileUrl);
        dest.writeString(subLink);
        dest.writeString(unsubLink);
    }
}
