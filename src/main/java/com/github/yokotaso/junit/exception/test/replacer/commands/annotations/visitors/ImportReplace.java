package com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.github.javaparser.Range;

public class ImportReplace {
    MutableMap<Range, String> importReplace = Maps.mutable.empty();

    public void putImportReplace(Range range, String newFqdn) {
        importReplace.put(range, newFqdn);
    }

    public ImmutableMap<Range, String> getImportReplace() {
        return importReplace.toImmutable();
    }
}
