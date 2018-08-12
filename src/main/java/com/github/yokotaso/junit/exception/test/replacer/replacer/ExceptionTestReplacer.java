package com.github.yokotaso.junit.exception.test.replacer.replacer;

import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.tuple.Pair;

import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import com.github.javaparser.Range;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.yokotaso.junit.exception.test.replacer.commands.CodeModification;

public class ExceptionTestReplacer {
    private final boolean useAssertJMultiStaticAssertions;
    private Range lastImportRange;


    private final ImmutableMap<Range, ClassExpr> testAnnotationPositionAndExpectedClass;
    private final ImmutableMap<Range, ClassExpr> lastStatementPositionAndExpectedClass;

    private final Patch<String> patch;
    private final List<String> sourceCode;

    public ExceptionTestReplacer(ExceptionTestReplacerBuilder builder) {
        this.useAssertJMultiStaticAssertions = builder.useAssertJMultiStaticAssertions;
        this.lastImportRange = builder.lastImportRange;

        this.testAnnotationPositionAndExpectedClass = builder.testAnnotationPositionAndExpectedClass;
        this.lastStatementPositionAndExpectedClass = builder.lastStatementPositionAndExpectedClass;
        this.sourceCode = builder.sourceCode;
        this.patch = new Patch<>();
    }

    public CodeModification getModifiedCode() {
        if (!this.useAssertJMultiStaticAssertions && this.testAnnotationPositionAndExpectedClass.notEmpty()) {
            Chunk<String> oldChunk = new Chunk<>(lastImportRange.begin.line, Collections.emptyList());
            Chunk<String> newChunk = new Chunk<>(lastImportRange.begin.line, Collections.singletonList("import static org.assertj.core.api.Assertions.assertThatThrownBy;"));
            patch.addDelta(new InsertDelta<>(oldChunk, newChunk));
        }

        for (Pair<Range, ClassExpr> rangeAndClassExpr : testAnnotationPositionAndExpectedClass.keyValuesView()) {
            Range range = rangeAndClassExpr.getOne();
            int beginLine = range.begin.line - 1;
            int beginColumn = range.begin.column - 1;
            String oldSource = sourceCode.get(beginLine);
            String indent = oldSource.substring(0, beginColumn);

            // @Test(expected = Throwable.class) を次のように置き換えるパッチを生成
            // @Test
            Chunk<String> oldChunk = new Chunk<>(beginLine, Lists.newArrayList(oldSource));
            Chunk<String> newChunk = new Chunk<>(beginLine, Lists.newArrayList(indent + "@Test"));
            patch.addDelta(new ChangeDelta<>(oldChunk, newChunk));
        }

        for (Pair<Range, ClassExpr> rangeAndClassExpr : lastStatementPositionAndExpectedClass.keyValuesView()) {
            Range range = rangeAndClassExpr.getOne();
            if (range.begin.line < range.end.line) {
                addPatchAssertThatThrownByWithBlockArg(rangeAndClassExpr);
            } else {
                addPatchAssertThatThrownByWithStatement(rangeAndClassExpr);
            }
        }

        try {
            return new CodeModification(patch.applyTo(sourceCode));
        } catch (PatchFailedException e) {
            throw new IllegalStateException(e);
        }

    }

    private String getExpcetedClass(ClassExpr classExpr) {
        Range classExprRange = classExpr.getRange().orElseThrow(IllegalStateException::new);

        if (classExprRange.begin.line != classExprRange.end.line) {
            throw new IllegalStateException("@Test(expected = >>SomeException.class<<) is strange");
        }
        return sourceCode.get(classExprRange.begin.line - 1).substring(classExprRange.begin.column - 1, classExprRange.end.column);
    }

    /**
     * <pre></pre>
     * @param rangeAndClassExpr
     */
    private void addPatchAssertThatThrownByWithStatement(Pair<Range, ClassExpr> rangeAndClassExpr) {
        Range range = rangeAndClassExpr.getOne();
        int beginLine = range.begin.line - 1;
        int beginColumn = range.begin.column - 1;
        String indent = sourceCode.get(beginLine).substring(0, beginColumn);

        StringBuilder newSource = new StringBuilder(indent).append("assertThatThrownBy(() -> ");
        newSource.append(sourceCode.get(beginLine).substring(beginColumn, range.end.column - 1));
        newSource.append(").isInstanceOf(").append(getExpcetedClass(rangeAndClassExpr.getTwo())).append(");");

        Chunk<String> oldChunk = new Chunk<>(beginLine, Collections.singletonList(sourceCode.get(beginLine)));
        Chunk<String> newChunk = new Chunk<>(beginLine, Collections.singletonList(newSource.toString()));
        patch.addDelta(new ChangeDelta<>(oldChunk, newChunk));
    }

    /**
     * <pre>
     * try {
     *     sut.exercise();
     * } catch(IOExceeption e) {
     *     // ommit
     * }
     * を次の用に変換する
     *
     * assertThatThrownBy(() -> {
     *     try {
     *         sut.exercise();
     *     } catch(IOException e) {
     *         // ommit;
     *     }
     * }).inInstanceOf(<@Test(expectedで宣言されていたクラス>);
     * </pre>
     */
    private void addPatchAssertThatThrownByWithBlockArg(Pair<Range, ClassExpr> rangeAndClassExpr) {
        Range range = rangeAndClassExpr.getOne();
        int beginLine = range.begin.line - 1;
        int endLine = range.end.line - 1;
        int beginColumn = range.begin.column - 1;

        List<String> oldSource = Lists.newArrayList();
        List<String> newSource = Lists.newArrayList();

        String indent = sourceCode.get(beginLine).substring(0, beginColumn);
        newSource.add(indent + "assertThatThrownBy(() -> {");
        for (int i = beginLine; i <= endLine; i++) {
            oldSource.add(sourceCode.get(i));
            newSource.add(indent + sourceCode.get(i));
        }

        String classExpr = getExpcetedClass(rangeAndClassExpr.getTwo());
        newSource.add(indent + "}).isInstanceOf(" + classExpr + ");");

        Chunk<String> oldChunk = new Chunk<>(beginLine, oldSource);
        Chunk<String> newChunk = new Chunk<>(beginLine, newSource);
        patch.addDelta(new ChangeDelta<>(oldChunk, newChunk));
    }
}
