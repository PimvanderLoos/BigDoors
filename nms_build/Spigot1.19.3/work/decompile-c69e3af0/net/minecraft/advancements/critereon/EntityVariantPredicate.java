package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;

public class EntityVariantPredicate<V> {

    private static final String VARIANT_KEY = "variant";
    final Codec<V> variantCodec;
    final Function<Entity, Optional<V>> getter;
    final EntitySubPredicate.a type;

    public static <V> EntityVariantPredicate<V> create(IRegistry<V> iregistry, Function<Entity, Optional<V>> function) {
        return new EntityVariantPredicate<>(iregistry.byNameCodec(), function);
    }

    public static <V> EntityVariantPredicate<V> create(Codec<V> codec, Function<Entity, Optional<V>> function) {
        return new EntityVariantPredicate<>(codec, function);
    }

    private EntityVariantPredicate(Codec<V> codec, Function<Entity, Optional<V>> function) {
        this.variantCodec = codec;
        this.getter = function;
        this.type = (jsonobject) -> {
            JsonElement jsonelement = jsonobject.get("variant");

            if (jsonelement == null) {
                throw new JsonParseException("Missing variant field");
            } else {
                V v0 = ((Pair) SystemUtils.getOrThrow(codec.decode(new Dynamic(JsonOps.INSTANCE, jsonelement)), JsonParseException::new)).getFirst();

                return this.createPredicate(v0);
            }
        };
    }

    public EntitySubPredicate.a type() {
        return this.type;
    }

    public EntitySubPredicate createPredicate(final V v0) {
        return new EntitySubPredicate() {
            @Override
            public boolean matches(Entity entity, WorldServer worldserver, @Nullable Vec3D vec3d) {
                return ((Optional) EntityVariantPredicate.this.getter.apply(entity)).filter((object) -> {
                    return object.equals(v0);
                }).isPresent();
            }

            @Override
            public JsonObject serializeCustomData() {
                JsonObject jsonobject = new JsonObject();

                jsonobject.add("variant", (JsonElement) SystemUtils.getOrThrow(EntityVariantPredicate.this.variantCodec.encodeStart(JsonOps.INSTANCE, v0), (s) -> {
                    return new JsonParseException("Can't serialize variant " + v0 + ", message " + s);
                }));
                return jsonobject;
            }

            @Override
            public EntitySubPredicate.a type() {
                return EntityVariantPredicate.this.type;
            }
        };
    }
}
