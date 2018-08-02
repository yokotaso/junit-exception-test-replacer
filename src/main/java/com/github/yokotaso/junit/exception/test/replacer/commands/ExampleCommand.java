package com.github.yokotaso.junit.exception.test.replacer.commands;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;

public class ExampleCommand implements CommandExecutable {
    private static final class ClassAndPath {
        private final String description;
        private final String className;
        private final Path path;

        private ClassAndPath(String description, String className, Path path) {
            this.description = description;
            this.className = className;
            this.path = path;
        }
    }

    private static final ImmutableList<ClassAndPath> SAMPLE_CODES = Lists.immutable.of(
            new ClassAndPath("simple junit test case(no assertion)", "SampleTest1", Paths.get("src/main/java/com/github/yokotaso/junit/exception/test/replacer/sample/SampleTest1.java")),
            new ClassAndPath("junit4 style assert with multi imports", "SampleTest2", Paths.get("src/main/java/com/github/yokotaso/junit/exception/test/replacer/sample/SampleTest2.java")),
            new ClassAndPath("junit4 style assert with single imports", "SampleTest3", Paths.get("src/main/java/com/github/yokotaso/junit/exception/test/replacer/sample/SampleTest3.java")),
            new ClassAndPath("assertj style assert with single imports", "SampleTest4", Paths.get("src/main/java/com/github/yokotaso/junit/exception/test/replacer/sample/SampleTest4.java")),
            new ClassAndPath("assertj style assert with multi imports", "SampleTest5", Paths.get("src/main/java/com/github/yokotaso/junit/exception/test/replacer/sample/SampleTest5.java"))
    );

    @Override
    public CodeModification invokeCodeModification(InputStream dummyStream) throws Exception {
        CommandExecutable executable = new JavaFileModificationCommand();
        for (ClassAndPath classAndPath : SAMPLE_CODES) {
            System.out.println("// Sample Code " + classAndPath.className + " ===");
            System.out.println("// " + classAndPath.description);
            try (InputStream inputStream = new FileInputStream(classAndPath.path.toFile())) {
                CodeModification codeModification = executable.invokeCodeModification(inputStream);
                try (OutputStream outputStream = new BufferedOutputStream(System.out)) {
                    executable.invokeApplyCodeModification(outputStream, codeModification);
                }
            }
        }
        return null;
    }

    @Override
    public void invokeApplyCodeModification(OutputStream outputStream, CodeModification codeModification) {
        // do nothing
    }


}
