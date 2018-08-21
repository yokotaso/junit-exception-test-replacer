# Junit Exception Test Replacer
This tool replace Junit4 style annotation `@Test(expected=RuntimeException.class)` to Junit5 Ready style.

## Build

```
./gradlew jar
```

## Usage

```
java -jar junit-exeption-test-replacer-all.jar --input=<dir or file> --replace=(exception-test|classic-annotation)
```


### Options

#### input

Path or directory which you want to modify test.

If you set path of test code, this tool modify that code.

Or if you set directory of test code, this tool modify codes recursively in directory .


#### replace

##### exception-test

this option will replace bellow code.

Before:

```java
@Test(expected=RuntimeException.class)
public void test() {
   sut.setup();
   sut.exercise();
}
```

After:

```java
@Test
public void test() {
   sut.setup();
   assertThatThrownBy(() -> sut.exercise()).isInstnaceOf(RuntimeException.class);
}
```

And assertj is not imported in modified code, add import `import static org.assertj.core.api.Assertions.assertThatThrownBy;` automatically.

#### classic-annotation(experimental)
**this option is experimantal feature.**

this option will replace bellow code.

Before:

```java
import org.junit.Test;
import org.junit.Ignore;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;

@Test
public void test() {}

@Ignore("This is ignored because ...")
public void test2() {}

@Before
public void before() {}

@BeforeClass
public static void beforeClass() {}

@After
public void after() {}

@AfterClass
public void afterClass() {}
```

After:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;

@Test
public void test() {}

@Disabled("This is ignored because ...")
public void test2() {}

@BeforeEach
public void before() {}

@BeforeAll
public static void beforeClass() {}

@AfterEach
public void after() {}

@AfterAll
public void afterClass() {}
```

