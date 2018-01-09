package ke.co.thinksynergy.movers.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Anthony Ngure on 11/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class User implements Parcelable {


    private String token;
    private int id;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;

    public String getToken() {
        return token;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.token = in.readString();
        this.id = in.readInt();
        this.name = in.readString();
        this.email = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
