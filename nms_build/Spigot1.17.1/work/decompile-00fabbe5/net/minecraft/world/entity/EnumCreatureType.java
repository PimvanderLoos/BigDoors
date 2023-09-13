package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.INamable;

public enum EnumCreatureType implements INamable {

    MONSTER("monster", 70, false, false, 128), CREATURE("creature", 10, true, true, 128), AMBIENT("ambient", 15, true, false, 128), UNDERGROUND_WATER_CREATURE("underground_water_creature", 5, true, false, 128), WATER_CREATURE("water_creature", 5, true, false, 128), WATER_AMBIENT("water_ambient", 20, true, false, 64), MISC("misc", -1, true, true, 128);

    public static final Codec<EnumCreatureType> CODEC = INamable.a(EnumCreatureType::values, EnumCreatureType::a);
    private static final Map<String, EnumCreatureType> BY_NAME = (Map) Arrays.stream(values()).collect(Collectors.toMap(EnumCreatureType::a, (enumcreaturetype) -> {
        return enumcreaturetype;
    }));
    private final int max;
    private final boolean isFriendly;
    private final boolean isPersistent;
    private final String name;
    private final int noDespawnDistance = 32;
    private final int despawnDistance;

    private EnumCreatureType(String s, int i, boolean flag, boolean flag1, int j) {
        this.name = s;
        this.max = i;
        this.isFriendly = flag;
        this.isPersistent = flag1;
        this.despawnDistance = j;
    }

    public String a() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static EnumCreatureType a(String s) {
        return (EnumCreatureType) EnumCreatureType.BY_NAME.get(s);
    }

    public int b() {
        return this.max;
    }

    public boolean d() {
        return this.isFriendly;
    }

    public boolean e() {
        return this.isPersistent;
    }

    public int f() {
        return this.despawnDistance;
    }

    public int g() {
        return 32;
    }
}
