package net.minecraft.world.entity.decoration;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityPainting;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;

public class EntityPainting extends EntityHanging {

    public Paintings motive;

    public EntityPainting(EntityTypes<? extends EntityPainting> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityPainting(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(EntityTypes.PAINTING, world, blockposition);
        List<Paintings> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = IRegistry.MOTIVE.iterator();

        Paintings paintings;

        while (iterator.hasNext()) {
            paintings = (Paintings) iterator.next();
            this.motive = paintings;
            this.setDirection(enumdirection);
            if (this.survives()) {
                list.add(paintings);
                int j = paintings.getWidth() * paintings.getHeight();

                if (j > i) {
                    i = j;
                }
            }
        }

        if (!list.isEmpty()) {
            iterator = list.iterator();

            while (iterator.hasNext()) {
                paintings = (Paintings) iterator.next();
                if (paintings.getWidth() * paintings.getHeight() < i) {
                    iterator.remove();
                }
            }

            this.motive = (Paintings) list.get(this.random.nextInt(list.size()));
        }

        this.setDirection(enumdirection);
    }

    public EntityPainting(World world, BlockPosition blockposition, EnumDirection enumdirection, Paintings paintings) {
        this(world, blockposition, enumdirection);
        this.motive = paintings;
        this.setDirection(enumdirection);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Motive", IRegistry.MOTIVE.getKey(this.motive).toString());
        nbttagcompound.setByte("Facing", (byte) this.direction.get2DRotationValue());
        super.saveData(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        this.motive = (Paintings) IRegistry.MOTIVE.get(MinecraftKey.a(nbttagcompound.getString("Motive")));
        this.direction = EnumDirection.fromType2(nbttagcompound.getByte("Facing"));
        super.loadData(nbttagcompound);
        this.setDirection(this.direction);
    }

    @Override
    public int getHangingWidth() {
        return this.motive == null ? 1 : this.motive.getWidth();
    }

    @Override
    public int getHangingHeight() {
        return this.motive == null ? 1 : this.motive.getHeight();
    }

    @Override
    public void a(@Nullable Entity entity) {
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEffects.PAINTING_BREAK, 1.0F, 1.0F);
            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                if (entityhuman.getAbilities().instabuild) {
                    return;
                }
            }

            this.a((IMaterial) Items.PAINTING);
        }
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEffects.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.setPosition(d0, d1, d2);
    }

    @Override
    public void a(double d0, double d1, double d2, float f, float f1, int i, boolean flag) {
        BlockPosition blockposition = this.pos.b(d0 - this.locX(), d1 - this.locY(), d2 - this.locZ());

        this.setPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
    }

    @Override
    public Packet<?> getPacket() {
        return new PacketPlayOutSpawnEntityPainting(this);
    }

    @Override
    public ItemStack df() {
        return new ItemStack(Items.PAINTING);
    }
}
