/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by Anthony Ngure on 27/04/2017.
 * Email : anthonyngure25@gmail.com.
 * Company : Laysan Incorporation
 */

public class PhoneUtils {

    public static String normalizePhone(String phone) {
        if ((phone == null) || (phone.length() < 9)) {
            return "";
        }
        return "0" + phone.substring(phone.length() - 9);
    }

    public static boolean isValidPhoneInput(String phone) {
        return Patterns.PHONE.matcher(phone).matches()
                && TextUtils.isDigitsOnly(phone)
                && (phone.length() == 10);
    }
}
