package org.openstack.utils;

//import java.util.Serializable;

public class SecGroup {//implements Serializable {
    private String name;
    private String id;
    
    public SecGroup(String name, String id) {
	this.name = name;
	this.id    = id;
    }

    public String getName( ) { return name; }
    public String getID( ) { return id; }

    @Override
	public String toString( ) { return "SecGroup{name="+name+",ID="+id+"}"; }


//     public static User fromFileID( String ID ) throws RuntimeException {
// 	String filename = Environment.getExternalStorageDirectory() + "/AndroStack/users/" + ID;
// 	if(false == (new File(filename)).exists())
// 	    throw new RuntimeException( "File ["+filename+"] doesn't exist" );
// 	try {
// 	    InputStream is = new FileInputStream( filename );
// 	    ObjectInputStream ois = new ObjectInputStream( is );
// 	    User U = (User)ois.readObject( );
// 	    ois.close( );
// 	    return U;
// 	} catch(IOException ioe) {
// 	    throw new RuntimeException( "InputStream.read/close: " + ioe.getMessage( ) );
// 	} catch(ClassNotFoundException cnfe) {
// 	    throw new RuntimeException( "ObjectInputStream.readObject: " + cnfe.getMessage( ) );
// 	}
//     }

//     public void toFile( ) throws RuntimeException {
//     	String filename = Environment.getExternalStorageDirectory() + "/AndroStack/users/" + getUserID( ) + "." + getTenantID( );
//     	File f = new File( filename );
//     	if(f.exists()) f.delete();
// 	try {
// 	    OutputStream os = new FileOutputStream( filename );
// 	    ObjectOutputStream oos = new ObjectOutputStream( os );
// 	    oos.writeObject( this );
// 	    oos.close( );
// 	} catch(IOException ioe) {
// 	    throw new RuntimeException("OutputStream.write/close: "+ioe.getMessage() );
// 	}
//     }
}
