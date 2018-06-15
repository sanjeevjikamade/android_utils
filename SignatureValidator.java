package com.snapwork.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Vipul on 9/25/2017.
 */

public class SignatureValidator {

    public static boolean isValidSignature(Context context) {
        return getCertificateSHA1Fingerprint(context).equalsIgnoreCase("5f53ee7487f2154ee6a368580b48f1c8");
    }

    private static String getCertificateSHA1Fingerprint(Context mContext)
    {
        try
        {
            PackageManager pm = mContext.getPackageManager();
            String packageName = mContext.getPackageName();
            int flags = PackageManager.GET_SIGNATURES;
            PackageInfo packageInfo = null;
            try
            {
                packageInfo = pm.getPackageInfo(packageName, flags);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }

            Signature[] signatures = packageInfo.signatures;
            byte[] cert = signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = null;
            try
            {
                cf = CertificateFactory.getInstance("X509");
            }
            catch (CertificateException e)
            {
                e.printStackTrace();
            }

            X509Certificate c = null;
            try {
                c = (X509Certificate) cf.generateCertificate(input);
            }
            catch (CertificateException e)
            {
                e.printStackTrace();
            }

            String hexString = null;
            try
            {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                byte[] publicKey = md.digest(c.getEncoded());
                hexString = byte2HexFormatted(publicKey).replaceAll(":","");
            }
            catch (NoSuchAlgorithmException e1)
            {
                e1.printStackTrace();
            }
            catch (CertificateEncodingException e)
            {
                e.printStackTrace();
            }
            return EncryptionUtils.md5(hexString).replace("\r","").replace("\n","");
        }
        catch(Exception ex)
        {
            //Log.e("getCertificateSHA1Fingerprint : ", ex.toString());
        }

        return null;
    }

    private static String byte2HexFormatted(byte[] arr)
    {
        try
        {
            StringBuilder str = new StringBuilder(arr.length * 2);
            for (int i = 0; i < arr.length; i++)
            {
                String h = Integer.toHexString(arr[i]);
                int l = h.length();
                if (l == 1)
                    h = "0" + h;
                if (l > 2)
                    h = h.substring(l - 2, l);
                str.append(h.toUpperCase());
                if (i < (arr.length - 1))
                    str.append(':');
            }
            return str.toString();
        }
        catch(Exception ex)
        {
            //Log.e("byte2HexFormatted : ", ex.toString());
        }

        return null;
    }
}
