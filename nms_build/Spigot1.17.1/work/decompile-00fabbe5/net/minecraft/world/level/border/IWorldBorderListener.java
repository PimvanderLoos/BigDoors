package net.minecraft.world.level.border;

public interface IWorldBorderListener {

    void a(WorldBorder worldborder, double d0);

    void a(WorldBorder worldborder, double d0, double d1, long i);

    void a(WorldBorder worldborder, double d0, double d1);

    void a(WorldBorder worldborder, int i);

    void b(WorldBorder worldborder, int i);

    void b(WorldBorder worldborder, double d0);

    void c(WorldBorder worldborder, double d0);

    public static class a implements IWorldBorderListener {

        private final WorldBorder worldBorder;

        public a(WorldBorder worldborder) {
            this.worldBorder = worldborder;
        }

        @Override
        public void a(WorldBorder worldborder, double d0) {
            this.worldBorder.setSize(d0);
        }

        @Override
        public void a(WorldBorder worldborder, double d0, double d1, long i) {
            this.worldBorder.transitionSizeBetween(d0, d1, i);
        }

        @Override
        public void a(WorldBorder worldborder, double d0, double d1) {
            this.worldBorder.setCenter(d0, d1);
        }

        @Override
        public void a(WorldBorder worldborder, int i) {
            this.worldBorder.setWarningTime(i);
        }

        @Override
        public void b(WorldBorder worldborder, int i) {
            this.worldBorder.setWarningDistance(i);
        }

        @Override
        public void b(WorldBorder worldborder, double d0) {
            this.worldBorder.setDamageAmount(d0);
        }

        @Override
        public void c(WorldBorder worldborder, double d0) {
            this.worldBorder.setDamageBuffer(d0);
        }
    }
}
