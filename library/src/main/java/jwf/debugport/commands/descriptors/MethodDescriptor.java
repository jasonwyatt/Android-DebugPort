package jwf.debugport.commands.descriptors;

import android.text.TextUtils;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 *
 */
public class MethodDescriptor extends MemberDescriptor {
    private final Method mMethod;

    public MethodDescriptor(Method method) {
        super(method);
        mMethod = method;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String modifiers = getModifierString();
        if (!TextUtils.isEmpty(modifiers)) {
            sb.append(modifiers);
            sb.append(" ");
        }

        try {
            Type genericReturnType = mMethod.getGenericReturnType();
            sb.append(getSimpleClassName(genericReturnType));
            sb.append(" ");
        } catch (Exception e) {
            Class<?> returnType = mMethod.getReturnType();
            if (returnType.equals(Void.TYPE)) {
                sb.append("void ");
            } else {
                sb.append(returnType.getSimpleName());
                sb.append(" ");
            }
        }

        sb.append(getName());
        sb.append("(");
        try {
            Type[] genericParams = mMethod.getGenericParameterTypes();
            for (int i = 0; i < genericParams.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                Type paramType = genericParams[i];
                sb.append(getSimpleClassName(paramType));
            }
        } catch (Exception e) {
            Class<?>[] params = mMethod.getParameterTypes();
            for (int i = 0; i < params.length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(params[i].getSimpleName());
            }
        }
        sb.append(")");

        return sb.toString();
    }

    public static MethodDescriptor[] fromMethods(Method[] methods) {
        MethodDescriptor[] result = new MethodDescriptor[methods.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new MethodDescriptor(methods[i]);
        }
        Arrays.sort(result);
        return result;
    }
}
