package com.scadroid.webservice.request;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class ExtendedSoapSerializationEnvelope extends SoapSerializationEnvelope {
    @SuppressWarnings("rawtypes")
	static HashMap< String,Class> classNames = new HashMap< String, Class>();
   
    static {
        classNames.put("http://vo.api.scadabr.org.br^^APIError",APIError.class);
        classNames.put("http://vo.api.scadabr.org.br^^ItemInfo",ItemInfo.class);
        classNames.put("http://vo.api.scadabr.org.br^^ReplyBase",ReplyBase.class);
        classNames.put("http://vo.api.scadabr.org.br^^ServerStatus",ServerStatus.class);
        classNames.put("http://config.api.scadabr.org.br^^BrowseTagsResponse",BrowseTagsResponse.class);
        classNames.put("http://config.api.scadabr.org.br^^ReadDataResponse",ReadDataResponse.class);
        classNames.put("http://config.api.scadabr.org.br^^WriteStringDataResponse",WriteDataResponse.class);
        classNames.put("http://da.api.scadabr.org.br^^GetStatusResponse",GetStatusResponse.class);
    }   

    @SuppressWarnings("unused")
	private final String MsNs="http://schemas.microsoft.com/2003/10/Serialization/";
    protected static final int QNAME_NAMESPACE = 0;
    private static final String TYPE_LABEL = "type";

    public ExtendedSoapSerializationEnvelope() {
        super(SoapEnvelope.VER11);
        implicitTypes = true;
        dotNet = true;
        new MarshalGuid().register(this);
        new MarshalDate().register(this);
        new MarshalFloat().register(this);
    }

    

    @Override
    protected void writeProperty(XmlSerializer writer, Object obj, PropertyInfo type) throws IOException {
        //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
        //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
        if (obj == null || obj== SoapPrimitive.NullNilElement) {
            writer.attribute(xsi, version >= VER12 ? NIL_LABEL : NULL_LABEL, "true");
            return;
        }
            Object[] qName = getInfo(null, obj);
            if (!type.multiRef && qName[2] == null )
            {
                if (!implicitTypes || (obj.getClass() != type.type && !(obj instanceof Vector ) && type.type!=String.class)) {
                    String xmlName=Helper.getKeyByValue(classNames,obj.getClass());
                    if(xmlName!=null)
                    {
                        String[] parts=xmlName.split("\\^\\^");
                        String prefix = writer.getPrefix(parts[0], true);
                        writer.attribute(xsi, TYPE_LABEL, prefix + ":" +parts[1]);
                    }
                    else
                    {
                        String prefix = writer.getPrefix(type.namespace, true);
                        writer.attribute(xsi, TYPE_LABEL, prefix + ":" + obj.getClass().getSimpleName());
                    }
                }
                //super.writeProperty(writer,obj,type);

                //!!!!! If you have a compilation error here then you are using old version of ksoap2 library. Please upgrade to the latest version.
                //!!!!! You can find a correct version in Lib folder from generated zip file!!!!!
                writeElement(writer, obj, type, qName[QNAME_MARSHAL]);
            }
            else {
                super.writeProperty(writer, obj, type);
            }
    }

    public SoapObject GetExceptionDetail(Element detailElement) {
         Element errorElement=detailElement.getElement(0);
         return GetSoapObject(errorElement);
    }

    public SoapObject GetSoapObject(Element detailElement) {
        try{
            XmlSerializer xmlSerializer = XmlPullParserFactory.newInstance().newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            detailElement.write(xmlSerializer);
            xmlSerializer.flush();

            XmlPullParser xpp = new KXmlParser();
            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);

            xpp.setInput(new StringReader(writer.toString()));
            xpp.nextTag();
            SoapObject soapObj = new SoapObject(detailElement.getNamespace(),detailElement.getName());
            readSerializable(xpp,soapObj);
            return soapObj;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public Object GetHeader(Element detailElement) {
        if(detailElement.getText(0)!=null)
        {
            SoapPrimitive primitive = new SoapPrimitive(detailElement.getNamespace(),detailElement.getName(),detailElement.getText(0));
            return  primitive;
        }
    
        return GetSoapObject(detailElement);
    }
    

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Object get(Object soap,Class cl)
    {
        if(soap==null)
        {
            return null;
        }
        try
        {
            if(soap instanceof  Vector)
            {
				Constructor ctor = cl.getConstructor(Vector.class,ExtendedSoapSerializationEnvelope.class);
                return ctor.newInstance(soap,this);
            }
            {
                if(soap instanceof SoapObject)
                {
                    String key=String.format("%s^^%s",((SoapObject)soap).getNamespace(),((SoapObject)soap).getName());
                    if(classNames.containsKey(key))
                    {
                        cl=classNames.get(key);
                    }
                }
                Constructor ctor = cl.getConstructor(AttributeContainer.class,ExtendedSoapSerializationEnvelope.class);
                return ctor.newInstance(soap,this);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
} 

