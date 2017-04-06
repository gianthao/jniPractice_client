/**
 * Created by user on 2017/3/28.
 */
package com.example.user.fpclient;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.hardware.fingerprint.FingerprintManager;
import android.util.Log;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/// <summary>
/// Sample code for building a CryptoWrapper
/// </summary>
public class CryptoObjectHelper
{
    // This can be key name you want. Should be unique for the app.
    static final String KEY_NAME = "com.example.user.fpclient.fingerprint_authentication_key";

    // We always use this keystore on Android.
    static final String KEYSTORE_NAME = "AndroidKeyStore";

    // Should be no need to change these values.
    static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    static final String TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" +
            ENCRYPTION_PADDING;
    final KeyStore _keystore;
    String tag="CryptoObjectHelper";
    public CryptoObjectHelper() throws Exception
    {
        _keystore = KeyStore.getInstance(KEYSTORE_NAME);
        _keystore.load(null);
    }

    public FingerprintManager.CryptoObject buildCryptoObject() throws Exception
    {

        Cipher cipher = createCipher(true);
        Log.i(tag,"buildCryptoObject");
        return new FingerprintManager.CryptoObject(cipher);
    }

    Cipher createCipher(boolean retry) throws Exception
    {
        Log.i(tag,"1111 createCipher");
        Key key = GetKey();
        Log.i(tag,"3333 createCipher");
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        try
        {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch(KeyPermanentlyInvalidatedException e)
        {
            Log.i(tag,"KeyPermanentlyInvalidatedException");
            _keystore.deleteEntry(KEY_NAME);
            if(retry)
            {
                createCipher(false);
            } else
            {
                throw new Exception("Could not create the cipher for fingerprint authentication.", e);
            }
        }
        Log.i(tag,"222 createCipher");
        return cipher;
    }

    Key GetKey() throws Exception
    {
        Log.i(tag,"GetKey 111");
        Key secretKey;
        if(!_keystore.isKeyEntry(KEY_NAME))
        {
            Log.i(tag,"GetKey 333");
            CreateKey();
            Log.i(tag, "GetKey 444");
        }
        Log.i(tag,"GetKey 222");
        secretKey = _keystore.getKey(KEY_NAME, null);
        return secretKey;
    }

    void CreateKey() throws Exception
    {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}