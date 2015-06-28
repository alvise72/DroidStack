package org.stackdroid.utils;

import android.widget.ImageView;
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

public class ImageViewWithView extends ImageView implements GetView {

    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private FloatingIPView relatedFloatingIPView = null;
	private ListSecGroupView relatedListSecGroupView = null;
	private RuleView relatedRuleView = null;
	private VolumeView relatedVolumeView = null;
	private SecGroupView relatedSecGroupView = null;
    private NetworkListView relatedNetworkListView = null;
    private RouterView relatedRouterView = null;
    private RouterPortView relatedRouterPortView   = null;


    public ImageViewWithView( Context ctx, RouterView rv ) {
        super( ctx );
        relatedRouterView = rv;
    }

    public ImageViewWithView( Context ctx, UserView uv ) {
    	super( ctx );
    	relatedUserView = uv;
    }

    public ImageViewWithView( Context ctx, ServerView sv ) {
    	super( ctx );
    	relatedServerView = sv;
    }

    public ImageViewWithView( Context ctx, OSImageView sv ) {
    	super( ctx );
    	relatedOSImageView = sv;
    }

    public ImageViewWithView( Context ctx, NetworkView nv ) {
    	super( ctx );
    	relatedNetworkView = nv;
    }

    public ImageViewWithView( Context ctx, NetworkListView nv ) {
    	super( ctx );
    	relatedNetworkListView = nv;
    }

    @Override
	public UserView getUserView( ) { return relatedUserView; }
    @Override
	public ServerView getServerView( ) { return relatedServerView; }
    @Override
	public OSImageView getOSImageView( ) { return relatedOSImageView; }
    @Override
	public NetworkView getNetworkView( ) { return relatedNetworkView; }
    @Override
	public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    @Override
	public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
    @Override
	public RuleView getRuleView( ) { return relatedRuleView; }
    @Override
	public VolumeView getVolumeView( ) { return relatedVolumeView; }
    @Override
	public SecGroupView getSecGroupView( ) { return relatedSecGroupView; }

	@Override
	public NetworkListView getNetworkListView() { return relatedNetworkListView; }
    @Override
    public RouterView getRouterView() {return relatedRouterView;}
    @Override
    public RouterPortView getRouterPortView( ) { return relatedRouterPortView; }
};




