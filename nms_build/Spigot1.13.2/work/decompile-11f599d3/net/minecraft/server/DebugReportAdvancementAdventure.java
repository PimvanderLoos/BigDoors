package net.minecraft.server;

import java.util.function.Consumer;

public class DebugReportAdvancementAdventure implements Consumer<Consumer<Advancement>> {

    private static final BiomeBase[] a = new BiomeBase[] { Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN};
    private static final EntityTypes<?>[] b = new EntityTypes[] { EntityTypes.CAVE_SPIDER, EntityTypes.SPIDER, EntityTypes.ZOMBIE_PIGMAN, EntityTypes.ENDERMAN, EntityTypes.POLAR_BEAR, EntityTypes.BLAZE, EntityTypes.CREEPER, EntityTypes.EVOKER, EntityTypes.GHAST, EntityTypes.GUARDIAN, EntityTypes.HUSK, EntityTypes.MAGMA_CUBE, EntityTypes.SHULKER, EntityTypes.SILVERFISH, EntityTypes.SKELETON, EntityTypes.SLIME, EntityTypes.STRAY, EntityTypes.VINDICATOR, EntityTypes.WITCH, EntityTypes.WITHER_SKELETON, EntityTypes.ZOMBIE, EntityTypes.ZOMBIE_VILLAGER, EntityTypes.PHANTOM, EntityTypes.DROWNED};

