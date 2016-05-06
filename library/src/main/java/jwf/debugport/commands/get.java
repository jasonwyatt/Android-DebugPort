package jwf.debugport.commands;

import java.lang.reflect.Field;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command
public class get {
    @Command.Help("Get the value of a field, regardless of access modifiers, on the provided object.")
    public static Object invoke(
            Interpreter interpreter,
            CallStack callstack,
            @Command.ParamName("obj") Object object,
            @Command.ParamName("fieldName") String param) throws NoSuchFieldException, IllegalAccessException {
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
