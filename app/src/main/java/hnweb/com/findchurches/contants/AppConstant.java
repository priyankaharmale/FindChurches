package hnweb.com.findchurches.contants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppConstant {


    public static final String KEY_PASSWORD = "pwd";
    public static final String KEY_CONFRIMPASSWORD = "pwd";
    /*============================================Register==================================================*/
    public static final String KEY_PHONE = "mobno";

    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";

    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";

    public static final String USER_ID = "reg_id";
    public static final String CHURCH_ID = "cid";
    public static final String EVENT_ID = "eid";
    public static final String FAV_STATUS = "fav_status";


    public static final String BASE_URL = "http://tech599.com/tech599.com/johnaks/seed_harvest_giving/api/";

    /*=================================================Login=========================================================*/
    public static final String API_REGISTER = BASE_URL + "registration.php";
    public static final String API_LOGIN = BASE_URL + "login.php";
    public static final String API_FORGOTPWD = BASE_URL + "forgot_password.php";
    public static final String API_CHANGE_PASSWORD=BASE_URL + "change_password.php";

    /*=================================================GET Map Data=========================================================*/
    public static final String API_GETLIST_CHURCH_NEARBY = BASE_URL + "find_nearby_church.php";
    public static final String API_GETLIST_ALL_CHURCH= BASE_URL + "get_all_church.php";

        /*=================================================GET Church Detail Data=========================================================*/

    public static final String API_CHURCH_DETAILS = BASE_URL + "get_church_detail.php";
    public static final String API_CHURCH_FAVSTATUS = BASE_URL + "get_church_detail.php";

    /*=================================================GET Event Data=========================================================*/
    public static final String API_GET_EVENT = BASE_URL + "get_events_dates.php";
    public static final String API_GET_EVENTDETAIL = BASE_URL + "get_event_detail.php";

    /*=================================================Favorite=========================================================*/
    public static final String API_ADD_FAV = BASE_URL + "mark_as_favorite_church.php";
    public static final String API_FAV_LIST = BASE_URL + "get_my_favourite_church.php";

    /*=================================================Payment=========================================================*/

    public static final String API_STRIPE_PAYMENT = BASE_URL + "payment.php";

    /*=================================================Profile=========================================================*/
    public static final String API_PROFILE_DETAILS = BASE_URL + "get_profile.php";
    public static final String API_PROFILE_EDIT = BASE_URL + "edit_profile.php";
    /*=================================================Histoty=========================================================*/
    public static final String API_PAYMENT_HOSTORY = BASE_URL + "donation_history_against_user.php";

    /*=================================================Charities List=========================================================*/
    public static final String API_CHARITY_LIST = BASE_URL + "charity.php";


    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


}
