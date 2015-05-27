package com.navriti.upload;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.certiplate.R;
import com.navriti.database.*;
import com.navriti.parserclass.*;
import com.navriti.skillahead.HomeActivity;
import com.navriti.skillahead.SyncActivity;

public class IndividualActivity extends HomeActivity  {

	 ProgressDialog progressbar = null;

	    int uploaded_number = 0;
	  
	    int percentage = 0;
	    String fileName[];
	    int number_of_files = 0;

	    String filepath = "";

	    String filename = "";
	    String[] FileName;

	    String imeinumber = "";

	    SqliteDbhelper sqliteDbhelper;

	    String s = "";

	    String staticdata = "";

	    Handler handler;
	    Context context;

	    boolean upload_status = true;

	    String exception = "";

	    String toastdata = "";

	    public static String Upload_URL = "";
	    
	   // Dialog dialog;
	    
	    ProgressDialog pdialog;

	    AssetManager assetManager;
	    // videos list from xml
	    ArrayList<String> videofilenameList = new ArrayList<String>();

	   
	    //button
	    Button[] button;
	    Button buttonresponse;
	    
	    int size=0;
	    int counter=0;
	    int count=0;
	    int j=0;
	    String str = "";
        String ServerResponseData="";
        TextView[] NotDone;
        TextView Status;
        SimpleDateFormat dateFormat;
        String uploaddate;
        String tabresonseXmlFilename = "";
        ActionBar actionbar;
	 
SqliteDbhelper sqlitedbhelper;

	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        actionbar = getActionBar();
			actionbar.setTitle("Sync Individual");
	        setContentView(R.layout.upload_individual);
	        context = this;
	       	 dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	          uploaddate = dateFormat.format(new Date());
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
	       buttonresponse=(Button)findViewById(R.id.btn1);
	       
	       buttonresponse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					if(haveNetworkConnection())
						uploadResponse();
					else
						Toast.makeText(getApplicationContext(), "Check your Intenet Connectivity", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					
					e.printStackTrace();
					
					
				}
				
			}
		});
	        init();
	       
	 }
	 private boolean haveNetworkConnection() {
	        boolean haveConnectedWifi = false;
	        boolean haveConnectedMobile = false;

	        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	        for (NetworkInfo ni : netInfo) {
	            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	                if (ni.isConnected())
	                    haveConnectedWifi = true;
	            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	                if (ni.isConnected())
	                    haveConnectedMobile = true;
	        }
	        return haveConnectedWifi || haveConnectedMobile;
	    }

