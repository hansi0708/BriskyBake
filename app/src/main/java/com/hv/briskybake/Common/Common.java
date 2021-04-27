package com.hv.briskybake.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseUser;

public class Common {
    public static FirebaseUser currentUser;

    public static final String DELETE="Delete";
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";

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
}
