package com.gillsoft.matrix.model;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Discount {

	private int id;
	private String kind;
	private String type;

	@JsonProperty("limit_from")
	private int limitFrom;

	@JsonProperty("limit_to")
	private int limitTo;

	private BigDecimal value;
	private Map<String, Parameters> i18n;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLimitFrom() {
		return limitFrom;
	}

	public void setLimitFrom(int limitFrom) {
		this.limitFrom = limitFrom;
	}

	public int getLimitTo() {
		return limitTo;
	}

	public void setLimitTo(int limitTo) {
		this.limitTo = limitTo;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Map<String, Parameters> getI18n() {
		return i18n;
	}

	public void setI18n(Map<String, Parameters> i18n) {
		this.i18n = i18n;
	}

}
