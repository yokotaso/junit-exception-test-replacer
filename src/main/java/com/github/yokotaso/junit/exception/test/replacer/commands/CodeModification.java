package com.github.yokotaso.junit.exception.test.replacer.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CodeModification {
    private final List<String> modifiedCode;

    public CodeModification(List<String> modifiedCode) {
        this.modifiedCode = modifiedCode;
    }

    public void write(OutputStream stream) throws IOException {
        for (String code : modifiedCode) {
            stream.write((code + "\n").getBytes(StandardCharsets.UTF_8));
        }
    }
}
