package org.stackdroid.utils;

import android.widget.ImageButton;
import android.content.Context;

import org.stackdroid.views.FloatingIPView;
import org.stackdroid.views.ListSecGroupView;
import org.stackdroid.views.NetworkListView;
import org.stackdroid.views.RouterPortView;
import org.stackdroid.views.RouterView;
import org.stackdroid.views.RuleView;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.UserView;
import org.stackdroid.views.ServerView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.VolumeView;

public class ImageButtonWithView extends ImageButton implements GetView {
	
    private RuleView relatedRuleView = null;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private VolumeView relatedVolumeView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private FloatingIPView relatedFloatingIPView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private SecGroupView relatedSecGroupView = null;
    private NetworkListView relatedNetworkListView = null;
    private RouterView relatedRouterView = null;
    private RouterPortView relatedRouterPortView   = null;


    public ImageButtonWithView( Context ctx, RouterView rv ) {
        super( ctx );
        relatedRouterView = rv;
    }

    public ImageButtonWithView( Context ctx, UserView uv ) {
    	super( ctx );
    	relatedUserView = uv;
    }

    public ImageButtonWithView( Context ctx, ServerView sv ) {
    	super( ctx );
    	relatedServerView = sv;
    }

    public ImageButtonWithView( Context ctx, OSImageView iv ) {
    	super( ctx );
    	relatedOSImageView = iv;
    }

    public ImageButtonWithView( Context ctx, NetworkView iv ) {
    	super( ctx );
    	relatedNetworkView = iv;
    }

    public ImageButtonWithView( Context ctx, FloatingIPView iv ) {
    	super( ctx );
    	relatedFloatingIPView = iv;
    }

    public ImageButtonWithView( Context ctx, ListSecGroupView sv ) {
    	super( ctx );
    	relatedListSecGroupView = sv;
    }

    public ImageButtonWithView( Context ctx, VolumeView vv ) {
    	super( ctx );
   	    relatedVolumeView = vv;
    }
    
    public ImageButtonWithView( Context ctx, RuleView rv ) {
   	    super( ctx );
   	    relatedRuleView = rv;
    }

    public ImageButtonWithView( Context ctx, NetworkListView rv ) {
   	    super( ctx );
   	    relatedNetworkListView = rv;
    }
    public ImageButtonWithView( Context ctx, RouterPortView rv ) {
        super( ctx );
        relatedRouterPortView = rv;
    }
    @Override
	public RuleView getRuleView( ) { return relatedRuleView; }
    @Override
	public UserView getUserView( ) { return relatedUserView; }
    @Override
	public VolumeView getVolumeView( ) { return relatedVolumeView; }
    @Override
	public ServerView getServerView( ) { return relatedServerView; }
    @Override
	public OSImageView getOSImageView( ) { return relatedOSImageView; }
    @Override
	public NetworkView getNetworkView( ) { return relatedNetworkView; }
    @Override
	public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
    @Override
	public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    @Override
	public SecGroupView getSecGroupView( ) { return relatedSecGroupView ; }
    @Override
	public NetworkListView getNetworkListView() { return relatedNetworkListView; }
    @Override
    public RouterView getRouterView() {return relatedRouterView;}
    @Override
    public RouterPortView getRouterPortView( ) { return relatedRouterPortView; }
};
