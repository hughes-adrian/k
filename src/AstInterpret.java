import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AstInterpret implements Exp.Visitor<A> {

    private final Environment environment;

    public AstInterpret(Environment environment){
        this.environment = environment;
    }
    public A interpret(Exp exp){
        return exp.accept(this);
    }
    @Override
    public A visitDyadExp(DyadExp expr) {
        A res = null;
        A r = expr.right.accept(this);
        A l = expr.left.accept(this);
        Type tl = l.type;
        Type tr = r.type;
        switch (expr.op){
            case SymOpExp s -> {

            }
            case OpExp s -> {
                if ( s.name.compareTo("+") == 0){
                    if (tl == Type.INT && tr == Type.INT) {
                        return new A(l.isingle + r.isingle);
                    } else if (tl == Type.ILIST && tr == Type.INT) {
                        ArrayList<Integer> array = new ArrayList<>(l.ilist.length);
                        for (int i=0; i<l.ilist.length; i++){
                            array.add(l.ilist[i]+r.isingle);
                        }
                        return new A(array);
                    } else if (tl == Type.INT && tr == Type.ILIST) {
                        ArrayList<Integer> array = new ArrayList<>(r.ilist.length);
                        for (int i=0; i<r.ilist.length; i++){
                            array.add(l.isingle+r.ilist[i]);
                        }
                        return new A(array);
                    } else if (tl == Type.ILIST && tr == Type.ILIST) {
                        int n = Math.max(l.ilist.length,r.ilist.length);
                        ArrayList<Integer> array = new ArrayList<>(n);
                        for (int i=0; i<n; i++){
                            array.add(l.ilist[i]+r.ilist[i]);
                        }
                        return new A(array);
                    }
                }
                if ( s.name.compareTo("-") == 0){
                    //if (tl == Type.INT && tr == Type.INT) {
                    //    return new A(l.single - r.single);
                    //} else if (tl == Type.ILIST && tr == Type.INT) {
                    //    ArrayList<Integer> array = new ArrayList<>(l.val.size());
                    //    for (int i=0; i<l.val.size(); i++){
                    //        array.add(l.val.get(i)-r.single);
                    //    }
                    //    return new A(array);
                    //} else if (tl == Type.INT && tr == Type.ILIST) {
                    //    ArrayList<Integer> array = new ArrayList<>(r.val.size());
                    //    for (int i=0; i<r.val.size(); i++){
                    //        array.add(l.single-r.val.get(i));
                    //    }
                    //    return new A(array);
                    //} else if (tl == Type.ILIST && tr == Type.ILIST) {
                    //    ArrayList<Integer> array = new ArrayList<>(r.val.size());
                    //    for (int i=0; i<r.val.size(); i++){
                    //        array.add(l.val.get(i)-r.val.get(i));
                    //    }
                    //    return new A(array);
                    //}
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + expr.op);
        }
        assert false : "unreachable";
        return res;
    }

    @Override
    public A visitMonadExp(MonadExp expr) {
        A res = null;
        switch (expr.op){
            case SymOpExp s -> {
                A r = expr.exp.accept(this);
                switch (expr.exp){
                    case NounExp n -> {
                        if (n.value == null) res = environment.get(s.name);
                        else {
                            A fn = environment.get(s.name);
                            ArrayList<Integer> array = new ArrayList<>(n.value.ilist.length);
                            for (int i=0; i<n.value.ilist.length; i++){
                                array.add(fn.ilist[i]);
                            }
                            res = new A(array);
                        }
                    }
                    default -> res = r;
                }
                //if (expr.exp == null) {
                //   res = expr.op.accept(this);
                //
                //else res = expr.exp.accept(this);
            }
            case OpExp s -> {
                A r = expr.exp.accept(this);
                //if (s.name.compareTo("!") == 0) {
                //    if (r.type == Type.INT) {
                //        ArrayList<Integer> array = new ArrayList<>(r.single);
                //        for (int i = 0; i < r.single; i++) {
                //            array.add(i);
                //        }
                //        res = new A(array);
                //    } else if (r.type == Type.ILIST) {
                //        ArrayList<Integer> array = new ArrayList<>(r.val.get(0));
                //        for (int i = 0; i < r.val.get(0); i++) {
                //            array.add(i);
                //        }
                //        res = new A(array);
                //    }
                //} else if ( s.name.compareTo("+") == 0){
                //    res = r;
                //} else if (s.name.compareTo("-") == 0){
                //    if (r.type == Type.INT){
                //        res = new A(-r.single);
                //    } else if (r.type == Type.ILIST){
                //        ArrayList<Integer> array = new ArrayList<>(r.val.size());
                //        for (int i=0; i<r.val.size(); i++){
                //            array.add(-r.val.get(i));
                //        }
                //        res = new A(array);
                //    }
                //}
            }
            case FuncExp f -> {
                return new A(f.body);
            }
            default -> res = expr.exp.accept(this);
        }
        return res;
    }

    @Override
    public A visitNounExp(NounExp expr) {
        return expr.getValue();
    }

    @Override
    public A visitAdverb(Adverb expr) {
        return null;
    }

    @Override
    public A visitAssignExp(AssignExp expr) {
        A result = expr.exp.accept(this);
        environment.assign(expr.name,result);
        return result;
    }

    @Override
    public A visitEachExp(EachExp expr) {
        return null;
    }

    @Override
    public A visitEachLeftExp(EachLeftExp expr) {
        return null;
    }

    @Override
    public A visitEachPairExp(EachPairExp expr) {
        return null;
    }

    @Override
    public A visitEachRightExp(EachRightExp expr) {
        return null;
    }

    @Override
    public A visitFuncCallExp(FuncCallExp expr) {
        return null;
    }

    @Override
    public A visitFuncExp(FuncExp expr) {
        return null;
    }

    @Override
    public A visitListExp(ListExp expr) {
        return null;
    }

    @Override
    public A visitOverExp(OverExp expr) {
        return null;
    }

    @Override
    public A visitScanExp(ScanExp expr) {
        return null;
    }

    @Override
    public A visitSymExp(SymExp expr) {
        return null;
    }

    @Override
    public A visitSymOpExp(SymOpExp expr) {
        return environment.get(expr.name);
    }

    @Override
    public A visitOpExp(OpExp expr) {
        return null;
    }
}