    public DebugReportAdvancementAdventure() {}

    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.SerializedAdvancement.a().a(Items.MAP, new ChatMessage("advancements.adventure.root.title", new Object[0]), new ChatMessage("advancements.adventure.root.description", new Object[0]), new MinecraftKey("minecraft:textures/gui/advancements/backgrounds/adventure.png"), AdvancementFrameType.TASK, false, false, false).a(AdvancementRequirements.OR).a("killed_something", (CriterionInstance) CriterionTriggerKilled.b.c()).a("killed_by_something", (CriterionInstance) CriterionTriggerKilled.b.d()).a(consumer, "adventure/root");
        Advancement advancement1 = Advancement.SerializedAdvancement.a().a(advancement).a(Blocks.RED_BED, new ChatMessage("advancements.adventure.sleep_in_bed.title", new Object[0]), new ChatMessage("advancements.adventure.sleep_in_bed.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a("slept_in_bed", (CriterionInstance) CriterionTriggerLocation.b.c()).a(consumer, "adventure/sleep_in_bed");
        Advancement advancement2 = this.b(Advancement.SerializedAdvancement.a()).a(advancement1).a(Items.DIAMOND_BOOTS, new ChatMessage("advancements.adventure.adventuring_time.title", new Object[0]), new ChatMessage("advancements.adventure.adventuring_time.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.CHALLENGE, true, true, false).a(AdvancementRewards.a.a(500)).a(consumer, "adventure/adventuring_time");
        Advancement advancement3 = Advancement.SerializedAdvancement.a().a(advancement).a(Items.EMERALD, new ChatMessage("advancements.adventure.trade.title", new Object[0]), new ChatMessage("advancements.adventure.trade.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a("traded", (CriterionInstance) CriterionTriggerVillagerTrade.b.c()).a(consumer, "adventure/trade");
        Advancement advancement4 = this.a(Advancement.SerializedAdvancement.a()).a(advancement).a(Items.IRON_SWORD, new ChatMessage("advancements.adventure.kill_a_mob.title", new Object[0]), new ChatMessage("advancements.adventure.kill_a_mob.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a(AdvancementRequirements.OR).a(consumer, "adventure/kill_a_mob");
        Advancement advancement5 = this.a(Advancement.SerializedAdvancement.a()).a(advancement4).a(Items.DIAMOND_SWORD, new ChatMessage("advancements.adventure.kill_all_mobs.title", new Object[0]), new ChatMessage("advancements.adventure.kill_all_mobs.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.CHALLENGE, true, true, false).a(AdvancementRewards.a.a(100)).a(consumer, "adventure/kill_all_mobs");
        Advancement advancement6 = Advancement.SerializedAdvancement.a().a(advancement4).a(Items.BOW, new ChatMessage("advancements.adventure.shoot_arrow.title", new Object[0]), new ChatMessage("advancements.adventure.shoot_arrow.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a("shot_arrow", (CriterionInstance) CriterionTriggerPlayerHurtEntity.b.a(CriterionConditionDamage.a.a().a(CriterionConditionDamageSource.a.a().a(true).a(CriterionConditionEntity.a.a().a(EntityTypes.ARROW))))).a(consumer, "adventure/shoot_arrow");
        Advancement advancement7 = Advancement.SerializedAdvancement.a().a(advancement4).a(Items.TRIDENT, new ChatMessage("advancements.adventure.throw_trident.title", new Object[0]), new ChatMessage("advancements.adventure.throw_trident.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a("shot_trident", (CriterionInstance) CriterionTriggerPlayerHurtEntity.b.a(CriterionConditionDamage.a.a().a(CriterionConditionDamageSource.a.a().a(true).a(CriterionConditionEntity.a.a().a(EntityTypes.TRIDENT))))).a(consumer, "adventure/throw_trident");
        Advancement advancement8 = Advancement.SerializedAdvancement.a().a(advancement7).a(Items.TRIDENT, new ChatMessage("advancements.adventure.very_very_frightening.title", new Object[0]), new ChatMessage("advancements.adventure.very_very_frightening.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a("struck_villager", (CriterionInstance) CriterionTriggerChanneledLightning.b.a(CriterionConditionEntity.a.a().a(EntityTypes.VILLAGER).b())).a(consumer, "adventure/very_very_frightening");
        Advancement advancement9 = Advancement.SerializedAdvancement.a().a(advancement3).a(Blocks.CARVED_PUMPKIN, new ChatMessage("advancements.adventure.summon_iron_golem.title", new Object[0]), new ChatMessage("advancements.adventure.summon_iron_golem.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.GOAL, true, true, false).a("summoned_golem", (CriterionInstance) CriterionTriggerSummonedEntity.b.a(CriterionConditionEntity.a.a().a(EntityTypes.IRON_GOLEM))).a(consumer, "adventure/summon_iron_golem");
        Advancement advancement10 = Advancement.SerializedAdvancement.a().a(advancement6).a(Items.ARROW, new ChatMessage("advancements.adventure.sniper_duel.title", new Object[0]), new ChatMessage("advancements.adventure.sniper_duel.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.CHALLENGE, true, true, false).a(AdvancementRewards.a.a(50)).a("killed_skeleton", (CriterionInstance) CriterionTriggerKilled.b.a(CriterionConditionEntity.a.a().a(EntityTypes.SKELETON).a(CriterionConditionDistance.a(CriterionConditionValue.FloatRange.b(50.0F))), CriterionConditionDamageSource.a.a().a(true))).a(consumer, "adventure/sniper_duel");
        Advancement advancement11 = Advancement.SerializedAdvancement.a().a(advancement4).a(Items.TOTEM_OF_UNDYING, new ChatMessage("advancements.adventure.totem_of_undying.title", new Object[0]), new ChatMessage("advancements.adventure.totem_of_undying.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.GOAL, true, true, false).a("used_totem", (CriterionInstance) CriterionTriggerUsedTotem.b.a((IMaterial) Items.TOTEM_OF_UNDYING)).a(consumer, "adventure/totem_of_undying");
    }

    private Advancement.SerializedAdvancement a(Advancement.SerializedAdvancement advancement_serializedadvancement) {
        EntityTypes[] aentitytypes = DebugReportAdvancementAdventure.b;
        int i = aentitytypes.length;

        for (int j = 0; j < i; ++j) {
            EntityTypes<?> entitytypes = aentitytypes[j];

            advancement_serializedadvancement.a(IRegistry.ENTITY_TYPE.getKey(entitytypes).toString(), (CriterionInstance) CriterionTriggerKilled.b.a(CriterionConditionEntity.a.a().a(entitytypes)));
        }

        return advancement_serializedadvancement;
    }

    private Advancement.SerializedAdvancement b(Advancement.SerializedAdvancement advancement_serializedadvancement) {
        BiomeBase[] abiomebase = DebugReportAdvancementAdventure.a;
        int i = abiomebase.length;

        for (int j = 0; j < i; ++j) {
            BiomeBase biomebase = abiomebase[j];

            advancement_serializedadvancement.a(IRegistry.BIOME.getKey(biomebase).toString(), (CriterionInstance) CriterionTriggerLocation.b.a(CriterionConditionLocation.a(biomebase)));
        }

        return advancement_serializedadvancement;
    }
}
