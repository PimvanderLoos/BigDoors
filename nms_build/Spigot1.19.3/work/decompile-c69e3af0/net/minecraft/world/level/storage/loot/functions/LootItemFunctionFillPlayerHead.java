package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemFunctionFillPlayerHead extends LootItemFunctionConditional {

    final LootTableInfo.EntityTarget entityTarget;

    public LootItemFunctionFillPlayerHead(LootItemCondition[] alootitemcondition, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        super(alootitemcondition);
        this.entityTarget = loottableinfo_entitytarget;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.FILL_PLAYER_HEAD;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(this.entityTarget.getParam());
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.is(Items.PLAYER_HEAD)) {
            Entity entity = (Entity) loottableinfo.getParamOrNull(this.entityTarget.getParam());

            if (entity instanceof EntityHuman) {
                GameProfile gameprofile = ((EntityHuman) entity).getGameProfile();

                itemstack.getOrCreateTag().put("SkullOwner", GameProfileSerializer.writeGameProfile(new NBTTagCompound(), gameprofile));
            }
        }

        return itemstack;
    }

    public static LootItemFunctionConditional.a<?> fillPlayerHead(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        return simpleBuilder((alootitemcondition) -> {
            return new LootItemFunctionFillPlayerHead(alootitemcondition, loottableinfo_entitytarget);
        });
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionFillPlayerHead> {

        public a() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionFillPlayerHead lootitemfunctionfillplayerhead, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionfillplayerhead, jsonserializationcontext);
            jsonobject.add("entity", jsonserializationcontext.serialize(lootitemfunctionfillplayerhead.entityTarget));
        }

        @Override
        public LootItemFunctionFillPlayerHead deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) ChatDeserializer.getAsObject(jsonobject, "entity", jsondeserializationcontext, LootTableInfo.EntityTarget.class);

            return new LootItemFunctionFillPlayerHead(alootitemcondition, loottableinfo_entitytarget);
        }
    }
}
