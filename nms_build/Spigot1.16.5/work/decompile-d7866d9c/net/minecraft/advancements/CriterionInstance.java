package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.LootSerializationContext;
import net.minecraft.resources.MinecraftKey;

public interface CriterionInstance {

    MinecraftKey a();

    JsonObject a(LootSerializationContext lootserializationcontext);
}
