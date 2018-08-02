package com.gillsoft.matrix.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof City)) {
			return false;
		}
		City city = (City) obj;
		return super.equals(obj)
				&& geoCountryId == city.getGeoCountryId()
				&& geoRegionId == city.getGeoRegionId()
				&& latitude == city.getLatitude()
				&& longitude == city.getLongitude();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() * 31 + Objects.hash(geoCountryId, geoRegionId, latitude, longitude);
	}

}
