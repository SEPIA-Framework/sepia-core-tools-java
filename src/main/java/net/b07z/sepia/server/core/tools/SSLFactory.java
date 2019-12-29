package net.b07z.sepia.server.core.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

/**
 * Class that helps building SSL contexts, socket-factories, HTTP clients...<br>
 * Some info: https://hc.apache.org/httpcomponents-client-ga/tutorial/html/connmgmt.html
 * 
 * @author Florian Quirin
 *
 */
public class SSLFactory {
	
	/**
	 * Load a keystore file.
	 * @param keyStoreType - a type like JKS, PKCS12, etc. or null for default (as specified by the keystore.type security property, or the string "jks").
	 * @param keystorePath - path to keystore file
	 * @param keystorePassword - password of keystore 
	 * @return
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException
	 */
    public static KeyStore loadKeyStore(String keyStoreType, String keystorePath, String keystorePassword) 
    		throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        final InputStream stream = Files.newInputStream(Paths.get(keystorePath));
        if(stream == null) {
            throw new RuntimeException("Could not load keystore");
        }
        try(InputStream is = stream) {
        	if (keyStoreType == null) keyStoreType = KeyStore.getDefaultType();
            KeyStore loadedKeystore = KeyStore.getInstance(keyStoreType);
            loadedKeystore.load(is, keystorePassword.toCharArray());
            return loadedKeystore;
        }
    }
	
	/**
	 * Get SSLContext that trusts own CA and self-signed certificates.
	 * @param keystorePath - path to keystore file
	 * @param keystorePassword - password of keystore
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static SSLContext buildSslContextForSelfSignedCert(String keystorePath, String keystorePassword) 
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {
		 SSLContext sslContext = SSLContexts.custom()
				 .loadTrustMaterial(new File(keystorePath), keystorePassword.toCharArray(), new TrustSelfSignedStrategy())
	             .build();
		 return sslContext;
	}

	/**
	 * Get Apache HTTP client from given {@link SSLContext} and optionally disable hostname verification.
	 * @param sslContext - custom {@link SSLContext}
	 * @param disableHostnameVerification - turn hostname verification off or use default RFC 2818 compliant
	 * @return {@link CloseableHttpClient}
	 */
	public static HttpClient getHttpClientWithCustomSslContext(SSLContext sslContext, boolean disableHostnameVerification){
		SSLConnectionSocketFactory socketFactory;
		if (disableHostnameVerification){
			socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		}else{
			socketFactory = new SSLConnectionSocketFactory(sslContext);
		}
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
		return httpClient;
	}
}
