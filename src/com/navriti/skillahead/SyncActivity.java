package com.navriti.skillahead;

import com.app.certiplate.R;
import com.navriti.upload.IndividualActivity;
import com.navriti.upload.OverallActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SyncActivity extends HomeActivity {

	Button BtnAll, BtnIndividual;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);
		actionbar = getActionBar();
		actionbar.setTitle("Sync Response");
		
		BtnAll = (Button) findViewById(R.id.synallBtn);
		BtnAll.setOnClickListener(this);
		BtnIndividual =(Button) findViewById(R.id.syncIndvidualBtn);
		BtnIndividual.setOnClickListener(this);
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
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.synallBtn:
				if(!haveNetworkConnection())
	        		Toast.makeText(getApplicationContext(), "Check your Internet Connectivity", Toast.LENGTH_SHORT).show();
				else
					startActivity(new Intent(getApplicationContext(),OverallActivity.class));
				break;
				
		case R.id.syncIndvidualBtn:
				startActivity(new Intent(getApplicationContext(),IndividualActivity.class));
				break;
		}
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
	    
	    @Override
	    public void onBackPressed() {
	    	startActivity(new Intent(getApplicationContext(),AdminActivity.class));
	    	finish();
	    }
}
