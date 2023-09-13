package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.EntityBee;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.entity.animal.EntityFish;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityPanda;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntityPolarBear;
import net.minecraft.world.entity.animal.EntityRabbit;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.animal.EntitySnowman;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseChestedAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseSkeleton;
import net.minecraft.world.entity.animal.horse.EntityHorseZombie;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.entity.monster.EntityBlaze;
import net.minecraft.world.entity.monster.EntityCaveSpider;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityEnderman;
import net.minecraft.world.entity.monster.EntityEndermite;
import net.minecraft.world.entity.monster.EntityEvoker;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityGiantZombie;
import net.minecraft.world.entity.monster.EntityGuardian;
import net.minecraft.world.entity.monster.EntityGuardianElder;
import net.minecraft.world.entity.monster.EntityIllagerIllusioner;
import net.minecraft.world.entity.monster.EntityMagmaCube;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.monster.EntityPillager;
import net.minecraft.world.entity.monster.EntityRavager;
import net.minecraft.world.entity.monster.EntityShulker;
import net.minecraft.world.entity.monster.EntitySilverfish;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.monster.EntityStrider;
import net.minecraft.world.entity.monster.EntityVex;
import net.minecraft.world.entity.monster.EntityVindicator;
import net.minecraft.world.entity.monster.EntityWitch;
import net.minecraft.world.entity.monster.EntityZoglin;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.monster.hoglin.EntityHoglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglin;
import net.minecraft.world.entity.monster.piglin.EntityPiglinBrute;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeDefaults {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<EntityTypes<? extends EntityLiving>, AttributeProvider> SUPPLIERS = ImmutableMap.builder().put(EntityTypes.ARMOR_STAND, EntityLiving.dq().a()).put(EntityTypes.AXOLOTL, Axolotl.fE().a()).put(EntityTypes.BAT, EntityBat.n().a()).put(EntityTypes.BEE, EntityBee.fJ().a()).put(EntityTypes.BLAZE, EntityBlaze.n().a()).put(EntityTypes.CAT, EntityCat.fK().a()).put(EntityTypes.CAVE_SPIDER, EntityCaveSpider.n().a()).put(EntityTypes.CHICKEN, EntityChicken.p().a()).put(EntityTypes.COD, EntityFish.n().a()).put(EntityTypes.COW, EntityCow.p().a()).put(EntityTypes.CREEPER, EntityCreeper.n().a()).put(EntityTypes.DOLPHIN, EntityDolphin.fw().a()).put(EntityTypes.DONKEY, EntityHorseChestedAbstract.t().a()).put(EntityTypes.DROWNED, EntityZombie.fC().a()).put(EntityTypes.ELDER_GUARDIAN, EntityGuardianElder.n().a()).put(EntityTypes.ENDERMAN, EntityEnderman.n().a()).put(EntityTypes.ENDERMITE, EntityEndermite.n().a()).put(EntityTypes.ENDER_DRAGON, EntityEnderDragon.n().a()).put(EntityTypes.EVOKER, EntityEvoker.p().a()).put(EntityTypes.FOX, EntityFox.p().a()).put(EntityTypes.GHAST, EntityGhast.t().a()).put(EntityTypes.GIANT, EntityGiantZombie.n().a()).put(EntityTypes.GLOW_SQUID, GlowSquid.fw().a()).put(EntityTypes.GOAT, Goat.p().a()).put(EntityTypes.GUARDIAN, EntityGuardian.fw().a()).put(EntityTypes.HOGLIN, EntityHoglin.p().a()).put(EntityTypes.HORSE, EntityHorseAbstract.fS().a()).put(EntityTypes.HUSK, EntityZombie.fC().a()).put(EntityTypes.ILLUSIONER, EntityIllagerIllusioner.p().a()).put(EntityTypes.IRON_GOLEM, EntityIronGolem.n().a()).put(EntityTypes.LLAMA, EntityLlama.gg().a()).put(EntityTypes.MAGMA_CUBE, EntityMagmaCube.n().a()).put(EntityTypes.MOOSHROOM, EntityCow.p().a()).put(EntityTypes.MULE, EntityHorseChestedAbstract.t().a()).put(EntityTypes.OCELOT, EntityOcelot.p().a()).put(EntityTypes.PANDA, EntityPanda.fI().a()).put(EntityTypes.PARROT, EntityParrot.fE().a()).put(EntityTypes.PHANTOM, EntityMonster.fB().a()).put(EntityTypes.PIG, EntityPig.p().a()).put(EntityTypes.PIGLIN, EntityPiglin.fC().a()).put(EntityTypes.PIGLIN_BRUTE, EntityPiglinBrute.fC().a()).put(EntityTypes.PILLAGER, EntityPillager.p().a()).put(EntityTypes.PLAYER, EntityHuman.eY().a()).put(EntityTypes.POLAR_BEAR, EntityPolarBear.p().a()).put(EntityTypes.PUFFERFISH, EntityFish.n().a()).put(EntityTypes.RABBIT, EntityRabbit.t().a()).put(EntityTypes.RAVAGER, EntityRavager.n().a()).put(EntityTypes.SALMON, EntityFish.n().a()).put(EntityTypes.SHEEP, EntitySheep.p().a()).put(EntityTypes.SHULKER, EntityShulker.n().a()).put(EntityTypes.SILVERFISH, EntitySilverfish.n().a()).put(EntityTypes.SKELETON, EntitySkeletonAbstract.n().a()).put(EntityTypes.SKELETON_HORSE, EntityHorseSkeleton.t().a()).put(EntityTypes.SLIME, EntityMonster.fB().a()).put(EntityTypes.SNOW_GOLEM, EntitySnowman.n().a()).put(EntityTypes.SPIDER, EntitySpider.p().a()).put(EntityTypes.SQUID, EntitySquid.fw().a()).put(EntityTypes.STRAY, EntitySkeletonAbstract.n().a()).put(EntityTypes.STRIDER, EntityStrider.fw().a()).put(EntityTypes.TRADER_LLAMA, EntityLlama.gg().a()).put(EntityTypes.TROPICAL_FISH, EntityFish.n().a()).put(EntityTypes.TURTLE, EntityTurtle.fw().a()).put(EntityTypes.VEX, EntityVex.n().a()).put(EntityTypes.VILLAGER, EntityVillager.fI().a()).put(EntityTypes.VINDICATOR, EntityVindicator.p().a()).put(EntityTypes.WANDERING_TRADER, EntityInsentient.w().a()).put(EntityTypes.WITCH, EntityWitch.p().a()).put(EntityTypes.WITHER, EntityWither.p().a()).put(EntityTypes.WITHER_SKELETON, EntitySkeletonAbstract.n().a()).put(EntityTypes.WOLF, EntityWolf.fE().a()).put(EntityTypes.ZOGLIN, EntityZoglin.n().a()).put(EntityTypes.ZOMBIE, EntityZombie.fC().a()).put(EntityTypes.ZOMBIE_HORSE, EntityHorseZombie.t().a()).put(EntityTypes.ZOMBIE_VILLAGER, EntityZombie.fC().a()).put(EntityTypes.ZOMBIFIED_PIGLIN, EntityPigZombie.fG().a()).build();

    public AttributeDefaults() {}

    public static AttributeProvider a(EntityTypes<? extends EntityLiving> entitytypes) {
        return (AttributeProvider) AttributeDefaults.SUPPLIERS.get(entitytypes);
    }

    public static boolean b(EntityTypes<?> entitytypes) {
        return AttributeDefaults.SUPPLIERS.containsKey(entitytypes);
    }

    public static void a() {
        Stream stream = IRegistry.ENTITY_TYPE.g().filter((entitytypes) -> {
            return entitytypes.f() != EnumCreatureType.MISC;
        }).filter((entitytypes) -> {
            return !b(entitytypes);
        });
        RegistryBlocks registryblocks = IRegistry.ENTITY_TYPE;

        Objects.requireNonNull(registryblocks);
        stream.map(registryblocks::getKey).forEach((minecraftkey) -> {
            SystemUtils.a("Entity " + minecraftkey + " has no attributes");
        });
    }
}
