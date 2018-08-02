package com.github.yokotaso.junit.exception.test.replacer.visitors;

import java.util.Optional;

import org.junit.Test;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ExceptionTestVisitor extends VoidVisitorAdapter<ExceptionTest> {

    private void collectMethodDeclaration(final MethodDeclaration n, final ExceptionTest arg, final Optional<AnnotationExpr> testAnnotation) {
        testAnnotation.ifPresent((annotationExpr) -> {
            for (Node node : annotationExpr.getChildNodes()) {
                if (!(node instanceof MemberValuePair)) {
                    continue;
                }
                MemberValuePair memberValuePair = (MemberValuePair) node;
                if (memberValuePair.getName().asString().equals("expected")) {
                    arg.methodDeclarationList.add(n);
                }
            }
        });
    }

    @Override
    public void visit(final MethodDeclaration n, final ExceptionTest arg) {
        super.visit(n, arg);
        Optional<AnnotationExpr> testAnnotation = n.getAnnotationByClass(Test.class);
        if (!testAnnotation.isPresent()) {
            return;
        }
        collectMethodDeclaration(n, arg, testAnnotation);
    }
}
