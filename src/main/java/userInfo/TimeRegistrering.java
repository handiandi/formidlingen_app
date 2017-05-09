package userInfo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;

/**
 * Created by anders-dev on 5/9/17.
 */
public class TimeRegistrering {
    private LocalDateTime from;
    private LocalDateTime to;
    private String author;
    private Status status;
    private TimeType type;
    private double antalTimer;

    public enum Status{
        Godkendt, Afvendtende, Afvist
    }

    public enum TimeType{
        Almindelig, Sygdom, Personalemoede,
        BarnSyg, Foelgevagt, Puljetimer, MUSsamtale
    }
    public TimeRegistrering(LocalDateTime from, LocalDateTime to,
                            String author, Status status, TimeType type){
        this.from = from;
        this.to = to;
        this.author = author;
        this.status = status;
        this.type = type;
        this.antalTimer = calcAntalTimer(from, to);

    }

    private double calcAntalTimer(LocalDateTime from, LocalDateTime to){
        double m = ChronoUnit.MINUTES.between(from, to);
        int timer = (int) m/60;
        int mLeft = (int) m-(timer*60);
        double t = 1.0/(60.0/(double)mLeft);
        return (double) timer + t;

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

    public String getAuthor() {
        return this.author;
    }

    public TimeType getType() {
        return this.type;
    }
}
