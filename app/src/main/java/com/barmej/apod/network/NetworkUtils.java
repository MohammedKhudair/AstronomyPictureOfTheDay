package com.barmej.apod.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.barmej.apod.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASIC_URL = "https://api.nasa.gov/planetary/apod?";
    private static final String API_KEY = "api_key";


    public static URL getCurrentAPOD_Url(Context context) {
        // الحصول على التاريخ الحالي
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentDay = "date=" + df.format(new Date());
        return buildUrl(context, currentDay);
    }

    public static URL getSpecificAPOD_Url(Context context,String date) {
        return buildUrl(context,"date=" + date);
    }

    private static URL buildUrl(Context context, String date) {
        Uri.Builder uriBuilder = Uri.parse(BASIC_URL + date).buildUpon();

        Uri uri = uriBuilder.appendQueryParameter(API_KEY, context.getString(R.string.api_key)).build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
