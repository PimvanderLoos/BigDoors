package net.minecraft.server.level;

import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class DemoPlayerInteractManager extends PlayerInteractManager {

    private boolean c;
    private boolean d;
    private int e;
    private int f;

    public DemoPlayerInteractManager(WorldServer worldserver) {
        super(worldserver);
    }

    @Override
    public void a() {
        super.a();
        ++this.f;
        long i = this.world.getTime();
        long j = i / 24000L + 1L;

        if (!this.c && this.f > 20) {
            this.c = true;
            this.player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 0.0F));
        }

        this.d = i > 120500L;
        if (this.d) {
            ++this.e;
        }

        if (i % 24000L == 500L) {
            if (j <= 6L) {
                if (j == 6L) {
                    this.player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 104.0F));
                } else {
                    this.player.sendMessage(new ChatMessage("demo.day." + j), SystemUtils.b);
                }
            }
        } else if (j == 1L) {
            if (i == 100L) {
                this.player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 101.0F));
            } else if (i == 175L) {
                this.player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 102.0F));
            } else if (i == 250L) {
                this.player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.f, 103.0F));
            }
        } else if (j == 5L && i % 24000L == 22000L) {
            this.player.sendMessage(new ChatMessage("demo.day.warning"), SystemUtils.b);
        }

    }

    private void f() {
        if (this.e > 100) {
            this.player.sendMessage(new ChatMessage("demo.reminder"), SystemUtils.b);
            this.e = 0;
        }

    }

    @Override
    public void a(BlockPosition blockposition, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, EnumDirection enumdirection, int i) {
        if (this.d) {
            this.f();
        } else {
            super.a(blockposition, packetplayinblockdig_enumplayerdigtype, enumdirection, i);
        }
    }

    @Override
    public EnumInteractionResult a(EntityPlayer entityplayer, World world, ItemStack itemstack, EnumHand enumhand) {
        if (this.d) {
            this.f();
            return EnumInteractionResult.PASS;
        } else {
            return super.a(entityplayer, world, itemstack, enumhand);
        }
    }

    @Override
    public EnumInteractionResult a(EntityPlayer entityplayer, World world, ItemStack itemstack, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (this.d) {
            this.f();
            return EnumInteractionResult.PASS;
        } else {
            return super.a(entityplayer, world, itemstack, enumhand, movingobjectpositionblock);
        }
    }
}
