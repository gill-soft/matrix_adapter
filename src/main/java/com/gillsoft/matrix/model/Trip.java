package com.gillsoft.matrix.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class Trip {
	
	private int tripId;
	private int routeId;
	private String routeCode;
	private String carrier;
	private String intervalId;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date departDate;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	private Date arriveDate;
	
	private String arriveTime;
	private String departTime;
	private String departCity;
	private int departCityId;
	private String arriveCity;
	private int arriveCityId;
	private String carrierCode;
	private String departStation;
	private int departStationId;
	private String arriveStation;
	private int arriveStationId;
	private Seats freeSeats;
	private int distance;
	private String timeInWay;
	private boolean international;
	private Map<String, Boolean> docFields;
	private int tariff;
	private Price price;
	private List<Discount> discounts;
	private String currency;
	
	@JsonProperty("exchange_rate")
	private float exchangeRate;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date saleCloseAt;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservationCloseAt;

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getIntervalId() {
		return intervalId;
	}

	public void setIntervalId(String intervalId) {
		this.intervalId = intervalId;
	}

	public Date getDepartDate() {
		return departDate;
	}

	public void setDepartDate(Date departDate) {
		this.departDate = departDate;
	}

	public Date getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(Date arriveDate) {
		this.arriveDate = arriveDate;
	}

	public String getArriveTime() {
		return arriveTime;
	}

	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}

	public String getDepartTime() {
		return departTime;
	}

	public void setDepartTime(String departTime) {
		this.departTime = departTime;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public int getDepartCityId() {
		return departCityId;
	}

	public void setDepartCityId(int departCityId) {
		this.departCityId = departCityId;
	}

	public String getArriveCity() {
		return arriveCity;
	}

	public void setArriveCity(String arriveCity) {
		this.arriveCity = arriveCity;
	}

	public int getArriveCityId() {
		return arriveCityId;
	}

	public void setArriveCityId(int arriveCityId) {
		this.arriveCityId = arriveCityId;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getDepartStation() {
		return departStation;
	}

	public void setDepartStation(String departStation) {
		this.departStation = departStation;
	}

	public int getDepartStationId() {
		return departStationId;
	}

	public void setDepartStationId(int departStationId) {
		this.departStationId = departStationId;
	}

	public String getArriveStation() {
		return arriveStation;
	}

	public void setArriveStation(String arriveStation) {
		this.arriveStation = arriveStation;
	}

	public int getArriveStationId() {
		return arriveStationId;
	}

	public void setArriveStationId(int arriveStationId) {
		this.arriveStationId = arriveStationId;
	}

	public Seats getFreeSeats() {
		return freeSeats;
	}

	public void setFreeSeats(Seats freeSeats) {
		this.freeSeats = freeSeats;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getTimeInWay() {
		return timeInWay;
	}

	public void setTimeInWay(String timeInWay) {
		this.timeInWay = timeInWay;
	}

	public boolean isInternational() {
		return international;
	}

	public void setInternational(boolean international) {
		this.international = international;
	}

	public Map<String, Boolean> getDocFields() {
		return docFields;
	}

	public void setDocFields(Map<String, Boolean> docFields) {
		this.docFields = docFields;
	}

	public int getTariff() {
		return tariff;
	}

	public void setTariff(int tariff) {
		this.tariff = tariff;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}

	public void setDiscounts(List<Discount> discounts) {
		this.discounts = discounts;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public float getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Date getSaleCloseAt() {
		return saleCloseAt;
	}

	public void setSaleCloseAt(Date saleCloseAt) {
		this.saleCloseAt = saleCloseAt;
	}

	public Date getReservationCloseAt() {
		return reservationCloseAt;
	}

	public void setReservationCloseAt(Date reservationCloseAt) {
		this.reservationCloseAt = reservationCloseAt;
	}
	
}
