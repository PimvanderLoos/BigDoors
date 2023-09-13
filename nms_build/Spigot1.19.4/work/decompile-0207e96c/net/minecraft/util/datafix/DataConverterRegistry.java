package net.minecraft.util.datafix;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.util.datafix.fixes.AbstractArrowPickupFix;
import net.minecraft.util.datafix.fixes.AddFlagIfNotPresentFix;
import net.minecraft.util.datafix.fixes.BlendingDataFix;
import net.minecraft.util.datafix.fixes.BlendingDataRemoveFromNetherEndFix;
import net.minecraft.util.datafix.fixes.BlockRenameFixWithJigsaw;
import net.minecraft.util.datafix.fixes.CauldronRenameFix;
import net.minecraft.util.datafix.fixes.CavesAndCliffsRenames;
import net.minecraft.util.datafix.fixes.ChunkConverterPalette;
import net.minecraft.util.datafix.fixes.ChunkDeleteIgnoredLightDataFix;
import net.minecraft.util.datafix.fixes.ChunkHeightAndBiomeFix;
import net.minecraft.util.datafix.fixes.ChunkProtoTickListFix;
import net.minecraft.util.datafix.fixes.ChunkRenamesFix;
import net.minecraft.util.datafix.fixes.CriteriaRenameFix;
import net.minecraft.util.datafix.fixes.DataConverterAddChoices;
import net.minecraft.util.datafix.fixes.DataConverterAdvancement;
import net.minecraft.util.datafix.fixes.DataConverterAdvancementBase;
import net.minecraft.util.datafix.fixes.DataConverterArmorStand;
import net.minecraft.util.datafix.fixes.DataConverterAttributes;
import net.minecraft.util.datafix.fixes.DataConverterBanner;
import net.minecraft.util.datafix.fixes.DataConverterBannerColour;
import net.minecraft.util.datafix.fixes.DataConverterBedBlock;
import net.minecraft.util.datafix.fixes.DataConverterBedItem;
import net.minecraft.util.datafix.fixes.DataConverterBiome;
import net.minecraft.util.datafix.fixes.DataConverterBiomeBase;
import net.minecraft.util.datafix.fixes.DataConverterBitStorageAlign;
import net.minecraft.util.datafix.fixes.DataConverterBlockEntityKeepPacked;
import net.minecraft.util.datafix.fixes.DataConverterBlockEntityUUID;
import net.minecraft.util.datafix.fixes.DataConverterBlockName;
import net.minecraft.util.datafix.fixes.DataConverterBlockRename;
import net.minecraft.util.datafix.fixes.DataConverterBook;
import net.minecraft.util.datafix.fixes.DataConverterCatType;
import net.minecraft.util.datafix.fixes.DataConverterChunkLightRemove;
import net.minecraft.util.datafix.fixes.DataConverterChunkStatus;
import net.minecraft.util.datafix.fixes.DataConverterChunkStatus2;
import net.minecraft.util.datafix.fixes.DataConverterChunkStructuresTemplateRename;
import net.minecraft.util.datafix.fixes.DataConverterColorlessShulkerEntity;
import net.minecraft.util.datafix.fixes.DataConverterCoral;
import net.minecraft.util.datafix.fixes.DataConverterCoralFan;
import net.minecraft.util.datafix.fixes.DataConverterCustomNameEntity;
import net.minecraft.util.datafix.fixes.DataConverterCustomNameItem;
import net.minecraft.util.datafix.fixes.DataConverterCustomNameTile;
import net.minecraft.util.datafix.fixes.DataConverterDropChances;
import net.minecraft.util.datafix.fixes.DataConverterDye;
import net.minecraft.util.datafix.fixes.DataConverterEntity;
import net.minecraft.util.datafix.fixes.DataConverterEntityBlockState;
import net.minecraft.util.datafix.fixes.DataConverterEntityCatSplit;
import net.minecraft.util.datafix.fixes.DataConverterEntityCodSalmon;
import net.minecraft.util.datafix.fixes.DataConverterEntityProjectileOwner;
import net.minecraft.util.datafix.fixes.DataConverterEntityPufferfish;
import net.minecraft.util.datafix.fixes.DataConverterEntityRavagerRename;
import net.minecraft.util.datafix.fixes.DataConverterEntityRename;
import net.minecraft.util.datafix.fixes.DataConverterEntityShulkerRotation;
import net.minecraft.util.datafix.fixes.DataConverterEntityTippedArrow;
import net.minecraft.util.datafix.fixes.DataConverterEntityUUID;
import net.minecraft.util.datafix.fixes.DataConverterEntityZombifiedPiglinRename;
import net.minecraft.util.datafix.fixes.DataConverterEquipment;
import net.minecraft.util.datafix.fixes.DataConverterFlatten;
import net.minecraft.util.datafix.fixes.DataConverterFlattenSpawnEgg;
import net.minecraft.util.datafix.fixes.DataConverterFlattenState;
import net.minecraft.util.datafix.fixes.DataConverterFurnaceRecipesUsed;
import net.minecraft.util.datafix.fixes.DataConverterGossip;
import net.minecraft.util.datafix.fixes.DataConverterGuardian;
import net.minecraft.util.datafix.fixes.DataConverterHanging;
import net.minecraft.util.datafix.fixes.DataConverterHealth;
import net.minecraft.util.datafix.fixes.DataConverterHeightmapRenaming;
import net.minecraft.util.datafix.fixes.DataConverterHorse;
import net.minecraft.util.datafix.fixes.DataConverterIglooMetadataRemoval;
import net.minecraft.util.datafix.fixes.DataConverterItemFrame;
import net.minecraft.util.datafix.fixes.DataConverterItemLoreComponentize;
import net.minecraft.util.datafix.fixes.DataConverterItemName;
import net.minecraft.util.datafix.fixes.DataConverterItemStackEnchantment;
import net.minecraft.util.datafix.fixes.DataConverterItemStackUUID;
import net.minecraft.util.datafix.fixes.DataConverterJigsawProperties;
import net.minecraft.util.datafix.fixes.DataConverterJigsawRotation;
import net.minecraft.util.datafix.fixes.DataConverterJukeBox;
import net.minecraft.util.datafix.fixes.DataConverterKeybind;
import net.minecraft.util.datafix.fixes.DataConverterKeybind2;
import net.minecraft.util.datafix.fixes.DataConverterLang;
import net.minecraft.util.datafix.fixes.DataConverterLeaves;
import net.minecraft.util.datafix.fixes.DataConverterLeavesBiome;
import net.minecraft.util.datafix.fixes.DataConverterLevelDataGeneratorOptions;
import net.minecraft.util.datafix.fixes.DataConverterMap;
import net.minecraft.util.datafix.fixes.DataConverterMapId;
import net.minecraft.util.datafix.fixes.DataConverterMaterialId;
import net.minecraft.util.datafix.fixes.DataConverterMemoryExpiry;
import net.minecraft.util.datafix.fixes.DataConverterMinecart;
import net.minecraft.util.datafix.fixes.DataConverterMiscUUID;
import net.minecraft.util.datafix.fixes.DataConverterMissingDimension;
import net.minecraft.util.datafix.fixes.DataConverterMobSpawner;
import net.minecraft.util.datafix.fixes.DataConverterNamedEntity;
import net.minecraft.util.datafix.fixes.DataConverterNewVillage;
import net.minecraft.util.datafix.fixes.DataConverterObjectiveDisplayName;
import net.minecraft.util.datafix.fixes.DataConverterObjectiveRenderType;
import net.minecraft.util.datafix.fixes.DataConverterOminousBannerBlockEntityRename;
import net.minecraft.util.datafix.fixes.DataConverterOminousBannerRename;
import net.minecraft.util.datafix.fixes.DataConverterOptionsAddTextBackground;
import net.minecraft.util.datafix.fixes.DataConverterPOI;
import net.minecraft.util.datafix.fixes.DataConverterPOIRebuild;
import net.minecraft.util.datafix.fixes.DataConverterPainting;
import net.minecraft.util.datafix.fixes.DataConverterPiston;
import net.minecraft.util.datafix.fixes.DataConverterPlayerUUID;
import net.minecraft.util.datafix.fixes.DataConverterPotionId;
import net.minecraft.util.datafix.fixes.DataConverterPotionWater;
import net.minecraft.util.datafix.fixes.DataConverterProtoChunk;
import net.minecraft.util.datafix.fixes.DataConverterRecipeBase;
import net.minecraft.util.datafix.fixes.DataConverterRecipeRename;
import net.minecraft.util.datafix.fixes.DataConverterRecipes;
import net.minecraft.util.datafix.fixes.DataConverterRedstoneConnections;
import net.minecraft.util.datafix.fixes.DataConverterRemoveGolemGossip;
import net.minecraft.util.datafix.fixes.DataConverterRiding;
import net.minecraft.util.datafix.fixes.DataConverterSaddle;
import net.minecraft.util.datafix.fixes.DataConverterSavedDataUUID;
import net.minecraft.util.datafix.fixes.DataConverterSettingRename;
import net.minecraft.util.datafix.fixes.DataConverterShoulderEntity;
import net.minecraft.util.datafix.fixes.DataConverterShulker;
import net.minecraft.util.datafix.fixes.DataConverterShulkerBoxBlock;
import net.minecraft.util.datafix.fixes.DataConverterShulkerBoxItem;
import net.minecraft.util.datafix.fixes.DataConverterSignText;
import net.minecraft.util.datafix.fixes.DataConverterSkeleton;
import net.minecraft.util.datafix.fixes.DataConverterSpawnEgg;
import net.minecraft.util.datafix.fixes.DataConverterStatistic;
import net.minecraft.util.datafix.fixes.DataConverterStriderGravity;
import net.minecraft.util.datafix.fixes.DataConverterStructureReference;
import net.minecraft.util.datafix.fixes.DataConverterTeamDisplayName;
import net.minecraft.util.datafix.fixes.DataConverterTileEntity;
import net.minecraft.util.datafix.fixes.DataConverterTrappedChest;
import net.minecraft.util.datafix.fixes.DataConverterTypes;
import net.minecraft.util.datafix.fixes.DataConverterUUID;
import net.minecraft.util.datafix.fixes.DataConverterVBO;
import net.minecraft.util.datafix.fixes.DataConverterVillagerFollowRange;
import net.minecraft.util.datafix.fixes.DataConverterVillagerLevelXp;
import net.minecraft.util.datafix.fixes.DataConverterVillagerProfession;
import net.minecraft.util.datafix.fixes.DataConverterVillagerTrade;
import net.minecraft.util.datafix.fixes.DataConverterWallProperty;
import net.minecraft.util.datafix.fixes.DataConverterWolf;
import net.minecraft.util.datafix.fixes.DataConverterWorldGenSettings;
import net.minecraft.util.datafix.fixes.DataConverterWorldGenSettingsBuilding;
import net.minecraft.util.datafix.fixes.DataConverterZombie;
import net.minecraft.util.datafix.fixes.DataConverterZombieType;
import net.minecraft.util.datafix.fixes.DataConverterZombieVillagerLevelXp;
import net.minecraft.util.datafix.fixes.EffectDurationFix;
import net.minecraft.util.datafix.fixes.EntityGoatMissingStateFix;
import net.minecraft.util.datafix.fixes.EntityPaintingFieldsRenameFix;
import net.minecraft.util.datafix.fixes.EntityVariantFix;
import net.minecraft.util.datafix.fixes.FilteredBooksFix;
import net.minecraft.util.datafix.fixes.FilteredSignsFix;
import net.minecraft.util.datafix.fixes.GoatHornIdFix;
import net.minecraft.util.datafix.fixes.ItemRemoveBlockEntityTagFix;
import net.minecraft.util.datafix.fixes.OptionsAccessibilityOnboardFix;
import net.minecraft.util.datafix.fixes.OptionsAmbientOcclusionFix;
import net.minecraft.util.datafix.fixes.OptionsProgrammerArtFix;
import net.minecraft.util.datafix.fixes.OverreachingTickFix;
import net.minecraft.util.datafix.fixes.PoiTypeRemoveFix;
import net.minecraft.util.datafix.fixes.PoiTypeRenameFix;
import net.minecraft.util.datafix.fixes.SavedDataFeaturePoolElementFix;
import net.minecraft.util.datafix.fixes.SimpleRenameFix;
import net.minecraft.util.datafix.fixes.SpawnerDataFix;
import net.minecraft.util.datafix.fixes.StatsRenameFix;
import net.minecraft.util.datafix.fixes.StructureSettingsFlattenFix;
import net.minecraft.util.datafix.fixes.StructuresBecomeConfiguredFix;
import net.minecraft.util.datafix.fixes.VariantRenameFix;
import net.minecraft.util.datafix.fixes.WeaponSmithChestLootTableFix;
import net.minecraft.util.datafix.fixes.WorldGenSettingsDisallowOldCustomWorldsFix;
import net.minecraft.util.datafix.fixes.WorldGenSettingsHeightAndBiomeFix;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV100;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV102;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1022;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV106;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV107;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1125;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV135;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV143;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451_1;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451_2;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451_3;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451_4;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451_5;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1451_6;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1460;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1466;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1470;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1481;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1483;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1486;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1510;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1800;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1801;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1904;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1906;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1909;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1920;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1928;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1929;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV1931;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2100;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2501;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2502;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2505;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2509;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2519;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2522;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2551;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV2568;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV501;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV700;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV701;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV702;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV703;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV704;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV705;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV808;
import net.minecraft.util.datafix.schemas.DataConverterSchemaV99;
import net.minecraft.util.datafix.schemas.V2571;
import net.minecraft.util.datafix.schemas.V2684;
import net.minecraft.util.datafix.schemas.V2686;
import net.minecraft.util.datafix.schemas.V2688;
import net.minecraft.util.datafix.schemas.V2704;
import net.minecraft.util.datafix.schemas.V2707;
import net.minecraft.util.datafix.schemas.V2831;
import net.minecraft.util.datafix.schemas.V2832;
import net.minecraft.util.datafix.schemas.V2842;
import net.minecraft.util.datafix.schemas.V3076;
import net.minecraft.util.datafix.schemas.V3078;
import net.minecraft.util.datafix.schemas.V3081;
import net.minecraft.util.datafix.schemas.V3082;
import net.minecraft.util.datafix.schemas.V3083;
import net.minecraft.util.datafix.schemas.V3202;
import net.minecraft.util.datafix.schemas.V3203;
import net.minecraft.util.datafix.schemas.V3204;
import net.minecraft.util.datafix.schemas.V3325;
import net.minecraft.util.datafix.schemas.V3326;
import net.minecraft.util.datafix.schemas.V3327;
import net.minecraft.util.datafix.schemas.V3328;

