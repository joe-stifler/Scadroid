package com.scadroid.webservice.request;

public class Enums
{

    public enum ErrorCode
    {
        
        OK(0),
        
        UNSPECIFIED_ERROR(1),
        
        INSUFFICIENT_PARAMETERS(2),
        
        INVALID_PARAMETER(3),
        
        ACCESS_DENIED(4),
        
        SERVER_BUSY(5),
        
        INVALID_ID(6),
        
        NOT_SUPPORTED(7),
        
        READ_ONLY(8),
        
        WRITE_ONLY(9),
        
        TIMED_OUT(10);
        
        private int code;
        
        ErrorCode(int code ){
            this.code = code;
        }
    
        public int getCode(){
		    return code;
	    }
    

        public static ErrorCode fromString(String str)
        {
            if (str.equals("OK"))
                return OK;
            if (str.equals("UNSPECIFIED_ERROR"))
                return UNSPECIFIED_ERROR;
            if (str.equals("INSUFFICIENT_PARAMETERS"))
                return INSUFFICIENT_PARAMETERS;
            if (str.equals("INVALID_PARAMETER"))
                return INVALID_PARAMETER;
            if (str.equals("ACCESS_DENIED"))
                return ACCESS_DENIED;
            if (str.equals("SERVER_BUSY"))
                return SERVER_BUSY;
            if (str.equals("INVALID_ID"))
                return INVALID_ID;
            if (str.equals("NOT_SUPPORTED"))
                return NOT_SUPPORTED;
            if (str.equals("READ_ONLY"))
                return READ_ONLY;
            if (str.equals("WRITE_ONLY"))
                return WRITE_ONLY;
            if (str.equals("TIMED_OUT"))
                return TIMED_OUT;
		    return null;
        }
    }

    public enum DataType{
    	INTEGER(0),
    	UNSIGNED_INTEGER(1),
    	LONG(2),
    	UNSIGNED_LONG(3),
    	STRING(4),
    	BOOLEAN(5),
    	FLOAT(6),
    	DOUBLE(7),
    	BYTE(8),
    	UNSIGNED_BYTE(9);
    
    private int code;
    
    DataType(int code){
        this.code = code;
    }
    
    public int getCode(){
        return code;
    }
    
    public static DataType fromString(String str)
    {
        if (str.equals("INTEGER"))
            return INTEGER;
        if (str.equals("UNSIGNED_INTEGER"))
            return UNSIGNED_INTEGER;
        if (str.equals("LONG"))
            return LONG;
        if (str.equals("UNSIGNED_LONG"))
            return UNSIGNED_LONG;
        if (str.equals("STRING"))
            return STRING;
        if (str.equals("BOOLEAN"))
            return BOOLEAN;
        if (str.equals("FLOAT"))
            return FLOAT;
        if (str.equals("DOUBLE"))
            return DOUBLE;
        if (str.equals("BYTE"))
            return BYTE;
        if (str.equals("UNSIGNED_BYTE"))
            return UNSIGNED_BYTE;
        return null;
    	}
    }
    
    public enum DataSourceType
    {
        
        MODBUS_IP(0),
        
        MODBUS_SERIAL(1);
        
        private int code;
        
        DataSourceType(int code ){
            this.code = code;
        }
    
        public int getCode(){
		    return code;
	    }
    

        public static DataSourceType fromString(String str)
        {
            if (str.equals("MODBUS_IP"))
                return MODBUS_IP;
            if (str.equals("MODBUS_SERIAL"))
                return MODBUS_SERIAL;
		    return null;
        }
    }

    public enum ServerStateCode
    {
        
        RUNNING(0),
        
        FAILED(1),
        
        NO_CONFIG(2),
        
        SUSPENDED(3),
        
        TEST(4),
        
        COMM_FAULT(5);
        
        private int code;
        
        ServerStateCode(int code ){
            this.code = code;
        }
    
        public int getCode(){
		    return code;
	    }
    

        public static ServerStateCode fromString(String str)
        {
            if (str.equals("RUNNING"))
                return RUNNING;
            if (str.equals("FAILED"))
                return FAILED;
            if (str.equals("NO_CONFIG"))
                return NO_CONFIG;
            if (str.equals("SUSPENDED"))
                return SUSPENDED;
            if (str.equals("TEST"))
                return TEST;
            if (str.equals("COMM_FAULT"))
                return COMM_FAULT;
		    return null;
        }
    }
    
    public enum QualityCode{
    	BAD(0),
    	BAD_CONFIGURATION_ERROR(1),
    	BAD_NOT_CONNECTED(2),
    	BAD_DEVICE_FAILURE(3),
    	BAD_SENSOR_FAILURE(4),
    	BAD_LAST_KNOWN_VALUE(5),
    	BAD_COMM_FAILURE(6),
    	BAD_OUT_OF_SERVICE(7),
    	BAD_WAITING_FOR_INITIAL_DATA(8),
    	UNCERTAIN(9),
    	UNCERTAIN_LAST_USABLE_VALUE(10),
    	UNCERTAIN_SENSOR_NOT_ACCURATE(11),
    	UNCERTAIN_EU_EXCEEDED(12),
    	UNCERTAIN_SUB_NORMAL(13),
    	GOOD(14),
    	GOOD_LOCAL_OVERRIDE(15);
    
    private int code;
    
    QualityCode(int code){
        this.code = code;
    }
    
    public int getCode(){
        return code;
    }
    
    public static QualityCode fromString(String str)
    {
        if (str.equals("BAD"))
            return BAD;
        if (str.equals("BAD_CONFIGURATION_ERROR"))
            return BAD_CONFIGURATION_ERROR;
        if (str.equals("BAD_NOT_CONNECTED"))
            return BAD_NOT_CONNECTED;
        if (str.equals("BAD_DEVICE_FAILURE"))
            return BAD_DEVICE_FAILURE;
        if (str.equals("BAD_SENSOR_FAILURE"))
            return BAD_SENSOR_FAILURE;
        if (str.equals("BAD_LAST_KNOWN_VALUE"))
            return BAD_LAST_KNOWN_VALUE;
        if (str.equals("BAD_COMM_FAILURE"))
            return BAD_COMM_FAILURE;
        if (str.equals("BAD_OUT_OF_SERVICE"))
            return BAD_OUT_OF_SERVICE;
        if (str.equals("BAD_WAITING_FOR_INITIAL_DATA"))
            return BAD_WAITING_FOR_INITIAL_DATA;
        if (str.equals("UNCERTAIN"))
            return UNCERTAIN;
        if (str.equals("UNCERTAIN_LAST_USABLE_VALUE"))
            return UNCERTAIN_LAST_USABLE_VALUE;
        if (str.equals("UNCERTAIN_SENSOR_NOT_ACCURATE"))
            return UNCERTAIN_SENSOR_NOT_ACCURATE;
        if (str.equals("UNCERTAIN_EU_EXCEEDED"))
            return UNCERTAIN_EU_EXCEEDED;
        if (str.equals("UNCERTAIN_SUB_NORMAL"))
            return UNCERTAIN_SUB_NORMAL;
        if (str.equals("GOOD"))
            return GOOD;
        if (str.equals("GOOD_LOCAL_OVERRIDE"))
            return GOOD_LOCAL_OVERRIDE;
        return null;
    }
}

}