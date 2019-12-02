package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
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
    
    public enum Interpol {
        LINEAR, SMOOTH, SMOOTH2, SMOOTHER, FADE, POW2, POW2IN, SLOW_FAST, POW2OUT, FAST_SLOW, POW2IN_INVERSE,
        POW2OUT_INVERSE, POW3, POW3IN, POW3OUT, POW3IN_INVERSE, POW3OUT_INVERSE, POW4, POW4IN, POW4OUT, POW5, POW5IN,
        POW5OUT, SINE, SINE_IN, SINE_OUT, EXP10, EXP10_IN, EXP10_OUT, EXP5, EXP5IN, EXP5OUT, CIRCLE, CIRCLE_IN,
        CIRCLE_OUT, ELASTIC, ELASTIC_IN, ELASTIC_OUT, SWING, SWING_IN, SWING_OUT, BOUNCE, BOUNCE_IN, BOUNCE_OUT
    }
    
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
        } else if (simActor instanceof SimButton) {
            var simButton = (SimButton) simActor;
            if (simButton.style != null && simButton.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(Button.ButtonStyle.class, simButton.style);
                var button = new Button(style);
                button.setChecked(simButton.checked);
                button.setDisabled(simButton.disabled);
                if (simButton.color != null) {
                    button.setColor(simButton.color.color);
                }
                button.pad(simButton.padTop, simButton.padLeft, simButton.padBottom, simButton.padRight);
                button.addListener(main.getHandListener());
                actor = button;
            }
        }  else if (simActor instanceof SimImageButton) {
            var simImageButton = (SimImageButton) simActor;
            if (simImageButton.style != null && simImageButton.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(ImageButton.ImageButtonStyle.class, simImageButton.style);
                var imageButton = new ImageButton(style);
                imageButton.setChecked(simImageButton.checked);
                imageButton.setDisabled(simImageButton.disabled);
                if (simImageButton.color != null) {
                    imageButton.setColor(simImageButton.color.color);
                }
                imageButton.pad(simImageButton.padTop, simImageButton.padLeft, simImageButton.padBottom, simImageButton.padRight);
                imageButton.addListener(main.getHandListener());
                actor = imageButton;
            }
        } else if (simActor instanceof SimImageTextButton) {
            var simImageTextButton = (SimImageTextButton) simActor;
            if (simImageTextButton.style != null && simImageTextButton.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(ImageTextButton.ImageTextButtonStyle.class, simImageTextButton.style);
                var textButton = new ImageTextButton(simImageTextButton.text == null ? "" : simImageTextButton.text, style);
                textButton.setChecked(simImageTextButton.checked);
                textButton.setDisabled(simImageTextButton.disabled);
                if (simImageTextButton.color != null) {
                    textButton.setColor(simImageTextButton.color.color);
                }
                textButton.pad(simImageTextButton.padTop, simImageTextButton.padLeft, simImageTextButton.padBottom, simImageTextButton.padRight);
                textButton.addListener(main.getHandListener());
                actor = textButton;
            }
        }
        
        return actor;
    }
    
    public static void assignParentRecursive(SimActor parent) {
        if (parent instanceof  SimSingleChild) {
            var child = ((SimSingleChild) parent).getChild();
            child.parent = parent;
            assignParentRecursive(child);
        }
        
        if (parent instanceof SimMultipleChildren) {
            for (var child : ((SimMultipleChildren) parent).getChildren()) {
                child.parent = parent;
                assignParentRecursive(child);
            }
        }
    }
    
    public static class SimActor {
        public transient SimActor parent;
    }
    
    public interface SimSingleChild {
        SimActor getChild();
    }
    
    public interface SimMultipleChildren {
        Array<? extends SimActor> getChildren();
    }
    
    public static class SimGroup extends SimActor implements SimMultipleChildren {
        public Array<SimActor> children = new Array<>();
    
        @Override
        public String toString() {
            return "Group";
        }
        
        public void reset() {
            children.clear();
        }
    
        @Override
        public Array<SimActor> getChildren() {
            return children;
        }
    }
    
    public static class SimTable extends SimActor implements SimMultipleChildren {
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
    
        @Override
        public Array<SimCell> getChildren() {
            return cells;
        }
    }
    
    public static class SimCell extends SimActor implements SimSingleChild {
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
    
        @Override
        public SimActor getChild() {
            return child;
        }
    }
    
    public static class SimButton extends SimActor {
        public String name;
        public StyleData style;
        public boolean checked;
        public boolean disabled;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
    
        public SimButton() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "Button" : name + " (Button)";
        }
    
        public void reset() {
            name = null;
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
    
    public static class SimCheckBox extends SimActor {
        public String name;
        public StyleData style;
        public boolean disabled;
        public String text;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
    
        public SimCheckBox() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "CheckBox" : name + " (CheckBox)";
        }
    
        public void reset() {
            name = null;
            style = null;
            disabled = false;
            text = null;
            color = null;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
        }
    }
    
    public static class SimImage extends SimActor {
        public String name;
        public DrawableData drawable;
        public Scaling scaling = Scaling.stretch;
    
        @Override
        public String toString() {
            return name == null ? "Image" : name + " (Image)";
        }
    
        public void reset() {
            name = null;
            drawable = null;
            scaling  = Scaling.stretch;
        }
    }
    
    public static class SimImageButton extends SimActor {
        public String name;
        public StyleData style;
        public boolean checked;
        public boolean disabled;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
    
        public SimImageButton() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(ImageButton.class);
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
            return name == null ? "ImageButton" : name + " (ImageButton)";
        }
    
        public void reset() {
            name = null;
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
    
    public static class SimImageTextButton extends SimActor {
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
    
        public SimImageTextButton() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(ImageTextButton.class);
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
            return name == null ? "ImageTextButton" : name + " (ImageTextButton)";
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
    
    public static class SimLabel extends SimActor {
        public String name;
        public StyleData style;
        public String text;
        public int textAlignment = Align.left;
        public boolean ellipsis;
        public String ellipsisString = "...";
        public boolean wrap;
        public ColorData color;
    
        public SimLabel() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "Label" : name + " (Label)";
        }
    
        public void reset() {
            name = null;
            style = null;
            text = null;
            textAlignment = Align.left;
            ellipsis = false;
            ellipsisString = "...";
            wrap = false;
            color = null;
        }
    }
    
    public static class SimList extends SimActor {
        public String name;
        public StyleData style;
        public Array<String> list = new Array<>();
    
        public SimList() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "List" : name + " (List)";
        }
    
        public void reset() {
            name = null;
            style = null;
            list.clear();
        }
    }
    
    public static class SimProgressBar extends SimActor {
        public String name;
        public StyleData style;
        public boolean disabled;
        public float value;
        public float minimum;
        public float maximum = 100;
        public float increment = 1;
        public boolean vertical;
        public float animationDuration;
        public Interpol animateInterpolation = Interpol.LINEAR;
        public boolean round;
        public Interpol visualInterpolation = Interpol.LINEAR;
    
        public SimProgressBar() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "ProgressBar" : name + " (ProgressBar)";
        }
    
        public void reset() {
            name = null;
            style = null;
            disabled = false;
            value = 0;
            minimum = 0;
            maximum = 100;
            increment = 1;
            vertical = false;
            animationDuration = 0;
            animateInterpolation = Interpol.LINEAR;
            round = true;
            visualInterpolation = Interpol.LINEAR;
        }
    }
    
    public static class SimSelectBox extends SimActor {
        public String name;
        public StyleData style;
        public boolean disabled;
        public int maxListCount;
        public Array<String> list = new Array<>();
        public int alignment = Align.center;
        public int selected;
        public boolean scrollingDisabled;
    
        public SimSelectBox() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "SelectBox" : name + " (SelectBox)";
        }
    
        public void reset() {
            name = null;
            style = null;
            disabled = false;
            maxListCount = 0;
            list.clear();
            alignment = Align.center;
            selected = 0;
            scrollingDisabled = false;
        }
    }
    
    public static class SimSlider extends SimActor {
        public String name;
        public StyleData style;
        public boolean disabled;
        public float value;
        public float minimum;
        public float maximum = 100;
        public float increment = 1;
        public boolean vertical;
        public float animationDuration;
        public Interpol animateInterpolation = Interpol.LINEAR;
        public boolean round;
        public Interpol visualInterpolation = Interpol.LINEAR;
    
        public SimSlider() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "Slider" : name + " (Slider)";
        }
    
        public void reset() {
            name = null;
            style = null;
            disabled = false;
            value = 0;
            minimum = 0;
            maximum = 100;
            increment = 1;
            vertical = false;
            animationDuration = 0;
            animateInterpolation = Interpol.LINEAR;
            round = true;
            visualInterpolation = Interpol.LINEAR;
        }
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
        public String name;
        public StyleData style;
        public String text;
        public String passwordCharacter = "*";
        public boolean passwordMode;
        public int alignment = Align.center;
        public boolean disabled;
        public int cursorPosition;
        public int selectionStart;
        public int selectionEnd;
        public boolean selectAll;
        public boolean focusTraversal;
        public int maxLength;
        public String messageText;
    
        public SimTextField() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "TextField" : name + " (TextField)";
        }
    
        public void reset() {
            name = null;
            style = null;
            text = null;
            passwordCharacter = "*";
            passwordMode = false;
            alignment = Align.center;
            disabled = false;
            cursorPosition = 0;
            selectionStart = 0;
            selectionEnd = 0;
            selectAll = false;
            focusTraversal = false;
            maxLength = 0;
            messageText = null;
        }
    }
    
    public static class SimTextArea extends SimActor {
        public String name;
        public StyleData style;
        public String text;
        public String passwordCharacter = "*";
        public boolean passwordMode;
        public int alignment = Align.center;
        public boolean disabled;
        public int cursorPosition;
        public int selectionStart;
        public int selectionEnd;
        public boolean selectAll;
        public boolean focusTraversal;
        public int maxLength;
        public String messageText;
        public int preferredRows;
    
        public SimTextArea() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "TextArea" : name + " (TextArea)";
        }
    
        public void reset() {
            name = null;
            style = null;
            text = null;
            passwordCharacter = "*";
            passwordMode = false;
            alignment = Align.center;
            disabled = false;
            cursorPosition = 0;
            selectionStart = 0;
            selectionEnd = 0;
            selectAll = false;
            focusTraversal = false;
            maxLength = 0;
            messageText = null;
            preferredRows = 0;
        }
    }
    
    public static class SimTouchPad extends SimActor {
        public String name;
        public StyleData style;
        public float deadZone;
        public boolean resetOnTouchUp = true;
    
        public SimTouchPad() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "TouchPad" : name + " (TouchPad)";
        }
    
        public void reset() {
            name = null;
            style = null;
            deadZone = 0;
            resetOnTouchUp = true;
        }
    }
    
    public static class SimContainer extends SimActor implements SimSingleChild {
        public String name;
        public int alignment = Align.center;
        public DrawableData background;
        public boolean fillX;
        public boolean fillY;
        public float minWidth = -1;
        public float minHeight = -1;
        public float maxWidth = -1;
        public float maxHeight = -1;
        public float preferredWidth = -1;
        public float preferredHeight = -1;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public SimActor child;
    
        public SimContainer() {
        
        }
    
        @Override
        public String toString() {
            return name == null ? "Container" : name + " (Container)";
        }
    
        public void reset() {
            name = null;
            alignment = Align.center;
            background = null;
            fillX = false;
            fillY = false;
            minWidth = -1;
            minHeight = -1;
            maxWidth = -1;
            maxHeight = -1;
            preferredWidth = -1;
            preferredHeight = -1;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            child = null;
        }
    
        @Override
        public SimActor getChild() {
            return child;
        }
    }
    
    public static class SimHorizontalGroup extends SimActor implements SimMultipleChildren {
        public String name;
        public int alignment = Align.center;
        public boolean expand;
        public float fill;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public boolean reverse;
        public int rowAlignment = Align.center;
        public float space;
        public boolean wrap;
        public float wrapSpace;
        public Array<SimActor> children = new Array<>();
    
        public SimHorizontalGroup() {
        }
    
        @Override
        public String toString() {
            return name == null ? "HorizontalGroup" : name + " (HorizontalGroup)";
        }
    
        public void reset() {
            name = null;
            alignment = Align.center;
            expand = false;
            fill = 0;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            reverse = false;
            rowAlignment = Align.center;
            space = 0;
            wrap = false;
            wrapSpace = 0;
            children.clear();
        }
    
        @Override
        public Array<SimActor> getChildren() {
            return children;
        }
    }
    
    public static class SimScrollPane extends SimActor implements SimSingleChild {
        public String name;
        public StyleData style;
        public boolean fadeScrollBars = true;
        public SimActor child;
        public boolean clamp;
        public boolean flickScroll = true;
        public float flingTime = 1f;
        public boolean forceScrollX;
        public boolean forceScrollY;
        public boolean overScrollX = true;
        public boolean overScrollY = true;
        public float overScrollDistance = 50;
        public float overScrollSpeedMin = 30;
        public float overScrollSpeedMax = 200;
        public boolean scrollBarBottom = true;
        public boolean scrollBarRight = true;
        public boolean scrollBarsOnTop;
        public boolean scrollBarsVisible = true;
        public boolean scrollBarTouch = true;
        public boolean scrollingDisabledX;
        public boolean scrollingDisabledY;
        public boolean smoothScrolling = true;
        public boolean variableSizeKnobs = true;
    
        public SimScrollPane() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "ScrollPane" : name + " (ScrollPane)";
        }
    
        public void reset() {
            name = null;
            style = null;
            fadeScrollBars = true;
            child = null;
            clamp = false;
            flickScroll = true;
            flingTime = 1f;
            forceScrollX = false;
            forceScrollY = false;
            overScrollX = true;
            overScrollY = true;
            overScrollDistance = 50;
            overScrollSpeedMin = 30;
            overScrollSpeedMax = 200;
            scrollBarBottom = true;
            scrollBarRight = true;
            scrollBarsOnTop = false;
            scrollBarsVisible = true;
            scrollBarTouch = true;
            scrollingDisabledX = false;
            scrollingDisabledY = false;
            smoothScrolling = true;
            variableSizeKnobs = true;
        }
    
        @Override
        public SimActor getChild() {
            return child;
        }
    }
    
    public static class SimStack extends SimActor implements SimMultipleChildren {
        public String name;
        public Array<SimActor> children = new Array<>();
    
        public SimStack() {
        
        }
    
        @Override
        public String toString() {
            return name == null ? "Stack" : name + " (Stack)";
        }
    
        public void reset() {
            name = null;
            children.clear();
        }
    
        @Override
        public Array<SimActor> getChildren() {
            return children;
        }
    }
    
    public static class SimSplitPane extends SimActor implements SimMultipleChildren {
        public String name;
        public StyleData style;
        public SimActor childFirst;
        public SimActor childSecond;
        public boolean vertical;
        public float split = .5f;
        public float splitMin;
        public float splitMax = 1;
        public transient Array<SimActor> tempChildren = new Array<>();
    
        public SimSplitPane() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "SplitPane" : name + " (SplitPane)";
        }
    
        public void reset() {
            name = null;
            style = null;
            childFirst = null;
            childSecond = null;
            vertical = false;
            split = .5f;
            splitMin = 0;
            splitMax = 1;
            tempChildren.clear();
        }
    
        @Override
        public Array<SimActor> getChildren() {
            tempChildren.clear();
            tempChildren.add(childFirst, childSecond);
            return tempChildren;
        }
    }
    
    public static class SimNode extends SimActor implements  SimSingleChild, SimMultipleChildren {
        public SimActor actor;
        public Array<SimNode> nodes = new Array<>();
        public boolean expanded;
    
        public SimNode() {
        }
    
        @Override
        public String toString() {
            return "Node";
        }
    
        public void reset() {
            actor = null;
            nodes.clear();
            expanded = false;
        }
    
        @Override
        public SimActor getChild() {
            return actor;
        }
    
        @Override
        public Array<SimNode> getChildren() {
            return nodes;
        }
    }
    
    public static class SimTree extends SimActor implements SimMultipleChildren {
        public String name;
        public StyleData style;
        public Array<SimNode> children = new Array<>();
        public float padLeft;
        public float padRight;
        public float iconSpaceLeft = 2;
        public float iconSpaceRight = 2;
        public float indentSpacing;
        public float ySpacing = 4;
    
        public SimTree() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
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
            return name == null ? "Tree" : name + " (Tree)";
        }
    
        public void reset() {
            name = null;
            style = null;
            children.clear();
            padLeft = 0;
            padRight = 0;
            iconSpaceLeft = 2;
            iconSpaceRight = 2;
            indentSpacing = 0;
            ySpacing = 4;
        }
    
        @Override
        public Array<SimNode> getChildren() {
            return children;
        }
    }
    
    public static class SimVerticalGroup extends SimActor implements SimMultipleChildren {
        public String name;
        public int alignment = Align.center;
        public boolean expand;
        public float fill;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public boolean reverse;
        public int columnAlignment = Align.center;
        public float space;
        public boolean wrap;
        public float wrapSpace;
        public Array<SimActor> children = new Array<>();
    
        public SimVerticalGroup() {
        
        }
    
        @Override
        public String toString() {
            return name == null ? "VerticalGroup" : name + " (VerticalGroup)";
        }
    
        public void reset() {
            name = null;
            alignment = Align.center;
            expand = false;
            fill = 0;
            padLeft = 0;
            padRight = 0;
            padTop = 0;
            padBottom = 0;
            reverse = false;
            columnAlignment = Align.center;
            space = 0;
            wrap = false;
            wrapSpace = 0;
            children.clear();
        }
    
        @Override
        public Array<SimActor> getChildren() {
            return children;
        }
    }
}
