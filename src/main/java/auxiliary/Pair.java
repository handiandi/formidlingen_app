package auxiliary;

/**
 * Created by anders-dev on 5/15/17.
 */
public final class Pair {
    private final int x;
    private final String y;

    public Pair(int x, String y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}