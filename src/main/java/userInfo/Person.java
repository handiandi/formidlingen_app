package userInfo;

import automatization.Rule;
import auxiliary.Pair;

import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by anders-dev on 5/6/17.
 */
public class Person {
    private Type type;
    private String name = "";
    private String email = "";
    private int id;
    private ArrayList<Ordning> ordninger = new ArrayList<>();
    private ArrayList<Person> ansatte = new ArrayList<>();
    private ArrayList<Rule> rules = new ArrayList<>();


    public enum Type{
        Bruger, Hjaelper
    }

    public Person(Type type, String name){
        this.type = type;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOrdninger(ArrayList<Ordning> ordninger) {
        this.ordninger = ordninger;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Ordning> getOrdninger() {
        return this.ordninger;
    }


    public void setAnsatte(ArrayList<Person> ansatte){
        this.ansatte = ansatte;
    }

    public ArrayList<Person> getAnsatte() {
        return ansatte;
    }

    public int getId() {
        return id;
    }

    public void addAnsat(Person ansat){
        if (findAnsatByName(ansat.getName()) == null) {
            this.ansatte.add(ansat);
        }
    }

    public void addOrdning(Ordning ordning) {
        this.ordninger.add(ordning);
    }

    public void addOrdning(String name, String urlId) {
        if (getOrdningByName(name) == null) {
            this.ordninger.add(new Ordning(name, urlId));
        }
    }

    public Ordning getOrdningByName(String name){
        for (Ordning o : this.ordninger) {
            if (o.getName().toLowerCase().equals(name.toLowerCase())){
                return o;
            }
        }
        return null;
    }

    public Ordning getOrdningByUrlId(String urlId){
        for (Ordning o : this.ordninger) {
            if (o.getUrlId().toLowerCase().equals(urlId.toLowerCase())){
                return o;
            }
        }
        return null;
    }

    public Ordning getOrdningById(int id){
        for (Ordning o : this.ordninger) {
            if (o.getId() == id){
                return o;
            }
        }
        return null;
    }

    public void setRules(ArrayList<Rule> rules){
        this.rules = rules;
    }

    public void addRule(Rule rule){
        this.rules.add(rule);

    }

    public void checkRules(int ordningsID, YearMonth ym){
        Ordning o = this.getOrdningById(ordningsID);
        ArrayList<TimeRegistrering> t = o.getTimeRegistreringer(ym);

        HashMap<Pair, Double> weekAccum = new HashMap<>();
        HashMap<String, Double> monthAccum = new HashMap<>();

        for (Rule r : this.rules) {
            Rule.AccumType tt = r.getAccumType();
            for (TimeRegistrering reg : t) {
                switch (tt){
                    case Weekly:
                        weekAccum = this.calcWeeklyRule(weekAccum, reg);

                        break;

                    case Monthly:
                        monthAccum = this.calcMonthlyRule(monthAccum, reg);
                        break;

                    default:
                        break;
                }

            } //for-end
            System.out.println("antal = " + weekAccum.keySet().size());
            for (Pair p : weekAccum.keySet()) {
                System.out.println("Week = " + p.getX()
                        + ", string = " + p.getY() + ". value = " + weekAccum.get(p));
            }
            double limit = r.getLimit();
            double accepteretSum = 0.0;
            double afventerSum = 0.0;
            switch (tt){
                case Weekly:
                    int highestWeekNumber = weekAccum.keySet().stream()
                            .max(Comparator.comparing(Pair::getX)).get().getX();
                    int lowestWeekNumber = weekAccum.keySet().stream()
                            .min(Comparator.comparing(Pair::getX)).get().getX();

                    System.out.println("highestWeekNumber = " + highestWeekNumber);
                    System.out.println("lowestWeekNumber = " + lowestWeekNumber);
                    ArrayList<Double> accepteret = new ArrayList<>();
                    ArrayList<Double> afventer = new ArrayList<>();
                    for (Pair p : weekAccum.keySet()) {
                        if (p.getY() == "Accepteret"){
                            accepteret.add(weekAccum.get(p));
                        }
                        if (p.getY() == "Afventer"){
                            accepteret.add(weekAccum.get(p));
                        }

                    }

                    accepteretSum = accepteret.stream().reduce(0.0, Double::sum);
                    afventerSum = afventer.stream().reduce(0.0, Double::sum);
                    System.out.println("accepteretSum = " + accepteretSum);
                    System.out.println("afventerSum = " + afventerSum);
                    if (accepteretSum > limit){
                        System.out.println("Der er flere timer end reglen siger, men timerne er godkendt, så der er ikke noget at gøre");

                        System.out.println("Reglen sagde: " + r.toString());
                    }
                    break;

                case Monthly:
                    accepteretSum = monthAccum.getOrDefault("Accepteret", 0.0);
                    afventerSum = monthAccum.getOrDefault("Afventer", 0.0);
                    System.out.println("accepteretSum = " + accepteretSum);
                    System.out.println("afventerSum = " + afventerSum);
                    if (accepteretSum > limit){
                        System.out.println("Der er flere timer end reglen siger, men timerne er godkendt, så der er ikke noget at gøre");
                        System.out.println("Reglen sagde: " + r.toString());
                    } else{
                        System.out.println("Der er " + String.valueOf(limit-accepteretSum) + " timer før grænsen er nået");
                        System.out.println("Reglen sagde: " + r.toString());
                    }
                    //monthAccum = this.calcMonthlyRule(monthAccum, reg);
                    break;

                default:
                    break;
            }



        } //for-end
    }

    private HashMap<Pair, Double> calcWeeklyRule(HashMap<Pair, Double> weeklyAccum, TimeRegistrering reg){
        int weekNumber = reg.getFrom().get(WeekFields.ISO.weekOfWeekBasedYear());
        switch (reg.getStatus()){
            case Afvist:
                break;

            case Afventer:
                weeklyAccum.put(new Pair(weekNumber, "Afventer"),
                        weeklyAccum.getOrDefault(new Pair(weekNumber, "Afventer"), 0.0)+reg.getAntalTimer());
                break;
            case Ukendt:
                break;

            case Godkendt:
            case Accepteret:
                weeklyAccum.put(new Pair(weekNumber, "Accepteret"),
                        weeklyAccum.getOrDefault(new Pair(weekNumber, "Accepteret"), 0.0)+reg.getAntalTimer());
                break;

            default:
                break;
        }
        return weeklyAccum;
    }

    private HashMap<String, Double> calcMonthlyRule(HashMap<String, Double> monthAccum, TimeRegistrering reg){
        switch (reg.getStatus()){
            case Afvist:
                break;

            case Afventer:
                monthAccum.put("Afventer", monthAccum.getOrDefault("Afventer", 0.0)+reg.getAntalTimer());
                break;
            case Ukendt:
                break;

            case Godkendt:
            case Accepteret:
                monthAccum.put("Accepteret", monthAccum.getOrDefault("Accepteret", 0.0)+reg.getAntalTimer());
                break;

            default:
                break;
        }
        return monthAccum;
    }

    @Override
    public String toString() {
        String s = this.name;
        if (!this.email.equals(""))
            s += " (" + this.getEmail() + ") - " + this.type;//super.toString();
        else
            s += " - " + this.type;
        if (this.id != 0)
            s += " ("+ this.id + ")";
        return s;
    }

    public Person findAnsatByName(String name){
        for (Person ansat : this.ansatte) {
            if (ansat.getName().toLowerCase().equals(name.toLowerCase())){
                return ansat;
            }
        }
        return null;
    }

    public String toStringOrdninger() {
        if (this.ordninger.size() == 0)
            return "Ingen ordninger";
        return this.ordninger.stream().map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    public String toStringAnsatte() {
        if (this.ansatte.size() == 0)
            return "Ingen ansatte";
        return this.ansatte.stream().map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

}
