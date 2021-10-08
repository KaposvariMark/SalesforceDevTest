package com.salesforce;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String AUTHORIZATION_API_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com/services/oauth2/token";
    public static final String VERSIONS_API_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com/services/data/";
    public static final String LIST_CONTACTS_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com//services/data/v53.0/sobjects/Contact";
    public static final String LIST_ACCOUNTS_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com//services/data/v53.0/sobjects/Account";

    private static String bearerToken = "";

    public static void main(String[] args) {

        if(!livenessCheck()) return;

        try {
            bearerToken = setUpAuthorization();
        } catch (Exception e){
            System.out.println("Authorization failed!");
            e.printStackTrace();
        }

        if(!bearerToken.isEmpty()){
            try {
                getAccounts(bearerToken);
                getContacts(bearerToken);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void getContacts(String token) throws IOException {
        HttpGet httpGet = new HttpGet(LIST_CONTACTS_URL);

        httpGet.setHeader("Authorization", "Bearer " + token );
        httpGet.setHeader("X-PrettyPrint", "1");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(httpGet);
        System.out.println("Contacts check is done! Status code: " + response.getStatusLine().getStatusCode());

        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println(responseString);

        client.close();
    }

    private static void getAccounts(String token) throws IOException {
        HttpGet httpGet = new HttpGet(LIST_ACCOUNTS_URL);

        //String credential = Base64.getEncoder().encodeToString(("Bearer " + token).getBytes("UTF-8"));
        //httpGet.setHeader("Authorization", "Basic " + credential.substring(0, credential.length()-1));
        httpGet.setHeader("Authorization", "Bearer " + token );
        httpGet.setHeader("X-PrettyPrint", "1");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(httpGet);
        System.out.println("Accounts check is done! Status code: " + response.getStatusLine().getStatusCode());

        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println(responseString);

        client.close();
    }

    public static String setUpAuthorization() throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(AUTHORIZATION_API_URL);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("client_id", "3MVG9_I_oWkIqLrlI5LkNC2hvXlqecJgiwYW3fqXeJkVPoxDSVHhLZbTQCPQUlNK5AzAgMCSwnCRLZe4d_08s"));
        params.add(new BasicNameValuePair("client_secret", "A78E9BAE7BD5CCB728104B39FCC8D1A54153840D8876E619470360BA14DF0DE6"));
        params.add(new BasicNameValuePair("username", "candidat@artemis-innovations.de.candidat"));
        params.add(new BasicNameValuePair("password", "ArtemisInno1!"));

        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(httpPost);
        String jsonText = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(jsonText);

        System.out.println("Authorization ready! Status code: " + response.getStatusLine().getStatusCode());

        client.close();
        return responseJson.getString("access_token");
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
