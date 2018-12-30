package com.gillsoft.matrix.model;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class ReturnRule {
	
	private String title;
	private String description;
	private int minutesBeforeDepart;
	private int calculationType;
	private BigDecimal value;
	private BigDecimal feeValue;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private String activeFrom;
	
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date activeTo;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMinutesBeforeDepart() {
		return minutesBeforeDepart;
	}

	public void setMinutesBeforeDepart(int minutesBeforeDepart) {
		this.minutesBeforeDepart = minutesBeforeDepart;
	}

	public int getCalculationType() {
		return calculationType;
	}

	public void setCalculationType(int calculationType) {
		this.calculationType = calculationType;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getFeeValue() {
		return feeValue;
	}

	public void setFeeValue(BigDecimal feeValue) {
		this.feeValue = feeValue;
	}

	public String getActiveFrom() {
		return activeFrom;
	}

	public void setActiveFrom(String activeFrom) {
		this.activeFrom = activeFrom;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

}
