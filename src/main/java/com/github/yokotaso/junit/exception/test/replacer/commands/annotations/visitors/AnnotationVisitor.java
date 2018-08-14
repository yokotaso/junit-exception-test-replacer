package com.github.yokotaso.junit.exception.test.replacer.commands.annotations.visitors;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class AnnotationVisitor extends VoidVisitorAdapter<AnnotationReplace> {
    ImmutableMap<Class<? extends Annotation>, Class<? extends Annotation>> annotationReplaceMap = getAnnotationReplaceMap();

    private ImmutableMap<Class<? extends Annotation>, Class<? extends Annotation>> getAnnotationReplaceMap() {
        MutableMap<Class<? extends Annotation>, Class<? extends Annotation>> map = Maps.mutable.empty();
        // map.put(org.junit.Ignore.class, org.junit.jupiter.api.Disabled.class);
        map.put(org.junit.Before.class, org.junit.jupiter.api.BeforeEach.class);
        map.put(org.junit.BeforeClass.class, org.junit.jupiter.api.BeforeAll.class);
        map.put(org.junit.After.class, org.junit.jupiter.api.AfterEach.class);
        map.put(org.junit.AfterClass.class, org.junit.jupiter.api.AfterAll.class);
        return map.toImmutable();
    }

    @Override
    public void visit(final MethodDeclaration n, final AnnotationReplace arg) {
        super.visit(n, arg);
        for (Pair<Class<? extends Annotation>, Class<? extends Annotation>> oldToNewPair : annotationReplaceMap.keyValuesView()) {
            Optional<AnnotationExpr> optExpr = n.getAnnotationByClass(oldToNewPair.getOne());
            if (optExpr.isPresent()) {
                AnnotationExpr expr = optExpr.orElseThrow(IllegalStateException::new);
                Range range = expr.getRange().orElseThrow(IllegalAccessError::new);
                arg.putAnnotationReplace(range, "@" + oldToNewPair.getTwo().getSimpleName());
            }
        }

        // @DisableはValueを取り出す
        Optional<AnnotationExpr> optExpr = n.getAnnotationByClass(org.junit.Ignore.class);
        if (optExpr.isPresent()) {
            AnnotationExpr annotationExpr = optExpr.orElseThrow(IllegalAccessError::new);
            Range range = annotationExpr.getRange().orElseThrow(IllegalStateException::new);
            if (annotationExpr.isMarkerAnnotationExpr()) {
                arg.putAnnotationReplace(range, "@Disabled");
            } else if (annotationExpr.isSingleMemberAnnotationExpr()) {
                StringLiteralExpr value = annotationExpr.asSingleMemberAnnotationExpr().getMemberValue().asStringLiteralExpr();
                arg.putAnnotationReplace(range, "@Disabled(\"" + value.asString() + "\")");
            }
        }
    }
}
