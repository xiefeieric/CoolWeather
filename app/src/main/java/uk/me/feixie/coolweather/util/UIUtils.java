package uk.me.feixie.coolweather.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Fei on 29/01/2016.
 */
public class UIUtils {

    private static Toast sToast;

    public static void showToast(Context context, String msg) {
        if (sToast==null) {
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        sToast.setText(msg);
        sToast.show();
    }

}
