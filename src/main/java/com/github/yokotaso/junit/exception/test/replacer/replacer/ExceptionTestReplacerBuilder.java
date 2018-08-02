package com.github.yokotaso.junit.exception.test.replacer.replacer;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

public class ExceptionTestReplacerBuilder implements JunitAssertionsConsumer, ExceptionTestsConsumer {
    final CompilationUnit compilationUnit;
    boolean useAssertJMultiStaticAssertions;
    List<MethodDeclaration> methodDeclarations;

    public ExceptionTestReplacerBuilder(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    @Override
    public JunitAssertionsConsumer useAssertJMultiStaticAssertions(boolean useIt) {
        this.useAssertJMultiStaticAssertions = useIt;
        return this;
    }

    @Override
    public ExceptionTestsConsumer exceptionTests(List<MethodDeclaration> methodDeclarations) {
        this.methodDeclarations = methodDeclarations;
        return this;
    }

    public ExceptionTestReplacer build() {
        return new ExceptionTestReplacer(this);
    }
}