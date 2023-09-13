package net.minecraft.world.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFireAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityLightning extends Entity {

    private static final int START_LIFE = 2;
    private static final double DAMAGE_RADIUS = 3.0D;
    private static final double DETECTION_RADIUS = 15.0D;
    private int life;
    public long seed;
    private int flashes;
    public boolean visualOnly;
    @Nullable
    private EntityPlayer cause;
    private final Set<Entity> hitEntities = Sets.newHashSet();
    private int blocksSetOnFire;

    public EntityLightning(EntityTypes<? extends EntityLightning> entitytypes, World world) {
        super(entitytypes, world);
        this.noCulling = true;
        this.life = 2;
        this.seed = this.random.nextLong();
        this.flashes = this.random.nextInt(3) + 1;
    }

    public void setEffect(boolean flag) {
        this.visualOnly = flag;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    @Nullable
    public EntityPlayer h() {
        return this.cause;
    }

    public void b(@Nullable EntityPlayer entityplayer) {
        this.cause = entityplayer;
    }

    private void l() {
        BlockPosition blockposition = this.n();
        IBlockData iblockdata = this.level.getType(blockposition);

        if (iblockdata.a(Blocks.LIGHTNING_ROD)) {
            ((LightningRodBlock) iblockdata.getBlock()).d(iblockdata, this.level, blockposition);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.life == 2) {
            if (this.level.isClientSide()) {
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
            } else {
                EnumDifficulty enumdifficulty = this.level.getDifficulty();

                if (enumdifficulty == EnumDifficulty.NORMAL || enumdifficulty == EnumDifficulty.HARD) {
                    this.a((int) 4);
                }

                this.l();
                b(this.level, this.n());
                this.a(GameEvent.LIGHTNING_STRIKE);
            }
        }

        --this.life;
        List list;
        Iterator iterator;

        if (this.life < 0) {
            if (this.flashes == 0) {
                if (this.level instanceof WorldServer) {
                    list = this.level.getEntities(this, new AxisAlignedBB(this.locX() - 15.0D, this.locY() - 15.0D, this.locZ() - 15.0D, this.locX() + 15.0D, this.locY() + 6.0D + 15.0D, this.locZ() + 15.0D), (entity) -> {
                        return entity.isAlive() && !this.hitEntities.contains(entity);
                    });
                    iterator = ((WorldServer) this.level).a((entityplayer) -> {
                        return entityplayer.e((Entity) this) < 256.0F;
                    }).iterator();

                    while (iterator.hasNext()) {
                        EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                        CriterionTriggers.LIGHTNING_STRIKE.a(entityplayer, this, list);
                    }
                }

                this.die();
            } else if (this.life < -this.random.nextInt(10)) {
                --this.flashes;
                this.life = 1;
                this.seed = this.random.nextLong();
                this.a((int) 0);
            }
        }

        if (this.life >= 0) {
            if (!(this.level instanceof WorldServer)) {
                this.level.c(2);
            } else if (!this.visualOnly) {
                list = this.level.getEntities(this, new AxisAlignedBB(this.locX() - 3.0D, this.locY() - 3.0D, this.locZ() - 3.0D, this.locX() + 3.0D, this.locY() + 6.0D + 3.0D, this.locZ() + 3.0D), Entity::isAlive);
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.onLightningStrike((WorldServer) this.level, this);
                }

                this.hitEntities.addAll(list);
                if (this.cause != null) {
                    CriterionTriggers.CHANNELED_LIGHTNING.a(this.cause, (Collection) list);
                }
            }
        }

    }

    private BlockPosition n() {
        Vec3D vec3d = this.getPositionVector();

        return new BlockPosition(vec3d.x, vec3d.y - 1.0E-6D, vec3d.z);
    }

    private void a(int i) {
        if (!this.visualOnly && !this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            BlockPosition blockposition = this.getChunkCoordinates();
            IBlockData iblockdata = BlockFireAbstract.a((IBlockAccess) this.level, blockposition);

            if (this.level.getType(blockposition).isAir() && iblockdata.canPlace(this.level, blockposition)) {
                this.level.setTypeUpdate(blockposition, iblockdata);
                ++this.blocksSetOnFire;
            }

            for (int j = 0; j < i; ++j) {
                BlockPosition blockposition1 = blockposition.c(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);

                iblockdata = BlockFireAbstract.a((IBlockAccess) this.level, blockposition1);
                if (this.level.getType(blockposition1).isAir() && iblockdata.canPlace(this.level, blockposition1)) {
                    this.level.setTypeUpdate(blockposition1, iblockdata);
                    ++this.blocksSetOnFire;
                }
            }

        }
    }

    private static void b(World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);
        BlockPosition blockposition1;
        IBlockData iblockdata1;

        if (iblockdata.a(Blocks.LIGHTNING_ROD)) {
            blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(LightningRodBlock.FACING)).opposite());
            iblockdata1 = world.getType(blockposition1);
        } else {
            blockposition1 = blockposition;
            iblockdata1 = iblockdata;
        }

        if (iblockdata1.getBlock() instanceof WeatheringCopper) {
            world.setTypeUpdate(blockposition1, WeatheringCopper.c(world.getType(blockposition1)));
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
            int i = world.random.nextInt(3) + 3;

            for (int j = 0; j < i; ++j) {
                int k = world.random.nextInt(8) + 1;

                a(world, blockposition1, blockposition_mutableblockposition, k);
            }

        }
    }

    private static void a(World world, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i) {
        blockposition_mutableblockposition.g(blockposition);

        for (int j = 0; j < i; ++j) {
            Optional<BlockPosition> optional = c(world, blockposition_mutableblockposition);

            if (!optional.isPresent()) {
                break;
            }

            blockposition_mutableblockposition.g((BaseBlockPosition) optional.get());
        }

    }

    private static Optional<BlockPosition> c(World world, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.a(world.random, 10, blockposition, 1).iterator();

        BlockPosition blockposition1;
        IBlockData iblockdata;

        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            blockposition1 = (BlockPosition) iterator.next();
            iblockdata = world.getType(blockposition1);
        } while (!(iblockdata.getBlock() instanceof WeatheringCopper));

        WeatheringCopper.b(iblockdata).ifPresent((iblockdata1) -> {
            world.setTypeUpdate(blockposition1, iblockdata1);
        });
        world.triggerEffect(3002, blockposition1, -1);
        return Optional.of(blockposition1);
    }

    @Override
    public boolean a(double d0) {
        double d1 = 64.0D * cl();

        return d0 < d1 * d1;
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {}

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {}

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntity(this);
    }

    public int i() {
        return this.blocksSetOnFire;
    }

    public Stream<Entity> j() {
        return this.hitEntities.stream().filter(Entity::isAlive);
    }
}
