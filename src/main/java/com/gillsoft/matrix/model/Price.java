package com.gillsoft.matrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Price {
	
	@JsonProperty("one_way")
	private int oneWay;
	
	@JsonProperty("roundTrip")
	private int round_trip;

	public int getOneWay() {
		return oneWay;
	}

	public void setOneWay(int oneWay) {
		this.oneWay = oneWay;
	}

	public int getRound_trip() {
		return round_trip;
	}

	public void setRound_trip(int round_trip) {
		this.round_trip = round_trip;
	}

}
