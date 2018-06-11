package com.gillsoft.matrix.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class Ticket {

	private String number;
	
	@JsonProperty("trip_id")
	private String tripId;
	
	@JsonProperty("pass_name")
	private String passName;
	
	@JsonProperty("pass_surname")
	private String passSurname;
	
	private String status;

	private BigDecimal cost;
	private BigDecimal price;

	@JsonProperty("sale_date")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date saleDate;

	@JsonProperty("reserved_to")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservedTo;

	@JsonProperty("geo_point_from")
	private String geoPointFrom;
	
	@JsonProperty("geo_locality_from")
	private String geoLocalityFrom;
	
	@JsonProperty("geo_point_to")
	private String geoPointTo;
	
	@JsonProperty("geo_locality_to")
	private String geoLocalityTo;

	@JsonProperty("depart_at")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date departAt;

	@JsonProperty("arrive_at")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date arriveAt;

	@JsonProperty("reservation_enable_to")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date reservationEnableTo;

	@JsonProperty("sale_enable_to")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date saleEnableTo;

	private String seat;
	
	@JsonProperty("seat_number")
	private String seatNumber;
	
	private String hash;
	
	@JsonProperty("path_tariff")
	private BigDecimal pathTariff;
	
	@JsonProperty("carrier_code")
	private String carrierCode;
	
	@JsonProperty("insurance_code")
	private String insuranceCode;
	
	@JsonProperty("station_code")
	private String stationCode;
	
	@JsonProperty("path_tariff_currency")
	private String pathTariffCurrency;
	
	private String currency;
	private BigDecimal vat;
	
	@JsonProperty("agent_code")
	private String agentCode;
	
	private BigDecimal tariff;
	
	@JsonProperty("vat_rate")
	private BigDecimal vatRate;
	
	@JsonProperty("carrier_tariff")
	private BigDecimal carrierTariff;
	
	@JsonProperty("carrier_fare_tariff")
	private BigDecimal carrierFareTariff;
	
	@JsonProperty("fare_tariff")
	private BigDecimal fareTariff;
	
	@JsonProperty("exchange_rate")
	private BigDecimal exchangeRate;
	
	@JsonProperty("route_code")
	private String routeCode;
	
	@JsonProperty("path_from_index")
	private int pathFromIndex;
	
	@JsonProperty("path_to_index")
	private int pathToIndex;
	
	@JsonProperty("geo_point_from_name")
	private String geoPointFromName;
	
	@JsonProperty("geo_point_to_name")
	private String geoPointToName;
	
	@JsonProperty("geo_locality_from_name")
	private String geoLocalityFromName;
	
	@JsonProperty("geo_locality_to_name")
	private String geoLocalityToName;
	
	@JsonProperty("carrier_name")
	private String carrierName;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public String getPassName() {
		return passName;
	}

	public void setPassName(String passName) {
		this.passName = passName;
	}

	public String getPassSurname() {
		return passSurname;
	}

	public void setPassSurname(String passSurname) {
		this.passSurname = passSurname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public Date getReservedTo() {
		return reservedTo;
	}

	public void setReservedTo(Date reservedTo) {
		this.reservedTo = reservedTo;
	}

	public String getGeoPointFrom() {
		return geoPointFrom;
	}

	public void setGeoPointFrom(String geoPointFrom) {
		this.geoPointFrom = geoPointFrom;
	}

	public String getGeoLocalityFrom() {
		return geoLocalityFrom;
	}

	public void setGeoLocalityFrom(String geoLocalityFrom) {
		this.geoLocalityFrom = geoLocalityFrom;
	}

	public String getGeoPointTo() {
		return geoPointTo;
	}

	public void setGeoPointTo(String geoPointTo) {
		this.geoPointTo = geoPointTo;
	}

	public String getGeoLocalityTo() {
		return geoLocalityTo;
	}

	public void setGeoLocalityTo(String geoLocalityTo) {
		this.geoLocalityTo = geoLocalityTo;
	}

	public Date getDepartAt() {
		return departAt;
	}

	public void setDepartAt(Date departAt) {
		this.departAt = departAt;
	}

	public Date getArriveAt() {
		return arriveAt;
	}

	public void setArriveAt(Date arriveAt) {
		this.arriveAt = arriveAt;
	}

	public Date getReservationEnableTo() {
		return reservationEnableTo;
	}

	public void setReservationEnableTo(Date reservationEnableTo) {
		this.reservationEnableTo = reservationEnableTo;
	}

	public Date getSaleEnableTo() {
		return saleEnableTo;
	}

	public void setSaleEnableTo(Date saleEnableTo) {
		this.saleEnableTo = saleEnableTo;
	}

	public String getSeat() {
		return seat;
	}

	public void setSeat(String seat) {
		this.seat = seat;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public BigDecimal getPathTariff() {
		return pathTariff;
	}

	public void setPathTariff(BigDecimal pathTariff) {
		this.pathTariff = pathTariff;
	}

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getInsuranceCode() {
		return insuranceCode;
	}

	public void setInsuranceCode(String insuranceCode) {
		this.insuranceCode = insuranceCode;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getPathTariffCurrency() {
		return pathTariffCurrency;
	}

	public void setPathTariffCurrency(String pathTariffCurrency) {
		this.pathTariffCurrency = pathTariffCurrency;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getVat() {
		return vat;
	}

	public void setVat(BigDecimal vat) {
		this.vat = vat;
	}

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public BigDecimal getTariff() {
		return tariff;
	}

	public void setTariff(BigDecimal tariff) {
		this.tariff = tariff;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}

	public BigDecimal getCarrierTariff() {
		return carrierTariff;
	}

	public void setCarrierTariff(BigDecimal carrierTariff) {
		this.carrierTariff = carrierTariff;
	}

	public BigDecimal getCarrierFareTariff() {
		return carrierFareTariff;
	}

	public void setCarrierFareTariff(BigDecimal carrierFareTariff) {
		this.carrierFareTariff = carrierFareTariff;
	}

	public BigDecimal getFareTariff() {
		return fareTariff;
	}

	public void setFareTariff(BigDecimal fareTariff) {
		this.fareTariff = fareTariff;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(String routeCode) {
		this.routeCode = routeCode;
	}

	public int getPathFromIndex() {
		return pathFromIndex;
	}

	public void setPathFromIndex(int pathFromIndex) {
		this.pathFromIndex = pathFromIndex;
	}

	public int getPathToIndex() {
		return pathToIndex;
	}

	public void setPathToIndex(int pathToIndex) {
		this.pathToIndex = pathToIndex;
	}

	public String getGeoPointFromName() {
		return geoPointFromName;
	}

	public void setGeoPointFromName(String geoPointFromName) {
		this.geoPointFromName = geoPointFromName;
	}

	public String getGeoPointToName() {
		return geoPointToName;
	}

	public void setGeoPointToName(String geoPointToName) {
		this.geoPointToName = geoPointToName;
	}

	public String getGeoLocalityFromName() {
		return geoLocalityFromName;
	}

	public void setGeoLocalityFromName(String geoLocalityFromName) {
		this.geoLocalityFromName = geoLocalityFromName;
	}

	public String getGeoLocalityToName() {
		return geoLocalityToName;
	}

	public void setGeoLocalityToName(String geoLocalityToName) {
		this.geoLocalityToName = geoLocalityToName;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

}
