package org.stackdroid.views;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;

import android.graphics.Typeface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import android.content.Context;
import android.widget.ProgressBar;

import org.stackdroid.R;
import org.stackdroid.utils.*;

public class ServerView extends LinearLayout {
    
    private LinearLayoutWithView row  = null;
    private LinearLayoutWithView text = null;
    private LinearLayoutWithView btns = null;
    private TextViewWithView Name     = null;
    private TextViewWithView Flavor   = null;
    private TextViewWithView Status   = null;
    private TextViewWithView Task     = null;
    private TextViewWithView Uptime   = null;

    private ImageButtonWithView manageServer = null;
    private ImageButtonWithView deleteServer = null;
    private ButtonWithView addIPToServer = null;

    private LinearLayoutWithView btns2 = null;
    private ButtonWithView consoleLog = null;
	private ProgressBar serverUpdateProgress = null;


	private Server S = null;

	public TextView getStatusTextView( ) { return Status; }

	public void setStatus( String status ) {
		S.setStatus(status);
		Status.setText("Status: " + status);
		if(status.compareToIgnoreCase("active")==0) {
			Status.setTextColor(Color.parseColor("#00AA00"));
			serverUpdateProgress.setVisibility(View.INVISIBLE);
		}
		if(status.compareToIgnoreCase("error")==0) {
			Status.setTextColor(Color.parseColor("#AA0000"));
			serverUpdateProgress.setVisibility(View.INVISIBLE);
		}
	}

    public ServerView( Server s, 
    			OnClickListener infoListener, 
    			OnClickListener consoleLogListener,
    			OnClickListener deleteServerListener,
    			OnClickListener addIP,
    			OnClickListener manageServerListener,
    			Context ctx ) 
    {
    	super(ctx);
    	S = s;
	
    	setOrientation( LinearLayout.HORIZONTAL );
    	LinearLayout.LayoutParams params1 
		    = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		setLayoutParams( params1 );
		int padding = Utils.getDisplayPixel( ctx, 2 );
		setPadding( padding, padding, padding, padding );
	
		row = new LinearLayoutWithView( ctx, this );
		row.setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams _params1
		    	= new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		row.setLayoutParams( _params1 );
		row.setBackgroundResource(R.drawable.rounded_corner_thin);


		text = new LinearLayoutWithView( ctx, (ServerView)this );
		text.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
		text.setLayoutParams( params2 );

		Name = new TextViewWithView( ctx, (ServerView)this );
		String servName = S.getName();
		Name.setText( servName );
		Name.setTextColor( Color.parseColor("#333333") );
		Name.setOnClickListener(infoListener);
		Name.setTypeface(null, Typeface.BOLD);
		Name.setEllipsize(TextUtils.TruncateAt.END);
		Name.setSingleLine();
	
		Flavor = new TextViewWithView( ctx, (ServerView)this );
	
		String flavName = S.getFlavor( )!=null ? S.getFlavor().getName() : "N/A";

		Flavor.setText( "Flavor: "+flavName );
		Flavor.setOnClickListener( infoListener );
		Flavor.setTextColor(Color.parseColor("#999999"));
		Flavor.setEllipsize(TextUtils.TruncateAt.END);
		Flavor.setSingleLine();

		Status = new TextViewWithView( ctx, (ServerView)this );
		Status.setText("Status: "+S.getStatus( ) );
		Status.setOnClickListener( infoListener );

		Task = new TextViewWithView( ctx, (ServerView)this );
		Task.setText("Task: "+(S.getTask()!=null || S.getTask().compareTo("null")==0 ? S.getTask() : getContext().getString(R.string.NONE) ) );
		Task.setOnClickListener( infoListener );

		if(S.getStatus( ).compareToIgnoreCase("active")==0)
			Status.setTextColor( Color.parseColor("#00AA00") );
		if(S.getStatus( ).compareToIgnoreCase("error")==0)
			Status.setTextColor( Color.parseColor("#AA0000") );
		if(S.getStatus( ).compareToIgnoreCase("build")==0) {
			Status.setText("Status: " + S.getStatus( )) ;//+" (" + (S.getTask( )!=null || S.getTask().compareTo("null")==0 ? S.getTask() : getContext().getString(R.string.NONE)) + ")");
		//if(S.getTask( ).compareToIgnoreCase("deleting")==0) 
		//	Status.setTextColor( Color.parseColor("#000000") );
		}
		if(S.getTask( ).compareToIgnoreCase("null")==0) 
			Task.setText("Task: " + ctx.getString(R.string.NONE));
		
		Uptime = new TextViewWithView(ctx, (ServerView)this);
		long delay = Utils.now( ) - S.getStartTime( ) ;
		
		int minutes = (int) ((delay / (60)) % 60);
		int hours   = (int) ((delay / (60*60)) % 24);
		Uptime.setText( "uptime: " + hours + " hr "+minutes+" mins" );
		Uptime.setOnClickListener( infoListener );
		
		LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		consoleLog = new ButtonWithView(ctx, this);
		consoleLog.setText("Console Log");
		consoleLog.setTextSize(10.0f);
		int density = 200;
		String dispDensity = Configuration.getInstance().getValue("DISPLAYDENSITY", "200");
		if(dispDensity!=null)
			density = Integer.parseInt(dispDensity);
		consoleLog.setPadding(10 * density, 2 * density, 10 * density, 2 * density);
		consoleLog.setOnClickListener(consoleLogListener);
		consoleLog.setLayoutParams(params5);

		serverUpdateProgress = new ProgressBar( ctx, null, android.R.attr.progressBarStyleSmall );
		serverUpdateProgress.setIndeterminate(true);
		LayoutParams params7 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		serverUpdateProgress.setLayoutParams(params7);
		serverUpdateProgress.setVisibility(View.INVISIBLE);

		btns2 = new LinearLayoutWithView( ctx, (ServerView)this );
		btns2.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		btns2.addView(consoleLog);
		btns2.addView(serverUpdateProgress);

		text.addView(Name);
		text.addView(Flavor);
		text.addView(Status);
		text.addView(Task);
		text.addView(Uptime);
		text.addView(btns2);
		text.setOnClickListener( infoListener );
		row.addView(text);
		setOnClickListener( infoListener );

		deleteServer = new ImageButtonWithView( ctx, this );
		deleteServer.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
		deleteServer.setOnClickListener( deleteServerListener );

		manageServer = new ImageButtonWithView( ctx, this );
		manageServer.setImageResource(android.R.drawable.ic_menu_edit);
		manageServer.setOnClickListener( manageServerListener );

		addIPToServer = new ButtonWithView( ctx, this );
		addIPToServer.setText("IP");
		addIPToServer.setOnClickListener(addIP);
	
		btns = new LinearLayoutWithView( ctx, (ServerView)this );
		btns.setOrientation( LinearLayout.HORIZONTAL );
	
		LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f);
		btns.setLayoutParams( params3 );
		btns.setGravity( Gravity.RIGHT | Gravity.CENTER_VERTICAL);

		btns.addView( addIPToServer );
		btns.addView( manageServer );
		btns.addView( deleteServer );
	
		row.addView( btns );
		addView( row );
    }

    public Server getServer( ) { return S; }

	public void activateStatusUpdatePB() {
		serverUpdateProgress.setVisibility(View.VISIBLE);
	}

	public void deactivateStatusUpdatePB() {
		serverUpdateProgress.setVisibility(View.INVISIBLE);
	}

}
