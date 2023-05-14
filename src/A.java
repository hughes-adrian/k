import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

enum Type {
    INT,DOUBLE,ILIST,DLIST,FUNC
}

public class A {
    public int[] ilist;
    public double[] flist;
    public char[] clist;
    public int isingle;
    public double fsingle;
    public boolean scalar;
    public List<Exp> fbody;
    public Type type;

    public A(ArrayList<Integer> n){
        ilist = n.stream().mapToInt(i -> i).toArray();
        scalar = false;
        type = Type.ILIST;
    }
    public A(int[] n){
        ilist = n;
        scalar = false;
        type = Type.ILIST;
    }
    public A(int d){
        isingle = d;
        scalar = true;
        type = Type.INT;
    }
    public A(List<Exp> b){
        fbody = b;
    }
    public String toString(){
        return "(obj:" +
                Objects.requireNonNullElseGet(ilist, () -> isingle) +
                ", type: " + type + ")";
    }
    public A get(){
        return this;
    }
}
