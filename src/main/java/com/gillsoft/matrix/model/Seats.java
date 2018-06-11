package com.gillsoft.matrix.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Seats {
	
	private Map<String, String> list;
	private int count;
	
	@JsonProperty("is_open")
	private boolean open;

	public Map<String, String> getList() {
		return list;
	}

	public void setList(Map<String, String> list) {
		this.list = list;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
}
