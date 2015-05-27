package com.navriti.defaultresponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.AssetManager;
import android.telephony.TelephonyManager;

import com.navriti.database.*;
import com.navriti.parserclass.*;

public class DefaultResponse {

SqliteDbhelper sqliteDbhelper;
Context context;
AssetManager assetManager;
public static String IMEI = "";
TelephonyManager tm;

public DefaultResponse(Context context){
	
this.context=context;

assetManager=context.getAssets();

tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
IMEI=tm.getDeviceId();
}

public void defualtResponse(){
	
	sqliteDbhelper=new SqliteDbhelper(context);
	
	// default response saved to sqlite
    String response = sqliteDbhelper.getScheduleresponse();
    if (response.equalsIgnoreCase("NO DATA")) {
        try {
            InputStream inputStream = assetManager.open("defaultresponse.xml");
            int size = inputStream.available();
            byte[] byte_data = new byte[size];
            inputStream.read(byte_data);
            String content = new String(byte_data);
            XMLParser parser = new XMLParser();
            Document doc = parser.getDomElement(content); // getting DOM
                                                          // element
            NodeList nl_scheduleresponse = doc.getElementsByTagName("scheduleresponse");
            Element scheduleresponse_element = (Element)nl_scheduleresponse.item(0);
            Node candiadateElement = scheduleresponse_element.getElementsByTagName("candidates").item(0);
            Element tabdeviceElement = doc.createElement("tabdevice");
            tabdeviceElement.setAttribute("macid", IMEI);
            scheduleresponse_element.insertBefore(tabdeviceElement, candiadateElement);
            // scheduleresponse_element.appendChild(tabdeviceElement);
            try {
                DOMSource domSource = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer transformer = tf.newTransformer();
                transformer.transform(domSource, result);
                String new_xml_string = writer.toString();
                sqliteDbhelper.addScheduleresponse(new_xml_string);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	
	
	
	
	
	
}



}

}//end class tag