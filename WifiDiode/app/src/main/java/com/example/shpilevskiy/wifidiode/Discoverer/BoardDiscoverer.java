package com.example.shpilevskiy.wifidiode.Discoverer;

import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BoardDiscoverer implements BoardDiscovererInterface {

    private final int HTTP_STATUS_200 = 200;

    private int DiscoveryTimeout = 25;
    private String port = "80";
    private WifiManager wifiManager;

    public BoardDiscoverer(int discoveryTimeout, WifiManager wifiManager) {
        this.DiscoveryTimeout = discoveryTimeout;
        this.wifiManager = wifiManager;
    }

    BoardDiscoverer(int discoveryTimeout, WifiManager wifiManager, String port) {
        this.DiscoveryTimeout = discoveryTimeout;
        this.wifiManager = wifiManager;
        this.port = port;
    }

    private String[] getCurrentIpAddress() {
        String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        return ip.split("\\.");
    }

    public String discoverBoard() {
        String[] address = getCurrentIpAddress();

        String baseAddress = String.format("http://%s.%s.%s.", address[0], address[1], address[2]);

        for (int i = 0; i < 256; ++i) {
            if (i != Integer.parseInt(address[3])) {
                try {
                    String addressToRequest = baseAddress + String.valueOf(i) + ":" + port + "/test";
                    URL url = new URL(addressToRequest);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    HttpURLConnection.setFollowRedirects(false);
                    urlConnection.setConnectTimeout(DiscoveryTimeout);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == HTTP_STATUS_200){
                        JSONObject jsonResponse = getJsonObject(urlConnection.getInputStream());
                        if (jsonResponse != null)
                        {
                            if (jsonResponse.has("status") && jsonResponse.get("status").equals("on")) {
                                return baseAddress + String.valueOf(i) + ":" + port;
                            }
                        }

                    }
                } catch (IOException ignored) {
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private JSONObject getJsonObject(InputStream in){
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            //returns the json object
            return new JSONObject(responseStrBuilder.toString());

        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

}

