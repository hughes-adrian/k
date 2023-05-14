import java.util.Collections;
import java.util.List;

public class AstPrinter implements Exp.Visitor<String> {

    public String print(Exp exp){
        return exp.accept(this);
    }
    @Override
    public String visitDyadExp(DyadExp expr) {
        return parens(expr.op.accept(this),expr.left,expr.right);
    }

    @Override
    public String visitMonadExp(MonadExp expr) {
        return parens(expr.op.accept(this),expr.exp);
    }

    @Override
    public String visitNounExp(NounExp expr) {
        A v = expr.getValue();
        return v == null ? "(Obj null)" : v.scalar ?
                parens("",v.isingle) :
                parens("", Collections.singletonList(v.ilist));
    }

    @Override
    public String visitAdverb(Adverb expr) {
        return null;
    }

    @Override
    public String visitAssignExp(AssignExp expr) {
        return parens(String.format("= %s",expr.name),  expr.exp);
    }

    @Override
    public String visitEachExp(EachExp expr) {
        return parens(String.format("%s'",expr.func.accept(this)),expr.left,expr.right);
    }

    @Override
    public String visitEachLeftExp(EachLeftExp expr) {
        return parens(String.format("%s\\:",expr.func.accept(this)),expr.left,expr.right);
    }

    @Override
    public String visitEachPairExp(EachPairExp expr) {
        return parens(String.format("%s':",expr.func.accept(this)),expr.left,expr.right);
    }

    @Override
    public String visitEachRightExp(EachRightExp expr) {
        return parens(String.format("%s/:",expr.func.accept(this)),expr.left,expr.right);
    }

    @Override
    public String visitFuncCallExp(FuncCallExp expr) {
        return null;
    }

    @Override
    public String visitFuncExp(FuncExp expr) {
        return "{"+expr.source+"}";
    }

    @Override
    public String visitListExp(ListExp expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("(list ");
        for (Exp exp : expr.val){
            if (exp != null) sb.append(exp.accept(this));
            else sb.append("null");
            sb.append(" ");
        }
        int last = sb.lastIndexOf(" ");
        sb.deleteCharAt(last);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitOverExp(OverExp expr) {
        return parens(String.format("%s/",expr.func.accept(this)),expr.left,expr.right);
    }

    @Override
    public String visitScanExp(ScanExp expr) {
        return parens(String.format("%s\\",expr.func.accept(this)),expr.left,expr.right);
    }

    @Override
    public String visitSymExp(SymExp expr) {
        return expr.name;
    }

    @Override
    public String visitSymOpExp(SymOpExp expr) {
        return expr.name;
    }

    @Override
    public String visitOpExp(OpExp expr) {
        return expr.name;
    }


    private String parens(String name, Exp... exps){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(name);
        for (Exp exp : exps){
            sb.append(" ");
            if (exp != null) sb.append(exp.accept(this));
            else sb.append("null");//sb.deleteCharAt(sb.lastIndexOf(" "));
        }
        sb.append(")");
        return sb.toString();
    }
    private String parens(String name, List val){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(name);
        //sb.append(" ");
        sb.append(val);
        sb.append(")");
        return sb.toString();
    }
    private String parens(String name, double val){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(name);
        //sb.append(" ");
        sb.append(val);
        sb.append(")");
        return sb.toString();
    }
}
