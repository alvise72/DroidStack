package org.droidstack.utils;

public class TenantQuota {
    private int maxSecGroups;
    private int maxCores;
    private int maxInstances;
    private int maxRAMSize;
    private int maxFloatingIPs;

    public TenantQuota( int maxgroups,
			int maxcores,
			int maxinst,
			int maxram,
			int maxfip )
    {
	maxSecGroups   = maxgroups;
	maxCores       = maxcores;
	maxInstances   = maxinst;
	maxRAMSize     = maxram;
	maxFloatingIPs = maxfip;
    }

    public int getMaxSecGroups( ) { return maxSecGroups; }
    public int getMaxCores( ) { return maxCores; }
    public int getMaxInstances( ) { return maxInstances; }
    public int getMaxRAMSize( ) { return maxRAMSize; }
    public int getMaxFloatingIPs( ) { return maxFloatingIPs;  }
}
