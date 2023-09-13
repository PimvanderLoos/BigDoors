package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

public class LootEntityProperties {

    private static final Map<MinecraftKey, LootEntityProperty.a<?>> a = Maps.newHashMap();
    private static final Map<Class<? extends LootEntityProperty>, LootEntityProperty.a<?>> b = Maps.newHashMap();

    public static <T extends LootEntityProperty> void a(LootEntityProperty.a<? extends T> lootentityproperty_a) {
        MinecraftKey minecraftkey = lootentityproperty_a.a();
        Class<T> oclass = lootentityproperty_a.b();

        if (LootEntityProperties.a.containsKey(minecraftkey)) {
            throw new IllegalArgumentException("Can't re-register entity property name " + minecraftkey);
        } else if (LootEntityProperties.b.containsKey(oclass)) {
            throw new IllegalArgumentException("Can't re-register entity property class " + oclass.getName());
        } else {
            LootEntityProperties.a.put(minecraftkey, lootentityproperty_a);
            LootEntityProperties.b.put(oclass, lootentityproperty_a);
        }
    }

    public static LootEntityProperty.a<?> a(MinecraftKey minecraftkey) {
        LootEntityProperty.a<?> lootentityproperty_a = (LootEntityProperty.a) LootEntityProperties.a.get(minecraftkey);

        if (lootentityproperty_a == null) {
            throw new IllegalArgumentException("Unknown loot entity property '" + minecraftkey + "'");
        } else {
            return lootentityproperty_a;
        }
    }

    public static <T extends LootEntityProperty> LootEntityProperty.a<T> a(T t0) {
        LootEntityProperty.a<?> lootentityproperty_a = (LootEntityProperty.a) LootEntityProperties.b.get(t0.getClass());

        if (lootentityproperty_a == null) {
            throw new IllegalArgumentException("Unknown loot entity property " + t0);
        } else {
            return lootentityproperty_a;
        }
    }

    static {
        a((LootEntityProperty.a) (new LootEntityPropertyOnFire.a()));
    }
}
