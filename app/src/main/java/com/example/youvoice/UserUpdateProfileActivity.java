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

public class UserUpdateProfileActivity extends Activity {
    Button user_update_btn;
    EditText user_name_txt, user_email_txt, user_password_txt, user_mobile_txt;

    int errors;

    String EMAIL_REGEX;
//    String MOBILE_REGEX;

    Controller aController;

    // the url for the profile page on the server
    private static final String UPDATE_URL = "https://yourvoice123.000webhostapp.com/user_update_profile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update_profile);

        user_update_btn = (Button) findViewById(R.id.user_profile_update_btn);

        user_name_txt = (EditText) findViewById(R.id.user_profile_name_txt);
        user_email_txt = (EditText) findViewById(R.id.user_profile_email_txt);
        user_password_txt = (EditText) findViewById(R.id.user_profile_password_txt);
        user_mobile_txt = (EditText) findViewById(R.id.user_profile_mobile_txt);

        /// get the values from the controller
        aController = (Controller) getApplicationContext();

        // get the user information from the controller class
        user_name_txt.setText(aController.getName());
        user_email_txt.setText(aController.getEmail());
        user_password_txt.setText(aController.getPassword());
        user_mobile_txt.setText(aController.getMobile());

        errors = 0;

        // email pattern
        EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        user_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validate the name
                if (user_name_txt.getText().toString()
                        .length() == 0) {
                    errors += 1;
                    user_name_txt
                            .setError("غير صحيح ");
                }

                // validate the email
                else if (user_email_txt.getText().toString()
                        .length() == 0
                        || !Pattern
                        .matches(EMAIL_REGEX,
                                user_email_txt.getText()
                                        .toString())) {
                    errors += 1;
                    user_email_txt.setError("غير صحيح");
                }

                // validate the password ( not less than six character )
                else if (user_password_txt.getText().toString().length() == 0) {
                    errors += 1;
                    user_password_txt
                            .setError("غير صحيح");
                }

                // validate the phone number
                else if (user_mobile_txt.getText().toString()
                        .length() == 0
                        || user_mobile_txt.getText().toString()
                        .length() > 10
                ) {
                    errors += 1;
                    user_mobile_txt
                            .setError("غير صحيح");
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
            pDialog = new ProgressDialog(UserUpdateProfileActivity.this);
            pDialog.setMessage("جاري التحديث");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;

            String name = user_name_txt.getText().toString();
            String email = user_email_txt.getText().toString();
            String password = user_password_txt.getText().toString();
            String mobile = user_mobile_txt.getText().toString();

            try {
                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(aController.getId()));
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("mobile", mobile);

                // getting user details by making HTTP request
                JSONObject json = JSONParser.makeHttpRequest(UPDATE_URL,
                        "POST", params);

                // json success tag
                success = json.getInt("success");

                if (success == 1) {
                    // set the new data for the user
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
            Toast.makeText(UserUpdateProfileActivity.this, message, Toast.LENGTH_LONG)
                    .show();

            // goto the  menu activity
            Intent menu = new Intent(UserUpdateProfileActivity.this,
                    UserMenuActivity.class);

            startActivity(menu);
        }
    }
}
