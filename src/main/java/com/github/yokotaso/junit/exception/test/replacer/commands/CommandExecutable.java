package com.github.yokotaso.junit.exception.test.replacer.commands;

import java.io.InputStream;
import java.io.OutputStream;

public interface CommandExecutable {
    CodeModification invokeCodeModification(InputStream inputStream) throws Exception;

    void invokeApplyCodeModification(OutputStream outputStream, CodeModification codeModification);
}
