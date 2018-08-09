package com.gillsoft;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.gillsoft.cache.AbstractUpdateTask;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.Trip;
import com.gillsoft.util.ContextProvider;

public class TripsUpdateTask extends AbstractUpdateTask {

	private static final long serialVersionUID = -7483878909388429051L;
	
	protected Connection connection;
	private MultiValueMap<String, String> params;

	public TripsUpdateTask(Connection connection, MultiValueMap<String, String> params) {
		this.connection = connection;
		this.params = params;
	}

	@Override
	public void run() {
		RestClient client = ContextProvider.getBean(RestClient.class);
		ResponseEntity<Response<List<Trip>>> response = client.getTrips(params, false, connection);
		long timeToLive = 0;
		long updateDelay = 0;
		if (response.getStatusCode() == HttpStatus.ACCEPTED
				|| response.getStatusCode() == HttpStatus.OK) {
			timeToLive = getTimeToLive(response.getBody().getData());
			updateDelay = Config.getCacheTripUpdateDelay();
		} else {
			timeToLive = Config.getCacheErrorTimeToLive();
			updateDelay = Config.getCacheErrorUpdateDelay();
		}
		writeObject(client.getCache(), RestClient.getCacheKey(RestClient.TRIPS_CACHE_KEY, connection.getId(), params), response.getBody(), timeToLive, updateDelay);
	}
	
	// время жизни до момента самого позднего отправления
	private long getTimeToLive(List<Trip> trips) {
		if (Config.getCacheTripTimeToLive() != 0) {
			return Config.getCacheTripTimeToLive();
		}
		long max = 0;
		for (Trip trip : trips) {
			if (trip.getDepartDate().getTime() > max) {
				max = trip.getDepartDate().getTime();
			}
		}
		if (max <= 0) {
			return Config.getCacheErrorTimeToLive();
		}
		return max - System.currentTimeMillis();
	}

}
