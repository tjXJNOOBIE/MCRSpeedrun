package com.tjxjnoobie.tools.novuscompose.inspection;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionConstants;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.CompositionDiagnostic;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionProjectAnalyzer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ComposesToInterfaceInspection extends LocalInspectionTool {
    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(@NotNull PsiClass aClass) {
                if (!aClass.isInterface() || aClass.getQualifiedName() == null) {
                    return;
                }
                if (aClass.getAnnotation(CompositionConstants.ANNOTATION_FQCN) == null) {
                    return;
                }

                var state = CompositionProjectAnalyzer.analyze(holder.getProject());
                List<CompositionDiagnostic> diagnostics = state.diagnosticsBySource().get(aClass.getQualifiedName());
                if (diagnostics == null || diagnostics.isEmpty()) {
                    return;
                }

                var anchor = aClass.getNameIdentifier() == null ? aClass : aClass.getNameIdentifier();
                for (CompositionDiagnostic diagnostic : diagnostics) {
                    holder.registerProblem(anchor, diagnostic.message(), toHighlightType(diagnostic.severity()));
                }
            }
        };
    }

    private static ProblemHighlightType toHighlightType(HighlightSeverity severity) {
        if (HighlightSeverity.ERROR.equals(severity)) {
            return ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
        }
        if (HighlightSeverity.WEAK_WARNING.equals(severity)) {
            return ProblemHighlightType.WEAK_WARNING;
        }
        return ProblemHighlightType.WARNING;
    }
}
