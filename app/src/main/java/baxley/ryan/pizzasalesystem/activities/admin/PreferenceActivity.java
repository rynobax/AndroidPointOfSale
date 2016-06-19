package baxley.ryan.pizzasalesystem.activities.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.math.BigDecimal;

import baxley.ryan.pizzasalesystem.R;
import baxley.ryan.pizzasalesystem.helpers.Settings;
/**
 * Activity for editing user preferences, like sales tax
 */
public class PreferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        EditText saleTax = (EditText) findViewById(R.id.salesTaxText);
        saleTax.setText(Settings.getInstance().getSalesTax(this).toString());
    }

    public void saveSettings(View view){
        EditText saleTax = (EditText) findViewById(R.id.salesTaxText);
        String s = saleTax.getText().toString();
        Settings.getInstance().setSalesTax(new BigDecimal(s), this);
        finish();
    }
}
