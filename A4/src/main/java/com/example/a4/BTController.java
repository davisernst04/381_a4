package com.example.a4;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class BTController {
    private BTModel model;
    private BTIModel iModel;
    private InteractionState state = new ReadyState();
    private double prevX, prevY, dX, dY;

    private final double EDGE_TOLERANCE = 5;

    public void setModel(BTModel m) {
        model = m;
    }

    public void setIModel(BTIModel im) {
        iModel = im;
    }

    private double screenToWorldX(double screenX) {
        return screenX + iModel.getViewLeft();
    }

    private double screenToWorldY(double screenY) {
        return screenY + iModel.getViewTop();
    }

    private interface InteractionState {
        void onMousePressed(MouseEvent event);
        void onMouseDragged(MouseEvent event);
        void onMouseReleased(MouseEvent event);
        void onMouseMoved(MouseEvent event);
        void onKeyPressed(KeyEvent event);
    }

    private class ReadyState implements InteractionState {
        private double pressX, pressY;
        private boolean pressedOnBackground;

        @Override
        public void onMousePressed(MouseEvent event) {
            pressX = event.getX();
            pressY = event.getY();

            double worldX = screenToWorldX(event.getX());
            double worldY = screenToWorldY(event.getY());

            BTNode node = model.whichEntity(worldX, worldY);

            if (event.isShiftDown() && node != null) {
                iModel.setTempEdgeStart(node);
                iModel.setTempEdgeEnd(worldX, worldY);
                iModel.setShowingTempEdge(true);
                state = new CreatingEdgeState();
                pressedOnBackground = false;
            } else if (node != null) {
                iModel.select(node);
                prevX = event.getX();
                prevY = event.getY();
                state = new DraggingState();
                pressedOnBackground = false;
            } else {
                pressedOnBackground = true;
            }
        }

        @Override
        public void onMouseDragged(MouseEvent event) {
            if (pressedOnBackground) {
                prevX = pressX;
                prevY = pressY;
                state = new PanningState();
                state.onMouseDragged(event);
            }
        }

        @Override
        public void onMouseReleased(MouseEvent event) {
            if (pressedOnBackground) {
                double worldX = screenToWorldX(event.getX());
                double worldY = screenToWorldY(event.getY());

                BTNode node = model.whichEntity(worldX, worldY);

                if (node != null) {
                    iModel.select(node);
                } else if (Math.abs(event.getX() - pressX) < 5 && Math.abs(event.getY() - pressY) < 5) {
                    model.createEntity(worldX, worldY);
                    BTNode newNode = model.whichEntity(worldX, worldY);
                    if (newNode != null) {
                        iModel.select(newNode);
                    }
                }
            }
            pressedOnBackground = false;
        }

        @Override
        public void onMouseMoved(MouseEvent event) {
            double worldX = screenToWorldX(event.getX());
            double worldY = screenToWorldY(event.getY());

            BTNode node = model.whichEntity(worldX, worldY);
            BTEdge edge = (node == null) ? model.whichEdge(worldX, worldY, EDGE_TOLERANCE) : null;

            iModel.setHoveredNode(node);
            iModel.setHoveredEdge(edge);
        }

        @Override
        public void onKeyPressed(KeyEvent event) {
            if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
                if (iModel.getHoveredNode() != null) {
                    model.deleteEntity(iModel.getHoveredNode());
                    iModel.setHoveredNode(null);
                    iModel.clearSelection();
                } else if (iModel.getHoveredEdge() != null) {
                    model.deleteEdge(iModel.getHoveredEdge());
                    iModel.setHoveredEdge(null);
                }
            }
        }
    }

    private class DraggingState implements InteractionState {
        @Override
        public void onMousePressed(MouseEvent event) {
        }

        @Override
        public void onMouseDragged(MouseEvent event) {
            dX = event.getX() - prevX;
            dY = event.getY() - prevY;
            prevX = event.getX();
            prevY = event.getY();
            model.move(iModel.getSelected(), dX, dY);
        }

        @Override
        public void onMouseReleased(MouseEvent event) {
            iModel.clearSelection();
            state = new ReadyState();
        }

        @Override
        public void onMouseMoved(MouseEvent event) {
        }

        @Override
        public void onKeyPressed(KeyEvent event) {
        }
    }

    private class CreatingEdgeState implements InteractionState {
        @Override
        public void onMousePressed(MouseEvent event) {
        }

        @Override
        public void onMouseDragged(MouseEvent event) {
            double worldX = screenToWorldX(event.getX());
            double worldY = screenToWorldY(event.getY());

            iModel.setTempEdgeEnd(worldX, worldY);
        }

        @Override
        public void onMouseReleased(MouseEvent event) {
            double worldX = screenToWorldX(event.getX());
            double worldY = screenToWorldY(event.getY());

            BTNode parent = iModel.getTempEdgeStart();
            BTNode child = model.whichEntity(worldX, worldY);

            if (child != null && model.canCreateEdge(parent, child)) {
                model.createEdge(parent, child);
            }

            iModel.setShowingTempEdge(false);
            state = new ReadyState();
        }

        @Override
        public void onMouseMoved(MouseEvent event) {
        }

        @Override
        public void onKeyPressed(KeyEvent event) {
        }
    }

    private class PanningState implements InteractionState {
        @Override
        public void onMousePressed(MouseEvent event) {
        }

        @Override
        public void onMouseDragged(MouseEvent event) {
            dX = event.getX() - prevX;
            dY = event.getY() - prevY;
            prevX = event.getX();
            prevY = event.getY();

            iModel.panView(-dX, -dY);
        }

        @Override
        public void onMouseReleased(MouseEvent event) {
            state = new ReadyState();
        }

        @Override
        public void onMouseMoved(MouseEvent event) {
        }

        @Override
        public void onKeyPressed(KeyEvent event) {
        }
    }

    public void handleMousePressed(MouseEvent event) {
        state.onMousePressed(event);
    }

    public void handleMouseDragged(MouseEvent event) {
        state.onMouseDragged(event);
    }

    public void handleMouseReleased(MouseEvent event) {
        state.onMouseReleased(event);
    }

    public void handleMouseMoved(MouseEvent event) {
        state.onMouseMoved(event);
    }

    public void handleKeyPressed(KeyEvent event) {
        state.onKeyPressed(event);
    }
}