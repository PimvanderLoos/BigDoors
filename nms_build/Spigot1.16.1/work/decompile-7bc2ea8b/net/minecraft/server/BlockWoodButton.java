package net.minecraft.server;

public class BlockWoodButton extends BlockButtonAbstract {

    protected BlockWoodButton(BlockBase.Info blockbase_info) {
        super(true, blockbase_info);
    }

    @Override
    protected SoundEffect a(boolean flag) {
        return flag ? SoundEffects.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEffects.BLOCK_WOODEN_BUTTON_CLICK_OFF;
    }
}
