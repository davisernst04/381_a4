package com.example.a4;

import java.util.ArrayList;

public class BTIModel {
    BTNode selected;
    BTNode hoveredNode;
    BTEdge hoveredEdge;

    BTNode tempEdgeStart;
    double tempEdgeEndX;
    double tempEdgeEndY;
    boolean showingTempEdge;

    private double viewLeft = 0;
    private double viewTop = 0;
    private double viewWidth = 800;
    private double viewHeight = 600;

    private final double worldWidth = 4000;
    private final double worldHeight = 2000;

    private ArrayList<BTSubscriber> subs;
    private PSManager psManager = null;
    private String channelName = null;

    public BTIModel() {
        selected = null;
        hoveredNode = null;
        hoveredEdge = null;
        showingTempEdge = false;
        subs = new ArrayList<>();
    }

    public void setPSManager(PSManager psManager, String channelName) {
        this.psManager = psManager;
        this.channelName = channelName;
    }

    public void select(BTNode e) {
        selected = e;
        notifySubscribers();
    }

    public BTNode getSelected() {
        return selected;
    }

    public void clearSelection() {
        selected = null;
        notifySubscribers();
    }

    public void setHoveredNode(BTNode node) {
        hoveredNode = node;
        notifySubscribers();
    }

    public BTNode getHoveredNode() {
        return hoveredNode;
    }

    public void setHoveredEdge(BTEdge edge) {
        hoveredEdge = edge;
        notifySubscribers();
    }

    public BTEdge getHoveredEdge() {
        return hoveredEdge;
    }

    public void setTempEdgeStart(BTNode node) {
        tempEdgeStart = node;
        notifySubscribers();
    }

    public BTNode getTempEdgeStart() {
        return tempEdgeStart;
    }

    public void setTempEdgeEnd(double x, double y) {
        tempEdgeEndX = x;
        tempEdgeEndY = y;
        notifySubscribers();
    }

    public double getTempEdgeEndX() {
        return tempEdgeEndX;
    }

    public double getTempEdgeEndY() {
        return tempEdgeEndY;
    }

    public void setShowingTempEdge(boolean showing) {
        showingTempEdge = showing;
        notifySubscribers();
    }

    public boolean isShowingTempEdge() {
        return showingTempEdge;
    }

    public void setViewport(double left, double top, double width, double height) {
        this.viewLeft = clamp(left, 0, worldWidth - width);
        this.viewTop = clamp(top, 0, worldHeight - height);
        this.viewWidth = width;
        this.viewHeight = height;
        notifySubscribers();
    }

    public void panView(double dx, double dy) {
        viewLeft = clamp(viewLeft + dx, 0, worldWidth - viewWidth);
        viewTop = clamp(viewTop + dy, 0, worldHeight - viewHeight);
        notifySubscribers();
    }

    public double getViewLeft() {
        return viewLeft;
    }

    public double getViewTop() {
        return viewTop;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    public double getViewHeight() {
        return viewHeight;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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
}