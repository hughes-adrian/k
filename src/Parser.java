import java.util.*;

public class Parser {
    private final Tokenizer tokenizer;
    private final Map<String, Type> symbolTable;

    public Parser(Tokenizer tokenizer, Map<String,Type> symbolTable){
        this.tokenizer = tokenizer;
        this.symbolTable = symbolTable;
    }

    public List<Exp> parse() throws TokenError {
        return bexpr(tokenizer);
    }

    private List<Exp> bexpr(Tokenizer tokenizer) throws TokenError {
        List<Exp> exp_list = new LinkedList<>();
        exp_list.add(expr(tokenizer));
        while(tokenizer.tok == Token.SEMI){
            tokenizer.next();
            exp_list.add(expr(tokenizer));
        }
        return exp_list;
    }

    private boolean pv(Tokenizer tokenizer) throws TokenError {
        String v = "+-*%|&^!<>=~@?_,#$.";
        return switch(tokenizer.tok) {
            //case OPENCURLY -> true;
            case OP -> v.chars().anyMatch(i -> i == tokenizer.op.charAt(0));
            default -> false;
        };
    }

    private boolean pn(Tokenizer tokenizer){
        Token t = tokenizer.tok;
        return t == Token.NUM || t == Token.SYMBOL || t == Token.OPENPAREN || t == Token.OPENCURLY;
    }

    private Exp expr(Tokenizer tokenizer) throws TokenError {
        Exp opl;
        if (pv(tokenizer)){
            opl = verb(tokenizer);
            //if (isColon(tokenizer)) {
            //    tokenizer.consume(":");
            //}
            if (tokenizer.tok == Token.ADVERB) {
                Adverb exp = adverb(tokenizer,opl);
                exp.monad = true;
                while (tokenizer.tok == Token.ADVERB){
                    exp = adverb(tokenizer,exp);
                    exp.monad = true;
                }
                exp.right = expr(tokenizer);
                return exp;
            }
            Exp e = expr(tokenizer);
            return new MonadExp(opl,e);
        } else if (pn(tokenizer)){ // Dyadic branch ======================================================
            Exp n = noun(tokenizer);
            if (tokenizer.tok == Token.OPENSQ){
                // function call
                return fnCall(tokenizer,n);
            }
            if (tokenizer.tok == Token.COLON){
                // a:1 2 3
                assert n != null;
                return assign(((SymExp)n).name, tokenizer);
            }
            if (pv(tokenizer)){
                if (tokenizer.isEol()) throw new TokenError("'syntax error");
                Exp op = verb(tokenizer);
                if (tokenizer.tok == Token.ADVERB) {
                    Adverb exp = adverb(tokenizer,op);
                    while (tokenizer.tok == Token.ADVERB){
                        exp = adverb(tokenizer,exp);
                    }
                    exp.right = expr(tokenizer);
                    exp.left = n;
                    return exp;
                }
                return new DyadExp(op,n,expr(tokenizer));
            } else if (pn(tokenizer)){
                Exp e = expr(tokenizer);
                if (e instanceof Adverb ad
                        && !ad.monad
                        && ad.left == null
                        && !tokenizer.op.equals(")")){ // ) will be left in the tokenizer
                    ((Adverb) e).left = n;
                    return e;
                }
                MonadExp m = new MonadExp(n,e);
                return m;
            } else if (tokenizer.tok == Token.ADVERB) {
                Adverb exp = adverb(tokenizer,n);
                while (tokenizer.tok == Token.ADVERB){
                    exp = adverb(tokenizer,exp);
                }
                exp.right = expr(tokenizer);
                return exp;
            }
            return n; // just return the noun as rhs
        }
        return null;
    }

    private Exp fnCall(Tokenizer t, Exp n) throws TokenError {
        t.consume(Token.OPENSQ);
        Exp exp = expr(t);
        if (t.tok == Token.SEMI){
            ArrayList<Exp> l = new ArrayList<>();
            l.add(exp);
            while (t.tok == Token.SEMI){
                t.consume();
                l.add(expr(t));
            }
            t.consume(Token.CLOSESQ);
            return new MonadExp(n,new ListExp(l));
        }
        t.consume(Token.CLOSESQ);
        return new MonadExp(n,exp);
    }

