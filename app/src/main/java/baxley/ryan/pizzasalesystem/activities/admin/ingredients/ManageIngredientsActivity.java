package baxley.ryan.pizzasalesystem.activities.admin.ingredients;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.BroadcastReceiver;

import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.adapters.IngredientAdapter;
import baxley.ryan.pizzasalesystem.models.IngredientType;
/**
 * For viewing the ingredients in the database
 */
public class ManageIngredientsActivity extends AppCompatActivity {

    public static final int createIngredientConstant = 1;
    public static final int editIngredientConstant = 2;

    /* Variables that define what is being shown in the table */
    private boolean active = true;

    private ArrayList<Ingredient> ingredients;
    private ArrayList<Ingredient> filteredIngredients;
    private Context context = this;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ingredients = Ingredient.downloadIngredients(this, "ManageIngredientsActivity");

        Button toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setText(getToggleButtonText());

        ArrayList<String> ingredientTypes = new ArrayList<>(0);
        for(IngredientType i : IngredientType.values()){
            ingredientTypes.add(i.getPlural());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ingredientTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.ingredientTypeSpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                refreshIngredientList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.v("NOTHING", "Nothing selected");
            }

        });
    }

    public void refreshIngredientList(){
        Spinner spinner = (Spinner) findViewById(R.id.ingredientTypeSpinner);
        String type = spinner.getSelectedItem().toString();
        Log.v("SPINTAG", "Spinner has selected type: " + type);
        filteredIngredients = new ArrayList<>(0);
        for(Ingredient ingredient : ingredients){
            Log.v("TAG", "ingredient: " + ingredient.getType().getPlural().toLowerCase() + ", type: " + type.toLowerCase());
            if(ingredient.isAvailable() == active &&
                    ingredient.getType().getPlural().toLowerCase().equals(type.toLowerCase())) {
                filteredIngredients.add(ingredient);
            }
        }

        Log.v("TAG", "filteredIngredients size: " + filteredIngredients.size());
        ListView ingredientList = (ListView) findViewById(R.id.ingredientList);
        ingredientAdapter = new IngredientAdapter(context, filteredIngredients);
        ingredientList.setAdapter(ingredientAdapter);
    }

    public void addIngredient(View view){
        startActivityForResult(new Intent(ManageIngredientsActivity.this, IngredientCreateActivity.class), createIngredientConstant);
    }

    public void toggleIngredients(View view){
        active = !active;
        Button toggleButton = (Button) findViewById(R.id.toggleButton);
        toggleButton.setText(getToggleButtonText());
        refreshIngredientList();
    }

    private String getToggleButtonText(){
        if(active){
            return getString(R.string.show_inactive);
        }else{
            return getString(R.string.show_active);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            ingredients = Ingredient.downloadIngredients(this, "ManageIngredientsActivity");
            refreshIngredientList();
        }else {
            Context context = getApplicationContext();
            CharSequence text = "Unknown error!";
            if(requestCode == createIngredientConstant){
                text = "Error creating ingredient!";
            }else if(requestCode == editIngredientConstant){
                text = "Error editing ingredient!";
            }
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String method = intent.getStringExtra("method");
            if(method.equals("refresh")){
                refreshIngredientList();
            }
            if(method.equals("get")){
                ingredients = Ingredient.downloadIngredients(context, "ManageIngredientsActivity");
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
                mMessageReceiver, new IntentFilter("ManageIngredientsActivity"));
        super.onResume();
    }
}
