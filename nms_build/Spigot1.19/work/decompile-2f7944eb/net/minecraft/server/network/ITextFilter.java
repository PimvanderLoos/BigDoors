package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SystemUtils;
import net.minecraft.network.chat.IChatBaseComponent;

public interface ITextFilter {

    ITextFilter DUMMY = new ITextFilter() {
        @Override
        public void join() {}

        @Override
        public void leave() {}

        @Override
        public CompletableFuture<FilteredText<String>> processStreamMessage(String s) {
            return CompletableFuture.completedFuture(FilteredText.passThrough(s));
        }

        @Override
        public CompletableFuture<List<FilteredText<String>>> processMessageBundle(List<String> list) {
            return CompletableFuture.completedFuture((List) list.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList()));
        }
    };

    void join();

    void leave();

    CompletableFuture<FilteredText<String>> processStreamMessage(String s);

    CompletableFuture<List<FilteredText<String>>> processMessageBundle(List<String> list);

    default CompletableFuture<FilteredText<IChatBaseComponent>> processStreamComponent(IChatBaseComponent ichatbasecomponent) {
        return this.processStreamMessage(ichatbasecomponent.getString()).thenApply((filteredtext) -> {
            IChatBaseComponent ichatbasecomponent1 = (IChatBaseComponent) SystemUtils.mapNullable((String) filteredtext.filtered(), IChatBaseComponent::literal);

            return new FilteredText<>(ichatbasecomponent, ichatbasecomponent1);
        });
    }
}
