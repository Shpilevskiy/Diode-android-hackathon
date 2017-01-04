package com.example.shpilevskiy.wifidiode.LEDHttp;

import android.util.Log;

import com.example.shpilevskiy.wifidiode.LEDHttp.LEDClient.LEDClientException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nick on 04.01.17.
 */

public abstract class HTTPClient {

    protected JSONObject getRequest(String requestURL) throws LEDClientException {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            return getJsonObject(urlConnection.getInputStream());
        } catch (MalformedURLException e) {
            Log.e("LED Client", e.toString());
            throw new LEDClientException("Incorrect URL");
        } catch (IOException e) {
            Log.e("LED Client", e.toString());
            throw new LEDClientException("Unknown error parsing URL");
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

    }

    protected JSONObject getJsonObject(InputStream in){
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null){
                responseStrBuilder.append(inputStr);
            }
            //returns the json object
            return new JSONObject(responseStrBuilder.toString());

        } catch (IOException | JSONException ignored) {
        }
        return null;
    }
}
