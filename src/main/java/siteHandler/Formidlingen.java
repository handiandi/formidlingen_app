package siteHandler;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import siteHandler.PageHandler;
import siteHandler.Response;
import userInfo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anders-dev on 5/6/17.
 */
public class Formidlingen {
    private PageHandler handler;
    private Response res;
    private Person p;


    public Formidlingen(){
        handler = new PageHandler("https://www.formidlingen.dk");
    }

    public JSONObject login(String email, String password){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Accept-Language", "da-DK,da;q=0.8,en-US;q=0.6,en;q=0.4");
        headers.put("Origin", "https://www.formidlingen.dk");

        String url = "https://www.formidlingen.dk/Services/UserService.asmx/Authenticate";
        String payload = "{\"username\":\""+email+"\"," +
                         "\"password\":\""+password+"\"," +
                         "\"rememberMe\":"+false+"," +
                         "\"referrer\":\"\"}";

        return handler.postAsJson(url, payload, headers);

    }

    public boolean go(String email, String password){
        //"/tidsregistrering.aspx"
        JSONObject result = login(email, password);
        if (!result.has("d")){
            return false; //noget gik helt galt
        } else {
            Gson g = new Gson();
            res = g.fromJson(result.toString(), Response.class);
            if (!res.d.Authenticated){
                return false;
            }
            createUser(email);
            String page = getMyPage();

            updateUser(page);

            return res.d.getAuthenticated(); //Kaldet var rigtigt. True/False om login er rigtigt
        } //if-else
    }
    private void updateUser(String page) {
        Document html = Jsoup.parse(page);
        Element a = html.select("li.last.user-ico").select("a").get(0);
        String name = a.text().split("-")[1].trim();
        this.p.setName(name);
    }


    private void createUser(String email) {

        switch (res.d.getType().toLowerCase()){
            case "bruger":
                p = new Person(Person.Type.Bruger, "");

                break;
            case "hjælper":
                p = new Person(Person.Type.Hjaelper, "");
                break;
        }
    }

    private String getMyPage(){
        return handler.getPage(res.d.getUrl(), "");
    }

    public void timeRegistrering(Ordning o){

    }

    public Person getPerson(){
        return this.p;

    }

    public void getTimeRegistreringer(Ordning o){ //Dato = nuværende måned og år
        String ordningRegistreringsSide = handler.getPage("/tidsregistrering.aspx" + o.getUrlId(), "");
        //System.out.println(ordning);
        parseTimeRegistreringsSide(ordningRegistreringsSide, o);
    }

    /**
     *
     * @param registreringsSideString
     */
    public void parseTimeRegistreringsSide(String registreringsSideString, Ordning o){
        Document registreringsSide = Jsoup.parse(registreringsSideString);
        Elements registreringer = registreringsSide.body()
                .getElementsByTag("table");
        Elements rows = registreringer.select("tbody").select("tr");
        rows.remove(0);
        //System.out.println("hej hej! |" + rows.get(0).text()+"|");
        if (rows.get(0).text().trim().toLowerCase()
                .equals("der er ingen registreringer for denne periode.")){
            System.out.println("Der er ingen indtastede timer endnu");
        } else {
            parseTimeRegistreringsTabel(registreringsSideString, o);
        }
    }
    public void getHelpers(){
        String ordningRegistreringsSide = handler.getPage("/tidsregistrering.aspx"
                                                          + this.p.getOrdninger().get(0).getUrlId(), "");
        extractHelpers(ordningRegistreringsSide);


    }

    public void extractHelpers(String registreringsSideString){
        Document registreringsSide = Jsoup.parse(registreringsSideString);
        Elements hjaelpere = registreringsSide.body()
                .select("select#udfoert_af")
                .select("option");

        for (Element hjaelper : hjaelpere) {
            Person ansat = new Person(Person.Type.Hjaelper, hjaelper.text());
            ansat.setId(Integer.parseInt(hjaelper.attr("value")));
            this.p.addAnsat(ansat);
            //System.out.println(hjaelper.attr("value") + " = " + hjaelper.text());
        }
    }


    public void getTimeRegistreringer(Ordning o, LocalDate dato){
        String request = "/tidsregistrering.aspx?Ordning=" + o.getId()
                + "&year=" + dato.getYear() + "&month=" + dato.getMonthValue()
                + "&orderBy=Dato&ascending=false&performedBy="
                + this.p.getAnsatte().get(0).getId() + "&onlyShowMine=false";
        parseTimeRegistreringsSide(this.handler.getPage(request, ""), o);
    }

    public void parseTimeRegistreringsTabel(String side, Ordning o){
        Document d = Jsoup.parse(side);
        Elements godkendteTimer = d.select("tr.row.approved"); //Kun når de er godkendte!!!!
        //System.out.println("Der er " + godkendteTimer.size() + " godkendte registreringer");
        for (Element time : godkendteTimer) {
            //System.out.println(time.text());
            Elements values = time.getElementsByTag("td");
            //System.out.println("Antal værdier: " + values.size() + "\n---------");
            String date = values.get(2).text();
            String fromString = date + " " + values.get(3).text();
            String toString = date + " " + values.get(4).text();
            String type = values.get(6).text();
            String author = values.get(7).text();
            String status = values.get(8).text();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy HH:mm"); //HH:mm
            LocalDateTime from = LocalDateTime.parse(fromString, formatter);
            LocalDateTime to = LocalDateTime.parse(toString, formatter);
            TimeRegistrering t = new TimeRegistrering(from, to, author,
                    TimeRegistrering.Status.valueOf(status), TimeRegistrering.TimeType.valueOf(type));
            o.addTimeRegistrering(t);
        }
    }

    public void getOrdninger(){
        String reg = handler.getPage("/tidsregistrering.aspx", "");
        //System.out.println("Tidsregistrering");
        Document doc = Jsoup.parse(reg);
        Elements ordninger = doc.body().getElementById("ordnings_list").getElementsByTag("a");
        //System.out.println(ordninger.size());
        for (Element ordning : ordninger) {
            p.addOrdning(new Ordning(ordning.text(), ordning.attr("href")));
            //System.out.println(ordning.text() + ": " + ordning.attr("href"));
        }

    }



}
