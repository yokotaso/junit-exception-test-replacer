package com.github.yokotaso.junit.exception.test.replacer.commands.annotations.replacer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.tuple.Pair;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.Patch;
import com.github.difflib.patch.PatchFailedException;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.yokotaso.junit.exception.test.replacer.commands.CodeModification;

public class ClassicStyleAnnotationReplacer {
    private static final Logger logger = Logger.getLogger(ClassicStyleAnnotationReplacer.class.getSimpleName());
    private final ImmutableMap<Range, String> annotationReplacement;
    private final ImmutableMap<Range, String> importReplacement;
    private final List<String> sourceCode;
    private final Patch<String> patch;

    public ClassicStyleAnnotationReplacer(
            List<String> sourceCode,
            ImmutableMap<Range, String> annotationReplacement,
            ImmutableMap<Range, String> importReplacement) {
        this.sourceCode = sourceCode;
        this.annotationReplacement = annotationReplacement;
        this.importReplacement = importReplacement;
        this.patch = new Patch<>();
    }

    public CodeModification getModifiedCode() {
        for (Pair<Range, String> importReplace : importReplacement.keyValuesView()) {
            int line = importReplace.getOne().begin.line - 1;
            List<String> oldSource = Collections.singletonList(sourceCode.get(line));
            List<String> newSource = Collections.singletonList("import " + importReplace.getTwo());
            patch.addDelta(new ChangeDelta<>(new Chunk<>(line, oldSource), new Chunk<>(line, newSource)));
        }

        Set<Integer> alreadModifiedLine = Sets.newHashSet();
        for (Pair<Range, String> annotationReplace : annotationReplacement.keyValuesView()) {
            Position begin = annotationReplace.getOne().begin;
            Position end = annotationReplace.getOne().end;
            int startLine = begin.line - 1;
            String replace = annotationReplace.getTwo();

            String skipMessage = String.format("modify to %s by hand. start(%d:%d)-end(%d:%d)", replace, begin.line, begin.column, end.line, end.column);
            if (begin.line != end.line) {
                logger.severe("multiple line annotation is not support. " + skipMessage);
                continue;
            }

            if (alreadModifiedLine.contains(begin.line)) {
                logger.severe("multiple annotation with one line is not support. " + skipMessage);
                continue;
            }
            String line = sourceCode.get(startLine);
            String indent = line.substring(0, begin.column - 1);
            String theOther = line.substring(end.column, line.length());

            List<String> oldSource = Collections.singletonList(sourceCode.get(startLine));
            List<String> newSource = Lists.newArrayList(indent + annotationReplace.getTwo() + theOther);

            patch.addDelta(new ChangeDelta<>(new Chunk<>(startLine, oldSource), new Chunk<>(startLine, newSource)));
            alreadModifiedLine.add(begin.line);
        }

        try {
            List<String> modifiedCode = DiffUtils.patch(sourceCode, patch);
            return new CodeModification(modifiedCode);
        } catch (PatchFailedException e) {
            throw new IllegalStateException(e);
        }
    }
}
