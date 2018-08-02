package com.github.yokotaso.junit.exception.test.replacer.commands;

import java.io.IOException;
import java.io.OutputStream;

public class CodeModification {
    private final byte[] modifiedCode;

    CodeModification(byte[] modifiedCode) {
        this.modifiedCode = modifiedCode;
    }

    public void write(OutputStream stream) throws IOException {
        stream.write(modifiedCode);
    }
}
