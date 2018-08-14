package com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import com.github.javaparser.Range;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ImportVisitor extends VoidVisitorAdapter<ImportReplace> {

    ImmutableMap<String, String> importReplaceMap = getImportReplaceMap();

    private ImmutableMap<String, String> getImportReplaceMap() {
        MutableMap<String, String> map = Maps.mutable.empty();
        map.put("org.junit.Test", "org.junit.jupiter.api.Test");
        map.put("org.junit.Ignore", "org.junit.jupiter.api.Disabled");
        map.put("org.junit.Before", "org.junit.jupiter.api.BeforeEach");
        map.put("org.junit.BeforeClass", "org.junit.jupiter.api.BeforeAll");
        map.put("org.junit.After", "org.junit.jupiter.api.Each");
        map.put("org.junit.AfterClass", "org.junit.jupiter.api.AfterAll");
        return map.toImmutable();
    }

    @Override
    public void visit(final ImportDeclaration n, final ImportReplace arg) {
        super.visit(n, arg);
        String fqdn = n.getNameAsString();
        if (importReplaceMap.containsKey(fqdn)) {
            Range range = n.getRange().orElseThrow(IllegalStateException::new);
            arg.putImportReplace(range, importReplaceMap.get(fqdn));
        }
    }
}
