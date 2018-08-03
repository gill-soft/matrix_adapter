package com.gillsoft.matrix.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gillsoft.Connection;

@JsonInclude(value = Include.NON_NULL)
public class Response<T> implements Serializable {
	
	private static final long serialVersionUID = -7978869662657932502L;

	@JsonIgnore
	private Connection connection;
	
	private boolean status;
	private String error;
	private String message;
	private Map<String, List<String>> messages;
	private T data;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, List<String>> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, List<String>> messages) {
		this.messages = messages;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
