public enum Token {
    NUM,SYMBOL,OP,SEMI,COLON,
    OPENPAREN,CLOSEPAREN,OPENSQ,CLOSESQ,
    OPENCURLY,CLOSECURLY,
    ADVERB,EOL;
    public String toString() {
        return switch(this) {
            case NUM -> "number";
            case SYMBOL -> "symbol";
            case OP -> "operator";
            case SEMI -> ";";
            case COLON -> ":";
            case OPENPAREN -> "(";
            case CLOSEPAREN -> ")";
            case OPENSQ -> "[";
            case CLOSESQ -> "]";
            case OPENCURLY -> "{";
            case CLOSECURLY -> "}";
            case ADVERB -> "adverb";
            case EOL -> "end";
        };
    }
}
