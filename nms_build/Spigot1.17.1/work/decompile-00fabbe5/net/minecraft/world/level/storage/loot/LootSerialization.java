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

    public static GsonBuilder a() {
        return (new GsonBuilder()).registerTypeAdapter(IntRange.class, new IntRange.c()).registerTypeHierarchyAdapter(NumberProvider.class, NumberProviders.a()).registerTypeHierarchyAdapter(LootItemCondition.class, LootItemConditions.a()).registerTypeHierarchyAdapter(ScoreboardNameProvider.class, ScoreboardNameProviders.a()).registerTypeHierarchyAdapter(LootTableInfo.EntityTarget.class, new LootTableInfo.EntityTarget.a());
    }

    public static GsonBuilder b() {
        return a().registerTypeHierarchyAdapter(LootEntryAbstract.class, LootEntries.a()).registerTypeHierarchyAdapter(LootItemFunction.class, LootItemFunctions.a()).registerTypeHierarchyAdapter(NbtProvider.class, NbtProviders.a());
    }

    public static GsonBuilder c() {
        return b().registerTypeAdapter(LootSelector.class, new LootSelector.b()).registerTypeAdapter(LootTable.class, new LootTable.b());
    }
}
