
package com.navriti.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.webkit.JavascriptInterface;

public class SqliteDbhelper extends SQLiteOpenHelper {
    Context context_;

    private static String databaseName = "certiplate";

    private static String table_schedulerequest = "schedulerequest";

    private static String table_scheduleresponse = "scheduleresponse";

    private static String table_responsedata = "responsedata";
    
    private static String table_status="teststatus";

    private static String table_theory_response = "theroryresponse";

    private static String table_videos = "practicaldata";

    private static String table_biometric = "biometricdata";

    private static String table_uploadStatus = "uploadstatus";
    
    private static String table_location="location";
    
    private static String table_usersinfo = "usersinfo";
    
    private static String table_download="downloadStatus";
    
    private static String table_users = "users";
    
    private static String table_theory="questions"; 
    
    private static String table_optiontheory="options";
    
    private static String table_instruction="instructiondetail";
    
    private static String table_practical_marksObtained="practicalmarks";
    
    private static String table_practical_question="practicalquestions"; 
    
    private static String table_practical_options="practicaloptions";
    
    private static String DATE_VIDEO="uploaded_dt";
    


    public SqliteDbhelper(Context context) {
        super(context, databaseName, null, 1);
        context_ = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String schedulerequest_table = "CREATE TABLE IF NOT EXISTS   " + table_schedulerequest + " (xml_data TEXT, download_dt TEXT)";
        db.execSQL(schedulerequest_table);
        String scheduleresponse_table = "CREATE TABLE IF NOT EXISTS " + table_scheduleresponse + " (xml_data TEXT)";
        db.execSQL(scheduleresponse_table);
        String responsedata_table = "CREATE TABLE IF NOT EXISTS   " + table_responsedata + " (id  INTEGER PRIMARY KEY autoincrement, candidateid_name TEXT, response_for TEXT, response_data TEXT, response_dt TEXT)";
        db.execSQL(responsedata_table);
        String response_table = "CREATE TABLE IF NOT EXISTS    " + table_theory_response + " (candidateid TEXT,qpid TEXT, qptypeid TEXT, qsnid TEXT, optionid TEXT)";
        db.execSQL(response_table);
        String videoinfo_table = "CREATE TABLE IF NOT EXISTS   " + table_videos + " (id  INTEGER PRIMARY KEY autoincrement,filename TEXT,uploaded_status TEXT, uploaded_dt TEXT, status TEXT)";
        db.execSQL(videoinfo_table);
        String biometric_table = "CREATE TABLE IF NOT EXISTS   " + table_biometric + " (imagebase64 TEXT, isobase64 TEXT)";
        db.execSQL(biometric_table);
        String uploadstatus_table = "CREATE TABLE IF NOT EXISTS   " + table_uploadStatus + " (videos TEXT, xml TEXT,acknowledgment TEXT,xmlfilename TEXT)";
        db.execSQL(uploadstatus_table);
        String locationupdate = "CREATE TABLE IF NOT EXISTS   " + table_location + " (lattitude INTEGER,longitude INTEGER,address TEXT,city TEXT,state TEXT,country TEXT)";
        db.execSQL(locationupdate);
        String downloadstatus_table = "CREATE TABLE IF NOT EXISTS   " + table_download + " (downloadpaper TEXT, getlocation TEXT,savelocation TEXT)";
        db.execSQL(downloadstatus_table);
        String question_theory = "CREATE TABLE IF NOT EXISTS   " + table_theory + " (qpid TEXT,question TEXT,qsnid TEXT,qsntype TEXT,sectionid TEXT,sectionname TEXT,weightage TEXT,image TEXT,qptypeid TEXT)";
        db.execSQL(question_theory);
        String theory_option = "CREATE TABLE IF NOT EXISTS   " + table_optiontheory + " (opid TEXT,option TEXT,qsnid TEXT,qpid TEXT, image TEXT)";
        db.execSQL(theory_option);
    
      
        String status_data = "CREATE TABLE IF NOT EXISTS   " + table_status+ " (candidateid TEXT,theorystatus TEXT,practicalstatus TEXT)";
        db.execSQL(status_data);
        
        String question_instruction= "CREATE TABLE IF NOT EXISTS   " + table_instruction+ " (qpid TEXT,instruction TEXT,theoryduration TEXT,practicalduration TEXT)";
        db.execSQL(question_instruction);
        
        String usersinfo_table = "CREATE TABLE IF NOT EXISTS   " + table_usersinfo + " (candidateid TEXT,  qpid  TEXT,  scheduledate TEXT, scheduleid TEXT , assessorid TEXT, candidatename TEXT,password TEXT)";
        db.execSQL(usersinfo_table);
        
        String practicalmarksobtained_table = "CREATE TABLE IF NOT EXISTS   " + table_practical_marksObtained + " (candidateid TEXT,  qsnid  TEXT,  optnid TEXT, obtainedmarks TEXT , videofilename TEXT, qptypeid TEXT)";
        db.execSQL(practicalmarksobtained_table);
        
        String practicalquestion_table = "CREATE TABLE  IF NOT EXISTS "+table_practical_question+"(questionid INTEGER PRIMARY KEY , question TEXT not null, qptypeid TEXT, qsntype TEXT,qpid TEXT )";
        db.execSQL(practicalquestion_table);
        
        String practicaloption_table = "CREATE TABLE IF NOT EXISTS "+table_practical_options+"  (optionid  INTEGER PRIMARY KEY ,  option  TEXT,   weightage TEXT,  questionidis INTEGER, FOREIGN KEY (questionidis)  REFERENCES questions(questionid) )";
        db.execSQL(practicaloption_table);
        
        String users_table = "CREATE TABLE IF NOT EXISTS    " + table_users + " (xmldata TEXT)";
        db.execSQL(users_table);
    
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + table_schedulerequest);
        db.execSQL("DROP TABLE IF EXISTS " + table_scheduleresponse);
        db.execSQL("DROP TABLE IF EXISTS " + table_responsedata);
        db.execSQL("DROP TABLE IF EXISTS " + table_theory_response);
        db.execSQL("DROP TABLE IF EXISTS " + table_videos);
        db.execSQL("DROP TABLE IF EXISTS " + table_biometric);
        db.execSQL("DROP TABLE IF EXISTS " + table_uploadStatus);
        db.execSQL("DROP TABLE IF EXISTS " + table_download);
        db.execSQL("DROP TABLE IF EXISTS " + table_location);
        db.execSQL("DROP TABLE IF EXISTS " + table_theory);
        db.execSQL("DROP TABLE IF EXISTS " + table_optiontheory);
        db.execSQL("DROP TABLE IF EXISTS " + table_status);
        db.execSQL("DROP TABLE IF EXISTS " + table_instruction);
        db.execSQL("DROP TABLE IF EXISTS " + table_usersinfo);
        db.execSQL("DROP TABLE IF EXISTS " + table_users);
        db.execSQL("DROP TABLE IF EXISTS " + table_practical_marksObtained);
        db.execSQL("DROP TABLE IF EXISTS " + table_practical_options);
        db.execSQL("DROP TABLE IF EXISTS " + table_practical_question);
        onCreate(db);
        db.close();
    }

    // schedulerequest
    @JavascriptInterface
    public void addSchedulerequest(String xml_data, String download_dt) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_schedulerequest + "(xml_data,download_dt) VALUES('" + xml_data + "','" + download_dt + "' )");
        database.close();
    }

    @JavascriptInterface
    public String getSchedulerequest() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_schedulerequest, null);
        if (cursor.moveToNext()) {
            String xml_data = cursor.getString(0);
            // String download_dt = cursor.getString(1);
            db.close();
            return xml_data;
        } else {
            db.close();
            return "NO DATA";
        }
    }

    // scheduleresponse
    @JavascriptInterface
    public void addScheduleresponse(String xml_data) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_scheduleresponse + "(xml_data) VALUES('" + xml_data + "' )");
        database.close();
    }

    @JavascriptInterface
    public String getScheduleresponse() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_scheduleresponse, null);
        if (cursor.moveToNext()) {
            String xml_data = cursor.getString(0);
            db.close();
            return xml_data;
        } else {
            db.close();
            return "NO DATA";
        }
    }

    @JavascriptInterface
    public void updateScheduleresponse(String newSchedulerResponse) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_scheduleresponse + " SET xml_data = '" + newSchedulerResponse + "' ");
        database.close();
    }

    // responsedata
    @JavascriptInterface
    public void addResponsedata(String candidateid_name, String response_for, String response_data, String response_dt) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_responsedata + "(candidateid_name,response_for,response_data,response_dt) VALUES('" + candidateid_name + "','" + response_for + "' ,'" + response_data + "' ,'" + response_dt + "' )");
        database.close();
    }

    @JavascriptInterface
    public Cursor getResponsedata() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_responsedata, null);
        return cursor;
    }

    // Theory Response
    public void addTheoryResponse(String candidateid, String qpid, String qptypeid, String qsnid, String optionid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_theory_response + "(candidateid,qpid,qptypeid,qsnid,optionid) VALUES('" +candidateid + "','"+qpid + "','" + qptypeid + "' ,'" + qsnid + "' ,'" + optionid + "' )");
        database.close();
    }
    
    public String getTheoryResponse(String candidateid, String qsnid){
    	SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_theory_response+" WHERE candidateid='"+candidateid+"'"+" AND qsnid='"+qsnid+"'", null);
    	if(cursor.moveToNext())
    		return cursor.getString(4);
    	else 
    		return "-1";
    }

    public void updateTheoryResponse(String candidateid, String qsnid, String optionid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_theory_response + " SET optionid = '" + optionid + "' "+" WHERE candidateid='"+candidateid+"'"+" AND qsnid='"+qsnid+"' ");
        database.close();
    }
    
    public void delete_TheoryResponse(String candidateid) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE optionid FROM " + table_theory_response + " WHERE candidateid='"+candidateid+"' ");
        database.close();
    }

    public void addNewVideo(String filename, String uploaded_status, String uploaded_dt) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_videos + "(filename,uploaded_status,uploaded_dt) VALUES ('" + filename + "','" + uploaded_status + "','" + uploaded_dt + "' )");
        database.close();
    }

    public Cursor getvideodetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT * FROM " + table_videos , null);
      //  Cursor cursor=db.query(table_videos, null, null, null, null, null,+"");
      Cursor cursor=db.rawQuery("SELECT * FROM " + table_videos +
              " ORDER BY "+DATE_VIDEO + " DESC"
              , new String[] {});
        return cursor;
    }

    public String getvideodetail(String filename) {
        String status = "NOTAVAILABLE";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  " + table_videos + " where filename = '" + filename + "' ", null);
        if (cursor.moveToNext()) {
            status = cursor.getString(2);
        }
        return status;
    }

    public void updateVideoStatus(String filename, String status) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_videos + " SET uploaded_status = '" + status + "' where filename ='" + filename + "' ");
        database.close();
    }

    public void updateVideoStatus(String filename, String status, String uploaddate) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_videos + " SET uploaded_status = '" + status + "' , uploaded_dt = '" + uploaddate + "' where filename ='" + filename + "' ");
        database.close();
    }
    
    public void updateUVideoStatus(String filename, String status) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_videos + " SET  status = '" + status + "' where filename ='" + filename + "' ");
        database.close();
    }

    public void updateallVideoStaus() {
        String status = "N";
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_videos + " SET uploaded_status = '" + status + "'  ");
        database.close();
    }

    public void addingBiometricData(String imagebase64, String isobase64) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_biometric + "(imagebase64,isobase64) VALUES ('" + imagebase64 + "','" + isobase64 + "' )");
        database.close();
    }

    @JavascriptInterface
    public String getBiometricBase64() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  " + table_biometric + " ", null);
        if (cursor.moveToNext()) {
            String biometricBase64 = cursor.getString(0);
            biometricBase64 = "data:image/jpeg;base64," + biometricBase64;
            db.close();
            return biometricBase64;
        } else {
            db.close();
            return "NO DATA";
        }
    }

    @JavascriptInterface
    public String getBiometricIsoBase64() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  " + table_biometric + " ", null);
        if (cursor.moveToNext()) {
            String biometricIsoBase64 = cursor.getString(1);
            db.close();
            return biometricIsoBase64;
        } else {
            db.close();
            return "NO DATA";
        }
    }

    @JavascriptInterface
    public void clearBiometricData() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_biometric + "   ");
        database.close();
    }

    // upload status maintain NOTYET,DONE,PENDING
    @JavascriptInterface
    public void addDefulatuploadStatus(String videoStatus, String xmlStatus, String ackStatus) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_uploadStatus + "(videos, xml, acknowledgment) VALUES('" + videoStatus + "','" + xmlStatus + "','" + ackStatus + "' )");
        database.close();
    }
    
    public void addDefultdownloadStatus(String downloadStatus, String getlocationStatus, String savelocStatus) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_download + "(downloadpaper, getlocation, savelocation) VALUES('" + downloadStatus + "','" + getlocationStatus+ "','" + savelocStatus + "' )");
        database.close();
    }

    public void addLocation(double lattitude, double longitude, String address,String city,String state,String country) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("INSERT INTO " + table_location + "(lattitude, longitude, address,city,state,country) VALUES('" + lattitude + "','" + longitude+ "','" + address + "','"+city+"','"+state+"','"+country+"' )");
        database.close();
    }
    
    
    
    
    @JavascriptInterface
    public Cursor getUploadStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_uploadStatus, null);
        return cursor;
    }
    
    
    @JavascriptInterface
    public Cursor getLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table_location, null);
        return cursor;
    }
    
    
    
    @JavascriptInterface
  public Cursor getDownloadStatus() {
        SQLiteDatabase db = this.getReadableDatabase();
       Cursor cursor = db.rawQuery("SELECT * FROM " + table_download, null);
       return cursor;
    }

    public void updateDownloadPaperStatus(String downloadstatus) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_download + " SET  downloadpaper = '" + downloadstatus + "' ");
        database.close();
    }

    public void updateGetLocationStatus(String getlocation) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_download + " SET getlocation = '" + getlocation + "'");
        database.close();
    }

    public void updateSaveLocationStatus(String savelocation) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_download + " SET savelocation= '" + savelocation + "' ");
        database.close();
    }

    @JavascriptInterface
    public void clearDownloadStatus() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_download + "   ");
        database.close();
    }
    
    public void updateVideoUploadStatus(String videostatus) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_uploadStatus + " SET videos = '" + videostatus + "' ");
        database.close();
    }

    public void updateXmlUploadStatus(String xmlstatus, String xmlfilename) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_uploadStatus + " SET xml = '" + xmlstatus + "' , xmlfilename = '" + xmlfilename + "' ");
        database.close();
    }

    public void updateAckUploadStatus(String ackstatus) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("UPDATE " + table_uploadStatus + " SET acknowledgment = '" + ackstatus + "' ");
        database.close();
    }

    
    //Location Update 
    public void locationupdate(String address){
    	
    	 SQLiteDatabase database = this.getWritableDatabase();
         database.execSQL("UPDATE " + table_location + " SET address='"+address+"' ");
         database.close();
    	
    }
    
    
    @JavascriptInterface
    public void clearUploadStatus() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_uploadStatus + "   ");
        database.close();
    }

   
    
    // for purge data call the following method one by one to clear the sqlite
    // db
    public void delete_table_theoryResponse() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_theory_response + "   ");
        database.close();
    }

    public void delete_table_scheduleresponse() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_scheduleresponse + "   ");
        database.close();
    }

    public void delete_table_schedulerequest() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_schedulerequest + "   ");
        database.close();
    }

    public void delete_table_responsedata() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_responsedata + "   ");
        database.close();
    }

    public void delete_table_videos() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE  FROM " + table_videos + "   ");
        database.close();
    }
    
    public void delete_table_locatoin()
    {
    	
    	  SQLiteDatabase database = this.getWritableDatabase();
          database.execSQL("DELETE  FROM " + table_location + "   ");
          database.close();
    	
    	
    	
    }
