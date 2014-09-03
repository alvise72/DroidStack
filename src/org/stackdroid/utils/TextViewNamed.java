package org.stackdroid.utils;

import android.widget.TextView;
import android.content.Context;

import org.stackdroid.views.FloatingIPView;
import org.stackdroid.views.ListSecGroupView;
import org.stackdroid.views.RuleView;
import org.stackdroid.views.UserView;
import org.stackdroid.views.ServerView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.VolumeView;

public class TextViewNamed extends TextView implements Named {

	private RuleView relatedRuleView = null;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private VolumeView relatedVolumeView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    
    public TextViewNamed( Context ctx, UserView uv ) {
	super( ctx );
	relatedUserView = uv;
    }

    public TextViewNamed( Context ctx, ServerView sv ) {
	super( ctx );
	relatedServerView = sv;
    }

    public TextViewNamed( Context ctx, OSImageView sv ) {
	super( ctx );
	relatedOSImageView = sv;
    }

    public TextViewNamed( Context ctx, NetworkView nv ) {
	super( ctx );
	relatedNetworkView = nv;
    }
    public TextViewNamed( Context ctx, ListSecGroupView nv ) {
    	super( ctx );
    	relatedListSecGroupView = nv;
        }

    public TextViewNamed( Context ctx, VolumeView vv ) {
   	    super( ctx );
   	    relatedVolumeView = vv;
     }
    
    public TextViewNamed( Context ctx, RuleView rv ) {
   	    super( ctx );
   	    relatedRuleView = rv;
     }
    
    public RuleView getRuleView( ) { return relatedRuleView; }
    public UserView getUserView( ) { return relatedUserView; }
    public ServerView getServerView( ) { return relatedServerView; }
    public OSImageView getOSImageView( ) { return relatedOSImageView; }
    public NetworkView getNetworkView( ) { return relatedNetworkView; }
    public ListSecGroupView getSecGroupView( ) { return relatedListSecGroupView;}
    public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    public VolumeView getVolumeView( ) { return relatedVolumeView; }
    public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
};
