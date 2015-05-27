package com.navriti.skillahead;

import com.app.certiplate.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {
	
	Button submit;
	EditText username, password;
	TextView label;
	String user, pass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		
		user = "";
		pass = "";
		label = (TextView) findViewById(R.id.appLabel);
		label.setTypeface(Typeface.createFromAsset(getAssets(), "JosefinSlab.ttf"));
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				user = username.getText().toString();
				pass = password.getText().toString();
				if(user.equals("")||pass.equals("")){
					Toast.makeText(getApplicationContext(), "Enter Username/Password", Toast.LENGTH_SHORT).show();
				}else
					if(user.equals("admin")||pass.equals("admin")){
						Intent i = new Intent(getApplicationContext(), HomeActivity.class);
						startActivity(i);
					}else{
						password.setText("");
						username.setText("");
						
						Toast.makeText(getApplicationContext(), "Invalid Username/Password", Toast.LENGTH_SHORT).show();
						
					}
						
			}
		});
	}

	
}
