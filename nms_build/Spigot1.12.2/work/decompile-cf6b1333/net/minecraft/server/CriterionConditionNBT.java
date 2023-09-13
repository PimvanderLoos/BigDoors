package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

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
        return this == CriterionConditionNBT.a ? true : this.a((NBTBase) CommandAbstract.a(entity));
    }

    public boolean a(@Nullable NBTBase nbtbase) {
        return nbtbase == null ? this == CriterionConditionNBT.a : this.b == null || GameProfileSerializer.a(this.b, nbtbase, true);
    }

    public static CriterionConditionNBT a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            NBTTagCompound nbttagcompound;

            try {
                nbttagcompound = MojangsonParser.parse(ChatDeserializer.a(jsonelement, "nbt"));
            } catch (MojangsonParseException mojangsonparseexception) {
                throw new JsonSyntaxException("Invalid nbt tag: " + mojangsonparseexception.getMessage());
            }

            return new CriterionConditionNBT(nbttagcompound);
        } else {
            return CriterionConditionNBT.a;
        }
    }
}
