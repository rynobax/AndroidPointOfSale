package baxley.ryan.pizzasalesystem.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.models.Ingredient;

/**
 * Listview adapter for displaying historical pizza orders
 */
public class DBPizzaAdapter extends BaseAdapter {
    private HashMap<Integer, HashMap<Integer, Integer>> pizzas;
    private final Context context;
    private ArrayList<Ingredient> allIngredients;
    private ArrayList<ArrayList<String>> ingredientStrings;

    public DBPizzaAdapter(Context context, HashMap<Integer, HashMap<Integer, Integer>> pizzas, ArrayList<Ingredient> allIngredients) {
        this.pizzas = pizzas;
        this.context = context;
        this.allIngredients = allIngredients;
        Log.v("SPECIAL", "allingredients size: " + allIngredients.size());
        ingredientStrings = new ArrayList<>(0);
        Log.v("TAG", "pizzas.keySet size is " + pizzas.keySet().size());
        for(Integer pizzaNum : pizzas.keySet()){
            Log.v("SPECIAL", "pizza: " + pizzaNum);
            ArrayList<String> a = new ArrayList<>();
            ingredientStrings.add(a);
            for(Integer ingredientId : pizzas.get(pizzaNum).keySet()){
                for(Ingredient ingredient : allIngredients){
                    Log.v("SPECIAL", "ingredientid: " + ingredient.getId());
                    if(ingredientId.equals(ingredient.getId())){
                        for(Integer n = 0; n < pizzas.get(pizzaNum).get(ingredientId); n++) {
                            a.add(ingredient.getName());
                        }
                        break;
                    }
                }
            }

        }
        Log.v("SPECIAL", "Size: " + ingredientStrings.size());
    }

    @Override
    public int getCount() {
        return ingredientStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return ingredientStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dbpizza_listview, null);
        }

        //Handle TextView and display string from your list
        TextView pizzaName = (TextView) view.findViewById(R.id.dbpizza_listview_name);
        pizzaName.setText(new Integer(position).toString());

        LinearLayout a = (LinearLayout) view.findViewById(R.id.dbpizza_listview_ingredient_list);
        for(String s : ingredientStrings.get(position)){
            Log.v("TAG", "s is " + s);
            TextView textView = new TextView(context);
            textView.setTextSize(16f);
            textView.setText(s);
            a.addView(textView);
        }

        return view;
    }
}