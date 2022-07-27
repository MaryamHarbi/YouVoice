package com.example.youvoice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UserAddPlaceCompliantActivity extends Activity {
    Button send_btn, img_btn;
    EditText title_txt, content_txt;

    int errors;

    Controller aController;

    String filePath, file_name = "";

    private ProgressDialog pDialog;

    private int serverResponseCode = 0;
    private String upLoadServerUri = "https://yourvoice123.000webhostapp.com/UploadToServer.php";

    // the place id from the previous activity
    String place_id = "";

    // the url for the add page on the server
    private static final String ADD_URL = "https://yourvoice123.000webhostapp.com/user_add_place_compliant.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_place_compliant);

        send_btn = (Button) findViewById(R.id.send_btn);
        img_btn = (Button) findViewById(R.id.img_btn);

        title_txt = (EditText) findViewById(R.id.title_txt);
        content_txt = (EditText) findViewById(R.id.content_txt);

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

                // validate the content
                else if (content_txt.getText().toString().length() == 0) {
                    errors += 1;
                    content_txt.setError("غير صحيح");
                }

                // check the value of the errors variable
                // if it equal to 0; then goto the next activity
                // else; show error messages
                if (errors > 0 || file_name.equals("")) {
                    // show error message
                    Toast.makeText(UserAddPlaceCompliantActivity.this, "تأكد من جميع المدخلات وملف الصورة", Toast.LENGTH_LONG).show();

                    // set the errors variable to 0
                    errors = 0;
                } else {
                    pDialog = ProgressDialog.show(UserAddPlaceCompliantActivity.this,
                            "", "انتظر من أجل تحميل ملف الصورة ", true);
                    new Thread(new Runnable() {
                        public void run() {
                            uploadFile(filePath);
                        }
                    }).start();
                }
            }
        });

        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "حدد الملف : "), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();   // get the file uri
            filePath = getPath(selectedFileUri);    // get the path of the uri
            file_name = filePath.substring(filePath.lastIndexOf("/") + 1);  // get the name of the file

//            Toast.makeText(UserAddPlaceCompliantActivity.this, filePath, Toast.LENGTH_SHORT).show();
//            Toast.makeText(UserAddPlaceCompliantActivity.this, file_name, Toast.LENGTH_SHORT).show();
        }
    }

    // get the path of the file from uri
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public int uploadFile(String sourceFileUri) {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(filePath);

        if (!sourceFile.isFile()) {

            pDialog.dismiss();

            Log.e("uploadFile", "الملف غير موجود :" + sourceFileUri);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(UserAddPlaceCompliantActivity.this, "الملف غير موجود", Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(
                        sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", sourceFileUri);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + sourceFileUri + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pDialog.dismiss();
                            new Add().execute();
                            Toast.makeText(UserAddPlaceCompliantActivity.this, "تم رفع الصورة بنجاح", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                pDialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UserAddPlaceCompliantActivity.this, "خطأ في رفع الملف", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                pDialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UserAddPlaceCompliantActivity.this, "خطأ في الرفع", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }

            pDialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    class Add extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserAddPlaceCompliantActivity.this);
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
            String content = content_txt.getText().toString();
            String img = file_name;

            try {
                // Building Parameters
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_id", String.valueOf(aController.getId()));
                params.put("place_id", place_id);
                params.put("title", title);
                params.put("content", content);
                params.put("img", img);

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
            Toast.makeText(UserAddPlaceCompliantActivity.this, message, Toast.LENGTH_LONG).show();

            // goto the compliants activity
            Intent in = new Intent(UserAddPlaceCompliantActivity.this, UserShowPlaceMenuActivity.class);
            in.putExtra("place_id", place_id);
            startActivity(in);
        }
    }
}
