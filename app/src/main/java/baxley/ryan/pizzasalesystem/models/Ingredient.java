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
import java.util.ArrayList;
import java.util.HashMap;

import baxley.ryan.pizzasalesystem.tasks.DownloadWebpageTask;

/**
 * Model of an ingredient
 */
public class Ingredient {
    private Integer id;
    private final String name;
    private BigDecimal price;
    private boolean available;
    private IngredientType type;
    private Integer count;

    private static HashMap<IngredientType, Integer> IngredientTypeCounts;

    public Ingredient(JSONObject object) throws JSONException {
        name = object.getString("name");
        price = new BigDecimal(object.getString("price"));
        id = new Integer(object.getInt("id"));
        String boolString = object.getString("available");
        if(boolString.equals("0")) available = false;
        else available = true;
        type = IngredientType.valueOf(object.getString("type").toUpperCase());
        count = 1;
    }

    /* Only used to convert local sql db to the arraylist used by the order sequence */
    public Ingredient(String n, float p, String t, int true_id){
        name = n;
        price = new BigDecimal(p);
        id = new Integer(true_id);
        available = true;
        type = IngredientType.valueOf(t.toUpperCase());
        count = 1;
    }

    public String getName(){return name;}
    public BigDecimal getPrice(){return price;}
    public Integer getId(){return id;}
    public boolean isAvailable(){return available;}
    public IngredientType getType(){return type;}

    public Integer getCount(){return count;}
    public void incrementCount(){count++;}
    public void decrementCount(){if(count>1)count--;}

    public static Integer getIngredientTypeCount(IngredientType i){return IngredientTypeCounts.get(i);}

    /**
     * Creates ArrayList of ingredients from a json string
     */
    public static void buildArrayFromJson(ArrayList<Ingredient> ingredients, String json) throws JSONException {
        IngredientTypeCounts = new HashMap<>();
        Log.v("TAG", "buildArray from this json: " + json);
        JSONArray jsonArray = new JSONArray(json);
        Log.v("TAG", "jsonArray length: " + jsonArray.length());
        JSONObject element = null;
        for(int i = 0; i<jsonArray.length(); i++){
            element = jsonArray.getJSONObject(i);
            Log.v("TAG", "Adding " + element.toString());
            Ingredient ingredient = new Ingredient(element);
            ingredients.add(ingredient);
            Integer count;
            if( (count = IngredientTypeCounts.get(ingredient.getType())) != null){
                IngredientTypeCounts.put(ingredient.getType(), ++count);
            }else {
                IngredientTypeCounts.put(ingredient.getType(), 1);
            }
        }
    }

    public static ArrayList<Ingredient> downloadIngredients(final Context context, final String activitySource) {
        final ArrayList<Ingredient> ingredients = new ArrayList<>();
        String stringUrl = "http://people.cs.clemson.edu/~rmbaxle/pizzaDatabase/getIngredients.php";
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            Log.e("LOG", "ConnectivityManager is null");
            return null;
        }
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWebpageTask task = new DownloadWebpageTask() {
                @Override
                public void onPostExecute(String json) {
                    //Do something
                    try {
                        buildArrayFromJson(ingredients, json);

                        Intent intent = new Intent(activitySource);
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
        return ingredients;
    }
}
