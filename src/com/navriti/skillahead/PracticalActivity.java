
package com.navriti.skillahead;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Text;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.app.certiplate.R;
import com.navriti.database.*;
import com.navriti.defaultresponse.DefaultResponse;
import com.navriti.defaultresponse.PracticalResponse;

public class PracticalActivity extends Activity {
    private Camera myCamera;

    private MyCameraSurfaceView myCameraSurfaceView;
    
    FrameLayout myCameraPreview;
    
    private MediaRecorder mediaRecorder;

    ImageView startbutton, submiButton;

    SurfaceHolder surfaceHolder;

    boolean recording; // camera recording status

    boolean exam_start = false;

    // back count if user want to exit he need to press two times
    int backcount = 0;

    // video quality 0 = low 2 = mid 7 = high
    int qualityis = 7;

    // Default video quality we can use "mid" or "high"
    public static String videoquality = "Low";

    String filepath = ""; // recorded video file path

    String imeinumber = ""; // Device IMEI number we use for file name

    PowerManager.WakeLock wakeLock; // power management

    // parent node temporary i need to parse with QPID
    static final String KEY_assessment = "assessment";

    String ATTR_type = "type";

    static final String KEY_text = "text";

	private String Lattitude = "No lat";

	private  String Longitude = "No Lon";

	private  String Address ="feching";
	private  String CityState ="No Data";
	private  String Country ="No Data";
	
	private String[] PcMarks=new String[100];
    // database object
  
	String type;
    SqliteDbhelper sqliteDbhelper;

    AssetManager assetManager;

    int cursorsize = 0;

    Cursor cursor;

    int size;
    Boolean flag;

    // layout for Dynamically created views (TEXTVIEWS and EDITTEXTS)
    LinearLayout linearLayout;
    LinearLayout linearLayout1;

    ScrollView scrollView;

    // response submit status
    boolean shall_i_submit = false;

    // present candiated question paperid
    public static String candiateid = "", qpaperid = "";
    
    //question type
    String qsntype="";

    EditText editText;

    // dynamically created EDITTEXTS INFO LIST LIST
    ArrayList<EditText> edittextarraylist = new ArrayList<EditText>();

    // AUTO ID for EDITTEXTS
    int idis = 0;

    // MAXIMUM MARKS LIST for the question id
    ArrayList<Double> max_marksentrylist = new ArrayList<Double>();

    // parameter list
    ArrayList<Integer> optionidlist = new ArrayList<Integer>();

    int qid; // current question id

    // current qptypeid
    String qptypeid = "";

    // to check shall i insert marks or not
    boolean gofurther = false;

    // practical Test START time and END time
    String startTime, endtime;

    Handler handler;

