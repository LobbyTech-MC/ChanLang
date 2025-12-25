package cn.whiteg.chanlang;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


public class NMSUtils {
    public static Entity getNmsPlayer(Player player) {
        return getNmsEntity(player);
    }

    public static Entity getNmsEntity(org.bukkit.entity.Entity bukkitEntity) {
        try{
            return (Entity) bukkitEntity.getClass().getMethod("getHandle").invoke(bukkitEntity);
        }catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
            throw new RuntimeException(new NoSuchFieldException("getHandle"));
        }
    }

    //根据类型获取Field
    public static Field getFieldFormType(Class<?> clazz,Class<?> type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(type)) return declaredField;
        }
        throw new NoSuchFieldException(type.getName());
    }

    //根据类型获取Field
    public static Field getFieldFormType(Class<?> clazz,String type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getAnnotatedType().getType().getTypeName().equals(type)) return declaredField;
        }
        throw new NoSuchFieldException(type);
    }

    //从数组结构中查找Field
    public static Field getFieldFormStructure(Class<?> clazz,Class<?>[] st,int index) throws NoSuchFieldException {
        var fields = clazz.getDeclaredFields();
        int end = fields.length - st.length;

        loop:
        for (int i = 0; i < end; i++) {
            for (int i1 = 0; i1 < st.length; i1++) {
                var f1 = fields[i + i1];
                var aClass = st[i1];
                if (!f1.getType().equals(aClass)){
                    continue loop;
                }
            }
            return fields[i + index];
        }
        throw new NoSuchFieldException(Arrays.toString(st));
    }

    //根据实体Class获取实体Types
    public static <T extends Entity> Entity getEntityType(Class<? extends Entity> clazz) {
        String name = Entity.class.getSimpleName().concat("<").concat(clazz.getSimpleName()).concat(">");
        for (Field field : Entity.class.getFields()) {
            try{
                if (field.getAnnotatedType().getType().getTypeName().equals(name))
                    //noinspection unchecked
                    return (Entity) field.get(null);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
