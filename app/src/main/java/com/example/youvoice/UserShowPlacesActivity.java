package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserShowPlacesActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> placesList;

    // url to get all places list
    private static String url_places = "https://yourvoice123.000webhostapp.com/user_show_places.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_PLACES = "places";

    private static final String TAG_PLACE_NAME = "place_name";
    private static final String TAG_PLACE_EVALUATION = "place_evaluation";
    private static final String TAG_PLACE_ID = "place_id";

    ListView place_list;

    // places JSONArray
    JSONArray places = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_places);

        // Hashmap for ListView
        placesList = new ArrayList<HashMap<String, String>>();

        // LoadAllPlaces in Background Thread
        new LoadAllPlaces().execute();
    }

    /**
     * Background Async Task to Load all items by making HTTP Request
     */
    class LoadAllPlaces extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserShowPlacesActivity.this);
            pDialog.setMessage("ارجاع الأماكن التجارية ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All places from url
         */
        protected String doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("test", "test");

            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_places, "GET",
                    params);

            // Check your log cat for JSON reponse
            Log.d("All places info : ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of items
                    places = json.getJSONArray(TAG_PLACES);

                    // looping through All items
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject c = places.getJSONObject(i);

                        // Storing each json item in variable
                        String place_name = c.getString(TAG_PLACE_NAME);
                        String place_evaluation = c.getString(TAG_PLACE_EVALUATION);
                        String place_id = c.getString(TAG_PLACE_ID);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PLACE_NAME, place_name);
                        map.put(TAG_PLACE_EVALUATION, "مجموع التقييمات : " + place_evaluation);
                        map.put(TAG_PLACE_ID, place_id);

                        // adding HashList to ArrayList
                        placesList.add(map);
                    }
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String message) {
            // dismiss the dialog
            pDialog.dismiss();

            // show the returned message from the server
            Toast.makeText(UserShowPlacesActivity.this, message, Toast.LENGTH_LONG).show();

            // make list adapter to fill the place list
            ListAdapter adapter = new SimpleAdapter(UserShowPlacesActivity.this,
                    placesList, R.layout.places_list, new String[]{
                    TAG_PLACE_NAME, TAG_PLACE_EVALUATION, TAG_PLACE_ID
            }, new int[]{R.id.place_name, R.id.place_evaluation, R.id.place_id});

            // Get listview
            place_list = (ListView) findViewById(R.id.places_lst);

            place_list.setAdapter(adapter);

            place_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // getting values from selected ListItem
                    String place_id = ((TextView) view.findViewById(R.id.place_id)).getText().toString();

                    // open the place menu activity
                    Intent place_menu = new Intent(UserShowPlacesActivity.this, UserShowPlaceMenuActivity.class);

                    // pass the place_id to the next intent
                    place_menu.putExtra("place_id", place_id);

                    startActivity(place_menu);
                }
            });
        }
    }

    // if the user click the back button
    @Override
    public void onBackPressed() {
        Intent in = new Intent(UserShowPlacesActivity.this, UserMenuActivity.class);
        startActivity(in);
    }
}