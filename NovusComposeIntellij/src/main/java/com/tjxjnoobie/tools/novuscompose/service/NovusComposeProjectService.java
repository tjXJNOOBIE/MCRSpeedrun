package com.tjxjnoobie.tools.novuscompose.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
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
import org.tavall.couriers.api.concurrent.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Service(Service.Level.PROJECT)
public final class NovusComposeProjectService implements com.intellij.openapi.Disposable {
    private final Project project;
    private final Alarm alarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
    private final CopyOnWriteArrayList<Consumer<ProjectCompositionState>> listeners = new CopyOnWriteArrayList<>();
    private final AtomicLong requestSequence = new AtomicLong();
    private volatile ProjectCompositionState lastState = new ProjectCompositionState(Map.of(), Map.of(), Map.of());
    private volatile CompletableFuture<ProjectCompositionState> inFlightRegeneration;
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
        commitAllDocuments();
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            regenerateSynchronously();
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.runWhenSmart(this::regenerateNow);
            return;
        }
        long requestId = requestSequence.incrementAndGet();
        AsyncTask.ScopeOptions options = AsyncTask.ScopeOptions.defaults().withName("novus-compose-regeneration");
        CompletableFuture<ProjectCompositionState> future = AsyncTask.runFuture(this::analyzeState, options)
                .thenApply(state -> persistState(state, requestId));
        inFlightRegeneration = future;
        future.exceptionally(throwable -> {
            if (!project.isDisposed()) {
                throwable.printStackTrace();
            }
            return null;
        });
    }

    public ProjectCompositionState getLastState() {
        return lastState;
    }

    public void awaitIdle() {
        CompletableFuture<ProjectCompositionState> future = inFlightRegeneration;
        if (future != null) {
            future.join();
        }
    }

    public void addStateListener(Consumer<ProjectCompositionState> listener, Disposable parentDisposable) {
        listeners.add(listener);
        Disposer.register(parentDisposable, () -> listeners.remove(listener));
    }

    private void notifyListeners(ProjectCompositionState state) {
        Runnable notifier = () -> {
            if (project.isDisposed()) {
                return;
            }
            for (Consumer<ProjectCompositionState> listener : listeners) {
                listener.accept(state);
            }
        };
        if (ApplicationManager.getApplication().isDispatchThread()) {
            notifier.run();
            return;
        }
        ApplicationManager.getApplication().invokeLater(notifier, ModalityState.any());
    }

    private void regenerateSynchronously() {
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.completeJustSubmittedTasks();
            dumbService.waitForSmartMode();
        }
        long requestId = requestSequence.incrementAndGet();
        ProjectCompositionState state = analyzeState();
        persistState(state, requestId);
        inFlightRegeneration = CompletableFuture.completedFuture(state);
    }

    private ProjectCompositionState analyzeState() {
        return com.intellij.openapi.application.ReadAction.compute(() -> CompositionProjectAnalyzer.analyze(project));
    }

    private ProjectCompositionState persistState(ProjectCompositionState state, long requestId) {
        if (project.isDisposed() || requestId != requestSequence.get()) {
            return state;
        }
        try {
            ComposedInterfaceGenerator.write(project, state);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write composed interfaces", e);
        }
        lastState = state;
        notifyListeners(state);
        return state;
    }

    private void commitAllDocuments() {
        Runnable commitAction = () -> PsiDocumentManager.getInstance(project).commitAllDocuments();
        if (ApplicationManager.getApplication().isDispatchThread()) {
            commitAction.run();
            return;
        }
        ApplicationManager.getApplication().invokeAndWait(commitAction);
    }

    @Override
    public void dispose() {
        alarm.cancelAllRequests();
        CompletableFuture<ProjectCompositionState> future = inFlightRegeneration;
        if (future != null) {
            future.cancel(true);
        }
        listeners.clear();
    }
}
