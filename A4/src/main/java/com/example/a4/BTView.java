package com.example.a4;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

public abstract class BTView extends StackPane implements BTSubscriber {
    protected Canvas myCanvas;
    protected GraphicsContext gc;
    protected BTModel model;
    protected BTIModel iModel;

    protected double worldWidth = 4000;
    protected double worldHeight = 2000;

    public BTView(double width, double height) {
        myCanvas = new Canvas(width, height);
        gc = myCanvas.getGraphicsContext2D();
        this.getChildren().add(myCanvas);

        myCanvas.widthProperty().bind(widthProperty());
        myCanvas.heightProperty().bind(heightProperty());
    }

    public void setModel(BTModel model) {
        this.model = model;
    }

    public void setIModel(BTIModel iModel) {
        this.iModel = iModel;
    }

    @Override
    public void update() {
        draw();
    }

    protected abstract void draw();
}