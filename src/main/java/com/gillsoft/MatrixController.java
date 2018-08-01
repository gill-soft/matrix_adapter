package com.gillsoft;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.matrix.model.City;
import com.gillsoft.matrix.model.Country;
import com.gillsoft.matrix.model.Locale;
import com.gillsoft.matrix.model.Response;
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
		return client.getCountries(login, password, locale);
	}
	
	@GetMapping(RestClient.CITIES)
	public ResponseEntity<Response<Set<City>>> getCities(String login, String password, String locale,
			String countryId) {
		return client.getCities(login, password, locale, countryId);
	}
	
	@PostMapping(path = RestClient.TRIPS)
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
				isTest, withEmptySeats, currency, uniqueTrip);
	}
	
	
}
