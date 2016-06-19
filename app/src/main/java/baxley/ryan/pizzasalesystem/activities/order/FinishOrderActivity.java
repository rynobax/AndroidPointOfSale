package baxley.ryan.pizzasalesystem.activities.order;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.adapters.ItemAdapter;
import baxley.ryan.pizzasalesystem.db.PizzaContract;
import baxley.ryan.pizzasalesystem.db.PizzaDbHelper;
import baxley.ryan.pizzasalesystem.helpers.NetworkHelper;
import baxley.ryan.pizzasalesystem.helpers.Settings;
import baxley.ryan.pizzasalesystem.models.BasicItem;
import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.models.Item;
import baxley.ryan.pizzasalesystem.models.Pizza;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;

import static baxley.ryan.pizzasalesystem.db.PizzaContract.*;
import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;
/**
 * Activity that sends the completed order to the server to be added to the database
 */
public class FinishOrderActivity extends AppCompatActivity {
    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_order);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        items = new ArrayList<>(0);
        Intent intent = getIntent();
        Gson gson = new Gson();

        String json = intent.getStringExtra("pizza");
        Type ArrayListOfPizza = new TypeToken<ArrayList<Pizza>>(){}.getType();
        ArrayList<Pizza> pizzas = gson.fromJson(json, ArrayListOfPizza);

        json = intent.getStringExtra("basicItems");
        Type ArrayListOfBasicItem = new TypeToken<ArrayList<BasicItem>>(){}.getType();
        ArrayList<BasicItem> basicItems = gson.fromJson(json, ArrayListOfBasicItem);

        for(Pizza p : pizzas){
            items.add(p);
        }
        for(BasicItem b : basicItems){
            items.add(b);
        }

        ItemAdapter itemAdapter = new ItemAdapter(this, items);
        ListView orders = (ListView) findViewById(R.id.finishOrderList);
        orders.setAdapter(itemAdapter);

        BigDecimal total = new BigDecimal(0);
        for(Item item : items){
            total = total.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
            Log.v("TAG", "Total: " + total);
        }
        BigDecimal salesTax = Settings.getInstance().getSalesTax(this);
        total = total.multiply(salesTax.add(BigDecimal.ONE));
        TextView textView = (TextView) findViewById(R.id.finishTotalText);
        textView.setText(currencyFormat(total));
    }

    public void payCash(View view){
        completeOrder();
    }

    public void payDebit(View view){
        completeOrder();
    }

    /**
     * Converts the order to a format that the server can parse
     */
    private void completeOrder(){
        Gson gson = new Gson();
        ArrayList<Pizza> pizzas = new ArrayList<>(0);
        for (Item i : items) pizzas.add((Pizza) i);

        BigDecimal price = new BigDecimal(0);
        ArrayList<ArrayList<Integer>> pizzaArray = new ArrayList<>(0);
        for (Pizza p : pizzas) {
            ArrayList<Integer> ingredientsArray = new ArrayList<>(0);
            for (Ingredient i : p.getToppings()) {
                for (int n = 0; n < i.getCount(); n++) {
                    ingredientsArray.add(i.getId());
                    price = price.add(i.getPrice());
                }
            }
            pizzaArray.add(ingredientsArray);
        }
        String json = gson.toJson(pizzaArray);
        if(NetworkHelper.isNetworkAvailable(this) == true) {
            // Send to external db
            Log.v("TAG", "Sending json: " + json);
            sendOrder(json, price);
        }else{
            // Add to local db
            PizzaDbHelper mDbHelper = new PizzaDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            java.util.Date date= new java.util.Date();
            java.sql.Timestamp sqlDate = new java.sql.Timestamp(date.getTime());
            Log.v("TAG", "SQLtime: " + sqlDate.toString());

            ContentValues values = new ContentValues();
            values.put(OrderEntry.COLUMN_NAME_DATE, sqlDate.toString());
            values.put(OrderEntry.COLUMN_NAME_JSON, json);
            values.put(OrderEntry.COLUMN_NAME_PRICE, price.floatValue());

            db.insert(
                    OrderEntry.TABLE_NAME,
                    null,
                    values);
        }

        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * Starts Asynchronous task to send order
     */
    private void sendOrder(final String json, final BigDecimal price) {
        String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/addOrder.php";
        PostWebpageTask task = new PostWebpageTask(){
            @Override
            protected void onPreExecute() {
                try {
                    jsonParams.put("order", json);
                    jsonParams.put("price", price.floatValue());
                } catch (JSONException e) {
                    Log.e("LOG", "Error in IngredientCreateActivity onPreExecute");
                    e.printStackTrace();
                }
            }
        };
        task.execute(stringUrl);
    }
}