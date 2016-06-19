package baxley.ryan.pizzasalesystem.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.HashMap;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;

/**
 * Local instance of the settings database.  Is a singleton.  Contains methods for downloading the settings.
 */
public class Settings {
    private static Settings ourInstance = new Settings();

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
        online = true;
    }

    HashMap<String, String> settings;
    boolean online;
    private final String preferenceString = "my_preferences";
    private final String taxString = "tax_preference";

    public void setSalesTax(BigDecimal tax, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceString, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(taxString, tax.floatValue());
        editor.commit();
    }

    public BigDecimal getSalesTax(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceString, Context.MODE_PRIVATE);
        return new BigDecimal(new Float(sharedPref.getFloat(taxString, 0.02f)).toString());
    }

    public boolean isOnline(){
        return online;
    }
    public void setOnline(Context context){
        if(NetworkHelper.isNetworkAvailable(context) == true){
            online = true;
        }else{
            online = false;
        }
    }
}
