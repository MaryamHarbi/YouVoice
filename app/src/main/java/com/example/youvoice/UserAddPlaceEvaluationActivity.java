package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

public class UserAddPlaceEvaluationActivity extends Activity {
    Button send_btn;
    EditText title_txt, notes_txt;
    RatingBar resultBar;

    int errors;

    Controller aController;

    // the place id from the previous activity
    String place_id = "";

    // the url for the add page on the server
    private static final String ADD_URL = "https://yourvoice123.000webhostapp.com/user_add_place_evaluation.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_place_evaluation);

        send_btn = (Button) findViewById(R.id.send_btn);

        title_txt = (EditText) findViewById(R.id.title_txt);
        notes_txt = (EditText) findViewById(R.id.notes_txt);
        resultBar = (RatingBar) findViewById(R.id.resultBar);

        // get place_id from previous activity
        place_id = getIntent().getStringExtra("place_id");

        // get the values from the controller
        aController = (Controller) getApplicationContext();

        errors = 0;

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validate the title
                if (title_txt.getText().toString().length() == 0) {
                    errors += 1;
                    title_txt.setError("غير صحيح ");
                }

                // validate the notes
                else if (notes_txt.getText().toString().length() == 0) {
                    errors += 1;
                    notes_txt.setError("غير صحيح");
                }

                // validate the result
                else if (resultBar.getRating() == 0) {
                    errors += 1;
                }

                // check the value of the errors variable
                // if it equal to 0; then goto the next activity
                // else; show error messages
                if (errors > 0) {
                    // show error message
                    Toast.makeText(UserAddPlaceEvaluationActivity.this, "قم بتصليح الأخطاء واختيار تقييم", Toast.LENGTH_LONG).show();

                    // set the errors variable to 0
                    errors = 0;
                } else {
                    new Add().execute();
                }
            }
        });
    }

    class Add extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserAddPlaceEvaluationActivity.this);
            pDialog.setMessage("جاري الارسال");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;

            String title = title_txt.getText().toString();
            String notes = notes_txt.getText().toString();
            String result = String.valueOf(resultBar.getRating());

            try {
                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(aController.getId()));
                params.put("place_id", place_id);
                params.put("title", title);
                params.put("notes", notes);
                params.put("result", result);

                // HTTP request for the server
                JSONObject json = JSONParser.makeHttpRequest(ADD_URL, "GET", params);

                return json.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        // After completing background task Dismiss the progress dialog
        protected void onPostExecute(String message) {
            // dismiss the dialog
            pDialog.dismiss();

            // show the returned message from the server
            Toast.makeText(UserAddPlaceEvaluationActivity.this, message, Toast.LENGTH_LONG).show();

            // goto the evaluations activity
            Intent in = new Intent(UserAddPlaceEvaluationActivity.this, UserShowPlaceEvaluationsActivity.class);
            in.putExtra("place_id", place_id);
            startActivity(in);
        }
    }
}
