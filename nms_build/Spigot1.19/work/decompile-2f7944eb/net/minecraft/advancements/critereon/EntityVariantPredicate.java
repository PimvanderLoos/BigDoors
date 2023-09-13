package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;

public class EntityVariantPredicate<V> {

    private static final String VARIANT_KEY = "variant";
    final IRegistry<V> registry;
    final Function<Entity, Optional<V>> getter;
    final EntitySubPredicate.a type;

    public static <V> EntityVariantPredicate<V> create(IRegistry<V> iregistry, Function<Entity, Optional<V>> function) {
        return new EntityVariantPredicate<>(iregistry, function);
    }

    private EntityVariantPredicate(IRegistry<V> iregistry, Function<Entity, Optional<V>> function) {
        this.registry = iregistry;
        this.getter = function;
        this.type = (jsonobject) -> {
            String s = ChatDeserializer.getAsString(jsonobject, "variant");
            V v0 = iregistry.get(MinecraftKey.tryParse(s));

            if (v0 == null) {
                throw new JsonSyntaxException("Unknown variant: " + s);
            } else {
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

                jsonobject.addProperty("variant", EntityVariantPredicate.this.registry.getKey(v0).toString());
                return jsonobject;
            }

            @Override
            public EntitySubPredicate.a type() {
                return EntityVariantPredicate.this.type;
            }
        };
    }
}
