package net.minecraft.server;

import com.google.common.collect.ImmutableMap;

public class FluidImpl extends BlockDataAbstract<FluidType, Fluid> implements Fluid {

    public FluidImpl(FluidType fluidtype, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
        super(fluidtype, immutablemap);
    }

    public FluidType c() {
        return (FluidType) this.e_;
    }
}
