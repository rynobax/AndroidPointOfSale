package baxley.ryan.pizzasalesystem.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Asynchronous task for sending an http post request
 */
public class PostWebpageTask extends AsyncTask<String, Void, String> {
    public JSONObject jsonParams = new JSONObject();

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                // Starts the query
                conn.connect();

                DataOutputStream printout = new DataOutputStream(conn.getOutputStream ());
                String str = jsonParams.toString();
                Log.v("LOG", "str: " + str);
                byte[] data=str.getBytes("UTF-8");
                Log.v("LOG", "data: " + data.toString());
                printout.write(data);
                printout.flush();
                printout.close();

                int response = conn.getResponseCode();
                Log.d("LOG", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = "Error parsing response (It's probably blank)";
                try {
                    contentAsString = new Scanner(is).useDelimiter("\\A").next();
                    Log.v("LOG", "We got: " + contentAsString);
                } catch (java.util.NoSuchElementException e){
                    Log.v("LOG", "NoSuchElementException when parsing response");
                }
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }
}