package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsInstance;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;

public class CriterionConditionFluid {

    public static final CriterionConditionFluid a = new CriterionConditionFluid((Tag) null, (FluidType) null, CriterionTriggerProperties.a);
    @Nullable
    private final Tag<FluidType> b;
    @Nullable
    private final FluidType c;
    private final CriterionTriggerProperties d;

    public CriterionConditionFluid(@Nullable Tag<FluidType> tag, @Nullable FluidType fluidtype, CriterionTriggerProperties criteriontriggerproperties) {
        this.b = tag;
        this.c = fluidtype;
        this.d = criteriontriggerproperties;
    }

    public boolean a(WorldServer worldserver, BlockPosition blockposition) {
        if (this == CriterionConditionFluid.a) {
            return true;
        } else if (!worldserver.p(blockposition)) {
            return false;
        } else {
            Fluid fluid = worldserver.getFluid(blockposition);
            FluidType fluidtype = fluid.getType();

            return this.b != null && !this.b.isTagged(fluidtype) ? false : (this.c != null && fluidtype != this.c ? false : this.d.a(fluid));
        }
    }

    public static CriterionConditionFluid a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "fluid");
            FluidType fluidtype = null;

            if (jsonobject.has("fluid")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "fluid"));

                fluidtype = (FluidType) IRegistry.FLUID.get(minecraftkey);
            }

            Tag<FluidType> tag = null;

            if (jsonobject.has("tag")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));

                tag = TagsInstance.a().getFluidTags().a(minecraftkey1);
                if (tag == null) {
                    throw new JsonSyntaxException("Unknown fluid tag '" + minecraftkey1 + "'");
                }
            }

            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));

            return new CriterionConditionFluid(tag, fluidtype, criteriontriggerproperties);
        } else {
            return CriterionConditionFluid.a;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionFluid.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.c != null) {
                jsonobject.addProperty("fluid", IRegistry.FLUID.getKey(this.c).toString());
            }

            if (this.b != null) {
                jsonobject.addProperty("tag", TagsInstance.a().getFluidTags().b(this.b).toString());
            }

            jsonobject.add("state", this.d.a());
            return jsonobject;
        }
    }
}
