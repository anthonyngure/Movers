package ke.co.thinksynergy.movers.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Anthony Ngure on 01/08/2017.
 * Email : anthonyngure25@gmail.com.
 * http://www.toshngure.co.ke
 */

public class Status {

    @SerializedName("status_code")
    private int code;
    @SerializedName("status_text")
    private String text;

    public Status() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
