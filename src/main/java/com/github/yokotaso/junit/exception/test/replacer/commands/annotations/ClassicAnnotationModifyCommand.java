package com.github.yokotaso.junit.exception.test.replacer.commands.annotations;

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
import com.github.yokotaso.junit.exception.test.replacer.commands.annotations.replacer.ClassicStyleAnnotationReplacer;
import com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors.AnnotationReplace;
import com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors.AnnotationVisitor;
import com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors.ImportReplace;
import com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors.ImportVisitor;


public class ClassicAnnotationModifyCommand implements CommandExecutable {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public ClassicAnnotationModifyCommand() {
    }

    @Override
    public CodeModification invokeCodeModification(InputStream inputStream) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(new InputStreamReader(inputStream), UTF_8));

        CompilationUnit compilationUnit = JavaParser.parse(byteArrayInputStream);
        ImportReplace importReplacement = new ImportReplace();
        compilationUnit.accept(new ImportVisitor(), importReplacement);

        AnnotationReplace annotationReplacement = new AnnotationReplace();
        compilationUnit.accept(new AnnotationVisitor(), annotationReplacement);

        byteArrayInputStream.reset();
        List<String> original = new BufferedReader(new InputStreamReader(byteArrayInputStream, UTF_8)).lines().collect(Collectors.toList());
        ClassicStyleAnnotationReplacer replacer = new ClassicStyleAnnotationReplacer(
                original,
                annotationReplacement.getImportReplace(),
                importReplacement.getImportReplace()
        );

        return replacer.getModifiedCode();
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