package com.github.yokotaso.junit.exception.test.replacer.visitors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class AssertionImportsVisitor extends VoidVisitorAdapter<AssertionImports> {
    @Override
    public void visit(final ImportDeclaration n, final AssertionImports arg) {
        super.visit(n, arg);
        String name = n.getName().asString();
        if (name.startsWith("org.junit.Assert")) {
            arg.useJunitStaticAssertions = n.isStatic();
        }

        if (name.startsWith("org.assertj.core.api.Assertions")) {
            arg.useAssertJStaticAssertion = n.isStatic() && !n.isAsterisk();
            arg.useAssertMultiStaticAssertions = n.isStatic() && n.isAsterisk();
        }
    }
}
