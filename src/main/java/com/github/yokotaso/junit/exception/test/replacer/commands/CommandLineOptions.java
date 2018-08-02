package com.github.yokotaso.junit.exception.test.replacer.commands;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class CommandLineOptions extends OptionsBase {

    @Option(
            name = "example",
            defaultValue = "false"
    )
    public boolean example;

    @Option(
            name = "input",
            defaultValue = ""
    )
    public String input;

    @Option(
            name = "output",
            defaultValue = ""
    )
    public String output;
}
