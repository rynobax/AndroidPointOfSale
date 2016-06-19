package baxley.ryan.pizzasalesystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.models.Ingredient;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;

/**
 * Listview adapter for viewing pizza toppings
 */
public class PizzaToppingAdapter extends ArrayAdapter<Ingredient> {
    private ArrayList<Ingredient> toppings;
    private final Context context;

    public PizzaToppingAdapter(Context context, ArrayList<Ingredient> toppings) {
        super(context, 0, toppings);
        this.toppings = toppings;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_listview, null);
        }

        TextView listItemCount = (TextView)view.findViewById(R.id.item_listview_count);
        listItemCount.setText("x" + toppings.get(position).getCount());

        TextView listItemText = (TextView)view.findViewById(R.id.item_listview_name);
        listItemText.setText(toppings.get(position).getName());

        TextView listItemPrice = (TextView)view.findViewById(R.id.item_listview_price);
        listItemPrice.setText(currencyFormat(toppings.get(position).getPrice()
                .multiply(new BigDecimal(toppings.get(position).getCount()))));


        Button plusBtn = (Button)view.findViewById(R.id.item_listview_plus);
        Button minusBtn = (Button)view.findViewById(R.id.item_listview_minus);

        plusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                toppings.get(position).incrementCount();
                requestRefresh();
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                toppings.get(position).decrementCount();
                requestRefresh();
            }
        });

        return view;
    }

    public void requestRefresh(){
        Intent intent = new Intent("refresh");
        intent.putExtra("method", "refresh");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}