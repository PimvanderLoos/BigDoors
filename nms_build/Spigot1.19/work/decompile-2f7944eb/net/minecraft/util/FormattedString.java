package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.chat.ChatModifier;

@FunctionalInterface
public interface FormattedString {

    FormattedString EMPTY = (formattedstringempty) -> {
        return true;
    };

    boolean accept(FormattedStringEmpty formattedstringempty);

    static FormattedString codepoint(int i, ChatModifier chatmodifier) {
        return (formattedstringempty) -> {
            return formattedstringempty.accept(0, chatmodifier, i);
        };
    }

    static FormattedString forward(String s, ChatModifier chatmodifier) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.iterate(s, chatmodifier, formattedstringempty);
        };
    }

    static FormattedString forward(String s, ChatModifier chatmodifier, Int2IntFunction int2intfunction) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.iterate(s, chatmodifier, decorateOutput(formattedstringempty, int2intfunction));
        };
    }

    static FormattedString backward(String s, ChatModifier chatmodifier) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.iterateBackwards(s, chatmodifier, formattedstringempty);
        };
    }

    static FormattedString backward(String s, ChatModifier chatmodifier, Int2IntFunction int2intfunction) {
        return s.isEmpty() ? FormattedString.EMPTY : (formattedstringempty) -> {
            return StringDecomposer.iterateBackwards(s, chatmodifier, decorateOutput(formattedstringempty, int2intfunction));
        };
    }

    static FormattedStringEmpty decorateOutput(FormattedStringEmpty formattedstringempty, Int2IntFunction int2intfunction) {
        return (i, chatmodifier, j) -> {
            return formattedstringempty.accept(i, chatmodifier, (Integer) int2intfunction.apply(j));
        };
    }

    static FormattedString composite() {
        return FormattedString.EMPTY;
    }

    static FormattedString composite(FormattedString formattedstring) {
        return formattedstring;
    }

    static FormattedString composite(FormattedString formattedstring, FormattedString formattedstring1) {
        return fromPair(formattedstring, formattedstring1);
    }

    static FormattedString composite(FormattedString... aformattedstring) {
        return fromList(ImmutableList.copyOf(aformattedstring));
    }

    static FormattedString composite(List<FormattedString> list) {
        int i = list.size();

        switch (i) {
            case 0:
                return FormattedString.EMPTY;
            case 1:
                return (FormattedString) list.get(0);
            case 2:
                return fromPair((FormattedString) list.get(0), (FormattedString) list.get(1));
            default:
                return fromList(ImmutableList.copyOf(list));
        }
    }

    static FormattedString fromPair(FormattedString formattedstring, FormattedString formattedstring1) {
        return (formattedstringempty) -> {
            return formattedstring.accept(formattedstringempty) && formattedstring1.accept(formattedstringempty);
        };
    }

    static FormattedString fromList(List<FormattedString> list) {
        return (formattedstringempty) -> {
            Iterator iterator = list.iterator();

            FormattedString formattedstring;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                formattedstring = (FormattedString) iterator.next();
            } while (formattedstring.accept(formattedstringempty));

            return false;
        };
    }
}
