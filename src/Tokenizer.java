import java.util.Optional;

public class Tokenizer {
    private final String code;
    private int p;
    private final int len;
    public double val;
    public Token tok;
    public String symbol;
    public String op;

    public Tokenizer(String code){
        this.code = code;
        p = 0;
        len = this.code.length();
        tok = lex();
    }

    public boolean isEol(){
        return p==len;
    }

    public int getPos() {
        return p;
    }

    private void skipSpaces(){
        while(p<len && Character.isSpaceChar(code.charAt(p))){
            p++;
        }
    }

    public Token lex() {
        skipSpaces();
        if (p==len)
            return Token.EOL;
        if (p<len && Character.isDigit(code.charAt(p))){
            double v = 0.0;
            for (; p<len; p++){
                char c = code.charAt(p);
                if (Character.isDigit(c)){
                    v = 10*v + ((int)c-(int)'0');
                } else {
                     break;
                }
            }
            val = v;
            return Token.NUM;
        }
        if (p<len && Character.isAlphabetic(code.charAt(p))){
            StringBuilder sym = new StringBuilder();
            while (p<len && Character.isLetterOrDigit(code.charAt(p))) {
                sym.append(code.charAt(p++));
            }
            symbol = sym.toString();
            return Token.SYMBOL;
        }
        char c = code.charAt(p);
        switch (c) {
            case '+', '-', '*', '%', '|', '&', '^', '!', '<', '>', '=',
                    '~', '@', '?', '_', ',', '#', '$', '.', '(',
                    ')', '[', ']', '{', '}',
                    ';', ':' -> {
                p++;
                op = String.valueOf(c);
                return switch(c){
                    case '(' -> Token.OPENPAREN;
                    case ')' -> Token.CLOSEPAREN;
                    case '{' -> Token.OPENCURLY;
                    case '}' -> Token.CLOSECURLY;
                    case '[' -> Token.OPENSQ;
                    case ']' -> Token.CLOSESQ;
                    case ';' -> Token.SEMI;
                    case ':' -> Token.COLON;
                    default -> Token.OP;
                };
            }
            case '/','\\','\'' -> {
                p++;
                op = String.valueOf(c);
                if (p<len && (c = code.charAt(p)) == ':'){
                    p++;
                    op += ':';
                }
                return Token.ADVERB;
            }
        }
        return Token.EOL;
    }

    public void next(){
        tok = lex();
    }

    public void consume(){
        next();
    }

    public void consume(Token t) throws TokenError {
        if (t == tok) next();
        else throw new TokenError(String.format("expected %s, but got %s.",t,tok));
    }

    public void consume(String s) throws TokenError {
        if (s.compareTo(op)==0) next();
        else throw new TokenError(String.format("expected %s, but got %s.",s,tok));
    }

    public Optional<String > substringTo(char c){
        String s = code.substring(p-1);
        int pos = s.indexOf(c);
        if (pos<0)
            return Optional.empty();
        else
            return Optional.of(s.substring(0, pos));
    }
}
