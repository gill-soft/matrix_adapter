package com.gillsoft.matrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Price {
	
	@JsonProperty("one_way")
	private int oneWay;
	
	@JsonProperty("round_trip")
	private int roundTrip;

	public int getOneWay() {
		return oneWay;
	}

	public void setOneWay(int oneWay) {
		this.oneWay = oneWay;
	}

	public int getRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(int roundTrip) {
		this.roundTrip = roundTrip;
	}

}
