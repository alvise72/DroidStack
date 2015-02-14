package org.stackdroid.utils;

import org.json.JSONObject;
import org.stackdroid.parse.ParseException;

public class Quota {
    private final int maxCPU;
    private final int currCPU;
    
    private final int maxVM;
    private final int currVM;
    
    private final int maxRAM;
    private final int currRAM;

    private final int maxSECG;
    private final int currSECG;

    private final int maxFIP;
    private final int currFIP;
    

    public Quota( int currentInstance, 
    			  int currentVirtCPU,
    			  int currentRAM,
    			  int currentFIP,
    			  int currentSECG,
    			  int maxInstances,
    			  int maxVirtCPU,
    			  int maxRAM,
    			  int maxFIP,
    			  int maxSecGroups ) {
	this.maxCPU  = maxVirtCPU;
	this.currCPU = currentVirtCPU;

	this.maxVM  = maxInstances;
	this.currVM = currentInstance;

	this.maxRAM  = maxRAM;
	this.currRAM = currentRAM;

	this.maxSECG  = maxSecGroups;
	this.currSECG = currentSECG;

	this.maxFIP  = maxFIP;
	this.currFIP = currentFIP;
    }

    public int getMaxRAM() { return maxRAM; }
    public int getMaxInstances() { return maxVM; }
    public int getMaxFloatingIP() { return maxFIP; }
    public int getMaxSecurityGroups() { return maxSECG; }
    public int getMaxCPU() { return maxCPU;}
    public int getCurrentRAM() { return currRAM;}
    public int getCurrentCPU() { return currCPU;}
    public int getCurrentFloatingIP() { return currFIP;}
    public int getCurrentInstances() { return currVM;}
    public int getCurrentSecurityGroups() { return currSECG;}

    public static Quota parse( String jsonBuf )  throws ParseException {
    	try {
    	    JSONObject jsonObject = new JSONObject( jsonBuf );
    	    JSONObject limits     = (JSONObject)jsonObject.getJSONObject("limits");
    	    JSONObject absolute   = (JSONObject)limits.getJSONObject("absolute");
    	    int maxInstances      = absolute.getInt("maxTotalInstances");
    	    int maxVirtCPU        = absolute.getInt("maxTotalCores");
    	    int maxRAM            = absolute.getInt("maxTotalRAMSize");
    	    int maxFIP            = absolute.getInt("maxTotalFloatingIps");
    	    int maxSecGroups      = absolute.getInt("maxSecurityGroups");
    	    int currentInstance   = absolute.getInt("totalInstancesUsed");
    	    int currentVirtCPU    = absolute.getInt("totalCoresUsed");
    	    int currentRAM        = absolute.getInt("totalRAMUsed");
    	    int currentFIP        = absolute.getInt("totalFloatingIpsUsed");
    	    int currentSECG       = absolute.getInt("totalSecurityGroupsUsed");
    	    return new Quota(currentInstance, 
    			     currentVirtCPU,
    			     currentRAM,
    			     currentFIP,
    			     currentSECG,
    			     maxInstances,
    			     maxVirtCPU,
    			     maxRAM,
    			     maxFIP,
    			     maxSecGroups );
    	} catch(org.json.JSONException je) {
    	    throw new ParseException( je.getMessage( ) );
    	}
        }
}
