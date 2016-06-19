package baxley.ryan.pizzasalesystem.db;

import android.provider.BaseColumns;

/**
 * Created by Ryan on 3/8/2016.
 */
public class PizzaContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PizzaContract() {}

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Pizza.db";
    private static final String TEXT_TYPE          = " TEXT";
    private static final String FLOAT_TYPE         = " FLOAT";
    private static final String INTEGER_TYPE         = " INTEGER";
    private static final String COMMA_SEP          = ",";

    /* Inner class that defines the table contents */
    public static abstract class OrderEntry implements BaseColumns {
        public static final String TABLE_NAME = "my_order";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_JSON = "json";
        public static final String COLUMN_NAME_PRICE = "price";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_JSON + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PRICE + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class IngredientEntry implements BaseColumns {
        public static final String TABLE_NAME = "ingredient";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TRUE_ID = "true_id";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PRICE + FLOAT_TYPE + COMMA_SEP +
                COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TRUE_ID + INTEGER_TYPE +
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }


}
