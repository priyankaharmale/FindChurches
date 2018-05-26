package hnweb.com.findchurches.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hnwebmarketing on 6/6/2017.
 */
public class ValidationMethods {

    // validating email id
    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    public boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 0) {    //if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    // validating Name
    public boolean isValidName(String name) {
        if (name != null && name.matches("[a-zA-Z ]+")) {
            return true;
        }
        return false;
    }

    // validating Number
    public boolean isValidNumber(String number) {
        if (number != null && number.matches("[0-9]+")) {
            return true;
        }
        return false;
    }

    // validating Location = Address
    public boolean isValidLocation(String location) {
        if (location != null && location.matches("[a-zA-Z0-9~@#$%^&*:;<>.,/}{+ ]+")) {
            return true;
        }
        return false;
    }

    // validating Username
    public boolean isValidUserName(String username) {
        if (username != null && username.matches("[a-zA-Z0-9]+")) {
            return true;
        }
        return false;
    }

    // validating Password and Confirm Password
    public boolean isPasswordMatching(String password, String confirmPassword) {
        Pattern pattern = Pattern.compile(password, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(confirmPassword);

        if (!matcher.matches()) {
            // do your Toast("passwords are not matching");
            return false;
        }
        return true;
    }

    // validating Mobile no
    public boolean isValidMobileNo(String mbno) {
        if (mbno != null && mbno.length() == 10) {
            return true;
        }
        return false;
        //Pattern pattern0 = Pattern.compile("\\d{10}");
        //Matcher matchr = pattern0.matcher(mbno);
        //return matchr.matches();
    }

    // validating Mobile no
    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target.length()!=10) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

}
