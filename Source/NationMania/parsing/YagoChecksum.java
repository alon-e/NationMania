//BASED ON CODE: http://www.mkyong.com/java/how-to-generate-a-file-checksum-value-in-java/
package NationMania.parsing;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This Class Calculates the SHA1 checksum of a given file<br>
 * It is used by YagoUpdate to validate the SQL script files.
 * 
 */
public class YagoChecksum {

 
	/**
	 * Calculates the SHA1 checksum of a given file<br>
	 * @param path a path to the file you want to digest
	 * @return a digest string (SHA1)
	 * @throws IOException Signals that an I/O exception of some sort has occurred. 
	 * @throws NoSuchAlgorithmException This exception is thrown when a particular cryptographic algorithm is requested but is not available in the environment.
	 */
	public static String check(String path) throws IOException, NoSuchAlgorithmException {
 
	 
	    MessageDigest md = MessageDigest.getInstance("SHA1");
	    FileInputStream fis = new FileInputStream(path);
	    byte[] dataBytes = new byte[1024];
	 
	    int nread = 0; 
	 
	    while ((nread = fis.read(dataBytes)) != -1) {
	      md.update(dataBytes, 0, nread);
	    };
	    fis.close();
	    byte[] mdbytes = md.digest();
	 
	    //convert the byte to hex format
	    StringBuffer sb = new StringBuffer("");
	    for (int i = 0; i < mdbytes.length; i++) {
	    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    //use to get file SHA1 digest (for debug)
	    //System.out.println("Digest(in hex format):: "+ path + ": " + sb.toString());
	    return sb.toString();
	 
	}
}

