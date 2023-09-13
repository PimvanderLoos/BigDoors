package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.INamable;

public enum EnumCreatureType implements INamable {

    MONSTER("monster", 70, false, false, 128), CREATURE("creature", 10, true, true, 128), AMBIENT("ambient", 15, true, false, 128), WATER_CREATURE("water_creature", 5, true, false, 128), WATER_AMBIENT("water_ambient", 20, true, false, 64), MISC("misc", -1, true, true, 128);

    public static final Codec<EnumCreatureType> g = INamable.a(EnumCreatureType::values, EnumCreatureType::a);
    private static final Map<String, EnumCreatureType> h = (Map) Arrays.stream(values()).collect(Collectors.toMap(EnumCreatureType::b, (enumcreaturetype) -> {
        return enumcreaturetype;
    }));
    private final int i;
    private final boolean j;
    private final boolean k;
    private final String l;
    private final int m = 32;
    private final int n;

    private EnumCreatureType(String s, int i, boolean flag, boolean flag1, int j) {
        this.l = s;
        this.i = i;
        this.j = flag;
        this.k = flag1;
        this.n = j;
    }

    public String b() {
        return this.l;
    }

    @Override
    public String getName() {
        return this.l;
    }

    public static EnumCreatureType a(String s) {
        return (EnumCreatureType) EnumCreatureType.h.get(s);
    }

    public int c() {
        return this.i;
    }

    public boolean d() {
        return this.j;
    }

    public boolean e() {
        return this.k;
    }

    public int f() {
        return this.n;
    }

    public int g() {
        return 32;
    }
}
