package com.gillsoft;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestClientException;

public abstract class RestTemplateUtil {
	
	public static ClientHttpRequestFactory createPoolingFactory(String url, int maxConnections, int requestTimeout) {
		return createPoolingFactory(url, maxConnections, requestTimeout, false, false);
 	}
 	
 	public static ClientHttpRequestFactory createPoolingFactory(String url, int maxConnections, int requestTimeout,
			boolean disableAuthCaching, boolean disableCookieManagement) {
 		return createPoolingFactory(url, maxConnections, -1, requestTimeout, -1, disableAuthCaching, disableCookieManagement);
 	}
 	
	public static ClientHttpRequestFactory createPoolingFactory(String url, int maxConnections, int connectTimeout,
			int requestTimeout, int readTimeout, boolean disableAuthCaching, boolean disableCookieManagement) {
		
		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
		}
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();

		// создаем пул соединений
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxPerRoute(new HttpRoute(new HttpHost(url)), maxConnections);
		
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
		if (disableAuthCaching) {
			httpClientBuilder.disableAuthCaching();
		}
		if (disableCookieManagement) {
			httpClientBuilder.disableCookieManagement();
		}
		HttpClient httpClient = httpClientBuilder.build();
		
		// настраиваем таймауты
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		if (connectTimeout >= 0) {
			factory.setConnectTimeout(connectTimeout);
		}
		if (readTimeout >= 0) {
			factory.setReadTimeout(readTimeout);
		}
		if (requestTimeout >= 0) {
			factory.setConnectionRequestTimeout(requestTimeout);
		}
		factory.setHttpClient(httpClient);
		return factory;
 	}
	
	public static List<HttpMessageConverter<?>> getMarshallingMessageConverters(Class<?>... classesToBeBound) {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(classesToBeBound);
		return Collections.singletonList(new MarshallingHttpMessageConverter(marshaller, marshaller));
	}
	
	public static RestClientException createUnavailableMethod() {
		return new RestClientException("Method is unavailable");
	}
	
	public static RestClientException createRestException(String message) {
		return new RestClientException(message);
	}
	
	public static RestClientException createRestException(String message, Throwable exception) {
		return new RestClientException(message, exception);
	}

}
