package com.hv.briskybake.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hv.briskybake.Model.Request;
import com.hv.briskybake.Model.User;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static User currentUser;
    public static Request currentRequest;

    private static final String BASE_URL="https://fcm.googleapis.com/";

    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";
    public static final int PICK_IMAGE_REQUEST = 71;

    public static final double server_latitude=26.79866;
    public static final double server_longitude= 80.909712;

    public static String convertCodeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "On the Way";
        else
            return "Shipped";
    }

    public static boolean isConnectToInternet(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null)
        {
            NetworkInfo[] infos=connectivityManager.getAllNetworkInfo();
            if(infos!=null)
            {
                for (int i=0;i<infos.length;i++)
                {
                    if(infos[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static String getDate(long time)
    {
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date=new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm"
                ,calendar)
                .toString()
        );
        return date.toString();
    }
}
