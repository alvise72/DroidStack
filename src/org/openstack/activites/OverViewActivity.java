package org.openstack.activities;

import android.os.Bundle;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ActivityInfo;

import android.net.Uri;

import android.util.Log;
import android.util.DisplayMetrics;

import android.app.ActivityManager.MemoryInfo;
import android.app.AlertDialog;
import android.app.ActivityManager;
import android.app.Activity;

//import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.view.Gravity;
import android.view.View;

//import android.graphics.Bitmap;

import java.io.IOException;

import java.util.Vector;

import org.openstack.R;
import org.openstack.utils.User;
import org.openstack.utils.Utils;
import org.openstack.utils.Base64;
import org.openstack.comm.RESTClient;
import org.openstack.parse.ParseUtils;
import org.openstack.parse.ParseException;

public class OverViewActivity extends Activity {

  //__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.overview );
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
  }

  //__________________________________________________________________________________
  @Override
    public void onPause( ) {
      super.onPause( );
    }
}
