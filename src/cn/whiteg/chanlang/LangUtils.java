package cn.whiteg.chanlang;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LangUtils {
    static private Method getItemMethod;
    static private Method getBlockMethod;
    static private Method itemGetNameMethod;
    static private Method blockGetItem;

    static {
        try{
            Class craftMagicNumbersClass = Class.forName("org.bukkit.craftbukkit." + ChanLang.getServerVersion() + ".util.CraftMagicNumbers");
            getItemMethod = craftMagicNumbersClass.getMethod("getItem",Material.class);
            getBlockMethod = craftMagicNumbersClass.getMethod("getBlock",Material.class);

            itemGetNameMethod = ChanLang.getNmsClass("Item").getMethod("getName");
            blockGetItem = ChanLang.getNmsClass("Block").getMethod("getItem");
        }catch (ClassNotFoundException | NoSuchMethodException e){
            e.printStackTrace();
        }

    }

    /**
     * 获取附魔效果名称
     *
     * @param enchantment 附魔效果
     * @return 名称
     */
    public static String getEnchantmentName(Enchantment enchantment) {
        return getMessage("enchantment.minecraft." + enchantment.getKey().getKey());
    }

    /**
     * 获取附魔等级名称
     *
     * @param i 等级
     * @return 等级名称
     */
    public static String getEnchantmentLvlName(int i) {
        return getMessage("enchantment.level." + i);
    }

    /**
     * 获取药水效果名称
     *
     * @param potionEffect 药水效果
     * @return 名称
     */
    public static String getPotionEffectName(PotionEffectType potionEffect) {
        return getMessage("effect.minecraft." + potionEffect.getName().toLowerCase());
    }

    /**
     * 获取药水等级
     *
     * @param i 等级
     * @return 等级名称
     */
    public static String getPotionEffectLvlName(int i) {
        return getMessage("potion.potency." + i);
    }

    /**
     * 获取id的名称
     *
     * @param mat 物品ID
     * @return 名称
     */
    public static String getMaterialName(Material mat) {
        try{
            Object item = getNmsItem(mat);
            if (item == null){
                return mat.name();
            }
            return getMessage((String) itemGetNameMethod.invoke(item));
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return mat.name();
    }

    /**
     * 获取id的名称
     *
     * @param mat 物品ID
     * @param def 如果没有返回的默认值
     * @return 名称
     */
    public static String getMaterialName(Material mat,String def) {
        try{
            Object item = getNmsItem(mat);
            if (item == null){
                return def;
            }
            return getMessage((String) itemGetNameMethod.invoke(item));
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return def;
    }

    /**
     * 获取物品语言文件里的名称
     *
     * @param item 物品堆
     * @return 物品的名称
     */
    public static String getI18NDisplayName(ItemStack item) {
        return getMaterialName(item.getType());
    }

    /**
     * @param item 物品
     * @param def  如果语言文件里没有返回的默认值
     * @return 物品名称
     */
    public static String getI18NDisplayName(ItemStack item,String def) {
        return getMaterialName(item.getType(),def);
    }

    /**
     * 获取物品的显示名称
     *
     * @param itemStack 物品堆
     * @return 如果物品有自定义物品则返回物品的自定义名称，如果没有就返回语言文件的名称
     */
    public static String getItemDisplayName(ItemStack itemStack) {
        if (itemStack.hasItemMeta()){
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) return meta.getDisplayName();
        }
        return getMaterialName(itemStack.getType());
    }

    /**
     * 获取实体类型名称
     *
     * @param type 实体类型
     * @return 名称
     */
    public static String getEntityTypeName(EntityType type) {
        try{
            return getMessage("entity.minecraft." + type.getKey().getKey());
        }catch (IllegalArgumentException e){
            return type.name();
        }
    }

    /**
     * 获取实体名称
     * @param entity 实体
     * @return 如果实体有自定义名称就返回名称，没有就返回实体类型名称
     */
    public static String getEntityName(Entity entity) {
        String custName = entity.getCustomName();
        if (custName == null) getEntityTypeName(entity.getType());
        return custName;
    }

    public static Object getNmsItem(Material mat) {
        try{
            Object item = getItemMethod.invoke(null,mat);
            if (item == null){
                Object block = getBlockMethod.invoke(null,mat);
                if (block != null){
                    return blockGetItem.invoke(block);
                }
            }
            return item;
        }catch (IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取语言的值
     *
     * @param key key
     * @return 返回语言值，如果没有就返回key
     */
    public static String getMessage(String key) {
        return ChanLang.getLangMap().getOrDefault(key,key);
    }

    /**
     * 获取语言的值
     *
     * @param key key
     * @param def 默认值
     * @return 如果有就返回语言，没有就返回 @param def
     */
    public static String getMessage(String key,String def) {
        return ChanLang.getLangMap().getOrDefault(key,def);
    }

}
