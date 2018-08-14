package com.github.yokotaso.junit.exception.test.replacer.commands.exception;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.yokotaso.junit.exception.test.replacer.commands.CodeModification;
import com.github.yokotaso.junit.exception.test.replacer.commands.CommandExecutable;
import com.github.yokotaso.junit.exception.test.replacer.commands.exception.replacer.ExceptionTestReplacerBuilder;
import com.github.yokotaso.junit.exception.test.replacer.commands.exception.visitors.AssertionImports;
import com.github.yokotaso.junit.exception.test.replacer.commands.exception.visitors.AssertionImportsVisitor;
import com.github.yokotaso.junit.exception.test.replacer.commands.exception.visitors.ExceptionTest;
import com.github.yokotaso.junit.exception.test.replacer.commands.exception.visitors.ExceptionTestVisitor;


public class ExceptionTestModifyCommand implements CommandExecutable {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public ExceptionTestModifyCommand() throws IOException {
    }

    @Override
    public CodeModification invokeCodeModification(InputStream inputStream) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(new InputStreamReader(inputStream), UTF_8));

        CompilationUnit compilationUnit = JavaParser.parse(byteArrayInputStream);
        AssertionImports assertionImports = new AssertionImports();
        compilationUnit.accept(new AssertionImportsVisitor(), assertionImports);

        ExceptionTest exceptionTest = new ExceptionTest();
        compilationUnit.accept(new ExceptionTestVisitor(), exceptionTest);

        byteArrayInputStream.reset();
        List<String> original = new BufferedReader(new InputStreamReader(byteArrayInputStream, UTF_8)).lines().collect(Collectors.toList());
        ExceptionTestReplacerBuilder builder = new ExceptionTestReplacerBuilder(original);
        assertionImports.provide(builder);
        exceptionTest.provide(builder);

        return builder.build().getModifiedCode();
    }

    @Override
    public void invokeApplyCodeModification(OutputStream outputStream, CodeModification codeModification) {
        try {
            codeModification.write(outputStream);
        } catch (IOException e) {
            throw new IllegalStateException("failed to write output stream", e);
        }
    }
}
