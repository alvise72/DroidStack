package org.droidstack.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog extends ProgressDialog {

    public CustomProgressDialog(Context context, int theme) {
	super(context, theme );
    }

    public CustomProgressDialog(Context context) {
	super(context);
    }

    public void onBackPressed( ) {
	return;
    }

};
