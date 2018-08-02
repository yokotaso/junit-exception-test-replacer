package com.github.yokotaso.junit.exception.test.replacer.replacer;

import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;

public interface ExceptionTestsConsumer {
    ExceptionTestsConsumer exceptionTests(List<MethodDeclaration> methodDeclarations);
}
