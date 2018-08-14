package com.github.yokotaso.junit.exception.test.replacer.commands.exception.visitors;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.stmt.Statement;
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
                    Range range = annotationExpr.getRange().orElseThrow(IllegalStateException::new);
                    ClassExpr expected = (ClassExpr) memberValuePair.getValue();
                    arg.testAnnotationPositions.put(range, expected);

                    List<Statement> statements = n.getBody().orElseThrow(IllegalStateException::new).getStatements();
                    range = statements.get(statements.size() - 1).getRange().orElseThrow(IllegalAccessError::new);
                    arg.lastStatementPositions.put(range, expected);
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
