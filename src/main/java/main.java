//package main.java;

import automatization.Rule;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import siteHandler.Formidlingen;
import siteHandler.PageHandler;
import userInfo.Ordning;
import userInfo.TimeRegistrering;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class main {
    public static void main(String[] args) {
        PageHandler handler = new PageHandler("");
        //Formidlingen bhf = new Formidlingen();
        Formidlingen bhf = Formidlingen.INSTANCE;
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
            test[i][0] = lines[i].split("##")[0];
            test[i][1] = lines[i].split("##")[1];
        }





        if(!bhf.go(test[0][0], test[0][1])){
            throw new RuntimeException("Kunne ikke logge ind");

        }

        System.out.println("Navn: " + bhf.getPerson().getName());
        System.out.println("Email: " + bhf.getPerson().getEmail());
        System.out.println("To-string: " + bhf.getPerson().toString());



        //bhf.getOrdninger();
        System.out.println("Ordninger toString = \n"
                +bhf.getPerson().toStringOrdninger());

        bhf.getTimeRegistreringer(bhf.getPerson().getOrdningById(6523));

        //bhf.getHelpers();
        System.out.println(bhf.getPerson().getName() + " har følgende hjælpere:");
        System.out.println(bhf.getPerson().toStringAnsatte());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM-yyyy"); //HH:mm
        LocalDate date = LocalDate.parse("09/04-2017", formatter);
        bhf.getTimeRegistreringer(bhf.getPerson().getOrdningById(6523), date);

        System.out.println("######");
        YearMonth ym = YearMonth.of(2017, 4);
        System.out.println(bhf.getPerson().getOrdningById(6523).getTimeRegistreringer(ym).size());

        for (TimeRegistrering r : bhf.getPerson().getOrdningById(6523).getTimeRegistreringer(ym)) {
            System.out.println(r.getAntalTimer());

        }

        System.out.println("\n\n---------- Rule Tester ------------");
        YearMonth ym0 = YearMonth.of(2017, 5);
        LocalTime from = LocalTime.of(0, 0);
        LocalTime to = LocalTime.of(23, 59);
        Rule r = new Rule.Builder(from, to, Rule.AccumType.Weekly, 20.0).build();
        Rule r2 = new Rule.Builder(from, to, Rule.AccumType.Monthly, 80.0).addDay(DayOfWeek.FRIDAY).addDay(DayOfWeek.MONDAY).build();

        bhf.getPerson().addRule(r2);
        bhf.getPerson().checkRules(6523, ym0);


        System.out.println("\n\n---------- Create new TimeRegistrering  ------------");
        
        YearMonth ym2 = YearMonth.of(2017, 5);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("d-MM-yyyy HH:mm"); //HH:mm
        LocalDateTime from1 = LocalDateTime.parse("12-05-2017 10:00", formatter2);
        LocalDateTime to1 = LocalDateTime.parse("12-05-2017 15:00", formatter2);
        TimeRegistrering time = new TimeRegistrering.Builder(from1, to1, bhf.getPerson().getName())
                .status(TimeRegistrering.Status.Ny).type(TimeRegistrering.TimeType.Almindelig).build();
        LocalDateTime from2 = LocalDateTime.parse("3-05-2017 10:00", formatter2);
        LocalDateTime to2 = LocalDateTime.parse("3-05-2017 15:00", formatter2);
        TimeRegistrering time2 = new TimeRegistrering.Builder(from2, to2, bhf.getPerson().getName())
                .status(TimeRegistrering.Status.Ny).type(TimeRegistrering.TimeType.Almindelig).build();
        bhf.getPerson().getOrdningById(6523).addTimeRegistrering(time);
        bhf.getPerson().getOrdningById(6523).addTimeRegistrering(time2);
        System.out.println("Antal nye timer: " + bhf.getPerson().getOrdningById(6523).getNewCreated(ym2).size());
        System.out.println("Ny time id: " + bhf.getPerson().getOrdningById(6523).getNewCreated(ym2).get(0).getId());
        System.out.println("Ny time id: " + bhf.getPerson().getOrdningById(6523).getNewCreated(ym2).get(1).getId());
        System.out.println(-10* (int)Math.pow(10.0,6.0));


        System.out.println("\n\n---------- Save the new TimeRegistreringer to website ------------");
        bhf.saveTimer(6523, ym2);






    } //main
} //class