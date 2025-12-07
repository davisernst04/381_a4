package com.example.a4;

public class BTEdge {
    private BTNode parent;
    private BTNode child;

    public BTEdge(BTNode parent, BTNode child) {
        this.parent = parent;
        this.child = child;
    }

    public BTNode getParent() { return parent; }
    public BTNode getChild() { return child; }
}