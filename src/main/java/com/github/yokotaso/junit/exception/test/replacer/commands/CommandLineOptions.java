package com.github.yokotaso.junit.exception.test.replacer.commands;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

public class CommandLineOptions extends OptionsBase {

    @Option(
            name = "input",
            defaultValue = "",
            help = "value of input file or directory"
    )
    public String input;

    @Option(
            name = "output",
            defaultValue = "",
            help = "value of output directory"
    )
    public String output;
}
