package com.navriti.skillahead;

import com.app.certiplate.R;
import com.navriti.database.SqliteDbhelper;
import com.navriti.parserclass.TheoryParser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AuthActivity extends HomeActivity{
	
	String type, candidateID, password;
	EditText candiID, pass;
	Button submitBtn;
	SqliteDbhelper db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		actionbar = getActionBar();
		actionbar.setTitle("Assessment");
		
		db = new SqliteDbhelper(getApplicationContext());
		Intent i = getIntent();
		type = i.getExtras().getString("type");
		if(type.equals("self")){
			setContentView(R.layout.activity_self);
			candiID = (EditText) findViewById(R.id.candiID);
			pass = (EditText) findViewById(R.id.pass);
			submitBtn = (Button) findViewById(R.id.loginBtn);
			submitBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					candidateID = candiID.getText().toString();
					password = pass.getText().toString();
					if(candidateID.equals("")||pass.equals(""))
						Toast.makeText(getApplicationContext(), "Enter Candidate ID/Passwod", Toast.LENGTH_SHORT).show();
					else if(db.cmpltCandidateVerify(candidateID, password)){
							Intent intent = new Intent(getApplicationContext(), IntermediateActivity.class);
							intent.putExtra("candidate", candidateID);
							intent.putExtra("type", type);
							startActivity(intent);
						 }else
							Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
					
				}		
			});
		}else 
			if(type.equals("assisted")){
				setContentView(R.layout.activity_assisted);
				candiID = (EditText) findViewById(R.id.candiID);
				submitBtn = (Button) findViewById(R.id.loginBtn);
				submitBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						candidateID = candiID.getText().toString();
						if(candidateID.equals(""))
							Toast.makeText(getApplicationContext(), "Enter Candidate ID", Toast.LENGTH_SHORT).show();
						else if(db.getCandidateVerify(candidateID))
						{
							if(!db.getTheoryStatus(candidateID) || !db.getPracticalStatus(candidateID)){
								Intent intent = new Intent(getApplicationContext(), IntermediateActivity.class);
								intent.putExtra("candidate", candidateID);
								intent.putExtra("type", type);
								startActivity(intent);
							}
							else
								Toast.makeText(getApplicationContext(), "Test has been conducted", Toast.LENGTH_SHORT).show();
							
						}else
								Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
						
					}		
				});
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
	    	startActivity(new Intent(getApplicationContext(),HomeActivity.class));
	    	finish();
	    }
}
