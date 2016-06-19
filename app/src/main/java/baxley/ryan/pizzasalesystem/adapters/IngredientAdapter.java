package baxley.ryan.pizzasalesystem.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.activities.admin.ingredients.ManageIngredientsActivity;
import baxley.ryan.pizzasalesystem.models.Ingredient;
import baxley.ryan.pizzasalesystem.activities.admin.ingredients.IngredientEditActivity;
import baxley.ryan.pizzasalesystem.tasks.PostWebpageTask;
import baxley.ryan.pizzasalesystem.R;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;

/**
 * Listview adapter for viewing ingredients
 */
public class IngredientAdapter extends ArrayAdapter<Ingredient>{
    private ArrayList<Ingredient> ingredients;
    private final Context context;

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredients) {
        super(context, 0, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ingredient_listview, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.ingredient_list_name);
        listItemText.setText(ingredients.get(position).getName());

        TextView listItemPrice = (TextView)view.findViewById(R.id.ingredient_list_price);
        listItemPrice.setText(currencyFormat(ingredients.get(position).getPrice()));

        //Handle buttons and add onClickListeners
        Button toggleBtn = (Button)view.findViewById(R.id.delete_btn);
        Button editBtn = (Button)view.findViewById(R.id.edit_btn);

        listItemText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        toggleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Delete button
                PostWebpageTask task = new PostWebpageTask(){
                    @Override
                    protected void onPreExecute() {
                        try {
                            jsonParams.put("id", ingredients.get(position).getId());
                        } catch (JSONException e) {
                            Log.e("LOG","Error in toggle onPreExecute");
                            e.printStackTrace();
                        }

                    }

                    @Override
                    protected void onPostExecute(String s) {
                        Intent intent = new Intent("ManageIngredientsActivity");
                        intent.putExtra("method", "get");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                };
                String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/toggleIngredientAvailability.php";
                task.execute(stringUrl);
            }
        }
        );
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Edit button
                Intent i = new Intent(context, IngredientEditActivity.class)
                        .putExtra("id", ingredients.get(position).getId())
                        .putExtra("name", ingredients.get(position).getName())
                        .putExtra("price", ingredients.get(position).getPrice().toString());
                ((Activity)context).startActivityForResult(i, ManageIngredientsActivity.editIngredientConstant);
            }
        });

        return view;
    }
}
