package com.gillsoft;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.cache.CacheHandler;
import com.gillsoft.concurrent.PoolType;
import com.gillsoft.concurrent.ThreadPoolStore;
import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.matrix.model.Locale;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.ReturnRule;
import com.gillsoft.matrix.model.RouteInfo;
import com.gillsoft.matrix.model.Trip;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RestClient {
	
	public static final String PING = "/get/ping";
	public static final String LOCALES = "/get/locales";
	public static final String CURRENCIES = "/get/currency-list";
	public static final String COUNTRIES = "/get/countries";
	public static final String CITIES = "/get/cities";
	public static final String TRIPS = "/get/trips";
	public static final String RULES = "/get/trip/return-rules";
	public static final String ROUTE = "/get/route-info";
	
	@Autowired
    @Qualifier("RedisMemoryCache")
	private CacheHandler cache;
	
	@Scheduled(initialDelay = 5000, fixedDelay = 300000)
	public ResponseEntity<Response<Object>> ping() {
		return ping(Config.getLogin(), Config.getPassword(), true);
	}
	
	public ResponseEntity<Response<Object>> ping(String login, String password) {
		return ping(login, password, false);
	}
	
	private ResponseEntity<Response<Object>> ping(String login, String password, boolean checkConnection) {
		List<Callable<ResponseEntity<Response<Object>>>> callables = new ArrayList<>();
		for (Connection connection : Config.getConnections()) {
			callables.add(() -> {
				URI uri = UriComponentsBuilder.fromUriString(connection.getUrl() + PING)
						.queryParam("login", login)
						.queryParam("password", password)
						.build().toUri();
				try {
					RequestEntity<Object> request = new RequestEntity<>(HttpMethod.GET, uri);
					ResponseEntity<Response<Object>> response = connection.getTemplate().exchange(request,
							new ParameterizedTypeReference<Response<Object>>() {});
					if (checkConnection) {
						connection.setAvailable(response.getBody().isStatus());
					}
					return response;
				} catch (RestClientException e) {
					if (checkConnection) {
						connection.setAvailable(false);
					}
					return null;
				}
			});
		}
		// возвращаем первый удачный пинг
		List<ResponseEntity<Response<Object>>> pings = ThreadPoolStore.getResult(PoolType.RESOURCE_INFO, callables);
		for (ResponseEntity<Response<Object>> ping : pings) {
			if (ping != null
					&& ping.getBody().isStatus()) {
				return ping;
			}
		}
		// возвращаем первый неудачный пинг
		for (ResponseEntity<Response<Object>> ping : pings) {
			if (ping != null) {
				return ping;
			}
		}
		return null;
	}
	
	public ResponseEntity<Response<Map<String, Locale>>> getLocales(String login, String password, String locale) {
		
		return new RequestSender<Map<String, Locale>>().getDataResponse(LOCALES, HttpMethod.GET, createLoginParams(login, password, locale),
				new ParameterizedTypeReference<Response<Map<String, Locale>>>() {}, PoolType.RESOURCE_INFO, new ConcurrentHashMap<String, Locale>(),
				(result, container) -> container.putAll(result.getBody().getData()));
	}
	
	public ResponseEntity<Response<Map<String, String>>> getCurrencies(String login, String password, String locale) {
		
		return new RequestSender<Map<String, String>>().getDataResponse(CURRENCIES, HttpMethod.GET, createLoginParams(login, password, locale),
				new ParameterizedTypeReference<Response<Map<String, String>>>() {}, PoolType.RESOURCE_INFO, new ConcurrentHashMap<String, String>(),
				(result, container) -> container.putAll(result.getBody().getData()));
	}
	
	public ResponseEntity<Set<Country>> getCountries(String login, String password, String locale) {
		return new RequestSender<Set<Country>>().getResponse(COUNTRIES, HttpMethod.GET, createLoginParams(login, password, locale),
				new ParameterizedTypeReference<Set<Country>>() {}, PoolType.LOCALITY, new CopyOnWriteArraySet<Country>(),
				(result, container) -> container.addAll(result.getBody()));
	}
	
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale, String countryId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("country_id", countryId);
		return new RequestSender<Set<City>>().getDataResponse(CITIES, HttpMethod.GET, params,
				new ParameterizedTypeReference<Response<Set<City>>>() {}, PoolType.LOCALITY, new CopyOnWriteArraySet<City>(),
				(result, container) -> container.addAll(result.getBody().getData()));
	}
	
	public ResponseEntity<Response<List<Trip>>> getTrips(String login, String password, String locale, String routeId,
			String departLocality, String arriveLocality, String departDate, String period, String isTest,
			String withEmptySeats, String currency, String uniqueTrip) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("route_id", routeId);
		params.add("depart_locality", departLocality);
		params.add("arrive_locality", arriveLocality);
		params.add("depart_date", departDate);
		params.add("period", period);
		params.add("is_test", isTest);
		params.add("with_empty_seats", withEmptySeats);
		params.add("currency", currency);
		params.add("unique_trip", uniqueTrip);
		return new RequestSender<List<Trip>>().getDataResponse(TRIPS, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<Trip>>>() {}, PoolType.SEARCH, new CopyOnWriteArrayList<Trip>(),
				(result, container) -> {
					Connection connection = result.getBody().getConnection();
					for (Trip trip : result.getBody().getData()) {
						trip.setIntervalId(trip.getIntervalId() + String.format("%03d", connection.getId()));
						trip.setRouteId(Integer.parseInt(trip.getRouteId() + String.format("%03d", connection.getId())));
						//TODO other params
					}
					container.addAll(result.getBody().getData());
				});
	}
	
	public ResponseEntity<Response<List<ReturnRule>>> getReturnRules(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("interval_id", trimConnectionId("interval_id", intervalId));
		return new RequestSender<List<ReturnRule>>().getDataResponse(RULES, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<ReturnRule>>>() {}, PoolType.SEARCH, new CopyOnWriteArrayList<ReturnRule>(),
				(result, container) -> container.addAll(result.getBody().getData()), Config.getConnection(intervalId));
	}
	
	public ResponseEntity<Response<RouteInfo>> getRoute(String login, String password, String locale, String routeId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("route_id", trimConnectionId("route_id", routeId));
		return new RequestSender<RouteInfo>().getDataResponse(ROUTE, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<RouteInfo>>() {}, PoolType.SEARCH, null, null,
				Config.getConnection(routeId));
	}
	
	private MultiValueMap<String, String> createLoginParams(String login, String password, String locale) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("login", login);
		params.add("password", password);
		params.add("locale", locale);
		return params;
	}
	
	private String trimConnectionId(String name, String id) {
		if (id == null
				|| id.isEmpty()
				|| id.length() < 3) {
			throw new RestClientException("Invalid parameter " + name);
		}
		return id.substring(0, id.length() - 3);
	}
	
}
