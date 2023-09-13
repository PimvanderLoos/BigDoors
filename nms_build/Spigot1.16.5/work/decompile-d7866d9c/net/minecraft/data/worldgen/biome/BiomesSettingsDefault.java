package net.minecraft.data.worldgen.biome;

import net.minecraft.core.particles.Particles;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.data.worldgen.BiomeSettings;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.data.worldgen.WorldGenCarvers;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeParticles;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.biome.CaveSound;
import net.minecraft.world.level.biome.CaveSoundSettings;
import net.minecraft.world.level.levelgen.WorldGenStage;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceConfigurationBase;

public class BiomesSettingsDefault {

    private static int a(float f) {
        float f1 = f / 3.0F;

        f1 = MathHelper.a(f1, -1.0F, 1.0F);
        return MathHelper.f(0.62222224F - f1 * 0.05F, 0.5F + f1 * 0.1F, 1.0F);
    }

    public static BiomeBase a(float f, float f1, float f2, boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.WOLF, 8, 4, 4));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 4, 2, 3));
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.FOX, 8, 2, 4));
        if (flag) {
            BiomeSettings.c(biomesettingsmobs_a);
        } else {
            BiomeSettings.b(biomesettingsmobs_a);
            BiomeSettings.b(biomesettingsmobs_a, 100, 25, 100);
        }

        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.i);

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.p(biomesettingsgeneration_a);
        BiomeSettings.q(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, flag ? BiomeDecoratorGroups.TREES_GIANT_SPRUCE : BiomeDecoratorGroups.TREES_GIANT);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.T(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.s(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.TAIGA).a(f).b(f1).c(f2).d(0.8F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(f2)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        BiomeSettings.c(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.N(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        if (flag) {
            BiomeSettings.z(biomesettingsgeneration_a);
        } else {
            BiomeSettings.x(biomesettingsgeneration_a);
        }

        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.O(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.FOREST).a(f).b(f1).c(0.6F).d(0.6F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.6F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a() {
        return a(0.1F, 0.2F, 40, 2, 3);
    }

    public static BiomeBase b() {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.h(biomesettingsmobs_a);
        return a(0.1F, 0.2F, 0.8F, false, true, false, biomesettingsmobs_a);
    }

    public static BiomeBase c() {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.h(biomesettingsmobs_a);
        return a(0.2F, 0.4F, 0.8F, false, true, true, biomesettingsmobs_a);
    }

    public static BiomeBase d() {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.h(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PARROT, 10, 1, 1)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.OCELOT, 2, 1, 1));
        return a(0.2F, 0.4F, 0.9F, false, false, true, biomesettingsmobs_a);
    }

    public static BiomeBase e() {
        return a(0.45F, 0.3F, 10, 1, 1);
    }

    public static BiomeBase f() {
        return a(0.1F, 0.2F, 40, 2);
    }

    public static BiomeBase g() {
        return a(0.45F, 0.3F, 10, 1);
    }

    private static BiomeBase a(float f, float f1, int i, int j, int k) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.h(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PARROT, i, 1, j)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.OCELOT, 2, 1, k)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PANDA, 1, 1, 2));
        biomesettingsmobs_a.a();
        return a(f, f1, 0.9F, false, false, false, biomesettingsmobs_a);
    }

    private static BiomeBase a(float f, float f1, int i, int j) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.h(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PARROT, i, 1, j)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PANDA, 80, 1, 2)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.OCELOT, 2, 1, 1));
        return a(f, f1, 0.9F, true, false, false, biomesettingsmobs_a);
    }

    private static BiomeBase a(float f, float f1, float f2, boolean flag, boolean flag1, boolean flag2, BiomeSettingsMobs.a biomesettingsmobs_a) {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        if (!flag1 && !flag2) {
            biomesettingsgeneration_a.a(StructureFeatures.e);
        }

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.A);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        if (flag) {
            BiomeSettings.u(biomesettingsgeneration_a);
        } else {
            if (!flag1 && !flag2) {
                BiomeSettings.t(biomesettingsgeneration_a);
            }

            if (flag1) {
                BiomeSettings.F(biomesettingsgeneration_a);
            } else {
                BiomeSettings.E(biomesettingsgeneration_a);
            }
        }

        BiomeSettings.V(biomesettingsgeneration_a);
        BiomeSettings.I(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.ac(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.JUNGLE).a(f).b(f1).c(0.95F).d(f2).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.95F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> worldgensurfacecomposite, boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.LLAMA, 5, 4, 6));
        BiomeSettings.c(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(worldgensurfacecomposite);

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.C);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        if (flag) {
            BiomeSettings.D(biomesettingsgeneration_a);
        } else {
            BiomeSettings.C(biomesettingsgeneration_a);
        }

        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.l(biomesettingsgeneration_a);
        BiomeSettings.m(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.EXTREME_HILLS).a(f).b(f1).c(0.2F).d(0.3F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.2F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, boolean flag, boolean flag1, boolean flag2) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.f(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.d);

        if (flag) {
            biomesettingsgeneration_a.a(StructureFeatures.u);
            biomesettingsgeneration_a.a(StructureFeatures.a);
        }

        if (flag1) {
            biomesettingsgeneration_a.a(StructureFeatures.f);
        }

        if (flag2) {
            BiomeSettings.ag(biomesettingsgeneration_a);
        }

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.z);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.g(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.S(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.ad(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.af(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.DESERT).a(f).b(f1).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(2.0F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.d(biomesettingsmobs_a);
        if (!flag) {
            biomesettingsmobs_a.a();
        }

        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        if (!flag) {
            biomesettingsgeneration_a.a(StructureFeatures.t).a(StructureFeatures.a);
        }

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.Y(biomesettingsgeneration_a);
        if (flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_SUNFLOWER);
        }

        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.R(biomesettingsgeneration_a);
        if (flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_SUGAR_CANE);
        }

        BiomeSettings.Z(biomesettingsgeneration_a);
        if (flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.PATCH_PUMPKIN);
        } else {
            BiomeSettings.aa(biomesettingsgeneration_a);
        }

        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.PLAINS).a(0.125F).b(0.05F).c(0.8F).d(0.4F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.8F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    private static BiomeBase a(BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.i(biomesettingsmobs_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.THEEND).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(10518688).d(0).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase h() {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.e);

        return a(biomesettingsgeneration_a);
    }

    public static BiomeBase i() {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.e).a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.END_SPIKE);

        return a(biomesettingsgeneration_a);
    }

    public static BiomeBase j() {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.e).a(StructureFeatures.q);

        return a(biomesettingsgeneration_a);
    }

    public static BiomeBase k() {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.e).a(StructureFeatures.q).a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.END_GATEWAY).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.CHORUS_PLANT);

        return a(biomesettingsgeneration_a);
    }

    public static BiomeBase l() {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.e).a(WorldGenStage.Decoration.RAW_GENERATION, BiomeDecoratorGroups.END_ISLAND_DECORATED);

        return a(biomesettingsgeneration_a);
    }

    public static BiomeBase a(float f, float f1) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.g(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.n);

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.Q(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.MUSHROOM).a(f).b(f1).c(0.9F).d(1.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.9F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    private static BiomeBase a(float f, float f1, float f2, boolean flag, boolean flag1, BiomeSettingsMobs.a biomesettingsmobs_a) {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(flag1 ? WorldGenSurfaceComposites.r : WorldGenSurfaceComposites.j);

        if (!flag && !flag1) {
            biomesettingsgeneration_a.a(StructureFeatures.v).a(StructureFeatures.a);
        }

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(flag ? StructureFeatures.C : StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        if (!flag1) {
            BiomeSettings.J(biomesettingsgeneration_a);
        }

        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        if (flag1) {
            BiomeSettings.B(biomesettingsgeneration_a);
            BiomeSettings.U(biomesettingsgeneration_a);
            BiomeSettings.K(biomesettingsgeneration_a);
        } else {
            BiomeSettings.A(biomesettingsgeneration_a);
            BiomeSettings.V(biomesettingsgeneration_a);
            BiomeSettings.L(biomesettingsgeneration_a);
        }

        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.SAVANNA).a(f).b(f1).c(f2).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(f2)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, float f2, boolean flag, boolean flag1) {
        BiomeSettingsMobs.a biomesettingsmobs_a = x();

        return a(f, f1, f2, flag, flag1, biomesettingsmobs_a);
    }

    private static BiomeSettingsMobs.a x() {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.HORSE, 1, 2, 6)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.DONKEY, 1, 1, 1));
        BiomeSettings.c(biomesettingsmobs_a);
        return biomesettingsmobs_a;
    }

    public static BiomeBase m() {
        BiomeSettingsMobs.a biomesettingsmobs_a = x();

        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.LLAMA, 8, 4, 4));
        return a(1.5F, 0.025F, 1.0F, true, false, biomesettingsmobs_a);
    }

    private static BiomeBase a(WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> worldgensurfacecomposite, float f, float f1, boolean flag, boolean flag1) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.c(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(worldgensurfacecomposite);

        BiomeSettings.a(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(flag ? StructureFeatures.C : StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.k(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        if (flag1) {
            BiomeSettings.G(biomesettingsgeneration_a);
        }

        BiomeSettings.M(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.ab(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.MESA).a(f).b(f1).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(2.0F)).e(10387789).f(9470285).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase b(float f, float f1, boolean flag) {
        return a(WorldGenSurfaceComposites.a, f, f1, flag, false);
    }

    public static BiomeBase b(float f, float f1) {
        return a(WorldGenSurfaceComposites.w, f, f1, true, true);
    }

    public static BiomeBase n() {
        return a(WorldGenSurfaceComposites.f, 0.1F, 0.2F, true, false);
    }

    private static BiomeBase a(BiomeSettingsMobs.a biomesettingsmobs_a, int i, int j, boolean flag, BiomeSettingsGeneration.a biomesettingsgeneration_a) {
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.OCEAN).a(flag ? -1.8F : -1.0F).b(0.1F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(i).c(j).a(12638463).d(a(0.5F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    private static BiomeSettingsGeneration.a a(WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> worldgensurfacecomposite, boolean flag, boolean flag1, boolean flag2) {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(worldgensurfacecomposite);
        StructureFeature<?, ?> structurefeature = flag1 ? StructureFeatures.n : StructureFeatures.m;

        if (flag2) {
            if (flag) {
                biomesettingsgeneration_a.a(StructureFeatures.l);
            }

            BiomeSettings.c(biomesettingsgeneration_a);
            biomesettingsgeneration_a.a(structurefeature);
        } else {
            biomesettingsgeneration_a.a(structurefeature);
            if (flag) {
                biomesettingsgeneration_a.a(StructureFeatures.l);
            }

            BiomeSettings.c(biomesettingsgeneration_a);
        }

        biomesettingsgeneration_a.a(StructureFeatures.D);
        BiomeSettings.e(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.w(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        return biomesettingsgeneration_a;
    }

    public static BiomeBase b(boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a, 3, 4, 15);
        biomesettingsmobs_a.a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.SALMON, 15, 1, 5));
        boolean flag1 = !flag;
        BiomeSettingsGeneration.a biomesettingsgeneration_a = a(WorldGenSurfaceComposites.j, flag, false, flag1);

        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, flag ? BiomeDecoratorGroups.SEAGRASS_DEEP_COLD : BiomeDecoratorGroups.SEAGRASS_COLD);
        BiomeSettings.ai(biomesettingsgeneration_a);
        BiomeSettings.ah(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return a(biomesettingsmobs_a, 4020182, 329011, flag, biomesettingsgeneration_a);
    }

    public static BiomeBase c(boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a, 1, 4, 10);
        biomesettingsmobs_a.a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.DOLPHIN, 1, 1, 2));
        BiomeSettingsGeneration.a biomesettingsgeneration_a = a(WorldGenSurfaceComposites.j, flag, false, true);

        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, flag ? BiomeDecoratorGroups.SEAGRASS_DEEP : BiomeDecoratorGroups.SEAGRASS_NORMAL);
        BiomeSettings.ai(biomesettingsgeneration_a);
        BiomeSettings.ah(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return a(biomesettingsmobs_a, 4159204, 329011, flag, biomesettingsgeneration_a);
    }

    public static BiomeBase d(boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        if (flag) {
            BiomeSettings.a(biomesettingsmobs_a, 8, 4, 8);
        } else {
            BiomeSettings.a(biomesettingsmobs_a, 10, 2, 15);
        }

        biomesettingsmobs_a.a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.PUFFERFISH, 5, 1, 3)).a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.TROPICAL_FISH, 25, 8, 8)).a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.DOLPHIN, 2, 1, 2));
        BiomeSettingsGeneration.a biomesettingsgeneration_a = a(WorldGenSurfaceComposites.q, flag, true, false);

        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, flag ? BiomeDecoratorGroups.SEAGRASS_DEEP_WARM : BiomeDecoratorGroups.SEAGRASS_WARM);
        if (flag) {
            BiomeSettings.ai(biomesettingsgeneration_a);
        }

        BiomeSettings.aj(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return a(biomesettingsmobs_a, 4566514, 267827, flag, biomesettingsgeneration_a);
    }

    public static BiomeBase o() {
        BiomeSettingsMobs.a biomesettingsmobs_a = (new BiomeSettingsMobs.a()).a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.PUFFERFISH, 15, 1, 3));

        BiomeSettings.a(biomesettingsmobs_a, 10, 4);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = a(WorldGenSurfaceComposites.h, false, true, false).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.WARM_OCEAN_VEGETATION).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SEAGRASS_WARM).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SEA_PICKLE);

        BiomeSettings.an(biomesettingsgeneration_a);
        return a(biomesettingsmobs_a, 4445678, 270131, false, biomesettingsgeneration_a);
    }

    public static BiomeBase p() {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a, 5, 1);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, 5, 1, 1));
        BiomeSettingsGeneration.a biomesettingsgeneration_a = a(WorldGenSurfaceComposites.h, true, true, false).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SEAGRASS_DEEP_WARM);

        BiomeSettings.ai(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return a(biomesettingsmobs_a, 4445678, 270131, true, biomesettingsgeneration_a);
    }

    public static BiomeBase e(boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = (new BiomeSettingsMobs.a()).a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.SQUID, 1, 1, 4)).a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.SALMON, 15, 1, 5)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.POLAR_BEAR, 1, 1, 2));

        BiomeSettings.c(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, 5, 1, 1));
        float f = flag ? 0.5F : 0.0F;
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.g);

        biomesettingsgeneration_a.a(StructureFeatures.m);
        if (flag) {
            biomesettingsgeneration_a.a(StructureFeatures.l);
        }

        BiomeSettings.c(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.D);
        BiomeSettings.e(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.al(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.am(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.w(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(flag ? BiomeBase.Precipitation.RAIN : BiomeBase.Precipitation.SNOW).a(BiomeBase.Geography.OCEAN).a(flag ? -1.8F : -1.0F).b(0.1F).c(f).a(BiomeBase.TemperatureModifier.FROZEN).d(0.5F).a((new BiomeFog.a()).b(3750089).c(329011).a(12638463).d(a(f)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    private static BiomeBase a(float f, float f1, boolean flag, BiomeSettingsMobs.a biomesettingsmobs_a) {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        if (flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FOREST_FLOWER_VEGETATION_COMMON);
        } else {
            BiomeSettings.N(biomesettingsgeneration_a);
        }

        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        if (flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FOREST_FLOWER_TREES);
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.FLOWER_FOREST);
            BiomeSettings.W(biomesettingsgeneration_a);
        } else {
            BiomeSettings.y(biomesettingsgeneration_a);
            BiomeSettings.U(biomesettingsgeneration_a);
            BiomeSettings.O(biomesettingsgeneration_a);
        }

        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.FOREST).a(f).b(f1).c(0.7F).d(0.8F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.7F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    private static BiomeSettingsMobs.a y() {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        BiomeSettings.c(biomesettingsmobs_a);
        return biomesettingsmobs_a;
    }

    public static BiomeBase c(float f, float f1) {
        BiomeSettingsMobs.a biomesettingsmobs_a = y().a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.WOLF, 5, 4, 4)).a();

        return a(f, f1, false, biomesettingsmobs_a);
    }

    public static BiomeBase q() {
        BiomeSettingsMobs.a biomesettingsmobs_a = y().a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 4, 2, 3));

        return a(0.1F, 0.4F, true, biomesettingsmobs_a);
    }

    public static BiomeBase a(float f, float f1, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.WOLF, 8, 4, 4)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.RABBIT, 4, 2, 3)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.FOX, 8, 2, 4));
        if (!flag && !flag1) {
            biomesettingsmobs_a.a();
        }

        BiomeSettings.c(biomesettingsmobs_a);
        float f2 = flag ? -0.5F : 0.25F;
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        if (flag2) {
            biomesettingsgeneration_a.a(StructureFeatures.x);
            biomesettingsgeneration_a.a(StructureFeatures.a);
        }

        if (flag3) {
            biomesettingsgeneration_a.a(StructureFeatures.g);
        }

        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(flag1 ? StructureFeatures.C : StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.q(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.v(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.X(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        if (flag) {
            BiomeSettings.r(biomesettingsgeneration_a);
        } else {
            BiomeSettings.s(biomesettingsgeneration_a);
        }

        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(flag ? BiomeBase.Precipitation.SNOW : BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.TAIGA).a(f).b(f1).c(f2).d(flag ? 0.4F : 0.8F).a((new BiomeFog.a()).b(flag ? 4020182 : 4159204).c(329011).a(12638463).d(a(f2)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase c(float f, float f1, boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        BiomeSettings.c(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        biomesettingsgeneration_a.a(StructureFeatures.d);
        BiomeSettings.b(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, flag ? BiomeDecoratorGroups.DARK_FOREST_VEGETATION_RED : BiomeDecoratorGroups.DARK_FOREST_VEGETATION_BROWN);
        BiomeSettings.N(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.O(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.FOREST).a(f).b(f1).c(0.7F).d(0.8F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.7F)).a(BiomeFog.GrassColor.DARK_FOREST).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase d(float f, float f1, boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        BiomeSettings.a(biomesettingsmobs_a);
        BiomeSettings.c(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SLIME, 1, 1, 1));
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.u);

        if (!flag) {
            biomesettingsgeneration_a.a(StructureFeatures.j);
        }

        biomesettingsgeneration_a.a(StructureFeatures.b);
        biomesettingsgeneration_a.a(StructureFeatures.B);
        BiomeSettings.d(biomesettingsgeneration_a);
        if (!flag) {
            BiomeSettings.ag(biomesettingsgeneration_a);
        }

        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.o(biomesettingsgeneration_a);
        BiomeSettings.P(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.ae(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        if (flag) {
            BiomeSettings.ag(biomesettingsgeneration_a);
        } else {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SEAGRASS_SWAMP);
        }

        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.SWAMP).a(f).b(f1).c(0.8F).d(0.9F).a((new BiomeFog.a()).b(6388580).c(2302743).a(12638463).d(a(0.8F)).e(6975545).a(BiomeFog.GrassColor.SWAMP).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, boolean flag, boolean flag1) {
        BiomeSettingsMobs.a biomesettingsmobs_a = (new BiomeSettingsMobs.a()).a(0.07F);

        BiomeSettings.e(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(flag ? WorldGenSurfaceComposites.l : WorldGenSurfaceComposites.j);

        if (!flag && !flag1) {
            biomesettingsgeneration_a.a(StructureFeatures.w).a(StructureFeatures.g);
        }

        BiomeSettings.b(biomesettingsgeneration_a);
        if (!flag && !flag1) {
            biomesettingsgeneration_a.a(StructureFeatures.a);
        }

        biomesettingsgeneration_a.a(flag1 ? StructureFeatures.C : StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        if (flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.ICE_SPIKE);
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.ICE_PATCH);
        }

        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.H(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.SNOW).a(BiomeBase.Geography.ICY).a(f).b(f1).c(0.0F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.0F)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, float f2, int i, boolean flag) {
        BiomeSettingsMobs.a biomesettingsmobs_a = (new BiomeSettingsMobs.a()).a(EnumCreatureType.WATER_CREATURE, new BiomeSettingsMobs.c(EntityTypes.SQUID, 2, 1, 4)).a(EnumCreatureType.WATER_AMBIENT, new BiomeSettingsMobs.c(EntityTypes.SALMON, 5, 1, 5));

        BiomeSettings.c(biomesettingsmobs_a);
        biomesettingsmobs_a.a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.DROWNED, flag ? 1 : 100, 1, 1));
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.j);

        biomesettingsgeneration_a.a(StructureFeatures.b);
        biomesettingsgeneration_a.a(StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.w(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        if (!flag) {
            biomesettingsgeneration_a.a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SEAGRASS_RIVER);
        }

        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(flag ? BiomeBase.Precipitation.SNOW : BiomeBase.Precipitation.RAIN).a(BiomeBase.Geography.RIVER).a(f).b(f1).c(f2).d(0.5F).a((new BiomeFog.a()).b(i).c(329011).a(12638463).d(a(f2)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase a(float f, float f1, float f2, float f3, int i, boolean flag, boolean flag1) {
        BiomeSettingsMobs.a biomesettingsmobs_a = new BiomeSettingsMobs.a();

        if (!flag1 && !flag) {
            biomesettingsmobs_a.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.TURTLE, 5, 2, 5));
        }

        BiomeSettings.c(biomesettingsmobs_a);
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(flag1 ? WorldGenSurfaceComposites.t : WorldGenSurfaceComposites.d);

        if (flag1) {
            BiomeSettings.b(biomesettingsgeneration_a);
        } else {
            biomesettingsgeneration_a.a(StructureFeatures.b);
            biomesettingsgeneration_a.a(StructureFeatures.r);
            biomesettingsgeneration_a.a(StructureFeatures.i);
        }

        biomesettingsgeneration_a.a(flag1 ? StructureFeatures.C : StructureFeatures.y);
        BiomeSettings.d(biomesettingsgeneration_a);
        BiomeSettings.f(biomesettingsgeneration_a);
        BiomeSettings.h(biomesettingsgeneration_a);
        BiomeSettings.i(biomesettingsgeneration_a);
        BiomeSettings.j(biomesettingsgeneration_a);
        BiomeSettings.n(biomesettingsgeneration_a);
        BiomeSettings.U(biomesettingsgeneration_a);
        BiomeSettings.W(biomesettingsgeneration_a);
        BiomeSettings.Z(biomesettingsgeneration_a);
        BiomeSettings.aa(biomesettingsgeneration_a);
        BiomeSettings.ak(biomesettingsgeneration_a);
        BiomeSettings.an(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(flag ? BiomeBase.Precipitation.SNOW : BiomeBase.Precipitation.RAIN).a(flag1 ? BiomeBase.Geography.NONE : BiomeBase.Geography.BEACH).a(f).b(f1).c(f2).d(f3).a((new BiomeFog.a()).b(i).c(329011).a(12638463).d(a(f2)).a(CaveSoundSettings.b).a()).a(biomesettingsmobs_a.b()).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase r() {
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.p);

        biomesettingsgeneration_a.a(WorldGenStage.Decoration.TOP_LAYER_MODIFICATION, BiomeDecoratorGroups.VOID_START_PLATFORM);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NONE).a(0.1F).b(0.2F).c(0.5F).d(0.5F).a((new BiomeFog.a()).b(4159204).c(329011).a(12638463).d(a(0.5F)).a(CaveSoundSettings.b).a()).a(BiomeSettingsMobs.b).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase s() {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.GHAST, 50, 4, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 100, 4, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 2, 4, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 1, 4, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.PIGLIN, 15, 4, 4)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).b();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.o).a(StructureFeatures.E).a(StructureFeatures.o).a(StructureFeatures.s).a(WorldGenStage.Features.AIR, WorldGenCarvers.f).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_LAVA);

        BiomeSettings.Z(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_OPEN).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_SOUL_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE_EXTRA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_NETHER).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_NETHER).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_MAGMA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_CLOSED);
        BiomeSettings.ao(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(3344392).d(a(2.0F)).a(SoundEffects.AMBIENT_NETHER_WASTES_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_NETHER_WASTES)).a()).a(biomesettingsmobs).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase t() {
        double d0 = 0.7D;
        double d1 = 0.15D;
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.SKELETON, 20, 5, 5)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.GHAST, 50, 4, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 1, 4, 4)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).a(EntityTypes.SKELETON, 0.7D, 0.15D).a(EntityTypes.GHAST, 0.7D, 0.15D).a(EntityTypes.ENDERMAN, 0.7D, 0.15D).a(EntityTypes.STRIDER, 0.7D, 0.15D).b();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.s).a(StructureFeatures.o).a(StructureFeatures.p).a(StructureFeatures.E).a(StructureFeatures.s).a(WorldGenStage.Features.AIR, WorldGenCarvers.f).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_LAVA).a(WorldGenStage.Decoration.LOCAL_MODIFICATIONS, BiomeDecoratorGroups.BASALT_PILLAR).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_OPEN).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE_EXTRA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_CRIMSON_ROOTS).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_SOUL_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_MAGMA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_CLOSED).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_SOUL_SAND);

        BiomeSettings.ao(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(1787717).d(a(2.0F)).a(new BiomeParticles(Particles.ASH, 0.00625F)).a(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_SOUL_SAND_VALLEY)).a()).a(biomesettingsmobs).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase u() {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.GHAST, 40, 1, 1)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.MAGMA_CUBE, 100, 2, 5)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).b();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.b).a(StructureFeatures.E).a(WorldGenStage.Features.AIR, WorldGenCarvers.f).a(StructureFeatures.o).a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.DELTA).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_LAVA_DOUBLE).a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.SMALL_BASALT_COLUMNS).a(WorldGenStage.Decoration.SURFACE_STRUCTURES, BiomeDecoratorGroups.LARGE_BASALT_COLUMNS).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.BASALT_BLOBS).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.BLACKSTONE_BLOBS).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_DELTA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_SOUL_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE_EXTRA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.BROWN_MUSHROOM_NETHER).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.RED_MUSHROOM_NETHER).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_MAGMA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_CLOSED_DOUBLE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_GOLD_DELTAS).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_QUARTZ_DELTAS);

        BiomeSettings.ap(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(4341314).a(6840176).d(a(2.0F)).a(new BiomeParticles(Particles.WHITE_ASH, 0.118093334F)).a(SoundEffects.AMBIENT_BASALT_DELTAS_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_BASALT_DELTAS)).a()).a(biomesettingsmobs).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase v() {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ZOMBIFIED_PIGLIN, 1, 2, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.HOGLIN, 9, 3, 4)).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.PIGLIN, 5, 3, 4)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).b();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.c).a(StructureFeatures.E).a(WorldGenStage.Features.AIR, WorldGenCarvers.f).a(StructureFeatures.o).a(StructureFeatures.s).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_LAVA);

        BiomeSettings.Z(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_OPEN).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE_EXTRA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_MAGMA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_CLOSED).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.WEEPING_VINES).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.CRIMSON_FUNGI).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.CRIMSON_FOREST_VEGETATION);
        BiomeSettings.ao(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(3343107).d(a(2.0F)).a(new BiomeParticles(Particles.CRIMSON_SPORE, 0.025F)).a(SoundEffects.AMBIENT_CRIMSON_FOREST_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_CRIMSON_FOREST)).a()).a(biomesettingsmobs).a(biomesettingsgeneration_a.a()).a();
    }

    public static BiomeBase w() {
        BiomeSettingsMobs biomesettingsmobs = (new BiomeSettingsMobs.a()).a(EnumCreatureType.MONSTER, new BiomeSettingsMobs.c(EntityTypes.ENDERMAN, 1, 4, 4)).a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.STRIDER, 60, 1, 2)).a(EntityTypes.ENDERMAN, 1.0D, 0.12D).b();
        BiomeSettingsGeneration.a biomesettingsgeneration_a = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.v).a(StructureFeatures.o).a(StructureFeatures.s).a(StructureFeatures.E).a(WorldGenStage.Features.AIR, WorldGenCarvers.f).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.SPRING_LAVA);

        BiomeSettings.Z(biomesettingsgeneration_a);
        biomesettingsgeneration_a.a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_OPEN).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.PATCH_SOUL_FIRE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE_EXTRA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.GLOWSTONE).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.ORE_MAGMA).a(WorldGenStage.Decoration.UNDERGROUND_DECORATION, BiomeDecoratorGroups.SPRING_CLOSED).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.WARPED_FUNGI).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.WARPED_FOREST_VEGETATION).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.NETHER_SPROUTS).a(WorldGenStage.Decoration.VEGETAL_DECORATION, BiomeDecoratorGroups.TWISTING_VINES);
        BiomeSettings.ao(biomesettingsgeneration_a);
        return (new BiomeBase.a()).a(BiomeBase.Precipitation.NONE).a(BiomeBase.Geography.NETHER).a(0.1F).b(0.2F).c(2.0F).d(0.0F).a((new BiomeFog.a()).b(4159204).c(329011).a(1705242).d(a(2.0F)).a(new BiomeParticles(Particles.WARPED_SPORE, 0.01428F)).a(SoundEffects.AMBIENT_WARPED_FOREST_LOOP).a(new CaveSoundSettings(SoundEffects.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0D)).a(new CaveSound(SoundEffects.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111D)).a(Musics.a(SoundEffects.MUSIC_NETHER_WARPED_FOREST)).a()).a(biomesettingsmobs).a(biomesettingsgeneration_a.a()).a();
    }
}