    Context context;
    //gps locations variables
    Double lat=0.0;
	Double lon=0.0;
	String add="";
	String city="";
	String country="";
	String resultgps="feching";
	TextView textaddress;
	String TAG="GPS";
	RelativeLayout item;
	ViewGroup parent;
	Text loc;
	String locallocationdata="";
	DefaultResponse defaultResponse;
	ActionBar actionbar;
	PracticalResponse pr;
	
//current date
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
	String currentDateandTime = sdf.format(new Date());
	ImageView recordstatusImageView; 
    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
    @SuppressLint({
            "SimpleDateFormat", "Wakelock"
    })
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assetManager = getAssets();
        handler = new Handler();
        context = this;
        sqliteDbhelper = new SqliteDbhelper(this);
        pr = new PracticalResponse(getApplicationContext());
         
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        startTime = dateFormat.format(new Date());
        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imeinumber = tm.getDeviceId();
        recording = false;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_practical);
            Intent i  = getIntent();
            candiateid = i.getExtras().getString("candidate"); 
            type = i.getExtras().getString("type");
             qpaperid = sqliteDbhelper.getqpid(candiateid);
        linearLayout = (LinearLayout)findViewById(R.id.linearlayoutdesign);
        linearLayout1=(LinearLayout)findViewById(R.id.linearlayoutdesign1);
        scrollView = (ScrollView)findViewById(R.id.scrollView1);
        myCameraPreview=(FrameLayout)findViewById(R.id.videoview);
        
       // recordstatusImageView = (ImageView)findViewById(R.id.recordstatus_imageView);
        //recordstatusImageView.setVisibility(View.INVISIBLE);
        // fetching all questions from DB
        Cursor cursor = sqliteDbhelper.getall_Practicalquestions();
        // if data exists in DB means no need to parse and save in DB it is
        // we need to call web service and save request string in DB
        // temporary now i am reading from asset
       // if (cursor.moveToNext()) {
        //} else {
            // saving xml parsing content to sqlite
          //  saving();
        //}
        // POWER MANAGEMENT while capturing videos mobile should not go to sleep
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My wakelook");
        wakeLock.acquire();
        // Get Camera for preview
        myCamera = getCameraInstance();
        if (myCamera == null) {
            Toast.makeText(PracticalActivity.this, "Failed to start Camera", Toast.LENGTH_LONG).show();
        }
        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        // Instantiating frame layout
        myCameraPreview.addView(myCameraSurfaceView);
        
        // Instantiating image views   
        startbutton = (ImageView)findViewById(R.id.imageView1);
        startbutton.setOnClickListener(myButtonOnClickListener);
        submiButton = (ImageView)findViewById(R.id.imageView2);
        submiButton.setEnabled(false);
        submiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (recording) {
                    Toast.makeText(getApplicationContext(), "Recording in progress, stop recording to continue to next question.", Toast.LENGTH_SHORT).show();
                } else {
                    if (qsntype.equalsIgnoreCase("Video") && filepath.length() <= 0) {
                        Toast.makeText(getApplicationContext(), "Video recording for the question is mandatory.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (shall_i_submit) {
                            boolean nomarks = false;
                            if (edittextarraylist.size() == 0) {
                                nomarks = true;
                            }
                            for (int i = 0; i < edittextarraylist.size(); i++) {
                                try {
                                    String enteredvalue = edittextarraylist.get(i).getText().toString().trim();
                                    if (enteredvalue.length() <= 0) {
                                    	nomarks = false;
                                        break;
                                    } else {
                                        nomarks = true;
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }
                            if (nomarks) {
                                onsubmit();
                            } else {
                                AlertDialog.Builder alertbuBuilder = new AlertDialog.Builder(context);
                                alertbuBuilder.setTitle("Marks not alloted for all the PCs");
                                alertbuBuilder.setMessage("Click yes to Continue").setCancelable(false);
                                alertbuBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        for (int i = 0; i < edittextarraylist.size(); i++) {
                                            try {
                                                String enteredvalue = edittextarraylist.get(i).getText().toString().trim();
                                                if (enteredvalue.length() <= 0) {
                                                    edittextarraylist.get(i).setText("0");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        onsubmit();
                                    }
                                });
                                alertbuBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog dialog = alertbuBuilder.create();
                                dialog.show();
                            }
                        } else {
                            boolean nomarks = false;
                            if (edittextarraylist.size() == 0) {
                                nomarks = true;
                            }
                            for (int i = 0; i < edittextarraylist.size(); i++) {
                                try {
                                    String enteredvalue = edittextarraylist.get(i).getText().toString().trim();
                                    if (enteredvalue.length() <= 0) {
                                        nomarks = false;
                                        break;
                                    } else {
                                        nomarks = true;
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }
                            if (nomarks) {
                                next_button();
                            } else {
                            
                                AlertDialog.Builder alertbuBuilder = new AlertDialog.Builder(context);
                                alertbuBuilder.setTitle("Marks not alloted for all the PCs");
                                alertbuBuilder.setMessage("Click yes to Continue").setCancelable(false);
                                alertbuBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                       for (int i = 0; i < edittextarraylist.size(); i++) {
                                            try {
                                                String enteredvalue = edittextarraylist.get(i).getText().toString().trim();
                                                if (enteredvalue.length() <= 0) {
                                                    edittextarraylist.get(i).setText("0");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        next_button();
                                    }
                                });
                                alertbuBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog dialog = alertbuBuilder.create();
                                dialog.show();
                            }
                        }
                    }
                }
            }
        });
        // if usersinfo exists in db means no need to parse and save in DB it
        // is... temporary we need to call web service and save request string
        // in DB..now i am reading from asset
       /* Cursor userCursor = sqliteDbhelper.getAllUser();
        if (userCursor.moveToNext()) {
        } else {
            saveUsersInfo();
        
        }*/
   
        
        // displaying first question
        view_button();
    }

    Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (recording) {
                try {
                    // stop recording and release camera
                    try {
                        mediaRecorder.stop(); // stop the recording
                        releaseMediaRecorder();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        // some time media recorded cant stop so
                        releaseMediaRecorder();
                    } // release the MediaRecorder object
                      // setting the BUTTON as RECORD
                    recordstatusImageView.setVisibility(View.INVISIBLE);
                    startbutton.setImageResource(R.drawable.startgreen);
                    recording = false;
                    releaseCamera();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                try {
                    if (filepath.length() <= 0) {
                        // Release Camera before MediaRecorder start
                        releaseCamera();
                        String currenttime = String.valueOf(System.currentTimeMillis());
                        currenttime = candiateid + "-" + qpaperid + "-" + qid + "-" + currenttime;
                        filepath = getResources().getString(R.string.sdcardpath) + currenttime + ".mp4";
                        if (!prepareMediaRecorder()) {
                            Toast.makeText(PracticalActivity.this, "Failed to prepare for MediaRecorder!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        mediaRecorder.start();
                        recording = true;
                        // setting the BUTTON as STOP
                        startbutton.setImageResource(R.drawable.stopgreen);
                        recordstatusImageView.setVisibility(View.VISIBLE);
                    } else {
                        // already video recorded for the question but user need
                        // to discard old one and like to record new video for
                        // current question
                        AlertDialog.Builder alertbuBuilder = new AlertDialog.Builder(context);
                        alertbuBuilder.setTitle("Response video has already been recorded");
                        alertbuBuilder.setMessage("Click yes to record new video ").setCancelable(false);
                        alertbuBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                // recording new video
                                releaseCamera();
                                String currenttime = String.valueOf(System.currentTimeMillis());
                                currenttime = candiateid + "-" + qpaperid + "-" + qid + "-" + currenttime;
                                filepath = getResources().getString(R.string.sdcardpath) + currenttime + ".mp4";
                                if (!prepareMediaRecorder()) {
                                    Toast.makeText(PracticalActivity.this, "Failed to prepare MediaRecorder!!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                mediaRecorder.start();
                                recording = true;
                                // setting the BUTTON as STOP
                                startbutton.setImageResource(R.drawable.stopgreen);
                                recordstatusImageView.setVisibility(View.VISIBLE);
                            }
                        });
                        alertbuBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = alertbuBuilder.create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

	
    private Camera getCameraInstance() {
        // TODO Auto-generated method stub
        Camera c = null;
        try {
            // attempt to get a Camera instance
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        // returns null if camera is unavailable
        return c;
    }

    @SuppressLint("SdCardPath")
    private boolean prepareMediaRecorder() {
        myCamera = getCameraInstance();
        mediaRecorder = new MediaRecorder();
        myCamera.unlock();
        mediaRecorder.setCamera(myCamera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
   
        // VIDEO QUALITY SETTING
        if (videoquality.equalsIgnoreCase("low")) {
            qualityis = 0;
        } else if (videoquality.equalsIgnoreCase("mid")) {
            qualityis = 2;
        } else if (videoquality.equalsIgnoreCase("high")) {
            qualityis = 7;
        }
      
        // setting video path
        mediaRecorder.setOutputFile(filepath);
        mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if you are using MediaRecorder, release it first
        releaseMediaRecorder();
        // release the camera immediately on pause event
        releaseCamera();
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            myCamera.lock(); // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (myCamera != null) {
            // release the camera for other applications
            myCamera.release();
            myCamera = null;
        }
    }

    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;

        private Camera mCamera;

        @SuppressWarnings("deprecation")
        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight, int height) {
            // If your preview can change or rotate, take care of those events
            // here.
            // Make sure to stop the preview before resizing or reformatting it.
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            // make any resize, rotate or reformatting changes here
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        }
        
       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.refresh):
                if (exam_start) {
                    Toast.makeText(getApplicationContext(), "Practical test in progress, can't refresh now", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(getIntent());
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    // first question of practical test
    @SuppressWarnings("static-access")
    public void view_button() {
    	
        cursor = sqliteDbhelper.getall_Practicalquestions();        
        size = cursor.getCount();
        if (cursor.moveToNext()) {
            cursorsize++;
            qid = cursor.getInt(0);
            String question = cursor.getString(1);
            qptypeid = cursor.getString(2);
            qsntype=cursor.getString(3);
            
            if(qsntype.equalsIgnoreCase("Video")){
            	//flag=true;
            	
            	startbutton = (ImageView)findViewById(R.id.imageView1);
            	startbutton.setVisibility(View.VISIBLE);
            	myCameraPreview.setVisibility(View.VISIBLE);
            	recordstatusImageView = (ImageView)findViewById(R.id.recordstatus_imageView);
            	recordstatusImageView.setVisibility(View.INVISIBLE);
            }else{
            	//flag=false;
            	//view=LayoutInflater.from(this).inflate(R.id.linearlayoutdesign1, null);
            	//parent.addView(view);
            	startbutton = (ImageView)findViewById(R.id.imageView1);
            	startbutton.setVisibility(View.INVISIBLE);
            	myCameraPreview.setVisibility(View.GONE);
            	recordstatusImageView = (ImageView)findViewById(R.id.recordstatus_imageView);
            	recordstatusImageView.setVisibility(View.INVISIBLE);
            	
            }
            TextView textview1 = new TextView(this);
            textview1.setTextSize(25);
            // textview1.setText(qid + ")." + question + "?");
            textview1.setText(cursorsize + ")." + question + "?");
            TextView textview2 = new TextView(this);
            textview2.setText("Parameters             ");
            textview2.setTextColor(android.graphics.Color.RED);
            TextView textview2a = new TextView(this);
            textview2a.setText("MAX     ");
            textview2a.setTextColor(android.graphics.Color.RED);
            TextView textview2b = new TextView(this);
            textview2b.setText("Obtained  ");
            textview2b.setTextColor(android.graphics.Color.RED);
            TableRow tableRowa = new TableRow(this);
            tableRowa.addView(textview2, 350, 50);
            // tableRow.addView(textview3);
            tableRowa.addView(textview2a, 90, 50);
            // tableRow.addView(editText);
            tableRowa.addView(textview2b, 130, 50);
            Cursor optionCursor = sqliteDbhelper.get_Practicaloptions(qid + "");
            linearLayout.addView(textview1,600,LinearLayout.LayoutParams.WRAP_CONTENT);
           // linearLayout.addView(tableRowa, new LinearLayout.LayoutParams(tableRowa.getLayoutParams().MATCH_PARENT, tableRowa.getLayoutParams().WRAP_CONTENT));
            // linearLayout.addView(textview2);
            // optionTextView.setText("");
            while (optionCursor.moveToNext()) {
                idis++;
                
                int optionid = optionCursor.getInt(0);
                String option = optionCursor.getString(1);
                String weightage = optionCursor.getString(2);
                LinearLayout Row = new LinearLayout(this);
                Row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 10);
                Row.setLayoutParams(params);
                optionidlist.add(optionid);
                max_marksentrylist.add(Double.parseDouble(weightage));
                TextView textview3 = new TextView(this);
              
                textview3.setText(option);
                TextView textview4 = new TextView(this);
                textview4.setText(weightage);
                editText = new EditText(this);
                // editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                edittextarraylist.ensureCapacity(edittextarraylist.size() + 1);
                edittextarraylist.add(editText);// Array list to store all
                                                // edittexts
                editText.setId(idis);
                LinearLayout.LayoutParams pr = new LinearLayout.LayoutParams(320,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                pr.setMargins(0, 0, 0, 20);
                textview3.setLayoutParams(pr);
                textview3.setBackgroundColor(Color.parseColor("#F5A9A9"));
                Row.addView(textview3);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(50,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(30, 0, 0, 0);
                textview4.setLayoutParams(p);
                Row.addView(textview4);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(100,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(40, 0, 0, 0);
                editText.setLayoutParams(param);
                Row.addView(editText);
              //  linearLayout.addView(Row);
                   
            
            
            }
            if (cursorsize == size) {
                shall_i_submit = true;
                submiButton.setEnabled(true);
                submiButton.setImageResource(R.drawable.submitgreen);
            } else {
                shall_i_submit = false;
                submiButton.setEnabled(true);
                submiButton.setImageResource(R.drawable.next);
            }
        } else {
            shall_i_submit = true;
            submiButton.setEnabled(true);
            submiButton.setImageResource(R.drawable.submitgreen);
        }
       
      
    }

    
    //String[] PcMarks=new String[100];
    
    public void next_button() {
        // onsaving();	
        if (edittextarraylist.size() == 0) {
            gofurther = true;
            
        }
             
        for (int i = 0; i < edittextarraylist.size(); i++) {
            try {
                  String enteredvalue = edittextarraylist.get(i).getText().toString().trim();
                 PcMarks[i]=enteredvalue;
                
                if (Double.parseDouble(enteredvalue) <= max_marksentrylist.get(i)) {
                    gofurther = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Marks alloted cannot exceed the maximum for that paramerter", Toast.LENGTH_SHORT).show();
                    gofurther = false;
                    edittextarraylist.get(i).requestFocus();
                    break;
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                gofurther = false;
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Alphabets and special characters not allowed", Toast.LENGTH_SHORT).show();
                edittextarraylist.get(i).requestFocus();
                break;
            }
        }
      
        
        
        if (gofurther) {
        	String videofilename=null;
        	if(qsntype.equalsIgnoreCase("Video")){
            File uploadFile = new File(filepath);
            videofilename = uploadFile.getName();
            sqliteDbhelper.addNewVideo(videofilename, "N", startTime);
        	}
        	
 
            // if not options/parameter
            if (edittextarraylist.size() == 0) {
            	 sqliteDbhelper.AddPracticalmarks(candiateid, qid + "", "nooptions" + "", "nomarks", videofilename, qptypeid);
                exam_start = true; // setting exam is going on
            } else {
                for (int i = 0; i < edittextarraylist.size(); i++) {
                    try {
                        String enteredvalue = edittextarraylist.get(i).getText().toString();
                        if (Double.parseDouble(enteredvalue) <= max_marksentrylist.get(i)) {
                            sqliteDbhelper.AddPracticalmarks(candiateid, qid + "", optionidlist.get(i) + "", enteredvalue, videofilename, qptypeid);
                            exam_start = true; // setting exam is going on
                        } else {
                            Toast.makeText(getApplicationContext(), "Marks alloted cannot exceed the maximum for that paramerter", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            
                       
            // reseting all values
            qid = 0;
            optionidlist.clear();
            max_marksentrylist.clear();
            edittextarraylist.clear();
            idis = 0;
            gofurther = false;
            button_next();
        }
    }

    @SuppressWarnings("static-access")
    public void button_next() {
    	
    	 Cursor usercursor=sqliteDbhelper.getAllUser();
         if(usercursor.moveToNext()){
        	if(resultgps!="feching"){
         	
         	sqliteDbhelper.locationupdate(resultgps);
       
         	
         	}
         	
         } 
    	scrollUp();
        filepath = ""; // for checking rakshit
        linearLayout.removeAllViews();
    
        if (cursor.moveToNext()) {
            cursorsize++;
            qid = cursor.getInt(0);
            String question = cursor.getString(1);
            qptypeid = cursor.getString(2);
            qsntype=cursor.getString(3);
            if(qsntype.equalsIgnoreCase("Video")){
            	//flag=true;
            	
            	startbutton = (ImageView)findViewById(R.id.imageView1);
            
            	startbutton.setVisibility(View.VISIBLE);
            	myCameraPreview.setVisibility(View.VISIBLE);
            	recordstatusImageView = (ImageView)findViewById(R.id.recordstatus_imageView);
            	recordstatusImageView.setVisibility(View.INVISIBLE);
            }else{
            	//flag=false;
            	startbutton = (ImageView)findViewById(R.id.imageView1);
            	startbutton.setVisibility(View.INVISIBLE);
            	myCameraPreview.setVisibility(View.GONE);
            	recordstatusImageView = (ImageView)findViewById(R.id.recordstatus_imageView);
            	recordstatusImageView.setVisibility(View.INVISIBLE);
            	
            }
            TextView textview1 = new TextView(this);
            textview1.setTextSize(25);
            // textview1.setText(qid + ")." + question + "?");
            textview1.setText(cursorsize + ")." + question + "?");
            TextView textview2 = new TextView(this);
            textview2.setText("Parameters             ");
            textview2.setTextColor(android.graphics.Color.RED);
            TextView textview2a = new TextView(this);
            textview2a.setText("MAX     ");
            textview2a.setTextColor(android.graphics.Color.RED);
            TextView textview2b = new TextView(this);
            textview2b.setText("Obtained  ");
            textview2b.setTextColor(android.graphics.Color.RED);
            TableRow tableRowa = new TableRow(this);
            tableRowa.addView(textview2, 350, 50);
            tableRowa.addView(textview2a, 90, 50);
            tableRowa.addView(textview2b, 130, 50);
            Cursor optionCursor = sqliteDbhelper.get_Practicaloptions(qid +" ");
            linearLayout.addView(textview1,600,LinearLayout.LayoutParams.WRAP_CONTENT);
           // linearLayout.addView(tableRowa, new LinearLayout.LayoutParams(tableRowa.getLayoutParams().MATCH_PARENT, tableRowa.getLayoutParams().WRAP_CONTENT));
            while (optionCursor.moveToNext()) {
                idis++;
                int optionid = optionCursor.getInt(0);
                String option = optionCursor.getString(1);
                String weightage = optionCursor.getString(2);
                optionidlist.add(optionid);
                max_marksentrylist.add(Double.parseDouble(weightage));
                LinearLayout Row = new LinearLayout(this);
                Row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 10);
                Row.setLayoutParams(params);
                TextView textview3 = new TextView(this);
                textview3.setText(option);
                TextView textview4 = new TextView(this);
                textview4.setText(weightage);
                editText = new EditText(this);
                // editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
                edittextarraylist.ensureCapacity(edittextarraylist.size() + 1);
                edittextarraylist.add(editText);// Array list to store all
                                                // edittexts
                editText.setId(idis);
                LinearLayout.LayoutParams pr = new LinearLayout.LayoutParams(320,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                pr.setMargins(0, 0, 0, 20);
                textview3.setLayoutParams(pr);
                textview3.setBackgroundColor(Color.parseColor("#F5A9A9"));
                Row.addView(textview3);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(50,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(30, 0, 0, 0);
                textview4.setLayoutParams(p);
                Row.addView(textview4);
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(100,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
                param.setMargins(40, 0, 0, 0);
                editText.setLayoutParams(param);
                Row.addView(editText);
               // linearLayout.addView(Row);
                  
            }
            if (cursorsize == size) {
                submiButton.setEnabled(true);
                shall_i_submit = true;
                submiButton.setImageResource(R.drawable.submitgreen);
            } else {
                submiButton.setEnabled(true);
                shall_i_submit = false;
                submiButton.setImageResource(R.drawable.next);
            }
        } else {
            submiButton.setEnabled(true);
            shall_i_submit = true;
            submiButton.setImageResource(R.drawable.submitgreen);
        }
    }

    public void onsubmit() {
       // onsaving();
        if (edittextarraylist.size() == 0) {
            gofurther = true;
        }
        for (int i = 0; i < edittextarraylist.size(); i++) {
            try {
                String enteredvalue = edittextarraylist.get(i).getText().toString().trim();
                if (Double.parseDouble(enteredvalue) <= max_marksentrylist.get(i)) {
                    gofurther = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Obtained marks cannot exceed maximum for the question.", Toast.LENGTH_SHORT).show();
                    gofurther = false;
                    edittextarraylist.get(i).requestFocus();
                    break;
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                gofurther = false;
                e.printStackTrace();
                edittextarraylist.get(i).requestFocus();
                Toast.makeText(getApplicationContext(), "Alphabets and special characters not allowed", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if (gofurther) {
        	String videofilename=null;
        	if(qsntype.equalsIgnoreCase("Video")){
            File uploadFile = new File(filepath);
             videofilename = uploadFile.getName();
            sqliteDbhelper.addNewVideo(videofilename, "N", startTime);
        	}
            if (edittextarraylist.size() == 0) {
            	sqliteDbhelper.AddPracticalmarks(candiateid, qid + "", "nooptions" + "", "nomarks", videofilename, qptypeid);
                exam_start = true; // setting exam is going on
            } else {
                for (int i = 0; i < edittextarraylist.size(); i++) {
                    try {
                        String enteredvalue = edittextarraylist.get(i).getText().toString();
                        if (Double.parseDouble(enteredvalue) <= max_marksentrylist.get(i)) {
                        	sqliteDbhelper.AddPracticalmarks(candiateid, qid + "", optionidlist.get(i) + "", enteredvalue, videofilename, qptypeid);
                            exam_start = true; // setting exam is going on
                        } else {
                            Toast.makeText(getApplicationContext(), "Obtained marks cannot exceed maximum for the question.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            // reseting all values
            qid = 0;
            optionidlist.clear();
            max_marksentrylist.clear();
            edittextarraylist.clear();
            idis = 0;
            gofurther = false;
           submitResponse();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void submitResponse() {
    	wakeLock.release();
    	 pr.PracticalResponseData(candiateid, startTime);
        sqliteDbhelper.UpdatePracticalStatus(candiateid, "Y");
        Intent intent = new Intent(getApplicationContext(), IntermediateActivity.class);
        intent.putExtra("candidate", candiateid);
        intent.putExtra("type", type);
        startActivity(intent);
        finish();
    }
   
  
    public void scrollUp() {
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(0, scrollView.getTop());
            }
        });
    }
   
  
    
    
    
    
    
    
}// class end tag
