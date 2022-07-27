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

public class EmployeeShowPlaceEvaluationsActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<HashMap<String, String>> evaluationsList;

    // the place id from the previous activity
    String place_id = "";

    // url to get all evaluations list
    private static String url_evaluations = "https://yourvoice123.000webhostapp.com/user_show_place_evaluations.php";

    // JSON Node titles
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_EVALUATIONS = "evaluations";

    private static final String TAG_EVALUATION_TITLE = "evaluation_title";
    private static final String TAG_EVALUATION_NOTES = "evaluation_notes";
    private static final String TAG_EVALUATION_RESULT = "evaluation_result";
    private static final String TAG_EVALUATION_DATE = "evaluation_date";
    private static final String TAG_EVALUATION_USER = "evaluation_user";
    private static final String TAG_EVALUATION_ID = "evaluation_id";

    ListView evaluation_list;

    // evaluations JSONArray
    JSONArray evaluations = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_show_place_evaluations);

        // Hashmap for ListView
        evaluationsList = new ArrayList<HashMap<String, String>>();

        // get place_id from previous activity
        place_id = getIntent().getStringExtra("place_id");

        // LoadAllEvaluations in Background Thread
        new LoadAllEvaluations().execute();
    }

    /**
     * Background Async Task to Load all items by making HTTP Request
     */
    class LoadAllEvaluations extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EmployeeShowPlaceEvaluationsActivity.this);
            pDialog.setMessage("جاري ارجاع التقييمات للمكان ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         */
        protected String doInBackground(String... args) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("place_id", place_id);

            // getting JSON string from URL
            JSONObject json = JSONParser.makeHttpRequest(url_evaluations, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All evaluations info : ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // Getting Array of items
                    evaluations = json.getJSONArray(TAG_EVALUATIONS);

                    // looping through All items
                    for (int i = 0; i < evaluations.length(); i++) {
                        JSONObject c = evaluations.getJSONObject(i);

                        // Storing each json item in variable
                        String evaluation_title = c.getString(TAG_EVALUATION_TITLE);
                        String evaluation_notes = c.getString(TAG_EVALUATION_NOTES);
                        String evaluation_result = c.getString(TAG_EVALUATION_RESULT);
                        String evaluation_date = c.getString(TAG_EVALUATION_DATE);
                        String evaluation_user = c.getString(TAG_EVALUATION_USER);
                        String evaluation_id = c.getString(TAG_EVALUATION_ID);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_EVALUATION_TITLE, "العنوان : " + evaluation_title);
                        map.put(TAG_EVALUATION_NOTES, "الملاحظات : " + evaluation_notes);
                        map.put(TAG_EVALUATION_RESULT, "التقييم : " + evaluation_result);
                        map.put(TAG_EVALUATION_DATE, "التاريخ : " + evaluation_date);
                        map.put(TAG_EVALUATION_USER, "المستخدم : " + evaluation_user);
                        map.put(TAG_EVALUATION_ID, evaluation_id);

                        // adding HashList to ArrayList
                        evaluationsList.add(map);
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
            Toast.makeText(EmployeeShowPlaceEvaluationsActivity.this, message, Toast.LENGTH_LONG).show();

            // make list adapter to fill the evaluation list
            ListAdapter adapter = new SimpleAdapter(EmployeeShowPlaceEvaluationsActivity.this, evaluationsList, R.layout.evaluations_list, new String[]{
                    TAG_EVALUATION_TITLE, TAG_EVALUATION_NOTES, TAG_EVALUATION_RESULT, TAG_EVALUATION_DATE, TAG_EVALUATION_USER, TAG_EVALUATION_ID}, new int[]{
                    R.id.evaluation_title, R.id.evaluation_notes, R.id.evaluation_result, R.id.evaluation_date, R.id.evaluation_user, R.id.evaluation_id}
            );

            // Get listview
            evaluation_list = (ListView) findViewById(R.id.evaluations_lst);

            evaluation_list.setAdapter(adapter);

            // share title and content when click on the item
            evaluation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String share = "تقييم المكان\n" + ((TextView) view.findViewById(R.id.evaluation_title)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.evaluation_notes)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.evaluation_result)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.evaluation_date)).getText().toString() + "\n" +
                            ((TextView) view.findViewById(R.id.evaluation_user)).getText().toString();

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
        Intent in = new Intent(EmployeeShowPlaceEvaluationsActivity.this, EmployeeShowPlaceMenuActivity.class);
        in.putExtra("place_id", place_id);
        startActivity(in);
    }
}