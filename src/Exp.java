import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract class Exp {
}

class MonadExp extends Exp {
    public Exp op;
    public Exp exp;
    public MonadExp(Exp op, Exp exp){
        this.op = op;
        this.exp = exp;
    }
    @Override
    public String toString(){
        return "(m"+op+" "+exp+")";
    }
}

class DyadExp extends Exp {
    public Exp op;
    public Exp left;
    public Exp right;
    public DyadExp(Exp op, Exp l, Exp r){
        this.op = op;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(d"+op+" "+left+" "+right+")";
    }
}

class Adverb extends Exp {
    public Exp func;
    public Exp left;
    public Exp right;
}

class OverExp extends Adverb {
    public OverExp(Exp f,Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(/:"+func+" "+left+" "+right+")";
    }
}

class ScanExp extends Adverb {
    public ScanExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(\\:"+func+" "+left+" "+right+")";
    }
}

class EachExp extends Adverb {
    public EachExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(':"+func+" "+left+" "+right+")";
    }
}

class EachRightExp extends Adverb {
    public EachRightExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(/::"+func+" "+left+" "+right+")";
    }
}

class EachLeftExp extends Adverb {
    public EachLeftExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(\\::"+func+" "+left+" "+right+")";
    }
}

class EachPairExp extends Adverb {
    public EachPairExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "('::" +func+" "+left+" "+right+")";
    }
}

class AssignExp extends Exp {
    public String name;
    public Exp exp;
    public AssignExp(String name, Exp e){
        this.name = name;
        this.exp = e;
    }
    @Override
    public String toString(){
        return "(=:"+name+" "+exp+")";
    }
}

class SymExp extends Exp {
    public String name;
    public SymExp(String s){
        name = s;
    }
    public String toString(){
        return "(Sym:"+name+")";
    }
}

class NounExp extends Exp {
    public ArrayList<Double> val;
    public double single;
    public boolean scalar;
    public NounExp(ArrayList<Double> n){
        val = n;
        scalar = false;
    }
    public NounExp(double d){
        single = d;
        scalar = true;
    }
    public String toString(){
        return "(obj:" + Objects.requireNonNullElseGet(val, () -> single) + ")";
    }
}

class ListExp extends Exp {
    public ArrayList<Exp> val;
    public ListExp(ArrayList<Exp> n){
        val = n;
    }
    public String toString(){
        return "(list:" + val + ")";
    }
}
class OpExp extends Exp {
    public String name;
    public OpExp(String s){
        name = s;
    }
    public String toString(){
        return "(fn:"+name+")";
    }
}

class SymOpExp extends Exp {
    public String name;
    public SymOpExp(String s){
        name = s;
    }
    public String toString(){
        return "(sfn:"+name+")";
    }
}

class FuncExp extends Exp {
    public List<Exp> body;
    String source;
    public FuncExp(List<Exp> b, String s){
        body = b;
        source = s;
    }
    public String toString(){
        return "(afn:{"+body+"})";
    }
}
class FuncCallExp extends Exp {
    public Exp body;
    public List<Exp> args;
    String source;
    public FuncCallExp(Exp b, List<Exp> a){
        body = b;
        args = a;
    }
    public String toString(){
        return "(fncall:"+body+"," + args +")";
    }
}
