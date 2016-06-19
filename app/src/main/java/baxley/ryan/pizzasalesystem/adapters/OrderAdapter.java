package baxley.ryan.pizzasalesystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.models.Order;

import static baxley.ryan.pizzasalesystem.helpers.MoneyHelper.currencyFormat;

/**
 * Listview adapter for viewing historical orders
 */
public class OrderAdapter extends ArrayAdapter<Order> {
    private ArrayList<Order> orders;
    private final Context context;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        super(context, 0, orders);
        this.orders = orders;
        this.context = context;
    }

    @Override
    public Order getItem(int position) {
        return super.getItem(getCount() - 1 - position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.order_listview, null);
        }

        //Handle TextView and display string from your list
        TextView orderPrice = (TextView)view.findViewById(R.id.pizza_listview_name);
        orderPrice.setText(currencyFormat(getItem(position).getPrice()));


        TextView orderDate = (TextView)view.findViewById(R.id.order_listview_date);
        orderDate.setText(getItem(position).getTimestamp().toString());

        return view;
    }
}
