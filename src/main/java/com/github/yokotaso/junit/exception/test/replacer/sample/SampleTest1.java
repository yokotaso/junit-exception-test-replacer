package com.github.yokotaso.junit.exception.test.replacer.sample;

import org.junit.Test;

/**
 * <pre>
 * Vintage Junit Assertion not used case
 * Add static import for assertThatThrownBy
 * </pre>
 */
public class SampleTest1 {

    private SystemUnderTest sut = new SystemUnderTest();

    @Test(expected = IllegalStateException.class)
    public void test1() {
        sut.doSomething();
        sut.returnSomething();
        sut.throwException();
    }
}
