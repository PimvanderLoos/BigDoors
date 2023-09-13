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

    public static final CriterionConditionNBT ANY = new CriterionConditionNBT((NBTTagCompound) null);
    @Nullable
    private final NBTTagCompound tag;

    public CriterionConditionNBT(@Nullable NBTTagCompound nbttagcompound) {
        this.tag = nbttagcompound;
    }

    public boolean matches(ItemStack itemstack) {
        return this == CriterionConditionNBT.ANY ? true : this.matches((NBTBase) itemstack.getTag());
    }

    public boolean matches(Entity entity) {
        return this == CriterionConditionNBT.ANY ? true : this.matches((NBTBase) getEntityTagToCompare(entity));
    }

    public boolean matches(@Nullable NBTBase nbtbase) {
        return nbtbase == null ? this == CriterionConditionNBT.ANY : this.tag == null || GameProfileSerializer.compareNbt(this.tag, nbtbase, true);
    }

    public JsonElement serializeToJson() {
        return (JsonElement) (this != CriterionConditionNBT.ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
    }

    public static CriterionConditionNBT fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            NBTTagCompound nbttagcompound;

            try {
                nbttagcompound = MojangsonParser.parseTag(ChatDeserializer.convertToString(jsonelement, "nbt"));
            } catch (CommandSyntaxException commandsyntaxexception) {
                throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
            }

            return new CriterionConditionNBT(nbttagcompound);
        } else {
            return CriterionConditionNBT.ANY;
        }
    }

    public static NBTTagCompound getEntityTagToCompare(Entity entity) {
        NBTTagCompound nbttagcompound = entity.saveWithoutId(new NBTTagCompound());

        if (entity instanceof EntityHuman) {
            ItemStack itemstack = ((EntityHuman) entity).getInventory().getSelected();

            if (!itemstack.isEmpty()) {
                nbttagcompound.put("SelectedItem", itemstack.save(new NBTTagCompound()));
            }
        }

        return nbttagcompound;
    }
}
