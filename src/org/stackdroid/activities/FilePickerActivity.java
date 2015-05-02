package org.stackdroid.activities;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.util.Log;
import org.stackdroid.R;
import org.stackdroid.utils.Utils;

public class FilePickerActivity extends ListActivity {

    private List<String> item = null;
    private List<String> path = null;
    private String root = Environment.getExternalStorageDirectory( ).getAbsolutePath();
    private TextView myPath;

    /** Called when the activity is first created. */
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filepicker);
        myPath = (TextView)findViewById(R.id.path);
        getDir(root);
    }    

    /**
     *
     *
     *
     *
     */
    private void getDir(String dirPath)
    {
	myPath.setText("Location: " + dirPath);
	item = new ArrayList<String>();
	path = new ArrayList<String>();
	File f = new File(dirPath);
	File[] files = f.listFiles();

	Arrays.sort(files);
	
	if(!dirPath.equals(root))
	    {
		item.add(root);
		path.add(root);
		item.add("../");
		path.add(f.getParent());
		
	    }

	for(int i=0; i < files.length; i++)
	    {		
		File file = files[i];	
		path.add(file.getPath());
		if(file.isDirectory())
		    item.add(file.getName() + "/");
		else
		    item.add(file.getName());		
	    }
	
	ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.filepicker_row, item);
	setListAdapter(fileList);
    }

    /**
     *
     *
     *
     *
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
	File file = new File(path.get(position));
	
		if (file.isDirectory())
	    {
			if(file.canRead())
		   		 getDir(path.get(position));
			else {
		   	 new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
			}
	    } else {
			//Utils.putStringPreference("CAFILE", file.getAbsolutePath(), FilePickerActivity.this);
	    	Intent returnIntent = getIntent();
			returnIntent.putExtra( "selectedcafile", file.getAbsolutePath() );
	   	 	setResult(Activity.RESULT_OK, returnIntent);
	   		finish();
		}
    
    }
}
