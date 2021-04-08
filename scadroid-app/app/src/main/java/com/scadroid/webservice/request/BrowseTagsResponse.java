package com.scadroid.webservice.request;

import java.util.ArrayList;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class BrowseTagsResponse extends AttributeContainer
{
    public ArrayList<APIError> errors =new ArrayList<APIError>();
    public ArrayList<ItemInfo> itemList = new ArrayList<ItemInfo>();
    public ReplyBase replyBase;
    
    public BrowseTagsResponse (){}

    public BrowseTagsResponse (AttributeContainer inObj,ExtendedSoapSerializationEnvelope envelope)
    {

	    if (inObj == null)
            return;

	    SoapObject soapObject=(SoapObject)inObj;  
        if (soapObject.hasProperty("errors"))
        {	
	        int size = soapObject.getPropertyCount();
	        this.errors = new ArrayList<APIError>();
	        for (int i0=0;i0< size;i0++)
	        {
	            PropertyInfo info=new PropertyInfo();
	            soapObject.getPropertyInfo(i0, info);
                Object obj = info.getValue();
	            if (obj!=null && info.name.equals("errors"))
	            {
                    Object j =info.getValue();
	                APIError j1= (APIError)envelope.get(j,APIError.class);
	                this.errors.add(j1);
	            }
	        }
        }
        
        if (soapObject.hasProperty("itemsList"))
        {	
        	int size = soapObject.getPropertyCount();
	        this.itemList = new ArrayList<ItemInfo>();
	        for (int i0=0;i0< size;i0++)
	        {
	            PropertyInfo info=new PropertyInfo();
	            soapObject.getPropertyInfo(i0, info);
                Object obj = info.getValue();
	            if (obj!=null && info.name.equals("itemsList"))
	            {
                    Object j =info.getValue();
	                ItemInfo j1= (ItemInfo)envelope.get(j,ItemInfo.class);
	                this.itemList.add(j1);
	            }
	        }
        }
        
        if (soapObject.hasProperty("replyBase"))
        {	
	        Object j = soapObject.getProperty("replyBase");
	        this.replyBase = (ReplyBase)envelope.get(j,ReplyBase.class);
        }


    }

}

