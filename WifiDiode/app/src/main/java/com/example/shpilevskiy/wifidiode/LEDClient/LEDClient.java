package com.example.shpilevskiy.wifidiode.LEDClient;


import android.util.Log;
import android.util.StringBuilderPrinter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LEDClient implements LEDClientInterface {

    private static final String TOGGLE_LED_URL = "/toggle";
    private static final String SET_BRIGHTNESS_URL = "/set";
    private static final String STATUS_URL = "/status";
    private static final int MIN_BRIGHTNESS_LEVEL = 0;
    private static final int MAX_BRIGHTNESS_LEVEL = 255;
    private static final String BRIGHTNESS_LEVEL_PARAM = "level";
    private static final String JSON_STATUS_KEY = "status";


    private String host;

    private InputStream getRequest(String requestURL) throws LEDClientException  {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection.getInputStream();
        } catch (MalformedURLException e) {
            Log.e("LED Client", e.toString());
            throw new LEDClientException("Incorrect URL");
        } catch (IOException e){
            Log.e("LED Client", e.toString());
            throw new LEDClientException("Unknown error parsing URL");
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

    }

    private String inputStreamToString(InputStream in) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();

        String inputString;

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        while((inputString = streamReader.readLine()) != null){
            responseBuilder.append(inputString);
        }
        return responseBuilder.toString();
    }

    public LEDClient(String host){
        this.host = host;
    }

    @Override
    public void toggleLED() throws LEDClientException {
        String toggleURL = host + TOGGLE_LED_URL;
        getRequest(toggleURL);
    }

    @Override
    public void setBrightnessLevel(int level) throws LEDClientException {
        if (level < MIN_BRIGHTNESS_LEVEL){
            level = MIN_BRIGHTNESS_LEVEL;
        } else if (level > MAX_BRIGHTNESS_LEVEL){
            level = MAX_BRIGHTNESS_LEVEL;
        }
        String levelURL = String.format("%s%s?%s=%s",
                                        host, SET_BRIGHTNESS_URL,
                                        BRIGHTNESS_LEVEL_PARAM, level);
        getRequest(levelURL);
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public Boolean isOn() throws LEDClientException {
        String statusURL = host + STATUS_URL;
        InputStream in = getRequest(statusURL);

        String JSONString = null;
        try {
            JSONString = inputStreamToString(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new LEDClientException();
        }
        try {
            JSONObject jo = new JSONObject(JSONString);
            if (!jo.has(JSON_STATUS_KEY)){
                throw new LEDClientException("Invalid JSON response");
            }

            String ledStatus = jo.getString(JSON_STATUS_KEY);
            if (ledStatus.equals("on")){
                Log.d("LED Client", "LED is on");
                return true;
            } else if (ledStatus.equals("off")){
                Log.d("LED Client", "LED is off");
                return false;
            } else {
                throw new LEDClientException("Invalid LED status " + ledStatus);
            }

        } catch (JSONException e){
            Log.e("LED CLient", e.getMessage());
            throw new LEDClientException("JSON parsing error.");
        }
    }
}