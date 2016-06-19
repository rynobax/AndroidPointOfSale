package baxley.ryan.pizzasalesystem.models;

import java.math.BigDecimal;

/**
 * Was going to add other items but decided to limit the project scope to just pizza.  This is
 * just an example of how another item could be implemented.
 */
public class BasicItem implements Item {
    private String name;
    private BigDecimal price;
    private Integer count;

    public BasicItem(String n, BigDecimal p){
        name = n;
        price = p;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getPrice() {
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
