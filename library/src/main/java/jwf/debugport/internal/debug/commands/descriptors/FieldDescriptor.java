package jwf.debugport.internal.debug.commands.descriptors;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 *
 */
public class FieldDescriptor extends MemberDescriptor {
    private final Field mField;

    public FieldDescriptor(Field field) {
        super(field);
        mField = field;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String modifier = getModifierString();
        if (!TextUtils.isDigitsOnly(modifier)) {
            sb.append(modifier);
            sb.append(" ");
        }

        Type type = mField.getType();
        sb.append(getSimpleClassName(type));
        try {
            Type genericType = mField.getGenericType();
            if (genericType instanceof ParameterizedType) {
                sb.append(getParametrizedTypeString((ParameterizedType) genericType));
            }
        } catch (Exception e) {
            // nothing to do here.
        }
        sb.append(" ");
        sb.append(getName());

        return sb.toString();
    }

    public static FieldDescriptor[] fromFields(Field[] fields) {
        FieldDescriptor[] result = new FieldDescriptor[fields.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new FieldDescriptor(fields[i]);
        }
        Arrays.sort(result);
        return result;
    }
}
