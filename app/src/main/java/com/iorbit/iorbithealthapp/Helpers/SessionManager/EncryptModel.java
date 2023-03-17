package com.iorbit.iorbithealthapp.Helpers.SessionManager;

import android.content.Context;
import android.util.Base64;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptModel {
    public static EncryptModel encryptModel;
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";


    private final Cipher writer;
    private final Cipher reader;
    private final Cipher keyWriter;

    /**
     * This will initialize an instance of the SecurePreferences class
     * @param context your current context.
     * @param secureKey the key used for encryption, finding a good key scheme is hard.
     * Hardcoding your key in the application is bad, but better than plaintext preferences. Having the user enter the key upon application launch is a safe(r) alternative, but annoying to the user.
     *
     */
    private EncryptModel(Context context, String secureKey) throws SecurePreferences.SecurePreferencesException {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);

            initCiphers(secureKey);
        }
        catch (GeneralSecurityException e) {
            throw new SecurePreferences.SecurePreferencesException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferences.SecurePreferencesException(e);
        }
    }



    public static synchronized <T extends Object> T getEncryptedModel(Context context, String secureKey, Object object,ArrayList<String> excludeFunctionList){
        if(encryptModel==null)
            encryptModel = new EncryptModel(context,secureKey);
        Object u = null;
        try {
            u = object.getClass().getConstructor().newInstance();
            Method methods[] = object.getClass().getDeclaredMethods();
            for(int i = 0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("get")) {
                    if(!excludeFunctionList.contains(methods[i].getName()))
                        object.getClass().getDeclaredMethod("set"+methods[i].getName().substring(3),methods[i].invoke(u).getClass()).invoke(u,encryptModel.encrypt((String)methods[i].invoke(object)));
                    else
                        object.getClass().getDeclaredMethod("set" + methods[i].getName().substring(3), methods[i].invoke(u).getClass()).invoke(u, methods[i].invoke(object));
                }
            }
        } catch (Exception e){
          //  Utils.showException(e);
        }
        return (T)object.getClass().cast(u);
    }

    public static synchronized <T extends Object>T getDecryptedModel(Context context, String secureKey, Object object, ArrayList<String> excludeFunctionList){
        if(encryptModel==null)
            encryptModel = new EncryptModel(context,secureKey);
        Object u = null;
        try {
            u = object.getClass().getConstructor().newInstance();
            Method methods[] = object.getClass().getDeclaredMethods();

            for(int i = 0; i < methods.length; i++) {
                if (methods[i].getName().startsWith("get")) {
                    if(!excludeFunctionList.contains(methods[i].getName()))
                        object.getClass().getDeclaredMethod("set" + methods[i].getName().substring(3), methods[i].invoke(u).getClass()).invoke(u, encryptModel.decrypt((String) methods[i].invoke(object)));
                    else
                        object.getClass().getDeclaredMethod("set"+methods[i].getName().substring(3),methods[i].invoke(u).getClass()).invoke(u,methods[i].invoke(object));
                }
            }
        } catch (Exception e){
            //Utils.showException(e);
        }
        return (T)object.getClass().cast(u);
    }

    public static synchronized String getDecryptedString(Context context, String secureKey, String string){
        if(encryptModel==null)
            encryptModel = new EncryptModel(context,secureKey);
        return encryptModel.decrypt(string);
    }

    public static synchronized String getEncryptedString(Context context, String secureKey, String string){
        if(encryptModel==null)
            encryptModel = new EncryptModel(context,secureKey);
        return encryptModel.encrypt(string);
    }



    protected void initCiphers(String secureKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv();
        SecretKeySpec secretKey = getSecretKey(secureKey);

        writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        keyWriter.init(Cipher.ENCRYPT_MODE, secretKey);
    }

    protected IvParameterSpec getIv() {
        byte[] iv = new byte[writer.getBlockSize()];
        System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0, iv, 0, writer.getBlockSize());
        return new IvParameterSpec(iv);
    }

    protected SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes, TRANSFORMATION);
    }

    protected byte[] createKeyBytes(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
        return keyBytes;
    }

    protected String encrypt(String value) throws SecurePreferences.SecurePreferencesException {
        byte[] secureValue;
        try {
            secureValue = convert(writer, value.getBytes(CHARSET));
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferences.SecurePreferencesException(e);
        }
        String secureValueEncoded = Base64.encodeToString(secureValue, Base64.NO_WRAP);
        return secureValueEncoded;
    }

    protected String decrypt(String securedEncodedValue) {
        byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
        byte[] value = convert(reader, securedValue);
        try {
            return new String(value, CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferences.SecurePreferencesException(e);
        }
    }

    private static byte[] convert(Cipher cipher, byte[] bs) throws SecurePreferences.SecurePreferencesException {
        try {
            return cipher.doFinal(bs);
        }
        catch (Exception e) {
            throw new SecurePreferences.SecurePreferencesException(e);
        }
    }
}
