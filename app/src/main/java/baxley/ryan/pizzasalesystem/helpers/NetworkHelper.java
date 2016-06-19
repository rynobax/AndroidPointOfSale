package baxley.ryan.pizzasalesystem.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.db.PizzaContract;
import baxley.ryan.pizzasalesystem.db.PizzaDbHelper;
import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;

import static baxley.ryan.pizzasalesystem.db.PizzaContract.*;

/**
 * Created by Ryan on 3/8/2016.
 */
public class NetworkHelper {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void uploadDatabase(Context context){
        Log.v("TAG", "About to upload local database");
        PizzaDbHelper mDbHelper = new PizzaDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        /* Returnz the entire table */
        Cursor c = db.query(
                OrderEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String date = c.getString(c.getColumnIndex(OrderEntry.COLUMN_NAME_DATE));
            String json = c.getString(c.getColumnIndex(OrderEntry.COLUMN_NAME_JSON));
            float price = c.getFloat(c.getColumnIndex(OrderEntry.COLUMN_NAME_PRICE));
            sendOldOrder(date, json, price);

            // Define 'where' part of query.
            String selection = OrderEntry._ID + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] selectionArgs = { String.valueOf(c.getInt(c.getColumnIndex(OrderEntry._ID))) };
            // Issue SQL statement.
            db.delete(OrderEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    private static void sendOldOrder(final String date, final String json, final float price) {
        String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/addOrder.php";
        PostWebpageTask task = new PostWebpageTask(){
            @Override
            protected void onPreExecute() {
                try {
                    jsonParams.put("order", json);
                    jsonParams.put("price", price);
                    jsonParams.put("date", date);
                    Log.v("TAG", "Date being sent is " + date);
                } catch (JSONException e) {
                    Log.e("LOG", "Error in IngredientCreateActivity onPreExecute");
                    e.printStackTrace();
                }
            }
        };
        task.execute(stringUrl);
    }
}
