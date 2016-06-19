package baxley.ryan.pizzasalesystem.models;

/**
 * Enum of ingredient types
 */
public enum IngredientType {
    TOPPING, CRUST;

    public String getLower(){
        return toString().toLowerCase();
    }

    public String getSingle(){
        String firstLetter = toString().substring(0, 1);
        String rest = toString().substring(1);
        return firstLetter.concat(rest.toLowerCase());
    }

    public String getPlural(){
        return getSingle().concat("s");
    }
}
