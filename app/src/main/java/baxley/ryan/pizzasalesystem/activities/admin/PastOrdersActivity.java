package baxley.ryan.pizzasalesystem.activities.admin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.adapters.OrderAdapter;
import baxley.ryan.pizzasalesystem.adapters.DBPizzaAdapter;
import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.models.Order;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;

public class PastOrdersActivity extends AppCompatActivity {
    private ArrayList<Order> orders;
    private HashMap<Integer, HashMap<Integer, Integer>> pizzas;
    private OrderAdapter orderAdapter;
    private DBPizzaAdapter DBPizzaAdapter;
    private ArrayList<Ingredient> allIngredients;
    private Context context = this;
    private boolean viewingOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        viewingOrder = false;

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        Gson gson = new Gson();
        Type ArrayListOfIngredient = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        allIngredients = gson.fromJson(json, ArrayListOfIngredient);

        orders = Order.downloadOrders(context);
    }

    /**
     * Refreshes list of order.  Called when the asynchronous task for downloading the orders is finished
     */
    public void refreshOrderList(){
        ListView listView = (ListView) findViewById(R.id.pastOrderListview);
        orderAdapter = new OrderAdapter(context, orders);
        listView.setAdapter(orderAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final ArrayList<Ingredient> ingredients = new ArrayList<>();
                String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/getPizzas.php";
                ConnectivityManager connMgr = (ConnectivityManager)
                        context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    PostWebpageTask task = new PostWebpageTask() {
                        @Override
                        protected void onPreExecute() {
                            try {
                                jsonParams.put("order_id", orders.get(position).getId());
                            } catch (JSONException e) {
                                Log.e("LOG","Error in PastOrdersActivity onPreExecute");
                                e.printStackTrace();
                                ((Activity)context).setResult(RESULT_CANCELED);
                            }
                        }

                        @Override
                        public void onPostExecute(String json) {
                            //Do something
                            try {
                                Log.v("TAG", "WE RECIEVED: " + json);
                                pizzas = new HashMap<>(0);
                                Log.v("TAG", "buildArray from this json: " + json);
                                JSONArray jsonArray = new JSONArray(json);
                                Log.v("TAG", "jsonArray length: " + jsonArray.length());
                                JSONObject element = null;
                                for(int i = 0; i<jsonArray.length(); i++){
                                    element = jsonArray.getJSONObject(i);
                                    Integer pizza_id = Integer.parseInt(element.getString("pizza_id"));
                                    Integer ingredient_id = Integer.parseInt(element.getString("ingredient_id"));
                                    Log.v("TAG", "Got " + pizza_id);
                                    if(pizzas.containsKey(pizza_id)){
                                        Log.v("TAG", "Incrementing");
                                        HashMap<Integer, Integer> h = pizzas.get(pizza_id);
                                        if(h.containsKey(ingredient_id)){
                                            Integer n = h.get(ingredient_id);
                                            h.put(ingredient_id, ++n);
                                        }else{
                                            h.put(ingredient_id, 1);
                                        }
                                    }else{
                                        Log.v("TAG", "Creating");
                                        HashMap h = new HashMap<Integer, Integer>(0);
                                        h.put(ingredient_id, 1);
                                        pizzas.put(pizza_id, h);
                                    }
                                }

                                Intent intent = new Intent("PastOrdersActivity");
                                intent.putExtra("method", "pizza");
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
            }
        });
    }

    /**
     * Switches listview to show an order's pizzas
     */
    private void forcePizzaList(){
        ListView listView = (ListView) findViewById(R.id.pastOrderListview);
        DBPizzaAdapter = new DBPizzaAdapter(this, pizzas, allIngredients);
        listView.setAdapter(DBPizzaAdapter);
    }

    @Override
    public void onBackPressed() {
        if(viewingOrder){
            refreshOrderList();
            viewingOrder = false;
        }else{
            super.onBackPressed();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String method = intent.getStringExtra("method");
            if(method.equals("refresh")){
                refreshOrderList();
            }else if(method.equals("pizza")){
                forcePizzaList();
                viewingOrder = true;
            }

        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("PastOrdersActivity"));
        super.onResume();
    }
}
