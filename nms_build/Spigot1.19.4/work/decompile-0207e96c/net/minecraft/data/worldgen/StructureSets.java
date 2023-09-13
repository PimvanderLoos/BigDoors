package net.minecraft.data.worldgen;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public interface StructureSets {

    static void bootstrap(BootstapContext<StructureSet> bootstapcontext) {
        HolderGetter<Structure> holdergetter = bootstapcontext.lookup(Registries.STRUCTURE);
        HolderGetter<BiomeBase> holdergetter1 = bootstapcontext.lookup(Registries.BIOME);
        Holder.c<StructureSet> holder_c = bootstapcontext.register(BuiltinStructureSets.VILLAGES, new StructureSet(List.of(StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.VILLAGE_PLAINS)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.VILLAGE_DESERT)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.VILLAGE_SAVANNA)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.VILLAGE_SNOWY)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.VILLAGE_TAIGA))), new RandomSpreadStructurePlacement(34, 8, RandomSpreadType.LINEAR, 10387312)));

        bootstapcontext.register(BuiltinStructureSets.DESERT_PYRAMIDS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.DESERT_PYRAMID), new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357617)));
        bootstapcontext.register(BuiltinStructureSets.IGLOOS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.IGLOO), new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357618)));
        bootstapcontext.register(BuiltinStructureSets.JUNGLE_TEMPLES, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.JUNGLE_TEMPLE), new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357619)));
        bootstapcontext.register(BuiltinStructureSets.SWAMP_HUTS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.SWAMP_HUT), new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357620)));
        bootstapcontext.register(BuiltinStructureSets.PILLAGER_OUTPOSTS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.PILLAGER_OUTPOST), new RandomSpreadStructurePlacement(BaseBlockPosition.ZERO, StructurePlacement.c.LEGACY_TYPE_1, 0.2F, 165745296, Optional.of(new StructurePlacement.a(holder_c, 10)), 32, 8, RandomSpreadType.LINEAR)));
        bootstapcontext.register(BuiltinStructureSets.ANCIENT_CITIES, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.ANCIENT_CITY), new RandomSpreadStructurePlacement(24, 8, RandomSpreadType.LINEAR, 20083232)));
        bootstapcontext.register(BuiltinStructureSets.OCEAN_MONUMENTS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.OCEAN_MONUMENT), new RandomSpreadStructurePlacement(32, 5, RandomSpreadType.TRIANGULAR, 10387313)));
        bootstapcontext.register(BuiltinStructureSets.WOODLAND_MANSIONS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.WOODLAND_MANSION), new RandomSpreadStructurePlacement(80, 20, RandomSpreadType.TRIANGULAR, 10387319)));
        bootstapcontext.register(BuiltinStructureSets.BURIED_TREASURES, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.BURIED_TREASURE), new RandomSpreadStructurePlacement(new BaseBlockPosition(9, 0, 9), StructurePlacement.c.LEGACY_TYPE_2, 0.01F, 0, Optional.empty(), 1, 0, RandomSpreadType.LINEAR)));
        bootstapcontext.register(BuiltinStructureSets.MINESHAFTS, new StructureSet(List.of(StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.MINESHAFT)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.MINESHAFT_MESA))), new RandomSpreadStructurePlacement(BaseBlockPosition.ZERO, StructurePlacement.c.LEGACY_TYPE_3, 0.004F, 0, Optional.empty(), 1, 0, RandomSpreadType.LINEAR)));
        bootstapcontext.register(BuiltinStructureSets.RUINED_PORTALS, new StructureSet(List.of(StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_STANDARD)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_DESERT)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_JUNGLE)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_SWAMP)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_MOUNTAIN)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_OCEAN)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.RUINED_PORTAL_NETHER))), new RandomSpreadStructurePlacement(40, 15, RandomSpreadType.LINEAR, 34222645)));
        bootstapcontext.register(BuiltinStructureSets.SHIPWRECKS, new StructureSet(List.of(StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.SHIPWRECK)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.SHIPWRECK_BEACHED))), new RandomSpreadStructurePlacement(24, 4, RandomSpreadType.LINEAR, 165745295)));
        bootstapcontext.register(BuiltinStructureSets.OCEAN_RUINS, new StructureSet(List.of(StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.OCEAN_RUIN_COLD)), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.OCEAN_RUIN_WARM))), new RandomSpreadStructurePlacement(20, 8, RandomSpreadType.LINEAR, 14357621)));
        bootstapcontext.register(BuiltinStructureSets.NETHER_COMPLEXES, new StructureSet(List.of(StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.FORTRESS), 2), StructureSet.entry(holdergetter.getOrThrow(BuiltinStructures.BASTION_REMNANT), 3)), new RandomSpreadStructurePlacement(27, 4, RandomSpreadType.LINEAR, 30084232)));
        bootstapcontext.register(BuiltinStructureSets.NETHER_FOSSILS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.NETHER_FOSSIL), new RandomSpreadStructurePlacement(2, 1, RandomSpreadType.LINEAR, 14357921)));
        bootstapcontext.register(BuiltinStructureSets.END_CITIES, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.END_CITY), new RandomSpreadStructurePlacement(20, 11, RandomSpreadType.TRIANGULAR, 10387313)));
        bootstapcontext.register(BuiltinStructureSets.STRONGHOLDS, new StructureSet(holdergetter.getOrThrow(BuiltinStructures.STRONGHOLD), new ConcentricRingsStructurePlacement(32, 3, 128, holdergetter1.getOrThrow(BiomeTags.STRONGHOLD_BIASED_TO))));
    }
}
