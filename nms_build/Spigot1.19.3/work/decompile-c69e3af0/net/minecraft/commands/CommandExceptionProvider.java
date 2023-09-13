package net.minecraft.commands;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.IChatBaseComponent;

public class CommandExceptionProvider implements BuiltInExceptionProvider {

    private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.double.low", object1, object);
    });
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.double.big", object1, object);
    });
    private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.float.low", object1, object);
    });
    private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.float.big", object1, object);
    });
    private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.integer.low", object1, object);
    });
    private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.integer.big", object1, object);
    });
    private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.long.low", object1, object);
    });
    private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("argument.long.big", object1, object);
    });
    private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("argument.literal.incorrect", object);
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.quote.expected.start"));
    private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.quote.expected.end"));
    private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.quote.escape", object);
    });
    private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.bool.invalid", object);
    });
    private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.int.invalid", object);
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.int.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.long.invalid", object);
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.long.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.double.invalid", object);
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.double.expected"));
    private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.float.invalid", object);
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.float.expected"));
    private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(IChatBaseComponent.translatable("parsing.bool.expected"));
    private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("parsing.expected", object);
    });
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(IChatBaseComponent.translatable("command.unknown.command"));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(IChatBaseComponent.translatable("command.unknown.argument"));
    private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(IChatBaseComponent.translatable("command.expected.separator"));
    private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("command.exception", object);
    });

    public CommandExceptionProvider() {}

    public Dynamic2CommandExceptionType doubleTooLow() {
        return CommandExceptionProvider.DOUBLE_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType doubleTooHigh() {
        return CommandExceptionProvider.DOUBLE_TOO_BIG;
    }

    public Dynamic2CommandExceptionType floatTooLow() {
        return CommandExceptionProvider.FLOAT_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType floatTooHigh() {
        return CommandExceptionProvider.FLOAT_TOO_BIG;
    }

    public Dynamic2CommandExceptionType integerTooLow() {
        return CommandExceptionProvider.INTEGER_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType integerTooHigh() {
        return CommandExceptionProvider.INTEGER_TOO_BIG;
    }

    public Dynamic2CommandExceptionType longTooLow() {
        return CommandExceptionProvider.LONG_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType longTooHigh() {
        return CommandExceptionProvider.LONG_TOO_BIG;
    }

    public DynamicCommandExceptionType literalIncorrect() {
        return CommandExceptionProvider.LITERAL_INCORRECT;
    }

    public SimpleCommandExceptionType readerExpectedStartOfQuote() {
        return CommandExceptionProvider.READER_EXPECTED_START_OF_QUOTE;
    }

    public SimpleCommandExceptionType readerExpectedEndOfQuote() {
        return CommandExceptionProvider.READER_EXPECTED_END_OF_QUOTE;
    }

    public DynamicCommandExceptionType readerInvalidEscape() {
        return CommandExceptionProvider.READER_INVALID_ESCAPE;
    }

    public DynamicCommandExceptionType readerInvalidBool() {
        return CommandExceptionProvider.READER_INVALID_BOOL;
    }

    public DynamicCommandExceptionType readerInvalidInt() {
        return CommandExceptionProvider.READER_INVALID_INT;
    }

    public SimpleCommandExceptionType readerExpectedInt() {
        return CommandExceptionProvider.READER_EXPECTED_INT;
    }

    public DynamicCommandExceptionType readerInvalidLong() {
        return CommandExceptionProvider.READER_INVALID_LONG;
    }

    public SimpleCommandExceptionType readerExpectedLong() {
        return CommandExceptionProvider.READER_EXPECTED_LONG;
    }

    public DynamicCommandExceptionType readerInvalidDouble() {
        return CommandExceptionProvider.READER_INVALID_DOUBLE;
    }

    public SimpleCommandExceptionType readerExpectedDouble() {
        return CommandExceptionProvider.READER_EXPECTED_DOUBLE;
    }

    public DynamicCommandExceptionType readerInvalidFloat() {
        return CommandExceptionProvider.READER_INVALID_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedFloat() {
        return CommandExceptionProvider.READER_EXPECTED_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedBool() {
        return CommandExceptionProvider.READER_EXPECTED_BOOL;
    }

    public DynamicCommandExceptionType readerExpectedSymbol() {
        return CommandExceptionProvider.READER_EXPECTED_SYMBOL;
    }

    public SimpleCommandExceptionType dispatcherUnknownCommand() {
        return CommandExceptionProvider.DISPATCHER_UNKNOWN_COMMAND;
    }

    public SimpleCommandExceptionType dispatcherUnknownArgument() {
        return CommandExceptionProvider.DISPATCHER_UNKNOWN_ARGUMENT;
    }

    public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
        return CommandExceptionProvider.DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
    }

    public DynamicCommandExceptionType dispatcherParseException() {
        return CommandExceptionProvider.DISPATCHER_PARSE_EXCEPTION;
    }
}
