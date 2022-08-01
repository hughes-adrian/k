import java.lang.reflect.GenericDeclaration;
import java.util.List;

public class AstCodeGen implements Exp.Visitor<Object> {

    private final Environment environment;
    public StringBuilder code = new StringBuilder();

    public AstCodeGen(Environment environment){
        this.environment = environment;
    }
    public Object interpret(Exp exp){
        return exp.accept(this);
    }
    @Override
    public Object visitDyadExp(DyadExp expr) {
        double res = 0;
        switch (expr.op){
            case SymOpExp s -> {

            }
            case OpExp s -> {
                if ( s.name.compareTo("+") == 0){
                    res = (double)expr.left.accept(this) + (double)expr.right.accept(this);
                    code.append("\niadd");
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + expr.op);
        }
        return res;
    }

    @Override
    public Object visitMonadExp(MonadExp expr) {
        double res = 0;
        switch (expr.op){
            case SymOpExp s -> {
                if (expr.exp == null) {
                    res = (double)expr.op.accept(this);
                }
                else res = 2*(double)expr.exp.accept(this);
            }
            case OpExp s -> {
                if ( s.name.compareTo("+") == 0) res = (double)expr.exp.accept(this);
            }
            default -> res = (double) expr.exp.accept(this);
        }
        return res;
    }

    @Override
    public Object visitNounExp(NounExp expr) {
        A v = expr.getValue();
        code.append("\nbipush ").append(v.single);
        return v.scalar ?
                v.single :
                v.val.get(0);
    }

    @Override
    public Object visitAdverb(Adverb expr) {
        return null;
    }

    @Override
    public Object visitAssignExp(AssignExp expr) {
        A result = (A)expr.exp.accept(this);
        environment.assign(expr.name,result);
        return result;
    }

    @Override
    public Object visitEachExp(EachExp expr) {
        return null;
    }

    @Override
    public Object visitEachLeftExp(EachLeftExp expr) {
        return null;
    }

    @Override
    public Object visitEachPairExp(EachPairExp expr) {
        return null;
    }

    @Override
    public Object visitEachRightExp(EachRightExp expr) {
        return null;
    }

    @Override
    public Object visitFuncCallExp(FuncCallExp expr) {
        return null;
    }

    @Override
    public Object visitFuncExp(FuncExp expr) {
        return null;
    }

    @Override
    public Object visitListExp(ListExp expr) {
        return null;
    }

    @Override
    public Object visitOverExp(OverExp expr) {
        return null;
    }

    @Override
    public Object visitScanExp(ScanExp expr) {
        return null;
    }

    @Override
    public Object visitSymExp(SymExp expr) {
        return null;
    }

    @Override
    public Object visitSymOpExp(SymOpExp expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitOpExp(OpExp expr) {
        return null;
    }


}
