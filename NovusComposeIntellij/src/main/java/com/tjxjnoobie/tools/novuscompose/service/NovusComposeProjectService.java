package com.tjxjnoobie.tools.novuscompose.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.util.Alarm;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.ProjectCompositionState;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionProjectAnalyzer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service(Service.Level.PROJECT)
public final class NovusComposeProjectService implements com.intellij.openapi.Disposable {
    private final Project project;
    private final Alarm alarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    private volatile ProjectCompositionState lastState = new ProjectCompositionState(Map.of(), Map.of(), Map.of());
    private boolean started;

    public NovusComposeProjectService(Project project) {
        this.project = project;
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;
        project.getMessageBus().connect(this).subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            @Override
            public void after(@NotNull List<? extends com.intellij.openapi.vfs.newvfs.events.VFileEvent> events) {
                boolean relevantChange = events.stream()
                        .map(com.intellij.openapi.vfs.newvfs.events.VFileEvent::getPath)
                        .anyMatch(path -> path.endsWith(".java"));
                if (relevantChange) {
                    scheduleRegeneration();
                }
            }
        });
        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                scheduleIfJava(event);
            }

            @Override
            public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                scheduleIfJava(event);
            }

            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                scheduleIfJava(event);
            }

            @Override
            public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
                scheduleIfJava(event);
            }

            @Override
            public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
                scheduleIfJava(event);
            }

            private void scheduleIfJava(@NotNull PsiTreeChangeEvent event) {
                PsiFile file = event.getFile();
                if (file instanceof PsiJavaFile) {
                    scheduleRegeneration();
                }
            }
        }, this);
        scheduleRegeneration();
    }

    public void scheduleRegeneration() {
        alarm.cancelAllRequests();
        alarm.addRequest(this::regenerateNow, 250);
    }

    public void regenerateNow() {
        if (project.isDisposed()) {
            return;
        }
        Runnable work = () -> {
            ApplicationManager.getApplication().invokeAndWait(
                    () -> PsiDocumentManager.getInstance(project).commitAllDocuments());
            ProjectCompositionState state = com.intellij.openapi.application.ReadAction.compute(() -> CompositionProjectAnalyzer.analyze(project));
            lastState = state;
            try {
                ComposedInterfaceGenerator.write(project, state);
            } catch (IOException e) {
                throw new RuntimeException("Failed to write composed interfaces", e);
            }
        };
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.runWhenSmart(work);
        } else {
            work.run();
        }
    }

    public ProjectCompositionState getLastState() {
        return lastState;
    }

    @Override
    public void dispose() {
        alarm.cancelAllRequests();
    }
}