    private boolean isColon(Tokenizer tokenizer) {
        return tokenizer.op != null && tokenizer.op.compareTo(":") == 0;
    }

    private AssignExp assign(String name, Tokenizer tokenizer) throws TokenError {
        tokenizer.consume(":");
        Exp e = expr(tokenizer);
        // add the new name to the symbol table
        symbolTable.put(name,e.type);
        AssignExp a = new AssignExp(name,e);
        a.type = e.type;
        return a;
    }

    private Exp verb(Tokenizer tokenizer) throws TokenError {
        String op = tokenizer.op;
        String sym = tokenizer.symbol;
        Token t = tokenizer.tok;
        tokenizer.next();
        Exp exp = switch(t){
            case SYMBOL -> new SymOpExp(sym);
            case OP -> new OpExp(op);
            //case OPENCURLY -> fn(tokenizer);
            default -> null;
        };
        if (tokenizer.tok == Token.OPENSQ){
            tokenizer.consume(Token.OPENSQ);
            List<Exp> args = new ArrayList<>();
            args.add(expr(tokenizer));
            while (tokenizer.tok == Token.SEMI){
                tokenizer.consume(Token.SEMI);
                args.add(expr(tokenizer));
            }
            tokenizer.consume(Token.CLOSESQ);
            exp = new FuncCallExp(exp,args);
        }
        return exp;
    }

    private Adverb adverb(Tokenizer tokenizer, Exp op) throws TokenError {
        String c = tokenizer.op;
        tokenizer.consume();
        return switch(c){
            case "/"   -> new OverExp(op, null,null);//expr(tokenizer));
            case "\\"  -> new ScanExp(op, null,null);//expr(tokenizer));
            case "'"   -> new EachExp(op, null,null);//expr(tokenizer));
            case "/:"  -> new EachRightExp(op, null,null);//expr(tokenizer));
            case "\\:" -> new EachLeftExp(op, null,null);//expr(tokenizer));
            case "':"  -> new EachPairExp(op, null,null);//expr(tokenizer));
            default    -> null;
        };
    }

    private Exp noun(Tokenizer tokenizer) throws TokenError {
        Token t = tokenizer.tok;
        switch (t) {
            case OPENPAREN:
                tokenizer.consume();
                Exp exp = expr(tokenizer);
                if (tokenizer.tok == Token.SEMI){
                    ArrayList<Exp> l = new ArrayList<>();
                    l.add(exp);
                    while (tokenizer.tok == Token.SEMI){
                        tokenizer.consume();
                        l.add(expr(tokenizer));
                    }
                    tokenizer.consume(Token.CLOSEPAREN);
                    return new ListExp(l);
                }
                tokenizer.consume(Token.CLOSEPAREN);
                return exp;
            case NUM:
                return num(tokenizer);
            case SYMBOL:
                String name = tokenizer.symbol;
                tokenizer.consume();
                return new SymExp(name);
            case OPENCURLY:
                return fn(tokenizer);
        }
        return null;
    }

    private NounExp num(Tokenizer tokenizer) {
        ArrayList<Integer> n = new ArrayList<>();
        n.add(tokenizer.val);
        tokenizer.next();
        while (tokenizer.tok == Token.NUM){
            n.add(tokenizer.val);
            tokenizer.next();
        }
        if (n.size() == 1) {
            return new NounExp(n.get(0));
        }
        return new NounExp(n);
    }

    private FuncExp fn(Tokenizer tokenizer) throws TokenError {
        tokenizer.consume("{");
        Optional<String> source = tokenizer.substringTo('}');
        List<Exp> body = bexpr(tokenizer);
        FuncExp exp = new FuncExp(body,source.orElseThrow(
                () -> new TokenError("source code had no closing }")));
        tokenizer.consume("}");
        return exp;
    }
}
