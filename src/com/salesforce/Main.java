package com.salesforce;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class Main {

    public static final String VERSIONS_API_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com/services/data/";

    public static void main(String[] args) {

        livenessCheck();

    }

    public static boolean livenessCheck(){
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(VERSIONS_API_URL);
        try {
            CloseableHttpResponse response = client.execute(httpGet);
            client.close();

            if(response.getStatusLine().getStatusCode() < 299){
                System.out.println("Liveness Check is ready! Status code: " + response.getStatusLine().getStatusCode());
                return  true;
            }

        } catch (Exception e){
            System.out.println("Liveness Check failed!");
            e.printStackTrace();
        }
        return false;
    }
}
