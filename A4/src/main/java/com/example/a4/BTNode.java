package com.example.a4;
import java.util.ArrayList;
import java.util.List;

public class BTNode {
    private double x;
    private double y;
    private final double r;
    private BTNode parent;
    private BTNode leftChild;
    private BTNode rightChild;
    private int depth;

    public BTNode(double x, double y) {
        this.x = x;
        this.y = y;
        this.r = 20;
        this.parent = null;
        this.leftChild = null;
        this.rightChild = null;
        this.depth = 0;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getR() { return r; }

    public BTNode getParent() { return parent; }
    public int getDepth() { return depth; }

    public void setY(double y) { this.y = y; }
    public void setParent(BTNode parent) { this.parent = parent; }
    public void setDepth(int depth) { this.depth = depth; }

    public boolean contains(double mx, double my) {
        return dist(mx, my, x, y) <= r;
    }

    private double dist(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2-x1, y2-y1);
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public boolean canAddChild() {
        return leftChild == null || rightChild == null;
    }

    public void addChild(BTNode child) {
        if (leftChild == null) {
            leftChild = child;
        } else if (rightChild == null) {
            rightChild = child;
        }
        child.setParent(this);
        child.setDepth(this.depth + 1);
    }

    public void removeChild(BTNode child) {
        if (leftChild == child) leftChild = null;
        if (rightChild == child) rightChild = null;
        child.setParent(null);
        child.setDepth(0);
    }

    public List<BTNode> getChildren() {
        List<BTNode> children = new ArrayList<>();
        if (leftChild != null) children.add(leftChild);
        if (rightChild != null) children.add(rightChild);
        return children;
    }
}