package org.stackdroid.utils;

import android.widget.CheckBox;
import android.widget.TextView;
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

public class CheckBoxWithView extends CheckBox implements GetView {

	private RuleView relatedRuleView = null;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private VolumeView relatedVolumeView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private SecGroupView relatedSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    private NetworkListView relatedNetworkListView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private RouterView relatedRouterView = null;
    private RouterPortView relatedRouterPortView   = null;

    public CheckBoxWithView( Context ctx, RouterView rv ) {
        super( ctx );
        relatedRouterView = rv;
    }

    public CheckBoxWithView( Context ctx, UserView uv ) {
    	super( ctx );
    	relatedUserView = uv;
    }

    public CheckBoxWithView( Context ctx, ServerView sv ) {
    	super( ctx );
    	relatedServerView = sv;
    }

    public CheckBoxWithView( Context ctx, OSImageView sv ) {
    	super( ctx );
    	relatedOSImageView = sv;
    }

    public CheckBoxWithView( Context ctx, NetworkView nv ) {
    	super( ctx );
    	relatedNetworkView = nv;
    }
    
    public CheckBoxWithView( Context ctx, ListSecGroupView nv ) {
    	super( ctx );
    	relatedListSecGroupView = nv;
    }

    public CheckBoxWithView( Context ctx, VolumeView vv ) {
   	    super( ctx );
   	    relatedVolumeView = vv;
    }
    
    public CheckBoxWithView( Context ctx, RuleView rv ) {
   	    super( ctx );
   	    relatedRuleView = rv;
    }
    
    public CheckBoxWithView(Context ctx, NetworkListView nv ){
    	super( ctx );
   	    relatedNetworkListView = nv;
    }
    
    public CheckBoxWithView(Context ctx, SecGroupView sv ){
    	super( ctx );
    	relatedSecGroupView = sv;
    }
    
    public CheckBoxWithView(Context ctx, FloatingIPView nv ){
    	super( ctx );
    	relatedFloatingIPView = nv;
    }
    public CheckBoxWithView( Context ctx, RouterPortView rv ) {
        super( ctx );
        relatedRouterPortView = rv;
    }
    
    
    @Override
	public RuleView getRuleView( ) { return relatedRuleView; }
    @Override
	public UserView getUserView( ) { return relatedUserView; }
    @Override
	public ServerView getServerView( ) { return relatedServerView; }
    @Override
	public OSImageView getOSImageView( ) { return relatedOSImageView; }
    @Override
	public NetworkView getNetworkView( ) { return relatedNetworkView; }
    @Override
	public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView;}
    @Override
	public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
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