public void delete_table_downloadstatus(){
	
	 SQLiteDatabase database = this.getWritableDatabase();
     database.execSQL("DELETE  FROM " + table_download + "   ");
     database.close();
}

public void delete_table_theoryquestion(){
	
	 SQLiteDatabase database = this.getWritableDatabase();
     database.execSQL("DELETE  FROM " + table_theory + "   ");
     database.close();}

public void delete_table_optionstheory(){
	
	 SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_optiontheory + "   ");
    database.close();}

public void delete_table_instruction(){
	
	 SQLiteDatabase database = this.getWritableDatabase();
   database.execSQL("DELETE  FROM " + table_instruction + "   ");
   database.close();}

public void addnewquestiontheory(String qpid, String question, String qsnid,String qsntype,String sectionid,String sectionname,String weightage, String image, String qptypeid){
	
	SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_theory + "(qpid,question,qsnid,qsntype,sectionid,sectionname,weightage,image,qptypeid) VALUES( '" + qpid + "', '" + question + "','" +qsnid + "','" + qsntype + "','"+sectionid+"','"+sectionname+"','"+weightage+"','"+image+"','"+qptypeid+"' )");
    database.close();
	
	
}

public void theoryoption(String opid, String option, String qsnid, String qpid,String image){
	
	SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_optiontheory + "(opid,option,qsnid,qpid,image) VALUES( '" + opid + "', '" + option + "','" +qsnid + "','"+qpid+"','"+image+"')");
    database.close();	
}

