package userInfo;

/**
 * Created by anders-dev on 5/7/17.
 */
public class Ordning {
    private String name;
    private String urlId;
    private int id;

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

    @Override
    public String toString() {
        return this.name + " | " + this.urlId;
    }
}
