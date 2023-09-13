package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseChestedAbstract;
import net.minecraft.world.entity.animal.horse.EntityHorseSkeleton;
import net.minecraft.world.entity.animal.horse.EntityHorseZombie;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
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
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import org.slf4j.Logger;

public class AttributeDefaults {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<EntityTypes<? extends EntityLiving>, AttributeProvider> SUPPLIERS = ImmutableMap.builder().put(EntityTypes.ALLAY, Allay.createAttributes().build()).put(EntityTypes.ARMOR_STAND, EntityLiving.createLivingAttributes().build()).put(EntityTypes.AXOLOTL, Axolotl.createAttributes().build()).put(EntityTypes.BAT, EntityBat.createAttributes().build()).put(EntityTypes.BEE, EntityBee.createAttributes().build()).put(EntityTypes.BLAZE, EntityBlaze.createAttributes().build()).put(EntityTypes.CAT, EntityCat.createAttributes().build()).put(EntityTypes.CAMEL, Camel.createAttributes().build()).put(EntityTypes.CAVE_SPIDER, EntityCaveSpider.createCaveSpider().build()).put(EntityTypes.CHICKEN, EntityChicken.createAttributes().build()).put(EntityTypes.COD, EntityFish.createAttributes().build()).put(EntityTypes.COW, EntityCow.createAttributes().build()).put(EntityTypes.CREEPER, EntityCreeper.createAttributes().build()).put(EntityTypes.DOLPHIN, EntityDolphin.createAttributes().build()).put(EntityTypes.DONKEY, EntityHorseChestedAbstract.createBaseChestedHorseAttributes().build()).put(EntityTypes.DROWNED, EntityZombie.createAttributes().build()).put(EntityTypes.ELDER_GUARDIAN, EntityGuardianElder.createAttributes().build()).put(EntityTypes.ENDERMAN, EntityEnderman.createAttributes().build()).put(EntityTypes.ENDERMITE, EntityEndermite.createAttributes().build()).put(EntityTypes.ENDER_DRAGON, EntityEnderDragon.createAttributes().build()).put(EntityTypes.EVOKER, EntityEvoker.createAttributes().build()).put(EntityTypes.FOX, EntityFox.createAttributes().build()).put(EntityTypes.FROG, Frog.createAttributes().build()).put(EntityTypes.GHAST, EntityGhast.createAttributes().build()).put(EntityTypes.GIANT, EntityGiantZombie.createAttributes().build()).put(EntityTypes.GLOW_SQUID, GlowSquid.createAttributes().build()).put(EntityTypes.GOAT, Goat.createAttributes().build()).put(EntityTypes.GUARDIAN, EntityGuardian.createAttributes().build()).put(EntityTypes.HOGLIN, EntityHoglin.createAttributes().build()).put(EntityTypes.HORSE, EntityHorseAbstract.createBaseHorseAttributes().build()).put(EntityTypes.HUSK, EntityZombie.createAttributes().build()).put(EntityTypes.ILLUSIONER, EntityIllagerIllusioner.createAttributes().build()).put(EntityTypes.IRON_GOLEM, EntityIronGolem.createAttributes().build()).put(EntityTypes.LLAMA, EntityLlama.createAttributes().build()).put(EntityTypes.MAGMA_CUBE, EntityMagmaCube.createAttributes().build()).put(EntityTypes.MOOSHROOM, EntityCow.createAttributes().build()).put(EntityTypes.MULE, EntityHorseChestedAbstract.createBaseChestedHorseAttributes().build()).put(EntityTypes.OCELOT, EntityOcelot.createAttributes().build()).put(EntityTypes.PANDA, EntityPanda.createAttributes().build()).put(EntityTypes.PARROT, EntityParrot.createAttributes().build()).put(EntityTypes.PHANTOM, EntityMonster.createMonsterAttributes().build()).put(EntityTypes.PIG, EntityPig.createAttributes().build()).put(EntityTypes.PIGLIN, EntityPiglin.createAttributes().build()).put(EntityTypes.PIGLIN_BRUTE, EntityPiglinBrute.createAttributes().build()).put(EntityTypes.PILLAGER, EntityPillager.createAttributes().build()).put(EntityTypes.PLAYER, EntityHuman.createAttributes().build()).put(EntityTypes.POLAR_BEAR, EntityPolarBear.createAttributes().build()).put(EntityTypes.PUFFERFISH, EntityFish.createAttributes().build()).put(EntityTypes.RABBIT, EntityRabbit.createAttributes().build()).put(EntityTypes.RAVAGER, EntityRavager.createAttributes().build()).put(EntityTypes.SALMON, EntityFish.createAttributes().build()).put(EntityTypes.SHEEP, EntitySheep.createAttributes().build()).put(EntityTypes.SHULKER, EntityShulker.createAttributes().build()).put(EntityTypes.SILVERFISH, EntitySilverfish.createAttributes().build()).put(EntityTypes.SKELETON, EntitySkeletonAbstract.createAttributes().build()).put(EntityTypes.SKELETON_HORSE, EntityHorseSkeleton.createAttributes().build()).put(EntityTypes.SLIME, EntityMonster.createMonsterAttributes().build()).put(EntityTypes.SNIFFER, Sniffer.createAttributes().build()).put(EntityTypes.SNOW_GOLEM, EntitySnowman.createAttributes().build()).put(EntityTypes.SPIDER, EntitySpider.createAttributes().build()).put(EntityTypes.SQUID, EntitySquid.createAttributes().build()).put(EntityTypes.STRAY, EntitySkeletonAbstract.createAttributes().build()).put(EntityTypes.STRIDER, EntityStrider.createAttributes().build()).put(EntityTypes.TADPOLE, Tadpole.createAttributes().build()).put(EntityTypes.TRADER_LLAMA, EntityLlama.createAttributes().build()).put(EntityTypes.TROPICAL_FISH, EntityFish.createAttributes().build()).put(EntityTypes.TURTLE, EntityTurtle.createAttributes().build()).put(EntityTypes.VEX, EntityVex.createAttributes().build()).put(EntityTypes.VILLAGER, EntityVillager.createAttributes().build()).put(EntityTypes.VINDICATOR, EntityVindicator.createAttributes().build()).put(EntityTypes.WARDEN, Warden.createAttributes().build()).put(EntityTypes.WANDERING_TRADER, EntityInsentient.createMobAttributes().build()).put(EntityTypes.WITCH, EntityWitch.createAttributes().build()).put(EntityTypes.WITHER, EntityWither.createAttributes().build()).put(EntityTypes.WITHER_SKELETON, EntitySkeletonAbstract.createAttributes().build()).put(EntityTypes.WOLF, EntityWolf.createAttributes().build()).put(EntityTypes.ZOGLIN, EntityZoglin.createAttributes().build()).put(EntityTypes.ZOMBIE, EntityZombie.createAttributes().build()).put(EntityTypes.ZOMBIE_HORSE, EntityHorseZombie.createAttributes().build()).put(EntityTypes.ZOMBIE_VILLAGER, EntityZombie.createAttributes().build()).put(EntityTypes.ZOMBIFIED_PIGLIN, EntityPigZombie.createAttributes().build()).build();

    public AttributeDefaults() {}

    public static AttributeProvider getSupplier(EntityTypes<? extends EntityLiving> entitytypes) {
        return (AttributeProvider) AttributeDefaults.SUPPLIERS.get(entitytypes);
    }

    public static boolean hasSupplier(EntityTypes<?> entitytypes) {
        return AttributeDefaults.SUPPLIERS.containsKey(entitytypes);
    }

    public static void validate() {
        Stream stream = BuiltInRegistries.ENTITY_TYPE.stream().filter((entitytypes) -> {
            return entitytypes.getCategory() != EnumCreatureType.MISC;
        }).filter((entitytypes) -> {
            return !hasSupplier(entitytypes);
        });
        RegistryBlocks registryblocks = BuiltInRegistries.ENTITY_TYPE;

        Objects.requireNonNull(registryblocks);
        stream.map(registryblocks::getKey).forEach((minecraftkey) -> {
            SystemUtils.logAndPauseIfInIde("Entity " + minecraftkey + " has no attributes");
        });
    }
}
