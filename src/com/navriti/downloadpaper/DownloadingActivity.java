package com.navriti.downloadpaper;

import android.app.Activity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.app.certiplate.R;
import com.navriti.database.*;
import com.navriti.defaultresponse.DefaultResponse;
import com.navriti.parserclass.*;
import com.navriti.skillahead.*;

public class DownloadingActivity extends Activity {//class starting tag
	ProgressDialog dialog;

    String staticdata = "";

    Handler handler,handler1;

    String reversedata = "";

    String responsexml_str = "";

    SqliteDbhelper sqliteDbhelper;

    AssetManager assetManager;
    
    Callingwebservice callingwebservice;
    MyAsyncTask myAsynctask;
    
    DefaultResponse defaultRes;
    TextView downloadpaperTextView, getlocationTextView, savelocationTextView;

    ImageView downloadpaperImageView, getlocationImageView, savelocationImageView;

    boolean datadownloaded = false;
    
	//Dbhelper dbhelper;
	
    Context context;
    
    String toastdata = "";
    
    String locationdata="no data";
    
    double lat=0.0;
    
    double lon=0.0;
    
    String citydata="no city";
    String statedata="no state";
    String countrydata="no country";
    String downloadpaperStatus="";
    String getlocationStatus="";
    String savelocationStatus="" ;
       
    final private static int DIALOG_DOWNLOAD = 1;
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       
        super.onCreate(savedInstanceState);
        
      this.setContentView(R.layout.activity_admin);  
      
        //context = this;
       
