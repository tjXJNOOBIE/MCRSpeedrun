package com.tjxjnoobie.tools.novuscompose.toolwindow;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.Nullable;

public final class NovusComposeQuickFixes {
    private NovusComposeQuickFixes() {
    }

    public static boolean isFixableWarning(Object value) {
        return value instanceof NovusComposeTreeModelBuilder.DiagnosticTreeNode diagnosticTreeNode
                && diagnosticTreeNode.kind() == NovusComposeTreeModelBuilder.NodeKind.DIAGNOSTIC_WARNING
                && diagnosticTreeNode.diagnosticMessage().startsWith("Multiple generated methods named '");
    }

    public static boolean applyFix(Project project, Object value) {
        if (!(value instanceof NovusComposeTreeModelBuilder.DiagnosticTreeNode diagnosticTreeNode)) {
            return false;
        }
        return applyMethodPrefixFix(project, diagnosticTreeNode);
    }

    private static boolean applyMethodPrefixFix(Project project, NovusComposeTreeModelBuilder.DiagnosticTreeNode diagnosticTreeNode) {
        PsiClass sourceInterface = NovusComposeNavigationSupport.findClass(project, diagnosticTreeNode.sourceQualifiedName());
        if (sourceInterface == null) {
            return false;
        }
        PsiAnnotation annotation = sourceInterface.getAnnotation("com.tjxjnoobie.api.dependency.annotations.ComposesToInterface");
        if (annotation == null) {
            return false;
        }

        String suggestedPrefix = suggestedMethodPrefix(sourceInterface.getName());
        if (suggestedPrefix.isBlank()) {
            return false;
        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            annotation.setDeclaredAttributeValue(
                    "methodPrefix",
                    JavaPsiFacade.getElementFactory(project)
                            .createExpressionFromText("\"" + suggestedPrefix + "\"", sourceInterface));
        });
        return true;
    }

    private static String suggestedMethodPrefix(@Nullable String simpleName) {
        if (simpleName == null || simpleName.isBlank()) {
            return "";
        }
        String stripped = simpleName.length() > 1 && simpleName.startsWith("I") && Character.isUpperCase(simpleName.charAt(1))
                ? simpleName.substring(1)
                : simpleName;
        if (stripped.isBlank()) {
            return "";
        }
        if (stripped.length() == 1) {
            return stripped.toLowerCase();
        }
        return Character.toLowerCase(stripped.charAt(0)) + stripped.substring(1);
    }
}
