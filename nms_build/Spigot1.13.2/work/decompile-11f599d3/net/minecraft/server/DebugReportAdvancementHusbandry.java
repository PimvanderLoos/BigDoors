package net.minecraft.server;

import java.util.function.Consumer;

public class DebugReportAdvancementHusbandry implements Consumer<Consumer<Advancement>> {

    private static final EntityTypes<?>[] a = new EntityTypes[] { EntityTypes.HORSE, EntityTypes.SHEEP, EntityTypes.COW, EntityTypes.MOOSHROOM, EntityTypes.PIG, EntityTypes.CHICKEN, EntityTypes.WOLF, EntityTypes.OCELOT, EntityTypes.RABBIT, EntityTypes.LLAMA, EntityTypes.TURTLE};
    private static final Item[] b = new Item[] { Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
    private static final Item[] c = new Item[] { Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
    private static final Item[] d = new Item[] { Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP};

    public DebugReportAdvancementHusbandry() {}

    public void accept(Consumer<Advancement> consumer) {
        Advancement advancement = Advancement.SerializedAdvancement.a().a(Blocks.HAY_BLOCK, new ChatMessage("advancements.husbandry.root.title", new Object[0]), new ChatMessage("advancements.husbandry.root.description", new Object[0]), new MinecraftKey("minecraft:textures/gui/advancements/backgrounds/husbandry.png"), AdvancementFrameType.TASK, false, false, false).a("consumed_item", (CriterionInstance) CriterionTriggerConsumeItem.b.c()).a(consumer, "husbandry/root");
        Advancement advancement1 = Advancement.SerializedAdvancement.a().a(advancement).a(Items.WHEAT, new ChatMessage("advancements.husbandry.plant_seed.title", new Object[0]), new ChatMessage("advancements.husbandry.plant_seed.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a(AdvancementRequirements.OR).a("wheat", (CriterionInstance) CriterionTriggerPlacedBlock.b.a(Blocks.WHEAT)).a("pumpkin_stem", (CriterionInstance) CriterionTriggerPlacedBlock.b.a(Blocks.PUMPKIN_STEM)).a("melon_stem", (CriterionInstance) CriterionTriggerPlacedBlock.b.a(Blocks.MELON_STEM)).a("beetroots", (CriterionInstance) CriterionTriggerPlacedBlock.b.a(Blocks.BEETROOTS)).a("nether_wart", (CriterionInstance) CriterionTriggerPlacedBlock.b.a(Blocks.NETHER_WART)).a(consumer, "husbandry/plant_seed");
        Advancement advancement2 = Advancement.SerializedAdvancement.a().a(advancement).a(Items.WHEAT, new ChatMessage("advancements.husbandry.breed_an_animal.title", new Object[0]), new ChatMessage("advancements.husbandry.breed_an_animal.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a(AdvancementRequirements.OR).a("bred", (CriterionInstance) CriterionTriggerBredAnimals.b.c()).a(consumer, "husbandry/breed_an_animal");
        Advancement advancement3 = this.a(Advancement.SerializedAdvancement.a()).a(advancement1).a(Items.APPLE, new ChatMessage("advancements.husbandry.balanced_diet.title", new Object[0]), new ChatMessage("advancements.husbandry.balanced_diet.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.CHALLENGE, true, true, false).a(AdvancementRewards.a.a(100)).a(consumer, "husbandry/balanced_diet");
        Advancement advancement4 = Advancement.SerializedAdvancement.a().a(advancement1).a(Items.DIAMOND_HOE, new ChatMessage("advancements.husbandry.break_diamond_hoe.title", new Object[0]), new ChatMessage("advancements.husbandry.break_diamond_hoe.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.CHALLENGE, true, true, false).a(AdvancementRewards.a.a(100)).a("broke_hoe", (CriterionInstance) CriterionTriggerItemDurabilityChanged.b.a(CriterionConditionItem.a.a().a((IMaterial) Items.DIAMOND_HOE).b(), CriterionConditionValue.IntegerRange.a(-1))).a(consumer, "husbandry/break_diamond_hoe");
        Advancement advancement5 = Advancement.SerializedAdvancement.a().a(advancement).a(Items.LEAD, new ChatMessage("advancements.husbandry.tame_an_animal.title", new Object[0]), new ChatMessage("advancements.husbandry.tame_an_animal.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a("tamed_animal", (CriterionInstance) CriterionTriggerTamedAnimal.b.c()).a(consumer, "husbandry/tame_an_animal");
        Advancement advancement6 = this.b(Advancement.SerializedAdvancement.a()).a(advancement2).a(Items.GOLDEN_CARROT, new ChatMessage("advancements.husbandry.breed_all_animals.title", new Object[0]), new ChatMessage("advancements.husbandry.breed_all_animals.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.CHALLENGE, true, true, false).a(AdvancementRewards.a.a(100)).a(consumer, "husbandry/bred_all_animals");
        Advancement advancement7 = this.d(Advancement.SerializedAdvancement.a()).a(advancement).a(AdvancementRequirements.OR).a(Items.FISHING_ROD, new ChatMessage("advancements.husbandry.fishy_business.title", new Object[0]), new ChatMessage("advancements.husbandry.fishy_business.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a(consumer, "husbandry/fishy_business");
        Advancement advancement8 = this.c(Advancement.SerializedAdvancement.a()).a(advancement7).a(AdvancementRequirements.OR).a(Items.PUFFERFISH_BUCKET, new ChatMessage("advancements.husbandry.tactical_fishing.title", new Object[0]), new ChatMessage("advancements.husbandry.tactical_fishing.description", new Object[0]), (MinecraftKey) null, AdvancementFrameType.TASK, true, true, false).a(consumer, "husbandry/tactical_fishing");
    }

    private Advancement.SerializedAdvancement a(Advancement.SerializedAdvancement advancement_serializedadvancement) {
        Item[] aitem = DebugReportAdvancementHusbandry.d;
        int i = aitem.length;

        for (int j = 0; j < i; ++j) {
            Item item = aitem[j];

            advancement_serializedadvancement.a(IRegistry.ITEM.getKey(item).getKey(), (CriterionInstance) CriterionTriggerConsumeItem.b.a((IMaterial) item));
        }

        return advancement_serializedadvancement;
    }

    private Advancement.SerializedAdvancement b(Advancement.SerializedAdvancement advancement_serializedadvancement) {
        EntityTypes[] aentitytypes = DebugReportAdvancementHusbandry.a;
        int i = aentitytypes.length;

        for (int j = 0; j < i; ++j) {
            EntityTypes<?> entitytypes = aentitytypes[j];

            advancement_serializedadvancement.a(EntityTypes.getName(entitytypes).toString(), (CriterionInstance) CriterionTriggerBredAnimals.b.a(CriterionConditionEntity.a.a().a(entitytypes)));
        }

        return advancement_serializedadvancement;
    }

    private Advancement.SerializedAdvancement c(Advancement.SerializedAdvancement advancement_serializedadvancement) {
        Item[] aitem = DebugReportAdvancementHusbandry.c;
        int i = aitem.length;

        for (int j = 0; j < i; ++j) {
            Item item = aitem[j];

            advancement_serializedadvancement.a(IRegistry.ITEM.getKey(item).getKey(), (CriterionInstance) CriterionTriggerFilledBucket.b.a(CriterionConditionItem.a.a().a((IMaterial) item).b()));
        }

        return advancement_serializedadvancement;
    }

    private Advancement.SerializedAdvancement d(Advancement.SerializedAdvancement advancement_serializedadvancement) {
        Item[] aitem = DebugReportAdvancementHusbandry.b;
        int i = aitem.length;

        for (int j = 0; j < i; ++j) {
            Item item = aitem[j];

            advancement_serializedadvancement.a(IRegistry.ITEM.getKey(item).getKey(), (CriterionInstance) CriterionTriggerFishingRodHooked.b.a(CriterionConditionItem.a, CriterionConditionEntity.a, CriterionConditionItem.a.a().a((IMaterial) item).b()));
        }

        return advancement_serializedadvancement;
    }
}
