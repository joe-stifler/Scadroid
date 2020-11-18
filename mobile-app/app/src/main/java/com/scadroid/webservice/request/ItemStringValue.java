package com.scadroid.webservice.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import com.scadroid.webservice.request.Enums.DataType;
import com.scadroid.webservice.request.Enums.QualityCode;

public class ItemStringValue implements KvmSerializable {

    public String itemName;
    public DataType dataType;
    public String value;
    public QualityCode quality;
    public Date timestamp;

    public ItemStringValue(){}

    public ItemStringValue(String itemName, DataType dataType, String value, QualityCode quality, Date timestamp)
    {
        this.itemName = itemName;
        this.dataType = dataType;
        this.value = value;
        this.quality = quality;
        this.timestamp = timestamp;
    }

    public ItemStringValue (AttributeContainer inObj,ExtendedSoapSerializationEnvelope envelope)
    {
        if (inObj == null)
            return;

        SoapObject soapObject=(SoapObject)inObj;
        if (soapObject.hasProperty("itemName"))
        {

            Object obj = soapObject.getProperty("itemName");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString() != null)
                    this.itemName = j.toString();
            }else if (obj!= null && obj instanceof String)
                this.itemName = (String) obj;
        }

        if (soapObject.hasProperty("dataType"))
        {
            Object obj = soapObject.getProperty("dataType");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                {
                    this.dataType = DataType.fromString(j.toString());
                }
            }
            else if (obj!= null && obj instanceof Enums.DataSourceType)
                this.dataType = (DataType)obj;
        }
        if (soapObject.hasProperty("value"))
        {
            Object obj = soapObject.getPropertyAsString("value");

            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){ // -- > ele não está entrando neste if
                SoapPrimitive j =(SoapPrimitive) obj;
                value = j.toString();
            }else if (obj!= null && obj instanceof String) // --> não está entrando neste também
                value = (String) obj;
        }
        if (soapObject.hasProperty("timestamp"))
        {
            Object obj = soapObject.getProperty("timestamp");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class))
            {
                SoapPrimitive j =(SoapPrimitive) obj;
                if(j.toString()!=null)
                    this.timestamp = Helper.ConvertFromWebService(j.toString());
            }
            else if (obj!= null && obj instanceof Date)
                this.timestamp = (Date)obj;
        }
    }

    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return itemName;
            case 1:
                return dataType.toString();
            case 2:
                return value;
            case 3:
                return quality.toString();
            case 4:
                return timestamp;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 4;
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "itemName";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "dataType";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "value";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "quality";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "timestamp";
                break;
        }
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public QualityCode getQuality() {
        return quality;
    }

    public void setQuality(QualityCode quality) {
        this.quality = quality;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
