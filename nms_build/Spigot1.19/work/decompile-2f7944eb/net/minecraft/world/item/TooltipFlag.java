package net.minecraft.world.item;

public interface TooltipFlag {

    boolean isAdvanced();

    public static enum a implements TooltipFlag {

        NORMAL(false), ADVANCED(true);

        private final boolean advanced;

        private a(boolean flag) {
            this.advanced = flag;
        }

        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }
    }
}
