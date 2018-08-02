package com.github.yokotaso.junit.exception.test.replacer.visitors;

import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.yokotaso.junit.exception.test.replacer.replacer.ExceptionTestsConsumer;
import com.google.common.collect.Lists;

public class ExceptionTest {
    List<MethodDeclaration> methodDeclarationList = Lists.newArrayList();

    public void provideExceptionTests(ExceptionTestsConsumer consumer) {
        consumer.exceptionTests(methodDeclarationList);
    }
}
