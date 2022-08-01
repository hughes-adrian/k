import java.util.ArrayList;
import java.util.List;

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
        A l = expr.left.accept(this);
        A r = expr.right.accept(this);
        switch (expr.op){
            case SymOpExp s -> {

            }
            case OpExp s -> {
                // TODO fix this for deeper trees
                if ( s.name.compareTo("+") == 0){
                    if (l.scalar && r.scalar) return new A(l.single+r.single);
                    else if (l.scalar) {
                        ArrayList<Integer> array = new ArrayList<>(r.val.size());
                        for (int i=0; i<r.val.size(); i++){
                            array.add(r.val.get(i)+l.single);
                        }
                        return new A(array);
                    } else if (r.scalar) {
                        ArrayList<Integer> array = new ArrayList<>(l.val.size());
                        for (int i=0; i<l.val.size(); i++){
                            array.add(l.val.get(i)+r.single);
                        }
                        return new A(array);
                    }
                }
                if ( s.name.compareTo("-") == 0){
                    if (l.scalar && r.scalar) return new A(l.single-r.single);
                    else if (l.scalar) {
                        ArrayList<Integer> array = new ArrayList<>(r.val.size());
                        for (int i=0; i<r.val.size(); i++){
                            array.add(l.single-r.val.get(i));
                        }
                        return new A(array);
                    } else if (r.scalar) {
                        ArrayList<Integer> array = new ArrayList<>(l.val.size());
                        for (int i=0; i<l.val.size(); i++){
                            array.add(l.val.get(i)-r.single);
                        }
                        return new A(array);
                    }
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
                System.out.println(expr.exp.getClass());
                switch (expr.exp){
                    case NounExp n -> {
                        if (n.value == null) res = environment.get(s.name);
                        //else res = environment.get(s.name);
                    }
                    default -> res = expr.exp.accept(this);
                }
                //if (expr.exp == null) {
                //   res = expr.op.accept(this);
                //
                //else res = expr.exp.accept(this);
            }
            case OpExp s -> {
                if ( s.name.compareTo("+") == 0){
                    res = expr.exp.accept(this);
                }
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
