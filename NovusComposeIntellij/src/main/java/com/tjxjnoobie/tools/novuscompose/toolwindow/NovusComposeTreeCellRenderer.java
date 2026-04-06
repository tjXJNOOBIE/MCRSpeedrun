package com.tjxjnoobie.tools.novuscompose.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.util.ui.UIUtil;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;

public final class NovusComposeTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode treeNode
                && treeNode.getUserObject() instanceof NovusComposeTreeModelBuilder.TreeNodeValue treeNodeValue) {
            setText(treeNodeValue.label());
            setIcon(iconFor(treeNodeValue.kind()));
        }
        setOpaque(false);
        setBackgroundNonSelectionColor(null);
        setBackgroundSelectionColor(null);
        setBorderSelectionColor(null);
        setTextNonSelectionColor(UIUtil.getLabelForeground());
        setTextSelectionColor(UIUtil.getLabelForeground());

        return this;
    }

    private Icon iconFor(NovusComposeTreeModelBuilder.NodeKind kind) {
        return switch (kind) {
            case ROOT -> AllIcons.Nodes.ModuleGroup;
            case DOMAIN, DOMAIN_TARGET, DEPENDENCY -> AllIcons.Nodes.Interface;
            case METHOD -> AllIcons.Nodes.Method;
            case COLLISION_GROUP -> AllIcons.General.Warning;
            case DIAGNOSTIC_GROUP -> AllIcons.Toolwindows.Problems;
            case DIAGNOSTIC_ERROR -> AllIcons.General.Error;
            case DIAGNOSTIC_WARNING -> AllIcons.General.Warning;
            case SECTION -> AllIcons.Nodes.Folder;
        };
    }
}
