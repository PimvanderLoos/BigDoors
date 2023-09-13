package net.minecraft.world.level.storage.loot;

import com.google.gson.GsonBuilder;
import net.minecraft.world.level.storage.loot.entries.LootEntries;
import net.minecraft.world.level.storage.loot.entries.LootEntryAbstract;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProvider;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;

public class LootSerialization {

    public LootSerialization() {}

    public static GsonBuilder createConditionSerializer() {
        return (new GsonBuilder()).registerTypeAdapter(IntRange.class, new IntRange.c()).registerTypeHierarchyAdapter(NumberProvider.class, NumberProviders.createGsonAdapter()).registerTypeHierarchyAdapter(LootItemCondition.class, LootItemConditions.createGsonAdapter()).registerTypeHierarchyAdapter(ScoreboardNameProvider.class, ScoreboardNameProviders.createGsonAdapter()).registerTypeHierarchyAdapter(LootTableInfo.EntityTarget.class, new LootTableInfo.EntityTarget.a());
    }

    public static GsonBuilder createFunctionSerializer() {
        return createConditionSerializer().registerTypeHierarchyAdapter(LootEntryAbstract.class, LootEntries.createGsonAdapter()).registerTypeHierarchyAdapter(LootItemFunction.class, LootItemFunctions.createGsonAdapter()).registerTypeHierarchyAdapter(NbtProvider.class, NbtProviders.createGsonAdapter());
    }

    public static GsonBuilder createLootTableSerializer() {
        return createFunctionSerializer().registerTypeAdapter(LootSelector.class, new LootSelector.b()).registerTypeAdapter(LootTable.class, new LootTable.b());
    }
}
