package baxley.ryan.pizzasalesystem.models;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Model of a pizza
 */
public class Pizza implements Item {
    private ArrayList<Ingredient> toppings;
    private Integer count;

    public Pizza(ArrayList<Ingredient> toppings){
        this.toppings = toppings;
        count = 1;
    }

    public ArrayList<Ingredient> getToppings(){return toppings;}

    @Override
    public String getName() {
        return toppings.get(0).getName().concat(" pizza");
    }

    @Override
    public BigDecimal getPrice() {
        BigDecimal price = BigDecimal.ZERO;
        for(Ingredient i : toppings){
            price = price.add(i.getPrice().multiply(new BigDecimal(i.getCount())));
        }
        return price;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public void incrementCount() {
        count++;
    }

    @Override
    public void decrementCount() {
        if(count>1) count--;
    }
}
