package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EmployeeLoginActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // the url for the login page on the server
    private static final String LOGIN_URL = "https://yourvoice123.000webhostapp.com/employee_login.php";

    // JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    // input fields
    EditText email_txt;
    EditText password_txt;

    int errors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        errors = 0;

        // define the buttons
        Button login_btn = (Button) findViewById(R.id.login_login_btn);

        // setup the text fields ( id and password )
        email_txt = (EditText) findViewById(R.id.login_email_txt);
        password_txt = (EditText) findViewById(R.id.login_password_txt);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // validate the id and the password
                if (email_txt.getText().toString().length() == 0) {
                    errors += 1;
                    email_txt.setError("غير صحيح");
                } else if (password_txt.getText().toString().length() == 0) {
                    errors += 1;
                    password_txt.setError("غير صحيح");
                }

                // check the value of the errors variable
                // if it equal to 0; then goto the next activity
                // else; show error messages

                if (errors > 0) {
                    // show error message
                    Toast.makeText(EmployeeLoginActivity.this,
                            "قم بتصحيح الأخطاء", Toast.LENGTH_LONG)
                            .show();

                    // set the errors variable to 0
                    errors = 0;
                } else {

                    // call the attemptLogin class
                    new AttemptLogin().execute();
                }
            }
        });
    }

    // AsyncTask is a seperate thread than the thread that runs the GUI
    // Any type of networking should be done with asynctask.
    class AttemptLogin extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EmployeeLoginActivity.this);
            pDialog.setMessage("جاري تسجيل الدخول");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;
            String email = email_txt.getText().toString();
            String password = password_txt.getText().toString();

            try {
                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                // getting member details by making HTTP request
                JSONObject json = JSONParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                Log.d("All User information : ", json.toString());

                if (success == 1) {
                    // Log.d("All User information : ", json.toString());

                    // make a controller object to save member information in it
                    Controller aController = (Controller) getApplicationContext();

                    // save the member information in the controller class
                    aController.setEmail(json.getString("email"));
                    aController.setPassword(json.getString("password"));
                    aController.setName(json.getString("name"));
                    aController.setMobile(json.getString("mobile"));
                    aController.setId(json.getString("id"));

                    Intent member_menu;

                    member_menu = new Intent(EmployeeLoginActivity.this,
                            EmployeeMenuActivity.class);

                    startActivity(member_menu);

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
            // dismiss the dialog
            pDialog.dismiss();

            // show successful updating message
            Toast.makeText(EmployeeLoginActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }

    // if the member click the back button
    @Override
    public void onBackPressed() {
        // goto the Main activity
        Intent in = new Intent(EmployeeLoginActivity.this,
                MainActivity.class);
        startActivity(in);
    }
}
