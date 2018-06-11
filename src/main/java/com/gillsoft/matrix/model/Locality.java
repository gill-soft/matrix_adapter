package com.gillsoft.matrix.model;

import java.util.Map;

public class Locality extends City {

	private Map<String, Parameters> i18n;

	public Map<String, Parameters> getI18n() {
		return i18n;
	}

	public void setI18n(Map<String, Parameters> i18n) {
		this.i18n = i18n;
	}
	
}
