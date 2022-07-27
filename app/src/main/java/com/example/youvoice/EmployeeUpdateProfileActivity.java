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
import java.util.regex.Pattern;

public class EmployeeUpdateProfileActivity extends Activity {
    Button employee_update_btn;
    EditText employee_name_txt, employee_email_txt, employee_password_txt, employee_mobile_txt;

    int errors;

    String EMAIL_REGEX;
//    String MOBILE_REGEX;

    Controller aController;

    // the url for the profile page on the server
    private static final String UPDATE_URL = "https://yourvoice123.000webhostapp.com/employee_update_profile.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_update_profile);

        employee_update_btn = (Button) findViewById(R.id.employee_profile_update_btn);

        employee_name_txt = (EditText) findViewById(R.id.employee_profile_name_txt);
        employee_email_txt = (EditText) findViewById(R.id.employee_profile_email_txt);
        employee_password_txt = (EditText) findViewById(R.id.employee_profile_password_txt);
        employee_mobile_txt = (EditText) findViewById(R.id.employee_profile_mobile_txt);

        /// get the values from the controller
        aController = (Controller) getApplicationContext();

        // get the employee information from the controller class
        employee_name_txt.setText(aController.getName());
        employee_email_txt.setText(aController.getEmail());
        employee_password_txt.setText(aController.getPassword());
        employee_mobile_txt.setText(aController.getMobile());

        errors = 0;

        // email pattern
        EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        employee_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validate the name
                if (employee_name_txt.getText().toString()
                        .length() == 0) {
                    errors += 1;
                    employee_name_txt
                            .setError("غير صحيح");
                }

                // validate the email
                else if (employee_email_txt.getText().toString()
                        .length() == 0
                        || !Pattern
                        .matches(EMAIL_REGEX,
                                employee_email_txt.getText()
                                        .toString())) {
                    errors += 1;
                    employee_email_txt.setError("غير صحيح");
                }

                // validate the password ( not less than six character )
                else if (employee_password_txt.getText().toString().length() == 0) {
                    errors += 1;
                    employee_password_txt
                            .setError("غير صحيح");
                }

                // validate the phone number
                else if (employee_mobile_txt.getText().toString()
                        .length() == 0
                        || employee_mobile_txt.getText().toString()
                        .length() > 10
                ) {
                    errors += 1;
                    employee_mobile_txt.setError("غير صحيح");
                }

                // check the value of the errors variable
                // if it equal to 0; then goto the next activity
                // else; show error messages
                if (errors > 0) {
                    // show error message
                    // Toast.makeText(UPDATEActivity.this,
                    // "Please Fix the error(s)", Toast.LENGTH_LONG)
                    // .show();

                    // set the errors variable to 0
                    errors = 0;
                } else {
//                    Toast.makeText(UserProfileActivity.this, String.valueOf(aController.getId()) + " " +  name + " " + email + " " + password + " " + id , Toast.LENGTH_SHORT).show();
                    new UPDATE().execute();
                }
            }
        });
    }

    class UPDATE extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EmployeeUpdateProfileActivity.this);
            pDialog.setMessage("جاري التحديث");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;

            String name = employee_name_txt.getText().toString();
            String email = employee_email_txt.getText().toString();
            String password = employee_password_txt.getText().toString();
            String mobile = employee_mobile_txt.getText().toString();

            try {
                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(aController.getId()));
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("mobile", mobile);

                // getting employee details by making HTTP request
                JSONObject json = JSONParser.makeHttpRequest(UPDATE_URL,
                        "POST", params);

                // json success tag
                success = json.getInt("success");

                if (success == 1) {
                    // set the new data for the employee
                    aController.setEmail(email);
                    aController.setPassword(password);
                    aController.setName(name);
                    aController.setMobile(mobile);

                    return json.getString("message");

                } else {
                    return json.getString("message");
                }

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
            Toast.makeText(EmployeeUpdateProfileActivity.this, message, Toast.LENGTH_LONG)
                    .show();

            // goto the  menu activity
            Intent menu = new Intent(EmployeeUpdateProfileActivity.this,
                    EmployeeMenuActivity.class);

            startActivity(menu);
        }
    }
}
