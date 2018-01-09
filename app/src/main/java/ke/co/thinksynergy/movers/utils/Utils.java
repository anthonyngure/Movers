package ke.co.thinksynergy.movers.utils;

import java.util.Locale;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.model.Mover;

/**
 * Created by Anthony Ngure on 08/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class Utils {

    public static String toDistanceString(double distance) {
        String distanceString;
        if (distance < 0.5) {
            distanceString = String.format(Locale.ENGLISH, "%.2f", distance * 1000) + " m";
        } else {
            distanceString = String.format(Locale.ENGLISH, "%.2f", distance) + " km";
        }

        return distanceString;
    }

    public static int getMoverDrawable(Mover mover){
        int drawable;
        switch (mover.getType()) {
            case Mover.TRUCK:
                drawable = R.drawable.icon_car;
                break;
            case Mover.PICK_UP:
                drawable = R.drawable.icon_car;
                break;
            case Mover.MOTOR_BIKE:
                drawable = R.drawable.icon_car;
                break;
            default:
                drawable = R.drawable.icon_car;

        }
        return drawable;
    }
}
