package com.github.yokotaso.junit.exception.test.replacer.visitors;


import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.github.javaparser.Range;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.yokotaso.junit.exception.test.replacer.replacer.ExceptionTestReplacerBuilder;


public class ExceptionTest {
    MutableMap<Range, ClassExpr> testAnnotationPositions = Maps.mutable.empty();
    MutableMap<Range, ClassExpr> lastStatementPositions = Maps.mutable.empty();

    public void provide(ExceptionTestReplacerBuilder builder) {
        builder.lastStatementPositionAndExpectedClass(lastStatementPositions.toImmutable())
                .testAnnotationPositionAndExpectedClass(testAnnotationPositions.toImmutable());
    }
}