public class DataConverterRegistry {

    private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
    private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = DataConverterSchemaNamed::new;
    private static final DataFixer dataFixer = createFixerUpper(SharedConstants.DATA_FIX_TYPES_TO_OPTIMIZE);
    public static final int BLENDING_VERSION = 3088;

    private DataConverterRegistry() {}

    public static DataFixer getDataFixer() {
        return DataConverterRegistry.dataFixer;
    }

    private static synchronized DataFixer createFixerUpper(Set<TypeReference> set) {
        DataFixerBuilder datafixerbuilder = new DataFixerBuilder(SharedConstants.getCurrentVersion().getDataVersion().getVersion());

        addFixers(datafixerbuilder);
        if (set.isEmpty()) {
            return datafixerbuilder.buildUnoptimized();
        } else {
            ExecutorService executorservice = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Datafixer Bootstrap").setDaemon(true).setPriority(1).build());

            return datafixerbuilder.buildOptimized(set, executorservice);
        }
    }

    private static void addFixers(DataFixerBuilder datafixerbuilder) {
        datafixerbuilder.addSchema(99, DataConverterSchemaV99::new);
        Schema schema = datafixerbuilder.addSchema(100, DataConverterSchemaV100::new);

        datafixerbuilder.addFixer(new DataConverterEquipment(schema, true));
        Schema schema1 = datafixerbuilder.addSchema(101, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterSignText(schema1, false));
        Schema schema2 = datafixerbuilder.addSchema(102, DataConverterSchemaV102::new);

        datafixerbuilder.addFixer(new DataConverterMaterialId(schema2, true));
        datafixerbuilder.addFixer(new DataConverterPotionId(schema2, false));
        Schema schema3 = datafixerbuilder.addSchema(105, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterSpawnEgg(schema3, true));
        Schema schema4 = datafixerbuilder.addSchema(106, DataConverterSchemaV106::new);

        datafixerbuilder.addFixer(new DataConverterMobSpawner(schema4, true));
        Schema schema5 = datafixerbuilder.addSchema(107, DataConverterSchemaV107::new);

        datafixerbuilder.addFixer(new DataConverterMinecart(schema5, true));
        Schema schema6 = datafixerbuilder.addSchema(108, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterUUID(schema6, true));
        Schema schema7 = datafixerbuilder.addSchema(109, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterHealth(schema7, true));
        Schema schema8 = datafixerbuilder.addSchema(110, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterSaddle(schema8, true));
        Schema schema9 = datafixerbuilder.addSchema(111, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterHanging(schema9, true));
        Schema schema10 = datafixerbuilder.addSchema(113, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterDropChances(schema10, true));
        Schema schema11 = datafixerbuilder.addSchema(135, DataConverterSchemaV135::new);

        datafixerbuilder.addFixer(new DataConverterRiding(schema11, true));
        Schema schema12 = datafixerbuilder.addSchema(143, DataConverterSchemaV143::new);

        datafixerbuilder.addFixer(new DataConverterEntityTippedArrow(schema12, true));
        Schema schema13 = datafixerbuilder.addSchema(147, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterArmorStand(schema13, true));
        Schema schema14 = datafixerbuilder.addSchema(165, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterBook(schema14, true));
        Schema schema15 = datafixerbuilder.addSchema(501, DataConverterSchemaV501::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema15, "Add 1.10 entities fix", DataConverterTypes.ENTITY));
        Schema schema16 = datafixerbuilder.addSchema(502, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema16, "cooked_fished item renamer", (s) -> {
            return Objects.equals(DataConverterSchemaNamed.ensureNamespaced(s), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : s;
        }));
        datafixerbuilder.addFixer(new DataConverterZombie(schema16, false));
        Schema schema17 = datafixerbuilder.addSchema(505, DataConverterRegistry.SAME);

