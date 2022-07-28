import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class k {

    //private static Logger logger;

    private final Environment environment = new Environment();

    public static void main(String[] args) throws IOException {
        //logger = Logger.getLogger(k.class.getName());
        k interpreter = new k();
        interpreter.repl();
    }

    private void repl() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        char[] buf = new char[2];
        for(;;){
            reader.mark(2);
            int c = reader.read(buf,0,2);
            if (c ==2)
                if (new String(buf).startsWith("\\\\")) break;
            reader.reset();
            interpret(reader.readLine());
        }
    }

    private void interpret(String code) {
        Tokenizer tokenizer = new Tokenizer(code);
        try {
            List<Exp> expressions = bexpr(tokenizer);
            expressions.forEach(System.out::println);
            AstPrinter ap = new AstPrinter();
            AstCodeGen astCodeGen = new AstCodeGen(environment);
            expressions.forEach(i -> System.out.println(ap.print(i)));
            try {
                expressions.forEach(i -> System.out.println(astCodeGen.interpret(i)));
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        } catch (TokenError error) {
            //logger.log(Level.INFO, error.toString());
            //System.out.println(error);
            System.out.println("'syntax error");
            System.out.println(code);
            IntStream.range(0,tokenizer.getPos()-1).forEach(
                    foo -> System.out.print(" ")
            );
            System.out.println("^");
        }
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

    private boolean pv(Tokenizer tokenizer){
        String v = "+-*%|&^!<>=~@?_,#$.";
        return switch(tokenizer.tok) {
            case SYMBOL,OPENCURLY -> true;
            case OP -> v.chars().anyMatch(i -> i == tokenizer.op.charAt(0));
            default -> false;
        };
    }

    private boolean pn(Tokenizer tokenizer){
        Token t = tokenizer.tok;
        return t == Token.NUM || t == Token.SYMBOL || t == Token.OPENPAREN;
    }

    private Exp expr(Tokenizer tokenizer) throws TokenError {
        Exp opl;
        if (pv(tokenizer)){
            Token last = tokenizer.tok;
            opl = verb(tokenizer);
            if (tokenizer.tok == Token.COLON && last == Token.SYMBOL){
                // a:1 2 3
                return assign(((SymOpExp)opl).name, tokenizer);
            }
            if (isColon(tokenizer)) {
                tokenizer.consume(":");
            }
            /*if (tokenizer.tok == Token.ADVERB){
                // f/ 1 2 3
                Adverb exp = adverb(tokenizer,opl);
                while (tokenizer.tok == Token.ADVERB){
                    exp = adverb(tokenizer,exp);
                }
                exp.left = new MonadExp(opl,null);
                return exp;
            }*/
            if ((tokenizer.tok == Token.OP || tokenizer.tok == Token.SYMBOL) && last == Token.SYMBOL){
                if (pv(tokenizer)){
                    Exp op = verb(tokenizer);
                    if (tokenizer.tok == Token.COLON) {
                        tokenizer.consume(":");
                        if (tokenizer.tok == Token.ADVERB){
                            // TODO check for adverb trains
                            return new MonadExp(opl,adverb(tokenizer,op));
                        } else return new MonadExp(opl,new MonadExp(op,expr(tokenizer)));
                    }
                    if (tokenizer.tok == Token.ADVERB){
                        Adverb exp = adverb(tokenizer,op);
                        while (tokenizer.tok == Token.ADVERB){
                            exp = adverb(tokenizer,exp);
                        }
                        exp.left = new MonadExp(opl,null);
                        return exp;
                    }
                    return new DyadExp(op,new MonadExp(opl,null),expr(tokenizer));
                }
                return opl;
            }
            return new MonadExp(opl,expr(tokenizer));
        } else if (pn(tokenizer)){ // Dyadic branch ============================
            Exp n = noun(tokenizer);
            if (tokenizer.tok == Token.COLON) {
                throw new TokenError("assignment error");
            }
            if (!(n instanceof NounExp)) {
                if (tokenizer.tok == Token.OP) return new DyadExp(verb(tokenizer),n,expr(tokenizer));
                else return new MonadExp(n,expr(tokenizer));
            }
            if (pv(tokenizer)){
                if (tokenizer.isEol()) throw new TokenError("'syntax error");
                Exp op = verb(tokenizer);
                if (isColon(tokenizer)) {
                    tokenizer.consume(":");
                    if (tokenizer.tok == Token.ADVERB){
                        // TODO check for adverb trains
                        return new MonadExp(n,adverb(tokenizer,op));
                    } else return new MonadExp(n,new MonadExp(op,expr(tokenizer)));
                }
                if (tokenizer.tok == Token.ADVERB){
                    Adverb exp = adverb(tokenizer,op);
                    while (tokenizer.tok == Token.ADVERB){
                        exp = adverb(tokenizer,exp);
                    }
                    exp.left = n; // Patch the AST
                    return exp;
                }
                if (op instanceof FuncExp || op instanceof SymOpExp) {
                    return new MonadExp(n,new MonadExp(op,expr(tokenizer)));
                }
                return new DyadExp(op,n,expr(tokenizer));
            }
            return n;
        }
        return null;
    }

    private boolean isColon(Tokenizer tokenizer) {
        return tokenizer.op != null && tokenizer.op.compareTo(":") == 0;
    }

    private AssignExp assign(String name, Tokenizer tokenizer) throws TokenError {
        tokenizer.consume(":");
        return new AssignExp(name,expr(tokenizer));
    }

    private Exp verb(Tokenizer tokenizer) throws TokenError {
        String op = tokenizer.op;
        String sym = tokenizer.symbol;
        Token t = tokenizer.tok;
        tokenizer.next();
        Exp exp = switch(t){
            case SYMBOL -> new SymOpExp(sym);
            case OP -> new OpExp(op);
            case OPENCURLY -> fn(tokenizer);
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
            case "/" -> new OverExp(op, null,expr(tokenizer));
            case "\\" -> new ScanExp(op, null,expr(tokenizer));
            case "'" -> new EachExp(op, null,expr(tokenizer));
            case "/:" -> new EachRightExp(op, null,expr(tokenizer));
            case "\\:" -> new EachLeftExp(op, null,expr(tokenizer));
            case "':" -> new EachPairExp(op, null,expr(tokenizer));
            default -> null;
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
                tokenizer.consume();
                String name = tokenizer.symbol;
                return new SymExp(name);
        }
        return null;
    }

    private NounExp num(Tokenizer tokenizer) {
        ArrayList<Double> n = new ArrayList<>();
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
        Optional<String> source = tokenizer.substringTo('}');
        List<Exp> body = bexpr(tokenizer);
        FuncExp exp = new FuncExp(body,source.orElseThrow(
                () -> new TokenError("source code had no closing }")));
        tokenizer.consume("}");
        return exp;
    }
}
