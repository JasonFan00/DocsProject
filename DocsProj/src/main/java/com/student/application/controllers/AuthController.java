package com.student.application.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;

public class AuthController {
	@RequestMapping("/auth")
	public void AuthController(HttpServletRequest request) throws FileNotFoundException, IOException {
		
		if (request.getHeader("X-Requested-With") == null) {
			// Without the `X-Requested-With` header, this request could be forged. Aborts.
			return;
		}
		
		StringBuffer sb = new StringBuffer();
	    String line = null;
	    try {
	      BufferedReader reader = request.getReader();
	      while ((line = reader.readLine()) != null)
	    	  sb.append(line);
	    } catch (Exception e) { 
	    	
	    }
	    
	    JSONObject jsonObject = null;
	    try {
	    	jsonObject = new JSONObject(sb.toString());
	    } catch (Exception e) {
		throw new IOException("Error parsing JSON request string");
		
		}
	    
	    String authCode = jsonObject.getString("code");
	    
		// Set path to the Web application client_secret_*.json file you downloaded from the
		// Google API Console: https://console.developers.google.com/apis/credentials
		// You can also find your Web application client ID and client secret from the
		// console and specify them directly when you create the GoogleAuthorizationCodeTokenRequest
		// object.
		String authFile = "/src/main/resources/auth/g-api-client.json";

		// Exchange auth code for access token
		GoogleClientSecrets clientSecrets =
		    GoogleClientSecrets.load(
		        JacksonFactory.getDefaultInstance(), new FileReader(authFile));
		GoogleTokenResponse tokenResponse =
		          new GoogleAuthorizationCodeTokenRequest(
		              new NetHttpTransport(),
		              JacksonFactory.getDefaultInstance(),
		              "https://oauth2.googleapis.com/token",
		              clientSecrets.getDetails().getClientId(),
		              clientSecrets.getDetails().getClientSecret(),
		              authCode,
		              "")  // Specify the same redirect URI that you use with your web
		                             // app. If you don't have a web version of your app, you can
		                             // specify an empty string.
		              .execute();

		String accessToken = tokenResponse.getAccessToken();



		// Get profile info from ID token
		GoogleIdToken idToken = tokenResponse.parseIdToken();
		GoogleIdToken.Payload payload = idToken.getPayload();
		String userId = payload.getSubject();  // Use this value as a key to identify a user.
		String email = payload.getEmail();
		boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		String name = (String) payload.get("name");
		String pictureUrl = (String) payload.get("picture");
		String locale = (String) payload.get("locale");
		String familyName = (String) payload.get("family_name");
		String givenName = (String) payload.get("given_name");
		
		System.out.println(name);
	}
	
}
