package com.example.a4;

import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class MainUI extends StackPane {
    public MainUI() {
        BTController controller = new BTController();
        BTDetailView detailView = new BTDetailView(800, 600);
        BTOverview overview = new BTOverview(800, 600);
        BTModel model = new BTModel();
        BTIModel iModel = new BTIModel();

        PSManager psManager = new PSManager();
        model.setPSManager(psManager, "model");
        iModel.setPSManager(psManager, "iModel");

        detailView.setIModel(iModel);
        detailView.setModel(model);
        overview.setIModel(iModel);
        overview.setModel(model);

        detailView.setupEvents(controller);
        controller.setModel(model);
        controller.setIModel(iModel);

        model.addSubscriber(detailView);
        iModel.addSubscriber(detailView);
        model.addSubscriber(overview);
        iModel.addSubscriber(overview);

        this.getChildren().addAll(detailView, overview);

        Platform.runLater(() -> {
            double width = getWidth();
            double height = getHeight();
            if (width > 0 && height > 0) {
                iModel.setViewport(0, 0, width, height);
            } else {
                iModel.setViewport(0, 0, 800, 600);
            }
            detailView.update();
            overview.update();
        });

        widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                iModel.setViewport(iModel.getViewLeft(), iModel.getViewTop(),
                        newVal.doubleValue(), iModel.getViewHeight());
            }
        });

        heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                iModel.setViewport(iModel.getViewLeft(), iModel.getViewTop(),
                        iModel.getViewWidth(), newVal.doubleValue());
            }
        });
    }
}