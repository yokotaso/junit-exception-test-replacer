package com.github.yokotaso.junit.exception.test.replacer.replacer;


import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.collections.api.map.ImmutableMap;

import com.github.javaparser.Range;
import com.github.javaparser.ast.expr.ClassExpr;


public class ExceptionTestReplacerBuilder {
    boolean useAssertJMultiStaticAssertions;
    @Nullable
    Range lastImportRange;

    ImmutableMap<Range, ClassExpr> testAnnotationPositionAndExpectedClass;
    ImmutableMap<Range, ClassExpr> lastStatementPositionAndExpectedClass;

    final List<String> sourceCode;

    public ExceptionTestReplacerBuilder(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }


    public ExceptionTestReplacerBuilder useAssertJMultiStaticAssertions(boolean useIt) {
        this.useAssertJMultiStaticAssertions = useIt;
        return this;
    }

    public ExceptionTestReplacerBuilder testAnnotationPositionAndExpectedClass(ImmutableMap<Range, ClassExpr> methodDeclarations) {
        this.testAnnotationPositionAndExpectedClass = methodDeclarations;
        return this;
    }

    public ExceptionTestReplacerBuilder lastStatementPositionAndExpectedClass(ImmutableMap<Range, ClassExpr> lastStatementPositionAndExpectedClass) {
        this.lastStatementPositionAndExpectedClass = lastStatementPositionAndExpectedClass;
        return this;
    }

    public ExceptionTestReplacerBuilder lastImportPosition(Range range) {
        this.lastImportRange = range;
        return this;
    }

    public ExceptionTestReplacer build() {
        return new ExceptionTestReplacer(this);
    }
}