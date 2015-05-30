/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class handles all HTTPS (secure) calls
 */

package org.irdresearch.tbr3mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.irdresearch.tbr3mobile.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
@Deprecated
public class HttpsRequest
{
	private Context				context;
	private SSLContext			sslContext;
	private SchemeRegistry		schemeRegistry;
	private HttpsURLConnection	httpsClient;
	private KeyStore			trustStore;
	private KeyStore			localTrustStore;

	private static final int	HTTP_PORT	= 80;
	private static final int	HTTPS_PORT	= 443;

	public HttpsRequest (Activity activity)
	{
		try
		{
			context = activity;
			URL url = new URL ("https://myserver");
			initialize (url);
			// TODO: Complete this class in order to use HttpsURLConnection instead of HttpClient
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * This method registers local keystore (in res/raw) in the keystore schema registry and initiate client connection
	 * using thread-safe connection manager
	 * 
	 * @param url
	 */
	public void initialize (URL url)
	{
		try
		{
			// The Trust Store will be using Bouncy Castle jar, therefore BKS Format should be used
			trustStore = KeyStore.getInstance ("BKS");
			localTrustStore = KeyStore.getInstance ("BKS");
			SSLSocketFactory sslSocketFactory = new SSLSocketFactory (trustStore);
			// Read the raw Keystore file in resources
			InputStream in = context.getResources ().openRawResource (R.raw.ihskeystore);
			// Initialize local Trust Store object
			localTrustStore.load (in, context.getResources ().getString (R.string.trust_store_password).toCharArray ());
			schemeRegistry = new SchemeRegistry ();
			// Register schemes for HTTP and HTTPS calls
			schemeRegistry.register (new Scheme ("http", PlainSocketFactory.getSocketFactory (), HTTP_PORT));
			schemeRegistry.register (new Scheme ("https", sslSocketFactory, HTTPS_PORT));
			// Initiate Trust Manager Factory and Key Manager Factory to initialize SSL Context
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance (TrustManagerFactory
					.getDefaultAlgorithm ());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance (KeyManagerFactory
					.getDefaultAlgorithm ());
			trustManagerFactory.init (trustStore);
			keyManagerFactory.init (localTrustStore, context.getResources ().getString (R.string.trust_store_password)
					.toCharArray ());
			// Initialize SSL Context object
			sslContext = SSLContext.getInstance ("TLS");
			sslContext.init (keyManagerFactory.getKeyManagers (), trustManagerFactory.getTrustManagers (), null);

			httpsClient = (HttpsURLConnection) url.openConnection ();
			httpsClient.setSSLSocketFactory (sslContext.getSocketFactory ());

		}
		catch (KeyManagementException e)
		{
			e.printStackTrace ();
		}
		catch (UnrecoverableKeyException e)
		{
			e.printStackTrace ();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace ();
		}
		catch (KeyStoreException e)
		{
			e.printStackTrace ();
		}
		catch (NotFoundException e)
		{
			e.printStackTrace ();
		}
		catch (CertificateException e)
		{
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			e.printStackTrace ();
		}
	}

	/**
	 * Returns all trusted certificates (trust anchors) in the system
	 * 
	 * @return
	 */
	public X509Certificate[] getSystemTrustedCertificates ()
	{
		TrustManagerFactory trustManagerFactory;
		try
		{
			trustManagerFactory = TrustManagerFactory.getInstance (TrustManagerFactory.getDefaultAlgorithm ());
			trustManagerFactory.init ((KeyStore) null);
			X509TrustManager xTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers ()[0];
			return xTrustManager.getAcceptedIssuers ();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace ();
			return null;
		}
		catch (KeyStoreException e)
		{
			e.printStackTrace ();
			return null;
		}
	}

	/**
	 * Prints the contents of the response from server
	 * 
	 * @param connection
	 */
	public void printServerCertificates (HttpsURLConnection con)
	{
		// Prints all certificates on the connection server
		if (con != null)
		{
			try
			{
				int responseCode = con.getResponseCode ();
				String cipherSuite = con.getCipherSuite ();
				System.out.println (responseCode + " " + cipherSuite);

				Certificate[] certs = con.getServerCertificates ();
				for (Certificate cert : certs)
				{
					System.out.println ("Type : " + cert.getType ());
					System.out.println ("Hash Code : " + cert.hashCode ());
					System.out.println ("Public Key Algorithm : " + cert.getPublicKey ().getAlgorithm ());
					System.out.println ("Public Key Format : " + cert.getPublicKey ().getFormat ());
					System.out.println ("\n");
				}
			}
			catch (SSLPeerUnverifiedException e)
			{
				e.printStackTrace ();
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
		}

	}
}
