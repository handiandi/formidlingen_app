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

    public boolean checkID(YearMonth ym, int id){
        if (!this.timeRegistreringer.containsKey(ym))
            return true;

        for (TimeRegistrering time : this.timeRegistreringer.get(ym)){
            if (time.getId() == id)
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.name + " | " + this.urlId;
    }
}
