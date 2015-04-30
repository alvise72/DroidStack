package org.stackdroid.activities;
import android.content.Intent;
import android.widget.Toast;
import java.io.File;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import org.stackdroid.R;
import android.widget.ArrayAdapter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import android.view.View;
import android.os.Environment;
import android.util.Log;

public class ListFileActivity extends ListActivity {

  private String path;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_files);

    // Use the current directory as title
    File sd = Environment.getExternalStorageDirectory();
    path = sd.getAbsolutePath( );
    if (getIntent().hasExtra("path")) {
      path = getIntent().getStringExtra("path");
    }
    Log.d("LISTFILEACT-Create", "path="+path);
    setTitle(path);

    // Read all files sorted into the values-array
    List values = new ArrayList();
    File dir = new File(path);
    if (!dir.canRead()) {
      setTitle(getTitle() + " (inaccessible)");
    }
    String[] list = dir.list();
    if (list != null) {
      for (String file : list) {
        if (!file.startsWith(".")) {
          values.add(file);
        }
      }
    }
    Collections.sort(values);

    // Put the data into the list
    ArrayAdapter adapter = new ArrayAdapter(this,
        android.R.layout.simple_list_item_2, android.R.id.text1, values);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    String filename = (String) getListAdapter().getItem(position);
    if (path.endsWith(File.separator)) {
      filename = path + filename;
    } else {
      filename = path + File.separator + filename;
    }
    if (new File(filename).isDirectory()) {
	Log.d("LISTFILEACT", "path="+filename);
      Intent intent = new Intent(this, ListFileActivity.class);
      intent.putExtra("path", filename);
      startActivity(intent);
    } else {
      Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
    }
  }
}
