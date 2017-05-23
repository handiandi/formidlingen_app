package siteHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anders-dev on 5/6/17.
 */
public enum  Formidlingen {
    INSTANCE;
    private PageHandler handler;
    private Response res;
    private Person p;


    Formidlingen(){
        handler = new PageHandler("https://www.formidlingen.dk");
    }


    /**
     * This method logs in on formidlingen.dk with a given email and password
     * @param email - Email for login
     * @param password - Password for login
     * @return The response as JSON
     */
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

    /**
     * This method try to login, create the user and update the user.
     * This method call multiple other methods.
     * @param email - Email for login
     * @param password - Password for login
     * @return Boolean whether the login was successful or not
     */
    public boolean go(String email, String password){
        JSONObject result = login(email, password);
        if (!result.has("d")){
            return false; //noget gik helt galt
        } else {
            Gson g = new Gson();
            res = g.fromJson(result.toString(), Response.class);
            if (!res.d.Authenticated){
                return false;
            }
            String page = getMyPage();
            createUser(extractName(page));
            return res.d.getAuthenticated(); //Kaldet var rigtigt. True/False om login er rigtigt
        } //if-else
    }

    /**
     * This method updates the user with name, by extracting it from the html-page after successful login
     * @param page - HTML-page as a string
     */
    private String extractName(String page) {
        Document html = Jsoup.parse(page);
        Element a = html.select("li.last.user-ico").select("a").get(0);
        return a.text().split("-")[1].trim();
    }


    /**
     * This method creates the user
     */
    private void createUser(String name) {
        switch (res.d.getType().toLowerCase()){
            case "bruger":
                p = new Person(Person.Type.Bruger, name);
                extractOrdninger();
                extractHelpers();
                break;
            case "hjælper":
                p = new Person(Person.Type.Hjaelper, name);
                extractOrdninger();
                break;
        }
    }

    private String getMyPage(){
        return handler.getPage(res.d.getUrl(), "");
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
    private void parseTimeRegistreringsSide(String registreringsSideString, Ordning o){
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


    private void extractHelpers(){
        String registreringsSideString = handler.getPage("/tidsregistrering.aspx"
                + this.p.getOrdninger().get(0).getUrlId(), "");
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
                        + "&orderBy=Dato&ascending=false";
        if (this.p.getType() == Person.Type.Bruger) {
                    request += "&performedBy=" + this.p.getAnsatte().get(0).getId();
        }
        request += "&onlyShowMine=false";
        parseTimeRegistreringsSide(this.handler.getPage(request, ""), o);
    }

    private void parseTimeRegistreringsTabel(String side, Ordning o){
        Document d = Jsoup.parse(side);
        Elements allRow = d.select("tr.row");
        Elements row_details = d.select("tr.details_row");
        //System.out.println("parseTimeRegistreringsTabel - Antal rows: " + allRow.size());
        ArrayList<TimeRegistrering> timeRegistreringArrayList = new ArrayList<>();
        for (int i=0; i<allRow.size(); i++) {
            Elements values = allRow.get(i).getElementsByTag("td");
            String date = values.get(2).text();
            String fromString = date + " " + values.get(3).text();
            String toString = date + " " + values.get(4).text();
            String type = values.get(6).text();
            String ansat = values.get(7).text();
            String status = values.get(8).text();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy HH:mm"); //HH:mm
            LocalDateTime from = LocalDateTime.parse(fromString, formatter);
            LocalDateTime to = LocalDateTime.parse(toString, formatter);
            int id = Integer.parseInt(row_details.get(i)
                    .select("div.details > input")
                    .attr("value"));

            Elements detailLines = row_details.get(i).select("div.detail_line");
            String author = "";
            for (Element line : detailLines) {
                String text = line.select("div.text").text();
                if (text.toLowerCase().contains("indtastet af")){
                    author = text.split(":")[1].trim();
                }
            }
            TimeRegistrering t = new TimeRegistrering.Builder(from, to, ansat)
                    .id(id)
                    .author(author)
                    .status(TimeRegistrering.Status.valueOf(status))
                    .type(TimeRegistrering.TimeType.valueOf(type))
                    .build();

           timeRegistreringArrayList.add(t);
        }
        o.setTimeRegistreringer(timeRegistreringArrayList.get(0).getYearMonth(), timeRegistreringArrayList);
    }

    public void extractOrdninger(){
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



    public void saveTimer(int ordningsId, YearMonth ym){
        ArrayList<TimeRegistrering> timer = this.p.getOrdningById(ordningsId).getNewCreated(ym);
        this.saveTimer(ordningsId, timer);
    }

    public void saveTimer(int ordningsId, ArrayList<TimeRegistrering> timer){
        String requestUrl = "https://www.formidlingen.dk/usercontrols/registerwork/TimeregistreringService.asmx/Create";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Accept-Language", "da-DK,da;q=0.8,en-US;q=0.6,en;q=0.4");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Origin", "https://www.formidlingen.dk");

        DateTimeFormatter datesFormatter = DateTimeFormatter.ofPattern("\"dd/MM/yyyy\"");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("\"HH:mm\"");
        Gson gson = new GsonBuilder().serializeNulls().create(); //.setPrettyPrinting()
        JsonParser jp = new JsonParser();
        String jsonPayload = "";
        for (TimeRegistrering time : timer) {
            jsonPayload += "{ordningsnr: " + ordningsId + ", "
                    + "vagtType: \"" + time.getType().getValue() + "\", "
                    + "dates: " + "["+time.getFrom().format(datesFormatter) + "], "
                    + "from: " + time.getFrom().format(timeFormatter) + ", "
                    + "to: " + time.getTo().format(timeFormatter) + ", "
                    + "performedBy: null, "
                    + "comment: \"\"}";

            JsonElement je = jp.parse(jsonPayload);
            System.out.println("Payload = \n" + gson.toJson(je));
            System.out.println();
            jsonPayload = "";

            handler.postAsJson(requestUrl, gson.toJson(je), headers);

        }
        Ordning o = this.p.getOrdningById(ordningsId);
        this.getTimeRegistreringer(o, timer.get(0).getFrom().toLocalDate());
    }

    public void deleteTimer(int ordningsId, ArrayList<TimeRegistrering> timer){
        String requestUrl = "https://www.formidlingen.dk/usercontrols/registerwork/TimeregistreringService.asmx/Delete";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Accept-Language", "da-DK,da;q=0.8,en-US;q=0.6,en;q=0.4");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Origin", "https://www.formidlingen.dk");

        Gson gson = new GsonBuilder().serializeNulls().create(); //.setPrettyPrinting()
        JsonParser jp = new JsonParser();
        String jsonPayload = "";
        for (TimeRegistrering time : timer) {
            jsonPayload += "{timeregistreringsId: \"" + time.getId()+"\"}";

            JsonElement je = jp.parse(jsonPayload);
            System.out.println("Delete Payload = \n" + gson.toJson(je));
            System.out.println();
            jsonPayload = "";

            handler.postAsJson(requestUrl, gson.toJson(je), headers);
        }
        Ordning o = this.p.getOrdningById(ordningsId);
        this.getTimeRegistreringer(o, timer.get(0).getFrom().toLocalDate());
    }



}

