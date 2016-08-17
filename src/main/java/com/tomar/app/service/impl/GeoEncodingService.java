package com.tomar.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomar.app.domain.GoogleResponse;
import com.tomar.app.domain.Result;

public class GeoEncodingService {
	private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json";
	//AIzaSyDWU6n5l8qpZ-jE7LLrFw0QgI8-Sjgu5Qc

	public static GoogleResponse convertToLatLong(String fullAddress) throws IOException {
		URL url = new URL(URL + "?address=" + URLEncoder.encode(fullAddress, "UTF-8") + "&sensor=false");
		URLConnection conn = url.openConnection();

		InputStream in = conn.getInputStream();
		ObjectMapper mapper = new ObjectMapper();
		GoogleResponse response = (GoogleResponse) mapper.readValue(in, GoogleResponse.class);
		in.close();
		return response;

	}


	public static String[] getValue(StringBuilder address) {
		GoogleResponse res = null;
		try {
			new GeoEncodingService();
			res = GeoEncodingService.convertToLatLong(address.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] str = new String[3];
		if (res.getStatus().equals("OK")) {
			for (Result result : res.getResults()) {
				str[1] = result.getGeometry().getLocation().getLat();
				str[2] = result.getGeometry().getLocation().getLng();
				str[0] = result.getFormatted_address();
			}
		} else {
			System.out.println(res.getStatus());
		}
		return str;
	}

}