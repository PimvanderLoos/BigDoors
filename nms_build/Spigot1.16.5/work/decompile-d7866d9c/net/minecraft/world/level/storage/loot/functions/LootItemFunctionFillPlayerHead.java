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

    private final LootTableInfo.EntityTarget a;

    public LootItemFunctionFillPlayerHead(LootItemCondition[] alootitemcondition, LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        super(alootitemcondition);
        this.a = loottableinfo_entitytarget;
    }

    @Override
    public LootItemFunctionType b() {
        return LootItemFunctions.t;
    }

    @Override
    public Set<LootContextParameter<?>> a() {
        return ImmutableSet.of(this.a.a());
    }

    @Override
    public ItemStack a(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (itemstack.getItem() == Items.PLAYER_HEAD) {
            Entity entity = (Entity) loottableinfo.getContextParameter(this.a.a());

            if (entity instanceof EntityHuman) {
                GameProfile gameprofile = ((EntityHuman) entity).getProfile();

                itemstack.getOrCreateTag().set("SkullOwner", GameProfileSerializer.serialize(new NBTTagCompound(), gameprofile));
            }
        }

        return itemstack;
    }

    public static class a extends LootItemFunctionConditional.c<LootItemFunctionFillPlayerHead> {

        public a() {}

        public void a(JsonObject jsonobject, LootItemFunctionFillPlayerHead lootitemfunctionfillplayerhead, JsonSerializationContext jsonserializationcontext) {
            super.a(jsonobject, (LootItemFunctionConditional) lootitemfunctionfillplayerhead, jsonserializationcontext);
            jsonobject.add("entity", jsonserializationcontext.serialize(lootitemfunctionfillplayerhead.a));
        }

        @Override
        public LootItemFunctionFillPlayerHead b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            LootTableInfo.EntityTarget loottableinfo_entitytarget = (LootTableInfo.EntityTarget) ChatDeserializer.a(jsonobject, "entity", jsondeserializationcontext, LootTableInfo.EntityTarget.class);

            return new LootItemFunctionFillPlayerHead(alootitemcondition, loottableinfo_entitytarget);
        }
    }
}
