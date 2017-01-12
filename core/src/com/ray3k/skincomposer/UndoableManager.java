package com.ray3k.skincomposer;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.data.StyleProperty;

public class UndoableManager {
    private final Array<Undoable> undoables;
    private int undoIndex;
    private final Main main;

    public UndoableManager(Main main) {
        undoables = new Array<>();
        undoIndex = -1;
        this.main = main;
    }
    
    public void clearUndoables() {
        undoables.clear();
        undoIndex = -1;
        
        main.getRootTable().setUndoDisabled(true);
        main.getRootTable().setRedoDisabled(true);
        
        main.getRootTable().setUndoText("Undo");
        main.getRootTable().setRedoText("Redo");
    }
    
    public void undo() {
        if (undoIndex >= 0 && undoIndex < undoables.size) {
            main.getProjectData().setChangesSaved(false);
            Undoable undoable = undoables.get(undoIndex);
            undoable.undo();
            undoIndex--;

            if (undoIndex < 0) {
                main.getRootTable().setUndoDisabled(true);
                main.getRootTable().setUndoText("Undo");
            } else {
                main.getRootTable().setUndoText("Undo " + undoables.get(undoIndex).getUndoText());
            }

            main.getRootTable().setRedoDisabled(false);
            main.getRootTable().setRedoText("Redo " + undoable.getUndoText());
        }
    }
    
    public void redo() {
        if (undoIndex >= -1 && undoIndex < undoables.size) {
            main.getProjectData().setChangesSaved(false);
            if (undoIndex < undoables.size - 1) {
                undoIndex++;
                undoables.get(undoIndex).redo();
            }

            if (undoIndex >= undoables.size - 1) {
                main.getRootTable().setRedoDisabled(true);
                main.getRootTable().setRedoText("Redo");
            } else {
                main.getRootTable().setRedoText("Redo " + undoables.get(undoIndex + 1).getUndoText());
            }

            main.getRootTable().setUndoDisabled(false);
            main.getRootTable().setUndoText("Undo " + undoables.get(undoIndex).getUndoText());
        }
    }
    
    public void addUndoable(Undoable undoable, boolean redoImmediately) {
        main.getProjectData().setChangesSaved(false);
        undoIndex++;
        if (undoIndex <= undoables.size - 1) {
            undoables.removeRange(undoIndex, undoables.size - 1);
        }
        undoables.add(undoable);
        
        if (redoImmediately) {
            undoable.redo();
        }
        
        main.getRootTable().setUndoDisabled(false);
        main.getRootTable().setRedoDisabled(true);
        main.getRootTable().setRedoText("Redo");
        main.getRootTable().setUndoText("Undo " + undoable.getUndoText());
        
        if (undoables.size > main.getProjectData().getMaxUndos()) {
            int offset = undoables.size - main.getProjectData().getMaxUndos();
            
            undoIndex -= offset;
            undoIndex = MathUtils.clamp(undoIndex, -1, undoables.size - 1);
            undoables.removeRange(0, offset - 1);
        }
    }
    
    public void addUndoable(Undoable undoable) {
        addUndoable(undoable, false);
    }
    
    public static class FloatUndoable implements Undoable {
        private final StyleProperty property;
        private final float oldValue;
        private final float newValue;
        RootTable rootTable;

        public FloatUndoable(RootTable rootTable, StyleProperty property, float newValue) {
            this.property = property;
            oldValue = (float) property.value;
            this.newValue = newValue;
            this.rootTable = rootTable;
        }
        
