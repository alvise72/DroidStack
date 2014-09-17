package org.stackdroid.utils;

import android.widget.EditText;
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

public class EditTextNamed extends EditText implements Named {

	private RuleView relatedRuleView = null;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private VolumeView relatedVolumeView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private SecGroupView relatedSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    
    public EditTextNamed( Context ctx, UserView uv ) {
    	super( ctx );
    	relatedUserView = uv;
    }

    public EditTextNamed( Context ctx, ServerView sv ) {
    	super( ctx );
    	relatedServerView = sv;
    }

    public EditTextNamed( Context ctx, OSImageView sv ) {
    	super( ctx );
    	relatedOSImageView = sv;
    }

    public EditTextNamed( Context ctx, NetworkView nv ) {
    	super( ctx );
    	relatedNetworkView = nv;
    }

    public EditTextNamed( Context ctx, RuleView rv ) {
   	    super( ctx );
   	    relatedRuleView = rv;
     }
    
    public RuleView getRuleView( ) { return relatedRuleView; }
    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
    public VolumeView getVolumeView( ) { return relatedVolumeView; }
    public SecGroupView getSecGroupView( ) { return relatedSecGroupView; }
};
