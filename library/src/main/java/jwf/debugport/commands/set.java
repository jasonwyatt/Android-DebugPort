package jwf.debugport.commands;

import java.lang.reflect.Field;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 * Created by jason on 5/6/16.
 */
@Command
public class set {
    @Command.Help("Set the value of a field on the provided object to the given value, regardless of access modifiers.")
    public static Object invoke(Interpreter interpreter, CallStack callStack, Object object, String param, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = object.getClass().getDeclaredField(param);
        } catch (NoSuchFieldException e) {
            // try a field on the inherited classes
            field = object.getClass().getField(param);
        }
        field.setAccessible(true);
        field.set(object, value);
        return value;
    }
}
