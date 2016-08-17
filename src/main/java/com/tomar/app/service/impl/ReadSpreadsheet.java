package com.tomar.app.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.tomar.app.domain.Entry;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadSpreadsheet {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Adobe LatLang";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        "src/main/resources/cred1.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList(SheetsScopes.SPREADSHEETS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
        		ReadSpreadsheet.class.getResourceAsStream("/cred.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .setApprovalPrompt("force") 
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static List<Entry> getEntries(String spreadsheetId) throws IOException {
        // Build a new authorized API client service.
        Sheets service = getSheetsService();

        String range = "Sheet1!A2:I";
        ValueRange response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute();
        List<List<Object>> values = response.getValues();
        List<Entry> entryList = new ArrayList<Entry>();
        if (values == null || values.size() == 0) {
            System.out.println("No data found.");
        } else {
          System.out.println("Processing data");
          for (List row : values) {
        	  Entry e = new Entry();
        	  StringBuilder address = new StringBuilder(); 
        	  e.setAddress((String) row.get(0));
        	  e.setCity((String) row.get(1));
        	  e.setCountry((String) row.get(2));
        	  e.setPostal_code((String) row.get(3));
        	  e.setState_province((String) row.get(4));
        	  e.setCountry1((String) row.get(5));
        	  
        	  //form address string 
        	  address.append(row.get(0));
        	  address.append(",");
        	  address.append(row.get(1));
        	  address.append(",");
        	  address.append(row.get(4));
        	  address.append(",");
        	  address.append(row.get(5));
        	  address.append(",");
        	  address.append(row.get(3));

        	  String[] res = GeoEncodingService.getValue(address);
        	  row.add(6, res[0]);
        	  row.add(7, res[1]);
        	  row.add(8, res[2]);
        	  e.setGoogle_verified_address(res[0]);
        	  e.setLatitude(res[1]);
        	  e.setLongitude(res[2]);
        	  entryList.add(e);
          }
          ValueRange oRange = new ValueRange();
          oRange.setRange(range); // I NEED THE NUMBER OF THE LAST ROW
          oRange.setValues(values);

          List<ValueRange> oList = new ArrayList<>();
          oList.add(oRange);

          BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
          oRequest.setValueInputOption("RAW");
          oRequest.setData(oList);

          BatchUpdateValuesResponse oResp1 = service.spreadsheets().values().batchUpdate(spreadsheetId, oRequest).execute();

         // service.spreadsheets().values().update (spreadsheetId, range,) ;     
          //return request;

        }
        return entryList;
    }


}