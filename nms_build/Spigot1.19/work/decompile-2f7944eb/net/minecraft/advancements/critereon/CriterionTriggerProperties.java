package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.INamable;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.IBlockDataHolder;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;

public class CriterionTriggerProperties {

    public static final CriterionTriggerProperties ANY = new CriterionTriggerProperties(ImmutableList.of());
    private final List<CriterionTriggerProperties.c> properties;

    private static CriterionTriggerProperties.c fromJson(String s, JsonElement jsonelement) {
        if (jsonelement.isJsonPrimitive()) {
            String s1 = jsonelement.getAsString();

            return new CriterionTriggerProperties.b(s, s1);
        } else {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "value");
            String s2 = jsonobject.has("min") ? getStringOrNull(jsonobject.get("min")) : null;
            String s3 = jsonobject.has("max") ? getStringOrNull(jsonobject.get("max")) : null;

            return (CriterionTriggerProperties.c) (s2 != null && s2.equals(s3) ? new CriterionTriggerProperties.b(s, s2) : new CriterionTriggerProperties.d(s, s2, s3));
        }
    }

    @Nullable
    private static String getStringOrNull(JsonElement jsonelement) {
        return jsonelement.isJsonNull() ? null : jsonelement.getAsString();
    }

    CriterionTriggerProperties(List<CriterionTriggerProperties.c> list) {
        this.properties = ImmutableList.copyOf(list);
    }

    public <S extends IBlockDataHolder<?, S>> boolean matches(BlockStateList<?, S> blockstatelist, S s0) {
        Iterator iterator = this.properties.iterator();

        CriterionTriggerProperties.c criteriontriggerproperties_c;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            criteriontriggerproperties_c = (CriterionTriggerProperties.c) iterator.next();
        } while (criteriontriggerproperties_c.match(blockstatelist, s0));

        return false;
    }

    public boolean matches(IBlockData iblockdata) {
        return this.matches(iblockdata.getBlock().getStateDefinition(), iblockdata);
    }

    public boolean matches(Fluid fluid) {
        return this.matches(fluid.getType().getStateDefinition(), fluid);
    }

    public void checkState(BlockStateList<?, ?> blockstatelist, Consumer<String> consumer) {
        this.properties.forEach((criteriontriggerproperties_c) -> {
            criteriontriggerproperties_c.checkState(blockstatelist, consumer);
        });
    }

    public static CriterionTriggerProperties fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "properties");
            List<CriterionTriggerProperties.c> list = Lists.newArrayList();
            Iterator iterator = jsonobject.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator.next();

                list.add(fromJson((String) entry.getKey(), (JsonElement) entry.getValue()));
            }

            return new CriterionTriggerProperties(list);
        } else {
            return CriterionTriggerProperties.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionTriggerProperties.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.properties.isEmpty()) {
                this.properties.forEach((criteriontriggerproperties_c) -> {
                    jsonobject.add(criteriontriggerproperties_c.getName(), criteriontriggerproperties_c.toJson());
                });
            }

            return jsonobject;
        }
    }

    private static class b extends CriterionTriggerProperties.c {

        private final String value;

        public b(String s, String s1) {
            super(s);
            this.value = s1;
        }

        @Override
        protected <T extends Comparable<T>> boolean match(IBlockDataHolder<?, ?> iblockdataholder, IBlockState<T> iblockstate) {
            T t0 = iblockdataholder.getValue(iblockstate);
            Optional<T> optional = iblockstate.getValue(this.value);

            return optional.isPresent() && t0.compareTo((Comparable) optional.get()) == 0;
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(this.value);
        }
    }

    private static class d extends CriterionTriggerProperties.c {

        @Nullable
        private final String minValue;
        @Nullable
        private final String maxValue;

        public d(String s, @Nullable String s1, @Nullable String s2) {
            super(s);
            this.minValue = s1;
            this.maxValue = s2;
        }

        @Override
        protected <T extends Comparable<T>> boolean match(IBlockDataHolder<?, ?> iblockdataholder, IBlockState<T> iblockstate) {
            T t0 = iblockdataholder.getValue(iblockstate);
            Optional optional;

            if (this.minValue != null) {
                optional = iblockstate.getValue(this.minValue);
                if (!optional.isPresent() || t0.compareTo((Comparable) optional.get()) < 0) {
                    return false;
                }
            }

            if (this.maxValue != null) {
                optional = iblockstate.getValue(this.maxValue);
                if (!optional.isPresent() || t0.compareTo((Comparable) optional.get()) > 0) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonobject = new JsonObject();

            if (this.minValue != null) {
                jsonobject.addProperty("min", this.minValue);
            }

            if (this.maxValue != null) {
                jsonobject.addProperty("max", this.maxValue);
            }

            return jsonobject;
        }
    }

    private abstract static class c {

        private final String name;

        public c(String s) {
            this.name = s;
        }

        public <S extends IBlockDataHolder<?, S>> boolean match(BlockStateList<?, S> blockstatelist, S s0) {
            IBlockState<?> iblockstate = blockstatelist.getProperty(this.name);

            return iblockstate == null ? false : this.match(s0, iblockstate);
        }

        protected abstract <T extends Comparable<T>> boolean match(IBlockDataHolder<?, ?> iblockdataholder, IBlockState<T> iblockstate);

        public abstract JsonElement toJson();

        public String getName() {
            return this.name;
        }

        public void checkState(BlockStateList<?, ?> blockstatelist, Consumer<String> consumer) {
            IBlockState<?> iblockstate = blockstatelist.getProperty(this.name);

            if (iblockstate == null) {
                consumer.accept(this.name);
            }

        }
    }

    public static class a {

        private final List<CriterionTriggerProperties.c> matchers = Lists.newArrayList();

        private a() {}

        public static CriterionTriggerProperties.a properties() {
            return new CriterionTriggerProperties.a();
        }

        public CriterionTriggerProperties.a hasProperty(IBlockState<?> iblockstate, String s) {
            this.matchers.add(new CriterionTriggerProperties.b(iblockstate.getName(), s));
            return this;
        }

        public CriterionTriggerProperties.a hasProperty(IBlockState<Integer> iblockstate, int i) {
            return this.hasProperty(iblockstate, Integer.toString(i));
        }

        public CriterionTriggerProperties.a hasProperty(IBlockState<Boolean> iblockstate, boolean flag) {
            return this.hasProperty(iblockstate, Boolean.toString(flag));
        }

        public <T extends Comparable<T> & INamable> CriterionTriggerProperties.a hasProperty(IBlockState<T> iblockstate, T t0) {
            return this.hasProperty(iblockstate, ((INamable) t0).getSerializedName());
        }

        public CriterionTriggerProperties build() {
            return new CriterionTriggerProperties(this.matchers);
        }
    }
}
