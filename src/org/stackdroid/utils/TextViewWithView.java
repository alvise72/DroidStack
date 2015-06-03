package org.stackdroid.utils;

import android.widget.TextView;
import android.content.Context;

import org.stackdroid.views.FloatingIPView;
import org.stackdroid.views.ListSecGroupView;
import org.stackdroid.views.NetworkListView;
import org.stackdroid.views.RouterView;
import org.stackdroid.views.RuleView;
import org.stackdroid.views.SecGroupView;
import org.stackdroid.views.UserView;
import org.stackdroid.views.ServerView;
import org.stackdroid.views.OSImageView;
import org.stackdroid.views.NetworkView;
import org.stackdroid.views.VolumeView;

public class TextViewWithView extends TextView implements GetView {

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

    public TextViewWithView( Context ctx, RouterView rv ) {
        super( ctx );
        relatedRouterView = rv;
    }

    public TextViewWithView( Context ctx, UserView uv ) {
    	super( ctx );
    	relatedUserView = uv;
    }

    public TextViewWithView( Context ctx, ServerView sv ) {
    	super( ctx );
    	relatedServerView = sv;
    }

    public TextViewWithView( Context ctx, OSImageView sv ) {
    	super( ctx );
    	relatedOSImageView = sv;
    }

    public TextViewWithView( Context ctx, NetworkView nv ) {
    	super( ctx );
    	relatedNetworkView = nv;
    }
    
    public TextViewWithView( Context ctx, ListSecGroupView nv ) {
    	super( ctx );
    	relatedListSecGroupView = nv;
    }

    public TextViewWithView( Context ctx, VolumeView vv ) {
   	    super( ctx );
   	    relatedVolumeView = vv;
    }
    
    public TextViewWithView( Context ctx, RuleView rv ) {
   	    super( ctx );
   	    relatedRuleView = rv;
    }
    
    public TextViewWithView(Context ctx, NetworkListView nv ){
    	super( ctx );
   	    relatedNetworkListView = nv;
    }
    
    public TextViewWithView(Context ctx, SecGroupView sv ){
    	super( ctx );
    	relatedSecGroupView = sv;
    }
    
    public TextViewWithView(Context ctx, FloatingIPView nv ){
    	super( ctx );
    	relatedFloatingIPView = nv;
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
};
