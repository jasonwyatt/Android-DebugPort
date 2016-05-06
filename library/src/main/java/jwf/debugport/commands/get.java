package jwf.debugport.commands;

import java.lang.reflect.Field;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 * Created by jason on 5/6/16.
 */
@Command
public class get {
    @Command.Help("Get the value of a parameter, regardless of access modifiers, on the provided object.")
    public static Object invoke(Interpreter interpreter, CallStack callstack, Object object, String param) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = object.getClass().getDeclaredField(param);
        } catch (NoSuchFieldException e) {
            // let's try a regular field..
            field = object.getClass().getField(param);
        }
        field.setAccessible(true);
        return field.get(object);
    }
}
