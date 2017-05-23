package userInfo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by anders-dev on 5/7/17.
 */
public class Ordning {
    private String name;
    private String urlId;
    private int id;
    //          Måned (ikke dato)
    private HashMap<YearMonth, ArrayList<TimeRegistrering>> timeRegistreringer = new HashMap<>();
    //private ArrayList<TimeRegistrering> timeRegistreringer = new ArrayList<>();

    public Ordning(String name, String urlId){
        this.name = name;
        this.urlId = urlId;
        this.id = Integer.parseInt(urlId.split("=")[1]);
    }

    public String getName() {
        return name;
    }

    public String getUrlId() {
        return urlId;
    }

    public int getId(){
        return this.id;
    }

    public void addTimeRegistrering(TimeRegistrering timer){
        YearMonth monthYear = timer.getYearMonth();
        if (this.timeRegistreringer.containsKey(monthYear)){
            this.timeRegistreringer.get(monthYear).add(timer);
        } else{
            ArrayList<TimeRegistrering> temp = new ArrayList<>();
            temp.add(timer);
            this.timeRegistreringer.put(monthYear, temp);
        }
    }

    public void setTimeRegistreringer(YearMonth ym, ArrayList<TimeRegistrering> timer){
        this.timeRegistreringer.put(ym, timer);
    }


    public ArrayList<TimeRegistrering> getTimeRegistreringer(YearMonth yearMonth) {
        if (this.timeRegistreringer.containsKey(yearMonth)) {
            return this.timeRegistreringer.get(yearMonth);
        }
        return null;
    }

    public ArrayList<TimeRegistrering> getNewCreated(YearMonth yearMonth){
        ArrayList<TimeRegistrering> newCreated = new ArrayList<>();
        if (!this.timeRegistreringer.containsKey(yearMonth))
            return newCreated;

        for (TimeRegistrering time : this.timeRegistreringer.get(yearMonth)){
            if (time.getStatus() == TimeRegistrering.Status.Ny)
                newCreated.add(time);
        }
        return newCreated;
    }



    @Override
    public String toString() {
        return this.name + " | " + this.urlId;
    }

    public String toStringTimeregistreringer(YearMonth ym){
        String s = "\tID\t\tStatus\t\t\t\tHjælper\t\t\t\t\tIndskriver\t\t\t\tType\t\t\tFra\t\t\t\t\tTil\t\t\tTimer\n"
                + "--------------------------------------------------------------------------------------------------" +
                "---------------------------------------------\n";
        for (TimeRegistrering t : this.timeRegistreringer.get(ym)) {
            s += t.toString() + "\n";
        }
        return s;
        //this.id + "\t " + this.status + " \t " + this.ansat + " \t" + this.author + "\t " +this.type +"\t "
        //+ this.from.toString() + "\t" + this.to.toString() + " \t" +this.antalTimer + "";
    }
}
