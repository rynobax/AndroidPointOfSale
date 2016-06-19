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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.adapters.PizzaToppingAdapter;
import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.models.IngredientType;
import baxley.ryan.pizzasalesystem.models.Pizza;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;
/**
 * Activity for building a new pizza
 */
public class PizzaActivity extends AppCompatActivity {
    private static final int PICK_TOPPING_ACTIVITY_CONSTANT = 1;

    private ArrayList<Ingredient> toppings;
    private ArrayList<String> crusts;
    private ArrayList<Ingredient> ingredientList;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        toppings = new ArrayList(0);
        crusts = new ArrayList(0);

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        Gson gson = new Gson();
        Type ArrayListOfIngredient = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        ingredientList = gson.fromJson(json, ArrayListOfIngredient);

        for(Ingredient i : ingredientList){
            if(i.getType().getLower().equals("crust")){
                Log.v("CRUSTTAG", "Adding crust " + i.getName());
                crusts.add(i.getName() + " (" + currencyFormat(i.getPrice()) + ")");
            }
        }

        Spinner spinner = (Spinner) findViewById(R.id.crustSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, crusts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(intent.hasExtra("editjson")){
            ArrayList<Ingredient> ingredientsToPopulateWith = gson.fromJson(intent.getStringExtra("editjson"), ArrayListOfIngredient);
            for(Ingredient i : ingredientsToPopulateWith){
                if(i.getType().equals(IngredientType.CRUST)){
                    Log.v("LOG", "Crust: " + i.getName());
                    Integer crustLocation = 0;
                    Integer count = 0;
                    for(String c : crusts){
                        Log.v("LOG", "c: " + c.substring(0, c.indexOf(" (")));
                        if(c.substring(0, c.indexOf(" (")).equals(i.getName())){
                            Log.v("LOG", "Matched at position " + count);
                            crustLocation = count;
                            break;
                        }
                        count++;
                    }
                    spinner.setSelection(crustLocation);
                }else{
                    toppings.add(i);
                    Ingredient removeme = null;
                    for(Ingredient ingredient : ingredientList){
                        if(ingredient.getName().equals(i.getName())) removeme = ingredient;
                    }
                    ingredientList.remove(removeme);
                }
            }
        }

        refreshToppingsList();
    }

    private void refreshToppingsList() {
        Log.v("TAG","Trying to refresh topping list");
        PizzaToppingAdapter pizzaToppingAdapter = new PizzaToppingAdapter(this, toppings);
        ListView listView = (ListView) findViewById(R.id.toppingsList);
        listView.setAdapter(pizzaToppingAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    // set title
                    alertDialogBuilder.setTitle("Ingredient Action");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Would you like to remove the ingredient?")
                            .setCancelable(false)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            })
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ingredientList.add(toppings.get(position));
                                    toppings.remove(position);
                                    refreshToppingsList();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
            }
        });
    }

    public void addTopping(View view){
        Intent intent = new Intent(PizzaActivity.this, PickToppingActivity.class);
        Gson gson = new Gson();
        String json = gson.toJson(ingredientList);
        intent.putExtra("json", json);
        startActivityForResult(intent, PICK_TOPPING_ACTIVITY_CONSTANT);
    }

    public void finishPizza(View view){
        /* Add crust to ingredient list */
        Spinner spinner = (Spinner) findViewById(R.id.crustSpinner);
        String crustString = spinner.getSelectedItem().toString();
        crustString = crustString.substring(0, crustString.indexOf(" ("));
        Log.v("LOG", "crustString: " + crustString);
        for(Ingredient ingredient : ingredientList){
            if(ingredient.getName().equals(crustString)){
                toppings.add(ingredient);
            }
        }

        Pizza pizza = new Pizza(toppings);

        Intent oldIntent = getIntent();
        Intent intent = new Intent();
        Gson gson = new Gson();
        intent.putExtra("Pizza", gson.toJson(pizza));
        intent.putExtra("position", oldIntent.getIntExtra("position", -1));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            String topping = data.getStringExtra("topping");
            for(Ingredient ingredient : ingredientList){
                if(ingredient.getName().equals(topping)){
                    toppings.add(ingredient);
                    ingredientList.remove(ingredient);
                    refreshToppingsList();
                    return;
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshToppingsList();
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