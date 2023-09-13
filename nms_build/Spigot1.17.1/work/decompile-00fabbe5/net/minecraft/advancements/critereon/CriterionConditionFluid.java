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

    public static final CriterionConditionFluid ANY = new CriterionConditionFluid((Tag) null, (FluidType) null, CriterionTriggerProperties.ANY);
    @Nullable
    private final Tag<FluidType> tag;
    @Nullable
    private final FluidType fluid;
    private final CriterionTriggerProperties properties;

    public CriterionConditionFluid(@Nullable Tag<FluidType> tag, @Nullable FluidType fluidtype, CriterionTriggerProperties criteriontriggerproperties) {
        this.tag = tag;
        this.fluid = fluidtype;
        this.properties = criteriontriggerproperties;
    }

    public boolean a(WorldServer worldserver, BlockPosition blockposition) {
        if (this == CriterionConditionFluid.ANY) {
            return true;
        } else if (!worldserver.o(blockposition)) {
            return false;
        } else {
            Fluid fluid = worldserver.getFluid(blockposition);
            FluidType fluidtype = fluid.getType();

            return this.tag != null && !fluidtype.a(this.tag) ? false : (this.fluid != null && fluidtype != this.fluid ? false : this.properties.a(fluid));
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

                tag = TagsInstance.a().a(IRegistry.FLUID_REGISTRY, minecraftkey1, (minecraftkey2) -> {
                    return new JsonSyntaxException("Unknown fluid tag '" + minecraftkey2 + "'");
                });
            }

            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.a(jsonobject.get("state"));

            return new CriterionConditionFluid(tag, fluidtype, criteriontriggerproperties);
        } else {
            return CriterionConditionFluid.ANY;
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionFluid.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.fluid != null) {
                jsonobject.addProperty("fluid", IRegistry.FLUID.getKey(this.fluid).toString());
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", TagsInstance.a().a(IRegistry.FLUID_REGISTRY, this.tag, () -> {
                    return new IllegalStateException("Unknown fluid tag");
                }).toString());
            }

            jsonobject.add("state", this.properties.a());
            return jsonobject;
        }
    }

    public static class a {

        @Nullable
        private FluidType fluid;
        @Nullable
        private Tag<FluidType> fluids;
        private CriterionTriggerProperties properties;

        private a() {
            this.properties = CriterionTriggerProperties.ANY;
        }

        public static CriterionConditionFluid.a a() {
            return new CriterionConditionFluid.a();
        }

        public CriterionConditionFluid.a a(FluidType fluidtype) {
            this.fluid = fluidtype;
            return this;
        }

        public CriterionConditionFluid.a a(Tag<FluidType> tag) {
            this.fluids = tag;
            return this;
        }

        public CriterionConditionFluid.a a(CriterionTriggerProperties criteriontriggerproperties) {
            this.properties = criteriontriggerproperties;
            return this;
        }

        public CriterionConditionFluid b() {
            return new CriterionConditionFluid(this.fluids, this.fluid, this.properties);
        }
    }
}
