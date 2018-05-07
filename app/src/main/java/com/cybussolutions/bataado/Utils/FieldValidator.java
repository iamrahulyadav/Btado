package com.cybussolutions.bataado.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FieldValidator {

    public static boolean lengthValidator(Activity context, String input,
                                          int minLength, int maxLength, String label) {
        String error = "";
        if (input.isEmpty()) {
            error = "Please enter " + label;
        } else if (input.length() < minLength) {
            error = label + " cannot be less than " + minLength + " characters";
        } else if (input.length() > maxLength) {
            error = label + " cannot be more than " + maxLength + " characters";
        }

        if (error.equalsIgnoreCase("")) {
            return true;
        } else {
            new DialogBox(context, error, "Error", "Alert");
            return false;
        }
    }

    public static boolean ValidateEmail(Activity context, String input) {
        boolean b=false;
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(input.contains(",")){
            String[] inputs=input.split(",");
            for(int i=0;i<inputs.length;i++){
                if(inputs[i].matches(EMAIL_REGEX)){
                   b=true;
                }else {
                    b=false;
                    break;
                }
            }
        }else {
            //String EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
             b = input.matches(EMAIL_REGEX);
        }
        if (!b) {
            new DialogBox(context, "Email is not in proper format.", "nothing",
                    "Error");
        }
        return b;
    }

    public static String checkPasswordStrength(String password) {
        int strengthPercentage = 0;
        String[] partialRegexChecks = {".*[a-z]+.*", // lower
                ".*[A-Z]+.*", // upper
                ".*[\\d]+.*", // digits
                ".*[@#$%]+.*" // symbols
        };

        if (password.matches(partialRegexChecks[0])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[1])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[2])) {
            strengthPercentage += 25;
        }
        if (password.matches(partialRegexChecks[3])) {
            strengthPercentage += 25;
        }
        String result = "";
        if (strengthPercentage == 0) {
            result = "Very Poor Password";
        } else if (strengthPercentage == 25) {
            result = "Poor Password";
        } else if (strengthPercentage == 50) {
            result = "Normal Password";
        } else if (strengthPercentage == 75) {
            result = "Good Password";
        } else if (strengthPercentage == 100) {
            result = "Strong Password";
        }
        return result;
    }

    public static boolean match(Activity c, String a, String b) {
        if (a.equals(b)) {
            return true;
        } else {
            new DialogBox(c, "Password and Confirm Password donot match.", "", "Alert!");
            return false;

        }
    }

    public static boolean ageCheck(View.OnClickListener c, String DOB) {
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parseddate = sdf.parse(DOB);
            Calendar c2 = Calendar.getInstance();
            c2.add(Calendar.DAY_OF_YEAR, -18);
            Date dateObj2 = new Date(System.currentTimeMillis());
            if (parseddate.before(c2.getTime())) {
                result = true;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }
    public static boolean checkEmpty(Activity context, String variable, String lable)
    {
        if(variable.length()==0)
        {
            new DialogBox(context, lable+" is Missing !", "", "Alert!");

            return false;
        }
        else return true;

    }

    public static boolean expiryCheck(Activity c, String day, String month, String year) {
        final Calendar c1 = Calendar.getInstance();
        int year1 = c1.get(Calendar.YEAR);
        c1.get(Calendar.MONTH);
        c1.get(Calendar.DAY_OF_MONTH);

        if (year != null) {
            int yearInt = Integer.parseInt(year);
            if (year1 + yearInt <= 2016) {
                new DialogBox(c, "Your Document seems to be Expired.", "", "Alert!");
                return false;
            } else {
                return true;
            }
        }
        return false;

    }

    public static boolean specialCharacters(Activity c, String check) {
        String specialChars = "+";
        for (int i = 0; i < check.length(); i++) {
            if (specialChars.contains(check.substring(i, 1))) {
                new DialogBox(c, "Input cannot contain special characters", "", "Alert");
                return false;
            }
        }
        return true;
    }

    public static float setprecision(float number, int precision) {
        BigDecimal numberPrecised = new BigDecimal(number).setScale(precision, BigDecimal.ROUND_HALF_UP);
        return numberPrecised.floatValue();
    }

    public static double setprecision(double number, int precision) {
        BigDecimal numberPrecised = new BigDecimal(number).setScale(precision, BigDecimal.ROUND_HALF_UP);
        return numberPrecised.doubleValue();
    }
}
