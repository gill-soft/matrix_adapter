package com.gillsoft;

import java.util.Set;

import org.springframework.http.ResponseEntity;

import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Response;

public class CitiesUpdateTask extends CountriesUpdateTask {

	private static final long serialVersionUID = 1792318881958028832L;

	public CitiesUpdateTask(Connection connection, String login, String password, String locale) {
		super(connection, login, password, locale);
	}
	
	@Override
	protected String getCacheKey() {
		return RestClient.getCitiesCacheKey(connection.getId());
	}
	
	@Override
	protected Object getCacheObject(RestClient client) {
		try {
			ResponseEntity<Response<Set<City>>> response = client.getCities(login, password, locale, null, false);
			if (client.checkResponse(response)) {
				return response.getBody();
			}
		} catch (Exception e) {
		}
		return null;
	}

}
