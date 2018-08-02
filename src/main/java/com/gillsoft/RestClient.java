package com.gillsoft;

import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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
	
	public ResponseEntity<Response<List<Trip>>> getTrips(HttpServletRequest request) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		Enumeration<String> requestParams = request.getParameterNames();
		while (requestParams.hasMoreElements()) {
			String name = requestParams.nextElement();
			params.add(name, request.getParameter(name));
		}
		return new RequestSender<List<Trip>>().getDataResponse(TRIPS, HttpMethod.POST, params,
				new ParameterizedTypeReference<Response<List<Trip>>>() {}, PoolType.SEARCH, new CopyOnWriteArrayList<Trip>(),
				(result, container) -> {
					Connection connection = result.getBody().getConnection();
					for (Trip trip : result.getBody().getData()) {
						trip.setIntervalId(addConnectionId(trip.getIntervalId(), connection));
						trip.setRouteId(Integer.parseInt(addConnectionId(trip.getRouteId(), connection)));
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
				new ParameterizedTypeReference<Response<RouteInfo>>() {}, PoolType.SEARCH,
				Config.getConnection(routeId));
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
		Enumeration<String> requestParams = request.getParameterNames();
		String orderId = null;
		while (requestParams.hasMoreElements()) {
			String name = requestParams.nextElement();
			if (Objects.equals("order_id", name)) {
				orderId = request.getParameter(name);
				params.add(name, trimConnectionId(name, orderId));
			} else if (name.contains("amount")) {
				params.add(name, trimConnectionId(name, request.getParameter(name), 4) + "]");
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
	
	public ResponseEntity<Response<Order>> ticketAutoReturn(String login, String password, String locale, String ticketId, String description) {
		return orderOperation(TICKET_AUTO_RETURN, login, password, locale, ticketId, null, description, null);
	}
	
	public ResponseEntity<Response<Order>> ticketAutoReturnPrice(String login, String password, String locale, String ticketId, String description) {
		return orderOperation(TICKET_AUTO_RETURN_PRICE, login, password, locale, ticketId, null, description, null);
	}
	
	public ResponseEntity<Response<Order>> ticketAnnulment(String login, String password, String locale, String ticketId, String description) {
		return orderOperation(TICKET_ANNULMENT, login, password, locale, ticketId, null, description, null);
	}
	
	public ResponseEntity<Response<Order>> ticketManualReturn(String login, String password, String locale, String ticketId, String description, String amount) {
		return orderOperation(TICKET_RETURN, login, password, locale, ticketId, null, description, amount);
	}
	
	private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale, String orderId) {
		return orderOperation(method, login, password, locale, orderId, null, null);
	}
	
	private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale,
			String orderId, String withFees, String description) {
		return orderOperation(Config.getConnection(orderId), method, login, password, locale,
				trimConnectionId("order_id", orderId), null, withFees, description, null);
	}
	
	private ResponseEntity<Response<Order>> orderOperation(String method, String login, String password, String locale,
			String ticketId, String withFees, String description, String amount) {
		return orderOperation(Config.getConnection(ticketId), method, login, password, locale, null,
				trimConnectionId("ticket_id", ticketId), withFees, description, null);
	}
	
	private ResponseEntity<Response<Order>> orderOperation(Connection connection, String method, String login,
			String password, String locale, String orderId, String ticketId, String withFees, String description,
			String amount) {
		MultiValueMap<String, String> params = createLoginParams(login, password, locale);
		params.add("order_id", orderId);
		params.add("with_fees", withFees);
		params.add("description", description);
		params.add("ticket_id", ticketId);
		params.add("amount", amount);
		ResponseEntity<Response<Order>> response = new RequestSender<Order>().getDataResponse(method, HttpMethod.POST,
				params, new ParameterizedTypeReference<Response<Order>>() {}, PoolType.ORDER, connection);
		if (checkResponse(response)) {
			updateOrder(response.getBody().getData(), connection);
		}
		return response;
	}
	
	private <T> boolean checkResponse(ResponseEntity<Response<T>> response) {
		return (response.getStatusCode() == HttpStatus.ACCEPTED
				|| response.getStatusCode() == HttpStatus.OK)
				&& response.getBody().getData() != null;
	}
	
	private void updateOrder(Order order, Connection connection) {
		order.setHash(addConnectionId(order.getHash(), connection));
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
	
}