public void addInstruction(String qpid, String instruction, String theoryduration,String practicalduration){
	
	SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_instruction+ "(qpid,instruction,theoryduration,practicalduration) VALUES( '" + qpid + "', '" + instruction + "','" +theoryduration + "','"+practicalduration+"')");
    database.close();
}
public Cursor getdetails(String qpid){
	SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " +table_instruction+" WHERE qpid="+qpid , null);
    return cursor;
}


public Cursor getCandidate(String candidateid){
	String qpid;
	Cursor cursor;
	qpid=getqpid(candidateid);
	cursor=getQuestion(qpid);
	return cursor;
}
 
public String getqpid(String candidateid){
	 Cursor cursor = null;
     String qpidnumber ="";
     SQLiteDatabase database = this.getReadableDatabase();
     cursor = database.rawQuery("SELECT * FROM "+ table_usersinfo+" WHERE candidateid="+candidateid, null);
     cursor.moveToNext();
     qpidnumber = cursor.getString(1);
   return  qpidnumber;
}

public Cursor getQuestion(String qpid){
	
	SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_theory+" WHERE qpid="+qpid , null);
    return cursor;
}

public Cursor getStatus(){
	SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_status, null);
    return cursor;
}
public void AddStatus(String candidateid, String theory, String practical){
	SQLiteDatabase database = this.getWritableDatabase();
	 database.execSQL("INSERT INTO " + table_status+ "(candidateid,theorystatus,practicalstatus) "
	 		+ "VALUES( '" + candidateid + "', '" + theory + "','" +practical+"')");
	 database.close();
}
public void UpdateTheoryStatus(String candidateid, String theory){
	SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("UPDATE " + table_status + " SET theorystatus = '" + theory + "' "+" WHERE candidateid='"+candidateid+"' ");
    database.close();
}
public void UpdatePracticalStatus(String candidateid, String practical){
	SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("UPDATE " + table_status + " SET practicalstatus = '" + practical + "' "+" WHERE candidateid='"+candidateid+"' ");
    database.close();
}

