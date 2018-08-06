package com.gillsoft;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.cache.CacheHandler;
import com.gillsoft.cache.IOCacheException;
import com.gillsoft.cache.RedisMemoryCache;
import com.gillsoft.concurrent.PoolType;
import com.gillsoft.concurrent.ThreadPoolStore;
import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.matrix.model.Locale;
import com.gillsoft.matrix.model.Order;
import com.gillsoft.matrix.model.Response;
import com.gillsoft.matrix.model.ReturnRule;
import com.gillsoft.matrix.model.RouteInfo;
import com.gillsoft.matrix.model.Seat;
import com.gillsoft.matrix.model.Ticket;
import com.gillsoft.matrix.model.Trip;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RestClient {
	
	public static final String COUNTRIES_CACHE_KEY = "matrix.countries.";
	public static final String CITIES_CACHE_KEY = "matrix.cities.";
	public static final String ROUTE_CACHE_KEY = "matrix.route.";
	public static final String TRIPS_CACHE_KEY = "matrix.trips";
	
	public static final String PING = "/get/ping";
	public static final String LOCALES = "/get/locales";
	public static final String CURRENCIES = "/get/currency-list";
	public static final String COUNTRIES = "/get/countries";
	public static final String CITIES = "/get/cities";
	public static final String TRIPS = "/get/trips";
	public static final String RULES = "/get/trip/return-rules";
	public static final String ROUTE = "/get/route-info";
	public static final String SEATS_MAP = "/get/seatsMap";
	public static final String FREE_SEATS = "/get/freeSeats";
	public static final String NEW_ORDER = "/order/new";
	public static final String RESERVE = "/order/reserve";
	public static final String BUY = "/order/buy";
	public static final String INFO = "/order/info";
	public static final String CANCEL = "/order/cancel";
	public static final String ANNULMENT = "/order/annulment";
	public static final String AUTO_RETURN = "/order/auto-return";
	public static final String RETURN = "/order/return";
	public static final String TICKET_AUTO_RETURN = "/ticket/auto-return";
	public static final String TICKET_AUTO_RETURN_PRICE = "/ticket/auto-return-price";
	public static final String TICKET_ANNULMENT = "/ticket/annulment";
	public static final String TICKET_RETURN = "/ticket/return";
			
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
	
	public ResponseEntity<Set<Country>> getCountries(String login, String password, String locale, boolean useCache) {
		return getCountries(login, password, locale, useCache, null);
	}
	
	@SuppressWarnings("unchecked")
	public ResponseEntity<Set<Country>> getCountries(String login, String password, String locale, boolean useCache, Connection connection) {
		return new RequestSender<Set<Country>>().getResponse(COUNTRIES, HttpMethod.GET, createLoginParams(login, password, locale),
				new ParameterizedTypeReference<Set<Country>>() {}, PoolType.LOCALITY, new CopyOnWriteArraySet<Country>(),
				(result, container) -> container.addAll(result.getBody()), connection,
				!useCache ? null : 
					(conn) -> {
						return (Set<Country>) readCacheObject(cache, conn, getCountriesCacheKey(conn.getId()),
								new CountriesUpdateTask(conn, login, password, locale), Config.getRequestTimeout());
					});
	}
	
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale, String countryId, boolean useCache) {
		return getCities(login, password, locale, countryId, useCache, null);
	}
	
	@SuppressWarnings("unchecked")
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale, String countryId, boolean useCache, Connection connection) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("country_id", countryId);
		ResponseEntity<Response<Set<City>>> responseEntity = new RequestSender<Set<City>>().getDataResponse(
				CITIES, HttpMethod.GET, params, new ParameterizedTypeReference<Response<Set<City>>>() {},
				PoolType.LOCALITY, new HashSet<City>(),
				(result, container) -> container.addAll(result.getBody().getData()), connection,
				!useCache ? null :
					(conn) -> {
						return (Response<Set<City>>) readCacheObject(cache, conn, getCitiesCacheKey(conn.getId()),
								new CitiesUpdateTask(conn, login, password, locale), Config.getRequestTimeout());
					});
		if (countryId != null
				&& !countryId.isEmpty()
				&& checkResponse(responseEntity)) {
			for (Iterator<City> iterator = responseEntity.getBody().getData().iterator(); iterator.hasNext();) {
				City city = iterator.next();
				if (!Objects.equals(countryId, String.valueOf(city.getGeoCountryId()))) {
					iterator.remove();
				}
			}
		}
		return responseEntity;
	}
	
	public ResponseEntity<Response<List<Trip>>> getTrips(String login, String password, String locale, String routeId,
			String departLocality, String arriveLocality, String departDate, String period, String isTest,
			String withEmptySeats, String currency, String uniqueTrip, boolean useCache) {
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
		return getTrips(params, useCache, null);
	}
	
	@SuppressWarnings("unchecked")
	public ResponseEntity<Response<List<Trip>>> getTrips(MultiValueMap<String, String> params, boolean useCache, Connection connection) {
		return new RequestSender<List<Trip>>().getDataResponse(TRIPS, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<Trip>>>() {}, PoolType.SEARCH, new CopyOnWriteArrayList<Trip>(),
				(result, container) -> {
					if (!result.getBody().isFromCache()) {
						Connection conn = result.getBody().getConnection();
						for (Trip trip : result.getBody().getData()) {
							trip.setIntervalId(addConnectionId(trip.getIntervalId(), conn));
							trip.setRouteId(Long.parseLong(addConnectionId(trip.getRouteId(), conn)));
						}
					}
					container.addAll(result.getBody().getData());
				}, connection,
				!useCache ? null :
					(conn) -> {
						return (Response<List<Trip>>) readCacheObject(cache, conn, getTripsCacheKey(conn.getId(), params),
								new TripsUpdateTask(conn, params), Config.getSearchRequestTimeout());
					});
	}
	
	public ResponseEntity<Response<List<ReturnRule>>> getReturnRules(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("interval_id", trimConnectionId("interval_id", intervalId));
		return new RequestSender<List<ReturnRule>>().getDataResponse(RULES, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<ReturnRule>>>() {}, PoolType.SEARCH,
				Config.getConnection(intervalId));
	}
	
	@SuppressWarnings("unchecked")
	public ResponseEntity<Response<RouteInfo>> getRoute(String login, String password, String locale, String routeId, boolean useCache) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("route_id", trimConnectionId("route_id", routeId));
		return new RequestSender<RouteInfo>().getDataResponse(ROUTE, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<RouteInfo>>() {}, PoolType.SEARCH,
				Config.getConnection(routeId),
				!useCache ? null :
					(conn) -> {
						return (Response<RouteInfo>) readCacheObject(cache, conn, getRouteCacheKey(conn.getId(), routeId),
								new RouteUpdateTask(conn, login, password, locale, routeId), Config.getSearchRequestTimeout());
					});
	}
	
	public ResponseEntity<Response<List<List<Seat>>>> getSeatsMap(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("interval_id", trimConnectionId("interval_id", intervalId));
		return new RequestSender<List<List<Seat>>>().getDataResponse(SEATS_MAP, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<List<Seat>>>>() {}, PoolType.SEARCH,
				Config.getConnection(intervalId));
	}
	
	public ResponseEntity<Response<Map<String, String>>> getFreeSeats(String login, String password, String locale, String intervalId) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("interval_id", trimConnectionId("interval_id", intervalId));
		return new RequestSender<Map<String, String>>().getDataResponse(FREE_SEATS, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<Map<String, String>>>() {}, PoolType.SEARCH,
				Config.getConnection(intervalId));
	}
	
	public ResponseEntity<Response<Order>> create(HttpServletRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		Set<Integer> intervalIds = new HashSet<>();
		Enumeration<String> requestParams = request.getParameterNames();
		while (requestParams.hasMoreElements()) {
			String name = requestParams.nextElement();
			if (name.contains("interval_id")) {
				String intervalId = request.getParameter(name);
				params.add(name, trimConnectionId(name, intervalId));
				intervalIds.add(Config.getConnection(intervalId).getId());
			} else {
				params.add(name, request.getParameter(name));
			}
		}
		if (intervalIds.size() > 1) {
			throw new RestClientException("It's forbidden to create an order using intervals with different three ending numbers.");
		}
		Connection connection = Config.getConnection(intervalIds.iterator().next());
		ResponseEntity<Response<Order>> response = new RequestSender<Order>().getDataResponse(NEW_ORDER, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Order>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			updateOrder(response.getBody().getData(), connection);
		}
		return response;
	}
	
	public ResponseEntity<Response<Order>> reserve(String login, String password, String locale, String orderId) {
		return orderOperation(RESERVE, login, password, locale, orderId);
	}
	
	public ResponseEntity<Response<Order>> buy(String login, String password, String locale, String orderId) {
		return orderOperation(BUY, login, password, locale, orderId);
	}
	
	public ResponseEntity<Response<Order>> cancel(String login, String password, String locale, String orderId) {
		return orderOperation(CANCEL, login, password, locale, orderId);
	}
	
	public ResponseEntity<Response<Order>> info(String login, String password, String locale, String orderId, String withFees) {
		return orderOperation(INFO, login, password, locale, orderId, withFees, null);
	}
	
	public ResponseEntity<Response<Order>> annulment(String login, String password, String locale, String orderId, String description) {
		return orderOperation(ANNULMENT, login, password, locale, orderId, null, description);
	}
	
	public ResponseEntity<Response<Order>> autoReturn(String login, String password, String locale, String orderId, String description) {
		return orderOperation(AUTO_RETURN, login, password, locale, orderId, null, description);
	}
	
	public ResponseEntity<Response<Order>> manualReturn(HttpServletRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		Enumeration<String> paramNames = request.getParameterNames();
		String orderId = null;
		while (paramNames.hasMoreElements()) {
			String name = paramNames.nextElement();
			if (Objects.equals("order_id", name)) {
				orderId = request.getParameter(name);
				params.add(name, trimConnectionId(name, orderId));
			} else if (name.contains("amount")) {
				params.add(trimConnectionId(name, name, 4) + "]", request.getParameter(name));
			} else {
				params.add(name, request.getParameter(name));
			}
		}
		Connection connection = Config.getConnection(orderId);
		ResponseEntity<Response<Order>> response = new RequestSender<Order>().getDataResponse(RETURN, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Order>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			updateOrder(response.getBody().getData(), connection);
		}
		return response;
	}
	
	private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale, String orderId) {
		return orderOperation(method, login, password, locale, orderId, null, null, null);
	}
	
	private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale, String orderId, String withFees, String description) {
		return orderOperation(method, login, password, locale, orderId, withFees, description, null);
	}
	
	private ResponseEntity<Response<Order>> orderOperation(String method, String login,
			String password, String locale, String orderId, String withFees, String description, String amount) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("order_id", trimConnectionId("order_id", orderId));
		params.add("with_fees", withFees);
		params.add("description", description);
		params.add("amount", amount);
		Connection connection = Config.getConnection(orderId);
		ResponseEntity<Response<Order>> response = new RequestSender<Order>().getDataResponse(method, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Order>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			updateOrder(response.getBody().getData(), connection);
		}
		return response;
	}
	
	public ResponseEntity<Response<Ticket>> ticketAutoReturn(String login, String password, String locale, String ticketId, String description) {
		return ticketOperation(TICKET_AUTO_RETURN, login, password, locale, ticketId, description, null);
	}
	
	public ResponseEntity<Response<Ticket>> ticketAutoReturnPrice(String login, String password, String locale, String ticketId, String description) {
		return ticketOperation(TICKET_AUTO_RETURN_PRICE, login, password, locale, ticketId, description, null);
	}
	
	public ResponseEntity<Response<Ticket>> ticketAnnulment(String login, String password, String locale, String ticketId, String description) {
		return ticketOperation(TICKET_ANNULMENT, login, password, locale, ticketId, description, null);
	}
	
	public ResponseEntity<Response<Ticket>> ticketManualReturn(String login, String password, String locale, String ticketId, String description, String amount) {
		return ticketOperation(TICKET_RETURN, login, password, locale, ticketId, description, amount);
	}
	
	private ResponseEntity<Response<Ticket>> ticketOperation(String method, String login, String password,
			String locale, String ticketId, String description, String amount) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("ticket_id", trimConnectionId("ticket_id", ticketId));
		params.add("description", description);
		params.add("amount", amount);
		Connection connection = Config.getConnection(ticketId);
		ResponseEntity<Response<Ticket>> response = new RequestSender<Ticket>().getDataResponse(method, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Ticket>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			response.getBody().getData().setHash(addConnectionId(response.getBody().getData().getHash(), connection));
		}
		return response;
	}
	
	public <T> boolean checkResponse(ResponseEntity<Response<T>> response) {
		return (response.getStatusCode() == HttpStatus.ACCEPTED
				|| response.getStatusCode() == HttpStatus.OK)
				&& response.getBody().getData() != null;
	}
	
	private void updateOrder(Order order, Connection connection) {
		if (order.getHash() != null) {
			order.setHash(addConnectionId(order.getHash(), connection));
		}
		if (order.getTickets() != null) {
			Map<String, List<Ticket>> tickets = new HashMap<>(order.getTickets().size());
			for (Entry<String, List<Ticket>> ticketList : order.getTickets().entrySet()) {
				tickets.put(addConnectionId(ticketList.getKey(), connection), ticketList.getValue());
				for (Ticket ticket : ticketList.getValue()) {
					ticket.setHash(addConnectionId(ticket.getHash(), connection));
				}
			}
			order.setTickets(tickets);
		}
	}
	
	private MultiValueMap<String, String> createLoginParams(String login, String password, String locale) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("login", login);
		params.add("password", password);
		params.add("locale", locale);
		return params;
	}
	
	private String addConnectionId(Object id, Connection connection) {
		return id + String.format("%03d", connection.getId());
	}
	
	private String trimConnectionId(String name, String id) {
		return trimConnectionId(name, id, 3);
	}
	
	private String trimConnectionId(String name, String id, int charsCount) {
		if (id == null
				|| id.isEmpty()
				|| id.length() < charsCount) {
			throw new RestClientException("Invalid parameter " + name);
		}
		return id.substring(0, id.length() - charsCount);
	}
	
	public CacheHandler getCache() {
		return cache;
	}

	public static String getCountriesCacheKey(int connectionId) {
		return COUNTRIES_CACHE_KEY + connectionId;
	}
	
	public static String getCitiesCacheKey(int connectionId) {
		return CITIES_CACHE_KEY + connectionId;
	}
	
	public static String getTripsCacheKey(int connectionId, MultiValueMap<String, String> params) {
		List<String> values = new ArrayList<>();
		for (List<String> list : params.values()) {
			values.addAll(list.stream().filter(param -> param != null).collect(Collectors.toList()));
		}
		Collections.sort(values);
		values.add(0, String.valueOf(connectionId));
		values.add(0, TRIPS_CACHE_KEY);
		return String.join(".", values);
	}
	
	public static String getRouteCacheKey(int connectionId, String routeId) {
		return String.join(".", ROUTE_CACHE_KEY, String.valueOf(connectionId), routeId);
	}
	
	public static Object readCacheObject(CacheHandler cache, Connection connection, String cacheKey, Runnable updateTask, int requestTimeout) {
		boolean cacheError = true;
		int tryCount = 0;
		Map<String, Object> cacheParams = new HashMap<>();
		cacheParams.put(RedisMemoryCache.OBJECT_NAME, cacheKey);
		cacheParams.put(RedisMemoryCache.UPDATE_TASK, updateTask);
		do {
			try {
				return cache.read(cacheParams);
			} catch (IOCacheException e) {
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch (InterruptedException ie) {
				}
			}
		} while (cacheError && tryCount++ < requestTimeout / 1000);
		return null;
	}
	
}