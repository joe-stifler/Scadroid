package com.scadroid.webservice.request;

import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class GetDataHistoryOptions extends AttributeContainer implements KvmSerializable{

	private int maxReturn;
	private Date initialDate;
	private Date finalDate;
	
	public GetDataHistoryOptions(int maxReturn, Date initialDate, Date finalDate){
		this.maxReturn = maxReturn;
		this.initialDate = initialDate;
		this.finalDate = finalDate;
	}

	@Override
	public int getPropertyCount() {
		return 3;
	}

	@Override
	public Object getProperty(int arg0) {
			switch(arg0){
		        case 0:
		            return maxReturn;
		        case 1:
		            return initialDate;
		        case 2:
		            return finalDate;
			}
	    return null;
	}
	
	@Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
	                info.type = PropertyInfo.INTEGER_CLASS;
	                info.name = "maxReturn";
	                info.namespace= "http://config.api.scadabr.org.br";
                break;
            case 1:
	                info.type = PropertyInfo.STRING_CLASS;
	                info.name = "initialDate";
	                info.namespace= "http://config.api.scadabr.org.br";
                break;
            case 2:
	                info.type = PropertyInfo.STRING_CLASS;
	                info.name = "finalDate";
	                info.namespace= "http://config.api.scadabr.org.br";
                break;
        }
    }

	@Override
	public void setProperty(int arg0, Object arg1) {}

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
