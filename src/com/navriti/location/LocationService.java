package com.navriti.location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {
	public static final String BROADCAST_ACTION = "com.navriti.assessment";
	public LocationManager locationManager;
	public MyLocationListener listener;
	
	public Location previousBestLocation = null;

	Intent intent;
	int counter = 0;
	Context context;
	
    
	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);      

	
	}

	@Override
	public void onStart(Intent intent, int startId) {      
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
		locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
		//locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {       
		// handler.removeCallbacks(sendUpdatesToUI);     
		super.onDestroy();
		locationManager.removeUpdates(listener);        
	}   

	/*public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}*/


	public class MyLocationListener implements LocationListener
	{
		
		public void onLocationChanged(final Location loc)
		{
				    double latitude= loc.getLatitude();
					double longitude= loc.getLongitude();
					String address="";
					String city="";
					String country="";
					locationManager.removeUpdates(listener);
					
					
					intent.putExtra("Latitude", latitude);
					intent.putExtra("Longitude", longitude);     
					intent.putExtra("Provider", loc.getProvider());
					//intent.putExtra("enable", true);
	
					
		//geocoder
					Geocoder geocoder;
					geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
					try {
						List<Address> addresses  = geocoder.getFromLocation(latitude,longitude, 1);
						
						 address=addresses.get(0).getAddressLine(0);
						 city=addresses.get(0).getAddressLine(1);
						 country=addresses.get(0).getAddressLine(2);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
		
					intent.putExtra("Address", address);
					intent.putExtra("City", city);
					intent.putExtra("Country", country);
					sendBroadcast(intent);
			if(!address.equals("")){
			onDestroy();
			
			}
			 //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			 
			
		}
		
		
		
		public void onProviderDisabled(String provider)
		{
		//Toast.makeText( getApplicationContext(), "GPS is Disabled", Toast.LENGTH_SHORT ).show();
            
		
		
		}


		public void onProviderEnabled(String provider)
		{
	
			
		//	Toast.makeText( getApplicationContext(), " GPS is Enabled", Toast.LENGTH_SHORT).show();
		}


		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}

	}
}
