package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EmployeeShowPlaceCompliantActivity extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // the url for the compliant page on the server
    private static final String ITEM_URL = "https://yourvoice123.000webhostapp.com/employee_show_compliant_info.php";

    // JSON element ids from repsonse of php script:
    private static final String TAG_TITLE = "compliant_title";
    private static final String TAG_CONTENT = "compliant_content";
    private static final String TAG_REPLY = "compliant_reply";
    private static final String TAG_DATE = "compliant_date";
    private static final String TAG_IMAGE = "compliant_image";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    TextView compliant_title;
    TextView compliant_content;
    TextView compliant_reply;
    TextView compliant_date;

    String reply;
    String title;
    String content;
    String date;
    String image;

    WebView webView;

    String compliant_id;

    Button reply_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_show_compliant);

        reply_btn = (Button) findViewById(R.id.reply_btn);

        webView = (WebView) findViewById(R.id.my_webview);
        webView.setWebViewClient(new WebViewClient());
//        webView.addView(webView.se getZoomControls());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        compliant_id = getIntent().getStringExtra("compliant_id");

        compliant_reply = (TextView) findViewById(R.id.compliant_reply_txt);
        compliant_title = (TextView) findViewById(R.id.compliant_title_txt);
        compliant_content = (TextView) findViewById(R.id.compliant_content_txt);
        compliant_date = (TextView) findViewById(R.id.compliant_date_txt);
        compliant_reply = (TextView) findViewById(R.id.compliant_reply_txt);

        // connect to the server and get the compliant details
        new AttemptGetInfo().execute();

        // add to cart button
        reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(EmployeeShowPlaceCompliantActivity.this, EmployeeReplyCompliantActivity.class);
                intent.putExtra("compliant_id", compliant_id);
                startActivity(intent);
            }
        });

        Button share_btn = (Button) findViewById(R.id.share_btn);

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String share = "شكوى المكان\n" +
                        "العنوان : " + compliant_title.getText().toString() + "\n" +
                        "المحتوى : " + compliant_content.getText().toString() + "\n" +
                        "التاريخ : " + compliant_date.getText().toString() + "\n" +
                        "الرد : " + compliant_reply.getText().toString();

                // share intent
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "مشاركة");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                startActivity(Intent.createChooser(sharingIntent, "Sharing"));
            }
        });
    }

    // AsyncTask is a seperate thread than the thread that runs the GUI
    // Any type of networking should be done with asynctask.
    class AttemptGetInfo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EmployeeShowPlaceCompliantActivity.this);
            pDialog.setMessage("جاري ارجاع بيانات الشكوى ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // Check for success tag
            int success;

            try {
                // get the compliant title from the prev intent
                Intent intent = getIntent();

                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("compliant_id", compliant_id);

                // getting compliant details by making HTTP request
                JSONObject json = JSONParser.makeHttpRequest(ITEM_URL, "POST", params);

                // json success tag
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    Log.d("Compliant information: ", json.toString());

                    // set the vars to the returned server data
                    reply = json.getString(TAG_REPLY);
                    content = json.getString(TAG_CONTENT);
                    title = json.getString(TAG_TITLE);
                    date = json.getString(TAG_DATE);
                    image = json.getString(TAG_IMAGE);

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

            // set the compliant texts with the data from server
            compliant_reply.setText(reply);
            compliant_content.setText(content);
            compliant_title.setText(title);
            compliant_date.setText(date);
            webView.loadUrl("https://yourvoice123.000webhostapp.com/compliants/" + image);
        }
    }
}
