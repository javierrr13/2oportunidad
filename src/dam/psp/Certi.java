package dam.psp;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Certi implements Runnable {	
	Socket client;
	DataOutputStream out;

	public Certi(Socket client) {
		this.client = client;
		try {
			this.out = new DataOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			DataInputStream in = new DataInputStream(client.getInputStream());
			String command = in.readUTF();
			switch (command) {
			case "hash":
				getHash(in);
				break;
			case "cert":
				saveCert(in);
				break;
			case "cifrar":
				encode(in);
				break;
			default:
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getHash(DataInputStream in) {
		String algorithm;
		try {
			algorithm = in.readUTF();
			byte[] bytes = in.readAllBytes();
			MessageDigest md = MessageDigest.getInstance(algorithm);
			if (bytes.length > 0) {
				sendMessage("OK:" + Base64.getEncoder().encodeToString(md.digest(bytes)));
			} else
				sendMessage("ERROR: DATA TO ENCODE EXPECTED");
		} catch (NoSuchAlgorithmException e) {
			sendMessage("ERROR: NOT VALID ALGORITHM");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveCert(DataInputStream in) {
		try {
			String alias = in.readUTF();
			String base64Cert = in.readUTF();
			CertificateFactory f = CertificateFactory.getInstance("X.509");
			byte[] encodedCerrt = Base64.getDecoder().decode(base64Cert);
			Certificate cert = f.generateCertificate(new ByteArrayInputStream(encodedCerrt));
			CertC.ks.setCertificateEntry(alias, cert);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(base64Cert.getBytes());
			sendMessage("OK:" + Base64.getEncoder().encode(md.digest()));
		} catch (CertificateException e) {
			sendMessage("ERROR: CERTIFICATE DOESN'T EXISTS");
			e.printStackTrace();
		} catch (KeyStoreException e) {
			sendMessage("ERROR: KEYSTORE PROBLEM");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			sendMessage("ERROR: NOT VALID ALGORITHM");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

	private void encode(DataInputStream in) {
		try {
			String alias = in.readUTF();
			Certificate cert = CertC.ks.getCertificate(alias);
			if (cert == null)
				sendMessage("ERROR: THERE'S NO CERTIFICATE CALLED " + alias);
			else {
				Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				c.init(Cipher.ENCRYPT_MODE, cert.getPublicKey());

				int n;
				byte[] buffer = new byte[256];
				while ((n = in.read(buffer)) != -1) {
					byte[] coded = c.doFinal(buffer, 0, n);
					sendMessage("OK:" + Base64.getEncoder().encodeToString(coded));
				}
				sendMessage("FIN:CIFRADO");
			}
		} catch (KeyStoreException e) {
			sendMessage("ERROR: KEYSTORE PROBLEM");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			sendMessage("ERROR: NOT VALID ALGORITHM");
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			sendMessage("ERROR: NOT VALID ALGORITHM");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			sendMessage("ERROR: NOT VALID KEY");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(String msg) {
		try {
			out.writeUTF(msg);
		} catch (IOException e) {
			System.err.println("Error communicating with client.");
			e.printStackTrace();
		}
	}
}