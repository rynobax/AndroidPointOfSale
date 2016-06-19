package baxley.ryan.pizzasalesystem.activities.admin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.activities.MainActivity;
import baxley.ryan.pizzasalesystem.activities.admin.ingredients.ManageIngredientsActivity;
import baxley.ryan.pizzasalesystem.helpers.NetworkHelper;

/**
 * Activity for displaying the other various functions of the app
 */
public class SettingsActivity extends AppCompatActivity {
    public static final int MANAGE_INGREDIENTS_CONSTANT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void ingredients(View view){
        startActivityForResult(new Intent(SettingsActivity.this, ManageIngredientsActivity.class), MANAGE_INGREDIENTS_CONSTANT);
    }

    public void preferences(View view){
        startActivity(new Intent(SettingsActivity.this, PreferenceActivity.class));
    }

    public void pastOrders(View view){
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
        intent = new Intent(SettingsActivity.this, PastOrdersActivity.class);
        intent.putExtra("json", json);
        startActivity(intent);
    }

    public void infoAction(View view){
        startActivity(new Intent(SettingsActivity.this, InfoActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MANAGE_INGREDIENTS_CONSTANT){
            Intent intent = new Intent("MainActivity");
            intent.putExtra("method", "download");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
