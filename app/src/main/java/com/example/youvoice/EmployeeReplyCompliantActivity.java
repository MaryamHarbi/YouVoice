package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EmployeeReplyCompliantActivity extends Activity {
    Button send_btn;
    EditText reply_txt;

    int errors;

    // the compliant id from the previous activity
    String compliant_id = "";

    // the url for the add page on the server
    private static final String ADD_URL = "https://yourvoice123.000webhostapp.com/employee_reply_compliant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_reply_compliant);

        send_btn = (Button) findViewById(R.id.send_btn);

        reply_txt = (EditText) findViewById(R.id.reply_txt);

        // get compliant_id from previous activity
        compliant_id = getIntent().getStringExtra("compliant_id");

        errors = 0;

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reply_txt.getText().toString().length() == 0) {
                    errors += 1;
                    reply_txt.setError("غير صحيح");
                }

                // check the value of the errors variable
                // if it equal to 0; then goto the next activity
                // else; show error messages
                if (errors > 0) {
                    // show error message
                    Toast.makeText(EmployeeReplyCompliantActivity.this, "قم بتصليح الأخطاء", Toast.LENGTH_LONG).show();

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
            pDialog = new ProgressDialog(EmployeeReplyCompliantActivity.this);
            pDialog.setMessage("جاري الارسال");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;

            String reply = reply_txt.getText().toString();

            try {
                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("compliant_id", compliant_id);
                params.put("reply", reply);

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
            Toast.makeText(EmployeeReplyCompliantActivity.this, message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(EmployeeReplyCompliantActivity.this, EmployeeShowPlacesActivity.class);
            startActivity(intent);

        }
    }
}