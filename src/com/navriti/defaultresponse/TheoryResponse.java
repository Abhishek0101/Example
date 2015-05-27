package com.navriti.defaultresponse;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.navriti.database.SqliteDbhelper;
import com.navriti.parserclass.XMLParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;

@SuppressLint("SimpleDateFormat")
public class TheoryResponse {
Context context;
SqliteDbhelper sqlitedbhelper;
String endTime;
Boolean candidateExist=false;
public TheoryResponse(Context context){
	
	this.context=context;	
	sqlitedbhelper=new SqliteDbhelper(context);
}

     public void TheoryResponseIndividual(String candidateid,String startTime){
	   String individualresponse = "";
       // end time
       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
       endTime = dateFormat.format(new Date());
       boolean candiateExist = false;
       
       // getting schedule reponse
       String schecdulereponse = sqlitedbhelper.getScheduleresponse();       
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
               qpapertype.setAttribute("type", "Theory");
               String candiate_qptypeid = sqlitedbhelper.get_qptypeid(candidateid);
               qpapertype.setAttribute("qptypeid", candiate_qptypeid);
               Element startElement = doc.createElement("startdatetime");
               startElement.appendChild(doc.createTextNode(startTime));
               qpapertype.appendChild(startElement);
               Element endElement = doc.createElement("enddatetime");
               endElement.appendChild(doc.createTextNode(endTime));
               qpapertype.appendChild(endElement);
               Element responsesElement = doc.createElement("responses");
               qpapertype.appendChild(responsesElement);
        
               Cursor cursormarks = sqlitedbhelper.getall_TheoryMarks(candidateid);
               while (cursormarks.moveToNext()) {
                String qsnid = cursormarks.getString(3);
                String optnid = cursormarks.getString(4);
                Element response_Element = doc.createElement("response");
                if(optnid!="0"){
                response_Element.setAttribute("qsnid", qsnid);
                response_Element.setAttribute("optionid", optnid);
                responsesElement.appendChild(response_Element);
                }
               }
               
               assementnode.appendChild(qpapertype);
               // here i need to add individual response to db ..... us
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
                   sqlitedbhelper.addResponsedata(candidateid, "manually written job", output, endTime);
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
           String candiate_qptypeid = sqlitedbhelper.get_qptypeidfortheory(candidateid);
           NodeList nlcandidates = doc.getElementsByTagName("candidates");
           Element candidatesElement = (Element)nlcandidates.item(0);
           // candidate element
           Element candidateElement = doc.createElement("candidate");
           candidateElement.setAttribute("candidateid", candidateid);
           candidateElement.setAttribute("qptypeid", candiate_qptypeid);
           Cursor candiate_cursor = sqlitedbhelper.getUser(candidateid);
           String sid = "", asid = "",qpaperid="";
           if (candiate_cursor.moveToNext()) {
        	   qpaperid=candiate_cursor.getString(1);
               sid = candiate_cursor.getString(3);
               asid = candiate_cursor.getString(4);
           }
           candidateElement.setAttribute("scheduleid", sid);
           candidateElement.setAttribute("assessorid", asid);
           /*
            * // tab element Element tabdeviceElement =
            * doc.createElement("tabdevice");
            * tabdeviceElement.setAttribute("macid", imeinumber);
            * candidateElement.appendChild(tabdeviceElement);
            */
           // assessment element
           Element assessmentElement = doc.createElement("assessment");
           assessmentElement.setAttribute("qpid", qpaperid);
           candidateElement.appendChild(assessmentElement);
           Element qpapertype = doc.createElement("qpapertype");
           qpapertype.setAttribute("type", "Theory");
           qpapertype.setAttribute("qptypeid", candiate_qptypeid);
           Element startElement = doc.createElement("startdatetime");
           startElement.appendChild(doc.createTextNode(startTime));
           qpapertype.appendChild(startElement);
           Element endElement = doc.createElement("enddatetime");
           endElement.appendChild(doc.createTextNode(endTime));
           qpapertype.appendChild(endElement);
      Element responsesElement = doc.createElement("responses");
           qpapertype.appendChild(responsesElement);
           Cursor cursormarks = sqlitedbhelper.getall_TheoryMarks(candidateid);
           // Toast.makeText(getApplicationContext(),
           // "cursormarks length"+cursormarks.getCount(),
           // Toast.LENGTH_LONG).show();
           while (cursormarks.moveToNext()) {
               String qsnid = cursormarks.getString(3);
               String optnid = cursormarks.getString(4);
               Element response_Element = doc.createElement("response");
               if(optnid!="0"){
               response_Element.setAttribute("qsnid", qsnid);
               response_Element.setAttribute("optionid", optnid);
               responsesElement.appendChild(response_Element);}
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
               sqlitedbhelper.addResponsedata(candidateid, "TheoryPaper", individualresponse, endTime);
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
           sqlitedbhelper.updateScheduleresponse(new_xml_string);
       } catch (TransformerException ex) {
           ex.printStackTrace();
    
           
       }
}//function end 
   
}//class end 
