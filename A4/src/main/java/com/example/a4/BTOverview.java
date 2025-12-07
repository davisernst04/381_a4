package com.example.a4;

import javafx.scene.paint.Color;
import java.util.List;

public class BTOverview extends BTView {

    public BTOverview(double width, double height) {
        super(width, height);
        setMouseTransparent(true);
    }

    @Override
    protected void draw() {
        double canvasWidth = myCanvas.getWidth();
        double canvasHeight = myCanvas.getHeight();

        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        double scaleX = canvasWidth / worldWidth;
        double scaleY = canvasHeight / worldHeight;
        double scale = Math.min(scaleX, scaleY);

        double scaledWorldWidth = worldWidth * scale;
        double scaledWorldHeight = worldHeight * scale;
        double offsetX = (canvasWidth - scaledWorldWidth) / 2;
        double offsetY = (canvasHeight - scaledWorldHeight) / 2;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(offsetX, offsetY, scaledWorldWidth, scaledWorldHeight);

        List<BTEdge> edges = model.getEdges();
        for (BTEdge edge : edges) {
            drawEdge(edge, scale, offsetX, offsetY);
        }

        List<BTNode> entities = model.getEntities();
        for (BTNode entity : entities) {
            drawNode(entity, scale, offsetX, offsetY);
        }

        drawViewRectangle(scale, offsetX, offsetY);
    }

    private double worldToOverviewX(double worldX, double scale, double offsetX) {
        return worldX * scale + offsetX;
    }

    private double worldToOverviewY(double worldY, double scale, double offsetY) {
        return worldY * scale + offsetY;
    }

    private void drawNode(BTNode node, double scale, double offsetX, double offsetY) {
        double x = worldToOverviewX(node.getX(), scale, offsetX);
        double y = worldToOverviewY(node.getY(), scale, offsetY);
        double r = node.getR() * scale;

        if (r < 2) r = 2;

        gc.setFill(Color.LIGHTBLUE);
        gc.fillOval(x - r, y - r, r * 2, r * 2);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeOval(x - r, y - r, r * 2, r * 2);
    }

    private void drawEdge(BTEdge edge, double scale, double offsetX, double offsetY) {
        BTNode parent = edge.getParent();
        BTNode child = edge.getChild();

        double x1 = worldToOverviewX(parent.getX(), scale, offsetX);
        double y1 = worldToOverviewY(parent.getY(), scale, offsetY);
        double x2 = worldToOverviewX(child.getX(), scale, offsetX);
        double y2 = worldToOverviewY(child.getY(), scale, offsetY);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(x1, y1, x2, y2);
    }

    private void drawViewRectangle(double scale, double offsetX, double offsetY) {
        double viewLeft = iModel.getViewLeft();
        double viewTop = iModel.getViewTop();
        double viewWidth = iModel.getViewWidth();
        double viewHeight = iModel.getViewHeight();

        double rectX = worldToOverviewX(viewLeft, scale, offsetX);
        double rectY = worldToOverviewY(viewTop, scale, offsetY);
        double rectWidth = viewWidth * scale;
        double rectHeight = viewHeight * scale;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(rectX, rectY, rectWidth, rectHeight);

        gc.setFill(Color.rgb(0, 0, 0, 0.1));
        gc.fillRect(rectX, rectY, rectWidth, rectHeight);
    }
}