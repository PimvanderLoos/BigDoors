package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;

public final class BiomeDesert extends BiomeBase {

    public BiomeDesert() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.H).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.DESERT).a(0.125F).b(0.05F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).a(CaveSoundSettings.b).a()).a((String) null).a((List) ImmutableList.of(new BiomeBase.d(0.5F, -0.5F, 0.0F, 0.0F, 1.0F))));
        this.a(BiomeDecoratorGroups.u);
        this.a(BiomeDecoratorGroups.a);
        this.a(BiomeDecoratorGroups.f);
        BiomeDecoratorGroups.ai(this);
        BiomeDecoratorGroups.b(this);
        this.a(BiomeDecoratorGroups.z);
        BiomeDecoratorGroups.d(this);
        BiomeDecoratorGroups.g(this);
        BiomeDecoratorGroups.h(this);
        BiomeDecoratorGroups.i(this);
        BiomeDecoratorGroups.j(this);
        BiomeDecoratorGroups.n(this);
        BiomeDecoratorGroups.W(this);
        BiomeDecoratorGroups.Y(this);
        BiomeDecoratorGroups.U(this);
        BiomeDecoratorGroups.ab(this);
        BiomeDecoratorGroups.af(this);
        BiomeDecoratorGroups.ao(this);
        BiomeDecoratorGroups.ah(this);
        BiomeDecoratorGroups.ar(this);
        this.a(EnumCreatureType.CREATURE, new BiomeBase.BiomeMeta(EntityTypes.RABBIT, 4, 2, 3));
        this.a(EnumCreatureType.AMBIENT, new BiomeBase.BiomeMeta(EntityTypes.BAT, 10, 8, 8));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SPIDER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SKELETON, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.CREEPER, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.SLIME, 100, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 1, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.WITCH, 5, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE, 19, 4, 4));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ZOMBIE_VILLAGER, 1, 1, 1));
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.HUSK, 80, 4, 4));
    }
}
