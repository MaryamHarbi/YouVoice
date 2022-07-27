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

public class EmployeeShowPlaceSuggestionsActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> suggestionsList;

    // the place id from the previous activity
    String place_id = "";

    // url to get all suggestions list
    private static String url_suggestions = "https://yourvoice123.000webhostapp.com/user_show_place_suggestions.php";

    // JSON Node titles
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUGGESTIONS = "suggestions";

    private static final String TAG_SUGGESTION_TITLE = "suggestion_title";
    private static final String TAG_SUGGESTION_CONTENT = "suggestion_content";
    private static final String TAG_SUGGESTION_DATE = "suggestion_date";
    private static final String TAG_SUGGESTION_USER = "suggestion_user";
    private static final String TAG_SUGGESTION_ID = "suggestion_id";

    ListView suggestion_list;

    // suggestions JSONArray
    JSONArray suggestions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_show_place_suggestions);

        // Hashmap for ListView
        suggestionsList = new ArrayList<HashMap<String, String>>();

        // get place_id from previous activity
        place_id = getIntent().getStringExtra("place_id");

        // LoadAllSuggestions in Background Thread
        new LoadAllSuggestions().execute();
    }

    /**
     * Background Async Task to Load all items by making HTTP Request
     */
    class LoadAllSuggestions extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EmployeeShowPlaceSuggestionsActivity.this);
            pDialog.setMessage("جاري ارجاع الاقتراحات للمكان ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All suggestions from url
         */
        protected String doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("place_id", place_id);

            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_suggestions, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All suggestions info : ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of items
                    suggestions = json.getJSONArray(TAG_SUGGESTIONS);

                    // looping through All items
                    for (int i = 0; i < suggestions.length(); i++) {
                        JSONObject c = suggestions.getJSONObject(i);

                        // Storing each json item in variable
                        String suggestion_title = c.getString(TAG_SUGGESTION_TITLE);
                        String suggestion_content = c.getString(TAG_SUGGESTION_CONTENT);
                        String suggestion_date = c.getString(TAG_SUGGESTION_DATE);
                        String suggestion_user = c.getString(TAG_SUGGESTION_USER);
                        String suggestion_id = c.getString(TAG_SUGGESTION_ID);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_SUGGESTION_TITLE, "العنوان : " + suggestion_title);
                        map.put(TAG_SUGGESTION_CONTENT, "المحتوى : " + suggestion_content);
                        map.put(TAG_SUGGESTION_DATE, "التاريخ : " + suggestion_date);
                        map.put(TAG_SUGGESTION_USER, "المستخدم : " + suggestion_user);
                        map.put(TAG_SUGGESTION_ID, suggestion_id);

                        // adding HashList to ArrayList
                        suggestionsList.add(map);
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
            Toast.makeText(EmployeeShowPlaceSuggestionsActivity.this, message, Toast.LENGTH_LONG).show();

            // make list adapter to fill the suggestion list
            ListAdapter adapter = new SimpleAdapter(EmployeeShowPlaceSuggestionsActivity.this, suggestionsList, R.layout.suggestions_list, new String[]{
                    TAG_SUGGESTION_TITLE, TAG_SUGGESTION_CONTENT, TAG_SUGGESTION_DATE, TAG_SUGGESTION_USER, TAG_SUGGESTION_ID}, new int[]{
                    R.id.suggestion_title, R.id.suggestion_content, R.id.suggestion_date, R.id.suggestion_user, R.id.suggestion_id}
            );

            // Get listview
            suggestion_list = (ListView) findViewById(R.id.suggestions_lst);

            suggestion_list.setAdapter(adapter);

            // share title and content when click on the item
            suggestion_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String share = "إقتراح المكان\n" + ((TextView) view.findViewById(R.id.suggestion_title)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.suggestion_content)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.suggestion_date)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.suggestion_user)).getText().toString();

                    // share intent
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "مشاركة");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                    startActivity(Intent.createChooser(sharingIntent, "Sharing"));
                }
            });
        }
    }

    // if the user click the back button
    @Override
    public void onBackPressed() {
        // return to the place menu activity
        // pass the place_id to the activity
        Intent in = new Intent(EmployeeShowPlaceSuggestionsActivity.this, EmployeeShowPlaceMenuActivity.class);
        in.putExtra("place_id", place_id);
        startActivity(in);
    }
}