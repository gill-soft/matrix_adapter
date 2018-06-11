package com.gillsoft.matrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point extends Locality {
	
	@JsonProperty("native_address")
	private String nativeAddress;

	public String getNativeAddress() {
		return nativeAddress;
	}

	public void setNativeAddress(String nativeAddress) {
		this.nativeAddress = nativeAddress;
	}

}
