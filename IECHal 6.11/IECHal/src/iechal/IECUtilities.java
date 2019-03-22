package iechal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class IECUtilities {

    private static final String [] IEC_TYPES = {"BOOL", "BYTE", "WORD", "DWORD",
        "LWORD", "SINT", "INT", "DINT", "LINT", "USINT", "UINT", "UDINT", "ULINT",
        "REAL", "LREAL", "TIME" };
     
    private static final String [] JAVA_TYPES = {"boolean", "byte", "short", "int",
        "long", "byte", "short", "int", "long", "byte", "short", "int", "long",
        "float", "double", "long" };

    private static final String [] INITIAL_VALUES = {"false", "0", "0", "0",
        "0L", "0", "0", "0", "0L", "0", "0", "0", "0L", "0.0f", "0.0d", "0L"
    };
    
    private static final String [] FB_NAMES_STRINGS = {"SR","RS","R_TRIG","F_TRIG"};
    
    private static final HashSet fb_names_set = 
                    new HashSet(Arrays.asList(FB_NAMES_STRINGS));
    
    private static final String counter_regex = "CT(D|(U(D)?))(_(U?(D|L))?INT)?";
    private static final String timer_regex = "T(P|ON|OFF)(_TIME)?";
    
    public List<String> getIECtypes() {
        return Arrays.asList(IEC_TYPES);
    }
    
    public static String getJavaType(String iecType) {
        
        String javaType = "";
        
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        for (int i = 0; i < IEC_TYPES.length; i++) {
            if (capitalizedIecType.equals(IEC_TYPES[i])) {
                javaType = JAVA_TYPES[i];
                break;
            }    
        }
        
        return javaType;
    
    }
    
    public static String getInitialValue(String iecType) {
        
        String initialValue = "";
        
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        for (int i = 0; i < IEC_TYPES.length; i++) {
            if (capitalizedIecType.equals(IEC_TYPES[i])) {
                initialValue = INITIAL_VALUES[i];
                break;
            }    
        }
        
        return initialValue;
        
    }
       
    public static boolean isAnyElementary(String iecType) {
        return isAnyMagnitude(iecType) || isAnyBit(iecType);
    }

    public static boolean isAnyReal(String iecType) {
    
        boolean retval = false;
        
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        switch(capitalizedIecType) {
            case "REAL":
            case "LREAL":
                retval = true;
        }
        return retval;
        
    }
    
    public static boolean isAnyUnsigned(String iecType) {
        
        boolean retval = false;
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        switch(capitalizedIecType) {
            case "USINT":
            case "UINT":
            case "UDINT":
            case "ULINT":
                retval = true;
        }
        return retval;
        //Όπως το προηγούμενο, επιστρέφει true για τους τύπους δεδομένων
        //"USINT", "UINT", "UDINT", "ULINT"
    }
    
    public static boolean isAnySigned(String iecType) {
        
        boolean retval = false;
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        switch(capitalizedIecType) {
            case "SINT":
            case "INT":
            case "DINT":
            case "LINT":
                retval = true;
        }
        //Όπως το προηγούμενο, επιστρέφει true για τους τύπους δεδομένων
        //"SINT", "INT", "DINT", "LINT"
        return retval;
    }
    
    public static boolean isAnyDuration(String iecType) {
                
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        return capitalizedIecType.equals("TIME");        
    }
    
    public static boolean isAnyBit(String iecType) {
        
        boolean retval = false;
        String capitalizedIecType = iecType.trim().toUpperCase(Locale.ENGLISH);
        switch(capitalizedIecType) {
            case "BOOL":
            case "BYTE":
            case "WORD":
            case "DWORD":
            case "LWORD":
                retval = true;
        }
        //Όπως το προηγούμενο, επιστρέφει true για τους τύπους δεδομένων
        //"BOOL", "BYTE", "WORD", "DWORD", "LWORD"
        return retval;
    }
    
    public static boolean isAnyInt(String iecType) {
        return isAnySigned(iecType) || isAnyUnsigned(iecType);
    }
    
    public static boolean isAnyNum(String iecType) {
        return isAnyInt(iecType) || isAnyReal(iecType);
    }
    
    public static boolean isAnyMagnitude(String iecType) {
        return isAnyNum(iecType) || isAnyDuration(iecType);
    }
        
    public static boolean isImplicitlyConverted(String source, String target) {
        
        boolean retval = false;
        
        String csource = source.trim().toUpperCase(Locale.ENGLISH);
        String ctarget = target.trim().toUpperCase(Locale.ENGLISH);
        if (csource.equals("REAL")) {
            if (ctarget.equals("LREAL")) {
                retval = true;
            }
        } else if (csource.equals("DINT")) {
            switch(ctarget) {
                case "LREAL":
                case "LINT":
                    retval = true;
            }
        } else if (csource.equals("INT")) {
            switch(ctarget) {
                case "LREAL":
                case "REAL" :
                case "LINT" :
                case "DINT" :
                    retval = true;
            }
        } else if (csource.equals("SINT")) {
            switch(ctarget) {
                case "LREAL":
                case "REAL" :
                case "LINT" :
                case "DINT" :
                case "INT"  :
                    retval = true;
            }
        } else if (csource.equals("UDINT")) {
            switch (ctarget) {
                case "LREAL" :
                case "LINT"  :
                case "ULINT" :
                    retval = true;
            }
        } else if (csource.equals("UINT")) {
            switch (ctarget) {
                case "LREAL" :
                case "REAL"  :
                case "LINT"  :
                case "DINT"  :
                case "ULINT" :
                case "UDINT" :
                    retval = true;
            }
        } else if (csource.equals("USINT")) {
            switch (ctarget) {
                case "LREAL" :
                case "REAL"  :
                case "LINT"  :
                case "DINT"  :
                case "INT"   : 
                case "ULINT" :
                case "UDINT" :
                case "UINT"  :
                    retval = true;
            }
        } else if (csource.equals("DWORD")) {
            switch (ctarget) {
                case "LWORD"  :
                    retval = true;
            }
        } else if (csource.equals("WORD")) {
            switch (ctarget) {
                case "LWORD"  :
                case "DWORD"  :
                    retval = true;
            }
        } else if (csource.equals("BYTE")) {
            switch (ctarget) {
                case "LWORD"  :
                case "DWORD"  :
                case "WORD"   : 
                    retval = true;
            }
        } else if (csource.equals("BOOL")) {
            switch (ctarget) {
                case "LWORD"  :
                case "DWORD"  :
                case "WORD"   :
                case "BYTE"   :
                    retval = true;
            }
        } 
        // κλπ (πίνακας σελ. 67 του προτύπου)
        return retval;
    }
    
    public static boolean isValidIdentifier(String str) {
        return (str.matches("^[\\w]+")&&!str.matches("(.*)_")&&str.matches("^[^\\d].*")&&!str.contains("__"));
    }    
    
    public static boolean isStandardFunctionName(String name) {
        return false;
    }
    
    public static boolean isStandardFunctionBlockName(String name) {
           
        String cname = name.trim().toUpperCase(Locale.ENGLISH);        
        
        return fb_names_set.contains(cname) || cname.matches(counter_regex) || cname.matches(timer_regex); 
    }
    
    public static boolean isStandardPouName(String s) {
        return isStandardFunctionName(s) || isStandardFunctionBlockName(s);
    }
    
    public static void main(String[] args) {
        System.out.println(isStandardFunctionBlockName("CTD_UDINT"));
    }
}
