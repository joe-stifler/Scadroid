package com.scadroid.webservice.request;

import java.util.ArrayList;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class GetDataHistoryResponse extends AttributeContainer{

	public ArrayList<APIError> errors;
    public ArrayList<ItemStringValue> itemsList;
    public ReplyBase replyBase;
    public Boolean moreValues;
    
	public GetDataHistoryResponse(){}
	
	public GetDataHistoryResponse(AttributeContainer inObj,ExtendedSoapSerializationEnvelope envelope){
		if (inObj == null)
            return;

	    SoapObject soapObject=(SoapObject)inObj;  
        if (soapObject.hasProperty("errors"))
        {	
	        int size = soapObject.getPropertyCount();
	        this.errors = new ArrayList<APIError>();
	        for (int i0=0;i0< size;i0++)
	        {
//				if (i0%5 == 0) {
					PropertyInfo info = new PropertyInfo();
					soapObject.getPropertyInfo(i0, info);
					Object obj = info.getValue();
					if (obj != null && info.name.equals("errors")) {
						Object j = info.getValue();
						APIError j1 = (APIError) envelope
								.get(j, APIError.class);
						this.errors.add(j1);
//					}
				}
	        }
        }
        
        if (soapObject.hasProperty("itemsList"))
        {	
        	int size = soapObject.getPropertyCount();
	        this.itemsList = new ArrayList<ItemStringValue>();
	        for (int i0=0;i0< size;i0++)
	        {
				PropertyInfo info = new PropertyInfo();
				soapObject.getPropertyInfo(i0, info);
				Object obj = info.getValue();
				if (obj != null && info.name.equals("itemsList")) {
					Object j = info.getValue();
					ItemStringValue j1 = (ItemStringValue) envelope.get(j,
							ItemStringValue.class);
					this.itemsList.add(j1);
				}
	        }
        }
        
        if (soapObject.hasProperty("moreValues"))
        {	
	        Object obj = soapObject.getProperty("moreValues");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.moreValues = Boolean.parseBoolean(j.toString());
                }
	        }
	        else if (obj!= null && obj instanceof Integer){
                this.moreValues = (Boolean)obj;
            }    
        }
        
        if (soapObject.hasProperty("replyBase"))
        {	
	        Object j = soapObject.getProperty("replyBase");
	        this.replyBase = (ReplyBase)envelope.get(j,ReplyBase.class);
        }
	}
}
