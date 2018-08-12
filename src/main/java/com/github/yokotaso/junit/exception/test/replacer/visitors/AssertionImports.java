package com.github.yokotaso.junit.exception.test.replacer.visitors;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.yokotaso.junit.exception.test.replacer.replacer.ExceptionTestReplacerBuilder;


public class AssertionImports {
    boolean useAssertMultiStaticAssertions = false;
    Range lastImportPosition = new Range(new Position(0, 0), new Position(0, 0));

    public void provide(ExceptionTestReplacerBuilder builder) {
        builder.useAssertJMultiStaticAssertions(useAssertMultiStaticAssertions)
                .lastImportPosition(lastImportPosition);
    }
}
