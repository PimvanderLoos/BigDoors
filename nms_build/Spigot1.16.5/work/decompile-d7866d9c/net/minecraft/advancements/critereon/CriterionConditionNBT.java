package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class CriterionConditionNBT {

    public static final CriterionConditionNBT a = new CriterionConditionNBT((NBTTagCompound) null);
    @Nullable
    private final NBTTagCompound b;

    public CriterionConditionNBT(@Nullable NBTTagCompound nbttagcompound) {
        this.b = nbttagcompound;
    }

    public boolean a(ItemStack itemstack) {
        return this == CriterionConditionNBT.a ? true : this.a((NBTBase) itemstack.getTag());
    }

    public boolean a(Entity entity) {
        return this == CriterionConditionNBT.a ? true : this.a((NBTBase) b(entity));
    }

    public boolean a(@Nullable NBTBase nbtbase) {
        return nbtbase == null ? this == CriterionConditionNBT.a : this.b == null || GameProfileSerializer.a(this.b, nbtbase, true);
    }

    public JsonElement a() {
        return (JsonElement) (this != CriterionConditionNBT.a && this.b != null ? new JsonPrimitive(this.b.toString()) : JsonNull.INSTANCE);
    }

    public static CriterionConditionNBT a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            NBTTagCompound nbttagcompound;

            try {
                nbttagcompound = MojangsonParser.parse(ChatDeserializer.a(jsonelement, "nbt"));
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
            }

            return new CriterionConditionNBT(nbttagcompound);
        } else {
            return CriterionConditionNBT.a;
        }
    }

    public static NBTTagCompound b(Entity entity) {
        NBTTagCompound nbttagcompound = entity.save(new NBTTagCompound());

        if (entity instanceof EntityHuman) {
            ItemStack itemstack = ((EntityHuman) entity).inventory.getItemInHand();

            if (!itemstack.isEmpty()) {
                nbttagcompound.set("SelectedItem", itemstack.save(new NBTTagCompound()));
            }
        }

        return nbttagcompound;
    }
}
