package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {

    <T> HolderLookup<T> holderLookup(ResourceKey<? extends IRegistry<T>> resourcekey);

    static CommandBuildContext simple(final HolderLookup.b holderlookup_b, final FeatureFlagSet featureflagset) {
        return new CommandBuildContext() {
            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends IRegistry<T>> resourcekey) {
                return holderlookup_b.lookupOrThrow(resourcekey).filterFeatures(featureflagset);
            }
        };
    }

    static CommandBuildContext.a configurable(final IRegistryCustom iregistrycustom, final FeatureFlagSet featureflagset) {
        return new CommandBuildContext.a() {
            CommandBuildContext.b missingTagAccessPolicy;

            {
                this.missingTagAccessPolicy = CommandBuildContext.b.FAIL;
            }

            @Override
            public void missingTagAccessPolicy(CommandBuildContext.b commandbuildcontext_b) {
                this.missingTagAccessPolicy = commandbuildcontext_b;
            }

            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends IRegistry<T>> resourcekey) {
                IRegistry<T> iregistry = iregistrycustom.registryOrThrow(resourcekey);
                final HolderLookup.c<T> holderlookup_c = iregistry.asLookup();
                final HolderLookup.c<T> holderlookup_c1 = iregistry.asTagAddingLookup();
                HolderLookup.c<T> holderlookup_c2 = new HolderLookup.c.a<T>() {
                    @Override
                    protected HolderLookup.c<T> parent() {
                        HolderLookup.c holderlookup_c3;

                        switch (missingTagAccessPolicy) {
                            case FAIL:
                                holderlookup_c3 = holderlookup_c;
                                break;
                            case CREATE_NEW:
                                holderlookup_c3 = holderlookup_c1;
                                break;
                            default:
                                throw new IncompatibleClassChangeError();
                        }

                        return holderlookup_c3;
                    }
                };

                return holderlookup_c2.filterFeatures(featureflagset);
            }
        };
    }

    public interface a extends CommandBuildContext {

        void missingTagAccessPolicy(CommandBuildContext.b commandbuildcontext_b);
    }

    public static enum b {

        CREATE_NEW, FAIL;

        private b() {}
    }
}
