import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

sealed abstract class Exp {
    interface Visitor<R> {
        R visitDyadExp(DyadExp expr);
        R visitMonadExp(MonadExp expr);
        R visitNounExp(NounExp expr);
        R visitAdverb(Adverb expr);
        R visitAssignExp(AssignExp expr);
        R visitEachExp(EachExp expr);
        R visitEachLeftExp(EachLeftExp expr);
        R visitEachPairExp(EachPairExp expr);
        R visitEachRightExp(EachRightExp expr);
        R visitFuncCallExp(FuncCallExp expr);
        R visitFuncExp(FuncExp expr);
        R visitListExp(ListExp expr);
        R visitOverExp(OverExp expr);
        R visitScanExp(ScanExp expr);
        R visitSymExp(SymExp expr);
        R visitSymOpExp(SymOpExp expr);
        R visitOpExp(OpExp expr);
    }
    abstract <R> R accept(Visitor<R> visitor);
    Type type;
    protected Type typeCheck(Exp op, Exp exp){
        return Type.INT;
    }
    protected Type typeCheck(Exp op, Exp exp, Map<String,Type> symbol){
        Type type = null;
        if (symbol.containsKey(op)){
            type = symbol.get(op);
        }
        return type;
    }
    protected Type typeCheck(Exp op, Exp l, Exp r){
        Type t;
        switch(op){
            case OpExp o -> {
                System.out.println(o.name);
                switch (o.name) {
                    case "+" -> {
                        if (l.type == Type.ILIST || r.type == Type.ILIST) {
                            t = Type.ILIST;
                        } else {
                            t = Type.INT;
                        }
                    }
                    default -> t = Type.INT;
                }
            }
            case SymOpExp s -> {
                t = Type.INT;
            }
            case FuncExp f -> {
                t = type.FUNC;
            }
            default -> throw new IllegalStateException("Unexpected value: " + op);
        };
        return t;
    }
}

final class MonadExp extends Exp {
    public Exp op;
    public Exp exp;
    public MonadExp(Exp op, Exp exp) {
        this.op = op;
        this.exp = exp;
        if (this.exp != null) {
            this.type = switch (this.op) {
                case OpExp o -> switch (o.name) {
                    case "!", "?" -> Type.ILIST;
                    case "*" -> Type.INT;
                    default -> exp.type;
                };
                case FuncExp f -> Type.FUNC;
                default -> exp.type;
            };
        } else {
            //this.type = typeCheck(this.op, this.exp);
        }
    }
    public MonadExp(Exp op, Exp exp, Map<String,Type> symbol) {
        this(op,exp);
        //this.type = typeCheck(op,exp,symbol);
    }
    @Override
    public String toString(){
        return "(m"+op+" "+exp+", type: " + type + ")";
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitMonadExp(this);
    }
}

final class DyadExp extends Exp {
    public Exp op;
    public Exp left;
    public Exp right;
    public DyadExp(Exp op, Exp l, Exp r){
        this.op = op;
        this.left = l;
        this.right = r;
        //this.type = typeCheck(op,l,r);
    }
    @Override
    public String toString(){
        return "(d"+op+" "+left+" "+right+", type: " + type + ")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitDyadExp(this);
    }
}

sealed class Adverb extends Exp {
    public Exp func;
    public Exp left;
    public Exp right;
    public boolean monad;
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitAdverb(this);
    }
}

final class OverExp extends Adverb {
    public OverExp(Exp f,Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(/:"+func+" "+left+" "+right+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitOverExp(this);
    }
}

final class ScanExp extends Adverb {
    public ScanExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(\\:"+func+" "+left+" "+right+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitScanExp(this);
    }
}

final class EachExp extends Adverb {
    public EachExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(':"+func+" "+left+" "+right+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitEachExp(this);
    }
}

final class EachRightExp extends Adverb {
    public EachRightExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(/::"+func+" "+left+" "+right+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitEachRightExp(this);
    }
}

final class EachLeftExp extends Adverb {
    public EachLeftExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "(\\::"+func+" "+left+" "+right+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitEachLeftExp(this);
    }
}

final class EachPairExp extends Adverb {
    public EachPairExp(Exp f, Exp l, Exp r){
        this.func = f;
        this.left = l;
        this.right = r;
    }
    @Override
    public String toString(){
        return "('::" +func+" "+left+" "+right+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitEachPairExp(this);
    }
}

final class AssignExp extends Exp {
    public String name;
    public Exp exp;
    public AssignExp(String name, Exp e){
        this.name = name;
        this.exp = e;
        this.type = e.type;
    }
    @Override
    public String toString(){
        return "(=:"+name+" "+exp+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignExp(this);
    }
}

final class SymExp extends Exp {
    public String name;
    public SymExp(String s){
        name = s;
    }
    public String toString(){
        return "(Sym:"+name+", type: "+type+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitSymExp(this);
    }
}

final class NounExp extends Exp {
    A value;
    public NounExp(ArrayList<Integer> n){
        value = new A(n);
        this.type = Type.ILIST;
    }
    public NounExp(int d){
        value = new A(d);
        this.type = Type.INT;
    }
    public NounExp(A v){
        value = v;
    }

    public NounExp(Type type){
        this.type = type;
    }

    A getValue(){
        return value;
    }

    public String toString(){
        if (value != null) {
            return "(obj:" + Objects.requireNonNullElseGet(value.ilist, () -> value.isingle)
                    + ", type: " + value.type + ")";
        } else {
            return "(obj: null, type: " + type + ")";

        }
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitNounExp(this);
    }
}

final class ListExp extends Exp {
    public ArrayList<Exp> val;
    public ListExp(ArrayList<Exp> n){
        val = n;
    }
    public String toString(){
        return "(list:" + val + ")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitListExp(this);
    }
}
final class OpExp extends Exp {
    public String name;
    public OpExp(String s){
        name = s;
    }
    public String toString(){
        return "(fn:"+name+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitOpExp(this);
    }
}

final class SymOpExp extends Exp {
    public String name;
    public SymOpExp(String s){
        name = s;
    }
    public String toString(){
        return "(sfn:"+name+")";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitSymOpExp(this);
    }
}

final class FuncExp extends Exp {
    public List<Exp> body;
    String source;
    public FuncExp(List<Exp> b, String s){
        body = b;
        source = s;
        type = Type.FUNC;
    }
    public String toString(){
        return "(afn:{"+body+"})";
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitFuncExp(this);
    }
}
final class FuncCallExp extends Exp {
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
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitFuncCallExp(this);
    }
}
