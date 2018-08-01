package com.gillsoft;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.gillsoft.concurrent.PoolType;
import com.gillsoft.concurrent.ThreadPoolStore;
import com.gillsoft.matrix.model.Response;

public class RequestSender<T> {
	
	public ResponseEntity<Response<T>> getDataResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<Response<T>> type, PoolType poolType,
			T container, ContainerDataFiller<T> filler) {
		List<Callable<ResponseEntity<Response<T>>>> callables = new ArrayList<>();
		for (Connection connection : Config.getConnections()) {
			if (connection.isAvailable()) {
				callables.add(() -> {
					URI uri = UriComponentsBuilder.fromUriString(connection.getUrl() + method)
							.queryParams(requestParams)
							.build().toUri();
					try {
						RequestEntity<Object> request = new RequestEntity<>(httpMethod, uri);
						ResponseEntity<Response<T>> response = connection.getTemplate().exchange(request,
								new ParameterizedTypeReference<Response<T>>() {});
						return response;
					} catch (RestClientException e) {
						return null;
					}
				});
			}
		}
		List<ResponseEntity<Response<T>>> results = ThreadPoolStore.getResult(poolType, callables);
		ResponseEntity<Response<T>> result = null;
		Response<T> response = null;
		for (ResponseEntity<Response<T>> responseEntity : results) {
			if (responseEntity != null
					&& (responseEntity.getStatusCode() == HttpStatus.ACCEPTED
							|| responseEntity.getStatusCode() == HttpStatus.OK)) {
				filler.fill(responseEntity, container);
				if (result == null) {
					response = new Response<>();
					response.setStatus(true);
					response.setData(container);
					result = new ResponseEntity<Response<T>>(response, responseEntity.getStatusCode());
				}
			}
		}
		if (result == null) {
			for (ResponseEntity<Response<T>> responseEntity : results) {
				if (responseEntity != null) {
					result = new ResponseEntity<Response<T>>(responseEntity.getBody(), responseEntity.getStatusCode());
					break;
				}
			}
		}
		return result;
	}
	
	public ResponseEntity<T> getResponse(String method, HttpMethod httpMethod,
			MultiValueMap<String, String> requestParams, ParameterizedTypeReference<T> type, PoolType poolType,
			T container, ContainerFiller<T> filler) {
		List<Callable<ResponseEntity<T>>> callables = new ArrayList<>();
		for (Connection connection : Config.getConnections()) {
			if (connection.isAvailable()) {
				callables.add(() -> {
					URI uri = UriComponentsBuilder.fromUriString(connection.getUrl() + method)
							.queryParams(requestParams)
							.build().toUri();
					try {
						RequestEntity<Object> request = new RequestEntity<>(httpMethod, uri);
						ResponseEntity<T> response = connection.getTemplate().exchange(request,
								new ParameterizedTypeReference<T>() {});
						return response;
					} catch (RestClientException e) {
						return null;
					}
				});
			}
		}
		List<ResponseEntity<T>> results = ThreadPoolStore.getResult(poolType, callables);
		ResponseEntity<T> result = null;
		for (ResponseEntity<T> responseEntity : results) {
			if (responseEntity != null
					&& (responseEntity.getStatusCode() == HttpStatus.ACCEPTED
							|| responseEntity.getStatusCode() == HttpStatus.OK)) {
				filler.fill(responseEntity, container);
				if (result == null) {
					result = new ResponseEntity<T>(container, responseEntity.getStatusCode());
				}
			}
		}
		if (result == null) {
			for (ResponseEntity<T> responseEntity : results) {
				if (responseEntity != null) {
					result = new ResponseEntity<T>(responseEntity.getBody(), responseEntity.getStatusCode());
					break;
				}
			}
		}
		return result;
	}
	
	public interface ContainerDataFiller<T> {
		
		public void fill(ResponseEntity<Response<T>> response, T container);
		
	}
	
	public interface ContainerFiller<T> {
		
		public void fill(ResponseEntity<T> response, T container);
		
	}

}
