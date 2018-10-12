package com.softlutions.hv12.reversegeocodertest;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Utils {

    public static void showSnackbar(AppCompatActivity context, final String mainTextStringId, final String actionStringId,
                                    View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(context.findViewById(android.R.id.content),
                mainTextStringId,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(actionStringId, listener);
        snackbar.setActionTextColor(Color.rgb(155,190,255));
        snackbar.show();
    }
}
