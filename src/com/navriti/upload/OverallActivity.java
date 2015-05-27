
package com.navriti.upload;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.certiplate.R;
import com.navriti.database.*;
import com.navriti.parserclass.*;
import com.navriti.skillahead.HomeActivity;
import com.navriti.skillahead.SyncActivity;
public class OverallActivity extends HomeActivity {
    // Globals
    ProgressDialog progressbar = null;

    int uploaded_number = 0;

    int percentage = 0;

    int number_of_files = 0;

    String filepath = "";

    String filename = "";

    String imeinumber = "";

    SqliteDbhelper sqliteDbhelper;

    String s = "";

    String staticdata = "";

    Handler handler;

    boolean upload_status = true;

    String exception = "";

    String toastdata = "";

    public static String Upload_URL = "";

    AssetManager assetManager;

    TextView videostatusTextView, xmlstatusTextView, acknowTextView;

    ImageView videostatusImageView, xmlstatusImageView, acknowstatusImageView;

    String tabresonseXmlFilename = "";
    
    ActionBar actionbar;

    // videos list from xml
    ArrayList<String> videofilenameList = new ArrayList<String>();

    final private static int DIALOG_UPLOAD = 1;

    Context context;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        actionbar = getActionBar();
		actionbar.setTitle("Sync All");
        setContentView(R.layout.uploadvideos);
        context = this;
        assetManager = getAssets();
        if (Upload_URL.length() <= 0) {
            Upload_URL = getResources().getString(R.string.Upload_URL);
        }
        sqliteDbhelper = new SqliteDbhelper(this);
        // sqliteDbhelper.updateallVideoStaus();
        handler = new Handler();
        // Fetching imei number
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imeinumber = tm.getDeviceId();
        // Display dialog on oncreate
        
