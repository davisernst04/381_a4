package com.example.a4;

import javafx.scene.paint.Color;
import javafx.geometry.VPos;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import java.util.List;

public class BTDetailView extends BTView {

    public BTDetailView(double width, double height) {
        super(width, height);
    }

    public void setupEvents(BTController controller) {
        myCanvas.setOnMousePressed(controller::handleMousePressed);
        myCanvas.setOnMouseDragged(controller::handleMouseDragged);
        myCanvas.setOnMouseReleased(controller::handleMouseReleased);
        myCanvas.setOnMouseMoved(controller::handleMouseMoved);

        myCanvas.setFocusTraversable(true);
        myCanvas.setOnKeyPressed(controller::handleKeyPressed);
        myCanvas.requestFocus();

        myCanvas.setOnMouseClicked(e -> myCanvas.requestFocus());
    }

    @Override
    protected void draw() {
        double canvasWidth = myCanvas.getWidth();
        double canvasHeight = myCanvas.getHeight();

        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        double viewLeft = iModel.getViewLeft();
        double viewTop = iModel.getViewTop();
        double viewWidth = canvasWidth;
        double viewHeight = canvasHeight;

        List<BTEdge> edges = model.getEdges();
        for (BTEdge edge : edges) {
            if (isEdgeInView(edge, viewLeft, viewTop, viewWidth, viewHeight)) {
                boolean isHovered = (edge == iModel.getHoveredEdge());
                drawEdge(edge, isHovered, viewLeft, viewTop);
            }
        }

        if (iModel.isShowingTempEdge()) {
            BTNode start = iModel.getTempEdgeStart();
            double startX = worldToScreenX(start.getX(), viewLeft);
            double startY = worldToScreenY(start.getY(), viewTop);
            double endX = worldToScreenX(iModel.getTempEdgeEndX(), viewLeft);
            double endY = worldToScreenY(iModel.getTempEdgeEndY(), viewTop);

            gc.setStroke(Color.GRAY);
            gc.setLineWidth(2);
            gc.strokeLine(startX, startY, endX, endY);
            gc.setLineDashes(0);
        }

        List<BTNode> entities = model.getEntities();
        for (BTNode entity : entities) {
            if (isNodeInView(entity, viewLeft, viewTop, viewWidth, viewHeight)) {
                boolean isSelected = (entity == iModel.getSelected());
                boolean isHovered = (entity == iModel.getHoveredNode());
                drawNode(entity, isSelected, isHovered, viewLeft, viewTop);
            }
        }
    }

    private boolean isNodeInView(BTNode node, double viewLeft, double viewTop, double viewWidth, double viewHeight) {
        double x = node.getX();
        double y = node.getY();
        double r = node.getR();
        return x + r >= viewLeft && x - r <= viewLeft + viewWidth &&
                y + r >= viewTop && y - r <= viewTop + viewHeight;
    }

    private boolean isEdgeInView(BTEdge edge, double viewLeft, double viewTop, double viewWidth, double viewHeight) {
        BTNode parent = edge.getParent();
        BTNode child = edge.getChild();
        return isNodeInView(parent, viewLeft, viewTop, viewWidth, viewHeight) ||
                isNodeInView(child, viewLeft, viewTop, viewWidth, viewHeight);
    }

    private double worldToScreenX(double worldX, double viewLeft) {
        return worldX - viewLeft;
    }

    private double worldToScreenY(double worldY, double viewTop) {
        return worldY - viewTop;
    }

    private void drawNode(BTNode node, boolean isSelected, boolean isHovered, double viewLeft, double viewTop) {
        double x = worldToScreenX(node.getX(), viewLeft);
        double y = worldToScreenY(node.getY(), viewTop);
        double r = node.getR();

        if (isSelected) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.LIGHTBLUE);
        }
        gc.fillOval(x - r, y - r, r * 2, r * 2);

        if (isHovered) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
        } else {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
        }
        gc.strokeOval(x - r, y - r, r * 2, r * 2);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(8));
        VPos oldVPos = gc.getTextBaseline();
        TextAlignment oldAlign = gc.getTextAlign();
        gc.setTextBaseline(VPos.CENTER);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("Name", x, y);
        gc.setTextBaseline(oldVPos);
        gc.setTextAlign(oldAlign);
    }

    private void drawEdge(BTEdge edge, boolean isHovered, double viewLeft, double viewTop) {
        BTNode parent = edge.getParent();
        BTNode child = edge.getChild();

        double x1 = worldToScreenX(parent.getX(), viewLeft);
        double y1 = worldToScreenY(parent.getY(), viewTop);
        double x2 = worldToScreenX(child.getX(), viewLeft);
        double y2 = worldToScreenY(child.getY(), viewTop);

        if (isHovered) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
        } else {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
        }

        gc.strokeLine(x1, y1, x2, y2);
    }
}