public boolean getTheoryStatus(String candidateid){

	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery("SELECT * FROM " + table_status +" WHERE candidateid='"+candidateid+"'", null);
	cursor.moveToNext();
	if(cursor.getString(1).equalsIgnoreCase("Y"))
		return true;
	else
		return false;
}
public boolean getPracticalStatus(String candidateid){

	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery("SELECT * FROM " + table_status +" WHERE candidateid='"+candidateid+"'", null);
	cursor.moveToNext();
	if(cursor.getString(2).equalsIgnoreCase("Y"))
		return true;
	else
		return false;
}

public boolean getCandidateVerify(String candidateid){
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery("SELECT candidateid FROM " + table_usersinfo +" WHERE candidateid='"+candidateid+"'", null);
	if (cursor.moveToNext()) {
            
		db.close();
		return true;
	}else {
		db.close();
		return false;
	}
}

public Cursor getoption(String qsnid){
	 Cursor cursor = null;
    SQLiteDatabase database = this.getReadableDatabase();
    cursor = database.rawQuery("SELECT * FROM "+ table_optiontheory+" WHERE qsnid="+qsnid, null);
  return  cursor;
}

public Boolean cmpltCandidateVerify(String candidateid, String password) {
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor = db.rawQuery("SELECT candidateid FROM " + table_usersinfo +" WHERE candidateid='"+candidateid+"'"
		+" AND WHERE password='"+password+"'", null);
	if (cursor.moveToNext()) {
            
		db.close();
		return true;
	}else {
		db.close();
    return false;
	}
}  
public void delete_table_usersinfo() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_usersinfo + "   ");
    database.close();
}

