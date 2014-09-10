package org.stackdroid.utils;

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

}
