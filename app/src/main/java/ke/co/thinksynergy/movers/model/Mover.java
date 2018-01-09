package ke.co.thinksynergy.movers.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Anthony Ngure on 06/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

@Entity(nameInDb = "movers")
public class Mover implements Parcelable {

    public static final String TRUCK = "MECHANIC";
    public static final String PICK_UP = "TOWING";
    public static final String MOTOR_BIKE = "CAR_WASH";
    public static final String PERSON = "PERSON";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TRUCK, PICK_UP, MOTOR_BIKE, PERSON})
    public @interface MoverType {}

    private double latitude;
    private double longitude;
    private double distance;
    private long id;
    private String name;
    private String phone;
    private String email;

    @SerializedName("service")
    private String type;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.distance);
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.type);
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Mover() {
    }

    protected Mover(Parcel in) {
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.distance = in.readDouble();
        this.id = in.readLong();
        this.name = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.type = in.readString();
    }

    @Generated(hash = 1812988484)
    public Mover(double latitude, double longitude, double distance, long id, String name,
            String phone, String email, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.type = type;
    }


    public static final Parcelable.Creator<Mover> CREATOR = new Parcelable.Creator<Mover>() {
        @Override
        public Mover createFromParcel(Parcel source) {
            return new Mover(source);
        }

        @Override
        public Mover[] newArray(int size) {
            return new Mover[size];
        }
    };
}
