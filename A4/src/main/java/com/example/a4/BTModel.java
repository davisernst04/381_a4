package com.example.a4;

import java.util.ArrayList;
import java.util.List;

public class BTModel {
    private final ArrayList<BTSubscriber> subs;
    private final ArrayList<BTNode> entities;
    private final ArrayList<BTEdge> edges;

    private PSManager psManager = null;
    private String channelName = null;

    public BTModel() {
        subs = new ArrayList<>();
        entities = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void setPSManager(PSManager psManager, String channelName) {
        this.psManager = psManager;
        this.channelName = channelName;
    }

    public void createEntity(double x, double y) {
        BTNode e = new BTNode(x, y);
        entities.add(e);
        notifySubscribers();
    }

    public void deleteEntity(BTNode e) {
        if (e.getParent() != null) {
            e.getParent().removeChild(e);
        }

        for (BTNode child : e.getChildren()) {
            child.setParent(null);
            child.setDepth(0);
        }

        edges.removeIf(edge -> edge.getParent() == e || edge.getChild() == e);

        entities.remove(e);
        notifySubscribers();
    }

    public void deleteEdge(BTEdge edge) {
        BTNode parent = edge.getParent();
        BTNode child = edge.getChild();

        parent.removeChild(child);
        edges.remove(edge);
        notifySubscribers();
    }

    public void addSubscriber(BTSubscriber sub) {
        if (psManager != null && channelName != null) {
            psManager.addSubscriber(channelName, sub);
        } else {
            subs.add(sub);
        }
    }

    private void notifySubscribers() {
        if (psManager != null && channelName != null) {
            psManager.notifyChannel(channelName);
        } else {
            subs.forEach(BTSubscriber::update);
        }
    }

    public boolean contains(double x, double y) {
        for (BTNode entity : entities) {
            if (entity.contains(x, y)) return true;
        }
        return false;
    }

    public BTNode whichEntity(double x, double y) {
        for (BTNode entity : entities) {
            if (entity.contains(x, y)) return entity;
        }
        return null;
    }

    public BTEdge whichEdge(double x, double y, double tolerance) {
        for (BTEdge edge : edges) {
            if (distanceToEdge(edge, x, y) <= tolerance) {
                return edge;
            }
        }
        return null;
    }

    private double distanceToEdge(BTEdge edge, double x, double y) {
        double x1 = edge.getParent().getX();
        double y1 = edge.getParent().getY();
        double x2 = edge.getChild().getX();
        double y2 = edge.getChild().getY();

        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = (lenSq != 0) ? dot / lenSq : -1;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void move(BTNode selected, double dX, double dY) {
        if (selected.getDepth() == 0) {
            selected.move(dX, dY);
            moveSubtree(selected, dX, dY);
        } else {
            selected.move(dX, 0);
            moveSubtree(selected, dX, 0);
        }
        notifySubscribers();
    }

    private void moveSubtree(BTNode node, double dX, double dY) {
        for (BTNode child : node.getChildren()) {
            child.move(dX, dY);
            moveSubtree(child, dX, dY);
        }
    }

    public boolean canCreateEdge(BTNode parent, BTNode child) {
        if (parent == null || child == null) return false;
        if (parent == child) return false;
        if (child.getParent() != null) return false;
        if (!parent.canAddChild()) return false;
        if (wouldCreateCycle(parent, child)) return false;
        return true;
    }

    private boolean wouldCreateCycle(BTNode parent, BTNode child) {
        return isDescendant(child, parent);
    }

    private boolean isDescendant(BTNode ancestor, BTNode node) {
        if (ancestor == node) return true;
        if (node == null) return false;
        return isDescendant(ancestor, node.getParent());
    }

    public void createEdge(BTNode parent, BTNode child) {
        parent.addChild(child);

        child.setY(parent.getY() + 100);

        BTEdge edge = new BTEdge(parent, child);
        edges.add(edge);

        updateSubtreeDepths(child);

        notifySubscribers();
    }

    private void updateSubtreeDepths(BTNode node) {
        for (BTNode child : node.getChildren()) {
            child.setDepth(node.getDepth() + 1);
            updateSubtreeDepths(child);
        }
    }

    public List<BTNode> getEntities() {
        return entities;
    }

    public List<BTEdge> getEdges() {
        return edges;
    }
}