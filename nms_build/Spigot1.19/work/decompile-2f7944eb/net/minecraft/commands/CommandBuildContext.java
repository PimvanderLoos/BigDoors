package net.minecraft.commands;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public final class CommandBuildContext {

    private final IRegistryCustom registryAccess;
    CommandBuildContext.a missingTagAccessPolicy;

    public CommandBuildContext(IRegistryCustom iregistrycustom) {
        this.missingTagAccessPolicy = CommandBuildContext.a.FAIL;
        this.registryAccess = iregistrycustom;
    }

    public void missingTagAccessPolicy(CommandBuildContext.a commandbuildcontext_a) {
        this.missingTagAccessPolicy = commandbuildcontext_a;
    }

    public <T> HolderLookup<T> holderLookup(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return new HolderLookup.a<T>(this.registryAccess.registryOrThrow(resourcekey)) {
            @Override
            public Optional<? extends HolderSet<T>> get(TagKey<T> tagkey) {
                Optional optional;

                switch (CommandBuildContext.this.missingTagAccessPolicy) {
                    case FAIL:
                        optional = this.registry.getTag(tagkey);
                        break;
                    case CREATE_NEW:
                        optional = Optional.of(this.registry.getOrCreateTag(tagkey));
                        break;
                    case RETURN_EMPTY:
                        Optional<? extends HolderSet<T>> optional1 = this.registry.getTag(tagkey);

                        optional = Optional.of(optional1.isPresent() ? (HolderSet) optional1.get() : HolderSet.direct());
                        break;
                    default:
                        throw new IncompatibleClassChangeError();
                }

                return optional;
            }
        };
    }

    public static enum a {

        CREATE_NEW, RETURN_EMPTY, FAIL;

        private a() {}
    }
}
