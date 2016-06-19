package baxley.ryan.pizzasalesystem.activities.order;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.adapters.IngredientAdapter;
import baxley.ryan.pizzasalesystem.models.Ingredient;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;
/**
 * Activity for selecting a new topping for a pizza
 */
public class PickToppingActivity extends AppCompatActivity {
    ArrayList<String> toppingList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_topping);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        Gson gson = new Gson();
        Type ArrayListOfIngredient = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        ArrayList<Ingredient> ingredientList = gson.fromJson(json, ArrayListOfIngredient);
        toppingList = new ArrayList<>(0);

        for(Ingredient ingredient : ingredientList){
            if(ingredient.getType().getLower().equals("topping")){
                toppingList.add(ingredient.getName() + " (" + currencyFormat(ingredient.getPrice()) + ")");
                Log.v("TAG", "Added ingredient " + ingredient.getName());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toppingList);
        listView = (ListView) findViewById(R.id.pickToppingList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnTopping(position);
            }
        });
    }

    public void returnTopping(int position){
        String topping = listView.getItemAtPosition(position).toString();
        topping = topping.substring(0, topping.indexOf(" ("));
        Intent intent = new Intent();
        intent.putExtra("topping", topping);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
