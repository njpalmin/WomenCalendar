package com.womencalendar;

import android.net.Uri;
import android.provider.BaseColumns;

public final class WomenCalendar {
	public static final String AUTHORITY = "com.womencalendar.provider";
	
	private WomenCalendar(){}
	
	public static final class Profile implements BaseColumns {
		private Profile(){}
		
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/profile");
        
        //public static final String PK = "pk";
        public static final String NAME = "name";
        public static final String LASTACCESS = "lastaccess";
        public static final String CYCLELENGTH = "cyclelength";
        public static final String PERIODLENGTH = "periodlength";
        public static final String AUTOMATICFORECAST = "automaticforecast";
        public static final String PILLNOTIFICATION = "pillnotification";
        public static final String PERIODNOTIFICATION = "periodnotification";
        public static final String PERIODNOTIFICATIONDAYSBEFORE = "periodnotificationdaysbefore";
        public static final String PERIODNOTIFICATIONREPEAT = "periodnotificationrepeat";                                             
        public static final String OVULATIONNOTIFICATIONREPEAT = "ovulationnotificationrepeat";
        public static final String OVULATIONNOTIFICATIONDAYSBEFORE = "ovulationnotificationdaysbefore";
        public static final String OVULATIONNOTIFICATION = "ovulationnotification";
        public static final String LUTEALPHASELENGTH = "lutealphaselength";
        
	}
	
	public static final class Record implements BaseColumns {
	    private Record(){}
	    

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/record");
	    
	    //public static final String PK = "pk";
        public static final String PROFILEPK = "profilepk"; 
        public static final String DATE = "date";
        public static final String TYPE = "type";
        public static final String FLOATVALUE  = "floatvalue";
        public static final String STRINGVALUE = "stringvalue";
        public static final String INTVALUE = "intvalue";
        
        public static final String DEFAULT_SORT_ORDER = "date DESC";
 
	}   
	
	public static final class Config implements BaseColumns {
	    private Config(){}
	    
	    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/config");
	    
	    public static final String PASSWORD = "password";
        public static final String LOCALE = "locale";
        public static final String WEEKSTART = "weekstart";
        public static final String TEMPERATURESCALE = "temeraturescale";
        public static final String FIRSTLAUNCH = "firstlaunch";
        public static final String EMAIL = "email";
        public static final String WEIGHTSCALE = "weightscale";
        public static final String SKIN = "skin";

    }   
	
	
	public static final class Product implements BaseColumns {
	    private Product(){}
	    
	    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/product");
	    
	    public static final String PRODUCTID = "productid";
	}
}       
        
        
        
        