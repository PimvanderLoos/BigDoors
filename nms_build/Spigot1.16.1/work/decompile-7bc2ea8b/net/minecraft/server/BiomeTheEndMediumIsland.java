package net.minecraft.server;

public class BiomeTheEndMediumIsland extends BiomeBase {

    public BiomeTheEndMediumIsland() {
        super((new BiomeBase.a()).a(WorldGenSurface.S, WorldGenSurface.O).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.THEEND).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(10518688).a(CaveSoundSettings.b).a()).a((String) null));
        this.a(BiomeDecoratorGroups.q);
        this.a(EnumCreatureType.MONSTER, new BiomeBase.BiomeMeta(EntityTypes.ENDERMAN, 10, 4, 4));
    }
}
