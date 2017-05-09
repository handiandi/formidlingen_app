package siteHandler;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by anders-dev on 5/4/17.
 */
public class PageHandler {
    private String  baseURL;


    public PageHandler(String url){
        this.baseURL = url;
        BasicCookieStore cookieStore = new BasicCookieStore();
        Unirest.setHttpClient(HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build());

    }

    public String getPage(String urlPage, String t){ //Map<String, String> headers
        String r = "";
        urlPage = this.baseURL + urlPage;
        switch (t.toLowerCase()){
            case "json":
                try {
                    r = Unirest.get(urlPage)
                            .asJson().getBody().toString();
                } catch (UnirestException e){
                    System.out.println("FEEEJL!");
                }

                break;
            default: //asString
                try {
                    r = Unirest.get(urlPage)
                            .asString().getBody();
                } catch (UnirestException e){
                    System.out.println("FEEEJL!" + e.getMessage());
                }
        }
        return r;
    }



    public JSONObject postAsJson(String urlPage, String payload, Map<String, String> headers){
        HttpResponse<JsonNode> r = null;
        try {
            r = Unirest.post(urlPage)
                .headers(headers)
                .body(payload)
                .asJson();
        } catch (UnirestException e){
            System.out.println("FEEEJL i post!");
        }
        return r.getBody().getObject();





    }


}
