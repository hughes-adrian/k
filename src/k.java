import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class k {

    //private static Logger logger;

    private final Environment environment = new Environment();
    private final Map<String,Type> symbolTable = new HashMap<>();

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
            System.out.print("  "); // APL style - indent the prompt
            reader.mark(2);
            int c = reader.read(buf,0,2);
            if (c == 2)
                if (new String(buf).startsWith("\\\\")) break;
            reader.reset();
            interpret(reader.readLine());
        }
    }

    private void interpret(String code){
        Tokenizer tokenizer = new Tokenizer(code);
        try {
            Parser parser = new Parser(tokenizer, symbolTable);
            List<Exp> expressions = parser.parse();
            //expressions.forEach(System.out::println);
            AstPrinter ap = new AstPrinter();
            //AstInterpret astInterpret = new AstInterpret(environment);
            expressions.forEach(i -> System.out.printf("%s\n",ap.print(i)));
            //AstCodeGen cg = new AstCodeGen(environment);
            //cg.interpret(expressions.get(0));
            //cg.finishAndRun();

            //System.out.println("code: "+cg.code);
            //expressions.forEach(i -> System.out.println(astInterpret.interpret(i)));
        } catch (TokenError error) {
            //logger.log(Level.INFO, error.toString());
            //System.out.println(error);
            System.out.println(error.getMessage());//"'syntax error");
            System.out.println(code);
            IntStream.range(0, tokenizer.getPos() - 1).forEach(
                    foo -> System.out.print(" ")
            );
            System.out.println("^");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("length error");
        } catch (ClassFormatError e){
            e.printStackTrace();
        }
    }
}
