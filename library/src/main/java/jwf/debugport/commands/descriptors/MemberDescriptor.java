package jwf.debugport.commands.descriptors;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class MemberDescriptor implements Comparable<MemberDescriptor> {
    private final int mModifiers;
    private final Class<?> mClass;
    private final String mName;

    public MemberDescriptor(Member obj) {
        mModifiers = obj.getModifiers();
        mClass = obj.getDeclaringClass();
        mName = obj.getName();
    }

    public String getModifierString() {
        return Modifier.toString(mModifiers);
    }

    public String getName() {
        return mName;
    }

    public String getClassName() {
        return mClass.getSimpleName();
    }

    public int compareVisibility(MemberDescriptor another) {
        int visibilityMask = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
        int myVisibility = mModifiers & visibilityMask;
        int theirVisibility = another.mModifiers & visibilityMask;

        if (myVisibility < theirVisibility) {
            return -1;
        }
        if (myVisibility > theirVisibility) {
            return 1;
        }

        return 0;
    }

    @Override
    public int compareTo(MemberDescriptor another) {
        // first sort by staticness...
        if (!Modifier.isStatic(mModifiers) && Modifier.isStatic(another.mModifiers)) {
            return -1;
        } else if (Modifier.isStatic(mModifiers) && !Modifier.isStatic(another.mModifiers)) {
            return 1;
        }

        // then by visibility
        int visibilityComparison = compareVisibility(another);
        if (visibilityComparison != 0) {
            return visibilityComparison;
        }

        // then by name
        return mName.compareTo(another.mName);
    }

    public static String getParameterizedTypeString(ParameterizedType type) {
        StringBuilder sb = new StringBuilder();
        Type[] typeArgs = type.getActualTypeArguments();
        sb.append("<");
        for (int i = 0; i < typeArgs.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(getSimpleClassName(typeArgs[i]));
        }
        sb.append(">");
        return sb.toString();
    }

    private static final Pattern sTypeSimpleClassPattern = Pattern.compile("([@a-z]+ )?([a-zA-Z0-9_]*\\.)*([a-zA-Z0-9_\\$]+)");
    private static final Pattern sTypeArraySimpleClassPattern = Pattern.compile("([@a-z]+ )?\\[L([a-zA-Z0-9_]*\\.)*([a-zA-Z0-9_\\$]+);?");
    public static String getSimpleClassName(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            StringBuilder sb = new StringBuilder();
            sb.append(getSimpleClassName(pType.getRawType()));
            Type[] args = pType.getActualTypeArguments();
            if (args.length > 0) {
                sb.append("<");
                for (int i = 0; i < args.length; i++) {
                    if (i != 0) {
                        sb.append(", ");
                    }
                    sb.append(getSimpleClassName(args[i]));
                }
                sb.append(">");
            }
            return sb.toString();
        }
        if (type instanceof GenericArrayType) {
            GenericArrayType gType = (GenericArrayType) type;
            return getSimpleClassName(gType.getGenericComponentType());
        }

        Matcher matcher = sTypeSimpleClassPattern.matcher(type.toString());
        if (matcher.matches()) {
            String result = matcher.group(3);
            return result.replaceAll("\\$", ".");
        }
        matcher = sTypeArraySimpleClassPattern.matcher(type.toString());
        if (matcher.matches()) {
            // must be an array..
            String result = matcher.group(3);
            return result.replaceAll("\\$", ".") + "[]";
        }

        // handle primitive others..
        if (type instanceof Class) {
            Class c = (Class) type;
            StringBuilder sb = new StringBuilder();
            sb.append(c.getComponentType().toString());
            if (c.isArray()) {
                sb.append("[]");
            }
            return sb.toString();
        }
        // fallback
        return type.toString();
    }
}
