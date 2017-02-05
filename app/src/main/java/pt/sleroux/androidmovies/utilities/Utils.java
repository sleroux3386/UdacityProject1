package pt.sleroux.androidmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    public static List<Integer> JSONArrayIntegerToIntegerList(JSONArray array) throws JSONException {
        List<Integer> list = new ArrayList<Integer>();
        if(array!=null) {
            for (int i = 0; i < array.length(); ++i) {
                list.add(array.optInt(i));
            }
        }
        return list;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Date parseDate(String date) throws ParseException {
        Date d = null;
        if(date!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            d = sdf.parse(date);
        }
        return d;
    }

    
    public static String formatDate(Date date) {
        String res = "";
        if(date!=null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            res = sdf.format(date);
        }
        return res;
    }
}
