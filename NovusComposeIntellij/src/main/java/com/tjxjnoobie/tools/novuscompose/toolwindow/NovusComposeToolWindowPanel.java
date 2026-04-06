package com.tjxjnoobie.tools.novuscompose.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import com.tjxjnoobie.tools.novuscompose.analysis.CompositionModel.ProjectCompositionState;
import com.tjxjnoobie.tools.novuscompose.service.NovusComposeProjectService;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public final class NovusComposeToolWindowPanel extends SimpleToolWindowPanel implements Disposable {
    private static final String NAVIGATE_ACTION_ID = "NovusCompose.Navigate";

    private final Project project;
    private final NovusComposeProjectService projectService;
    private final Tree tree;

    public NovusComposeToolWindowPanel(Project project) {
        super(true, true);
        this.project = project;
        this.projectService = project.getService(NovusComposeProjectService.class);
        this.tree = new Tree(new DefaultTreeModel(NovusComposeTreeModelBuilder.buildRoot(projectService.getLastState()))) {
            @Override
            public String getToolTipText(MouseEvent event) {
                TreePath path = getPathForLocation(event.getX(), event.getY());
                if (path == null) {
                    return null;
                }
                Object value = ((javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                if (NovusComposeQuickFixes.isFixableWarning(value)) {
                    return "<html>Right click for more options.<br/>" +
                            ((NovusComposeTreeModelBuilder.DiagnosticTreeNode) value).diagnosticMessage() + "</html>";
                }
                return null;
            }
        };
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new NovusComposeTreeCellRenderer());
        new TreeSpeedSearch(tree);
        ToolTipManager.sharedInstance().registerComponent(tree);
        installNavigationHandlers();

        DefaultActionGroup toolbarActions = new DefaultActionGroup();
        toolbarActions.add(new DumbAwareAction(
                "Refresh Compose View",
                "Regenerate composed interfaces and refresh the compose view",
                AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent event) {
                projectService.regenerateNow();
            }
        });

        ActionToolbar toolbar = ActionManager.getInstance()
                .createActionToolbar("NovusComposeToolWindow", toolbarActions, false);
        toolbar.setTargetComponent(tree);

        setToolbar(toolbar.getComponent());
        setContent(ScrollPaneFactory.createScrollPane(tree));

        projectService.addStateListener(this::updateState, this);
        projectService.start();
        updateState(projectService.getLastState());
    }

    public List<String> snapshotLines() {
        return NovusComposeTreeModelBuilder.flatten((javax.swing.tree.DefaultMutableTreeNode) tree.getModel().getRoot());
    }

    private void updateState(ProjectCompositionState state) {
        tree.setModel(new DefaultTreeModel(NovusComposeTreeModelBuilder.buildRoot(state)));
        TreeUtil.expandAll(tree);
    }

    private void installNavigationHandlers() {
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
                    navigateSelection();
                }
            }

            @Override
            public void mousePressed(MouseEvent event) {
                maybeShowPopup(event);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                maybeShowPopup(event);
            }
        });
        tree.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), NAVIGATE_ACTION_ID);
        tree.getActionMap().put(NAVIGATE_ACTION_ID, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                navigateSelection();
            }
        });
    }

    private void maybeShowPopup(MouseEvent event) {
        if (!event.isPopupTrigger()) {
            return;
        }
        TreePath path = tree.getPathForLocation(event.getX(), event.getY());
        if (path == null) {
            return;
        }
        tree.setSelectionPath(path);
        Object value = ((javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        if (!NovusComposeQuickFixes.isFixableWarning(value)) {
            return;
        }

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem autoFixItem = new JMenuItem("Auto Fix Warning");
        autoFixItem.addActionListener(ignored -> {
            if (NovusComposeQuickFixes.applyFix(project, value)) {
                projectService.scheduleRegeneration();
            }
        });
        popupMenu.add(autoFixItem);
        popupMenu.show(tree, event.getX(), event.getY());
    }

    private void navigateSelection() {
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null) {
            return;
        }
        Object lastPathComponent = selectionPath.getLastPathComponent();
        if (lastPathComponent instanceof javax.swing.tree.DefaultMutableTreeNode treeNode) {
            NovusComposeNavigationSupport.navigate(project, treeNode.getUserObject());
        }
    }

    @Override
    public void dispose() {
    }
}
