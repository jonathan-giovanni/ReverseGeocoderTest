package com.softlutions.hv12.reversegeocodertest;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Utils {

    /**
     * Muestra un mensaje en un Snackbar ademas se establece el evento onClick
     */
    public static void showSnackbar(AppCompatActivity context, final String mainTextStringId, final String actionStringId,
                                    View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(context.findViewById(android.R.id.content),
                mainTextStringId,
                Snackbar.LENGTH_SHORT)
                .setAction(actionStringId, listener);
        snackbar.setActionTextColor(Color.rgb(155,190,255));
        snackbar.show();
    }
}
