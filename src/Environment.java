import java.util.HashMap;
import java.util.Map;

public class Environment {

    final Environment enclosing;
    private final Map<String,A> values = new HashMap<>();

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }
    public Environment(){
        this.enclosing = null;
    }

    A get(String name){
        if (values.containsKey(name)){
            return values.get(name);
        }
        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeException("Undefined variable: '"+name+"'");
    }

    void assign(String name, A value){
        if (values.containsKey(name)){
            values.replace(name,value);
        } else {
            values.put(name, value);
        }
    }

}