public void newUser(String candidateid, String qpid, String scheduledate, String scheduleid, String assessorid, String candidatename, String password) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_usersinfo + "(candidateid,qpid,scheduledate,scheduleid,assessorid, candidatename,password) VALUES('" + candidateid + "','" + qpid + "' ,'" + scheduledate + "' ,'" + scheduleid + "' ,'" + assessorid + "' ,'" + candidatename + "','"+password+"' )");
    database.close();
}

public Cursor getall_Practicalquestions() {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_question, null);
    return cursor;
}
public Cursor getall_Practicaloptions() {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_options, null);
    return cursor;
}

public void delete_table_Practicalmarksobtained() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_practical_marksObtained + "   ");
    database.close();
}

public void delete_table_Practicaloption() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_practical_options + "   ");
    database.close();
}

public void delete_table_Practicalquestion() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_practical_question + "   ");
    database.close();
}

public void AddtPracticalquestion(String qid, String question, String qptypeid, String qsntype,String qpid) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_practical_question + "(questionid,question, qptypeid,qsntype,qpid) VALUES(" + qid + ",'" + question + "' ," + qptypeid + ",'"+ qsntype +"','"+qpid+"')");
    database.close();
}
public Cursor GetPracQuestion(String candidateid){
	SQLiteDatabase db = this.getReadableDatabase();
	String qpid;
	qpid = getqpid(candidateid);
	Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_question + " where qpid =" + qpid, null);
	return cursor;
}

