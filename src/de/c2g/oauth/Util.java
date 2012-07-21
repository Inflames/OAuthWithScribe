package de.c2g.oauth;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class Util {

    public static final String DEBUGTAG  = "InfoLog";
    public static boolean      DEBUGMODE = true;



    public static void d(final String str) {
        if (Util.DEBUGMODE) {
        	System.out.println(str);
          
        }
    }

    
}
