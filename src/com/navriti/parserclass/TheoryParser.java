package com.navriti.parserclass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.widget.Toast;

import com.navriti.database.SqliteDbhelper;


public class TheoryParser {
	Context context;
	SqliteDbhelper sqliteDbhelper;
	
	
public TheoryParser(Context context){
	this.context = context;
	

}


public void parseXMl() {
      try {
    	  String img="";
    	  sqliteDbhelper=new SqliteDbhelper(context);
          // if u want request xml from sqlite
          String content = sqliteDbhelper.getSchedulerequest();
        
          XMLParser parser = new XMLParser();
          Document doc = parser.getDomElement(content); 
          
          //Assessment node
          NodeList nl = doc.getElementsByTagName("assessment"); 
          for (int i = 0; i < nl.getLength(); i++) {
              Element e = (Element)nl.item(i); 
              String attributename = e.getAttribute("type"); 
              if (!attributename.equalsIgnoreCase("PSYCHOMETRIC TEST")) {
                  NodeList nodelist_qpaper = e.getElementsByTagName("qpaper");
                  for (int q = 0; q < nodelist_qpaper.getLength(); q++) {
                   
                      Element element_qpaper = (Element)nodelist_qpaper.item(q);
              
                      String questionpaperid = element_qpaper.getAttribute("qpid");
              //Question Paper node           
                      NodeList nodelist_qpapertype = element_qpaper.getElementsByTagName("qpapertype");
                          for (int j = 0; j < nodelist_qpapertype.getLength(); j++) {
                              Element element = (Element)nodelist_qpapertype.item(j);
                              String questionpapertype = element.getAttribute("type");
                              String qptypeid_ = element.getAttribute("qptypeid");
                              if (questionpapertype.equalsIgnoreCase("Theory")) {
                                  // get list of all questions
                                  NodeList nodelist_question = element.getElementsByTagName("question");
                                  for (int k = 0; k < nodelist_question.getLength(); k++) {
                                	  String image="";
                                	 // Node child=nodelist_question.item(k);
                                	  Element element_question = (Element)nodelist_question.item(k); 
                                      String sectionid=element_question.getAttribute("sectionid");
                                      String sectionname=element_question.getAttribute("sectionname");
                                      String weightage=element_question.getAttribute("weightage");
                                      String qsnid = element_question.getAttribute("qsnid");
                                      String qsntype=element_question.getAttribute("qsntype");
                                      String questionis = parser.getValue(element_question, "text");
                                      image=parser.getImage(element_question, "image");
                                      questionis = questionis.replaceAll("'", "''");
                                     
                                      sqliteDbhelper.addnewquestiontheory(questionpaperid, questionis, qsnid, qsntype, 
                    		   		  sectionid, sectionname, weightage,image,qptypeid_);
                                        
                                     
                                 //for option data
                                 
                                 NodeList  nodelist_options=element_question.getElementsByTagName("options");
                                 for(int o=0;o<nodelist_options.getLength();o++)
                                 {
                                	 String imag="";
                                 Element element_options=(Element)nodelist_options.item(o);
                                 
                                 NodeList nodelist_option=element_options.getElementsByTagName("option");
                                 for(int op=0;op<nodelist_option.getLength();op++){
                              			   
                                 Element element_option=(Element)nodelist_option.item(op);
                                 String optionid=element_option.getAttribute("optnid");
                                 String optiontext=parser.getValue(element_option, "text");
                                 imag=parser.getImage(element_option, "image");
                                 optiontext=optiontext.replaceAll("'", "''");
                                 sqliteDbhelper.theoryoption(optionid, optiontext, qsnid,qptypeid_,imag);
                                 
                                 }    
                             }
                              		   
                          }  //end tag for option data                            
                              
                    }
                  }
                }
             }
       }
} catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
  }


//saving usersinfo
public void saveUsersInfo() {
    try {
   
        String content = sqliteDbhelper.getSchedulerequest();
        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(content); // getting DOM element
        NodeList nl = doc.getElementsByTagName("candidate");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element)nl.item(i);
            String candidateid = e.getAttribute("candidateid");
            String qpid = e.getAttribute("qpid");
            String scheduledate = e.getAttribute("scheduledate");
            String scheduleid = e.getAttribute("scheduleid");
            String assessorid = e.getAttribute("assessorid");
            String candidatename = e.getAttribute("candidatename");
            String password=e.getAttribute("password");
            sqliteDbhelper.newUser(candidateid, qpid, scheduledate, scheduleid, assessorid, candidatename,password);
            sqliteDbhelper.AddStatus(candidateid, "N", "N");
        }
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace(); 
    }
}

