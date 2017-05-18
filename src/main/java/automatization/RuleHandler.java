package automatization;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by anders-dev on 5/13/17.
 */
public class RuleHandler {

    /**
     * The method checks if a new rule violates with one or more
     * of already made rules.
     * @param rules - The list with already made rules
     * @param rule - The new rule
     */
    public void checkViolation(ArrayList<Rule> rules, Rule rule){
        for (Rule r : rules) {
            if (this.checkViolation(r, rule)){
                System.out.println("Rule violation!!");
            }
        }
    }


    /**
     * Checks if two rules violates
     * @param rule1 - Rule number 1
     * @param rule2 - Rule number 2
     */
    private boolean checkViolation(Rule rule1, Rule rule2){
        List<DayOfWeek> intersectDays = rule1.getDays().stream()
                .filter(rule2.getDays()::contains)
                .collect(Collectors.toList());

        if (intersectDays.size() == rule1.getDays().size() &&
                intersectDays.size() == rule2.getDays().size()){

            if (this.overlaps(rule1.getFrom(), rule1.getTo(), rule2.getFrom(), rule2.getTo())){
                System.out.println("Fejl. De har ens dage og overlappende tider!");
                return false;
            } //end-if
        } //end-if
        return true;
    }


    public void checkViolation(ArrayList<Rule> rules){
        for (int i = 0; i<rules.size()-1; i++){
            for (int j=i+1; j<rules.size(); j++){
                this.checkViolation(rules.get(i), rules.get(j));
            }
        }


    }

    public boolean overlaps(LocalTime t1From, LocalTime t1To, LocalTime t2From, LocalTime t2To) {
        return isBetween(t1From, t2From, t2To)
                || isBetween(t1To, t2From, t2To)
                || isBetween(t2From, t1From, t1To)
                || isBetween(t2To, t1From, t1To);
    }

    private static boolean isBetween(LocalTime t, LocalTime from, LocalTime to) {
        if (from.isBefore(to)) { // same day
            return from.isBefore(t) && t.isBefore(to);
        } else { // spans to the next day.
            return from.isBefore(t) || t.isBefore(to);
        }
    }

}
