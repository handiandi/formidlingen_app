//package main.java;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import siteHandler.Formidlingen;
import siteHandler.PageHandler;
import userInfo.Ordning;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class main {
    public static void main(String[] args) {
        PageHandler handler = new PageHandler("");
        Formidlingen bhf = new Formidlingen();
        //Loading login from file
        String loginInfo = "";
        try(FileInputStream inputStream = new FileInputStream("src/main/java/login.txt")) {
            loginInfo = IOUtils.toString(inputStream);
        } catch (IOException e ){
            System.out.println("Noget gik galt = " + e.getMessage());
        }
        String[][] test = new String[loginInfo.split("\\r?\\n").length][2];
        String[] lines = loginInfo.split("\\r?\\n");

        for(int i = 0; i<loginInfo.split("\\r?\\n").length; i++){
            System.out.println(i);
            System.out.println(lines[i]);
            test[i][0] = lines[i].split("##")[0];
            test[i][1] = lines[i].split("##")[1];
        }

        System.exit(1);

        if(!bhf.go(test[0][0], test[0][1])){
            throw new RuntimeException("Kunne ikke logge ind");

        }

        System.out.println("Navn: " + bhf.getPerson().getName());
        System.out.println("Email: " + bhf.getPerson().getEmail());
        System.out.println("To-string: " + bhf.getPerson().toString());



        bhf.getOrdninger();
        System.out.println("Ordninger toString = \n"
                +bhf.getPerson().toStringOrdninger());

        bhf.getTimeRegistreringer(bhf.getPerson().getOrdningById(6523));

        bhf.getHelpers();
        System.out.println(bhf.getPerson().getName() + " har følgende hjælpere:");
        System.out.println(bhf.getPerson().toStringAnsatte());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM-yyyy"); //HH:mm
        LocalDate date = LocalDate.parse("09/04-2017", formatter);
        bhf.getTimeRegistreringer(bhf.getPerson().getOrdningById(6523), date);
        System.exit(1);




        String ordningRegistreringsSide = handler.getPage("/tidsregistrering.aspx", "");
        //System.out.println(ordning);
        Document registreringsSide = Jsoup.parse(ordningRegistreringsSide);
        Elements registreringer = registreringsSide.body()
                .getElementsByTag("table");
        Elements rows = registreringer.select("tbody").select("tr");
        rows.remove(0);
        //System.out.println("hej hej! |" + rows.get(0).text()+"|");
        if (rows.get(0).text().trim().toLowerCase()
                .equals("der er ingen registreringer for denne periode.")){
            System.out.println("Der er ingen indtastede timer endnu");
        }


        int i = 0;
        //for (Element row: rows) {
        //    System.out.println(row.text() + i);
        //    i++;
        //}
        //System.out.println(registreringer.size());


        //Find hjælpere
        System.out.println("Finder hjælpere");
        Elements hjaelpere = registreringsSide.body()
                                              .select("select#udfoert_af")
                                              .select("option");

        for (Element hjaelper : hjaelpere) {
            System.out.println(hjaelper.attr("value") + " = " + hjaelper.text());
        }

        //Finder indtastede timer for en anden måned
        int year = 2017;
        int month = 4;
        //String ordningNumber = ordninger.get(0).attr("href").split("=")[1];
        String ordningNumber = "";
        System.out.println("ordningNumber = " + ordningNumber);
        // https://www.formidlingen.dk/tidsregistrering.aspx?Ordning=6523&year=2017&month=4&orderBy=Dato&ascending=false&performedBy=105103&onlyShowMine=false
        String urlPage = "/tidsregistrering.aspx?" +
                         "Ordning=" + ordningNumber +
                         "&year="+year+"&month="+month+"&orderBy=Dato&ascending=false" +
                         "&performedBy="+hjaelpere.get(2).attr("value") +
                         "&onlyShowMine=false";
        System.out.println(urlPage);
        System.out.println("---------------------------");
        String r = handler.getPage(urlPage, "");
        Document d = Jsoup.parse(r);
        rows = d.select("tr.row.approved"); //Kun når de er godkendte!!!!
        for (Element row : rows) {
            System.out.println(row.text());
            Elements tds = row.getElementsByTag("td");
            for (Element td : tds) {
                System.out.println("\t"+td.text());
            }

        }

    } //main
} //class