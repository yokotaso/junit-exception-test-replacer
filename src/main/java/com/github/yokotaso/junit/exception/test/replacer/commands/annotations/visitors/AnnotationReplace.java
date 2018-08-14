package com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.github.javaparser.Range;

public class AnnotationReplace {
    MutableMap<Range, String> importReplace = Maps.mutable.empty();

    public void putAnnotationReplace(Range range, String replace) {
        importReplace.put(range, replace);
    }

    public ImmutableMap<Range, String> getImportReplace() {
        return importReplace.toImmutable();
    }
}
