package com.navriti.skillahead;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.app.certiplate.R;
import com.navriti.database.*;
import com.navriti.downloadpaper.*;
import com.navriti.purgedata.*;
public class AdminActivity extends HomeActivity {
	
	Button dwnldBtn, syncBtn, purgeBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionbar = getActionBar();
		actionbar.setTitle("Administration");
		setContentView(R.layout.activity_admin);
		
		//dbhelper=new Dbhelper(getApplicationContext());
		dwnldBtn = (Button) findViewById(R.id.dwnldBtn);
		dwnldBtn.setOnClickListener(this);
		syncBtn = (Button) findViewById(R.id.syncBtn);
		syncBtn.setOnClickListener(this);
		purgeBtn = (Button) findViewById(R.id.purgeBtn);
		purgeBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.dwnldBtn:
			Intent d = new Intent(getApplicationContext(), DownloadingActivity.class);
			startActivity(d);
			break;
		case R.id.syncBtn:
			Intent i = new Intent(this, SyncActivity.class);
			startActivity(i);
			break;
		case R.id.purgeBtn:
			Intent p = new Intent(this, PurgeDataActivity.class);
			startActivity(p);
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
	    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		startActivity(i);
	    		return true;
	    	}
	    	return super.onOptionsItemSelected(item);
	    }

	    @Override
	    public void onBackPressed() {
	    	startActivity(new Intent(getApplicationContext(),HomeActivity.class));
	    	finish();
	    }
}
