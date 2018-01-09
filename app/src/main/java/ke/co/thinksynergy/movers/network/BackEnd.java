package ke.co.thinksynergy.movers.network;

/**
 * Created by Anthony Ngure on 04/06/2017.
 * Email : anthonyngure25@gmail.com.
 * http://www.toshngure.co.ke
 */

public class BackEnd {

    public static final String BASE_URL = "http://arrant.jkings.co.ke/api/v1";

    public static final class EndPoints {
        public static final String SERVICES = "/services";
        public static final String DEVICE_TOKEN = "/deviceToken";
        public static final String SIGN_IN = "/auth/signIn";
    }

    public static final class Params {
        public static final String CODE = "code";
        public static final String PHONE = "phone";
        public static final String EMAIL = "email";
        public static final String SIGN_IN_ID = "signInId";
        public static final String PASSWORD = "password";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String TYPE = "type";
    }

    public static final class Response {
        public static final String DATA = "data";
        public static final String META = "meta";
    }
}
