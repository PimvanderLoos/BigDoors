package net.minecraft.advancements.critereon;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.animal.EntityMushroomCow;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityRabbit;
import net.minecraft.world.entity.animal.EntityTropicalFish;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.horse.EntityHorse;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.animal.horse.HorseColor;
import net.minecraft.world.entity.decoration.EntityPainting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.vehicle.EntityBoat;
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
        public static final EntityVariantPredicate<CatVariant> CAT = EntityVariantPredicate.create(BuiltInRegistries.CAT_VARIANT, (entity) -> {
            Optional optional;

            if (entity instanceof EntityCat) {
                EntityCat entitycat = (EntityCat) entity;

                optional = Optional.of(entitycat.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<FrogVariant> FROG = EntityVariantPredicate.create(BuiltInRegistries.FROG_VARIANT, (entity) -> {
            Optional optional;

            if (entity instanceof Frog) {
                Frog frog = (Frog) entity;

                optional = Optional.of(frog.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<Axolotl.Variant> AXOLOTL = EntityVariantPredicate.create(Axolotl.Variant.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof Axolotl) {
                Axolotl axolotl = (Axolotl) entity;

                optional = Optional.of(axolotl.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityBoat.EnumBoatType> BOAT = EntityVariantPredicate.create((Codec) EntityBoat.EnumBoatType.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityBoat) {
                EntityBoat entityboat = (EntityBoat) entity;

                optional = Optional.of(entityboat.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityFox.Type> FOX = EntityVariantPredicate.create((Codec) EntityFox.Type.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityFox) {
                EntityFox entityfox = (EntityFox) entity;

                optional = Optional.of(entityfox.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityMushroomCow.Type> MOOSHROOM = EntityVariantPredicate.create((Codec) EntityMushroomCow.Type.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityMushroomCow) {
                EntityMushroomCow entitymushroomcow = (EntityMushroomCow) entity;

                optional = Optional.of(entitymushroomcow.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<Holder<PaintingVariant>> PAINTING = EntityVariantPredicate.create(BuiltInRegistries.PAINTING_VARIANT.holderByNameCodec(), (entity) -> {
            Optional optional;

            if (entity instanceof EntityPainting) {
                EntityPainting entitypainting = (EntityPainting) entity;

                optional = Optional.of(entitypainting.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityRabbit.Variant> RABBIT = EntityVariantPredicate.create(EntityRabbit.Variant.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityRabbit) {
                EntityRabbit entityrabbit = (EntityRabbit) entity;

                optional = Optional.of(entityrabbit.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<HorseColor> HORSE = EntityVariantPredicate.create(HorseColor.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityHorse) {
                EntityHorse entityhorse = (EntityHorse) entity;

                optional = Optional.of(entityhorse.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityLlama.Variant> LLAMA = EntityVariantPredicate.create(EntityLlama.Variant.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityLlama) {
                EntityLlama entityllama = (EntityLlama) entity;

                optional = Optional.of(entityllama.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<VillagerType> VILLAGER = EntityVariantPredicate.create(BuiltInRegistries.VILLAGER_TYPE.byNameCodec(), (entity) -> {
            Optional optional;

            if (entity instanceof VillagerDataHolder) {
                VillagerDataHolder villagerdataholder = (VillagerDataHolder) entity;

                optional = Optional.of(villagerdataholder.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityParrot.Variant> PARROT = EntityVariantPredicate.create(EntityParrot.Variant.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityParrot) {
                EntityParrot entityparrot = (EntityParrot) entity;

                optional = Optional.of(entityparrot.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final EntityVariantPredicate<EntityTropicalFish.Variant> TROPICAL_FISH = EntityVariantPredicate.create(EntityTropicalFish.Variant.CODEC, (entity) -> {
            Optional optional;

            if (entity instanceof EntityTropicalFish) {
                EntityTropicalFish entitytropicalfish = (EntityTropicalFish) entity;

                optional = Optional.of(entitytropicalfish.getVariant());
            } else {
                optional = Optional.empty();
            }

            return optional;
        });
        public static final BiMap<String, EntitySubPredicate.a> TYPES = ImmutableBiMap.builder().put("any", EntitySubPredicate.b.ANY).put("lightning", EntitySubPredicate.b.LIGHTNING).put("fishing_hook", EntitySubPredicate.b.FISHING_HOOK).put("player", EntitySubPredicate.b.PLAYER).put("slime", EntitySubPredicate.b.SLIME).put("cat", EntitySubPredicate.b.CAT.type()).put("frog", EntitySubPredicate.b.FROG.type()).put("axolotl", EntitySubPredicate.b.AXOLOTL.type()).put("boat", EntitySubPredicate.b.BOAT.type()).put("fox", EntitySubPredicate.b.FOX.type()).put("mooshroom", EntitySubPredicate.b.MOOSHROOM.type()).put("painting", EntitySubPredicate.b.PAINTING.type()).put("rabbit", EntitySubPredicate.b.RABBIT.type()).put("horse", EntitySubPredicate.b.HORSE.type()).put("llama", EntitySubPredicate.b.LLAMA.type()).put("villager", EntitySubPredicate.b.VILLAGER.type()).put("parrot", EntitySubPredicate.b.PARROT.type()).put("tropical_fish", EntitySubPredicate.b.TROPICAL_FISH.type()).buildOrThrow();

        public b() {}
    }

    public interface a {

        EntitySubPredicate deserialize(JsonObject jsonobject);
    }
}
