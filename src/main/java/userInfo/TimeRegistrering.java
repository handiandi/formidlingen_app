package userInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by anders-dev on 5/9/17.
 */
public class TimeRegistrering {
    private int id;
    private LocalDateTime from;
    private LocalDateTime to;
    private String ansat;
    private String author;
    private Status status;
    private TimeType type;
    private double antalTimer;
    private static final Map<Integer, TimeType> intToTimeType
            = new HashMap<Integer, TimeType>();

    static {
        for (TimeType type : TimeType.values()){
            intToTimeType.put(type.value, type);
        }
    }

    public enum Status{
        Ukendt, Accepteret, Afventer, Afvist, Godkendt, Ny; //Ny = nyoprettet, klar til at blive gemt/indtastet
    }

    public enum TimeType{
        Ukendt(-1), Almindelig(0), Sygdom(1), Personalemoede(2),
        BarnSyg(5), Foelgevagt(9), Puljetimer(10), MUSsamtale(13);

        private final int value;

        TimeType(int value){
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }



    public static class Builder{
        //Required parameters

        private final LocalDateTime from;
        private final LocalDateTime to;
        private final String ansat;

        //Optional parameters
        private int id ;
        private String author = "";
        private Status status = Status.Ukendt;
        private TimeType type = TimeType.Ukendt;
        private double antalTimer = 0.0;

        public Builder(LocalDateTime from, LocalDateTime to, String ansat){
            this.from = from;
            this.to = to;
            this.ansat = ansat;
        }

        public Builder author(String author){this.author = author; return this;}
        public Builder status(Status status){this.status = status; return this;}
        public Builder type(TimeType type){this.type = type; return this;}
        public Builder id(int id){this.id = id; return this;}

        public TimeRegistrering build(){
            return new TimeRegistrering(this);
        }
    } //end Builder class
    private TimeRegistrering(Builder builder){
        this.id = this.generateId(builder.id);
        this.from = builder.from;
        this.to = builder.to;
        this.ansat = builder.ansat;
        this.status = builder.status;
        this.type = builder.type;
        this.author = builder.author;
        this.antalTimer = calcAntalTimer(this.from, this.to);

    }


    private int generateId(int id){
        if (id != 0)
            return id;
        return ThreadLocalRandom.current().nextInt(-10*(int)Math.pow(10.0,6.0), 0);
    }
    private double calcAntalTimer(LocalDateTime from, LocalDateTime to){
        double m = ChronoUnit.MINUTES.between(from, to);
        int timer = (int) m/60;
        int mLeft = (int) m-(timer*60);
        double t = 1.0/(60.0/(double)mLeft);
        return (double) timer + t;

    }

    public TimeType getTimeTypeFromInt(int i){
        TimeType type = intToTimeType.get(Integer.valueOf(i));
        if (type == null)
            return TimeType.Ukendt;
        return type;
    }

    public YearMonth getYearMonth(){
        return YearMonth.from(this.from);
    }
    public LocalDateTime getFrom(){
        return this.from;
    }

    public double getAntalTimer() {
        return this.antalTimer;
    }

    public LocalDateTime getTo() {
        return this.to;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAnsat() {
        return this.ansat;
    }

    public TimeType getType() {
        return this.type;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return  this.id + "\t " + this.status + " \t " + this.ansat + " \t" + this.author + "\t " +this.type +"\t "
                + this.from.toString() + "\t" + this.to.toString() + " \t" +this.antalTimer + "";
    }
}