public void init() {
	 sqlitedbhelper=new SqliteDbhelper(this);
     
	 Cursor cursor=sqlitedbhelper.getvideodetails();
	 size=cursor.getCount();
	TableLayout stk = (TableLayout) findViewById(R.id.table_main);
    TableRow tbrow0 = new TableRow(this);
    TextView tv0 = new TextView(this);
    tv0.setText(" Sl.No ");
    tv0.setGravity(Gravity.CENTER);
   
    tv0.setTextColor(Color.BLUE);
    tv0.setGravity(20);
    tbrow0.addView(tv0);
    TextView tv1 = new TextView(this);
    tv1.setText( "FileName");
    tv1.setGravity(Gravity.CENTER);
    tv1.setTextColor(Color.BLUE);
    tv1.setGravity(20);
    tbrow0.addView(tv1);
    TextView tv3 = new TextView(this);
    tv3.setText(" Date");
    tv3.setGravity(Gravity.CENTER);
    tv3.setTextColor(Color.BLUE);
    tbrow0.addView(tv3); 
    Status=new TextView(this);
    Status.setText("Status");
    Status.setTextColor(Color.BLUE);
    tbrow0.addView(Status);
    stk.addView(tbrow0);
    
    j=0;
    fileName = new String[cursor.getCount()];
    button = new Button[cursor.getCount()];
    NotDone = new TextView[cursor.getCount()];
	while( cursor.moveToNext()){
		String id=cursor.getString(0);
		filename = cursor.getString(1);
		fileName[j] = filename;
		String upload_status=cursor.getString(2);
		String date=cursor.getString(3); 
		{
        TableRow tbrow = new TableRow(this);
        TextView t1v = new TextView(this);
        t1v.setText(id);
        t1v.setTextColor(Color.BLACK);
        t1v.setGravity(Gravity.START);
        tbrow.addView(t1v);
        TextView t2v = new TextView(this);
        t2v.setText(filename);
        t2v.setTextColor(Color.BLACK);
        t2v.setGravity(Gravity.START);
        tbrow.addView(t2v);
        TextView t4v = new TextView(this);
        t4v.setText(date);
        t4v.setTextColor(Color.BLACK);
        t4v.setGravity(Gravity.START);
        tbrow.addView(t4v);
        NotDone[j]=new TextView(this);
        if(upload_status.equalsIgnoreCase("N")){
        	NotDone[j].setText("Not Yet");
        	NotDone[j].setTextColor(Color.RED);
        }else{
        	NotDone[j].setText("DONE");
        	NotDone[j].setTextColor(Color.GREEN);	
        }
        tbrow.addView(NotDone[j]);
        stk.addView(tbrow);
        button[j] =new Button(getApplicationContext());
        if(upload_status.equalsIgnoreCase("N")){
        	 button[j].setClickable(true);
             button[j].setText(" Upload ");
             button[j].setTextColor(Color.WHITE);
             button[j].setOnClickListener(handleOnClick(button[j],j));
             button[j].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_general));
           
        }else{
        	 button[j].setClickable(false);
             button[j].setText("");
             button[j].setBackgroundDrawable(getResources().getDrawable(R.drawable.done_));
          
        }
       
        tbrow.addView(button[j]);
        
        
        j++;
		
		}
	 }
	 
	 }

 View.OnClickListener handleOnClick(final Button button, final int filenameIndex) {
	 return new View.OnClickListener() {
	        public void onClick(View v) {
	        	//Toast.makeText(getApplicationContext(), filename, Toast.LENGTH_SHORT).show();
	        	new UploadIndividual(fileName[filenameIndex],filenameIndex).execute();
	        }
	    };
}

 	@Override
 	public void onBackPressed() {
 		startActivity(new Intent(getApplicationContext(),SyncActivity.class));
 	}
 	
public boolean onCreateOptionsMenu(Menu menu) {
        
    	MenuInflater inflater = getMenuInflater();
		inflater .inflate(R.menu.restapp, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {      
        menu.findItem(R.id.action_home).setVisible(true);
    	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	if(item.getItemId()==R.id.action_home){
    		Intent i = new Intent(this, HomeActivity.class);
    		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		startActivity(i);
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
//
public class UploadIndividual extends AsyncTask<Void, Void, String> {
     String filename;
	int id;
	
	
	 public UploadIndividual(String filename,int id){
	 this.filename = filename;
	 this.id = id;
	 
	 }
	 
	
	@Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
		  super.onPreExecute();
	   pdialog=new ProgressDialog(IndividualActivity.this);
        
        pdialog.setTitle("Uploading"+filename);
        pdialog.setMessage("Uploading in process...");
        pdialog.setCancelable(false);
        pdialog.show();
        //pdialog.setCancelable(false);
      
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected String doInBackground(Void... arg0) {
        // TODO Auto-generated method stub
        try {
            File dir = new File(getResources().getString(R.string.sdcardpath));
            String[] filelist = dir.list();
            if (filelist.length == 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(), "video missing from the SDCARD.", Toast.LENGTH_SHORT).show();
                        progressbar.dismiss();
                    }
                });
            } else {
                    if (Arrays.asList(filelist).contains(filename)) {
                    	
                     
                        filepath = getResources().getString(R.string.sdcardpath) + filename;
                            callingServlet();
                         
                        
                    } else {
                        // file not found in sdcard
                        sqliteDbhelper.updateVideoStatus(filename, "NOTAVAILABLE");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Toast.makeText(getApplicationContext(), "video missing from SDCARD", Toast.LENGTH_SHORT).show();
                            }
                        });
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
       
    	 new Handler().post(new Runnable() {
    	        @Override
    	        public void run() {

    	       pdialog.dismiss();        	
    	    if(ServerResponseData.equalsIgnoreCase("UPLOAD DONE")){
    	        	 Toast.makeText(getApplicationContext(),"Video uploaded successfully.."+filename , Toast.LENGTH_SHORT).show();	 
    	    	        sqlitedbhelper.updateVideoStatus(filename, "Y", uploaddate);
    	    	        sqlitedbhelper.updateUVideoStatus(filename, "DONE");
    	    	        sqlitedbhelper. updateVideoUploadStatus("Done");
    	        	NotDone[id].setText("DONE");
    	        	NotDone[id].setTextColor(Color.GREEN);
    	        	button[id].setText("");
    	        	button[id].setClickable(false);
    	        	button[id].setBackgroundDrawable(getResources().getDrawable(R.drawable.done_));
    	        }
    	       
    	        }
    	    }); // starting it in 1 second
    	
    	
        super.onPostExecute(result);

    
    }
}

