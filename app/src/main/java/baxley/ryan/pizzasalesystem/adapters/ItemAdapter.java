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
import baxley.ryan.pizzasalesystem.models.Item;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;

/**
 * Listview adapter for displaying items, like pizza
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private ArrayList<Item> items;
    private final Context context;

    public ItemAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.items = items;
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
        listItemCount.setText("x" + items.get(position).getCount());

        TextView listItemText = (TextView)view.findViewById(R.id.item_listview_name);
        listItemText.setText(items.get(position).getName());

        TextView listItemPrice = (TextView)view.findViewById(R.id.item_listview_price);
        listItemPrice.setText(currencyFormat(items.get(position).getPrice()
                .multiply(new BigDecimal(items.get(position).getCount()))));


        Button plusBtn = (Button)view.findViewById(R.id.item_listview_plus);
        Button minusBtn = (Button)view.findViewById(R.id.item_listview_minus);

        plusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                items.get(position).incrementCount();
                requestRefresh();
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                items.get(position).decrementCount();
                requestRefresh();
            }
        });

        return view;
    }

    public void requestRefresh(){
        Intent intent = new Intent("refresh");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}