public void InstructionDetails()
{
	

	 try {
		 String theoryduration="";
		 String practicalduration="";
   	  sqliteDbhelper=new SqliteDbhelper(context);
         // if u want request xml from sqlite
         String content = sqliteDbhelper.getSchedulerequest();
       
         XMLParser parser = new XMLParser();
         Document doc = parser.getDomElement(content); 
         
         //Assessment node
         NodeList nl = doc.getElementsByTagName("assessment"); 
         for (int i = 0; i < nl.getLength(); i++) {
             Element e = (Element)nl.item(i); 
             String attributename = e.getAttribute("type"); 
             if (!attributename.equalsIgnoreCase("PSYCHOMETRIC TEST")) {
                 NodeList nodelist_qpaper = e.getElementsByTagName("qpaper");
                 for (int q = 0; q < nodelist_qpaper.getLength(); q++) {
                  
                     Element element_qpaper = (Element)nodelist_qpaper.item(q);
             
                     String questionpaperid = element_qpaper.getAttribute("qpid");
                     String instructiondetail=parser.getValue(element_qpaper, "instruction");
                     
             //Question Paper node           
                     NodeList nodelist_qpapertype = element_qpaper.getElementsByTagName("qpapertype");
                         for (int j = 0; j < nodelist_qpapertype.getLength(); j++) {
                             Element element = (Element)nodelist_qpapertype.item(j);
                             String questionpapertype = element.getAttribute("type");
                             String qptypeid_ = element.getAttribute("qptypeid");
                             if (questionpapertype.equalsIgnoreCase("Theory")) {
                          
                            	 theoryduration=element.getAttribute("duration");
                         }
                             if(questionpapertype.equalsIgnoreCase("Practical")){
                             
                            	 practicalduration=element.getAttribute("duration");
                      
                      }
                         
                         
                         
                         }
                         sqliteDbhelper.addInstruction(questionpaperid, instructiondetail, theoryduration, practicalduration);   
                      }
                        
                         }
                     }
                 
             

     } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     }
	}
	
public void parsePracticalQuestion() {
    try {
        // if u want request xml from sqlite
        String content = sqliteDbhelper.getSchedulerequest();
  
        XMLParser parser = new XMLParser();
        Document doc = parser.getDomElement(content); 
        NodeList nl = doc.getElementsByTagName("assessment"); 
                                                              
                                                                                         
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element)nl.item(i); // taking assessment element
            // add this to individual resposne db
            String attributename = e.getAttribute("type"); // assessment
                                                           // type
            // assessment type is not equal to PSYCHOMETRIC TEST we can
            // proceed bcoz in PSYCHOMETRIC TEST no practical test
            if (!attributename.equalsIgnoreCase("PSYCHOMETRIC TEST")) {
                // Toast.makeText(getApplicationContext(),
                // "NOT PSYCHOMETRIC", Toast.LENGTH_SHORT).show();
                // in assessment element take qpaper list
                NodeList nodelist_qpaper = e.getElementsByTagName("qpaper");
                for (int q = 0; q < nodelist_qpaper.getLength(); q++) {
                    // taking qpaper element
                    Element element_qpaper = (Element)nodelist_qpaper.item(q);
                    // qpaper qpid
                    String questionpaperid = element_qpaper.getAttribute("qpid");
                    // if qpid matches with given qpaperid
                   
                        NodeList nodelist_qpapertype = element_qpaper.getElementsByTagName("qpapertype");
                        for (int j = 0; j < nodelist_qpapertype.getLength(); j++) {
                            // taking qpapertype element
                            Element element = (Element)nodelist_qpapertype.item(j);
                            // taking type of qpapertype elementq
                            String questionpapertype = element.getAttribute("type");
                            String qptypeid_ = element.getAttribute("qptypeid");
                            // if qpaper type is equal to practical proceed
                            if (questionpapertype.equalsIgnoreCase("Practical")) {
                                // get list of all questions
                                NodeList nodelist_question = element.getElementsByTagName("question");
                                for (int k = 0; k < nodelist_question.getLength(); k++) {
                                    Element element_question = (Element)nodelist_question.item(k); // taking
                                                                                                   // question
                                    // element
                                    // taking qsnid of question element
                                    String qsnid = element_question.getAttribute("qsnid");
                                    String qsntype=element_question.getAttribute("qsntype");
                                    String questionis = parser.getValue(element_question, "text"); // taking
                                                                                                   // text
                                                                                                   // value
                                                                                                   // of
                                                                                                  // question
                                                                                                   // element
                                    questionis = questionis.replaceAll("'", "''");
                                    // adding qsnid and qustionis to
                                    // database
                                    // before adding check wheather candiate
                                    // already attend this question
                                   
                                    	sqliteDbhelper.AddtPracticalquestion(qsnid, questionis, qptypeid_,qsntype,questionpaperid);
                                        
                                 
                                    // taking parameters list of all current
                                    // question
                                    NodeList nodelist_parameters = element_question.getElementsByTagName("parameters");
                                    for (int z = 0; z < nodelist_parameters.getLength(); z++) {
                                        // taking element from parameters
                                        // list
                                        Element element_parameters = (Element)nodelist_parameters.item(z);
                                        // taking list from
                                        // element_parameter
                                        NodeList nodelist_parameter = element_parameters.getElementsByTagName("parameter");
                                        for (int p = 0; p < nodelist_parameter.getLength(); p++) {
                                            Element element_parameter = (Element)nodelist_parameter.item(p);
                                            String pid = element_parameter.getAttribute("id");
                                            String weightage = element_parameter.getAttribute("weightage");
                                            String option = parser.getValue(element_parameter, "text");
                                            option = option.replaceAll("'", "''");
                                   
                                                sqliteDbhelper.AddPracticaloption(pid, option, weightage, qsnid);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        
        // Toast.makeText(getApplicationContext(), content,
        // Toast.LENGTH_LONG).show();
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
	}

}//end class 
