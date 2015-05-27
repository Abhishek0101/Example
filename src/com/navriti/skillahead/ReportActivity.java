package com.navriti.skillahead;


import com.app.certiplate.R;
import com.navriti.database.SqliteDbhelper;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportActivity extends HomeActivity {

	int count;
	SqliteDbhelper db;
	LinearLayout layout,report;
	String sno,candidateID,theory,prac;
	Cursor cursor, cursor2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionbar = getActionBar();
		actionbar.setTitle("Assessment Report");
		setContentView(R.layout.activity_report);
		db = new SqliteDbhelper(getApplicationContext());
		LayoutInflater inflater = LayoutInflater.from(this);
		layout = (LinearLayout) findViewById(R.id.reportRow);
		layout.removeAllViews();
		cursor = db.getStatus();
		for(int i = 1;cursor.moveToNext();){
			if(cursor.getString(1).equalsIgnoreCase("Y")||cursor.getString(2).equalsIgnoreCase("Y")){
				View view  = inflater.inflate(R.layout.row_report, layout, false); 
				final TextView tv1 = (TextView) view.findViewById(R.id.sno);
				final TextView tv2 = (TextView) view.findViewById(R.id.candidateID);
				final TextView tv3 = (TextView) view.findViewById(R.id.theory);
				final TextView tv4 = (TextView) view.findViewById(R.id.prac);			
				
				sno = String.valueOf(i);
				tv1.setText(sno);
				
				candidateID = cursor.getString(0);
				tv2.setText(candidateID);
				
				cursor2 = db.getall_TheoryMarks(candidateID);
				count=0; 
				while (cursor2.moveToNext()) {
		                String optnid = cursor2.getString(4);
		                if(!optnid.equalsIgnoreCase("0"))
		                	count++;
		          }
				theory = String.valueOf(count);
				tv3.setText(theory);
				
				prac = String.valueOf(db.getPractCount(candidateID));
				tv4.setText(prac);
				i++;
				if(i%2==0)
					view.setBackgroundResource(R.drawable.border1);
				else
					view.setBackgroundResource(R.drawable.border2);
		        layout.addView(view);
			}
			
			
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
	    		finish();
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
