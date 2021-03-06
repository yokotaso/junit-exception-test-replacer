package com.github.yokotaso.junit.exception.test.replacer.sample;

import org.junit.Test;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;


/**
 * <pre>
 * Vintage Junit Assertion are used case (
 * </pre>
 */
public class SampleTest2 {

    private SystemUnderTest sut = new SystemUnderTest();

    @Test(expected = IllegalStateException.class)
    public void test1() {
        sut.doSomething();
        assertThat(sut.returnSomething(), is(1));
        sut.throwException();
    }
}
