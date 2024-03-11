package dam.psp;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;


public class Clave {

	public static void main(String[] args) {
		try {
			KeyStore ks = KeyStore.getInstance("pkcs12");
			char[] pwdArray = "4327".toCharArray();
			ks.load(new FileInputStream(System.getProperty("user.home")+ "/Desktop/Keystore.p12"), pwdArray);
			
			System.out.println("privateKey");
			PrivateKey privKey = (PrivateKey) ks.getKey("javi", pwdArray);
			System.out.println(privKey.getAlgorithm());
			System.out.println(privKey.getFormat());
			System.out.println(Arrays.toString(privKey.getEncoded()));

			
			System.out.println("Cert");
			Certificate miCert = ks.getCertificate("javi");
			System.out.println(miCert.getType());
			System.out.println(Arrays.toString(privKey.getEncoded()));
			System.out.println("PublicKey");

			PublicKey pubKey = miCert.getPublicKey();
			System.out.println(pubKey.getAlgorithm());
			System.out.println(pubKey.getFormat());
			System.out.println(Arrays.toString(pubKey.getEncoded()));
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}