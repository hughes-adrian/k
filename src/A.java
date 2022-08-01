import java.util.ArrayList;
import java.util.Objects;

enum Type {
    INT,DOUBLE,ILIST,DLIST
}

public class A {
    public ArrayList<Integer> val;
    public int single;
    public boolean scalar;
    public Type type;

    public A(ArrayList<Integer> n){
        val = n;
        scalar = false;
        type = Type.ILIST;
    }
    public A(int d){
        single = d;
        scalar = true;
        type = Type.INT;
    }
    public String toString(){
        return "(obj:" +
                Objects.requireNonNullElseGet(val, () -> single) +
                ", type: " + type + ")";
    }
}
