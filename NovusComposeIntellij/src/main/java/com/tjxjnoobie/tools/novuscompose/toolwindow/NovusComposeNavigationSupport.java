package com.tjxjnoobie.tools.novuscompose.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

public final class NovusComposeNavigationSupport {
    private NovusComposeNavigationSupport() {
    }

    public static void navigate(Project project, Object value) {
        if (value instanceof NovusComposeTreeModelBuilder.TypeNode typeNode) {
            navigateToClass(project, typeNode.qualifiedName());
            return;
        }
        if (value instanceof NovusComposeTreeModelBuilder.SourceInterfaceNode sourceInterfaceNode) {
            navigateToClass(project, sourceInterfaceNode.qualifiedName());
            return;
        }
        if (value instanceof NovusComposeTreeModelBuilder.MethodTreeNode methodTreeNode) {
            navigateToMethod(project, methodTreeNode);
            return;
        }
        if (value instanceof NovusComposeTreeModelBuilder.DiagnosticTreeNode diagnosticTreeNode) {
            navigateToClass(project, diagnosticTreeNode.sourceQualifiedName());
        }
    }

    public static @Nullable PsiClass findClass(Project project, String qualifiedName) {
        return JavaPsiFacade.getInstance(project).findClass(qualifiedName, GlobalSearchScope.projectScope(project));
    }

    public static @Nullable PsiMethod findMethod(Project project, NovusComposeTreeModelBuilder.MethodTreeNode methodTreeNode) {
        PsiClass owner = findClass(project, methodTreeNode.ownerQualifiedName());
        if (owner == null) {
            return null;
        }
        for (PsiMethod method : owner.getAllMethods()) {
            if (!method.getName().equals(methodTreeNode.methodName())) {
                continue;
            }
            if (method.getParameterList().getParametersCount() != methodTreeNode.parameterTypes().size()) {
                continue;
            }
            boolean exactMatch = true;
            for (int index = 0; index < method.getParameterList().getParametersCount(); index++) {
                String actualType = method.getParameterList().getParameters()[index].getType().getCanonicalText();
                if (!actualType.equals(methodTreeNode.parameterTypes().get(index))) {
                    exactMatch = false;
                    break;
                }
            }
            if (exactMatch) {
                return method;
            }
        }
        return null;
    }

    private static void navigateToClass(Project project, String qualifiedName) {
        PsiClass psiClass = findClass(project, qualifiedName);
        if (psiClass != null && psiClass.canNavigate()) {
            psiClass.navigate(true);
        }
    }

    private static void navigateToMethod(Project project, NovusComposeTreeModelBuilder.MethodTreeNode methodTreeNode) {
        PsiMethod psiMethod = findMethod(project, methodTreeNode);
        if (psiMethod != null && psiMethod.canNavigate()) {
            psiMethod.navigate(true);
        }
    }
}
