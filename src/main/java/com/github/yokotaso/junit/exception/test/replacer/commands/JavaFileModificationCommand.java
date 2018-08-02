package com.github.yokotaso.junit.exception.test.replacer.commands;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.yokotaso.junit.exception.test.replacer.replacer.ExceptionTestReplacer;
import com.github.yokotaso.junit.exception.test.replacer.replacer.ExceptionTestReplacerBuilder;
import com.github.yokotaso.junit.exception.test.replacer.visitors.AssertionImports;
import com.github.yokotaso.junit.exception.test.replacer.visitors.AssertionImportsVisitor;
import com.github.yokotaso.junit.exception.test.replacer.visitors.ExceptionTest;
import com.github.yokotaso.junit.exception.test.replacer.visitors.ExceptionTestVisitor;


public class JavaFileModificationCommand implements CommandExecutable {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private final File tempFile;

    public JavaFileModificationCommand() throws IOException {
        this.tempFile = File.createTempFile("/tmp/", ".java.tmp");
    }

    @Override
    public CodeModification invokeCodeModification(InputStream inputStream) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(new InputStreamReader(inputStream), UTF_8));

        CompilationUnit compilationUnit = JavaParser.parse(byteArrayInputStream);
        AssertionImports assertionImports = new AssertionImports();
        compilationUnit.accept(new AssertionImportsVisitor(), assertionImports);

        ExceptionTest exceptionTest = new ExceptionTest();
        compilationUnit.accept(new ExceptionTestVisitor(), exceptionTest);

        ExceptionTestReplacerBuilder builder = new ExceptionTestReplacerBuilder(compilationUnit);
        assertionImports.provideAssertionImports(builder);
        exceptionTest.provideExceptionTests(builder);

        ExceptionTestReplacer replacer = builder.build();
        replacer.replace();

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(compilationUnit.toString().getBytes(UTF_8));
        }

        // java-perserはlexical preserveできないので空白のみ変更は無視する
        byteArrayInputStream.reset();
        List<String> original = new BufferedReader(new InputStreamReader(byteArrayInputStream, UTF_8)).lines().collect(Collectors.toList());
        List<String> revised = Files.readAllLines(tempFile.toPath());

        Patch<String> patch = DiffUtils.diff(original, revised);
        Patch<String> whiteSpaceDiffExcludePatch = new Patch<>();
        List<Delta<String>> deltas = patch.getDeltas()
                .stream()
                .filter((delta) -> !(delta.getType() == DeltaType.INSERT && delta.getOriginal().getLines().isEmpty()))
                .filter((delta) -> !(delta.getType() == DeltaType.DELETE && delta.getRevised().getLines().isEmpty()))
                .collect(Collectors.toList());
        whiteSpaceDiffExcludePatch.getDeltas().addAll(deltas);
        List<String> lexicalPreservedCodes = DiffUtils.patch(original, whiteSpaceDiffExcludePatch);

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            for (String lexicalPreservedCode : lexicalPreservedCodes) {
                outputStream.write(lexicalPreservedCode.getBytes(StandardCharsets.UTF_8));
                outputStream.write('\n');
            }
        }
        CodeModification codeModification = new CodeModification(Files.readAllBytes(tempFile.toPath()));
        if (!tempFile.delete()) {
            throw new IllegalStateException("Failed to remove temp file");
        }
        return codeModification;
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
