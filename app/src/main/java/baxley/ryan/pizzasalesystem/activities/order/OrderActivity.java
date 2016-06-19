package baxley.ryan.pizzasalesystem.activities.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.activities.MainActivity;
import baxley.ryan.pizzasalesystem.adapters.ItemAdapter;
import baxley.ryan.pizzasalesystem.models.BasicItem;
import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.models.Item;
import baxley.ryan.pizzasalesystem.models.Pizza;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;
/**
 * Activity for viewing the current order
 */
public class OrderActivity extends AppCompatActivity {
    public static final int NEW_PIZZA_CONSTANT = 1;
    public static final int EDIT_PIZZA_CONSTANT = 2;
    public static final int FINISH_ORDER_CONSTANT = 4;
    private ArrayList<Item> items;
    private ItemAdapter itemAdapter;
    private ArrayList<Ingredient> ingredientList;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        items = new ArrayList<>(0);
        refreshListview();

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        Gson gson = new Gson();
        Type ArrayListOfIngredient = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        ingredientList = gson.fromJson(json, ArrayListOfIngredient);
    }

    public void refreshListview(){
        itemAdapter = new ItemAdapter(this, items);
        ListView orders = (ListView) findViewById(R.id.orders);
        orders.setAdapter(itemAdapter);
        orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                if (items.get(position).getClass().equals(Pizza.class)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set title
                    alertDialogBuilder.setTitle("Item Action");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("What would you like to do to the pizza?")
                            .setCancelable(false)
                            .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    editPizza(position);
                                }
                            })
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    items.remove(position);
                                    refreshListview();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }
        });

        BigDecimal total = new BigDecimal(0);
        for(Item item : items){
            total = total.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
        }
        TextView textView = (TextView) findViewById(R.id.totalText);
        textView.setText(currencyFormat(total));
    }

    public void editPizza(int position){
        Gson gson = new Gson();
        Intent intent = new Intent(OrderActivity.this, PizzaActivity.class);
        intent.putExtra("json", gson.toJson(ingredientList));
        intent.putExtra("editjson", gson.toJson( ((Pizza)items.get(position)).getToppings()));
        intent.putExtra("position", position);
        startActivityForResult(intent, EDIT_PIZZA_CONSTANT);
    }

    public void newPizza(){
        Gson gson = new Gson();
        Intent intent = new Intent(OrderActivity.this, PizzaActivity.class);
        intent.putExtra("json", gson.toJson(ingredientList));
        startActivityForResult(intent, NEW_PIZZA_CONSTANT);
    }

    public void finishOrder(View view){
        if(items.isEmpty()){
            Toast.makeText(OrderActivity.this, "Order is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(OrderActivity.this, FinishOrderActivity.class);
        Gson gson = new Gson();

        ArrayList<BasicItem> basicItems = new ArrayList<>(0);
        ArrayList<Pizza> pizzas = new ArrayList<>(0);

        for(Item i : items){
            if(i.getClass().equals(Pizza.class)) pizzas.add((Pizza)i);
            if(i.getClass().equals(BasicItem.class)) basicItems.add((BasicItem)i);
        }

        intent.putExtra("basicItems", gson.toJson(basicItems));
        intent.putExtra("pizza", gson.toJson(pizzas));
        startActivityForResult(intent, FINISH_ORDER_CONSTANT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            Gson gson = new Gson();
            if(requestCode == NEW_PIZZA_CONSTANT){
                items.add(gson.fromJson(data.getStringExtra("Pizza"), Pizza.class));
            }else if(requestCode == EDIT_PIZZA_CONSTANT){
                items.remove(data.getIntExtra("position", -1));
                items.add(gson.fromJson(data.getStringExtra("Pizza"), Pizza.class));
            }else if(requestCode == FINISH_ORDER_CONSTANT){
                Log.v("TAG", "I should be finished");
                finish();
            }
            refreshListview();
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            Log.v("TAG", "Got cancelled");
        }
    }

    @Override
    public void onBackPressed() {
        if(!items.isEmpty()){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle("Exit");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to exit?  You will lose the current order.")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            finish();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }else{
            super.onBackPressed();
        }
    }

    /* Should you want to add more items, you would launch a menu from here */
    public void newSomething(View view){
        newPizza();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshListview();
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
                mMessageReceiver, new IntentFilter("refresh"));
        super.onResume();
    }
}
