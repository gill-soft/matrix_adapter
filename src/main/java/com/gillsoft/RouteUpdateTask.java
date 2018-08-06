package com.gillsoft;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gillsoft.cache.AbstractUpdateTask;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.RouteInfo;
import com.gillsoft.util.ContextProvider;

public class RouteUpdateTask extends AbstractUpdateTask {

	private static final long serialVersionUID = -6859368060540072639L;
	
	private Connection connection;
	
	private String login;
	private String password;
	private String locale;
	private String routeId;
	
	public RouteUpdateTask(Connection connection, String login, String password, String locale, String routeId) {
		this.connection = connection;
		this.login = login;
		this.password = password;
		this.locale = locale;
		this.routeId = routeId;
	}

	@Override
	public void run() {
		RestClient client = ContextProvider.getBean(RestClient.class);
		ResponseEntity<Response<RouteInfo>> response = client.getRoute(login, password, locale, routeId, false);
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
		writeObject(client.getCache(), RestClient.getRouteCacheKey(connection.getId(), routeId), response.getBody(), timeToLive, updateDelay);
	}
	
	// время жизни до конца существования маршрута
	private long getTimeToLive(RouteInfo routeInfo) {
		if (Config.getCacheTripTimeToLive() != 0) {
			return Config.getCacheTripTimeToLive();
		}
		if (routeInfo.getRoute().getEnded() == null
				|| routeInfo.getRoute().getEnded().getTime() < System.currentTimeMillis()) {
			return Config.getCacheErrorTimeToLive();
		}
		return routeInfo.getRoute().getEnded().getTime() - System.currentTimeMillis();
	}

}
