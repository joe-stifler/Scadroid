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

public class GetStatusResponse implements KvmSerializable
{
    
    public ReplyBase replyBase;
    public ServerStatus serverStatus;

    public GetStatusResponse () {}

    public GetStatusResponse (AttributeContainer inObj,ExtendedSoapSerializationEnvelope envelope)
    {
	    
	    if (inObj == null)
            return;

        SoapObject soapObject=(SoapObject)inObj;  
        if (soapObject.hasProperty("replyBase"))
        {	
	        Object j = soapObject.getProperty("replyBase");
	        this.replyBase = (ReplyBase)envelope.get(j,ReplyBase.class);
        }
        if (soapObject.hasProperty("serverStatus"))
        {	
	        Object j = soapObject.getProperty("serverStatus");
	        this.serverStatus = (ServerStatus)envelope.get(j,ServerStatus.class);
        }


    }

    @Override
    public Object getProperty(int propertyIndex) {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if(propertyIndex==0)
        {
            return replyBase;
        }
        if(propertyIndex==1)
        {
            return serverStatus!=null?serverStatus:SoapPrimitive.NullNilElement;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = ReplyBase.class;
            info.name = "replyBase";
            info.namespace= "http://da.api.scadabr.org.br";
        }
        if(propertyIndex==1)
        {
            info.type = ServerStatus.class;
            info.name = "serverStatus";
            info.namespace= "http://da.api.scadabr.org.br";
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
