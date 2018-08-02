package com.github.yokotaso.junit.exception.test.replacer.visitors;

import com.github.yokotaso.junit.exception.test.replacer.replacer.JunitAssertionsConsumer;

public class AssertionImports {
    boolean useJunitStaticAssertions;
    boolean useAssertJStaticAssertion;
    boolean useAssertMultiStaticAssertions;

    public void provideAssertionImports(JunitAssertionsConsumer consumer) {
        consumer.useAssertJMultiStaticAssertions(useAssertMultiStaticAssertions);
    }
}
