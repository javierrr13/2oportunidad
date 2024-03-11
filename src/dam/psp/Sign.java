package dam.psp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

public class Sign {

	public static void main(String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("pkcs12");
		char[] pwdArray = "4327".toCharArray();
		ks.load(new FileInputStream(System.getProperty("user.home")+ "/Desktop/Keystore.p12"), pwdArray);
		PrivateKey privKey = (PrivateKey) ks.getKey("javi", pwdArray);
		
		try (BufferedInputStream in = new BufferedInputStream(Sign.class.getResourceAsStream("/OpenSSL.pdf"));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home") + "//Desktop//OpenSSL.pdf.sign"))) {
			Signature sign = Signature.getInstance("SHA512withRSA");
			sign.initSign(privKey);

			byte[] buffer = new byte[1024];
			int n; 
			while((n = in.read(buffer))> 0) {
			
				sign.update(buffer, 0, n);
			}
			byte[] signature = sign.sign();
			out.write(signature);
		
		}
		

	}

}