package net.minecraft.advancements.critereon;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.phys.Vec3D;

public interface EntitySubPredicate {

    EntitySubPredicate ANY = new EntitySubPredicate() {
        @Override
        public boolean matches(Entity entity, WorldServer worldserver, @Nullable Vec3D vec3d) {
            return true;
        }

        @Override
        public JsonObject serializeCustomData() {
            return new JsonObject();
        }

        @Override
        public EntitySubPredicate.a type() {
            return EntitySubPredicate.b.ANY;
        }
    };

    static EntitySubPredicate fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "type_specific");
            String s = ChatDeserializer.getAsString(jsonobject, "type", (String) null);

            if (s == null) {
                return EntitySubPredicate.ANY;
            } else {
                EntitySubPredicate.a entitysubpredicate_a = (EntitySubPredicate.a) EntitySubPredicate.b.TYPES.get(s);

                if (entitysubpredicate_a == null) {
                    throw new JsonSyntaxException("Unknown sub-predicate type: " + s);
                } else {
                    return entitysubpredicate_a.deserialize(jsonobject);
                }
            }
        } else {
            return EntitySubPredicate.ANY;
        }
    }

    boolean matches(Entity entity, WorldServer worldserver, @Nullable Vec3D vec3d);

    JsonObject serializeCustomData();

    default JsonElement serialize() {
        if (this.type() == EntitySubPredicate.b.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = this.serializeCustomData();
            String s = (String) EntitySubPredicate.b.TYPES.inverse().get(this.type());

            jsonobject.addProperty("type", s);
            return jsonobject;
        }
    }

    EntitySubPredicate.a type();

    static EntitySubPredicate variant(CatVariant catvariant) {
        return EntitySubPredicate.b.CAT.createPredicate(catvariant);
    }

    static EntitySubPredicate variant(FrogVariant frogvariant) {
        return EntitySubPredicate.b.FROG.createPredicate(frogvariant);
    }

    public static final class b {

        public static final EntitySubPredicate.a ANY = (jsonobject) -> {
            return EntitySubPredicate.ANY;
        };
        public static final EntitySubPredicate.a LIGHTNING = LighthingBoltPredicate::fromJson;
        public static final EntitySubPredicate.a FISHING_HOOK = CriterionConditionInOpenWater::fromJson;
        public static final EntitySubPredicate.a PLAYER = CriterionConditionPlayer::fromJson;
        public static final EntitySubPredicate.a SLIME = SlimePredicate::fromJson;
        public static final EntityVariantPredicate<CatVariant> CAT = EntityVariantPredicate.create(IRegistry.CAT_VARIANT, (entity) -> {
            Optional optional;

            if (entity instanceof EntityCat) {
                EntityCat entitycat = (EntityCat) entity;

                optional = Optional.of(entitycat.getCatVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<FrogVariant> FROG = EntityVariantPredicate.create(IRegistry.FROG_VARIANT, (entity) -> {
            Optional optional;

            if (entity instanceof Frog) {
                Frog frog = (Frog) entity;

                optional = Optional.of(frog.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final BiMap<String, EntitySubPredicate.a> TYPES = ImmutableBiMap.of("any", EntitySubPredicate.b.ANY, "lightning", EntitySubPredicate.b.LIGHTNING, "fishing_hook", EntitySubPredicate.b.FISHING_HOOK, "player", EntitySubPredicate.b.PLAYER, "slime", EntitySubPredicate.b.SLIME, "cat", EntitySubPredicate.b.CAT.type(), "frog", EntitySubPredicate.b.FROG.type());

        public b() {}
    }

    public interface a {

        EntitySubPredicate deserialize(JsonObject jsonobject);
    }
}
