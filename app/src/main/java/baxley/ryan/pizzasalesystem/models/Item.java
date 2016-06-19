package baxley.ryan.pizzasalesystem.models;

import java.math.BigDecimal;

/**
 * Item interface.  Currently only implemented by Pizza
 */
public interface Item {
    public String getName();
    public BigDecimal getPrice();
    public Integer getCount();
    public void incrementCount();
    public void decrementCount();
}
