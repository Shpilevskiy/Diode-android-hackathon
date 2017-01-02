package com.example.shpilevskiy.wifidiode.LEDClient;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LEDClient implements LEDClientInterface {

    private static final String TOGGLE_LED_URL = "/toggle";
    private static final String SET_BRIGHTNESS_URL = "/set";
    private static final String STATUS_URL = "/status";
    private static final int MIN_BRIGHTNESS_LEVEL = 0;
    private static final int MAX_BRIGHTNESS_LEVEL = 255;
    private  static final String BRIGHTNESS_LEVEL_PARAM = "level";


    private static String host;

    private InputStream getRequest(String requestURL) throws LEDClientException  {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            return urlConnection.getInputStream();
        } catch (MalformedURLException e) {
            throw new LEDClientException("Incorrect URL");
        } catch (IOException e){
            throw new LEDClientException("Unknown error parsing URL");
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

    }

    public LEDClient(String host){
        this.host = host;
    }

    public void toggleLED() throws LEDClientException {
        String toggleURL = host + TOGGLE_LED_URL;
        getRequest(toggleURL);
    }

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

    public Boolean isOn() throws LEDClientException {
        return false;
    }
}