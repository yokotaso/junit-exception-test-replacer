package com.github.yokotaso.junit.exception.test.replacer.sample;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * <pre>
 * assertj used case
 * </pre>
 */
public class SampleTest4 {

    private SystemUnderTest sut = new SystemUnderTest();

    @Test(expected = IllegalStateException.class)
    public void test1() {
        sut.doSomething();
        assertThat(sut.returnSomething()).isEqualTo(1);
        sut.throwException();
    }
}
