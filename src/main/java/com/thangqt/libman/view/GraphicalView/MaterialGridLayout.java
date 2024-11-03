package com.thangqt.libman.view.GraphicalView;

import com.thangqt.libman.model.Material;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.function.Consumer;

public class MaterialGridLayout extends GridPane {
    private static final int DEFAULT_COLUMNS = 3;
    private static final double DEFAULT_MIN_HEIGHT = 580;
    private static final double GRID_GAP = 10;

    private final int columns;

    public MaterialGridLayout() {
        this(DEFAULT_COLUMNS);
    }

    public MaterialGridLayout(int columns) {
        this.columns = columns;
        setupGridLayout();
    }

    private void setupGridLayout() {
        setMinHeight(DEFAULT_MIN_HEIGHT);
        setHgap(GRID_GAP);
        setVgap(GRID_GAP);
        setupColumnConstraints();
    }

    private void setupColumnConstraints() {
        for (int i = 0; i < columns; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / columns);
            getColumnConstraints().add(columnConstraints);
        }
    }

    public void displayMaterials(List<Material> materials, int startIndex, int endIndex,
                                 Consumer<Material> onMaterialClick) {
        getChildren().clear();

        List<Material> displayMaterials = materials.subList(startIndex, endIndex);
        for (int i = 0; i < displayMaterials.size(); i++) {
            Material material = displayMaterials.get(i);
            int row = i / columns;
            int col = i % columns;

            MaterialTile materialTile = new MaterialTile(material);
            materialTile.setOnMouseClicked(e -> onMaterialClick.accept(material));
            add(materialTile, col, row);
        }
    }
}