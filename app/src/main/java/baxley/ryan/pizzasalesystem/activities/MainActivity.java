package baxley.ryan.pizzasalesystem.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.activities.order.OrderActivity;
import baxley.ryan.pizzasalesystem.activities.admin.SettingsActivity;
import baxley.ryan.pizzasalesystem.db.PizzaDbHelper;
import baxley.ryan.pizzasalesystem.helpers.NetworkHelper;
import baxley.ryan.pizzasalesystem.helpers.Settings;
import baxley.ryan.pizzasalesystem.models.Ingredient;

import static baxley.ryan.pizzasalesystem.db.PizzaContract.*;


/**
 * The main activity
 */
public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 101;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 102;
    private static final int NEW_ORDER_CONSTANT = 1;
    private static final int SETTINGS_CONSTANT = 2;

    /**
     * A list of all the active ingredients that is used by the order activity
     */
    private ArrayList<Ingredient> ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /* Permissions */
        getHTTPPermissions();
        getInternetPermissions();

        /* Set the online status */
        Settings.getInstance().setOnline(this);

        /* If the last session ended while offline we want to upload that data */
        if(Settings.getInstance().isOnline()){
            NetworkHelper.uploadDatabase(this);
            refreshIngredientList();
        } else {
            ingredientList = makeIngredientListFromDb();
        }
    }

    private ArrayList<Ingredient> makeIngredientListFromDb() {
        ArrayList<Ingredient> list = new ArrayList<>();

        PizzaDbHelper mDbHelper = new PizzaDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        /* Returnz the entire table */
        Cursor c = db.query(
                IngredientEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String name = c.getString(c.getColumnIndex(IngredientEntry.COLUMN_NAME_NAME));
            float price = c.getFloat(c.getColumnIndex(IngredientEntry.COLUMN_NAME_PRICE));
            String type = c.getString(c.getColumnIndex(IngredientEntry.COLUMN_NAME_TYPE));
            int true_id = c.getInt(c.getColumnIndex(IngredientEntry.COLUMN_NAME_TRUE_ID));
            list.add(new Ingredient(name, price, type, true_id));
        }
        return list;
    }

    public void launchSettings(View view) {
        Settings.getInstance().setOnline(this);
        if(Settings.getInstance().isOnline()) {
            Gson gson = new Gson();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("json", gson.toJson(ingredientList));
            startActivityForResult(intent, SETTINGS_CONSTANT);
        }else{
            Toast.makeText(MainActivity.this, "The settings menu cannot be accessed while offline", Toast.LENGTH_LONG).show();
        }
    }

    public void newOrder(View view){
        if(ingredientList == null){
            Toast.makeText(this, "Ingredients have not been loaded yet", Toast.LENGTH_LONG).show();
            return;
        }
        Gson gson = new Gson();
        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        ArrayList<Ingredient> activeIngredients = new ArrayList<>(0);
        for(Ingredient i : ingredientList){
            if(i.isAvailable()) activeIngredients.add(i);
        }
        intent.putExtra("json", gson.toJson(activeIngredients));
        startActivityForResult(intent, NEW_ORDER_CONSTANT);
    }

    /*
    Was online
        Still online: update
        Not online: do nothing
    Wan't online
        now online: upload
        still offline: do nothing
     */
    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean wasOnline = Settings.getInstance().isOnline();
        Settings.getInstance().setOnline(this);
        if(Settings.getInstance().isOnline()){
            if(!wasOnline){
                NetworkHelper.uploadDatabase(this);
            }
        }else{
            // We are now offline, don't do anything
        }
    }

    public void refreshIngredientList(){
        if(NetworkHelper.isNetworkAvailable(this)) {
            ingredientList = Ingredient.downloadIngredients(this, "MainActivity");

        }
    }

    /* Broadcasting below */

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String method = intent.getStringExtra("method");
            if(method.equals("download")){
                refreshIngredientList();
            }else if(method.equals("refresh")){
                saveIngredientsToDb();
            }
        }
    };

    /* Only saves active ingredients, as those are the only ones that will be used in an order */
    private void saveIngredientsToDb() {
        PizzaDbHelper mDbHelper = new PizzaDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        mDbHelper.resetIngredient(db);
        for(Ingredient i : ingredientList) {
            if(i.isAvailable()) {
                ContentValues values = new ContentValues();
                values.put(IngredientEntry.COLUMN_NAME_NAME, i.getName());
                values.put(IngredientEntry.COLUMN_NAME_PRICE, i.getPrice().floatValue());
                values.put(IngredientEntry.COLUMN_NAME_TYPE, i.getType().getLower());
                values.put(IngredientEntry.COLUMN_NAME_TRUE_ID, i.getId());

                db.insert(
                        IngredientEntry.TABLE_NAME,
                        null,
                        values);
            }
        }
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("MainActivity"));
        super.onResume();
    }

    /* Permissions are below */

    private void getHTTPPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                        MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
            }
        }
    }

    private void getInternetPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_INTERNET);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Intent i = getIntent();
                    setResult(RESULT_CANCELED, i);
                    finish();
                }
            }

            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Intent i = getIntent();
                    setResult(RESULT_CANCELED, i);
                    finish();
                }
            }

        }
    }
}