        assetManager = getAssets();
       // dbhelper=new Dbhelper(this) ;  
        sqliteDbhelper = new SqliteDbhelper(this);
        defaultRes=new DefaultResponse(getApplicationContext());
        sqliteDbhelper.delete_table_downloadstatus();
        sqliteDbhelper.delete_table_locatoin();
        // sqliteDbhelper.updateallVideoStaus();
        handler = new Handler();
        //sqliteDbhelper.delete_table_locatoin();
        sqliteDbhelper.addDefultdownloadStatus("NOTYET", "NOTYET", "NOTYET");
        defaultRes.defualtResponse();
        showDialog(DIALOG_DOWNLOAD);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialogDetails = null;
        switch (id) {
            case DIALOG_DOWNLOAD:
                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogview = inflater.inflate(R.layout.downloadpaperstatus, null);
                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
                dialogbuilder.setCancelable(false);
                dialogbuilder.setTitle("Downloading Status");
                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();
                break;
        }
        return dialogDetails;
    }
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_DOWNLOAD:
                final AlertDialog alertDialog = (AlertDialog)dialog;
                downloadpaperTextView = (TextView)alertDialog.findViewById(R.id.downloadpaper_textView);
                getlocationTextView = (TextView)alertDialog.findViewById(R.id.getlocation_textView);
                savelocationTextView = (TextView)alertDialog.findViewById(R.id.savelocation_textView2);
                downloadpaperImageView = (ImageView)alertDialog.findViewById(R.id.downloadpaper_imageView);
                getlocationImageView = (ImageView)alertDialog.findViewById(R.id.getlocation_imageView);
                savelocationImageView = (ImageView)alertDialog.findViewById(R.id.saveloc_imageView);
                final ImageView exitImageView = (ImageView)alertDialog.findViewById(R.id.exit_imageView);
                exitImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                    	callingwebservice.cancel(true);
                    	myAsynctask.cancel(true);
                        alertDialog.dismiss();
                        finish();
                        onBackPressed();
                    }
                });
                Cursor cursor = sqliteDbhelper.getDownloadStatus();
                if (cursor.moveToNext()) {
                    downloadpaperStatus = cursor.getString(0);
                     getlocationStatus = cursor.getString(1);
                    savelocationStatus = cursor.getString(2);
                    if (downloadpaperStatus.equals("Done")) {
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        downloadpaperTextView.setText("Paper Download");
                        downloadpaperTextView.setTextColor(android.graphics.Color.GREEN);
                        downloadpaperImageView.setImageDrawable(trueDrawable);
                    }
                    if (getlocationStatus.equals("Done")) {
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        downloadpaperTextView.setText("Location Done");
                        downloadpaperTextView.setTextColor(android.graphics.Color.GREEN);
                        downloadpaperImageView.setImageDrawable(trueDrawable);
                    }
                    if (savelocationStatus.equals("Done")) {
                        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
                        downloadpaperTextView.setText("Save Location");
                        downloadpaperTextView.setTextColor(android.graphics.Color.GREEN);
                        downloadpaperImageView.setImageDrawable(trueDrawable);
                    }
                     if (!downloadpaperStatus.equalsIgnoreCase("Done")){
                    	StartDownloading();
                    	
                    }
                     if(!getlocationStatus.equalsIgnoreCase("Done")){
                    	 
                    	  myAsynctask =  new MyAsyncTask(DownloadingActivity.this);
                    	  	myAsynctask.execute();
                     }
                     
                     
                }else {
                	 handler.post(new Runnable() {
     					
     					@Override
     					public void run() {
     						Toast.makeText(getApplicationContext(),"No download status info " , Toast.LENGTH_SHORT).show();
     					
     					}
     				});
                }
         break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    
    public void StartDownloading() {
        // Checking already request XML data is there or not
        String db_data = sqliteDbhelper.getSchedulerequest();
        if (db_data.equalsIgnoreCase("NO DATA")) {
            
            // steps to consume the webservice
            try {
                String text = "AKIAJBWPOEBPWMGLGGDA|False";
                byte[] data = text.getBytes("UTF-8");
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                String random_number = "";
                Random randomGenerator = new Random();
                for (int i = 0; i < 5; i++) {
                    int randomInt = randomGenerator.nextInt(9);
                    random_number = random_number + randomInt;
                }
                random_number = random_number.trim();
                String random_base = random_number + base64;
                random_base = random_base.trim();
                random_number = "";
                for (int i = 0; i < 5; i++) {
                    int randomInt = randomGenerator.nextInt(9);
                    random_number = random_number + randomInt;
                }
                random_number = random_number.trim();
                String random_base_random = random_base + random_number;
                random_base_random = random_base_random.trim();
                StringBuffer reversebuffer = new StringBuffer(random_base_random);
                reversedata = reversebuffer.reverse().toString();
                reversedata = reversedata.trim();
                callingwebservice = new Callingwebservice();
                callingwebservice.execute(new String[] {
                    "datais"
                });
                        
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
        	
            Toast.makeText(getApplicationContext(), "Schedules and question paper are already downloaded.", Toast.LENGTH_SHORT).show();
            onBackPressed();
       
        }
    }

 // fetching xml file path from webservice
    public String gettingFilepathFromServer() {
        String requestxmlurl_string = "";
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            // for test server
            HttpGet httpGet = new HttpGet(URI.create(getResources().getString(R.string.webservice_request_URL)));
            HttpResponse httpResponse = httpClient.execute(httpGet, new BasicHttpContext());
            byte[] responsebyte = EntityUtils.toByteArray(httpResponse.getEntity());
            String response_str = new String(responsebyte, "UTF-8");
            // Parsing the json response taking url
         //   CommonUtilities.displayLog("Request Response:" + response_str);
            JSONObject jsonObj = new JSONObject(response_str);
            String ScheduleData_string = jsonObj.getString("GetScheduleData");
            JSONObject ScheduleDataObj = new JSONObject(ScheduleData_string);
            requestxmlurl_string = ScheduleDataObj.getString("RequestXMLURL");
          //  CommonUtilities.displayLog("RequestXMLURL:" + requestxmlurl_string);
            return requestxmlurl_string;
        } catch (Exception e) {
            e.printStackTrace();
            return requestxmlurl_string;
        }
    }

 // if you dont want to download data from webservice read from local file
    // use this method
    public void readFromLocal() {
        try {
            InputStream inputStream = assetManager.open("ntabrequest.xml");
            int size = inputStream.available();
            byte[] byte_data = new byte[size];
            inputStream.read(byte_data);
            String content = new String(byte_data);
            content = content.replaceAll("'", "''");
            content = content.replaceAll("&amp;lt;", "&lt;");
            content = content.replaceAll("&amp;gt;", "&gt;");
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(content); // getting DOM element
            NodeList nl = doc.getElementsByTagName("candidate");
            String xmlFilePath = Environment.getExternalStorageDirectory().getPath() + "/downloadata.xml";
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
            int candidateLength = nl.getLength();
            if (candidateLength == 0) {
                Toast.makeText(getApplicationContext(), "No scheduled candidate available for today.", Toast.LENGTH_SHORT).show();
            } else {
                // adding default upload status
                sqliteDbhelper.clearUploadStatus();
                sqliteDbhelper.addDefulatuploadStatus("NOTYET", "NOTYET", "NOTYET");
                sqliteDbhelper.addSchedulerequest(content, "27th augst");
                Toast.makeText(getApplicationContext(), "Schedules and question paper downloaded successfully.", Toast.LENGTH_SHORT).show();
                
               // onBackPressed();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // onBackPressed();
        }
    }

    
    public void gettingDataFromXmlPath(String xmlpathurl) {
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            HttpGet httpGet = new HttpGet(URI.create(xmlpathurl));
            HttpResponse httpResponse = httpClient.execute(httpGet, new BasicHttpContext());
            byte[] responsebyte = EntityUtils.toByteArray(httpResponse.getEntity());
            responsexml_str = new String(responsebyte, "UTF-8");
            // some special char is not saved in sqlite so replacing
            responsexml_str = responsexml_str.replaceAll("'", "''");
            responsexml_str = responsexml_str.replaceAll("&amp;lt;", "&lt;");
            responsexml_str = responsexml_str.replaceAll("&amp;gt;", "&gt;");
            datadownloaded = true;
        } catch (Exception e) {
            e.printStackTrace();
            staticdata = e.toString();
        }
    }
    class Callingwebservice extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub        	
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String xmlpath = gettingFilepathFromServer();
            if (xmlpath.length() > 0) {
                gettingDataFromXmlPath(xmlpath);
            }
        
            
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //TODO Auto-generated method stub
            handler.post(new Runnable() {
              @SuppressLint("SimpleDateFormat")
               @Override
             public void run() {
                    String db_data = sqliteDbhelper.getSchedulerequest();
                    if (db_data.equalsIgnoreCase("NO DATA") && datadownloaded) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                        String downlaodate = dateFormat.format(new Date());
                        // first line of the xml
                        try {
                        	
                            InputStream inputStream = assetManager.open("firstline.xml");
                            int size = inputStream.available();
                            byte[] byte_data = new byte[size];
                            inputStream.read(byte_data);
                            // xml data in string format
                            String firstlintXml = new String(byte_data);
                            responsexml_str = firstlintXml + "\n" + responsexml_str;
                         
                            // validating recived request xml is valid or not by
                            // counting candidate info
                            try {
                                String content = responsexml_str;
                                XMLParser parser = new XMLParser();
                                // getting DOM element
                                Document doc = parser.getDomElement(content);
                                NodeList nl = doc.getElementsByTagName("candidate");
                                int candidateLength = nl.getLength();
                                if (candidateLength == 0) {
                                 handler.post(new Runnable() {
									
									@Override
									public void run() {
										     Drawable falseDrawable = getResources().getDrawable(R.drawable.falseicon);
					                        downloadpaperTextView.setTextColor(android.graphics.Color.RED);
					                        downloadpaperImageView.setImageDrawable(falseDrawable); 
					                    		
		                                    Toast.makeText(getApplicationContext(), "No scheduled candidate available for today.", Toast.LENGTH_SHORT).show();
		                                   
					                      
									}
								});
                                	                        
                                
                                } else {
                                    sqliteDbhelper.addSchedulerequest(responsexml_str, downlaodate);
                                    // adding default upload status
                                    sqliteDbhelper.clearUploadStatus();
                                    sqliteDbhelper.addDefulatuploadStatus("NOTYET", "NOTYET", "NOTYET");
                   				    sqliteDbhelper.updateDownloadPaperStatus("DONE");
										    
                   				    Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
					                downloadpaperTextView.setTextColor(android.graphics.Color.GREEN);
					                downloadpaperImageView.setImageDrawable(trueDrawable);
					                Toast.makeText(getApplicationContext(), "Schedules and question paper downloaded successfully.", Toast.LENGTH_SHORT).show();
					                
					                TheoryParser exam=new TheoryParser(getApplicationContext());
					        		exam.parseXMl();
					        		exam.InstructionDetails();
					        		exam.saveUsersInfo(); 
					        		exam.parsePracticalQuestion();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            onBackPressed();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                    	handler.post(new Runnable() {
							
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "Unable to download Scheduled data", Toast.LENGTH_SHORT).show();
								 Drawable falseDrawable = getResources().getDrawable(R.drawable.falseicon);
			                        downloadpaperTextView.setTextColor(android.graphics.Color.GREEN);
			                        downloadpaperImageView.setImageDrawable(falseDrawable); 	
								
								
								//onBackPressed();
								
							}
						});
                        
                    }
                    //dialog.dismiss();
               }
           }); 
            super.onPostExecute(result);
            
          
        }
    }

