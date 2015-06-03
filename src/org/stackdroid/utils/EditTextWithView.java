package org.stackdroid.utils;

import android.widget.EditText;
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

public class EditTextWithView extends EditText implements GetView {

	private RuleView relatedRuleView = null;
    private UserView relatedUserView = null;
    private ServerView relatedServerView = null;
    private VolumeView relatedVolumeView = null;
    private OSImageView relatedOSImageView = null;
    private NetworkView relatedNetworkView = null;
    private SecGroupView relatedSecGroupView = null;
    private FloatingIPView relatedFloatingIPView = null;
    private ListSecGroupView relatedListSecGroupView = null;
    private NetworkListView relatedNetworkListView = null;
    private RouterView relatedRouterView = null;

    public EditTextWithView( Context ctx, RouterView rv ) {
        super( ctx );
        relatedRouterView = rv;
    }

    public EditTextWithView( Context ctx, UserView uv ) {
    	super( ctx );
    	relatedUserView = uv;
    }

    public EditTextWithView( Context ctx, ServerView sv ) {
    	super( ctx );
    	relatedServerView = sv;
    }

    public EditTextWithView( Context ctx, OSImageView sv ) {
    	super( ctx );
    	relatedOSImageView = sv;
    }

    public EditTextWithView( Context ctx, NetworkView nv ) {
    	super( ctx );
    	relatedNetworkView = nv;
    }

    public EditTextWithView( Context ctx, VolumeView rv ) {
   	    super( ctx );
   	    relatedVolumeView = rv;
    }

    public EditTextWithView( Context ctx, SecGroupView rv ) {
   	    super( ctx );
   	    relatedSecGroupView = rv;
    }

    public EditTextWithView( Context ctx, FloatingIPView rv ) {
   	    super( ctx );
   	    relatedFloatingIPView = rv;
    }

    public EditTextWithView( Context ctx, ListSecGroupView rv ) {
   	    super( ctx );
   	    relatedListSecGroupView = rv;
    }

    public EditTextWithView( Context ctx, NetworkListView rv ) {
   	    super( ctx );
   	    relatedNetworkListView = rv;
    }

    public EditTextWithView( Context ctx, RuleView rv ) {
   	    super( ctx );
   	    relatedRuleView = rv;
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
	public FloatingIPView getFloatingIPView( ) { return relatedFloatingIPView; }
    @Override
	public ListSecGroupView getListSecGroupView( ) { return relatedListSecGroupView; }
    @Override
	public VolumeView getVolumeView( ) { return relatedVolumeView; }
    @Override
	public SecGroupView getSecGroupView( ) { return relatedSecGroupView; }
    @Override
	public NetworkListView getNetworkListView() { return relatedNetworkListView; }
    @Override
    public RouterView getRouterView() {return relatedRouterView;}
};
