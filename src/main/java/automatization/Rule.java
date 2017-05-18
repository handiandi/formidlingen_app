package automatization;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by anders-dev on 5/13/17.
 */
public class Rule {
    private LocalTime from;
    private LocalTime to;
    private AccumType accumType;
    private ArrayList<DayOfWeek> days = new ArrayList<>();
    private double limit;

    public enum AccumType{
        Weekly, Monthly,

    }

    public static class Builder{
        //Required parameters
        private final LocalTime from;
        private final LocalTime to;
        private final AccumType accumType;
        private final double limit;

        //Optional parameters
        private ArrayList<DayOfWeek> days = new ArrayList<>();


        public Builder(LocalTime from, LocalTime to, AccumType accumType, double limit){
            this.from = from;
            this.to = to;
            this.accumType = accumType;
            this.limit = limit;
        }



        public Builder addDay(DayOfWeek day){
            if (!this.days.contains(day)){
                this.days.add(day);
            }
            return this;
        }


        public Rule build(){
            return new Rule(this);
        }
    } //end Builder class

    public Rule(Builder builder){
        this.accumType = builder.accumType;
        this.days = builder.days;
        this.from = builder.from;
        this.to = builder.to;
        this.limit = builder.limit;
    }

    public AccumType getAccumType() {
        return accumType;
    }

    public ArrayList<DayOfWeek> getDays() {
        return days;
    }

    public LocalTime getFrom() {
        return from;
    }

    public LocalTime getTo() {
        return to;
    }

    public double getLimit() {
        return limit;
    }

    @Override
    public String toString() {
        String s = this.limit + " timer";

        if (this.getDays().size() == 0){
            s += " over alle dage";
        } else {
            s += " over " + this.getDays().toString();
        }
        if (this.getAccumType() == Rule.AccumType.Monthly){
            s += " henover hele måneden";
        } else {
            s += " pr. uge";
        }
        return s;
    }
}
