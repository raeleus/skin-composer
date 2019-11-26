package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.SceneComposerUndoable;

import java.util.Locale;

public class DialogSceneComposerModel {
    private transient Main main;
    public transient Array<SceneComposerUndoable> undoables;
    public transient Array<SceneComposerUndoable> redoables;
    public SimGroup root;
    public transient Group preview;
    private static Json json;
    
    public DialogSceneComposerModel() {
        undoables = new Array<>();
        redoables = new Array<>();
        preview = new Group();
        main = Main.main;
    
        json = new Json();
        json.setSerializer(ColorData.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, ColorData object, Class knownType) {
                json.writeValue(object.getName());
            }

            @Override
            public ColorData read(Json json, JsonValue jsonData, Class type) {
                return Main.main.getJsonData().getColorByName(jsonData.asString());
            }
        });
        json.setSerializer(DrawableData.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, DrawableData object, Class knownType) {
                json.writeValue(object.name);
            }

            @Override
            public DrawableData read(Json json, JsonValue jsonData, Class type) {
                return Main.main.getAtlasData().getDrawable(jsonData.asString());
            }
        });
        json.setSerializer(StyleData.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, StyleData object, Class knownType) {
                json.writeObjectStart();
                json.writeValue("class", object.clazz.getName());
                json.writeValue("name", object.name);
                json.writeObjectEnd();
            }

            @Override
            public StyleData read(Json json, JsonValue jsonData, Class type) {
                try {
                    return Main.main.getJsonData().findStyle(ClassReflection.forName(jsonData.getString("class")), jsonData.getString("name"));
                } catch (ReflectionException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        json.setOutputType(JsonWriter.OutputType.json);
        
        root = new SimGroup();
        assignParentRecursive(root);
    }
    
    public static void saveToJson(FileHandle saveFile) {
        if (!saveFile.extension().toLowerCase(Locale.ROOT).equals("json")) {
            saveFile = saveFile.sibling(saveFile.nameWithoutExtension() + ".json");
        }
        saveFile.writeString(json.prettyPrint(DialogSceneComposer.dialog.model), false);
    }
    
    public static void loadFromJson(FileHandle loadFile) {
        DialogSceneComposer.dialog.model = json.fromJson(DialogSceneComposerModel.class, loadFile);
    }
    
    public void undo() {
        if (undoables.size > 0) {
            var undoable = undoables.pop();
            redoables.add(undoable);
            
            undoable.undo();
        }
    }
    
    public void redo() {
        if (redoables.size > 0) {
            var undoable = redoables.pop();
            undoables.add(undoable);
    
            undoable.redo();
        }
    }
    
    public void updatePreview() {
        preview.clearChildren();
        
        for (var simActor : root.children) {
            var actor = createPreviewWidget(simActor);
            if (actor != null) {
                preview.addActor(actor);
            }
        }
//        preview.debugAll();
    }
    
    private Actor createPreviewWidget(SimActor simActor) {
        Actor actor = null;
    
        if (simActor instanceof SimTable) {
            var simTable = (SimTable) simActor;
            var table = new Table();
            actor = table;
        
            table.setName(simTable.name);
        
            if (simTable.background != null) {
                table.setBackground(main.getAtlasData().getDrawablePairs().get(simTable.background));
            }
        
            if (simTable.color != null) {
                table.setColor(simTable.color.color);
            }
            
            table.pad(simTable.padTop, simTable.padLeft, simTable.padBottom, simTable.padRight);
            
            table.align(simTable.alignment);
            
            if (simTable.parent == root) {
                table.setFillParent(true);
            }
            
            int row = 0;
            for (var simCell : simTable.cells) {
                if (simCell.row > row) {
                    table.row();
                    row = simCell.row;
                }
                
                var child = createPreviewWidget(simCell.child);
                var cell = table.add(child).fill(simCell.fillX, simCell.fillY).expand(simCell.expandX, simCell.expandY);
                cell.pad(simCell.padTop, simCell.padLeft, simCell.padBottom, simCell.padRight);
                cell.space(simCell.spaceTop, simCell.spaceLeft, simCell.spaceBottom, simCell.spaceRight);
                cell.align(simCell.alignment).uniform(simCell.uniformX, simCell.uniformY).colspan(simCell.colSpan);
    
                if (simCell.minWidth >= 0) {
                    cell.minWidth(simCell.minWidth);
                }
    
                if (simCell.minHeight >= 0) {
                    cell.minWidth(simCell.minHeight);
                }
    
                if (simCell.maxWidth >= 0) {
                    cell.maxWidth(simCell.maxWidth);
                }
    
                if (simCell.maxHeight >= 0) {
                    cell.maxWidth(simCell.maxHeight);
                }
    
                if (simCell.preferredWidth >= 0) {
                    cell.prefWidth(simCell.preferredWidth);
                }
    
                if (simCell.preferredHeight >= 0) {
                    cell.prefHeight(simCell.preferredHeight);
                }
            }
        } else if (simActor instanceof SimTextButton) {
            var simTextButton = (SimTextButton) simActor;
            if (simTextButton.style != null && simTextButton.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(TextButton.TextButtonStyle.class, simTextButton.style);
                var textButton = new TextButton(simTextButton.text == null ? "" : simTextButton.text, style);
                textButton.setChecked(simTextButton.checked);
                textButton.setDisabled(simTextButton.disabled);
                if (simTextButton.color != null) {
                    textButton.setColor(simTextButton.color.color);
                }
                textButton.pad(simTextButton.padTop, simTextButton.padLeft, simTextButton.padBottom, simTextButton.padRight);
                textButton.addListener(main.getHandListener());
                actor = textButton;
            }
        }
        
        return actor;
    }
    
    public static void assignParentRecursive(SimActor parent) {
        if (parent instanceof SimCell) {
            var child = ((SimCell) parent).child;
            child.parent = parent;
            assignParentRecursive(child);
        } else if (parent instanceof SimGroup) {
            for (var child : ((SimGroup) parent).children) {
                child.parent = parent;
                assignParentRecursive(child);
            }
        } else if (parent instanceof SimTable) {
            for (var child : ((SimTable) parent).cells) {
                child.parent = parent;
                assignParentRecursive(child);
            }
        }
    }
    
    public static class SimActor {
        public transient SimActor parent;
    }
    
    public static class SimGroup extends SimActor {
        public Array<SimActor> children = new Array<>();
    
        @Override
        public String toString() {
            return "Group";
        }
        
        public void reset() {
            children.clear();
        }
    }
    
    public static class SimTable extends SimActor {
        public Array<SimCell> cells = new Array<>();
        public String name;
        public DrawableData background;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public int alignment = Align.center;
    
        @Override
        public String toString() {
            return name == null ? "Table" : name + " (Table)";
        }
    
        public void reset() {
            cells.clear();
            name = null;
            background = null;
            color = null;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            alignment = Align.center;
        }
        
        public void sort() {
            cells.sort((o1, o2) -> {
                if (o2.row < o1.row) {
                    return 1;
                } else if (o2.row > o1.row) {
                    return -1;
                } else if (o2.column < o1.column) {
                    return 1;
                } else if (o2.column > o1.column) {
                    return -1;
                } else {
                    return 0;
                }
            });
        }
    }
    
    public static class SimCell extends SimActor {
        public SimActor child;
        public int row;
        public int column;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public float spaceLeft;
        public float spaceRight;
        public float spaceTop;
        public float spaceBottom;
        public boolean expandX;
        public boolean expandY;
        public boolean fillX;
        public boolean fillY;
        public boolean growX;
        public boolean growY;
        public int alignment = Align.center;
        public float minWidth = -1;
        public float minHeight = -1;
        public float maxWidth = -1;
        public float maxHeight = -1;
        public float preferredWidth = -1;
        public float preferredHeight = -1;
        public boolean uniformX;
        public boolean uniformY;
        public int colSpan = 1;
    
        @Override
        public String toString() {
            return "Cell (" + column + "," + row + ")";
        }
        
        public void reset() {
            child = null;
            row = 0;
            column = 0;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            spaceLeft = 0;
            spaceRight = 0;
            spaceTop = 0;
            spaceBottom = 0;
            expandX = false;
            expandY = false;
            fillX = false;
            fillY = false;
            growX = false;
            growY = false;
            alignment = Align.center;
            minWidth = -1;
            minHeight = -1;
            maxWidth = -1;
            maxHeight = -1;
            preferredWidth = -1;
            preferredHeight = -1;
            uniformX = false;
            uniformY = false;
            colSpan = 1;
        }
    }
    
    public static class SimButton extends SimActor {
    
    }
    
    public static class SimCheckBox extends SimActor {
    
    }
    
    public static class SimImage extends SimActor {
    
    }
    
    public static class SimImageButton extends SimActor {
    
    }
    
    public static class SimImageTextButton extends SimActor {
    
    }
    
    public static class SimLabel extends SimActor {
    
    }
    
    public static class SimList extends SimActor {
    
    }
    
    public static class SimProgressBar extends SimActor {
    
    }
    
    public static class SimSelectBox extends SimActor {
    
    }
    
    public static class SimSlider extends SimActor {
    
    }
    
    public static class SimTextButton extends SimActor {
        public String name;
        public String text;
        public StyleData style;
        public boolean checked;
        public boolean disabled;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
    
        public SimTextButton() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextButton.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String toString() {
            return name == null ? "TextButton" : name + " (TextButton)";
        }
        
        public void reset() {
            name = null;
            text = null;
            style = null;
            checked = false;
            disabled = false;
            color = null;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
        }
    }
    
    public static class SimTextField extends SimActor {
    
    }
    
    public static class SimTextArea extends SimActor {
    
    }
    
    public static class SimTouchPad extends SimActor {
    
    }
    
    public static class SimContainer extends SimActor {
    
    }
    
    public static class SimHorizontalGroup extends SimActor {
    
    }
    
    public static class SimScrollPane extends SimActor {
    
    }
    
    public static class SimStack extends SimActor {
    
    }
    
    public static class SimSplitPane extends SimActor {
    
    }
    
    public static class SimTree extends SimActor {
    
    }
    
    public static class SimVerticalGroup extends SimActor {
    
    }
}
