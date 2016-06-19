package baxley.ryan.pizzasalesystem.activities.admin.ingredients;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.models.IngredientType;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;
/**
 * Activity for adding a new ingredient to the database
 */
public class IngredientCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_create);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ArrayList<String> ingredientTypes = new ArrayList<>(0);
        for(IngredientType i : IngredientType.values()){
            ingredientTypes.add(i.getSingle());
        }

        Spinner spinner = (Spinner) findViewById(R.id.createIngredientTypeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ingredientTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void addIngredient(View view){
        EditText nameText = (EditText) findViewById(R.id.ingredientName);
        final Spinner typeSpinner = (Spinner) findViewById(R.id.createIngredientTypeSpinner);
        EditText priceText = (EditText) findViewById(R.id.price);

        if(nameText.getText().toString().isEmpty() ||
                priceText.getText().toString().isEmpty()){
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
            return;
        }

        final String name = nameText.getText().toString();
        final BigDecimal price = new BigDecimal(priceText.getText().toString());

        final Context context = this;
        String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/addIngredient.php";
        PostWebpageTask task = new PostWebpageTask(){
            @Override
            protected void onPreExecute() {
                try {
                    jsonParams.put("name", name);
                    jsonParams.put("type", typeSpinner.getSelectedItem().toString().toLowerCase());
                    jsonParams.put("price", price.toString());
                } catch (JSONException e) {
                    Log.e("LOG","Error in IngredientCreateActivity onPreExecute");
                    e.printStackTrace();
                    ((Activity)context).setResult(RESULT_CANCELED);
                }

            }
        };
        task.execute(stringUrl);
        this.setResult(RESULT_OK);
        finish();
    }
}
