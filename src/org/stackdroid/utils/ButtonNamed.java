package org.stackdroid.utils;

import android.widget.Button;
import android.content.Context;

import org.stackdroid.views.FloatingIPView;
import org.stackdroid.views.ListSecGroupView;
import org.stackdroid.views.RuleView;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.UserView;
import org.stackdroid.views.ServerView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.VolumeView;

public class ButtonNamed extends Button implements Named {

    public static final int BUTTON_CONSOLE_LOG   = 1;
    
	private RuleView relatedRuleView = null;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private VolumeView relatedVolumeView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private SecGroupView relatedSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    
    

    private int type;

    public ButtonNamed( Context ctx, UserView uv, int _type ) {
	super( ctx );
	relatedUserView = uv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, ServerView sv, int _type ) {
	super( ctx );
	relatedServerView = sv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, OSImageView iv, int _type ) {
	super( ctx );
	relatedOSImageView = iv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, NetworkView iv, int _type ) {
	super( ctx );
	relatedNetworkView = iv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, FloatingIPView iv, int _type ) {
	super( ctx );
	relatedFloatingIPView = iv;
 	this.type = _type;
    }

    public ButtonNamed( Context ctx, ListSecGroupView sv, int _type ) {
    	super( ctx );
    	relatedListSecGroupView = sv;
     	this.type = _type;
        }
    
    public UserView getUserView( ) { return relatedUserView; }
    public RuleView getRuleView( ) { return relatedRuleView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public SecGroupView getSecGroupView( ) { return relatedSecGroupView; }
    public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    public VolumeView getVolumeView( ) { return relatedVolumeView; }
    public int getType( ) { return type; }
};
