package org.stackdroid.utils;

import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class QuotaVol {
	private final int volUsage;
	private final int gigaUsage;
	private final int snapsUsage;
	private final int maxVol;
	private final int maxGiga;
	private final int maxSnaps;
	
	public QuotaVol( int vu, int gu, int su, int maxv, int maxg, int maxs ) {
		volUsage = vu;
		gigaUsage = gu;
		snapsUsage = su;
		maxVol = maxv;
		maxGiga = maxg;
		maxSnaps = maxs;
	}
	
	public int getMaxVolumes( )		{ return maxVol; }
	public int getMaxGigabytes( ) 	{ return maxGiga; }
	public int getMaxSnapshots( ) 	{ return maxSnaps; }
	public int getVolumeUsage( ) 	{ return volUsage; }
	public int getGigabyteUsage( )	{ return gigaUsage; }
	public int getSnapshotUsage( )	{ return snapsUsage; }

	public static QuotaVol parse(String jsonVols) throws ParseException  {
		
		try {
			JSONObject quota = (new JSONObject( jsonVols )).getJSONObject("quota_set");
			JSONObject giga = quota.getJSONObject("gigabytes");
			JSONObject vols = quota.getJSONObject("volumes");
			JSONObject snaps= quota.getJSONObject("snapshots");
			
			int volUsage = vols.getInt("in_use");
			int gigaUsage = giga.getInt("in_use");
			int snapUsage = snaps.getInt("in_use");
			int maxVols  = vols.getInt("limit");
			int maxGiga = giga.getInt("limit");
			int maxSnaps = snaps.getInt("limit");
			return new QuotaVol( volUsage, gigaUsage, snapUsage, maxVols, maxGiga, maxSnaps );
		} catch(org.json.JSONException je) {
			throw new ParseException( je.getMessage( ) );
		}
    }
}
