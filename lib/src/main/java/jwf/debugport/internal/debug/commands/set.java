package jwf.debugport.internal.debug.commands;

import java.lang.reflect.Field;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command(group = Command.GROUP_ACCESS)
public class set {
    @Command.Help("Set the value of a field on the provided object to the given value, regardless of access modifiers.")
    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            @Command.ParamName("obj") Object object,
            @Command.ParamName("fieldName") String param,
            @Command.ParamName("value") Object value) throws NoSuchFieldException, IllegalAccessException {
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
