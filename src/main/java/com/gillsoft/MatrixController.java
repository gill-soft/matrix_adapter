package com.gillsoft;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
public class MatrixController {
	
	@Autowired
	private RestClient client;

	@GetMapping(RestClient.PING)
	public ResponseEntity<Response<Object>> ping(String login, String password, String locale) {
		return client.ping(login, password);
	}
	
	@GetMapping(RestClient.LOCALES)
	public ResponseEntity<Response<Map<String, Locale>>> getLocales(String login, String password, String locale) {
		return client.getLocales(login, password, locale);
	}
	
	@GetMapping(RestClient.CURRENCIES)
	public ResponseEntity<Response<Map<String, String>>> getCurrencies(String login, String password, String locale) {
		return client.getCurrencies(login, password, locale);
	}
	
	@GetMapping(RestClient.COUNTRIES)
	public ResponseEntity<Set<Country>> getCountries(String login, String password, String locale) {
		return client.getCountries(login, password, locale, true);
	}
	
	@GetMapping(RestClient.CITIES)
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale,
			@RequestParam(name = "country_id", required = false) String countryId) {
		return client.getCities(login, password, locale, countryId, true);
	}
	
	@PostMapping(RestClient.TRIPS)
	public ResponseEntity<Response<List<Trip>>> getTrips(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "route_id", required = false) String routeId,
			@RequestParam(name = "depart_locality", required = false) String departLocality,
			@RequestParam(name = "arrive_locality", required = false) String arriveLocality,
			@RequestParam(name = "depart_date", required = false) String departDate,
			@RequestParam(required = false) String period,
			@RequestParam(name = "is_test", required = false) String isTest,
			@RequestParam(name = "with_empty_seats", required = false) String withEmptySeats,
			@RequestParam(required = false) String currency,
			@RequestParam(name = "unique_trip", required = false) String uniqueTrip) {
		return client.getTrips(login, password, locale, routeId, departLocality, arriveLocality, departDate, period,
				isTest, withEmptySeats, currency, uniqueTrip, true);
	}
	
	@PostMapping(RestClient.RULES)
	public ResponseEntity<Response<List<ReturnRule>>> getReturnRules(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "interval_id", required = false) String intervalId) {
		return client.getReturnRules(login, password, locale, intervalId);
	}
	
	@PostMapping(RestClient.ROUTE)
	public ResponseEntity<Response<RouteInfo>> getRoute(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "route_id", required = false) String routeId) {
		return client.getRoute(login, password, locale, routeId);
	}
	
	@PostMapping(RestClient.SEATS_MAP)
	public ResponseEntity<Response<List<List<Seat>>>> getSeatsMap(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "interval_id", required = false) String intervalId) {
		return client.getSeatsMap(login, password, locale, intervalId);
	}
	
	@PostMapping(RestClient.FREE_SEATS)
	public ResponseEntity<Response<Map<String, String>>> getFreeSeats(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "interval_id", required = false) String intervalId) {
		return client.getFreeSeats(login, password, locale, intervalId);
	}
	
	@PostMapping(RestClient.NEW_ORDER)
	public ResponseEntity<Response<Order>> create(HttpServletRequest request) {
		return client.create(request);
	}
	
	@PostMapping(RestClient.RESERVE)
	public ResponseEntity<Response<Order>> reserve(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId) {
		return client.reserve(login, password, locale, orderId);
	}
	
	@PostMapping(RestClient.BUY)
	public ResponseEntity<Response<Order>> buy(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId) {
		return client.buy(login, password, locale, orderId);
	}
	
	@PostMapping(RestClient.CANCEL)
	public ResponseEntity<Response<Order>> cancel(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId) {
		return client.cancel(login, password, locale, orderId);
	}
	
	@PostMapping(RestClient.INFO)
	public ResponseEntity<Response<Order>> info(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId,
			@RequestParam(name = "with_fees", required = false) String withFees) {
		return client.info(login, password, locale, orderId, withFees);
	}
	
	@PostMapping(RestClient.ANNULMENT)
	public ResponseEntity<Response<Order>> annulment(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId,
			@RequestParam(required = false) String description) {
		return client.annulment(login, password, locale, orderId, description);
	}
	
	@PostMapping(RestClient.AUTO_RETURN)
	public ResponseEntity<Response<Order>> autoReturn(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "order_id", required = false) String orderId,
			@RequestParam(required = false) String description) {
		return client.autoReturn(login, password, locale, orderId, description);
	}
	
	@PostMapping(RestClient.RETURN)
	public ResponseEntity<Response<Order>> manualReturn(HttpServletRequest request) {
		return client.manualReturn(request);
	}
	
	@PostMapping(RestClient.TICKET_AUTO_RETURN)
	public ResponseEntity<Response<Ticket>> ticketAutoReturn(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description) {
		return client.ticketAutoReturn(login, password, locale, ticketId, description);
	}
	
	@PostMapping(RestClient.TICKET_AUTO_RETURN_PRICE)
	public ResponseEntity<Response<Ticket>> ticketAutoReturnPrice(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description) {
		return client.ticketAutoReturnPrice(login, password, locale, ticketId, description);
	}
	
	@PostMapping(RestClient.TICKET_ANNULMENT)
	public ResponseEntity<Response<Ticket>> ticketAnnulment(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description) {
		return client.ticketAnnulment(login, password, locale, ticketId, description);
	}
	
	@PostMapping(RestClient.TICKET_RETURN)
	public ResponseEntity<Response<Ticket>> ticketManualReturn(
			@RequestParam(required = false) String login,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) String locale,
			@RequestParam(name = "ticket_id", required = false) String ticketId,
			@RequestParam(required = false) String description,
			@RequestParam(required = false) String amount) {
		return client.ticketManualReturn(login, password, locale, ticketId, description, amount);
	}

}
