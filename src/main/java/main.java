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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;

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
            test[i][0] = lines[i].split("##")[0];
            test[i][1] = lines[i].split("##")[1];
        }





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

        System.out.println("######");
        YearMonth ym = YearMonth.of(2017, 4);
        System.out.println(bhf.getPerson().getOrdningById(6523).getTimeRegistreringer(ym).size());
     

    } //main
} //class