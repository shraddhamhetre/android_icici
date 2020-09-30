package com.youtility.intelliwiz20.Utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionDescryptionData 
{
	/*SecretKeySpec sks = null;
	public EncryptionDescryptionData()
	{
		
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("EncryptionDescryptionDt", "AES secret key spec error");
        }
	}*/
	
	/*public String setEncryptedData(String data)
	{
		String encryptData=null;
		byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(data.getBytes());
        } catch (Exception e) {
            Log.e("EncryptionDescryptionDt", "AES encryption error");
        }
        
        encryptData= Base64.encodeToString(encodedBytes, Base64.DEFAULT);
		
		return encryptData;
	}*/
	
	
	/*public String getDecryptedData(String data)
	{
		String decryptData=null;
		byte[] decodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, sks);
            decodedBytes = c.doFinal(Base64.decode(data, 0));
        } catch (Exception e) {
            Log.e("EncryptionDescryptionDt", "AES decryption error");
        }
        decryptData= new String(decodedBytes);
		
		return decryptData;
	}*/
	
	/*public byte[]  getKey()
	{
	   	KeyGenerator keyGen;
	   	byte[] dataKey=null;
			try 
			{
				keyGen = KeyGenerator.getInstance("AES");
				keyGen.init(256); 
				SecretKey secretKey = keyGen.generateKey();
				dataKey=secretKey.getEncoded();
				System.out.println("getKey: "+new String(dataKey.toString()));
			}
			catch (NoSuchAlgorithmException e) 
			{
				e.printStackTrace();
			}
	   	return dataKey;   	
	}*/

   /*public byte[] getIV()
   {
	   	SecureRandom random = new SecureRandom();
	   	byte[] iv = random.generateSeed(16);
	   	System.out.println("IV: "+new String(iv.toString()));
	   	return iv;
   } */
   
   /*public byte[] encrypt(byte[] data)
	{
	      //SecretKeySpec skeySpec = new SecretKeySpec(getKey(), "AES");
	   String key="[B@52769880";
	   String iv="[B@5277bd18";
	   SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
	      Cipher cipher;
	      byte[] encrypted=null;
			try {
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				//cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			  	cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv.getBytes()));
				encrypted = cipher.doFinal(data);
			}catch(Exception e){
				e.printStackTrace();
			}	
	      return encrypted;
	}*/
   
   /*public byte[] decrypt(FileInputStream fis)
	{
	   String key="[B@52769880";
	   String iv="[B@5277bd18";
	   	SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
	   	Cipher cipher;
	    byte[] decryptedData=null;
	    CipherInputStream cis=null;
			try 
			{
				cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
				//cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv.getBytes()));
				cis = new CipherInputStream(fis, cipher);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				byte[] data = new byte[2048];
				
				while ((cis.read(data)) != -1) 
				{
				  buffer.write(data);
				}
				buffer.flush();			
				decryptedData=buffer.toByteArray();
				}catch(Exception e){
					e.printStackTrace();
				}
				finally
				{
					try 
					{
						fis.close();
						cis.close();
					} catch (IOException e) 
					{
						e.printStackTrace();
					}				
				}      
	      return decryptedData;
	   }*/



	/*private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String myKey)
	{

		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
			secretKey = new SecretKeySpec(key, "AES");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	public static String encrypt(String strToEncrypt, String secret)
	{
		try
		{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				//return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
				return android.util.Base64.decode(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),0).toString();
			}
			else
				return android.util.Base64.encode(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),0).toString();
		}
		catch (Exception e)
		{
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret)
	{
		try
		{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				//return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
				return new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,0)));
			}
			else {
				//return cipher.doFinal(android.util.Base64.decode(strToDecrypt, 0)).toString();
				return android.util.Base64.decode(cipher.doFinal(strToDecrypt.getBytes("UTF-8")),0).toString();
			}
		}
		catch (Exception e)
		{
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}*/


	private static final byte[] keyValue =
			new byte[]{'m', 'y', 'c', 'o', 'n', 'a', 'm', 'e', 'm', 'y', 'd', 'o', 'b', 's', 'p', 's'};
	      //new byte[]{'c', 'o', 'd', 'i', 'n', 'g', 'a', 'f', 'f', 'a', 'i', 'r', 's', 'c', 'o', 'm'};


	public static String encrypt(String cleartext)
			throws Exception {
		byte[] rawKey = getRawKey();
		byte[] result = encrypt(rawKey, cleartext.getBytes());
		return toHex(result);
	}

	public static String decrypt(String encrypted)
			throws Exception {

		byte[] enc = toByte(encrypted);
		byte[] result = decrypt(enc);
		return new String(result);
	}

	private static byte[] getRawKey() throws Exception {
		SecretKey key = new SecretKeySpec(keyValue, "AES");
		byte[] raw = key.getEncoded();
		return raw;
	}

	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
		SecretKey skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decrypt(byte[] encrypted)
			throws Exception {
		SecretKey skeySpec = new SecretKeySpec(keyValue, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
					16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private final static String HEX = "0123456789ABCDEF";

	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
	
}
