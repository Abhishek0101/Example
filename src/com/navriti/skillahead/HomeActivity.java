package com.navriti.skillahead;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.certiplate.R;
import com.navriti.database.*;
public class HomeActivity extends Activity implements OnClickListener{
	Button assess, report, admin;
	ActionBar actionbar;
	SqliteDbhelper Db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionbar = getActionBar();
		setContentView(R.layout.activity_home);
		
		Db = new SqliteDbhelper(getApplicationContext());
		actionbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FE9A2E")));
		actionbar.setTitle("Home");
		assess = (Button) findViewById(R.id.assessBtn);
		assess.setOnClickListener(this);
		report = (Button) findViewById(R.id.reportBtn);
		report.setOnClickListener(this);
		admin = (Button) findViewById(R.id.adminBtn);
		admin.setOnClickListener(this);
	}
	
	 public boolean onCreateOptionsMenu(Menu menu) {
	        
	    	MenuInflater inflater = getMenuInflater();
			inflater .inflate(R.menu.global, menu);
	        return super.onCreateOptionsMenu(menu);
	    }
	    
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {      
	        menu.findItem(R.id.action_logout).setVisible(true);
	    	return super.onPrepareOptionsMenu(menu);
	    }
	
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	
	    	if(item.getItemId()==R.id.action_logout){
	    		Intent i = new Intent(this, StartActivity.class);
	    		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	    		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    		startActivity(i);
	    		return true;
	    	}
	    	return super.onOptionsItemSelected(item);
	    }
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.assessBtn:
			String db_data = Db.getSchedulerequest();
	        if (!db_data.equalsIgnoreCase("NO DATA")) {
	        	Intent i = new Intent(getApplicationContext(),AuthActivity.class);
				i.putExtra("type", "assisted");
				startActivity(i);
	        }else
	        	Toast.makeText(getApplicationContext(), "Test not Scheduled!!!", Toast.LENGTH_LONG).show();
			break;
		case R.id.reportBtn:
			Intent i = new Intent(this, ReportActivity.class);
    		startActivity(i);
			break;
		case R.id.adminBtn:
			showDialog(2);
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		 AlertDialog dialogDetails = null;

		  switch (id) {
		  case 1:
			   LayoutInflater inflater1 = LayoutInflater.from(this);
			   View dialogview1 = inflater1.inflate(R.layout.mode_dailog, null);
			   AlertDialog.Builder dialogbuilder1 = new AlertDialog.Builder(this);
			   dialogbuilder1.setView(dialogview1);
			   dialogDetails = dialogbuilder1.create();
			  	break;
		  case 2:

		   LayoutInflater inflater2 = LayoutInflater.from(this);
		   View dialogview2 = inflater2.inflate(R.layout.alertlogin, null);
		   AlertDialog.Builder dialogbuilder2 = new AlertDialog.Builder(this);
		   dialogbuilder2.setView(dialogview2);
		   dialogDetails = dialogbuilder2.create(); 
		   break;

		  }

		  return dialogDetails;
	
	}
	
	 @Override
	protected void onPrepareDialog(int id, Dialog dialog) {

	 

	 switch (id) {
	  
	 	case 1:
	 		final AlertDialog alertDialog = (AlertDialog) dialog;

	 	   Button selfBtn = (Button) alertDialog.findViewById(R.id.selfBtn);
	 	   selfBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				Intent i = new Intent(getApplicationContext(),AuthActivity.class);
				i.putExtra("type", "self");
				startActivity(i);
			}});  
	 	   Button assistedBtn = (Button) alertDialog.findViewById(R.id.assistedBtn);
	 	   assistedBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
					Intent i = new Intent(getApplicationContext(),AuthActivity.class);
					i.putExtra("type", "assisted");
					startActivity(i);
			}});
	 	   ImageButton closeBtn = (ImageButton) alertDialog.findViewById(R.id.close);
	 	   closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}});
	 	   break;

	  case 2:

		  final AlertDialog alertDialog1 = (AlertDialog) dialog;

		  Button loginbutton = (Button) alertDialog1.findViewById(R.id.okBtn);
		  Button cancelbutton = (Button) alertDialog1.findViewById(R.id.cnclBtn);

		  final EditText userName = (EditText) alertDialog1.findViewById(R.id.username);

		  final EditText password = (EditText) alertDialog1.findViewById(R.id.password);

		  loginbutton.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
	     if(userName.getText().toString().equals("admin") &&
	        password.getText().toString().equals("admin")){
	    	 alertDialog1.dismiss();
	    	 Intent i =new Intent(getApplication(),AdminActivity.class);
	    	 startActivity(i);	    	 
	     }else if(userName.getText().toString().equals("")||
	 	        password.getText().toString().equals("")) {
	    	 Toast.makeText(getApplicationContext(),"Enter Username/Password",Toast.LENGTH_LONG).show(); 	 
	     }else{
	    	 Toast.makeText(getApplicationContext(),"Invalid Credentials!!",Toast.LENGTH_LONG).show();
	     }
	    }});

	   cancelbutton.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
	     alertDialog1.dismiss();
	    }});
	   break;
	  }
	 }
	
	 @Override
	public void onBackPressed() {
	}
}
