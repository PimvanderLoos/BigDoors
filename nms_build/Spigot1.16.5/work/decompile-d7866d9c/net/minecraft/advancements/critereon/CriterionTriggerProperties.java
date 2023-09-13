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

    public static final CriterionTriggerProperties a = new CriterionTriggerProperties(ImmutableList.of());
    private final List<CriterionTriggerProperties.c> b;

    private static CriterionTriggerProperties.c a(String s, JsonElement jsonelement) {
        if (jsonelement.isJsonPrimitive()) {
            String s1 = jsonelement.getAsString();

            return new CriterionTriggerProperties.b(s, s1);
        } else {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "value");
            String s2 = jsonobject.has("min") ? b(jsonobject.get("min")) : null;
            String s3 = jsonobject.has("max") ? b(jsonobject.get("max")) : null;

            return (CriterionTriggerProperties.c) (s2 != null && s2.equals(s3) ? new CriterionTriggerProperties.b(s, s2) : new CriterionTriggerProperties.d(s, s2, s3));
        }
    }

    @Nullable
    private static String b(JsonElement jsonelement) {
        return jsonelement.isJsonNull() ? null : jsonelement.getAsString();
    }

    private CriterionTriggerProperties(List<CriterionTriggerProperties.c> list) {
        this.b = ImmutableList.copyOf(list);
    }

    public <S extends IBlockDataHolder<?, S>> boolean a(BlockStateList<?, S> blockstatelist, S s0) {
        Iterator iterator = this.b.iterator();

        CriterionTriggerProperties.c criteriontriggerproperties_c;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            criteriontriggerproperties_c = (CriterionTriggerProperties.c) iterator.next();
        } while (criteriontriggerproperties_c.a(blockstatelist, s0));

        return false;
    }

    public boolean a(IBlockData iblockdata) {
        return this.a(iblockdata.getBlock().getStates(), (IBlockDataHolder) iblockdata);
    }

    public boolean a(Fluid fluid) {
        return this.a(fluid.getType().g(), (IBlockDataHolder) fluid);
    }

    public void a(BlockStateList<?, ?> blockstatelist, Consumer<String> consumer) {
        this.b.forEach((criteriontriggerproperties_c) -> {
            criteriontriggerproperties_c.a(blockstatelist, consumer);
        });
    }

    public static CriterionTriggerProperties a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "properties");
            List<CriterionTriggerProperties.c> list = Lists.newArrayList();
            Iterator iterator = jsonobject.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, JsonElement> entry = (Entry) iterator.next();

                list.add(a((String) entry.getKey(), (JsonElement) entry.getValue()));
            }

            return new CriterionTriggerProperties(list);
        } else {
            return CriterionTriggerProperties.a;
        }
    }

    public JsonElement a() {
        if (this == CriterionTriggerProperties.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.b.isEmpty()) {
                this.b.forEach((criteriontriggerproperties_c) -> {
                    jsonobject.add(criteriontriggerproperties_c.b(), criteriontriggerproperties_c.a());
                });
            }

            return jsonobject;
        }
    }

    public static class a {

        private final List<CriterionTriggerProperties.c> a = Lists.newArrayList();

        private a() {}

        public static CriterionTriggerProperties.a a() {
            return new CriterionTriggerProperties.a();
        }

        public CriterionTriggerProperties.a a(IBlockState<?> iblockstate, String s) {
            this.a.add(new CriterionTriggerProperties.b(iblockstate.getName(), s));
            return this;
        }

        public CriterionTriggerProperties.a a(IBlockState<Integer> iblockstate, int i) {
            return this.a(iblockstate, Integer.toString(i));
        }

        public CriterionTriggerProperties.a a(IBlockState<Boolean> iblockstate, boolean flag) {
            return this.a(iblockstate, Boolean.toString(flag));
        }

        public <T extends Comparable<T> & INamable> CriterionTriggerProperties.a a(IBlockState<T> iblockstate, T t0) {
            return this.a(iblockstate, ((INamable) t0).getName());
        }

        public CriterionTriggerProperties b() {
            return new CriterionTriggerProperties(this.a);
        }
    }

    static class d extends CriterionTriggerProperties.c {

        @Nullable
        private final String a;
        @Nullable
        private final String b;

        public d(String s, @Nullable String s1, @Nullable String s2) {
            super(s);
            this.a = s1;
            this.b = s2;
        }

        @Override
        protected <T extends Comparable<T>> boolean a(IBlockDataHolder<?, ?> iblockdataholder, IBlockState<T> iblockstate) {
            T t0 = iblockdataholder.get(iblockstate);
            Optional optional;

            if (this.a != null) {
                optional = iblockstate.b(this.a);
                if (!optional.isPresent() || t0.compareTo(optional.get()) < 0) {
                    return false;
                }
            }

            if (this.b != null) {
                optional = iblockstate.b(this.b);
                if (!optional.isPresent() || t0.compareTo(optional.get()) > 0) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public JsonElement a() {
            JsonObject jsonobject = new JsonObject();

            if (this.a != null) {
                jsonobject.addProperty("min", this.a);
            }

            if (this.b != null) {
                jsonobject.addProperty("max", this.b);
            }

            return jsonobject;
        }
    }

    static class b extends CriterionTriggerProperties.c {

        private final String a;

        public b(String s, String s1) {
            super(s);
            this.a = s1;
        }

        @Override
        protected <T extends Comparable<T>> boolean a(IBlockDataHolder<?, ?> iblockdataholder, IBlockState<T> iblockstate) {
            T t0 = iblockdataholder.get(iblockstate);
            Optional<T> optional = iblockstate.b(this.a);

            return optional.isPresent() && t0.compareTo(optional.get()) == 0;
        }

        @Override
        public JsonElement a() {
            return new JsonPrimitive(this.a);
        }
    }

    abstract static class c {

        private final String a;

        public c(String s) {
            this.a = s;
        }

        public <S extends IBlockDataHolder<?, S>> boolean a(BlockStateList<?, S> blockstatelist, S s0) {
            IBlockState<?> iblockstate = blockstatelist.a(this.a);

            return iblockstate == null ? false : this.a(s0, iblockstate);
        }

        protected abstract <T extends Comparable<T>> boolean a(IBlockDataHolder<?, ?> iblockdataholder, IBlockState<T> iblockstate);

        public abstract JsonElement a();

        public String b() {
            return this.a;
        }

        public void a(BlockStateList<?, ?> blockstatelist, Consumer<String> consumer) {
            IBlockState<?> iblockstate = blockstatelist.a(this.a);

            if (iblockstate == null) {
                consumer.accept(this.a);
            }

        }
    }
}
