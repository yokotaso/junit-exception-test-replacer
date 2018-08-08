package com.github.yokotaso.junit.exception.test.replacer.visitors.methods;

import javax.annotation.Nullable;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeLiteral;

public class TestAnnotationVisitor extends ASTVisitor {



    @Override
    public boolean visit(final NormalAnnotation annotation) {
        if(annotation.getTypeName().getFullyQualifiedName().equals("org.junit.Test")) {
            ExceptionAttributeVisitor exceptionAttributeVisitor = new ExceptionAttributeVisitor();
            annotation.accept(exceptionAttributeVisitor);
        }
        return true;
    }


}