        @Override
        public void undo() {
            property.value = oldValue;
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public void redo() {
            property.value = newValue;
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
    
    public static class DrawableUndoable implements Undoable {
        private StyleProperty property;
        private Object oldValue, newValue;
        private RootTable rootTable;
        private AtlasData atlasData;

        public DrawableUndoable(RootTable rootTable, AtlasData atlasData, StyleProperty property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.rootTable = rootTable;
            this.atlasData = atlasData;
        }

        @Override
        public void undo() {
            rootTable.produceAtlas();
            if (oldValue == null || atlasData.getDrawable((String) oldValue) != null) {
                property.value = oldValue;
            }
//            PanelStatusBar.instance.message("Drawable selected: " + object.toString() + " for \"" + property.name + "\"");
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public void redo() {
            rootTable.produceAtlas();
            if (newValue == null || atlasData.getDrawable((String) newValue) != null) {
                property.value = newValue;
            }
//            PanelStatusBar.instance.message("Drawable selected: " + object.toString() + " for \"" + property.name + "\"");
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
        
    }
    
    public static class ColorUndoable implements Undoable {
        private StyleProperty property;
        private Object oldValue, newValue;
        private RootTable rootTable;
        private JsonData jsonData;

        public ColorUndoable(RootTable rootTable, JsonData jsonData, StyleProperty property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.rootTable = rootTable;
            this.jsonData = jsonData;
        }
        
        @Override
        public void undo() {
            if (oldValue == null) {
                property.value = oldValue;
            } else {
                for (ColorData color : jsonData.getColors()) {
                    if (color.getName().equals((String) oldValue)) {
                        property.value = oldValue;
                        break;
                    }
                }
            }
//            PanelStatusBar.instance.message("Selected color " + color.getName() + " for \"" + styleProperty.name + "\"");
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public void redo() {
            if (newValue == null) {
                property.value = newValue;
            } else {
                for (ColorData color : jsonData.getColors()) {
                    if (color.getName().equals((String) newValue)) {
                        property.value = newValue;
                        break;
                    }
                }
            }
//            PanelStatusBar.instance.message("Selected color " + color.getName() + " for \"" + styleProperty.name + "\"");
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
    
    public static class FontUndoable implements Undoable {
        private StyleProperty property;
        private Object oldValue, newValue;
        private RootTable rootTable;
        private JsonData jsonData;

        public FontUndoable(RootTable rootTable, JsonData jsonData, StyleProperty property, Object oldValue, Object newValue) {
            this.property = property;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.rootTable = rootTable;
            this.jsonData = jsonData;
        }
        
        @Override
        public void undo() {
            if (oldValue == null) {
                property.value = oldValue;
            } else {
                for (FontData font : jsonData.getFonts()) {
                    if (font.getName().equals((String) oldValue)) {
                        property.value = oldValue;
                        break;
                    }
                }
            }
//            PanelStatusBar.instance.message("Selected Font: " + font.getName() + " for \"" + styleProperty.name + "\"");
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public void redo() {
            if (newValue == null) {
                property.value = newValue;
            } else {
                for (FontData font : jsonData.getFonts()) {
                    if (font.getName().equals((String) newValue)) {
                        property.value = newValue;
                        break;
                    }
                }
            }
//            PanelStatusBar.instance.message("Selected Font: " + font.getName() + " for \"" + styleProperty.name + "\"");
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }
    
    public static class SelectBoxUndoable implements Undoable {
        private StyleProperty property;
        private SelectBox<StyleData> selectBox;
        private String oldValue, newValue;
        private RootTable rootTable;

        public SelectBoxUndoable(RootTable rootTable, StyleProperty property, SelectBox<StyleData> selectBox) {
            this.property = property;
            this.selectBox = selectBox;
            
            oldValue = (String) property.value;
            newValue = selectBox.getSelected().name;
            this.rootTable = rootTable;
        }

        @Override
        public void undo() {
            property.value = oldValue;
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public void redo() {
            property.value = newValue;
            rootTable.refreshStyleProperties(true);
            rootTable.refreshPreview();
        }

        @Override
        public String getUndoText() {
            return "Change Style Property " + property.name;
        }
    }

    public static class NewStyleUndoable implements Undoable {
        private StyleData styleData;
        private final Main main;
        private final Class selectedClass;
        private final String name;

        public NewStyleUndoable(Class selectedClass, String name, Main main) {
            this.main = main;
            this.selectedClass = selectedClass;
            this.name = name;
        }
        
        @Override
        public void undo() {
            main.getProjectData().getJsonData().deleteStyle(styleData);
            main.getRootTable().refreshStyles(true);
        }

        @Override
        public void redo() {
            styleData = main.getProjectData().getJsonData().newStyle(selectedClass, name);
            main.getRootTable().refreshStyles(true);
        }

        @Override
        public String getUndoText() {
            return "Create Style \"" + styleData.name + "\"";
        }
    }

    public static class DuplicateStyleUndoable implements Undoable {
        private StyleData styleData;
        private final Main main;
        private final String name;
        private final StyleData originalStyle;

        public DuplicateStyleUndoable(StyleData originalStyle, String name, Main main) {
            this.main = main;
            this.name = name;
            this.originalStyle = originalStyle;
        }
        
        @Override
        public void undo() {
            main.getProjectData().getJsonData().deleteStyle(styleData);
            main.getRootTable().refreshStyles(true);
        }

        @Override
        public void redo() {
            styleData = main.getProjectData().getJsonData().copyStyle(originalStyle, name);
            main.getRootTable().refreshStyles(true);
        }

        @Override
        public String getUndoText() {
            return "Duplicate Style \"" + styleData.name + "\"";
        }
    }
    
    public static class DeleteStyleUndoable implements Undoable {
        private final StyleData styleData;
        private final Main main;

        public DeleteStyleUndoable(StyleData styleData, Main main) {
            this.styleData = styleData;
            this.main = main;
        }

        @Override
        public void undo() {
            main.getProjectData().getJsonData().copyStyle(styleData, styleData.name);
            main.getRootTable().refreshStyles(true);
        }

        @Override
        public void redo() {
            main.getProjectData().getJsonData().deleteStyle(styleData);
            main.getRootTable().refreshStyles(true);
        }

        @Override
        public String getUndoText() {
            return "Delete Style \"" + styleData.name + "\"";
        }
    }
}