// Calling Servlet
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
    
 
  // MultipartEntity reqEntity=new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
    
   
    
    
  
  
   
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
                
            }
        });
    } catch (IOException ioe) {
        exception = ioe.toString();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    // ------------------ read the SERVER RESPONSE
    try {
        inStream = new DataInputStream(conn.getInputStream());
         str = "";
         ServerResponseData="";
        while ((str = inStream.readLine()) != null) {
        	
        	ServerResponseData=str;
            if(ServerResponseData==str){
              handler.post(new Runnable() {
					
					@Override
					public void run() {
			
			         //       displayToast(filename);		
					}
				});
                 handler.post(new Runnable() {
                 
                    @Override
                    public void run() {
                     
                    }
                });
                 
                 try {
                     Thread.sleep(2000);
                 } catch (InterruptedException e) {
                     e.printStackTrace();
                 } //pdialog.dismiss();
                 
            } else {
               
            	displayToast("no response from server");
            } 
        }
        inStream.close();
    } catch (IOException ioex) {
        exception = ioex.toString();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
               // Toast.makeText(getApplicationContext(), "IOException while reading resposne" + exception, Toast.LENGTH_LONG).show();
            	 Toast.makeText(getApplicationContext(), "Unable to upload", Toast.LENGTH_SHORT).show();
                 
            }
        });
    }
    java.lang.System.gc();
}

// Display Toast//

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
                        Toast.makeText(getApplicationContext(), "No response to synchronise", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    } else {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "No response to synchronise", Toast.LENGTH_SHORT).show();
            }
        });
    }
}//end tag UploadRespnse


// webservice to send response xml string
class Callingwebservice extends AsyncTask<String, Void, String> {
    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        
        pdialog=new ProgressDialog(IndividualActivity.this);
        pdialog.setTitle("Upload file");
        pdialog.setMessage("Uploading please wait..");
        pdialog.setCancelable(false);
        pdialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String serverresponse;
        serverresponse = senddata();
        return serverresponse;
    }//end tag Calling Webservice 

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        if(result.equalsIgnoreCase("UPLOAD DONE")){
        handler.post(new Runnable() {
			
			@Override
			public void run() {
			pdialog.dismiss();
		Toast.makeText(getApplicationContext(), "file uploaded", Toast.LENGTH_SHORT).show();		
			}
		});
        }else{
        	pdialog.dismiss();
        	Toast.makeText(getApplicationContext(), "Uploading unsuccessful, try again ", Toast.LENGTH_SHORT).show();
        }
    	super.onPostExecute(result);
        
        
        
        
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
}//end tag getCandidadateLength

//to send response xml data
public String senddata() {
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
            }
            if (serverResponse.toString().equalsIgnoreCase("UPLOAD DONE")) {
                // Calling dot net web service to give file name
                sendfilename(tabresonseXmlFilename);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                      sqliteDbhelper.updateXmlUploadStatus("DONE", tabresonseXmlFilename);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Synchronization failed, try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return serverResponse;
}//end Tag Send Data

//default reponse
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
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}//end tag default response


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
                   // acknowTextView.setTextColor(android.graphics.Color.GREEN);
                    //acknowstatusImageView.setImageDrawable(trueDrawable);
                    sqliteDbhelper.delete_table_scheduleresponse();
                    sqliteDbhelper.delete_table_schedulerequest();
                    
                    Toast.makeText(getApplicationContext(), "Acknowlegement Successful", Toast.LENGTH_SHORT).show();
                	buttonresponse.setClickable(false);
    	        	buttonresponse.setBackgroundDrawable(getResources().getDrawable(R.drawable.done_));
                    
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
                    Toast.makeText(getApplicationContext(), "Acknowlegement unsuccessful", Toast.LENGTH_LONG).show();
                   
                    
                    
                }
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        staticdata = e.toString();
        handler.post(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}





//end of class
}

