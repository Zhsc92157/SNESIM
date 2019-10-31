package Entity;

public class Coords3D {

    private int x;
    private int y;
    private int z;

    public Coords3D(){
        x = 0;
        y = 0;
        z = 0;
    }

    public Coords3D(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
