package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UserShowPlaceCompliantsActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<Compliants> compliantsList;

    CompliantAdapter adapter;

    // url to get all items list
    private static String url_compliants = "https://yourvoice123.000webhostapp.com/user_show_compliants.php";

    // JSON Node titles
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_COMPLIANTS = "compliants";

    private static final String TAG_COMPLIANTS_TITLE = "compliant_title";
    private static final String TAG_COMPLIANT_ID = "compliant_id";
    private static final String TAG_COMPLIANT_IMAGE = "compliant_image";

    ListView compliant_list;

    // compliants JSONArray
    JSONArray compliants = null;

    Controller aController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_compliants);

        compliant_list = (ListView) findViewById(R.id.compliants_lst);

        aController = (Controller) getApplicationContext();

        // Hashmap for ListView
        compliantsList = new ArrayList<Compliants>();

        new LoadAllCompliants().execute();
    }

    /**
     * Background Async Task to Load all items by making HTTP Request
     */
    class LoadAllCompliants extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserShowPlaceCompliantsActivity.this);
            pDialog.setMessage("جاري ارجاع بيانات الشكاوى");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All compliants from url
         */
        protected String doInBackground(String... args) {

            String user_id = aController.getId();
            String place_id = getIntent().getStringExtra("place_id");

            // Building Parameters
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("user_id", user_id);
            params.put("place_id", place_id);

            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_compliants, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("All compliants info : ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // items found
                    // Getting Array of items
                    compliants = json.getJSONArray(TAG_COMPLIANTS);

                    // looping through All items
                    for (int i = 0; i < compliants.length(); i++) {
                        JSONObject c = compliants.getJSONObject(i);

                        // Storing each json item in variable
                        String compliant_title = c.getString(TAG_COMPLIANTS_TITLE);
                        String compliant_id = c.getString(TAG_COMPLIANT_ID);
                        String compliant_image = c.getString(TAG_COMPLIANT_IMAGE);

                        Compliants compliant = new Compliants();

                        compliant.setName(compliant_title);
                        compliant.setId(compliant_id);
                        compliant.setImage("https://yourvoice123.000webhostapp.com/compliants/" + compliant_image);
                        compliantsList.add(compliant);
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
         * After completing background task Dismiss the progress dialog *
         */
        protected void onPostExecute(String message) {
            pDialog.dismiss();

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

            adapter = new CompliantAdapter(UserShowPlaceCompliantsActivity.this, R.layout.row, compliantsList);

            compliant_list.setAdapter(adapter);

            compliant_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String compliant_id = ((TextView) view.findViewById(R.id.tvId)).getText().toString();

                    // compliant details
                    Intent compliant_detail = new Intent(UserShowPlaceCompliantsActivity.this, UserShowPlaceCompliantActivity.class);

                    // sending title to next activity
                    compliant_detail.putExtra("compliant_id", compliant_id);
                    startActivity(compliant_detail);
                }
            });
        }
    }
}