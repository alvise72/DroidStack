package org.stackdroid.comm.commands;


import org.stackdroid.utils.User;
import org.stackdroid.utils.Utils;
import org.stackdroid.utils.Defaults;
import org.stackdroid.comm.RESTClient;
import org.stackdroid.utils.Configuration;



import android.util.Pair;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Hashtable;

public abstract class Command {
	
   public enum commandType {
   	   LISTIMAGES,
   	   DELETEIMAGE,
   	   LISTSERVERS,
   	   CREATESERVER,
   	   SERVERINFO,
   	   DELETEVOLUME,
   	   CREATEVOLUME,
   	   DETACHVOLUME,
   	   ATTACHVOLUME
   }
   
   protected User U;
   protected String restResponse;
   
   /**
    * 
    * 
    * 
    * 
    * 
    * 
    */   
   public abstract void execute( ) throws Exception;
   
   /**
    * 
    * 
    * 
    * 
    * 
    * 
    */    
   public abstract void setup( String id );
   public abstract void setup( String id, String id2 );
   public abstract void setup( String id, int integer);
   public abstract void setup(String serverName, 
					  		  String imageID,
					  		  String key_name,
					  		  String flavorID,
					  		  int count,
					  		  String securityGroupID,
					  		  Hashtable<Pair<String,String>, String> netID_to_netIP);
   /**
    * 
    * 
    * 
    * 
    * 
    * 
    */    
   public String getRESTResponse( ) { return restResponse; }
   
   /**
    * 
    * 
    * 
    * 
    * 
    * 
    */				       		
   public static Command commandFactory( commandType type, User U ) {
   	   switch(type) {
   	   case LISTIMAGES:
   	   	   return new ListImagesCommand( U );
   	   case DELETEIMAGE:
   	   	   return new DeleteImageCommand( U );
   	   case LISTSERVERS:
   	   	   return new ListServersCommand( U );
   	   case CREATESERVER:
   	   	   return new CreateServerCommand( U );
   	   case SERVERINFO:
   	   	   return new ServerInfoCommand( U );
   	   case DELETEVOLUME:
   	   	   return new DeleteVolumeCommand( U );
   	   case CREATEVOLUME:
   	   	   return new CreateVolumeCommand( U );
   	   case DETACHVOLUME:
   	   	   return new DetachVolumeCommand( U );
   	   case ATTACHVOLUME:
   	   	   return new AttachVolumeCommand( U );
   	   default:
   	   	   return null;
   	   }
   }
   
   /**
    * 
    * 
    * 
    * 
    * 
    * 
    */
   protected void checkToken( ) throws Exception {
	
   	   String gapiver = U.getGlanceEndpointAPIVER( );
   	   String napiver = U.getNeutronEndpointAPIVER( );
	    
   	   if(U.getVerifyServerCert()) {
   	   	   X509Certificate cert = null;
   	   	   cert = (X509Certificate)(CertificateFactory.getInstance("X.509")).generateCertificate(new FileInputStream( U.getCAFile() ));
   	   	   if(RESTClient.checkServerCert(U.getIdentityEndpoint(), cert.getIssuerX500Principal().getName()) == false)
   	   	   	   throw new CertificateException("Couldn't verify server's certificate. Please verify the correct CA selected.");
   	   }

   	   long exp_time = U.getTokenExpireTime();

    	if(exp_time <= Utils.now() + 5) {
    		String payload = null;
    		if(U.useV3())
    			payload = "{ \"auth\": { \"identity\": { \"methods\": [\"password\"],\"password\": { \"user\": { \"name\": \"" + U.getUserName() + "\",\"domain\": { \"id\": \"default\" }, \"password\": \"" + U.getPassword() + "\" } } }, \"scope\": { \"project\": { \"name\": \"" +  U.getTenantName() + "\", \"domain\": { \"id\": \"default\" } }}}}";
    		else
    			payload = "{\"auth\": {\"tenantName\": \""
		  	  + U.getTenantName() 
		  	  + "\", \"passwordCredentials\": {\"username\": \"" 
		  	  + U.getUserName( ) + "\", \"password\": \"" 
		  	  + U.getPassword() + "\"}}}";
	    
		  	  String identityEP = U.getIdentityEndpoint();
		  	  if(U.useV3() )
		  	  	  identityEP += "/auth";
		  	  identityEP += "/tokens";
	    
		  	  Pair<String,String> jsonBuffer_Token = RESTClient.requestToken(U.useSSL(),
				      												   		 identityEP,
				      												   		 payload);
	    
			  String  pwd = U.getPassword();
			  String  edp = U.getIdentityEndpoint();
			  boolean ssl = U.useSSL();
			  boolean verifyServerCert = U.getVerifyServerCert();
			  String CAFile = U.getCAFile();


			  U = User.parse( jsonBuffer_Token.first, U.useV3( ), jsonBuffer_Token.second );
			  U.setPassword( pwd);
			  U.setSSL(ssl);
			  U.setCAFile( CAFile );
			  U.setGlanceEndpointAPIVER( gapiver );
			  U.setNeutronEndpointAPIVER( napiver );
			  U.toFile(Configuration.getInstance().getValue("FILESDIR", Defaults.DEFAULTFILESDIR));
		}
    }
    
}