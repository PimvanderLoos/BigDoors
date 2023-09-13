package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.server.DebugReportAdvancement;
import net.minecraft.server.DebugReportBlocks;
import net.minecraft.server.DebugReportCommands;
import net.minecraft.server.DebugReportFluidTags;
import net.minecraft.server.DebugReportGenerator;
import net.minecraft.server.DebugReportItems;
import net.minecraft.server.DebugReportMojangson;
import net.minecraft.server.DebugReportNBT;
import net.minecraft.server.DebugReportRecipe;
import net.minecraft.server.DebugReportTagsBlock;
import net.minecraft.server.DebugReportTagsItem;

public class Main {

    public Main() {}

    public static void main(String[] astring) throws IOException {
        OptionParser optionparser = new OptionParser();
        AbstractOptionSpec<Void> abstractoptionspec = optionparser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionspecbuilder = optionparser.accepts("server", "Include server generators");
        OptionSpecBuilder optionspecbuilder1 = optionparser.accepts("client", "Include client generators");
        OptionSpecBuilder optionspecbuilder2 = optionparser.accepts("dev", "Include development tools");
        OptionSpecBuilder optionspecbuilder3 = optionparser.accepts("reports", "Include data reports");
        OptionSpecBuilder optionspecbuilder4 = optionparser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", new String[0]);
        ArgumentAcceptingOptionSpec<String> argumentacceptingoptionspec1 = optionparser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionset = optionparser.parse(astring);

        if (!optionset.has(abstractoptionspec) && optionset.hasOptions()) {
            Path path = Paths.get((String) argumentacceptingoptionspec.value(optionset));
            boolean flag = optionset.has(optionspecbuilder1) || optionset.has(optionspecbuilder4);
            boolean flag1 = optionset.has(optionspecbuilder) || optionset.has(optionspecbuilder4);
            boolean flag2 = optionset.has(optionspecbuilder2) || optionset.has(optionspecbuilder4);
            boolean flag3 = optionset.has(optionspecbuilder3) || optionset.has(optionspecbuilder4);
            DebugReportGenerator debugreportgenerator = a(path, (Collection) optionset.valuesOf(argumentacceptingoptionspec1).stream().map((s) -> {
                return Paths.get(s);
            }).collect(Collectors.toList()), flag, flag1, flag2, flag3);

            debugreportgenerator.c();
        } else {
            optionparser.printHelpOn(System.out);
        }
    }

    public static DebugReportGenerator a(Path path, Collection<Path> collection, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        DebugReportGenerator debugreportgenerator = new DebugReportGenerator(path, collection);

        if (flag || flag1) {
            debugreportgenerator.a(new DebugReportMojangson(debugreportgenerator));
        }

        if (flag1) {
            debugreportgenerator.a(new DebugReportFluidTags(debugreportgenerator));
            debugreportgenerator.a(new DebugReportTagsBlock(debugreportgenerator));
            debugreportgenerator.a(new DebugReportTagsItem(debugreportgenerator));
            debugreportgenerator.a(new DebugReportRecipe(debugreportgenerator));
            debugreportgenerator.a(new DebugReportAdvancement(debugreportgenerator));
        }

        if (flag2) {
            debugreportgenerator.a(new DebugReportNBT(debugreportgenerator));
        }

        if (flag3) {
            debugreportgenerator.a(new DebugReportBlocks(debugreportgenerator));
            debugreportgenerator.a(new DebugReportItems(debugreportgenerator));
            debugreportgenerator.a(new DebugReportCommands(debugreportgenerator));
        }

        return debugreportgenerator;
    }
}
