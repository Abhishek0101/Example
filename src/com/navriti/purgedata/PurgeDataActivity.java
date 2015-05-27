
package com.navriti.purgedata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.app.certiplate.R;
import com.navriti.database.*;
import com.navriti.parserclass.*;
import com.navriti.skillahead.*;

public class PurgeDataActivity extends Activity {
    SqliteDbhelper sqliteDbhelper;

  //  Dbhelper dbhelper;
    
    SqliteDbhelper sqlitedbhelper;

    ProgressDialog dialog;

    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      
        super.onCreate(savedInstanceState);
       
        this.setContentView(R.layout.activity_admin);
        sqliteDbhelper = new SqliteDbhelper(this);
       // dbhelper = new Dbhelper(this);
        // getting asset manager instances
        assetManager = getAssets();
        // dialog settings
        dialog = new ProgressDialog(this);
        dialog.setTitle("Clearing All Data");
        dialog.setMessage("Pls wait..");
        dialog.setCancelable(false);
        AlertDialog.Builder alertbuBuilder = new AlertDialog.Builder(this);
        
        alertbuBuilder.setTitle("This action will delete all the data from device. Do you still want to continue?");
        alertbuBuilder.setMessage("Click yes to delete").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {                
                try {
                    sqliteDbhelper.delete_table_theoryquestion();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                try {
                    sqliteDbhelper.delete_table_optionstheory();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                try {
                    sqliteDbhelper.delete_table_instruction();;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                
                
                try {
                    sqliteDbhelper.delete_table_users();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_scheduleresponse();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                 try{
                    sqliteDbhelper.delete_table_schedulerequest();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_responsedata();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_videos();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_downloadstatus();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_locatoin();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_usersinfo();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_Practicalmarksobtained();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_Practicaloption();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_Practicalquestion();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_testStatus();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    sqliteDbhelper.delete_table_theoryResponse();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Deleting navritis folder
                try {
                    File file = new File(getResources().getString(R.string.sdcardpath));
                    if (file.isDirectory()) {
                        String[] children = file.list();
                        for (int i = 0; i < children.length; i++) {
                            new File(file, children[i]).delete();
                        }
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getBaseContext(), "Videos files not Deleted,Try Again", Toast.LENGTH_SHORT).show();
                    e1.printStackTrace();
                }
                // adding default values to the application
                try {
                    File file = new File(getResources().getString(R.string.sdcardpath));
                    file.mkdirs();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                // default admin login details
                String useris = sqliteDbhelper.getUsers();
                if (useris.equalsIgnoreCase("NO DATA")) {
                    try {
                        InputStream inputStream = assetManager.open("users.xml");
                        int size = inputStream.available();
                        byte[] byte_data = new byte[size];
                        inputStream.read(byte_data);
                        String content = new String(byte_data);
                        sqliteDbhelper.addUsers(content);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getBaseContext(), "Schedule and question paper data were successfully deleted from the device.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Toast.makeText(getBaseContext(), "you click No",
                // Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
        AlertDialog dialog = alertbuBuilder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
    }
    
    
}
