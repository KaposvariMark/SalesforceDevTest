package com.salesforce;

import com.salesforce.entities.Account;
import com.salesforce.entities.Contact;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static final String AUTHORIZATION_API_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com/services/oauth2/token";
    public static final String VERSIONS_API_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com/services/data/";
    public static final String LIST_CONTACTS_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com//services/data/v53.0/sobjects/Contact";
    public static final String LIST_ACCOUNTS_URL = "https://artemis-innovations-gmbh--candidat.my.salesforce.com//services/data/v53.0/sobjects/Account";

    private static String bearerToken = "";

    private static final ArrayList<Account> Accounts = new ArrayList<>();
    private static final ArrayList<Contact> Contacts = new ArrayList<>();

    public static void main(String[] args) {
        int selectedOption;
        Menu menu = new Menu();
        Scanner sc = new Scanner(System.in);

        System.out.println("----------INIT---------");
        if(!init()){
            System.out.println("Something went wrong!");
            return;
        }

        System.out.println("----------INFO---------");
        System.out.println("Please type in a number between 1-4");

        do {
            selectedOption = menu.printMainOptions(sc);
            switch (selectedOption){
                case 0: break;
                case 1:
                    for (Account acc : Accounts) {
                        System.out.println("\n" + acc.getName() + "\nPhone number: " + acc.getPhone());
                    }
                    break;
                case 2:
                    for (Contact con : Contacts) {
                        System.out.println("\n" + con.getFirstName() + " " + con.getLastName());
                    }
                    break;
                case 3:
                    Account search = menu.printAccountsOptions(sc, Accounts);
                    getContactsOfAccount(search);
                    break;
                case 4:
                    System.out.println("Goodbye!");
                default:
                    System.out.println("Please chose a number from the aligning options");
            }
        } while (selectedOption != 4);

        sc.close();
    }

    private static boolean init() {
        if(livenessCheck()){
            try {
                bearerToken = setUpAuthorization();
            } catch (Exception e){
                System.out.println("Authorization failed!");
                e.printStackTrace();
            }
        } else System.out.println("Liveness check failed, please try again later!");
        if(bearerToken.isEmpty()) return false;
        else {
            try {
                loadAccounts(bearerToken);
                System.out.println("Loading accounts has finished!");
                loadContacts(bearerToken);
                System.out.println("Loading contacts has finished!");
            } catch (Exception e){
                System.out.println("Loading data has failed!");
            }
        }
        return true;
    }

    private static void getContactsOfAccount(Account account){
        System.out.println("\nContacts of this Account (" + account.getName() + "): ");
        for(Contact con : Contacts){
            if(account.getId().equals(con.getAccountID())) System.out.println(con.getFirstName() + " " + con.getLastName());
        }
    }

    private static void loadContacts(String token) throws IOException {
        HttpGet httpGet = new HttpGet(LIST_CONTACTS_URL);
        httpGet.setHeader(HttpParameters.AUTHORIZATION, HttpParameters.BEARER + token );
        httpGet.setHeader(HttpParameters.X_PRETTYPRINT, "1");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(httpGet);

        String jsonText = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(jsonText);
        JSONArray responseArray =  responseJson.getJSONArray("recentItems");

        for(int i = 0; i < responseArray.length(); i++){
            JSONObject recentItem = responseArray.getJSONObject(i);

            String id = recentItem.getString("Id");

            Contact contact = getContactById(id, bearerToken);
            Contacts.add(contact);
        }

        client.close();
    }

    private static Contact getContactById(String id, String token) throws IOException {
        HttpGet httpGet = new HttpGet(LIST_CONTACTS_URL + "/" + id);
        httpGet.setHeader(HttpParameters.AUTHORIZATION, HttpParameters.BEARER + token );
        httpGet.setHeader(HttpParameters.X_PRETTYPRINT, "1");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(httpGet);
        System.out.println("Contact by Id: " + id + " request is done! Status code: " + response.getStatusLine().getStatusCode());

        String jsonText = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(jsonText);
        String firstName = responseJson.getString("FirstName");
        String lastName = responseJson.getString("LastName");
        String accountID = responseJson.getString("AccountId");

        client.close();
        return new Contact(id, accountID, firstName, lastName);
    }

    private static void loadAccounts(String token) throws IOException {
        HttpGet httpGet = new HttpGet(LIST_ACCOUNTS_URL);
        httpGet.setHeader(HttpParameters.AUTHORIZATION, HttpParameters.BEARER + token );
        httpGet.setHeader(HttpParameters.X_PRETTYPRINT, "1");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(httpGet);

        String jsonText = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(jsonText);
        JSONArray responseArray =  responseJson.getJSONArray("recentItems");

        for(int i = 0; i < responseArray.length(); i++){
            JSONObject recentItem = responseArray.getJSONObject(i);

            String id = recentItem.getString("Id");
            String name = recentItem.getString("Name");

            Account account = getAccountById(id, name,  bearerToken);
            Accounts.add(account);
        }
        client.close();
    }

    private static Account getAccountById(String id, String name, String token) throws IOException {
        HttpGet httpGet = new HttpGet(LIST_ACCOUNTS_URL + "/" + id);
        httpGet.setHeader(HttpParameters.AUTHORIZATION, HttpParameters.BEARER + token );
        httpGet.setHeader(HttpParameters.X_PRETTYPRINT, "1");

        CloseableHttpClient client = HttpClientBuilder.create().build();
        CloseableHttpResponse response = client.execute(httpGet);
        System.out.println("Account by Id: " + id + "  request is done! Status code: " + response.getStatusLine().getStatusCode());

        String jsonText = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = new JSONObject(jsonText);
        String phone = responseJson.getString("Phone");

        client.close();
        return new Account(id, name, phone);
    }

    public static String setUpAuthorization() throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(AUTHORIZATION_API_URL);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(HttpParameters.GRANT_TYPE, HttpParameters.PASSWORD));
        params.add(new BasicNameValuePair(HttpParameters.CLIENT_ID, HttpParameters.ACTUAL_CLIENT_ID));
        params.add(new BasicNameValuePair(HttpParameters.CLIENT_SECRET, HttpParameters.ACTUAL_CLIENT_SECRET));
        params.add(new BasicNameValuePair(HttpParameters.USERNAME, HttpParameters.ACTUAL_USERNAME));
        params.add(new BasicNameValuePair(HttpParameters.PASSWORD, HttpParameters.ACTUAL_PASSWORD));

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
