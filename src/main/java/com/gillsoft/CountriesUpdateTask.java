package com.gillsoft;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.RedisMemoryCache;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.util.ContextProvider;

public class CountriesUpdateTask implements Runnable, Serializable {
	
	private static final long serialVersionUID = 8873082805767541195L;

	protected Connection connection;

	protected String login;
	protected String password;
	protected String locale;

	public CountriesUpdateTask(Connection connection, String login, String password, String locale) {
		this.connection = connection;
		this.login = login;
		this.password = password;
		this.locale = locale;
	}

	@Override
	public void run() {
		Map<String, Object> params = new HashMap<>();
		params.put(RedisMemoryCache.OBJECT_NAME, getCacheKey());
		params.put(RedisMemoryCache.IGNORE_AGE, true);
		params.put(RedisMemoryCache.UPDATE_DELAY, Config.getCacheStationsUpdateDelay());
		
		RestClient client = ContextProvider.getBean(RestClient.class);
		try {
			Object cache = getCacheObject(client);
			if (cache == null) {
				cache = client.getCache().read(params);
			}
			params.put(RedisMemoryCache.UPDATE_TASK, this);
			client.getCache().write(cache, params);
		} catch (IOCacheException e) {
			e.printStackTrace();
		}
	}
	
	protected String getCacheKey() {
		return RestClient.getCountriesCacheKey(connection.getId());
	}
	
	protected Object getCacheObject(RestClient client) {
		try {
			ResponseEntity<Set<Country>> response = client.getCountries(login, password, locale, false);
			if (response.getStatusCode() == HttpStatus.ACCEPTED
					|| response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			}
		} catch (Exception e) {
		}
		return null;
	}

}
