package baxley.ryan.pizzasalesystem.activities.admin.ingredients;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;

import java.math.BigDecimal;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;

/**
 * Activity for editing an existing database
 */
public class IngredientEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_create);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Button button = (Button) findViewById(R.id.addButton);
        button.setText("Edit");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIngredient(v);
            }
        });
        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");

        EditText nameText = (EditText) findViewById(R.id.ingredientName);
        EditText priceText = (EditText) findViewById(R.id.price);
        nameText.setText(name);
        priceText.setText(price);
    }

    public void editIngredient(View view){
        Intent intent = this.getIntent();
        EditText nameText = (EditText) findViewById(R.id.ingredientName);
        EditText priceText = (EditText) findViewById(R.id.price);

        final String name = nameText.getText().toString();
        final BigDecimal price = new BigDecimal(priceText.getText().toString());
        Log.v("TAG", "name: " + name + ", price: " + price);
        final int id = intent.getIntExtra("id", -1);

        final Context context = this;
        PostWebpageTask task = new PostWebpageTask(){
            @Override
            protected void onPreExecute() {
                try {
                    jsonParams.put("id", id);
                    jsonParams.put("name", name);
                    jsonParams.put("price", price.toString());
                } catch (JSONException e) {
                    Log.e("LOG", "Error in IngredientCreateActivity onPreExecute");
                    e.printStackTrace();
                    ((Activity)context).setResult(RESULT_CANCELED);
                }

            }
        };
        String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/editIngredient.php";
        task.execute(stringUrl);
        this.setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_OK);
        super.onBackPressed();
    }
}
