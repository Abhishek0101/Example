package com.navriti.defaultresponse;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.navriti.database.SqliteDbhelper;
import com.navriti.parserclass.XMLParser;

public class PracticalResponse {

	SqliteDbhelper db;
	Context context;
	String endTime, Latitude, Longitude, Address, CityState, Country;
	Handler handler;
	
	public PracticalResponse(Context context){
		
		
		this.context=context;
		db=new SqliteDbhelper(context);
		
		
	}
	
	public void PracticalResponseData(String candidateid, String startTime){
		 	
			String individualresponse = "";
	        // end time
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
	        endTime = dateFormat.format(new Date());
	        boolean candiateExist = false;
	        
	        handler = new Handler();
	        // getting schedule reponse
	        String schecdulereponse = db.getScheduleresponse();
	     
	        Cursor cursor=db.getLocation();
	        if(cursor.moveToNext()){
	        	Latitude=cursor.getString(0);
	        	Longitude=cursor.getString(1);
	        	Address=cursor.getString(2);
	        	CityState=cursor.getString(4);
	        	Country=cursor.getString(5);
	        }
	        
	        
	        XMLParser parser = new XMLParser();
	        Document doc = parser.getDomElement(schecdulereponse); // getting DOM
	                                                               // element
	        NodeList nl = doc.getElementsByTagName("candidate");
	        for (int i = 0; i < nl.getLength(); i++) {
	            Element e = (Element)nl.item(i);
	            String attributename = e.getAttribute("candidateid");
	            if (attributename.equalsIgnoreCase(candidateid)) {
	                candiateExist = true;
	                NodeList assesementList = e.getElementsByTagName("assessment");
	                Node assementnode = (Element)assesementList.item(0);
	                Element qpapertype = doc.createElement("qpapertype");
	                qpapertype.setAttribute("type", "Practical");
	                String candiate_qptypeid = db.get_qptypeid(candidateid);
	                qpapertype.setAttribute("qptypeid", candiate_qptypeid);
	                Element startElement = doc.createElement("startdatetime");
	                startElement.appendChild(doc.createTextNode(startTime));
	                qpapertype.appendChild(startElement);
	                Element endElement = doc.createElement("enddatetime");
	                endElement.appendChild(doc.createTextNode(endTime));
	                qpapertype.appendChild(endElement);
	                
	                //gps
	                Element latitude=doc.createElement("Latitude");
	                latitude.appendChild(doc.createTextNode(Latitude));
	                qpapertype.appendChild(latitude);
	                
	                Element longitude=doc.createElement("Longitude");
	                longitude.appendChild(doc.createTextNode(Longitude));
	                qpapertype.appendChild(longitude);
	              
	                Element address=doc.createElement("Address");
	                address.appendChild(doc.createTextNode(Address));
	                qpapertype.appendChild(address) ; 
	                
	                Element citystate=doc.createElement("CityState");
	                citystate.appendChild(doc.createTextNode(CityState));
	                qpapertype.appendChild(citystate) ; 
	                
	                Element country=doc.createElement("Country");
	                country.appendChild(doc.createTextNode(Country));
	                qpapertype.appendChild(country) ; 
	                
	                Element responsesElement = doc.createElement("responses");
	                qpapertype.appendChild(responsesElement);
	                Cursor qcursor = db. GetPracQuestion(candidateid);
	                while (qcursor.moveToNext()) {
	                    String qsnid = qcursor.getString(0);
	                    Element response_Element = doc.createElement("response");
                        response_Element.setAttribute("qsnid", qsnid);
                        Cursor optionCursor = db.getall_marks_Practical(candidateid, qsnid);
                        while (optionCursor.moveToNext()) {
	                      String optnid_ = optionCursor.getString(2);
	                      String obtainedmarks_ = optionCursor.getString(3);
	                      if(obtainedmarks_.equalsIgnoreCase("nomarks"))
	                    	  break;
	                      Element optionElement_ = doc.createElement("option");
	                      optionElement_.setAttribute("optnid", optnid_);
	                      optionElement_.setAttribute("obtainedmarks", obtainedmarks_);
	                      response_Element.appendChild(optionElement_);
                        }
                        optionCursor.moveToPosition(0);
                        String videofilename = optionCursor.getString(4);
	                    if(!videofilename.equalsIgnoreCase("null")){
	                    	Element videoElement = doc.createElement("video");
	                    	videoElement.setAttribute("filename", videofilename);
	                    	response_Element.appendChild(videoElement);	
	                    }
	                    responsesElement.appendChild(response_Element);
	                               
	                }
	                assementnode.appendChild(qpapertype);
	                // here i need to add individual response to db ..... use
	                // qpapertype element
	                // start
	                try {
	                    Node node = qpapertype;
	                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	                    factory.setNamespaceAware(true);
	                    DocumentBuilder builder = factory.newDocumentBuilder();
	                    Document newDocument = builder.newDocument();
	                    Node importedNode = newDocument.importNode(node, true);
	                    newDocument.appendChild(importedNode);
	                    TransformerFactory tf = TransformerFactory.newInstance();
	                    Transformer transformer = tf.newTransformer();
	                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	                    StringWriter writer = new StringWriter();
	                    transformer.transform(new DOMSource(newDocument), new StreamResult(writer));
	                    String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
	                    db.addResponsedata(candidateid, "manually written job", output, endTime);
	                    // nodlistTextView.setText(output+"");
	                } catch (Exception e1) {
	                    // TODO Auto-generated catch block
	                    e1.printStackTrace();
	                }
	                // end
	            }
	        }
	        if (!candiateExist) {//
	            // qtypeid is used in both candidate and qpapertype
	            String candiate_qptypeid = db.get_qptypeid(candidateid);
	            NodeList nlcandidates = doc.getElementsByTagName("candidates");
	            Element candidatesElement = (Element)nlcandidates.item(0);
	            // candidate element
	            Element candidateElement = doc.createElement("candidate");
	            candidateElement.setAttribute("candidateid", candidateid);
	            candidateElement.setAttribute("qptypeid", candiate_qptypeid);
	            Cursor candiate_cursor = db.getUser(candidateid);
	            String sid = "", asid = "";
	            if (candiate_cursor.moveToNext()) {
	                sid = candiate_cursor.getString(3);
	                asid = candiate_cursor.getString(4);
	            }
	            candidateElement.setAttribute("scheduleid", sid);
	            candidateElement.setAttribute("assessorid", asid);
	            
	            Element assessmentElement = doc.createElement("assessment");
	            String qpid = db.getqpid(candidateid);
	            assessmentElement.setAttribute("qpid", qpid);
	            candidateElement.appendChild(assessmentElement);
	            Element qpapertype = doc.createElement("qpapertype");
	            qpapertype.setAttribute("type", "Practical");
	            qpapertype.setAttribute("qptypeid", candiate_qptypeid);
	            Element startElement = doc.createElement("startdatetime");
	            startElement.appendChild(doc.createTextNode(startTime));
	            qpapertype.appendChild(startElement);
	            Element endElement = doc.createElement("enddatetime");
	            endElement.appendChild(doc.createTextNode(endTime));
	            qpapertype.appendChild(endElement);
	            
	            //gps node elements
	            Element latitude=doc.createElement("Latitude");
	            latitude.appendChild(doc.createTextNode(Latitude));
	            qpapertype.appendChild(latitude);
	            
	            Element longitude=doc.createElement("Longitude");
	            longitude.appendChild(doc.createTextNode(Longitude));
	            qpapertype.appendChild(longitude);
	          
	            Element address=doc.createElement("Address");
	            address.appendChild(doc.createTextNode(Address));
	            qpapertype.appendChild(address) ;  
	            
	            Element citystate=doc.createElement("CityState");
	            citystate.appendChild(doc.createTextNode(CityState));
	            qpapertype.appendChild(citystate) ; 
	            
	            Element country=doc.createElement("Country");
	            country.appendChild(doc.createTextNode(Country));
	            qpapertype.appendChild(country) ; 
	            
	            Element responsesElement = doc.createElement("responses");
	            qpapertype.appendChild(responsesElement);
	            Cursor qcursor = db. GetPracQuestion(candidateid);
                while (qcursor.moveToNext()) {
                    String qsnid = qcursor.getString(0);
                    Element response_Element = doc.createElement("response");
                    response_Element.setAttribute("qsnid", qsnid);
                    Cursor optionCursor = db.getall_marks_Practical(candidateid, qsnid);
                    while (optionCursor.moveToNext()) {
                      String optnid_ = optionCursor.getString(2);
                      String obtainedmarks_ = optionCursor.getString(3);
                      if(obtainedmarks_.equalsIgnoreCase("nomarks"))
                    	  break;
                      Element optionElement_ = doc.createElement("option");
                      optionElement_.setAttribute("optnid", optnid_);
                      optionElement_.setAttribute("obtainedmarks", obtainedmarks_);
                      response_Element.appendChild(optionElement_);
                    }
                    optionCursor.moveToPosition(0);
                    String videofilename = optionCursor.getString(4);
                    if(!videofilename.equalsIgnoreCase("null")){
                    	Element videoElement = doc.createElement("video");
                    	videoElement.setAttribute("filename", videofilename);
                    	response_Element.appendChild(videoElement);
                    	
                    }
                    responsesElement.appendChild(response_Element);     
                }
	            assessmentElement.appendChild(qpapertype);
	            candidatesElement.appendChild(candidateElement);
	            // individual response Responsedata table
	            try {
	                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	                factory.setNamespaceAware(true);
	                DocumentBuilder builder = factory.newDocumentBuilder();
	                Document newDocument = builder.newDocument();
	                Node importedNode = newDocument.importNode(candidateElement, true);
	                newDocument.appendChild(importedNode);
	                TransformerFactory tf = TransformerFactory.newInstance();
	                Transformer transformer = tf.newTransformer();
	                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	                StringWriter writer = new StringWriter();
	                transformer.transform(new DOMSource(newDocument), new StreamResult(writer));
	                individualresponse = writer.getBuffer().toString().replaceAll("\n|\r", "");
	                db.addResponsedata(candidateid, "manully adding training", individualresponse, endTime);
	            } catch (Exception e1) {
	                // TODO Auto-generated catch block
	                e1.printStackTrace();
	            }
	        }
	        // adding full response to db
	        try {
	            DOMSource domSource = new DOMSource(doc);
	            StringWriter writer = new StringWriter();
	            StreamResult result = new StreamResult(writer);
	            TransformerFactory tf = TransformerFactory.newInstance();
	            Transformer transformer = tf.newTransformer();
	            transformer.transform(domSource, result);
	            String new_xml_string = writer.toString();
	            db.updateScheduleresponse(new_xml_string);
	        } catch (TransformerException ex) {
	            ex.printStackTrace();
	        }
	        // creating response xml file in sdcard for checking purpose during
	        // development
	        try {
	            String xmlFilePath = Environment.getExternalStorageDirectory().getPath() + "/practicalresponse.xml";
	            // write the content into xml file
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            DOMSource source = new DOMSource(doc);
	            StreamResult result = new StreamResult(new File(xmlFilePath));
	            transformer.transform(source, result);
	            System.out.println("Done");
	        } catch (TransformerConfigurationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (TransformerFactoryConfigurationError e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (TransformerException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        handler.post(new Runnable() {
	            @Override
	            public void run() {
	                // TODO Auto-generated method stub
	                Toast.makeText(context, "You have sucessfully completed the practical test", Toast.LENGTH_LONG).show();
	            }
	        });
	        
		
		
	}//fuction end 

}// class end 
