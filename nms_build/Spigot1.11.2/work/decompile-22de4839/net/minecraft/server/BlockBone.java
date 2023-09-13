package net.minecraft.server;

public class BlockBone extends BlockRotatable {

    public BlockBone() {
        super(Material.STONE, MaterialMapColor.d);
        this.a(CreativeModeTab.b);
        this.c(2.0F);
        this.a(SoundEffectType.d);
    }
}
