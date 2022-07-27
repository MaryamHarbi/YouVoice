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

public class UserRegisterActivity extends Activity {
    Button user_register_btn;
    EditText user_name_txt, user_email_txt, user_password_txt, user_mobile_txt;

    int errors;

    String EMAIL_REGEX;

    // the url for the login page on the server
    private static final String Register_URL = "https://yourvoice123.000webhostapp.com/user_register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        user_register_btn = (Button) findViewById(R.id.register_btn);
        user_name_txt = (EditText) findViewById(R.id.name_txt);
        user_email_txt = (EditText) findViewById(R.id.email_txt);
        user_password_txt = (EditText) findViewById(R.id.password_txt);
        user_mobile_txt = (EditText) findViewById(R.id.mobile_txt);

        errors = 0;

        // email pattern
        EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        user_register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // validate the name
                if (user_name_txt.getText().toString()
                        .length() == 0) {
                    errors += 1;
                    user_name_txt
                            .setError("غير صحيح");
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
                        .length() > 10) {
                    errors += 1;
                    user_mobile_txt
                            .setError("غير صحيح");
                }

                // check the value of the errors variable
                // if it equal to 0; then goto the next activity
                // else; show error messages
                if (errors > 0) {
                    // show error message
                    // Toast.makeText(RegisterActivity.this,
                    // "Please Fix the error(s)", Toast.LENGTH_LONG)
                    // .show();

                    // set the errors variable to 0
                    errors = 0;
                } else {

                    new Register().execute();
                }
            }
        });
    }

    class Register extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserRegisterActivity.this);
            pDialog.setMessage("جاري التسجيل");
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
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("mobile", mobile);

                // getting user details by making HTTP request
                JSONObject json = JSONParser.makeHttpRequest(Register_URL,
                        "POST", params);

                // json success tag
                success = json.getInt("success");

                if (success == 1) {
                    // go to the login activity
                    Intent user_login = new Intent(UserRegisterActivity.this,
                            UserLoginActivity.class);
                    startActivity(user_login);

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
            Toast.makeText(UserRegisterActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}