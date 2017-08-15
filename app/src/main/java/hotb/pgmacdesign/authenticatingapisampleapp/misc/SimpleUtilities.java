package hotb.pgmacdesign.authenticatingapisampleapp.misc;

/**
 * Created by pmacdowell on 2017-07-31.
 */

public class SimpleUtilities {




    public static String removeSpaces(String str){
        if(str == null){
            return null;
        }
        str.replace(" ", "");
        str = str.trim();
        return  str;
    }


    /**
     * Checks if the passed String is null or empty
     * @param str String to check
     * @return Boolean, true if it is null or empty, false if it is not.
     */
    public static boolean isNullOrEmpty(String str){
        if(str == null){
            return true;
        }
        if(str.isEmpty()){
            return true;
        }
        if(str.length() == 0){
            return true;
        }
        if(str.equalsIgnoreCase(" ")){
            return true;
        }
        return false;
    }
}
