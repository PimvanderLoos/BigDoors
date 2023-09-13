package net.minecraft.world.level.border;

public interface IWorldBorderListener {

    void onBorderSizeSet(WorldBorder worldborder, double d0);

    void onBorderSizeLerping(WorldBorder worldborder, double d0, double d1, long i);

    void onBorderCenterSet(WorldBorder worldborder, double d0, double d1);

    void onBorderSetWarningTime(WorldBorder worldborder, int i);

    void onBorderSetWarningBlocks(WorldBorder worldborder, int i);

    void onBorderSetDamagePerBlock(WorldBorder worldborder, double d0);

    void onBorderSetDamageSafeZOne(WorldBorder worldborder, double d0);

    public static class a implements IWorldBorderListener {

        private final WorldBorder worldBorder;

        public a(WorldBorder worldborder) {
            this.worldBorder = worldborder;
        }

        @Override
        public void onBorderSizeSet(WorldBorder worldborder, double d0) {
            this.worldBorder.setSize(d0);
        }

        @Override
        public void onBorderSizeLerping(WorldBorder worldborder, double d0, double d1, long i) {
            this.worldBorder.lerpSizeBetween(d0, d1, i);
        }

        @Override
        public void onBorderCenterSet(WorldBorder worldborder, double d0, double d1) {
            this.worldBorder.setCenter(d0, d1);
        }

        @Override
        public void onBorderSetWarningTime(WorldBorder worldborder, int i) {
            this.worldBorder.setWarningTime(i);
        }

        @Override
        public void onBorderSetWarningBlocks(WorldBorder worldborder, int i) {
            this.worldBorder.setWarningBlocks(i);
        }

        @Override
        public void onBorderSetDamagePerBlock(WorldBorder worldborder, double d0) {
            this.worldBorder.setDamagePerBlock(d0);
        }

        @Override
        public void onBorderSetDamageSafeZOne(WorldBorder worldborder, double d0) {
            this.worldBorder.setDamageSafeZone(d0);
        }
    }
}
