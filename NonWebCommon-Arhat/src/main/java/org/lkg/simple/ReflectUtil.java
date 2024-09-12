package org.lkg.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * copy Spring#ReflectionUtils && 定制化了一些实现
 * 反射毕竟损伤性能，所以改工具类的使用最好放在低频对象创建上，例如spring启动时需要创建的代理对象、配置类等
 */
public class ReflectUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 根据字段名依次从自身到父类查找某个字段
     * @param clazz class
     * @param fieldName field name
     * @return field to find
     */
    public static Field findField(Class<?> clazz,String fieldName) {

        Field field = null;

        while (field == null && clazz != null){
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return field;
    }

    // 提供增强方法，可以直接获取目标对象，而不需要再写额外的对象转换操作
    public static <T> T findField(Object source,Class<T> filedType, String fieldName) {
        if (source == null) {
            return null;
        }
        Class<?> cls = source.getClass();
        try {
            Field field = findField(cls, fieldName);
            field.setAccessible(true);
            Object result = field.get(source);
            // 防止类型不匹配
            if (filedType.isInstance(result)) {
                return (T) result;
            }
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }


    /**
     * 根据方法名依次从自身到父类查找某个方法
     * @param clazz class
     * @param methodName method name
     * @param paramTypes type of method params
     * @return method to find
     * @throws NoSuchMethodException when method not found
     */
    public static Method findMethod(Class<?> clazz, String methodName,Class<?> ... paramTypes) throws NoSuchMethodException {

        Method method = null;

        while (method == null && clazz != null){
            try {
                method = clazz.getDeclaredMethod(methodName,paramTypes);
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        if (method == null){
            throw new NoSuchMethodException(methodName);
        }
        return method;
    }

    public static boolean setFieldValue(Object source, String fieldName,  Object value) {
        Field field = findField(source.getClass(), fieldName);
        if (field == null) {
            return false;
        }
        return setFieldValue(source, field, value);
    }

    public static boolean setFieldValue(Object source, Field field, Object value) {
        if (!field.getType().isInstance(value)) {
            return false;
        }
        int modify = 0;
        Field modifiersField = null;
        boolean staticFinal = false;
        try {
            modify = field.getModifiers();
            //final修饰的基本类型不可修改
            if (field.getType().isPrimitive() && Modifier.isFinal(modify)) {
                return false;
            }
            //获取访问权限
            if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                field.setAccessible(true);
            }
            //static final同时修饰
            staticFinal = Modifier.isStatic(modify) && Modifier.isFinal(modify);
            if (staticFinal) {
                setFinalStatic(field,value);
                return true;
            }
            //按照类型调用设置方法
            if (value != null && field.getType().isPrimitive()) {
                if (int.class.equals(field.getType()) && value instanceof Number) {
                    field.setInt(source, ((Number) value).intValue());
                } else if (boolean.class.equals(field.getType()) && value instanceof Boolean) {
                    field.setBoolean(source, (Boolean) value);
                } else if (byte.class.equals(field.getType()) && value instanceof Byte) {
                    field.setByte(source, (Byte) value);
                } else if (char.class.equals(field.getType()) && value instanceof Character) {
                    field.setChar(source, (Character) value);
                } else if (double.class.equals(field.getType()) && value instanceof Number) {
                    field.setDouble(source, ((Number) value).doubleValue());
                } else if (long.class.equals(field.getType()) && value instanceof Number) {
                    field.setLong(source, ((Number) value).longValue());
                } else if (float.class.equals(field.getType()) && value instanceof Number) {
                    field.setFloat(source, ((Number) value).floatValue());
                } else if (short.class.equals(field.getType()) && value instanceof Number) {
                    field.setShort(source, ((Number) value).shortValue());
                } else {
                    return false;
                }
            } else {
                field.set(source, value);
            }
        } catch (Exception e) {
            return false;
        } finally {
            try {
                //权限还原
                if (field != null) {
                    if (staticFinal && modifiersField != null) {
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        modifiersField.setAccessible(false);
                    }
                    if (!Modifier.isPublic(modify) || Modifier.isFinal(modify)) {
                        field.setAccessible(false);
                    }
                }
            } catch (IllegalAccessException e) {
                //
            }
        }
        return true;
    }
    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        if (!Modifier.isStatic(field.getModifiers())){
            return;
        }
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    /**
     * 从对象里获取指定类型的属性
     * @param bean bean
     * @param type field of type
     * @return fields
     * @param <E> type
     */
    @SuppressWarnings("unchecked")
    public static <E> Map<Field,E> findSpecificTypeFields(Object bean, Class<E> type) {
        if (bean == null){
            return new HashMap<>();
        }
        Class<?> clazz = bean.getClass();
        List<Field> fields = new ArrayList<>();
        while (clazz != null){
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        Map<Field,E> result = new HashMap<>();
        for (Field field : fields){
            if (type.isAssignableFrom(field.getType())){
                field.setAccessible(true);
                E e;
                try {
                    e = (E) field.get(bean);
                    result.put(field,e);
                } catch (IllegalAccessException ignored) {

                }
            }
        }
        return result;
    }

    public static  boolean anyFinalMethods(Object object, Class<?> iface) {
        try {
            for (Method method : getDeclaredMethods(iface)) {
                Method m = ReflectionUtils.findMethod(object.getClass(), method.getName(),
                        method.getParameterTypes());
                if (m != null && Modifier.isFinal(m.getModifiers())) {
                    return true;
                }
            }
        } catch (IllegalAccessError er) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while trying to access methods", er);
            }
        }
        return false;
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Method[] result = null;
        try {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
            if (defaultMethods != null) {
                result = new Method[declaredMethods.length + defaultMethods.size()];
                System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
                int index = declaredMethods.length;
                for (Method defaultMethod : defaultMethods) {
                    result[index] = defaultMethod;
                    index++;
                }
            }
            else {
                result = declaredMethods;
            }
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                    "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
        }
        return result;
    }
    public static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
        List<Method> result = null;
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
                    if (result == null) {
                        result = new LinkedList<>();
                    }
                    result.add(ifcMethod);
                }
            }
        }
        return result;
    }

}