        showDialog(DIALOG_UPLOAD);
    }

    
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialogDetails = null;
        switch (id) {
            case DIALOG_UPLOAD:
                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogview = inflater.inflate(R.layout.dialoguploads, null);
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
                dialogbuilder.setCancelable(false);
                dialogbuilder.setTitle("Synchronize Status");
                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();
                break;
        }
        return dialogDetails;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_UPLOAD:
                final AlertDialog alertDialog = (AlertDialog)dialog;
                videostatusTextView = (TextView)alertDialog.findViewById(R.id.videostatus_textView);
                xmlstatusTextView = (TextView)alertDialog.findViewById(R.id.response_textView);
                acknowTextView = (TextView)alertDialog.findViewById(R.id.acknowledgement_textView1);
                videostatusImageView = (ImageView)alertDialog.findViewById(R.id.video_imageView1);
                xmlstatusImageView = (ImageView)alertDialog.findViewById(R.id.response_imageView2);
                acknowstatusImageView = (ImageView)alertDialog.findViewById(R.id.ack_imageView1);
                final ImageView exitImageView = (ImageView)alertDialog.findViewById(R.id.exit_imageView);
                exitImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        alertDialog.dismiss();
                        onBackPressed();
                    }
                });
                Cursor cursor = sqliteDbhelper.getUploadStatus();
                if (cursor.moveToNext()) {
                    String videosUploadStatus = cursor.getString(0);
                    String xmlUploadStatus = cursor.getString(1);
                    String ackUploadStatus = cursor.getString(2);
                    if (videosUploadStatus.equals("DONE")) {
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        videostatusTextView.setText("Videos uploaded");
                        videostatusTextView.setTextColor(android.graphics.Color.GREEN);
                        videostatusImageView.setImageDrawable(trueDrawable);
                    }
                    if (xmlUploadStatus.equals("DONE")) {
                        // change the xmlstatus textview color and icon
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        xmlstatusTextView.setTextColor(android.graphics.Color.GREEN);
                        xmlstatusImageView.setImageDrawable(trueDrawable);
                    }
                    if (ackUploadStatus.equals("DONE")) {
                        // change the acknowledge textview color and icon
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        acknowTextView.setTextColor(android.graphics.Color.GREEN);
                        acknowstatusImageView.setImageDrawable(trueDrawable);
                    }
                    if (!videosUploadStatus.equalsIgnoreCase("DONE")) {
                        uploadVideos();
                    } else if (!xmlUploadStatus.equalsIgnoreCase("DONE")) {
                        // upload xml
                        uploadResponse();
                    } else if (!ackUploadStatus.equalsIgnoreCase("DONE")) {
                        String xmlFilename = cursor.getString(3);
                        // upload ack
                        FileNameWebservice fileNameWebservice = new FileNameWebservice();
                        fileNameWebservice.execute(new String[] {
                            xmlFilename
                        });
                    }
                } else {
                    displayToast("no upload status info");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),SyncActivity.class));
    }

    // uploading videos
    public class Upload extends AsyncTask<String, Void, String> {
        //String filename;
    	
    	/*
    	 public void Upload(String filename){
    	 this.filename = filename;
    	 }
    	 */
    	
    	@Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            try {
                File dir = new File(getResources().getString(R.string.sdcardpath));
                String[] filelist = dir.list();
                if (filelist.length == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "No videos available for upload in SDCARD.", Toast.LENGTH_LONG).show();
                            progressbar.dismiss();
                        }
                    });
                } else {
                    for (int i = 0; i < videofilenameList.size(); i++) {
                        filename = videofilenameList.get(i);
                        if (Arrays.asList(filelist).contains(filename)) {
                            String video_upload_status = sqliteDbhelper.getvideodetail(filename);
                            Log.i("video file statussssssssssssssssssssssss", video_upload_status);
                            if (!video_upload_status.equals("Y")) {
                                filepath = getResources().getString(R.string.sdcardpath) + filename;
                                Log.i("filenameeeeeeeeeeeeeeeeeee", "uploading file name" + filename);
                                callingServlet();
                            } else {
                            }
                        } else {
                            // file not found in sdcard
                            sqliteDbhelper.updateVideoStatus(filename, "NOTAVAILABLE");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    Toast.makeText(getApplicationContext(), "video missing from SDCARD", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (percentage >= 100) {
                uploaded_number = 0;
                // all videos uploaded
                sqliteDbhelper.updateVideoUploadStatus("DONE");
                Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                videostatusTextView.setText("Videos uploaded");
                videostatusTextView.setTextColor(android.graphics.Color.GREEN);
                videostatusImageView.setImageDrawable(trueDrawable);
                // uploading response xml
                uploadResponse();
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings({
        "deprecation"
    })
    public void callingServlet() {
        java.lang.System.gc();
        File uploadFile = new File(filepath);
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 100 * 1024;
        String urlString = Upload_URL;
        try {
            // ------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(filepath));
            // open a URL connection to the Servlet
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection)url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            // File size
            double bytes = uploadFile.length();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("fileName", uploadFile.getName());
            conn.setRequestProperty("fileSize", bytes + "");
            conn.setRequestProperty("Content-Type", "video/mp4");
         
            // OutputStream outputStream = conn.getOutputStream();
            dos = new DataOutputStream(conn.getOutputStream());
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
            // close streams
            Log.e("Debug", "File is written");
            fileInputStream.close();
            // outputStream.close();
            dos.flush();
            dos.close();
        } catch (MalformedURLException ex) {
            exception = ex.toString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // Toast.makeText(getApplicationContext(),
                    // "MalformedURLException" +
                    // exception,Toast.LENGTH_LONG).show();
                }
            });
            Log.e("Debug", "error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            exception = ioe.toString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "Lost Network Connection", Toast.LENGTH_LONG).show();
                }
            });
        }
        // ------------------ read the SERVER RESPONSE
        try {
            inStream = new DataInputStream(conn.getInputStream());
            String str = "";
            while ((str = inStream.readLine()) != null) {
                Log.e("Server Responseeeeeeeeeeeee", "Server Response " + str);
                if (str.equalsIgnoreCase("UPLOAD DONE")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String uploaddate = dateFormat.format(new Date());
                    sqliteDbhelper.updateVideoStatus(filename, "Y", uploaddate);
                    sqliteDbhelper.updateUVideoStatus(filename, "DONE");
                    sqliteDbhelper.updateVideoUploadStatus("PENDING");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Drawable exDrawable = getResources().getDrawable(R.drawable.exclamation);
                            videostatusTextView.setText("Videos upload pending");
                            videostatusImageView.setImageDrawable(exDrawable);
                        }
                    });
                    uploaded_number = uploaded_number + 1;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    percentage = (uploaded_number * 100) / number_of_files;
                    handler.post(new Runnable() {
                        public void run() {
                            progressbar.setMessage("Videos Uploading" + "\n" + uploaded_number + " Videos uploaded");
                            progressbar.setProgress(percentage);
                        }
                    });
                    if (percentage >= 100) {
                        uploaded_number = 0;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressbar.dismiss();
                        /*
                         * // all videos uploaded
                         * sqliteDbhelper.updateVideoUploadStatus("DONE");
                         * Drawable trueDrawable =
                         * getResources().getDrawable(R.drawable.trueicon);
                         * videostatusTextView.setText("Videos uploaded");
                         * videostatusTextView
                         * .setTextColor(android.graphics.Color.GREEN);
                         * videostatusImageView.setImageDrawable(trueDrawable);
                         * // uploading response xml uploadResponse();
                         */
                    }
                } else {
                    displayToast("no response from server");
                }
            }
            inStream.close();
        } catch (IOException ioex) {
            Log.e("Debug", "error: " + ioex.getMessage(), ioex);
            exception = ioex.toString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "IOException while reading resposne" + exception, Toast.LENGTH_LONG).show();
                }
            });
        }
        java.lang.System.gc();
    }

    // to send response xml data
    public void senddata() {
        String serverResponse = "";
        try {
            try {
                PrintWriter pw = null;
                URL url = new URL(getResources().getString(R.string.response_URL));
                URLConnection uc = url.openConnection();
                HttpURLConnection conn = (HttpURLConnection)uc;
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "text/xml");
                
                String currenttime = String.valueOf(System.currentTimeMillis());
                tabresonseXmlFilename = "tabresponse" + "-" + imeinumber + "-" + currenttime + ".xml";
                conn.setRequestProperty("fileName", tabresonseXmlFilename);
                pw = new PrintWriter(conn.getOutputStream());
                pw.write(s);
                pw.close();
                // always check HTTP response code from server
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // reads server's response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    serverResponse = reader.readLine();
                    Log.i("response from server", serverResponse);
                }
                if (serverResponse.toString().equalsIgnoreCase("UPLOAD DONE")) {
                    // Calling dot net web service to give file name
                    sendfilename(tabresonseXmlFilename);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Response synchronised", Toast.LENGTH_LONG).show();
                            // updating satatus as xml file uploaded to servlet
                            sqliteDbhelper.updateXmlUploadStatus("DONE", tabresonseXmlFilename);
                            // change the xmlstatus textview color and icon
                            Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                            xmlstatusTextView.setTextColor(android.graphics.Color.GREEN);
                            xmlstatusImageView.setImageDrawable(trueDrawable);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // response not xml uploaded
                            Toast.makeText(getApplicationContext(), "Response xml not synchronisedd" + staticdata, Toast.LENGTH_LONG).show();
                            Drawable falseDrawable = getResources().getDrawable(R.drawable.falseicon);
                            xmlstatusTextView.setTextColor(android.graphics.Color.RED);
                            xmlstatusImageView.setImageDrawable(falseDrawable);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // default reponse
    public void addingDefaultResponse() {
        String response = sqliteDbhelper.getScheduleresponse();
        if (response.equalsIgnoreCase("NO DATA")) {
            try {
                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String IMEI = tm.getDeviceId();
                InputStream inputStream = assetManager.open("defaultresponse.xml");
                int size = inputStream.available();
                byte[] byte_data = new byte[size];
                inputStream.read(byte_data);
                String content = new String(byte_data);
                XMLParser parser = new XMLParser();
                Document doc = parser.getDomElement(content); // getting DOM
                                                              // element
                NodeList nl_scheduleresponse = doc.getElementsByTagName("scheduleresponse");
                Element scheduleresponse_element = (Element)nl_scheduleresponse.item(0);
                Node candiadateElement = scheduleresponse_element.getElementsByTagName("candidates").item(0);
                Element tabdeviceElement = doc.createElement("tabdevice");
                tabdeviceElement.setAttribute("macid", IMEI);
                scheduleresponse_element.insertBefore(tabdeviceElement, candiadateElement);
                // scheduleresponse_element.appendChild(tabdeviceElement);
                try {
                    DOMSource domSource = new DOMSource(doc);
                    StringWriter writer = new StringWriter();
                    StreamResult result = new StreamResult(writer);
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.transform(domSource, result);
                    String new_xml_string = writer.toString();
                    // Toast.makeText(getApplicationContext(), new_xml_string,
                    // Toast.LENGTH_LONG).show();
                    sqliteDbhelper.addScheduleresponse(new_xml_string);
                    Log.i("default response data", new_xml_string);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // webservice to send response xml string
    class Callingwebservice extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            senddata();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }

    class FileNameWebservice extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String tabResponseFileName = params[0];
            sendfilename(tabResponseFileName);
            return null;
        }
    }

    // sending file name by calling dot net webservice
    public void sendfilename(String filename) {
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            // content = URLEncoder.encode(content);
            // content = URLEncoder.encode(content,"UTF-8");

            HttpGet httpGet = new HttpGet(URI.create(getResources().getString(R.string.webservice_responsedata_URL) + filename));
            // HttpGet httpGet = new
            // HttpGet(URI.create("http://clcs.learnplate.com:54322/json/syncreply/SaveResponseData?ResponseData="+filename));
            HttpResponse httpResponse = httpClient.execute(httpGet, new BasicHttpContext());
            byte[] jsonData = EntityUtils.toByteArray(httpResponse.getEntity());
            String str = new String(jsonData, "UTF-8");
            staticdata = str;
            Log.i("xml filepath responseeeeeeeeeeeeeeee", staticdata);
           // 
           // Toast.makeText(this, str.toString(), Toast.LENGTH_LONG).show();
            
            // {"saveResponseDataResponse":{"Success":0}}
            if (str.contains("Success")) {
                // deleting response xml and request xml after uploading process
                // finsh
                // sqliteDbhelper.delete_table_scheduleresponse();
                // sqliteDbhelper.delete_table_schedulerequest();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // Toast.makeText(getApplicationContext(),
                        // "Response synchronised", Toast.LENGTH_LONG).show();
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        acknowTextView.setTextColor(android.graphics.Color.GREEN);
                        acknowstatusImageView.setImageDrawable(trueDrawable);
                        sqliteDbhelper.delete_table_scheduleresponse();
                        sqliteDbhelper.delete_table_schedulerequest();
                        // after clear response data we need to add default
                        // response again
                        addingDefaultResponse();
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Drawable falseDrawable = getResources().getDrawable(R.drawable.falseicon);
                        acknowTextView.setTextColor(android.graphics.Color.RED);
                        acknowstatusImageView.setImageDrawable(falseDrawable);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            staticdata = e.toString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "Excepiton" + staticdata, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void getVideoFileNames() {
        try {
            String responsedata = sqliteDbhelper.getScheduleresponse();
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(responsedata);
            NodeList nl = doc.getElementsByTagName("video");
            // number_of_files = nl.getLength();
            File dir = new File(getResources().getString(R.string.sdcardpath));
            String[] filelist_sdcard = dir.list();
           
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element)nl.item(i);
                String filename = e.getAttribute("filename");
                String videoStatus = sqliteDbhelper.getvideodetail(filename);
                if (Arrays.asList(filelist_sdcard).contains(filename)) {
                    // already uploaded videos no need to count
                    if (videoStatus.equals("N")) {
                        number_of_files++;
                        videofilenameList.add(filename);
                    }
                } else {
                    // file not found in sdcard
                    sqliteDbhelper.updateVideoStatus(filename, "NOTAVAILABLE");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "video missing from SDCARD", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "No videos to upload", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // to find number of candidates in response
    public int getCandidateLength() {
        try {
            String responsedata = sqliteDbhelper.getScheduleresponse();
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(responsedata);
            NodeList nl = doc.getElementsByTagName("candidate");
            int candidatelength = nl.getLength();
            return candidatelength;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public void displayToast(String data) {
        toastdata = data;
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(context, toastdata, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadVideos() {
        // fetching video file name from response XML
        getVideoFileNames();
        if (progressbar != null) {
            progressbar = null;
        }
        progressbar = new ProgressDialog(context);
        progressbar.setCancelable(true);
        progressbar.setMessage("videos Uploading");
        progressbar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressbar.setProgress(0);
        progressbar.setMax(100);
        progressbar.setCancelable(false);
        progressbar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (videofilenameList.size() > 0) {
                    progressbar.show();
                    progressbar.setProgress(1);
                    Upload upload = new Upload();
                    upload.execute(new String[] {
                        "calling servlet"
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            // Toast.makeText(getApplicationContext(),
                            // "No videos to upload", Toast.LENGTH_LONG).show();
                            Drawable excDrawable = getResources().getDrawable(R.drawable.exclamation);
                            videostatusTextView.setText("No Videos");
                            videostatusTextView.setTextColor(android.graphics.Color.YELLOW);
                            videostatusImageView.setImageDrawable(excDrawable);
                            // no videos to upload so uploading response xml
                            // data
                            Cursor cursor = sqliteDbhelper.getUploadStatus();
                            if (cursor.moveToNext()) {
                                String xmlUploadStatus = cursor.getString(1);
                                String ackUploadStatus = cursor.getString(2);
                                if (xmlUploadStatus.equals("DONE")) {
                                    // change the xmlstatus textview color and
                                    // icon
                                    Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                                    xmlstatusTextView.setTextColor(android.graphics.Color.GREEN);
                                    xmlstatusImageView.setImageDrawable(trueDrawable);
                                    // xml upload is done checking remaing ack
                                    // thing
                                    if (!ackUploadStatus.equalsIgnoreCase("DONE")) {
                                        String xmlFilename = cursor.getString(3);
                                        // upload ack
                                        FileNameWebservice fileNameWebservice = new FileNameWebservice();
                                        fileNameWebservice.execute(new String[] {
                                            xmlFilename
                                        });
                                    }
                                } else if (!xmlUploadStatus.equalsIgnoreCase("DONE")) {
                                    // upload xml
                                    uploadResponse();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void uploadResponse() {
        int candidatelengh = getCandidateLength();
        if (candidatelengh > 0) {
            try {
                String schedule_response = sqliteDbhelper.getScheduleresponse();
                schedule_response = schedule_response.trim();
                s = schedule_response.trim();
                s = s.substring(s.indexOf("UTF-8") + 8);
                s = s.replaceAll("(\\r|\\n)", "");
                s = s.trim();
                if (s.length() > 0) {
                    Callingwebservice callingwebservice = new Callingwebservice();
                    callingwebservice.execute(new String[] {
                        s
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getApplicationContext(), "No response to synchronise", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), "No response to synchronise", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    
    
}
