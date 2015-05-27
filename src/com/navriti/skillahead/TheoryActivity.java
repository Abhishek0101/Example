package com.navriti.skillahead;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.app.certiplate.R;
import com.navriti.database.SqliteDbhelper;
import com.navriti.defaultresponse.TheoryResponse;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class TheoryActivity extends Activity implements OnClickListener{
	
	TextView question, clock, section, total_time;
	RadioButton option1, option2, option3, option4;
	Button submit,previous,  next, summary, unanswered;
	
	PowerManager.WakeLock wakeLock;
	
	ImageView Qimage, op1Image,op2Image,op3Image,op4Image;
	
	String candidateID, qpid, qsnid, opid,qptypeid,respnseOptionid;
	int qsnNo,count;
	private long duration;
	
	String Type,startTime,type;
	
	SqliteDbhelper db;
	Cursor cursor, cursor2,cursor3;
	
	TheoryResponse theoryResponse;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.examtheory);
		db = new SqliteDbhelper(getApplicationContext());
		Intent i = getIntent();
		candidateID = i.getExtras().getString("candidate");
		type = i.getExtras().getString("type");
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My wakelook");
        wakeLock.acquire();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
	    startTime = dateFormat.format(new Date());
	    
		section = (TextView) findViewById(R.id.section);
		question = (TextView) findViewById(R.id.question);
		option1 = (RadioButton) findViewById(R.id.option1);
		option1.setOnClickListener(this);
		option2 = (RadioButton) findViewById(R.id.option2);
		option2.setOnClickListener(this);
		option3 = (RadioButton) findViewById(R.id.option3);
		option3.setOnClickListener(this);
		option4 = (RadioButton) findViewById(R.id.option4);
		option4.setOnClickListener(this);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		previous = (Button) findViewById(R.id.previous);
		previous.setOnClickListener(this);
		next = (Button) findViewById(R.id.next);
		next.setOnClickListener(this);
		summary = (Button) findViewById(R.id.summary);
		summary.setOnClickListener(this);
		unanswered = (Button) findViewById(R.id.unanswered);
		unanswered.setOnClickListener(this);
		
		Qimage = (ImageView) findViewById(R.id.imageView1);
		op1Image = (ImageView)findViewById(R.id.opImage1);
		op2Image = (ImageView)findViewById(R.id.opImage2);
		op3Image = (ImageView)findViewById(R.id.opImage3);
		op4Image = (ImageView)findViewById(R.id.opImage4);
		
		total_time = (TextView) findViewById(R.id.Duration);
		clock = (TextView) findViewById(R.id.timer);
		
		Type = "";
		
		qpid = db.getqpid(candidateID);
		cursor = db.getdetails(qpid);
		cursor.moveToNext();
		total_time.setText(cursor.getString(2)+" min");
		duration = Long.parseLong(cursor.getString(2));
		
		qsnNo = 1;
		cursor2 = db.getCandidate(candidateID);
		cursor3=cursor2;
		cursor2.moveToNext();
		createView(cursor2, qsnNo);
		
		
		new CountDownTimer(duration*60000, 1000) { // adjust the milli seconds here

		    public void onTick(long millisUntilFinished) {
		    	duration = millisUntilFinished;
		    clock.setText(""+String.format("%d min:%d sec", 
		                    TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
		                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - 
		                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
		    }

		    public void onFinish() {
		    	db.UpdateTheoryStatus(candidateID, "Y");
		    	Toast.makeText(getApplicationContext(), "\t\t\tTime's Up!!!\nyour responses have been submitted", Toast.LENGTH_SHORT).show();
		    	 wakeLock.release(); 
		    	 theoryResponse=new TheoryResponse(getApplicationContext());			     
			     theoryResponse.TheoryResponseIndividual(candidateID, startTime);
		    	Intent intent = new Intent(getApplicationContext(),IntermediateActivity.class);
				 intent.putExtra("candidate", candidateID);
				 intent.putExtra("type", type);
				 startActivity(intent);
		    }
		 }.start();
	}

	
	public void createView(Cursor cursor, int qsnNo){
		
		Qimage.setVisibility(ImageView.GONE);
		op1Image.setVisibility(ImageView.GONE);
		op2Image.setVisibility(ImageView.GONE);
		op3Image.setVisibility(ImageView.GONE);
		op4Image.setVisibility(ImageView.GONE);
		respnseOptionid = "0";
		if(qsnNo==1){
			previous.setBackgroundColor(Color.parseColor("#F3F781"));
			previous.setClickable(false);
		}else{
			previous.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_general));
			previous.setClickable(true);
		}
		if(qsnNo==cursor.getCount()){
			next.setBackgroundColor(Color.parseColor("#F3F781"));
			next.setClickable(false);
		}else{
			next.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_general));
			next.setClickable(true);
		}
		Cursor opCursor;
		String sectionName, temp, question, option1, option2, option3, option4,imageString;
		qsnid = cursor.getString(2);
		qptypeid = cursor.getString(cursor.getColumnIndex("qptypeid"));
		sectionName = cursor.getString(5);
		section.setText(sectionName);
			temp = cursor.getString(7);
			if(!temp.equalsIgnoreCase("")){
				imageString = temp.replace("data:image/jpeg;base64,", "");
				Qimage.setVisibility(ImageView.VISIBLE);
				byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
				Qimage.setImageBitmap(decodedByte);	
			}
		temp = cursor.getString(cursor.getColumnIndex("question"));
		question = temp.replace("<br>", "\n      ");
		this.question.setText("Q "+qsnNo+") "+question);
		
		opCursor = db.getoption(qsnid);
		
		opCursor.moveToNext();
		temp = opCursor.getString(1);
		option1 = temp.replace("<br>", "\n");
		this.option1.setText(option1);
		this.option1.setChecked(false);
		this.option1.setSelected(false);
		temp = opCursor.getString(4);
		if(!temp.equalsIgnoreCase("")){
			imageString = temp.replace("data:image/jpeg;base64,", "");
			op1Image.setVisibility(ImageView.VISIBLE);
			byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			op1Image.setImageBitmap(decodedByte);	
		}
		this.option1.setBackgroundColor(Color.TRANSPARENT);
		
		opCursor.moveToNext();
		temp = opCursor.getString(1);
		option2 = temp.replace("<br>", "\n");
		this.option2.setText(option2);
		this.option2.setChecked(false);
		this.option2.setSelected(false);
		temp = opCursor.getString(4);
		if(!temp.equalsIgnoreCase("")){
			imageString = temp.replace("data:image/jpeg;base64,", "");
			op2Image.setVisibility(ImageView.VISIBLE);
			byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			op2Image.setImageBitmap(decodedByte);	
		}
		this.option2.setBackgroundColor(Color.TRANSPARENT);

		opCursor.moveToNext();
		temp = opCursor.getString(1);
		option3 = temp.replace("<br>", "\n");
		this.option3.setText(option3);
		this.option3.setChecked(false);
		this.option3.setSelected(false);
		temp = opCursor.getString(4);
		if(!temp.equalsIgnoreCase("")){
			imageString = temp.replace("data:image/jpeg;base64,", "");
			op3Image.setVisibility(ImageView.VISIBLE);
			byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			op3Image.setImageBitmap(decodedByte);	
		}
		this.option3.setBackgroundColor(Color.TRANSPARENT);
	
		opCursor.moveToNext();
		temp = opCursor.getString(1);
		option4 = temp.replace("<br>", "\n");
		this.option4.setText(option4);
		this.option4.setChecked(false);
		this.option4.setSelected(false);
		temp = opCursor.getString(4);
		if(!temp.equalsIgnoreCase("")){
			imageString = temp.replace("data:image/jpeg;base64,", "");
			op4Image.setVisibility(ImageView.VISIBLE);
			byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			op4Image.setImageBitmap(decodedByte);	
		}
		this.option4.setBackgroundColor(Color.TRANSPARENT);
			String op = db.getTheoryResponse(candidateID, qsnid);
			switch(Integer.parseInt(op)){
			case 1:
				this.option1.setChecked(true);
				this.option1.setSelected(true);
				this.option1.setBackgroundColor(Color.parseColor("#A9D0F5"));
				break;
			case 2:
				this.option2.setChecked(true);
				this.option2.setSelected(true);
				this.option2.setBackgroundColor(Color.parseColor("#A9D0F5"));
				break;
			case 3:
				this.option3.setChecked(true);
				this.option3.setSelected(true);
				this.option3.setBackgroundColor(Color.parseColor("#A9D0F5"));
				break;
			case 4:
				this.option4.setChecked(true);
				this.option4.setSelected(true);
				this.option4.setBackgroundColor(Color.parseColor("#A9D0F5"));
				break;
			default:
				break;
			}
		
		if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("-1")){
			db.addTheoryResponse(candidateID, qpid, qptypeid, qsnid, "0");
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.option1:
			 if(option1.isSelected()){
				 option1.setChecked(false);
				 option1.setSelected(false);
				 option1.setBackgroundColor(Color.TRANSPARENT);
				 respnseOptionid = "0";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
			 }else{
				 respnseOptionid = "1";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
				 option1.setBackgroundColor(Color.parseColor("#A9D0F5"));
				 option2.setBackgroundColor(Color.TRANSPARENT);
				 option3.setBackgroundColor(Color.TRANSPARENT);
				 option4.setBackgroundColor(Color.TRANSPARENT);
				 option1.setSelected(true);
				 option2.setChecked(false);
				 option3.setChecked(false);
				 option4.setChecked(false);
				 option2.setSelected(false);
				 option3.setSelected(false);
				 option3.setSelected(false);
			 }
			 break;
		case R.id.option2:
			 if(option2.isSelected()){
				 option2.setChecked(false);
				 option2.setSelected(false);
				 respnseOptionid = "0";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
				 option2.setBackgroundColor(Color.TRANSPARENT);
			 }else{
				 respnseOptionid = "2";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
				 option2.setBackgroundColor(Color.parseColor("#A9D0F5"));
				 option1.setBackgroundColor(Color.TRANSPARENT);
				 option3.setBackgroundColor(Color.TRANSPARENT);
				 option4.setBackgroundColor(Color.TRANSPARENT);
				 option2.setSelected(true);
				 option1.setChecked(false);
				 option3.setChecked(false);
				 option4.setChecked(false);
				 option1.setSelected(false);
				 option3.setSelected(false);
				 option4.setSelected(false);
			 }
			 break;
		
		case R.id.option3:
			 if(option3.isSelected()){
				 option3.setSelected(false);
				 option3.setChecked(false);
				 option3.setBackgroundColor(Color.TRANSPARENT);
				 respnseOptionid = "0";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
			 }else{
				 respnseOptionid = "3";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
				 option3.setBackgroundColor(Color.parseColor("#A9D0F5"));
				 option2.setBackgroundColor(Color.TRANSPARENT);
				 option1.setBackgroundColor(Color.TRANSPARENT);
				 option4.setBackgroundColor(Color.TRANSPARENT);
				 option3.setSelected(true);
				 option1.setChecked(false);
				 option2.setChecked(false);
				 option4.setChecked(false);
				 option1.setSelected(false);
				 option2.setSelected(false);
				 option4.setSelected(false);
			 }
			 break;
		case R.id.option4:
			 if(option4.isSelected()){
				 option4.setChecked(false);
				 option4.setSelected(false);
				 option4.setBackgroundColor(Color.TRANSPARENT);
				 respnseOptionid = "0";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
			 }else{
				 respnseOptionid = "4";
				 db.updateTheoryResponse(candidateID, qsnid, respnseOptionid);
				 option4.setBackgroundColor(Color.parseColor("#A9D0F5"));
				 option2.setBackgroundColor(Color.TRANSPARENT);
				 option3.setBackgroundColor(Color.TRANSPARENT);
				 option1.setBackgroundColor(Color.TRANSPARENT);
				 option4.setSelected(true);
				 option1.setChecked(false);
				 option2.setChecked(false);
				 option3.setChecked(false);
				 option1.setSelected(false);
				 option2.setSelected(false);
				 option3.setSelected(false);
			 }
			 break;
			 
		case R.id.submit:
			 	 db.UpdateTheoryStatus(candidateID, "Y");
			 	 theoryResponse=new TheoryResponse(getApplicationContext());			     
			     theoryResponse.TheoryResponseIndividual(candidateID, startTime);
			 	 Toast.makeText(getApplicationContext(), "All of your responses have been submitted", Toast.LENGTH_LONG).show();
				 wakeLock.release();
				 Intent intent = new Intent(this,IntermediateActivity.class);
				 intent.putExtra("candidate", candidateID);
				 intent.putExtra("type", type);
				 startActivity(intent);
				 finish();
			 break;
		case R.id.previous:
			if(!Type.equalsIgnoreCase("unanswered")){ 
				if(qsnNo>0){ 
					cursor2.moveToPrevious();
					createView(cursor2,--qsnNo);
				}
			}else{
				while(cursor2.moveToPrevious()){
					qsnid = cursor2.getString(2);
					if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("0")||
							db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("-1")){
						createView(cursor2, --qsnNo);
						break;
					}
					--qsnNo;
				}
			}
			break;
		case R.id.next:
				if(!Type.equalsIgnoreCase("unanswered")){ 
					if(qsnNo<cursor2.getCount()){ 
						cursor2.moveToNext();
						createView(cursor2,++qsnNo);
					}
				}else{
					while(cursor2.moveToNext()){
						qsnid = cursor2.getString(2);
						if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("0")||
								db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("-1")){
							createView(cursor2, ++qsnNo);
							break;
						}
						++qsnNo;
					}
				}
				break;
		case R.id.summary:
			 Type = "summary";
			 showDialog(1);
			 break;
		case R.id.unanswered:
			 Type = "unanswered";
			showDialog(2);
			 break;
		}
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialogDetails = null;
		  switch (id) {
		  case 1:
			  	dialogDetails = new Dialog(this);
			  	dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
			  	dialogDetails.setContentView(R.layout.summaryactivity);
			  	
			  	break;
		  case 2:
			  dialogDetails = new Dialog(this);
			  	dialogDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
			  	dialogDetails.setContentView(R.layout.summaryactivity);
			  	TextView tv = (TextView) dialogDetails.findViewById(R.id.textView1);
 				tv.setText("UNANSWERED");
 				TextView tv2 = (TextView) dialogDetails.findViewById(R.id.textView2);
 				tv2.setVisibility(TextView.INVISIBLE);
 				View v = (View) dialogDetails.findViewById(R.id.view2);
 				v.setVisibility(View.INVISIBLE);
			  	break;
		  }

		  return dialogDetails;
	
	}
	
	 @Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		 int qsnNo=0;
		 Button[] btn;
		 String qsnid;
		 Boolean flag;
	 switch (id) {
	  
	 	case 1:
	 			final Dialog alertDialog = (Dialog) dialog;
	 			LinearLayout linear = (LinearLayout) alertDialog.findViewById(R.id.linearQuestion);
	 			linear.removeAllViews();
				cursor = db.getCandidate(candidateID);
				 btn = new Button[cursor3.getCount()];
				for(int i=1;i<=cursor3.getCount();){
					LinearLayout row = new LinearLayout(alertDialog.getContext());
					row.setOrientation(LinearLayout.HORIZONTAL);
					  for(int j=1;j<6 && i<=cursor3.getCount();j++){
						  	cursor.moveToNext();
							  qsnid = cursor.getString(2);
						  	  
						 btn[qsnNo] =new Button(alertDialog.getContext());
				        if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("-1")){
				             btn[qsnNo].setText(String.valueOf(qsnNo+1));
				             btn[qsnNo].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_unseen));
		 				     btn[qsnNo].setOnClickListener(handleOnClick(btn[qsnNo],qsnNo,alertDialog));
				             qsnNo++;
				             Log.d("-1", qsnid + db.getTheoryResponse(candidateID, qsnid));
				        }else if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("0")){
				        	 	btn[qsnNo].setText(String.valueOf(qsnNo+1));
				        	 	btn[qsnNo].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_seen));
				        	 	btn[qsnNo].setOnClickListener(handleOnClick(btn[qsnNo],qsnNo,alertDialog));
				        	 	qsnNo++;
				        	 	Log.d("0", qsnid + db.getTheoryResponse(candidateID, qsnid));
				        	  }else{
				        		  	btn[qsnNo].setText(String.valueOf(qsnNo+1));
	 				        	 	btn[qsnNo].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_answered));
	 				        	 	btn[qsnNo].setOnClickListener(handleOnClick(btn[qsnNo],qsnNo,alertDialog));
	 				        	 	qsnNo++;
	 				        	 	Log.d("answer", qsnid + db.getTheoryResponse(candidateID, qsnid));
				        	  }
				        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			        			LinearLayout.LayoutParams.WRAP_CONTENT);
				        params.setMargins(15, 0, 15, 0);
				        btn[qsnNo-1].setLayoutParams(params);
				        row.addView(btn[qsnNo-1]);
				       i++;
					  }
					  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
		    					 (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					  params.setMargins(0, 10, 0, 0);
					  row.setLayoutParams(params);
					  linear.addView(row);
				}
	 				ImageButton close = (ImageButton) alertDialog.findViewById(R.id.close);
	 				close.setOnClickListener(new OnClickListener() {
	 					@Override
	 					public void onClick(View v) {
	 						alertDialog.dismiss();
	 					}});
	 				break;

	  case 2:
		  final Dialog alertDialog1 = (Dialog) dialog;
		  LinearLayout linear1 = (LinearLayout) alertDialog1.findViewById(R.id.linearQuestion);
		  linear1.removeAllViews();
			cursor = db.getCandidate(candidateID);
			 btn = new Button[cursor3.getCount()];
			 int no=0;
			for(int i=1;i<=cursor3.getCount();){
				LinearLayout row = new LinearLayout(alertDialog1.getContext());
				row.setOrientation(LinearLayout.HORIZONTAL);
				  for(int j=1;j<6 && i<=cursor2.getCount();j++){
					  	flag = true;
					  cursor.moveToNext();
						  qsnid = cursor.getString(2);
					  	  
					 btn[qsnNo] =new Button(alertDialog1.getContext());
			        if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("-1")){
			             btn[qsnNo].setText(String.valueOf(qsnNo+1));
			             btn[qsnNo].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_unseen));
	 				     btn[qsnNo].setOnClickListener(handleOnClick(btn[qsnNo],qsnNo,alertDialog1));
			             qsnNo++;
			             no++;
			        }else if(db.getTheoryResponse(candidateID, qsnid).equalsIgnoreCase("0")){
			        	 	btn[qsnNo].setText(String.valueOf(qsnNo+1));
			        	 	btn[qsnNo].setBackgroundDrawable(getResources().getDrawable(R.drawable.button_seen));
			        	 	btn[qsnNo].setOnClickListener(handleOnClick(btn[qsnNo],qsnNo,alertDialog1));
			        	 	qsnNo++;
			        	 	no++;
			        	  }else{
			        		  	flag =false;
				        	 	qsnNo++;
			        	  }
			        
			        if(flag==true){
			        	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
			        			LinearLayout.LayoutParams.WRAP_CONTENT);
			        	params.setMargins(15, 0, 15, 0);
			        	btn[qsnNo-1].setLayoutParams(params);
			        	btn[qsnNo-1].setLayoutParams(params);
			        	row.addView(btn[qsnNo-1]);
			        }
			       i++;
				  }
				  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
	    					 (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				  params.setMargins(0, 10, 0, 0);
				  row.setLayoutParams(params);
				linear1.addView(row);
			}
			if(no==0){
				LayoutInflater inflater = LayoutInflater.from(this);
				View view  = inflater.inflate(R.layout.no_unanswered, linear1, false); 
				linear1.addView(view);
				
			}
	   
		  			ImageButton close1 = (ImageButton) alertDialog1.findViewById(R.id.close);
	 				close1.setOnClickListener(new OnClickListener() {
	 					@Override
	 					public void onClick(View v) {
	 						alertDialog1.dismiss();
	 					}});
	 				break;
	  }
	 }
	 
	 Button.OnClickListener handleOnClick(final Button button, final int position,final Dialog dialog) {
		 return new Button.OnClickListener() {
		        public void onClick(View v) {
		        	cursor2.moveToPosition(position);
		        	qsnNo = position+1;
		        	createView(cursor2, qsnNo);
		        	dialog.dismiss();
		        }
		    };
	}
	
	@Override
	public void onBackPressed() {
	}
	
	
	
	
	//	
}