public void AddPracticalmarks(String candidateid, String qsnid, String optnid, String obtainedmarks, String videofilename, String qptypeid) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_practical_marksObtained + "(candidateid,qsnid,optnid,obtainedmarks,videofilename, qptypeid) VALUES('" + candidateid + "','" + qsnid + "' ,'" + optnid + "' ,'" + obtainedmarks + "' ,'" + videofilename + "' ,'" + qptypeid + "' )");
    database.close();
}

public Cursor getAllUser() {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM  " + table_usersinfo + "  ", null);
    return cursor;

}

public Cursor get_Practicaloptions(String qid) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT optionid,option,weightage FROM " + table_practical_options + " where questionidis =" + qid, null);
    return cursor;
}

public void AddPracticaloption(String optionid, String option, String weightage, String qidis) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_practical_options + "(optionid,option,weightage,questionidis) VALUES( " + optionid + ", '" + option + "','" + weightage + "'," + qidis + " )");
    database.close();
}

public Cursor checkingAttend(String candidateid, String qid) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_marksObtained + " where candidateid ='" + candidateid + "' and qsnid = '" + qid + "'   ", null);
    return cursor;
}

public Cursor checkingOptionAttend(String candidateid, String optionid) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_marksObtained + " where candidateid ='" + candidateid + "' and optnid = '" + optionid + "'   ", null);
    return cursor;
}

