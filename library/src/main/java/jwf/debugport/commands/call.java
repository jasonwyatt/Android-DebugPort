package jwf.debugport.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import bsh.CallStack;
import bsh.Interpreter;
import jwf.debugport.annotations.Command;

/**
 *
 */
@Command
public class call {
    /*
     * In order to support a varargs-like behavior when the command is called with no third
     * parameter, we have to supply several methods.. beanshell doesn't really support varargs well.
     */
    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, null);
    }

    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName,
            Object p1) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, p1);
    }

    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName,
            Object p1,
            Object p2) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, p1, p2);
    }

    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName,
            Object p1,
            Object p2,
            Object p3) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, p1, p2, p3);
    }

    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName,
            Object p1,
            Object p2,
            Object p3,
            Object p4) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, p1, p2, p3, p4);
    }

    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName,
            Object p1,
            Object p2,
            Object p3,
            Object p4,
            Object p5) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, p1, p2, p3, p4, p5);
    }

    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            Object object,
            String methodName,
            Object p1,
            Object p2,
            Object p3,
            Object p4,
            Object p5,
            Object p6) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeInner(object, methodName, p1, p2, p3, p4, p5, p6);
    }

    @Command.Help("Call a method, regardless of access modifiers, on the provided object.")
    public static Object invoke(
            Interpreter interpreter,
            CallStack callStack,
            @Command.ParamName("obj") Object object,
            @Command.ParamName("method") String methodName,
            @Command.ParamName("params") Object... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return invokeInner(object, methodName, params);
    }

    private static Object invokeInner(
            Object object,
            String methodName,
            Object... params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method;
        Class<?>[] paramClasses = new Class<?>[0];
        if (params != null) {
            paramClasses = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = params[i].getClass();
            }
        }

        try {
            method = object.getClass().getDeclaredMethod(methodName, paramClasses);
        } catch (NoSuchMethodException e) {
            // try a method on a parent of the object's class
            method = object.getClass().getMethod(methodName, paramClasses);
        }
        method.setAccessible(true);
        return method.invoke(object, params);
    }
}
