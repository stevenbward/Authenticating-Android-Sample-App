package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by pmacdowell on 2017-08-11.
 */

public class L {

    /**
     * Quick println
     * @param myObject The string to print (or double, int, whatever)
     * @param <E> Extends object
     */
    public static <E> void m (E myObject){
        String str = myObject + "";
        if(str.isEmpty()){
            return;
        }
        if (str.length() > 4000) {
            Log.v(TAG, "sb.length = " + str.length());
            int chunkCount = str.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= str.length()) {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i));
                } else {
                    Log.d(TAG, "chunk " + i + " of " + chunkCount + ":" + str.substring(4000 * i, max));
                }
            }
        } else {
            Log.d(TAG, str);
        }
    }

    /**
     * Short toast
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void toast(Context context, E myObject){
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Long toast
     * @param context context
     * @param myObject String to print (If OTHER things are passed in, it converts it to a String first)
     */
    public static <E> void Toast(Context context, E myObject){
        String str = myObject + ""; //Cast it to a String
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }
}
