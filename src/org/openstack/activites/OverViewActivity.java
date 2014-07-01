package org.openstack.activities;

import android.os.Bundle;

import android.widget.ProgressBar;
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

    Bundle bundle = null;

  //__________________________________________________________________________________
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView( R.layout.overview );
    bundle = getIntent().getExtras();
    
  }
  
  //__________________________________________________________________________________
  @Override
  public void onResume( ) {
    super.onResume( );
    
    ((TextView)findViewById(R.id.vmusageTV)).setText(""+bundle.getInt("CURRVM"));
    ((TextView)findViewById(R.id.vmusageMAXTV)).setText("/"+bundle.getInt("MAXVM"));
    ((ProgressBar)findViewById(R.id.vmusagePB)).setMax( bundle.getInt("MAXVM") );
    ((ProgressBar)findViewById(R.id.vmusagePB)).setProgress(bundle.getInt("CURRVM"));

    ((TextView)findViewById(R.id.cpuusageTV)).setText(""+bundle.getInt("CURRCPU"));
    ((TextView)findViewById(R.id.cpuusageMAXTV)).setText("/"+bundle.getInt("MAXCPU"));
    ((ProgressBar)findViewById(R.id.cpuusagePB)).setMax( bundle.getInt("MAXCPU") );
    ((ProgressBar)findViewById(R.id.cpuusagePB)).setProgress(bundle.getInt("CURRCPU"));
    
    ((TextView)findViewById(R.id.ramusageTV)).setText(""+bundle.getInt("CURRRAM"));
    ((TextView)findViewById(R.id.ramusageMAXTV)).setText("/"+bundle.getInt("MAXRAM"));
    ((ProgressBar)findViewById(R.id.ramusagePB)).setMax( bundle.getInt("MAXRAM") );
    ((ProgressBar)findViewById(R.id.ramusagePB)).setProgress(bundle.getInt("CURRRAM"));

    ((TextView)findViewById(R.id.fipusageTV)).setText(""+bundle.getInt("CURRFIP"));
    ((TextView)findViewById(R.id.fipusageMAXTV)).setText("/"+bundle.getInt("MAXFIP"));
    ((ProgressBar)findViewById(R.id.fipusagePB)).setMax( bundle.getInt("MAXFIP") );
    ((ProgressBar)findViewById(R.id.fipusagePB)).setProgress(bundle.getInt("CURRFIP"));
    
    ((TextView)findViewById(R.id.segusageTV)).setText(""+bundle.getInt("CURRSECG"));
    ((TextView)findViewById(R.id.segusageMAXTV)).setText("/"+bundle.getInt("MASECG"));
    ((ProgressBar)findViewById(R.id.segusagePB)).setMax( bundle.getInt("MAXSECG") );
    ((ProgressBar)findViewById(R.id.segusagePB)).setProgress(bundle.getInt("CURRSECG"));
   
  }

  //__________________________________________________________________________________
  @Override
    public void onPause( ) {
      super.onPause( );
    }
}
