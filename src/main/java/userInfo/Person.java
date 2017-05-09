package userInfo;

import java.util.ArrayList;
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
        return this.ordninger.stream().map(Object::toString)
                .collect(Collectors.joining("\n"));

    }

    public String toStringAnsatte() {
        return this.ansatte.stream().map(Object::toString)
                .collect(Collectors.joining("\n"));

    }

}
