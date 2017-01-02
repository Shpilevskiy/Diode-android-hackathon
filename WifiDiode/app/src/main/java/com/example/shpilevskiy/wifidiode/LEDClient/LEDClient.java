package com.example.shpilevskiy.wifidiode.LEDClient;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LEDClient implements LEDClientInterface {

    private static final String TOGGLE_LED_URL = "toggle";
    private static final String SET_BRIGHTNESS_URL = "set";
    private static final String STATUS_URL = "status";


    private static String host;

    public LEDClient(String host){
        this.host = host;
    }

    public void toggleLED() throws LEDClientException {
        String toggleURL = host + TOGGLE_LED_URL;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(toggleURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            // urlConnection.getInputStream();
        } catch (MalformedURLException e) {
            throw new LEDClientException("Incorrect URL");
        } catch (IOException e){
            throw new LEDClientException("Unknown error parsing URL");
        }
    }

    public void setBrightnessLevel(int level){

    }

    public Boolean isOn(){
        return false;
    }
}