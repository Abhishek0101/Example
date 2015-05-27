package com.navriti.skillahead;

import com.app.certiplate.R;
import com.navriti.database.SqliteDbhelper;
import com.navriti.parserclass.TheoryParser;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IntermediateActivity extends HomeActivity {
	
	TextView instruction;
	Button theoryBtn, pracBtn;
	String candidateID, inst;
	SqliteDbhelper db;
	Cursor cursor;
	String type;
	String qpid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		actionbar = getActionBar();
		actionbar.setTitle("Assessment");
		
		setContentView(R.layout.activity_intermediate);		
		Intent i = getIntent();
		candidateID = i.getExtras().getString("candidate");
		type = i.getExtras().getString("type");
		
		db = new SqliteDbhelper(this);
		qpid=db.getqpid(candidateID);
		cursor = db.getdetails(qpid);
		cursor.moveToNext();
		instruction = (TextView) findViewById(R.id.instructions);
		instruction.setText(Html.fromHtml(cursor.getString(1)));
		
		if(db.getTheoryStatus(candidateID)){
			theoryBtn = (Button) findViewById(R.id.theoryBtn);
			theoryBtn.setBackgroundColor(Color.parseColor("#F3F781"));
			theoryBtn.setClickable(false);
		}else{
			theoryBtn = (Button) findViewById(R.id.theoryBtn);
			theoryBtn.setOnClickListener(this);
		}
		if(db.getPracticalStatus(candidateID)){
			pracBtn = (Button) findViewById(R.id.pracBtn);
			pracBtn.setBackgroundColor(Color.parseColor("#F3F781"));
			pracBtn.setClickable(false);
		}else{
			pracBtn = (Button) findViewById(R.id.pracBtn);
			pracBtn.setOnClickListener(this);
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.theoryBtn:
			Intent intent = new Intent(getApplicationContext(), TheoryActivity.class);
			intent.putExtra("candidate", candidateID);
			intent.putExtra("type", type);
			startActivity(intent);
			break;
		case R.id.pracBtn:
			Intent pracIntent = new Intent(getApplicationContext(), PracticalActivity.class);
			pracIntent.putExtra("candidate", candidateID);
			pracIntent.putExtra("type", type);
			startActivity(pracIntent);
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
    	Intent intent = new Intent(getApplicationContext(),AuthActivity.class);
    	intent.putExtra("type", type);
    	startActivity(intent);
    	finish();
    }
}
