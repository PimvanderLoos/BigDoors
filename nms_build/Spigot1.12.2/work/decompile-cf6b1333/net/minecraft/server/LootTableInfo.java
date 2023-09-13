package net.minecraft.server;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import javax.annotation.Nullable;

public class LootTableInfo {

    private final float a;
    private final WorldServer b;
    private final LootTableRegistry c;
    @Nullable
    private final Entity d;
    @Nullable
    private final EntityHuman e;
    @Nullable
    private final DamageSource f;
    private final Set<LootTable> g = Sets.newLinkedHashSet();

    public LootTableInfo(float f, WorldServer worldserver, LootTableRegistry loottableregistry, @Nullable Entity entity, @Nullable EntityHuman entityhuman, @Nullable DamageSource damagesource) {
        this.a = f;
        this.b = worldserver;
        this.c = loottableregistry;
        this.d = entity;
        this.e = entityhuman;
        this.f = damagesource;
    }

    @Nullable
    public Entity a() {
        return this.d;
    }

    @Nullable
    public Entity b() {
        return this.e;
    }

    @Nullable
    public Entity c() {
        return this.f == null ? null : this.f.getEntity();
    }

    public boolean a(LootTable loottable) {
        return this.g.add(loottable);
    }

    public void b(LootTable loottable) {
        this.g.remove(loottable);
    }

    public LootTableRegistry e() {
        return this.c;
    }

    public float f() {
        return this.a;
    }

    @Nullable
    public Entity a(LootTableInfo.EntityTarget loottableinfo_entitytarget) {
        switch (loottableinfo_entitytarget) {
        case THIS:
            return this.a();

        case KILLER:
            return this.c();

        case KILLER_PLAYER:
            return this.b();

        default:
            return null;
        }
    }

    public static enum EntityTarget {

        THIS("this"), KILLER("killer"), KILLER_PLAYER("killer_player");

        private final String d;

        private EntityTarget(String s) {
            this.d = s;
        }

        public static LootTableInfo.EntityTarget a(String s) {
            LootTableInfo.EntityTarget[] aloottableinfo_entitytarget = values();
            int i = aloottableinfo_entitytarget.length;

            for (int j = 0; j < i; ++j) {
                LootTableInfo.EntityTarget loottableinfo_entitytarget = aloottableinfo_entitytarget[j];

                if (loottableinfo_entitytarget.d.equals(s)) {
                    return loottableinfo_entitytarget;
                }
            }

            throw new IllegalArgumentException("Invalid entity target " + s);
        }

        public static class a extends TypeAdapter<LootTableInfo.EntityTarget> {

            public a() {}

            public void a(JsonWriter jsonwriter, LootTableInfo.EntityTarget loottableinfo_entitytarget) throws IOException {
                jsonwriter.value(loottableinfo_entitytarget.d);
            }

            public LootTableInfo.EntityTarget a(JsonReader jsonreader) throws IOException {
                return LootTableInfo.EntityTarget.a(jsonreader.nextString());
            }

            public Object read(JsonReader jsonreader) throws IOException {
                return this.a(jsonreader);
            }

            public void write(JsonWriter jsonwriter, Object object) throws IOException {
                this.a(jsonwriter, (LootTableInfo.EntityTarget) object);
            }
        }
    }

    public static class a {

        private final WorldServer a;
        private float b;
        private Entity c;
        private EntityHuman d;
        private DamageSource e;

        public a(WorldServer worldserver) {
            this.a = worldserver;
        }

        public LootTableInfo.a a(float f) {
            this.b = f;
            return this;
        }

        public LootTableInfo.a a(Entity entity) {
            this.c = entity;
            return this;
        }

        public LootTableInfo.a a(EntityHuman entityhuman) {
            this.d = entityhuman;
            return this;
        }

        public LootTableInfo.a a(DamageSource damagesource) {
            this.e = damagesource;
            return this;
        }

        public LootTableInfo a() {
            return new LootTableInfo(this.b, this.a, this.a.getLootTableRegistry(), this.c, this.d, this.e);
        }
    }
}
