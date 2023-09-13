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

    public boolean matches(WorldServer worldserver, BlockPosition blockposition) {
        if (this == CriterionConditionFluid.ANY) {
            return true;
        } else if (!worldserver.isLoaded(blockposition)) {
            return false;
        } else {
            Fluid fluid = worldserver.getFluidState(blockposition);
            FluidType fluidtype = fluid.getType();

            return this.tag != null && !fluidtype.is(this.tag) ? false : (this.fluid != null && fluidtype != this.fluid ? false : this.properties.matches(fluid));
        }
    }

    public static CriterionConditionFluid fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "fluid");
            FluidType fluidtype = null;

            if (jsonobject.has("fluid")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "fluid"));

                fluidtype = (FluidType) IRegistry.FLUID.get(minecraftkey);
            }

            Tag<FluidType> tag = null;

            if (jsonobject.has("tag")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "tag"));

                tag = TagsInstance.getInstance().getTagOrThrow(IRegistry.FLUID_REGISTRY, minecraftkey1, (minecraftkey2) -> {
                    return new JsonSyntaxException("Unknown fluid tag '" + minecraftkey2 + "'");
                });
            }

            CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.fromJson(jsonobject.get("state"));

            return new CriterionConditionFluid(tag, fluidtype, criteriontriggerproperties);
        } else {
            return CriterionConditionFluid.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionFluid.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (this.fluid != null) {
                jsonobject.addProperty("fluid", IRegistry.FLUID.getKey(this.fluid).toString());
            }

            if (this.tag != null) {
                jsonobject.addProperty("tag", TagsInstance.getInstance().getIdOrThrow(IRegistry.FLUID_REGISTRY, this.tag, () -> {
                    return new IllegalStateException("Unknown fluid tag");
                }).toString());
            }

            jsonobject.add("state", this.properties.serializeToJson());
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

        public static CriterionConditionFluid.a fluid() {
            return new CriterionConditionFluid.a();
        }

        public CriterionConditionFluid.a of(FluidType fluidtype) {
            this.fluid = fluidtype;
            return this;
        }

        public CriterionConditionFluid.a of(Tag<FluidType> tag) {
            this.fluids = tag;
            return this;
        }

        public CriterionConditionFluid.a setProperties(CriterionTriggerProperties criteriontriggerproperties) {
            this.properties = criteriontriggerproperties;
            return this;
        }

        public CriterionConditionFluid build() {
            return new CriterionConditionFluid(this.fluids, this.fluid, this.properties);
        }
    }
}
