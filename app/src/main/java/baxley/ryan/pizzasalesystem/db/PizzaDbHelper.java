package baxley.ryan.pizzasalesystem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static baxley.ryan.pizzasalesystem.db.PizzaContract.*;

/**
 * Created by Ryan on 3/8/2016.
 */
public class PizzaDbHelper extends SQLiteOpenHelper {

    public PizzaDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(OrderEntry.CREATE_TABLE);
        db.execSQL(IngredientEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(OrderEntry.DELETE_TABLE);
        db.execSQL(IngredientEntry.DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void resetIngredient(SQLiteDatabase db){
        db.execSQL(IngredientEntry.DELETE_TABLE);
        db.execSQL(IngredientEntry.CREATE_TABLE);
    }
}
