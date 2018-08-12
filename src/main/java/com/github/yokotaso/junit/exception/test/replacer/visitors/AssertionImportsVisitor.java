package com.github.yokotaso.junit.exception.test.replacer.visitors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class AssertionImportsVisitor extends VoidVisitorAdapter<AssertionImports> {
    @Override
    public void visit(final ImportDeclaration n, final AssertionImports arg) {
        super.visit(n, arg);
        String name = n.getName().asString();
        if (name.startsWith("org.assertj.core.api.Assertions")) {
            arg.useAssertMultiStaticAssertions = n.isStatic() && n.isAsterisk();
        }

        Range importPosition = n.getRange().orElseThrow(IllegalStateException::new);
        if (arg.lastImportPosition.end.line < importPosition.end.line) {
            arg.lastImportPosition = importPosition;
        }
    }
}
