package baxley.ryan.pizzasalesystem.models;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.tasks.DownloadWebpageTask;

/**
 * Model of an order downloaded from the database
 */
public class Order {
    private BigDecimal price;
    private Timestamp timestamp;
    private Integer id;

    Order(JSONObject object) throws JSONException{
        price = new BigDecimal(object.getString("price"));
        timestamp = Timestamp.valueOf(object.getString("ordered_when"));
        id = Integer.parseInt(object.getString("id"));
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public Integer getId() {
        return id;
    }

    /**
     * Parses json and adds the orders to the specified ArrayList
     */
    public static void buildArrayFromJson(ArrayList<Order> orders, String json) throws JSONException {
        Log.v("TAG", "buildArray from this json: " + json);
        JSONArray jsonArray = new JSONArray(json);
        Log.v("TAG", "jsonArray length: " + jsonArray.length());
        JSONObject element = null;
        for(int i = 0; i<jsonArray.length(); i++){
            element = jsonArray.getJSONObject(i);
            Log.v("TAG", "Adding " + element.toString());
            Order order = new Order(element);
            orders.add(order);
        }
    }

    /**
     * Gets an ArrayList of all previous orders
     */
    public static ArrayList<Order> downloadOrders(final Context context) {
        final ArrayList<Order> orders = new ArrayList<>();
        String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/getOrders.php";
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask task = new DownloadWebpageTask() {
                @Override
                public void onPostExecute(String json) {
                    //Do something
                    try {
                        buildArrayFromJson(orders, json);

                        Intent intent = new Intent("PastOrdersActivity");
                        intent.putExtra("method", "refresh");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } catch (JSONException e) {
                        Log.e("TAG", "Error parsing json:" + json);
                        e.printStackTrace();
                    }
                }
            };
            task.execute(stringUrl);
        } else {
            Log.e("TAG", "Error accessing network");
        }
        return orders;
    }
}
