package siteHandler;

/**
 * Created by anders-dev on 5/4/17.
 */
public class Authentication {
    String __type;
    Boolean Authenticated;
    String Url;
    String Type;
    String ForceRedirect;

    public Boolean getAuthenticated() {
        return Authenticated;
    }

    public String getUrl() {
        return this.Url;
    }

    public String getType() {
        return this.Type;
    }
}
