package com.github.yokotaso.junit.exception.test.replacer.replacer;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

public class ExceptionTestReplacer {
    private final CompilationUnit compilationUnit;
    private final boolean useAssertJMultiStaticAssertions;
    private final List<MethodDeclaration> methodDeclarations;

    public ExceptionTestReplacer(ExceptionTestReplacerBuilder builder) {
        this.compilationUnit = builder.compilationUnit;
        this.useAssertJMultiStaticAssertions = builder.useAssertJMultiStaticAssertions;
        this.methodDeclarations = builder.methodDeclarations;
    }

    public void replace() {
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            ClassExpr expectedClass = replaceTestAnnotation(methodDeclaration);
            BlockStmt blockStatement = methodDeclaration.getBody().orElseThrow(IllegalStateException::new);
            replaceLastStatement(expectedClass, blockStatement);
        }
    }

    private ClassExpr replaceTestAnnotation(MethodDeclaration methodDeclaration) {
        AnnotationExpr annotationExpr = methodDeclaration.getAnnotationByClass(org.junit.Test.class).orElseThrow(IllegalStateException::new);
        MemberValuePair expectedAnnotation = (MemberValuePair) annotationExpr.getChildNodes()
                .stream()
                .filter((node) -> (node instanceof MemberValuePair))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        methodDeclaration.setLineComment("TODO CHECK ME " + annotationExpr.toString());
        methodDeclaration.replace(annotationExpr, new MarkerAnnotationExpr("Test"));

        return expectedAnnotation.getValue().asClassExpr();
    }

    private void replaceLastStatement(ClassExpr expectedClass, BlockStmt blockStatement) {
        Statement statement = blockStatement.getStatement(blockStatement.getStatements().size() - 1);
        MethodCallExpr assertionCall = null;
        if (useAssertJMultiStaticAssertions) {
            Expression fluentCall = new MethodCallExpr("assertThatThrownBy", new LambdaExpr(new NodeList<>(), statement, true));
            assertionCall = new MethodCallExpr(fluentCall, "isInstanceOf", new NodeList<>(expectedClass));
        } else {
            // fallback add static import and replace assertj assertion
            Expression fluentCall = new MethodCallExpr("assertThatThrownBy", new LambdaExpr(new NodeList<>(), statement, true));
            assertionCall = new MethodCallExpr(fluentCall, "isInstanceOf", new NodeList<>(expectedClass));
            compilationUnit.addImport(new ImportDeclaration("org.assertj.core.api.Assertions.assertThatThrownBy", true, false));
        }
        blockStatement.replace(statement, new ExpressionStmt(assertionCall));
    }
}