public String get_qptypeid(String candidateid) {
    String qptypeid = "";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_marksObtained + " where candidateid ='" + candidateid + "' ", null);
    if (cursor.moveToNext()) {
        qptypeid = cursor.getString(5);
    }
    return qptypeid;
}

public Cursor getall_PracticalMarks(String candidateid) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_marksObtained + " where candidateid ='" + candidateid + "' ", null);
    return cursor;
}

public Cursor getall_marks_Practical(String candidateid, String qsnid) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_marksObtained + " where  candidateid = '" + candidateid + "' and qsnid = " + qsnid, null);
    return cursor;
}

public Cursor getUser(String candidateid) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_usersinfo + " where candidateid = '" + candidateid + "' ", null);
    return cursor;
}

public void delete_table_users() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_users + "   ");
    database.close();
}

public String getUsers() {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_users, null);
    if (cursor.moveToNext()) {
        String xmldata = cursor.getString(0);
        db.close();
        return xmldata;
    } else {
        db.close();
        return "NO DATA";
    }
}

public void addUsers(String xmldata) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("INSERT INTO " + table_users + "(xmldata) VALUES('" + xmldata + "' )");
    database.close();
}

public void update_users(String newXmldata) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("UPDATE " + table_users + " SET xmldata = '" + newXmldata + "' ");
    database.close();
}
public Cursor getall_TheoryMarks(String candidateid) {
	
	
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_theory_response+ " where candidateid ='" + candidateid + "' ", null);
    return cursor;
}


public String get_qptypeidfortheory(String candidateid) {
    String qptypeid = "";
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_theory_response+ " where candidateid ='" + candidateid + "' ", null);
    if (cursor.moveToNext()) {
        qptypeid = cursor.getString(2);
    }
    return qptypeid;
}

//From here
public Cursor getPracQuestions(String qpid){
	SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT * FROM " + table_practical_question + " where qpid = '" + qpid + "' ", null);
    return cursor;
}

public int getPractCount(String candidateid){
	int count=0;
	Boolean flag;
	String qpid;
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cursor, cursor1;
	qpid = getqpid(candidateid);
	cursor = getPracQuestions(qpid);
	while(cursor.moveToNext()){	
		flag=false;
		cursor1 = db.rawQuery("SELECT * FROM " + table_practical_marksObtained + " where qsnid ='" + cursor.getString(0)
				+"' AND candidateid='"+candidateid+ "' ", null);
		while(cursor1.moveToNext()){
			if(!cursor1.getString(3).equalsIgnoreCase("nomarks"))
			if(Integer.parseInt(cursor1.getString(3))>0){
				flag = true;
				break;
			}
		}
		if(flag==true)
			count++;
	}	
	return count;
}

public void delete_DownloadStatus() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_download + "   ");
    database.close();
}

public void delete_testStatus() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("DELETE  FROM " + table_status + "   ");
    database.close();
}

}//class end 
