package net.minecraft.world.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.entity.animal.EntityFish;
import net.minecraft.world.entity.animal.EntityMushroomCow;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityPolarBear;
import net.minecraft.world.entity.animal.EntityRabbit;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.monster.EntityDrowned;
import net.minecraft.world.entity.monster.EntityEndermite;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityGuardian;
import net.minecraft.world.entity.monster.EntityMagmaCube;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityMonsterPatrolling;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.monster.EntitySilverfish;
import net.minecraft.world.entity.monster.EntitySkeletonStray;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.entity.monster.EntityStrider;
import net.minecraft.world.entity.monster.EntityZombieHusk;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.levelgen.HeightMap;

public class EntityPositionTypes {

    private static final Map<EntityTypes<?>, EntityPositionTypes.a> a = Maps.newHashMap();

    private static <T extends EntityInsentient> void a(EntityTypes<T> entitytypes, EntityPositionTypes.Surface entitypositiontypes_surface, HeightMap.Type heightmap_type, EntityPositionTypes.b<T> entitypositiontypes_b) {
        EntityPositionTypes.a entitypositiontypes_a = (EntityPositionTypes.a) EntityPositionTypes.a.put(entitytypes, new EntityPositionTypes.a(heightmap_type, entitypositiontypes_surface, entitypositiontypes_b));

        if (entitypositiontypes_a != null) {
            throw new IllegalStateException("Duplicate registration for type " + IRegistry.ENTITY_TYPE.getKey(entitytypes));
        }
    }

    public static EntityPositionTypes.Surface a(EntityTypes<?> entitytypes) {
        EntityPositionTypes.a entitypositiontypes_a = (EntityPositionTypes.a) EntityPositionTypes.a.get(entitytypes);

        return entitypositiontypes_a == null ? EntityPositionTypes.Surface.NO_RESTRICTIONS : entitypositiontypes_a.b;
    }

    public static HeightMap.Type b(@Nullable EntityTypes<?> entitytypes) {
        EntityPositionTypes.a entitypositiontypes_a = (EntityPositionTypes.a) EntityPositionTypes.a.get(entitytypes);

        return entitypositiontypes_a == null ? HeightMap.Type.MOTION_BLOCKING_NO_LEAVES : entitypositiontypes_a.a;
    }

    public static <T extends Entity> boolean a(EntityTypes<T> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        EntityPositionTypes.a entitypositiontypes_a = (EntityPositionTypes.a) EntityPositionTypes.a.get(entitytypes);

        return entitypositiontypes_a == null || entitypositiontypes_a.c.test(entitytypes, worldaccess, enummobspawn, blockposition, random);
    }

    static {
        a(EntityTypes.COD, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityFish::b);
        a(EntityTypes.DOLPHIN, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityDolphin::b);
        a(EntityTypes.DROWNED, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityDrowned::a);
        a(EntityTypes.GUARDIAN, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityGuardian::b);
        a(EntityTypes.PUFFERFISH, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityFish::b);
        a(EntityTypes.SALMON, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityFish::b);
        a(EntityTypes.SQUID, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySquid::b);
        a(EntityTypes.TROPICAL_FISH, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityFish::b);
        a(EntityTypes.BAT, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityBat::b);
        a(EntityTypes.BLAZE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::c);
        a(EntityTypes.CAVE_SPIDER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.CHICKEN, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.COW, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.CREEPER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.DONKEY, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.ENDERMAN, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.ENDERMITE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityEndermite::b);
        a(EntityTypes.ENDER_DRAGON, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
        a(EntityTypes.GHAST, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityGhast::b);
        a(EntityTypes.GIANT, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.HORSE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.HUSK, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityZombieHusk::a);
        a(EntityTypes.IRON_GOLEM, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
        a(EntityTypes.LLAMA, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.MAGMA_CUBE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMagmaCube::b);
        a(EntityTypes.MOOSHROOM, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMushroomCow::c);
        a(EntityTypes.MULE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.OCELOT, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING, EntityOcelot::c);
        a(EntityTypes.PARROT, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING, EntityParrot::c);
        a(EntityTypes.PIG, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.HOGLIN, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityHoglin::c);
        a(EntityTypes.PIGLIN, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityPiglin::b);
        a(EntityTypes.PILLAGER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonsterPatrolling::b);
        a(EntityTypes.POLAR_BEAR, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityPolarBear::c);
        a(EntityTypes.RABBIT, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityRabbit::c);
        a(EntityTypes.SHEEP, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.SILVERFISH, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySilverfish::b);
        a(EntityTypes.SKELETON, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.SKELETON_HORSE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.SLIME, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySlime::c);
        a(EntityTypes.SNOW_GOLEM, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
        a(EntityTypes.SPIDER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.STRAY, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntitySkeletonStray::a);
        a(EntityTypes.STRIDER, EntityPositionTypes.Surface.IN_LAVA, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityStrider::c);
        a(EntityTypes.TURTLE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityTurtle::c);
        a(EntityTypes.VILLAGER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
        a(EntityTypes.WITCH, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.WITHER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.WITHER_SKELETON, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.WOLF, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.ZOMBIE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.ZOMBIE_HORSE, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.ZOMBIFIED_PIGLIN, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityPigZombie::b);
        a(EntityTypes.ZOMBIE_VILLAGER, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.CAT, EntityPositionTypes.Surface.ON_GROUND, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.ELDER_GUARDIAN, EntityPositionTypes.Surface.IN_WATER, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityGuardian::b);
        a(EntityTypes.EVOKER, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.FOX, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.ILLUSIONER, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.PANDA, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.PHANTOM, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
        a(EntityTypes.RAVAGER, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.SHULKER, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
        a(EntityTypes.TRADER_LLAMA, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityAnimal::b);
        a(EntityTypes.VEX, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.VINDICATOR, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityMonster::b);
        a(EntityTypes.WANDERING_TRADER, EntityPositionTypes.Surface.NO_RESTRICTIONS, HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, EntityInsentient::a);
    }

    public static enum Surface {

        ON_GROUND, IN_WATER, NO_RESTRICTIONS, IN_LAVA;

        private Surface() {}
    }

    static class a {

        private final HeightMap.Type a;
        private final EntityPositionTypes.Surface b;
        private final EntityPositionTypes.b<?> c;

        public a(HeightMap.Type heightmap_type, EntityPositionTypes.Surface entitypositiontypes_surface, EntityPositionTypes.b<?> entitypositiontypes_b) {
            this.a = heightmap_type;
            this.b = entitypositiontypes_surface;
            this.c = entitypositiontypes_b;
        }
    }

    @FunctionalInterface
    public interface b<T extends Entity> {

        boolean test(EntityTypes<T> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random);
    }
}
