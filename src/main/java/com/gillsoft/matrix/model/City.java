package com.gillsoft.matrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class City extends Country {

	@JsonProperty("geo_country_id")
	private int geoCountryId;

	@JsonProperty("geo_region_id")
	private int geoRegionId;
	private float latitude;
	private float longitude;

	public int getGeoCountryId() {
		return geoCountryId;
	}

	public void setGeoCountryId(int geoCountryId) {
		this.geoCountryId = geoCountryId;
	}

	public int getGeoRegionId() {
		return geoRegionId;
	}

	public void setGeoRegionId(int geoRegionId) {
		this.geoRegionId = geoRegionId;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

}
