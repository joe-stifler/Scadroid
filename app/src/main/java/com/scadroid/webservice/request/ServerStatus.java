package com.scadroid.webservice.request;

//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 4.0.0.0
//
// Created by Quasar Development at 21-07-2014
//
//---------------------------------------------------


import java.util.Hashtable;

import org.ksoap2.serialization.*;

import java.util.ArrayList;

import org.ksoap2.serialization.PropertyInfo;

public class ServerStatus extends AttributeContainer implements KvmSerializable
{
    
    public java.util.Date startTime;
    public Enums.ServerStateCode serverState = Enums.ServerStateCode.RUNNING;
    public String productVersion;    
    public ArrayList<String> supportedLocaleIDs = new ArrayList<String>();
    
    public ServerStatus () {}

    public ServerStatus (AttributeContainer inObj,ExtendedSoapSerializationEnvelope envelope)
    {
	    
	    if (inObj == null)
            return;

        SoapObject soapObject=(SoapObject)inObj;  
        if (soapObject.hasProperty("startTime"))
        {	
	        Object obj = soapObject.getProperty("startTime");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.startTime = Helper.ConvertFromWebService(j.toString());
                }
	        }
	        else if (obj!= null && obj instanceof java.util.Date){
                this.startTime = (java.util.Date)obj;
            }    
        }
        if (soapObject.hasProperty("serverState"))
        {	
	        Object obj = soapObject.getProperty("serverState");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.serverState = Enums.ServerStateCode.fromString(j.toString());
                }
	        }
	        else if (obj!= null && obj instanceof Enums.ServerStateCode){
                this.serverState = (Enums.ServerStateCode)obj;
            }    
        }
        
        if (soapObject.hasProperty("productVersion"))
        {   
            
        	Object obj = soapObject.getProperty("productVersion");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString() != null)
                	this.productVersion = j.toString();
            }else if (obj!= null && obj instanceof String){
                this.productVersion = (String) obj;
            }
        }
        
        if (soapObject.hasProperty("supportedLocaleIDs"))
        {	
	        int size = soapObject.getPropertyCount();
	        this.supportedLocaleIDs = new ArrayList<String>();
	        for (int i0=0;i0< size;i0++)
	        {
	            PropertyInfo info=new PropertyInfo();
	            soapObject.getPropertyInfo(i0, info);
                Object obj = info.getValue();
	            if (obj!=null && info.name.equals("supportedLocaleIDs"))
	            {
                    Object j =info.getValue();
	                String j1= j.toString();
	                this.supportedLocaleIDs.add(j1);
	            }
	        }
        }


    }

    @Override
    public Object getProperty(int propertyIndex) {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if(propertyIndex==0)
        {
            return startTime!=null?startTime:SoapPrimitive.NullNilElement;
        }
        if(propertyIndex==1)
        {
            return serverState!=null?serverState.toString():SoapPrimitive.NullSkip;
        }
        if(propertyIndex==2)
        {
            return productVersion!=null?productVersion:SoapPrimitive.NullNilElement;
        }
        if(propertyIndex>=+3 && propertyIndex< + 3+this.supportedLocaleIDs.size())
        {
            return supportedLocaleIDs.get(propertyIndex-(+3));
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 3+supportedLocaleIDs.size();
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "startTime";
            info.namespace= "http://vo.api.scadabr.org.br";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "serverState";
            info.namespace= "http://vo.api.scadabr.org.br";
        }
        if(propertyIndex==2)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "productVersion";
            info.namespace= "http://vo.api.scadabr.org.br";
        }
        if(propertyIndex>=+3 && propertyIndex <= +3+this.supportedLocaleIDs.size())
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "supportedLocaleIDs";
            info.namespace= "http://vo.api.scadabr.org.br";
        }
    }
    
    @Override
    public void setProperty(int arg0, Object arg1)
    {
    }

	@Override
	public String getInnerText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInnerText(String arg0) {
		// TODO Auto-generated method stub

	}

}