// gps data 
    
    public class MyAsyncTask extends AsyncTask<Void, Void, Void> implements LocationListener {
	    private Context ContextAsync;
	    public MyAsyncTask (Context context){
	        this.ContextAsync = context;
	    }

	    Dialog progress;
	    private String providerAsync;
	    private LocationManager locationManagerAsync;  
	    double   latAsync=0.0;
	    double lonAsync=0.0;
	    String thikanaAsync="Scan sms for location";

	    String AddressAsync="";
	    String city1="";
	    String state1="";
	    String country1="";
	    
	    Geocoder GeocoderAsync;

	    Location location;

	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }


	    @Override
	    protected Void doInBackground(Void... arg0) {
	        // TODO Auto-generated method stub
	        locationManagerAsync = (LocationManager) ContextAsync.getSystemService(ContextAsync.LOCATION_SERVICE);


	        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	        criteria.setCostAllowed(false);
	        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
	        providerAsync = locationManagerAsync.getBestProvider(criteria, false);


	        if (locationManagerAsync.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	            providerAsync = LocationManager.GPS_PROVIDER;
	        } else if (locationManagerAsync.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	            providerAsync = LocationManager.NETWORK_PROVIDER;
	            /*AlertDialog.Builder alert = new AlertDialog.Builder(this);
	            alert.setTitle("GPS is disabled in the settings!");
	            alert.setMessage("It is recomended that you turn on your device's GPS and restart the app so the app can determine your location more accurately!");
	            alert.setPositiveButton("OK", null);
	            alert.show();*/         
	        } else if (locationManagerAsync.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
	            providerAsync = LocationManager.PASSIVE_PROVIDER;
	            //Toast.makeText(ContextAsync, "Switch On Data Connection!!!!", Toast.LENGTH_LONG).show();
	        }    

	        location = locationManagerAsync.getLastKnownLocation(providerAsync);
	        // Initialize the location fields
	        if (location != null) {
	            latAsync = location.getLatitude();
	            lonAsync = location.getLongitude();

	        } else {
	            //Toast.makeText(ContextAsync, " Locationnot available", Toast.LENGTH_SHORT).show();
	        }



	        List<Address> addresses = null;
	        GeocoderAsync = new Geocoder(ContextAsync, Locale.getDefault());
	        try {
	            addresses = GeocoderAsync.getFromLocation(latAsync, lonAsync, 1);

	            String address = addresses.get(0).getAddressLine(0);
	            String city = addresses.get(0).getAddressLine(1);
	            String state = addresses.get(0).getAddressLine(2);
	            String country=addresses.get(0).getAddressLine(3);
	            AddressAsync = Html.fromHtml(
	                    address + ", " + city + ",<br>" +""+state+""+ country).toString();
	            city1=city;
	            state1=state;
	            country1=country;
	        } catch (Exception e) {
	            e.printStackTrace();
	            
	              AddressAsync = "Refresh for the address";
	              handler.post(new Runnable() {
					
					@Override
					public void run() {
					//	Toast.makeText(getApplicationContext(),"No location Found..." , Toast.LENGTH_SHORT).show();
					
					}
				});
                  
                     }
	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {  

	        super.onPostExecute(result);
	        //progress.dismiss();
	        onLocationChanged(location);
	        lat=latAsync;
	        lon=lonAsync;
	        locationdata=AddressAsync;
            citydata=city1;
            statedata=state1;
            countrydata=country1;
	        if(!locationdata.equalsIgnoreCase("Refresh for the address")){
	        try {
				sqliteDbhelper.updateGetLocationStatus("Done");
				 handler.post(new Runnable() {
						
						@Override
						public void run() {
							//Toast.makeText(getApplicationContext(),locationdata , Toast.LENGTH_SHORT).show();
						
						}
					});
				Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
				getlocationTextView.setTextColor(android.graphics.Color.GREEN);
				getlocationImageView.setImageDrawable(trueDrawable);		
				if(!savelocationStatus.equalsIgnoreCase("Done") ){
		
					try{
					saveloc();
				   
					}catch(Exception e){
						
						e.printStackTrace();
					}
				}
			} catch (NotFoundException e) {
			
			displayToast(e.getMessage());
			}
	        }
	        
	        else
	        {	
	        	 Drawable trueDrawable = getResources().getDrawable(R.drawable.falseicon);
	             getlocationTextView.setTextColor(android.graphics.Color.RED);
	             getlocationImageView.setImageDrawable(trueDrawable);
	             
	   
	        }
            
	       
	    }



	    @Override
	    public void onLocationChanged(Location location) {
	        // TODO Auto-generated method stub
	        locationManagerAsync.requestLocationUpdates(providerAsync, 0, 0, this);
	    }

	    @Override
	    public void onProviderDisabled(String provider) {
	        // TODO Auto-generated method stub

	    }

	    @Override
	    public void onProviderEnabled(String provider) {
	        // TODO Auto-generated method stub

	    }

	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	        // TODO Auto-generated method stub

	    }
	}
    public void saveloc(){
    	if(!savelocationStatus.equalsIgnoreCase("Done")){
    	
        sqliteDbhelper.addLocation(lat, lon, locationdata,citydata,statedata,countrydata);
        sqliteDbhelper.updateSaveLocationStatus("Done");
        Drawable trueDrawable = getResources().getDrawable(R.drawable.trueicon);
        savelocationTextView.setTextColor(android.graphics.Color.GREEN);
        savelocationImageView.setImageDrawable(trueDrawable);
       
    	}else
    	{
    		Drawable falseDrawable = getResources().getDrawable(R.drawable.falseicon);
            savelocationTextView.setTextColor(android.graphics.Color.RED);
            savelocationImageView.setImageDrawable(falseDrawable);
    		
    		
    		
    	}
    	
    }

    
    
    

}//class end tag