        datafixerbuilder.addFixer(new DataConverterVBO(schema17, false));
        Schema schema18 = datafixerbuilder.addSchema(700, DataConverterSchemaV700::new);

        datafixerbuilder.addFixer(new DataConverterGuardian(schema18, true));
        Schema schema19 = datafixerbuilder.addSchema(701, DataConverterSchemaV701::new);

        datafixerbuilder.addFixer(new DataConverterSkeleton(schema19, true));
        Schema schema20 = datafixerbuilder.addSchema(702, DataConverterSchemaV702::new);

        datafixerbuilder.addFixer(new DataConverterZombieType(schema20, true));
        Schema schema21 = datafixerbuilder.addSchema(703, DataConverterSchemaV703::new);

        datafixerbuilder.addFixer(new DataConverterHorse(schema21, true));
        Schema schema22 = datafixerbuilder.addSchema(704, DataConverterSchemaV704::new);

        datafixerbuilder.addFixer(new DataConverterTileEntity(schema22, true));
        Schema schema23 = datafixerbuilder.addSchema(705, DataConverterSchemaV705::new);

        datafixerbuilder.addFixer(new DataConverterEntity(schema23, true));
        Schema schema24 = datafixerbuilder.addSchema(804, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterBanner(schema24, true));
        Schema schema25 = datafixerbuilder.addSchema(806, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterPotionWater(schema25, false));
        Schema schema26 = datafixerbuilder.addSchema(808, DataConverterSchemaV808::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema26, "added shulker box", DataConverterTypes.BLOCK_ENTITY));
        Schema schema27 = datafixerbuilder.addSchema(808, 1, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterShulker(schema27, false));
        Schema schema28 = datafixerbuilder.addSchema(813, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterShulkerBoxItem(schema28, false));
        datafixerbuilder.addFixer(new DataConverterShulkerBoxBlock(schema28, false));
        Schema schema29 = datafixerbuilder.addSchema(816, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterLang(schema29, false));
        Schema schema30 = datafixerbuilder.addSchema(820, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema30, "totem item renamer", createRenamer("minecraft:totem", "minecraft:totem_of_undying")));
        Schema schema31 = datafixerbuilder.addSchema(1022, DataConverterSchemaV1022::new);

        datafixerbuilder.addFixer(new DataConverterShoulderEntity(schema31, "added shoulder entities to players", DataConverterTypes.PLAYER));
        Schema schema32 = datafixerbuilder.addSchema(1125, DataConverterSchemaV1125::new);

        datafixerbuilder.addFixer(new DataConverterBedBlock(schema32, true));
        datafixerbuilder.addFixer(new DataConverterBedItem(schema32, false));
        Schema schema33 = datafixerbuilder.addSchema(1344, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterKeybind(schema33, false));
        Schema schema34 = datafixerbuilder.addSchema(1446, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterKeybind2(schema34, false));
        Schema schema35 = datafixerbuilder.addSchema(1450, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterFlattenState(schema35, false));
        Schema schema36 = datafixerbuilder.addSchema(1451, DataConverterSchemaV1451::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema36, "AddTrappedChestFix", DataConverterTypes.BLOCK_ENTITY));
        Schema schema37 = datafixerbuilder.addSchema(1451, 1, DataConverterSchemaV1451_1::new);

        datafixerbuilder.addFixer(new ChunkConverterPalette(schema37, true));
        Schema schema38 = datafixerbuilder.addSchema(1451, 2, DataConverterSchemaV1451_2::new);

        datafixerbuilder.addFixer(new DataConverterPiston(schema38, true));
        Schema schema39 = datafixerbuilder.addSchema(1451, 3, DataConverterSchemaV1451_3::new);

        datafixerbuilder.addFixer(new DataConverterEntityBlockState(schema39, true));
        datafixerbuilder.addFixer(new DataConverterMap(schema39, false));
        Schema schema40 = datafixerbuilder.addSchema(1451, 4, DataConverterSchemaV1451_4::new);

        datafixerbuilder.addFixer(new DataConverterBlockName(schema40, true));
        datafixerbuilder.addFixer(new DataConverterFlatten(schema40, false));
        Schema schema41 = datafixerbuilder.addSchema(1451, 5, DataConverterSchemaV1451_5::new);

        datafixerbuilder.addFixer(new ItemRemoveBlockEntityTagFix(schema41, false, Set.of("minecraft:note_block", "minecraft:flower_pot", "minecraft:dandelion", "minecraft:poppy", "minecraft:blue_orchid", "minecraft:allium", "minecraft:azure_bluet", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip", "minecraft:pink_tulip", "minecraft:oxeye_daisy", "minecraft:cactus", "minecraft:brown_mushroom", "minecraft:red_mushroom", "minecraft:oak_sapling", "minecraft:spruce_sapling", "minecraft:birch_sapling", "minecraft:jungle_sapling", "minecraft:acacia_sapling", "minecraft:dark_oak_sapling", "minecraft:dead_bush", "minecraft:fern")));
        datafixerbuilder.addFixer(new DataConverterAddChoices(schema41, "RemoveNoteBlockFlowerPotFix", DataConverterTypes.BLOCK_ENTITY));
        datafixerbuilder.addFixer(new DataConverterFlattenSpawnEgg(schema41, false, "minecraft:spawn_egg"));
        datafixerbuilder.addFixer(new DataConverterWolf(schema41, false));
        datafixerbuilder.addFixer(new DataConverterBannerColour(schema41, false));
        datafixerbuilder.addFixer(new DataConverterWorldGenSettings(schema41, false));
        Schema schema42 = datafixerbuilder.addSchema(1451, 6, DataConverterSchemaV1451_6::new);

        datafixerbuilder.addFixer(new DataConverterStatistic(schema42, true));
        datafixerbuilder.addFixer(new DataConverterShoulderEntity(schema42, "Rewrite objectives", DataConverterTypes.OBJECTIVE));
        datafixerbuilder.addFixer(new DataConverterJukeBox(schema42, false));
        Schema schema43 = datafixerbuilder.addSchema(1451, 7, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterVillagerTrade(schema43, false));
        Schema schema44 = datafixerbuilder.addSchema(1456, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterItemFrame(schema44, false));
        Schema schema45 = datafixerbuilder.addSchema(1458, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterCustomNameEntity(schema45, false));
        datafixerbuilder.addFixer(new DataConverterCustomNameItem(schema45, false));
        datafixerbuilder.addFixer(new DataConverterCustomNameTile(schema45, false));
        Schema schema46 = datafixerbuilder.addSchema(1460, DataConverterSchemaV1460::new);

        datafixerbuilder.addFixer(new DataConverterPainting(schema46, false));
        Schema schema47 = datafixerbuilder.addSchema(1466, DataConverterSchemaV1466::new);

        datafixerbuilder.addFixer(new DataConverterProtoChunk(schema47, true));
        Schema schema48 = datafixerbuilder.addSchema(1470, DataConverterSchemaV1470::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema48, "Add 1.13 entities fix", DataConverterTypes.ENTITY));
        Schema schema49 = datafixerbuilder.addSchema(1474, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterColorlessShulkerEntity(schema49, false));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema49, "Colorless shulker block fixer", (s) -> {
            return Objects.equals(DataConverterSchemaNamed.ensureNamespaced(s), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : s;
        }));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema49, "Colorless shulker item fixer", (s) -> {
            return Objects.equals(DataConverterSchemaNamed.ensureNamespaced(s), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : s;
        }));
        Schema schema50 = datafixerbuilder.addSchema(1475, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema50, "Flowing fixer", createRenamer(ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava"))));
        Schema schema51 = datafixerbuilder.addSchema(1480, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema51, "Rename coral blocks", createRenamer(DataConverterCoral.RENAMED_IDS)));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema51, "Rename coral items", createRenamer(DataConverterCoral.RENAMED_IDS)));
        Schema schema52 = datafixerbuilder.addSchema(1481, DataConverterSchemaV1481::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema52, "Add conduit", DataConverterTypes.BLOCK_ENTITY));
        Schema schema53 = datafixerbuilder.addSchema(1483, DataConverterSchemaV1483::new);

        datafixerbuilder.addFixer(new DataConverterEntityPufferfish(schema53, true));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema53, "Rename pufferfish egg item", createRenamer(DataConverterEntityPufferfish.RENAMED_IDS)));
        Schema schema54 = datafixerbuilder.addSchema(1484, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema54, "Rename seagrass items", createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema54, "Rename seagrass blocks", createRenamer(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))));
        datafixerbuilder.addFixer(new DataConverterHeightmapRenaming(schema54, false));
        Schema schema55 = datafixerbuilder.addSchema(1486, DataConverterSchemaV1486::new);

        datafixerbuilder.addFixer(new DataConverterEntityCodSalmon(schema55, true));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema55, "Rename cod/salmon egg items", createRenamer(DataConverterEntityCodSalmon.RENAMED_EGG_IDS)));
        Schema schema56 = datafixerbuilder.addSchema(1487, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema56, "Rename prismarine_brick(s)_* blocks", createRenamer(ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"))));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema56, "Rename prismarine_brick(s)_* items", createRenamer(ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"))));
        Schema schema57 = datafixerbuilder.addSchema(1488, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema57, "Rename kelp/kelptop", createRenamer(ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant"))));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema57, "Rename kelptop", createRenamer("minecraft:kelp_top", "minecraft:kelp")));
        datafixerbuilder.addFixer(new DataConverterNamedEntity(schema57, false, "Command block block entity custom name fix", DataConverterTypes.BLOCK_ENTITY, "minecraft:command_block") {
            @Override
            protected Typed<?> fix(Typed<?> typed) {
                return typed.update(DSL.remainderFinder(), DataConverterCustomNameEntity::fixTagCustomName);
            }
        });
        datafixerbuilder.addFixer(new DataConverterNamedEntity(schema57, false, "Command block minecart custom name fix", DataConverterTypes.ENTITY, "minecraft:commandblock_minecart") {
            @Override
            protected Typed<?> fix(Typed<?> typed) {
                return typed.update(DSL.remainderFinder(), DataConverterCustomNameEntity::fixTagCustomName);
            }
        });
        datafixerbuilder.addFixer(new DataConverterIglooMetadataRemoval(schema57, false));
        Schema schema58 = datafixerbuilder.addSchema(1490, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema58, "Rename melon_block", createRenamer("minecraft:melon_block", "minecraft:melon")));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema58, "Rename melon_block/melon/speckled_melon", createRenamer(ImmutableMap.of("minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"))));
        Schema schema59 = datafixerbuilder.addSchema(1492, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterChunkStructuresTemplateRename(schema59, false));
        Schema schema60 = datafixerbuilder.addSchema(1494, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterItemStackEnchantment(schema60, false));
        Schema schema61 = datafixerbuilder.addSchema(1496, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterLeaves(schema61, false));
        Schema schema62 = datafixerbuilder.addSchema(1500, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterBlockEntityKeepPacked(schema62, false));
        Schema schema63 = datafixerbuilder.addSchema(1501, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterAdvancement(schema63, false));
        Schema schema64 = datafixerbuilder.addSchema(1502, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterRecipes(schema64, false));
        Schema schema65 = datafixerbuilder.addSchema(1506, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterLevelDataGeneratorOptions(schema65, false));
        Schema schema66 = datafixerbuilder.addSchema(1510, DataConverterSchemaV1510::new);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema66, "Block renamening fix", createRenamer(DataConverterEntityRename.RENAMED_BLOCKS)));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema66, "Item renamening fix", createRenamer(DataConverterEntityRename.RENAMED_ITEMS)));
        datafixerbuilder.addFixer(new DataConverterRecipeRename(schema66, false));
        datafixerbuilder.addFixer(new DataConverterEntityRename(schema66, true));
        datafixerbuilder.addFixer(new StatsRenameFix(schema66, "SwimStatsRenameFix", ImmutableMap.of("minecraft:swim_one_cm", "minecraft:walk_on_water_one_cm", "minecraft:dive_one_cm", "minecraft:walk_under_water_one_cm")));
        Schema schema67 = datafixerbuilder.addSchema(1514, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterObjectiveDisplayName(schema67, false));
        datafixerbuilder.addFixer(new DataConverterTeamDisplayName(schema67, false));
        datafixerbuilder.addFixer(new DataConverterObjectiveRenderType(schema67, false));
        Schema schema68 = datafixerbuilder.addSchema(1515, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema68, "Rename coral fan blocks", createRenamer(DataConverterCoralFan.RENAMED_IDS)));
        Schema schema69 = datafixerbuilder.addSchema(1624, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterTrappedChest(schema69, false));
        Schema schema70 = datafixerbuilder.addSchema(1800, DataConverterSchemaV1800::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema70, "Added 1.14 mobs fix", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema70, "Rename dye items", createRenamer(DataConverterDye.RENAMED_IDS)));
        Schema schema71 = datafixerbuilder.addSchema(1801, DataConverterSchemaV1801::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema71, "Added Illager Beast", DataConverterTypes.ENTITY));
        Schema schema72 = datafixerbuilder.addSchema(1802, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema72, "Rename sign blocks & stone slabs", createRenamer(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"))));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema72, "Rename sign item & stone slabs", createRenamer(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign"))));
        Schema schema73 = datafixerbuilder.addSchema(1803, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterItemLoreComponentize(schema73, false));
        Schema schema74 = datafixerbuilder.addSchema(1904, DataConverterSchemaV1904::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema74, "Added Cats", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new DataConverterEntityCatSplit(schema74, false));
        Schema schema75 = datafixerbuilder.addSchema(1905, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterChunkStatus(schema75, false));
        Schema schema76 = datafixerbuilder.addSchema(1906, DataConverterSchemaV1906::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema76, "Add POI Blocks", DataConverterTypes.BLOCK_ENTITY));
        Schema schema77 = datafixerbuilder.addSchema(1909, DataConverterSchemaV1909::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema77, "Add jigsaw", DataConverterTypes.BLOCK_ENTITY));
        Schema schema78 = datafixerbuilder.addSchema(1911, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterChunkStatus2(schema78, false));
        Schema schema79 = datafixerbuilder.addSchema(1914, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new WeaponSmithChestLootTableFix(schema79, false));
        Schema schema80 = datafixerbuilder.addSchema(1917, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterCatType(schema80, false));
        Schema schema81 = datafixerbuilder.addSchema(1918, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterVillagerProfession(schema81, "minecraft:villager"));
        datafixerbuilder.addFixer(new DataConverterVillagerProfession(schema81, "minecraft:zombie_villager"));
        Schema schema82 = datafixerbuilder.addSchema(1920, DataConverterSchemaV1920::new);

        datafixerbuilder.addFixer(new DataConverterNewVillage(schema82, false));
        datafixerbuilder.addFixer(new DataConverterAddChoices(schema82, "Add campfire", DataConverterTypes.BLOCK_ENTITY));
        Schema schema83 = datafixerbuilder.addSchema(1925, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterMapId(schema83, false));
        Schema schema84 = datafixerbuilder.addSchema(1928, DataConverterSchemaV1928::new);

        datafixerbuilder.addFixer(new DataConverterEntityRavagerRename(schema84, true));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema84, "Rename ravager egg item", createRenamer(DataConverterEntityRavagerRename.RENAMED_IDS)));
        Schema schema85 = datafixerbuilder.addSchema(1929, DataConverterSchemaV1929::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema85, "Add Wandering Trader and Trader Llama", DataConverterTypes.ENTITY));
        Schema schema86 = datafixerbuilder.addSchema(1931, DataConverterSchemaV1931::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema86, "Added Fox", DataConverterTypes.ENTITY));
        Schema schema87 = datafixerbuilder.addSchema(1936, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterOptionsAddTextBackground(schema87, false));
        Schema schema88 = datafixerbuilder.addSchema(1946, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterPOI(schema88, false));
        Schema schema89 = datafixerbuilder.addSchema(1948, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterOminousBannerRename(schema89));
        Schema schema90 = datafixerbuilder.addSchema(1953, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterOminousBannerBlockEntityRename(schema90, false));
        Schema schema91 = datafixerbuilder.addSchema(1955, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterVillagerLevelXp(schema91, false));
        datafixerbuilder.addFixer(new DataConverterZombieVillagerLevelXp(schema91, false));
        Schema schema92 = datafixerbuilder.addSchema(1961, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterChunkLightRemove(schema92, false));
        Schema schema93 = datafixerbuilder.addSchema(1963, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterRemoveGolemGossip(schema93, false));
        Schema schema94 = datafixerbuilder.addSchema(2100, DataConverterSchemaV2100::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema94, "Added Bee and Bee Stinger", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new DataConverterAddChoices(schema94, "Add beehive", DataConverterTypes.BLOCK_ENTITY));
        datafixerbuilder.addFixer(new DataConverterRecipeBase(schema94, false, "Rename sugar recipe", createRenamer("minecraft:sugar", "sugar_from_sugar_cane")));
        datafixerbuilder.addFixer(new DataConverterAdvancementBase(schema94, false, "Rename sugar recipe advancement", createRenamer("minecraft:recipes/misc/sugar", "minecraft:recipes/misc/sugar_from_sugar_cane")));
        Schema schema95 = datafixerbuilder.addSchema(2202, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterLeavesBiome(schema95, false));
        Schema schema96 = datafixerbuilder.addSchema(2209, DataConverterRegistry.SAME_NAMESPACED);
        UnaryOperator<String> unaryoperator = createRenamer("minecraft:bee_hive", "minecraft:beehive");

        datafixerbuilder.addFixer(DataConverterItemName.create(schema96, "Rename bee_hive item to beehive", unaryoperator));
        datafixerbuilder.addFixer(new PoiTypeRenameFix(schema96, "Rename bee_hive poi to beehive", unaryoperator));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema96, "Rename bee_hive block to beehive", unaryoperator));
        Schema schema97 = datafixerbuilder.addSchema(2211, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterStructureReference(schema97, false));
        Schema schema98 = datafixerbuilder.addSchema(2218, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterPOIRebuild(schema98, false));
        Schema schema99 = datafixerbuilder.addSchema(2501, DataConverterSchemaV2501::new);

        datafixerbuilder.addFixer(new DataConverterFurnaceRecipesUsed(schema99, true));
        Schema schema100 = datafixerbuilder.addSchema(2502, DataConverterSchemaV2502::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema100, "Added Hoglin", DataConverterTypes.ENTITY));
        Schema schema101 = datafixerbuilder.addSchema(2503, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterWallProperty(schema101, false));
        datafixerbuilder.addFixer(new DataConverterAdvancementBase(schema101, false, "Composter category change", createRenamer("minecraft:recipes/misc/composter", "minecraft:recipes/decorations/composter")));
        Schema schema102 = datafixerbuilder.addSchema(2505, DataConverterSchemaV2505::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema102, "Added Piglin", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new DataConverterMemoryExpiry(schema102, "minecraft:villager"));
        Schema schema103 = datafixerbuilder.addSchema(2508, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema103, "Renamed fungi items to fungus", createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema103, "Renamed fungi blocks to fungus", createRenamer(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))));
        Schema schema104 = datafixerbuilder.addSchema(2509, DataConverterSchemaV2509::new);

        datafixerbuilder.addFixer(new DataConverterEntityZombifiedPiglinRename(schema104));
        datafixerbuilder.addFixer(DataConverterItemName.create(schema104, "Rename zombie pigman egg item", createRenamer(DataConverterEntityZombifiedPiglinRename.RENAMED_IDS)));
        Schema schema105 = datafixerbuilder.addSchema(2511, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterEntityProjectileOwner(schema105));
        Schema schema106 = datafixerbuilder.addSchema(2514, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterEntityUUID(schema106));
        datafixerbuilder.addFixer(new DataConverterBlockEntityUUID(schema106));
        datafixerbuilder.addFixer(new DataConverterPlayerUUID(schema106));
        datafixerbuilder.addFixer(new DataConverterMiscUUID(schema106));
        datafixerbuilder.addFixer(new DataConverterSavedDataUUID(schema106));
        datafixerbuilder.addFixer(new DataConverterItemStackUUID(schema106));
        Schema schema107 = datafixerbuilder.addSchema(2516, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterGossip(schema107, "minecraft:villager"));
        datafixerbuilder.addFixer(new DataConverterGossip(schema107, "minecraft:zombie_villager"));
        Schema schema108 = datafixerbuilder.addSchema(2518, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterJigsawProperties(schema108, false));
        datafixerbuilder.addFixer(new DataConverterJigsawRotation(schema108, false));
        Schema schema109 = datafixerbuilder.addSchema(2519, DataConverterSchemaV2519::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema109, "Added Strider", DataConverterTypes.ENTITY));
        Schema schema110 = datafixerbuilder.addSchema(2522, DataConverterSchemaV2522::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema110, "Added Zoglin", DataConverterTypes.ENTITY));
        Schema schema111 = datafixerbuilder.addSchema(2523, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterAttributes(schema111));
        Schema schema112 = datafixerbuilder.addSchema(2527, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterBitStorageAlign(schema112));
        Schema schema113 = datafixerbuilder.addSchema(2528, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema113, "Rename soul fire torch and soul fire lantern", createRenamer(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema113, "Rename soul fire torch and soul fire lantern", createRenamer(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_wall_torch", "minecraft:soul_wall_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))));
        Schema schema114 = datafixerbuilder.addSchema(2529, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterStriderGravity(schema114, false));
        Schema schema115 = datafixerbuilder.addSchema(2531, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterRedstoneConnections(schema115));
        Schema schema116 = datafixerbuilder.addSchema(2533, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterVillagerFollowRange(schema116));
        Schema schema117 = datafixerbuilder.addSchema(2535, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterEntityShulkerRotation(schema117));
        Schema schema118 = datafixerbuilder.addSchema(2550, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterWorldGenSettingsBuilding(schema118));
        Schema schema119 = datafixerbuilder.addSchema(2551, DataConverterSchemaV2551::new);

        datafixerbuilder.addFixer(new DataConverterShoulderEntity(schema119, "add types to WorldGenData", DataConverterTypes.WORLD_GEN_SETTINGS));
        Schema schema120 = datafixerbuilder.addSchema(2552, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterBiomeBase(schema120, false, "Nether biome rename", ImmutableMap.of("minecraft:nether", "minecraft:nether_wastes")));
        Schema schema121 = datafixerbuilder.addSchema(2553, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterBiome(schema121, false));
        Schema schema122 = datafixerbuilder.addSchema(2558, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterMissingDimension(schema122, false));
        datafixerbuilder.addFixer(new DataConverterSettingRename(schema122, false, "Rename swapHands setting", "key_key.swapHands", "key_key.swapOffhand"));
        Schema schema123 = datafixerbuilder.addSchema(2568, DataConverterSchemaV2568::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema123, "Added Piglin Brute", DataConverterTypes.ENTITY));
        Schema schema124 = datafixerbuilder.addSchema(2571, V2571::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema124, "Added Goat", DataConverterTypes.ENTITY));
        Schema schema125 = datafixerbuilder.addSchema(2679, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new CauldronRenameFix(schema125, false));
        Schema schema126 = datafixerbuilder.addSchema(2680, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema126, "Renamed grass path item to dirt path", createRenamer("minecraft:grass_path", "minecraft:dirt_path")));
        datafixerbuilder.addFixer(BlockRenameFixWithJigsaw.create(schema126, "Renamed grass path block to dirt path", createRenamer("minecraft:grass_path", "minecraft:dirt_path")));
        Schema schema127 = datafixerbuilder.addSchema(2684, V2684::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema127, "Added Sculk Sensor", DataConverterTypes.BLOCK_ENTITY));
        Schema schema128 = datafixerbuilder.addSchema(2686, V2686::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema128, "Added Axolotl", DataConverterTypes.ENTITY));
        Schema schema129 = datafixerbuilder.addSchema(2688, V2688::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema129, "Added Glow Squid", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new DataConverterAddChoices(schema129, "Added Glow Item Frame", DataConverterTypes.ENTITY));
        Schema schema130 = datafixerbuilder.addSchema(2690, DataConverterRegistry.SAME_NAMESPACED);
        ImmutableMap<String, String> immutablemap = ImmutableMap.builder().put("minecraft:weathered_copper_block", "minecraft:oxidized_copper_block").put("minecraft:semi_weathered_copper_block", "minecraft:weathered_copper_block").put("minecraft:lightly_weathered_copper_block", "minecraft:exposed_copper_block").put("minecraft:weathered_cut_copper", "minecraft:oxidized_cut_copper").put("minecraft:semi_weathered_cut_copper", "minecraft:weathered_cut_copper").put("minecraft:lightly_weathered_cut_copper", "minecraft:exposed_cut_copper").put("minecraft:weathered_cut_copper_stairs", "minecraft:oxidized_cut_copper_stairs").put("minecraft:semi_weathered_cut_copper_stairs", "minecraft:weathered_cut_copper_stairs").put("minecraft:lightly_weathered_cut_copper_stairs", "minecraft:exposed_cut_copper_stairs").put("minecraft:weathered_cut_copper_slab", "minecraft:oxidized_cut_copper_slab").put("minecraft:semi_weathered_cut_copper_slab", "minecraft:weathered_cut_copper_slab").put("minecraft:lightly_weathered_cut_copper_slab", "minecraft:exposed_cut_copper_slab").put("minecraft:waxed_semi_weathered_copper", "minecraft:waxed_weathered_copper").put("minecraft:waxed_lightly_weathered_copper", "minecraft:waxed_exposed_copper").put("minecraft:waxed_semi_weathered_cut_copper", "minecraft:waxed_weathered_cut_copper").put("minecraft:waxed_lightly_weathered_cut_copper", "minecraft:waxed_exposed_cut_copper").put("minecraft:waxed_semi_weathered_cut_copper_stairs", "minecraft:waxed_weathered_cut_copper_stairs").put("minecraft:waxed_lightly_weathered_cut_copper_stairs", "minecraft:waxed_exposed_cut_copper_stairs").put("minecraft:waxed_semi_weathered_cut_copper_slab", "minecraft:waxed_weathered_cut_copper_slab").put("minecraft:waxed_lightly_weathered_cut_copper_slab", "minecraft:waxed_exposed_cut_copper_slab").build();

        datafixerbuilder.addFixer(DataConverterItemName.create(schema130, "Renamed copper block items to new oxidized terms", createRenamer(immutablemap)));
        datafixerbuilder.addFixer(BlockRenameFixWithJigsaw.create(schema130, "Renamed copper blocks to new oxidized terms", createRenamer(immutablemap)));
        Schema schema131 = datafixerbuilder.addSchema(2691, DataConverterRegistry.SAME_NAMESPACED);
        ImmutableMap<String, String> immutablemap1 = ImmutableMap.builder().put("minecraft:waxed_copper", "minecraft:waxed_copper_block").put("minecraft:oxidized_copper_block", "minecraft:oxidized_copper").put("minecraft:weathered_copper_block", "minecraft:weathered_copper").put("minecraft:exposed_copper_block", "minecraft:exposed_copper").build();

        datafixerbuilder.addFixer(DataConverterItemName.create(schema131, "Rename copper item suffixes", createRenamer(immutablemap1)));
        datafixerbuilder.addFixer(BlockRenameFixWithJigsaw.create(schema131, "Rename copper blocks suffixes", createRenamer(immutablemap1)));
        Schema schema132 = datafixerbuilder.addSchema(2693, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new AddFlagIfNotPresentFix(schema132, DataConverterTypes.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
        Schema schema133 = datafixerbuilder.addSchema(2696, DataConverterRegistry.SAME_NAMESPACED);
        ImmutableMap<String, String> immutablemap2 = ImmutableMap.builder().put("minecraft:grimstone", "minecraft:deepslate").put("minecraft:grimstone_slab", "minecraft:cobbled_deepslate_slab").put("minecraft:grimstone_stairs", "minecraft:cobbled_deepslate_stairs").put("minecraft:grimstone_wall", "minecraft:cobbled_deepslate_wall").put("minecraft:polished_grimstone", "minecraft:polished_deepslate").put("minecraft:polished_grimstone_slab", "minecraft:polished_deepslate_slab").put("minecraft:polished_grimstone_stairs", "minecraft:polished_deepslate_stairs").put("minecraft:polished_grimstone_wall", "minecraft:polished_deepslate_wall").put("minecraft:grimstone_tiles", "minecraft:deepslate_tiles").put("minecraft:grimstone_tile_slab", "minecraft:deepslate_tile_slab").put("minecraft:grimstone_tile_stairs", "minecraft:deepslate_tile_stairs").put("minecraft:grimstone_tile_wall", "minecraft:deepslate_tile_wall").put("minecraft:grimstone_bricks", "minecraft:deepslate_bricks").put("minecraft:grimstone_brick_slab", "minecraft:deepslate_brick_slab").put("minecraft:grimstone_brick_stairs", "minecraft:deepslate_brick_stairs").put("minecraft:grimstone_brick_wall", "minecraft:deepslate_brick_wall").put("minecraft:chiseled_grimstone", "minecraft:chiseled_deepslate").build();

        datafixerbuilder.addFixer(DataConverterItemName.create(schema133, "Renamed grimstone block items to deepslate", createRenamer(immutablemap2)));
        datafixerbuilder.addFixer(BlockRenameFixWithJigsaw.create(schema133, "Renamed grimstone blocks to deepslate", createRenamer(immutablemap2)));
        Schema schema134 = datafixerbuilder.addSchema(2700, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(BlockRenameFixWithJigsaw.create(schema134, "Renamed cave vines blocks", createRenamer(ImmutableMap.of("minecraft:cave_vines_head", "minecraft:cave_vines", "minecraft:cave_vines_body", "minecraft:cave_vines_plant"))));
        Schema schema135 = datafixerbuilder.addSchema(2701, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new SavedDataFeaturePoolElementFix(schema135));
        Schema schema136 = datafixerbuilder.addSchema(2702, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new AbstractArrowPickupFix(schema136));
        Schema schema137 = datafixerbuilder.addSchema(2704, V2704::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema137, "Added Goat", DataConverterTypes.ENTITY));
        Schema schema138 = datafixerbuilder.addSchema(2707, V2707::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema138, "Added Marker", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new AddFlagIfNotPresentFix(schema138, DataConverterTypes.WORLD_GEN_SETTINGS, "has_increased_height_already", true));
        Schema schema139 = datafixerbuilder.addSchema(2710, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new StatsRenameFix(schema139, "Renamed play_one_minute stat to play_time", ImmutableMap.of("minecraft:play_one_minute", "minecraft:play_time")));
        Schema schema140 = datafixerbuilder.addSchema(2717, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(DataConverterItemName.create(schema140, "Rename azalea_leaves_flowers", createRenamer(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))));
        datafixerbuilder.addFixer(DataConverterBlockRename.create(schema140, "Rename azalea_leaves_flowers items", createRenamer(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))));
        Schema schema141 = datafixerbuilder.addSchema(2825, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new AddFlagIfNotPresentFix(schema141, DataConverterTypes.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
        Schema schema142 = datafixerbuilder.addSchema(2831, V2831::new);

        datafixerbuilder.addFixer(new SpawnerDataFix(schema142));
        Schema schema143 = datafixerbuilder.addSchema(2832, V2832::new);

        datafixerbuilder.addFixer(new WorldGenSettingsHeightAndBiomeFix(schema143));
        datafixerbuilder.addFixer(new ChunkHeightAndBiomeFix(schema143));
        Schema schema144 = datafixerbuilder.addSchema(2833, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(schema144));
        Schema schema145 = datafixerbuilder.addSchema(2838, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterBiomeBase(schema145, false, "Caves and Cliffs biome renames", CavesAndCliffsRenames.RENAMES));
        Schema schema146 = datafixerbuilder.addSchema(2841, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new ChunkProtoTickListFix(schema146));
        Schema schema147 = datafixerbuilder.addSchema(2842, V2842::new);

        datafixerbuilder.addFixer(new ChunkRenamesFix(schema147));
        Schema schema148 = datafixerbuilder.addSchema(2843, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new OverreachingTickFix(schema148));
        datafixerbuilder.addFixer(new DataConverterBiomeBase(schema148, false, "Remove Deep Warm Ocean", Map.of("minecraft:deep_warm_ocean", "minecraft:warm_ocean")));
        Schema schema149 = datafixerbuilder.addSchema(2846, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterAdvancementBase(schema149, false, "Rename some C&C part 2 advancements", createRenamer(ImmutableMap.of("minecraft:husbandry/play_jukebox_in_meadows", "minecraft:adventure/play_jukebox_in_meadows", "minecraft:adventure/caves_and_cliff", "minecraft:adventure/fall_from_world_height", "minecraft:adventure/ride_strider_in_overworld_lava", "minecraft:nether/ride_strider_in_overworld_lava"))));
        Schema schema150 = datafixerbuilder.addSchema(2852, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(schema150));
        Schema schema151 = datafixerbuilder.addSchema(2967, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new StructureSettingsFlattenFix(schema151));
        Schema schema152 = datafixerbuilder.addSchema(2970, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new StructuresBecomeConfiguredFix(schema152));
        Schema schema153 = datafixerbuilder.addSchema(3076, V3076::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema153, "Added Sculk Catalyst", DataConverterTypes.BLOCK_ENTITY));
        Schema schema154 = datafixerbuilder.addSchema(3077, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new ChunkDeleteIgnoredLightDataFix(schema154));
        Schema schema155 = datafixerbuilder.addSchema(3078, V3078::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema155, "Added Frog", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new DataConverterAddChoices(schema155, "Added Tadpole", DataConverterTypes.ENTITY));
        datafixerbuilder.addFixer(new DataConverterAddChoices(schema155, "Added Sculk Shrieker", DataConverterTypes.BLOCK_ENTITY));
        Schema schema156 = datafixerbuilder.addSchema(3081, V3081::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema156, "Added Warden", DataConverterTypes.ENTITY));
        Schema schema157 = datafixerbuilder.addSchema(3082, V3082::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema157, "Added Chest Boat", DataConverterTypes.ENTITY));
        Schema schema158 = datafixerbuilder.addSchema(3083, V3083::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema158, "Added Allay", DataConverterTypes.ENTITY));
        Schema schema159 = datafixerbuilder.addSchema(3084, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new SimpleRenameFix(schema159, DataConverterTypes.GAME_EVENT_NAME, ImmutableMap.builder().put("minecraft:block_press", "minecraft:block_activate").put("minecraft:block_switch", "minecraft:block_activate").put("minecraft:block_unpress", "minecraft:block_deactivate").put("minecraft:block_unswitch", "minecraft:block_deactivate").put("minecraft:drinking_finish", "minecraft:drink").put("minecraft:elytra_free_fall", "minecraft:elytra_glide").put("minecraft:entity_damaged", "minecraft:entity_damage").put("minecraft:entity_dying", "minecraft:entity_die").put("minecraft:entity_killed", "minecraft:entity_die").put("minecraft:mob_interact", "minecraft:entity_interact").put("minecraft:ravager_roar", "minecraft:entity_roar").put("minecraft:ring_bell", "minecraft:block_change").put("minecraft:shulker_close", "minecraft:container_close").put("minecraft:shulker_open", "minecraft:container_open").put("minecraft:wolf_shaking", "minecraft:entity_shake").build()));
        Schema schema160 = datafixerbuilder.addSchema(3086, DataConverterRegistry.SAME_NAMESPACED);
        TypeReference typereference = DataConverterTypes.ENTITY;
        Int2ObjectOpenHashMap int2objectopenhashmap = (Int2ObjectOpenHashMap) SystemUtils.make(new Int2ObjectOpenHashMap(), (int2objectopenhashmap1) -> {
            int2objectopenhashmap1.defaultReturnValue("minecraft:tabby");
            int2objectopenhashmap1.put(0, "minecraft:tabby");
            int2objectopenhashmap1.put(1, "minecraft:black");
            int2objectopenhashmap1.put(2, "minecraft:red");
            int2objectopenhashmap1.put(3, "minecraft:siamese");
            int2objectopenhashmap1.put(4, "minecraft:british");
            int2objectopenhashmap1.put(5, "minecraft:calico");
            int2objectopenhashmap1.put(6, "minecraft:persian");
            int2objectopenhashmap1.put(7, "minecraft:ragdoll");
            int2objectopenhashmap1.put(8, "minecraft:white");
            int2objectopenhashmap1.put(9, "minecraft:jellie");
            int2objectopenhashmap1.put(10, "minecraft:all_black");
        });

        Objects.requireNonNull(int2objectopenhashmap);
        datafixerbuilder.addFixer(new EntityVariantFix(schema160, "Change cat variant type", typereference, "minecraft:cat", "CatType", int2objectopenhashmap::get));
        ImmutableMap<String, String> immutablemap3 = ImmutableMap.builder().put("textures/entity/cat/tabby.png", "minecraft:tabby").put("textures/entity/cat/black.png", "minecraft:black").put("textures/entity/cat/red.png", "minecraft:red").put("textures/entity/cat/siamese.png", "minecraft:siamese").put("textures/entity/cat/british_shorthair.png", "minecraft:british").put("textures/entity/cat/calico.png", "minecraft:calico").put("textures/entity/cat/persian.png", "minecraft:persian").put("textures/entity/cat/ragdoll.png", "minecraft:ragdoll").put("textures/entity/cat/white.png", "minecraft:white").put("textures/entity/cat/jellie.png", "minecraft:jellie").put("textures/entity/cat/all_black.png", "minecraft:all_black").build();

        datafixerbuilder.addFixer(new CriteriaRenameFix(schema160, "Migrate cat variant advancement", "minecraft:husbandry/complete_catalogue", (s) -> {
            return (String) immutablemap3.getOrDefault(s, s);
        }));
        Schema schema161 = datafixerbuilder.addSchema(3087, DataConverterRegistry.SAME_NAMESPACED);

        typereference = DataConverterTypes.ENTITY;
        int2objectopenhashmap = (Int2ObjectOpenHashMap) SystemUtils.make(new Int2ObjectOpenHashMap(), (int2objectopenhashmap1) -> {
            int2objectopenhashmap1.put(0, "minecraft:temperate");
            int2objectopenhashmap1.put(1, "minecraft:warm");
            int2objectopenhashmap1.put(2, "minecraft:cold");
        });
        Objects.requireNonNull(int2objectopenhashmap);
        datafixerbuilder.addFixer(new EntityVariantFix(schema161, "Change frog variant type", typereference, "minecraft:frog", "Variant", int2objectopenhashmap::get));
        Schema schema162 = datafixerbuilder.addSchema(3088, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new BlendingDataFix(schema162));
        Schema schema163 = datafixerbuilder.addSchema(3090, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new EntityPaintingFieldsRenameFix(schema163));
        Schema schema164 = datafixerbuilder.addSchema(3093, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new EntityGoatMissingStateFix(schema164));
        Schema schema165 = datafixerbuilder.addSchema(3094, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new GoatHornIdFix(schema165));
        Schema schema166 = datafixerbuilder.addSchema(3097, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new FilteredBooksFix(schema166));
        datafixerbuilder.addFixer(new FilteredSignsFix(schema166));
        Map<String, String> map = Map.of("minecraft:british", "minecraft:british_shorthair");

        datafixerbuilder.addFixer(new VariantRenameFix(schema166, "Rename british shorthair", DataConverterTypes.ENTITY, "minecraft:cat", map));
        datafixerbuilder.addFixer(new CriteriaRenameFix(schema166, "Migrate cat variant advancement for british shorthair", "minecraft:husbandry/complete_catalogue", (s) -> {
            return (String) map.getOrDefault(s, s);
        }));
        Set set = Set.of("minecraft:unemployed", "minecraft:nitwit");

        Objects.requireNonNull(set);
        datafixerbuilder.addFixer(new PoiTypeRemoveFix(schema166, "Remove unpopulated villager PoI types", set::contains));
        Schema schema167 = datafixerbuilder.addSchema(3108, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new BlendingDataRemoveFromNetherEndFix(schema167));
        Schema schema168 = datafixerbuilder.addSchema(3201, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new OptionsProgrammerArtFix(schema168));
        Schema schema169 = datafixerbuilder.addSchema(3202, V3202::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema169, "Added Hanging Sign", DataConverterTypes.BLOCK_ENTITY));
        Schema schema170 = datafixerbuilder.addSchema(3203, V3203::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema170, "Added Camel", DataConverterTypes.ENTITY));
        Schema schema171 = datafixerbuilder.addSchema(3204, V3204::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema171, "Added Chiseled Bookshelf", DataConverterTypes.BLOCK_ENTITY));
        Schema schema172 = datafixerbuilder.addSchema(3209, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new DataConverterFlattenSpawnEgg(schema172, false, "minecraft:pig_spawn_egg"));
        Schema schema173 = datafixerbuilder.addSchema(3214, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new OptionsAmbientOcclusionFix(schema173));
        Schema schema174 = datafixerbuilder.addSchema(3319, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new OptionsAccessibilityOnboardFix(schema174));
        Schema schema175 = datafixerbuilder.addSchema(3322, DataConverterRegistry.SAME_NAMESPACED);

        datafixerbuilder.addFixer(new EffectDurationFix(schema175));
        Schema schema176 = datafixerbuilder.addSchema(3325, V3325::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema176, "Added displays", DataConverterTypes.ENTITY));
        Schema schema177 = datafixerbuilder.addSchema(3326, V3326::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema177, "Added Sniffer", DataConverterTypes.ENTITY));
        Schema schema178 = datafixerbuilder.addSchema(3327, V3327::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema178, "Archaeology", DataConverterTypes.BLOCK_ENTITY));
        Schema schema179 = datafixerbuilder.addSchema(3328, V3328::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema179, "Added interaction", DataConverterTypes.ENTITY));
    }

    private static UnaryOperator<String> createRenamer(Map<String, String> map) {
        return (s) -> {
            return (String) map.getOrDefault(s, s);
        };
    }

    private static UnaryOperator<String> createRenamer(String s, String s1) {
        return (s2) -> {
            return Objects.equals(s2, s) ? s1 : s2;
        };
    }
}
