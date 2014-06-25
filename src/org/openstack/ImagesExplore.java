package org.openstack;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ImagesExplore extends Activity {

	// Stores names of traversed directories
	ArrayList<String> str = new ArrayList<String>();
	ArrayList<String> osimages = null;
	// Check if the first level of the directory structure is the one showing
	private Boolean firstLvl = true;

	private static final String TAG = "F_IMAGES"; 

	private Item[] imageList; 
	
	private String chosenImage; 
	private static final int DIALOG_LOAD_IMAGES = 1000;

	ListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		osimages = bundle.getStringArrayList("OSIMAGELIST");
		if(osimages==null) {
		  Utils.alert("WARNING: passed an empty list of glance images", this);
		} else {
		  loadFileList();
		  showDialog(DIALOG_LOAD_IMAGES);
		}
		
	}

	private void loadFileList() {

			String[] iList = osimages.toArray(new String[osimages.size()]);
			imageList = new Item[iList.length];
			for (int i = 0; i < iList.length; i++) {
				imageList[i] = new Item(iList[i], R.drawable.osimage);
			}

		adapter = new ArrayAdapter<Item>( this,
						  android.R.layout.select_dialog_item, 
						  android.R.id.text1,
						  imageList) 
			  {
			  @Override
			  public View getView(int position, View convertView, ViewGroup parent) {
				// creates view
				View view = super.getView(position, convertView, parent);
				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);

				// put the image on the text view
				textView.setCompoundDrawablesWithIntrinsicBounds(
						imageList[position].icon, 0, 0, 0);

				// add margin between image and text (support various screen
				// densities)
				int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
				textView.setCompoundDrawablePadding(dp5);

				return view;
			}
		};

	}

	private class Item {
		public String file;
		public int icon;

		public Item(String file, Integer icon) {
			this.file = file;
			this.icon = icon;
		}

		@Override
		public String toString() {
			return file;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new Builder(this);

		if (imageList == null) {
			Log.e(TAG, "No image loaded");
			dialog = builder.create();
			return dialog;
		}

		switch (id) {
		case DIALOG_LOAD_IMAGES:
			builder.setTitle("Choose your OpenStack Image");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					chosenImage = imageList[which].file;
					
						// Perform action with file picked
						Utils.putStringPreference( "SELECTED_OSIMAGE", chosenImage, ImagesExplore.this );
						ImagesExplore.this.finish();

				}
			});
			break;
		}
		dialog = builder.show();
		return dialog;
	}
	

}
