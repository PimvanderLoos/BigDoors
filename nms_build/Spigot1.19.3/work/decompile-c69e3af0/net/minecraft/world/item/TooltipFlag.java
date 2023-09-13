package net.minecraft.world.item;

public interface TooltipFlag {

    TooltipFlag.a NORMAL = new TooltipFlag.a(false, false);
    TooltipFlag.a ADVANCED = new TooltipFlag.a(true, false);

    boolean isAdvanced();

    boolean isCreative();

    public static record a(boolean advanced, boolean creative) implements TooltipFlag {

        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }

        @Override
        public boolean isCreative() {
            return this.creative;
        }

        public TooltipFlag.a asCreative() {
            return new TooltipFlag.a(this.advanced, true);
        }
    }
}
