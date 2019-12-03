package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
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
        LINEAR(Interpolation.linear), SMOOTH(Interpolation.smooth), SMOOTH2(Interpolation.smooth2),
        SMOOTHER(Interpolation.smoother), FADE(Interpolation.fade), POW2(Interpolation.pow2),
        POW2IN(Interpolation.pow2In), SLOW_FAST(Interpolation.slowFast), POW2OUT(Interpolation.pow2Out),
        FAST_SLOW(Interpolation.fastSlow), POW2IN_INVERSE(Interpolation.pow2In),
        POW2OUT_INVERSE(Interpolation.pow2OutInverse), POW3(Interpolation.pow3), POW3IN(Interpolation.pow3In),
        POW3OUT(Interpolation.pow3Out), POW3IN_INVERSE(Interpolation.pow3InInverse),
        POW3OUT_INVERSE(Interpolation.pow3OutInverse), POW4(Interpolation.pow4), POW4IN(Interpolation.pow4In),
        POW4OUT(Interpolation.pow4Out), POW5(Interpolation.pow5), POW5IN(Interpolation.pow5In),
        POW5OUT(Interpolation.pow5Out), SINE(Interpolation.sine), SINE_IN(Interpolation.sineIn),
        SINE_OUT(Interpolation.sineOut), EXP10(Interpolation.exp10), EXP10_IN(Interpolation.exp10In),
        EXP10_OUT(Interpolation.exp10Out), EXP5(Interpolation.exp5), EXP5IN(Interpolation.exp5In),
        EXP5OUT(Interpolation.exp5Out), CIRCLE(Interpolation.circle), CIRCLE_IN(Interpolation.circleIn),
        CIRCLE_OUT(Interpolation.circleOut), ELASTIC(Interpolation.elastic), ELASTIC_IN(Interpolation.elasticIn),
        ELASTIC_OUT(Interpolation.elasticOut), SWING(Interpolation.swing), SWING_IN(Interpolation.swingIn),
        SWING_OUT(Interpolation.swingOut), BOUNCE(Interpolation.bounce), BOUNCE_IN(Interpolation.bounceIn),
        BOUNCE_OUT(Interpolation.bounceOut);
        
        Interpol(Interpolation interpolation) {
            this.interpolation = interpolation;
        }
        
        public Interpolation interpolation;
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
                textButton.setName(simTextButton.name);
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
                button.setName(simButton.name);
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
                imageButton.setName(simImageButton.name);
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
                var imageTextButton = new ImageTextButton(simImageTextButton.text == null ? "" : simImageTextButton.text, style);
                imageTextButton.setName(simImageTextButton.name);
                imageTextButton.setChecked(simImageTextButton.checked);
                imageTextButton.setDisabled(simImageTextButton.disabled);
                if (simImageTextButton.color != null) {
                    imageTextButton.setColor(simImageTextButton.color.color);
                }
                imageTextButton.pad(simImageTextButton.padTop, simImageTextButton.padLeft, simImageTextButton.padBottom, simImageTextButton.padRight);
                imageTextButton.addListener(main.getHandListener());
                actor = imageTextButton;
            }
        } else if (simActor instanceof SimCheckBox) {
            var simCheckBox = (SimCheckBox) simActor;
            if (simCheckBox.style != null && simCheckBox.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(CheckBox.CheckBoxStyle.class, simCheckBox.style);
                var checkBox = new CheckBox(simCheckBox.text == null ? "" : simCheckBox.text, style);
                checkBox.setName(simCheckBox.name);
                checkBox.setChecked(simCheckBox.checked);
                checkBox.setDisabled(simCheckBox.disabled);
                if (simCheckBox.color != null) {
                    checkBox.setColor(simCheckBox.color.color);
                }
                checkBox.pad(simCheckBox.padTop, simCheckBox.padLeft, simCheckBox.padBottom, simCheckBox.padRight);
                checkBox.addListener(main.getHandListener());
                actor = checkBox;
            }
        } else if (simActor instanceof SimImage) {
            var simImage = (SimImage) simActor;
            var image = new Image(main.getAtlasData().getDrawablePairs().get(simImage.drawable));
            image.setScaling(simImage.scaling);
            actor = image;
        } else if (simActor instanceof SimLabel) {
            var simLabel = (SimLabel) simActor;
            var style = main.getRootTable().createPreviewStyle(Label.LabelStyle.class, simLabel.style);
            var label = new Label(simLabel.text == null ? "" : simLabel.text, style);
            label.setName(simLabel.name);
            label.setAlignment(simLabel.textAlignment);
            if (simLabel.ellipsis) {
                label.setEllipsis(simLabel.ellipsisString);
            }
            label.setWrap(simLabel.wrap);
            if (simLabel.color != null) label.setColor(simLabel.color.color);
            actor = label;
        } else if (simActor instanceof SimList) {
            var simList = (SimList) simActor;
            var style = main.getRootTable().createPreviewStyle(List.ListStyle.class, simList.style);
            var list = new List<String>(style);
            list.setName(simList.name);
            list.setItems(simList.list);
            actor = list;
        } else if (simActor instanceof SimProgressBar) {
            var sim = (SimProgressBar) simActor;
            var style = main.getRootTable().createPreviewStyle(ProgressBar.ProgressBarStyle.class, sim.style);
            var progressBar = new ProgressBar(sim.minimum, sim.maximum, sim.increment, sim.vertical, style);
            progressBar.setName(sim.name);
            progressBar.setDisabled(sim.disabled);
            progressBar.setValue(sim.value);
            progressBar.setAnimateDuration(sim.animationDuration);
            progressBar.setAnimateInterpolation(sim.animateInterpolation.interpolation);
            progressBar.setRound(sim.round);
            progressBar.setVisualInterpolation(sim.visualInterpolation.interpolation);
            actor = progressBar;
        } else if (simActor instanceof SimSelectBox) {
            var sim = (SimSelectBox) simActor;
            var style = main.getRootTable().createPreviewStyle(SelectBox.SelectBoxStyle.class, sim.style);
            var selectBox = new SelectBox<String>(style);
            selectBox.setName(sim.name);
            selectBox.setDisabled(sim.disabled);
            selectBox.setMaxListCount(sim.maxListCount);
            selectBox.setItems(sim.list);
            selectBox.setAlignment(sim.alignment);
            selectBox.setSelectedIndex(sim.selected);
            selectBox.setScrollingDisabled(sim.scrollingDisabled);
            actor = selectBox;
        } else if (simActor instanceof SimSlider) {
            var sim = (SimSlider) simActor;
            var style = main.getRootTable().createPreviewStyle(Slider.SliderStyle.class, sim.style);
            var slider = new Slider(sim.minimum, sim.maximum, sim.increment, sim.vertical, style);
            slider.setName(sim.name);
            slider.setDisabled(sim.disabled);
            slider.setValue(sim.value);
            slider.setAnimateDuration(sim.animationDuration);
            slider.setAnimateInterpolation(sim.animateInterpolation.interpolation);
            slider.setRound(sim.round);
            slider.setVisualInterpolation(sim.visualInterpolation.interpolation);
            actor = slider;
        } else if (simActor instanceof SimTextField) {
            var sim = (SimTextField) simActor;
            var style = main.getRootTable().createPreviewStyle(TextField.TextFieldStyle.class, sim.style);
            var textField = new TextField(sim.text == null ? "" : sim.text, style);
            textField.setName(sim.name);
            textField.setPasswordCharacter(sim.passwordCharacter);
            textField.setPasswordMode(sim.passwordMode);
            textField.setAlignment(sim.alignment);
            textField.setDisabled(sim.disabled);
            textField.setCursorPosition(sim.cursorPosition);
            if (sim.selectAll) {
                textField.setSelection(0, textField.getText().length() - 1);
            } else {
                textField.setSelection(sim.selectionStart, sim.selectionEnd);
            }
            textField.setFocusTraversal(sim.focusTraversal);
            textField.setMaxLength(sim.maxLength);
            textField.setMessageText(sim.messageText);
            actor = textField;
        } else if (simActor instanceof SimTextArea) {
            var sim = (SimTextArea) simActor;
            var style = main.getRootTable().createPreviewStyle(TextField.TextFieldStyle.class, sim.style);
            var textArea = new TextArea(sim.text == null ? "" : sim.text, style);
            textArea.setName(sim.name);
            textArea.setPasswordCharacter(sim.passwordCharacter);
            textArea.setPasswordMode(sim.passwordMode);
            textArea.setAlignment(sim.alignment);
            textArea.setDisabled(sim.disabled);
            textArea.setCursorPosition(sim.cursorPosition);
            if (sim.selectAll) {
                textArea.setSelection(0, textArea.getText().length() - 1);
            } else {
                textArea.setSelection(sim.selectionStart, sim.selectionEnd);
            }
            textArea.setFocusTraversal(sim.focusTraversal);
            textArea.setMaxLength(sim.maxLength);
            textArea.setMessageText(sim.messageText);
            textArea.setPrefRows(sim.preferredRows);
            actor = textArea;
        } else if (simActor instanceof SimTouchPad) {
            var sim = (SimTouchPad) simActor;
            var style = main.getRootTable().createPreviewStyle(Touchpad.TouchpadStyle.class, sim.style);
            var touchPad = new Touchpad(sim.deadZone, style);
            touchPad.setResetOnTouchUp(sim.resetOnTouchUp);
            actor = touchPad;
        } else if (simActor instanceof SimContainer) {
            var sim = (SimContainer) simActor;
            var container = new Container();
            container.align(sim.alignment);
            if (sim.background != null) {
                container.setBackground(main.getAtlasData().drawablePairs.get(sim.background));
            }
            container.fill(sim.fillX, sim.fillY);
            if (sim.minWidth > 0) container.minWidth(sim.minWidth);
            if (sim.minHeight > 0) container.minHeight(sim.minHeight);
            if (sim.maxWidth > 0) container.maxWidth(sim.maxWidth);
            if (sim.maxHeight > 0) container.maxHeight(sim.maxHeight);
            if (sim.preferredWidth > 0) container.prefWidth(sim.preferredWidth);
            if (sim.preferredHeight > 0) container.prefHeight(sim.preferredHeight);
            container.padLeft(sim.padLeft);
            container.padRight(sim.padRight);
            container.padTop(sim.padTop);
            container.padBottom(sim.padBottom);
            if (sim.child != null) container.setActor(createPreviewWidget(sim.child));
            actor = container;
        } else if (simActor instanceof SimHorizontalGroup) {
            var sim = (SimHorizontalGroup) simActor;
            var horizontalGroup = new HorizontalGroup();
            horizontalGroup.align(sim.alignment);
            horizontalGroup.expand(sim.expand);
            horizontalGroup.fill(sim.fill);
            horizontalGroup.padLeft(sim.padLeft);
            horizontalGroup.padRight(sim.padRight);
            horizontalGroup.padTop(sim.padTop);
            horizontalGroup.padBottom(sim.padBottom);
            horizontalGroup.reverse(sim.reverse);
            horizontalGroup.rowAlign(sim.rowAlignment);
            horizontalGroup.space(sim.space);
            horizontalGroup.wrap(sim.wrap);
            horizontalGroup.wrapSpace(sim.wrapSpace);
            for (var child : sim.children) {
                horizontalGroup.addActor(createPreviewWidget(child));
            }
            actor = horizontalGroup;
        } else if (simActor instanceof SimScrollPane) {
            var sim = (SimScrollPane) simActor;
            var style = main.getRootTable().createPreviewStyle(ScrollPane.ScrollPaneStyle.class, sim.style);
            var scrollPane = new ScrollPane(createPreviewWidget(sim.child), style);
            scrollPane.setName(sim.name);
            scrollPane.setFadeScrollBars(sim.fadeScrollBars);
            scrollPane.setClamp(sim.clamp);
            scrollPane.setFlickScroll(sim.flickScroll);
            scrollPane.setFlingTime(sim.flingTime);
            scrollPane.setForceScroll(sim.forceScrollX, sim.forceScrollY);
            scrollPane.setOverscroll(sim.overScrollX, sim.overScrollY);
            scrollPane.setupOverscroll(sim.overScrollDistance, sim.overScrollSpeedMin, sim.overScrollSpeedMax);
            scrollPane.setScrollBarPositions(sim.scrollBarBottom, sim.scrollBarRight);
            scrollPane.setScrollbarsOnTop(sim.scrollBarsOnTop);
            scrollPane.setScrollbarsVisible(sim.scrollBarsVisible);
            scrollPane.setScrollBarTouch(sim.scrollBarTouch);
            scrollPane.setScrollingDisabled(sim.scrollingDisabledX, sim.scrollingDisabledY);
            scrollPane.setSmoothScrolling(sim.smoothScrolling);
            scrollPane.setVariableSizeKnobs(sim.variableSizeKnobs);
            actor = scrollPane;
        } else if (simActor instanceof SimStack) {
            var sim = (SimStack) simActor;
            var stack = new Stack();
            stack.setName(sim.name);
            for (var child : sim.children) {
                stack.add(createPreviewWidget(child));
            }
            actor = stack;
        } else if (simActor instanceof SimSplitPane) {
            var sim = (SimSplitPane) simActor;
            var style = main.getRootTable().createPreviewStyle(SplitPane.SplitPaneStyle.class, sim.style);
            var splitPane = new SplitPane(createPreviewWidget(sim.childFirst), createPreviewWidget(sim.childSecond), sim.vertical, style);
            splitPane.setName(sim.name);
            splitPane.setSplitAmount(sim.split);
            splitPane.setMinSplitAmount(sim.splitMin);
            splitPane.setMaxSplitAmount(sim.splitMax);
        } else if (simActor instanceof SimTree) {
            var sim = (SimTree) simActor;
            var style = main.getRootTable().createPreviewStyle(Tree.TreeStyle.class, sim.style);
            var tree = new Tree(style);
            tree.setName(sim.name);
            tree.setPadding(sim.padLeft, sim.padRight);
            tree.setIconSpacing(sim.iconSpaceLeft, sim.iconSpaceRight);
            tree.setIndentSpacing(sim.indentSpacing);
            tree.setYSpacing(sim.ySpacing);
            for (var child : sim.children) {
                tree.add(createPreviewNode(child));
            }
        } else if (simActor instanceof SimVerticalGroup) {
            var sim = (SimVerticalGroup) simActor;
            var verticalGroup = new VerticalGroup();
            verticalGroup.align(sim.alignment);
            verticalGroup.expand(sim.expand);
            verticalGroup.fill(sim.fill);
            verticalGroup.padLeft(sim.padLeft);
            verticalGroup.padRight(sim.padRight);
            verticalGroup.padTop(sim.padTop);
            verticalGroup.padBottom(sim.padBottom);
            verticalGroup.reverse(sim.reverse);
            verticalGroup.columnAlign(sim.columnAlignment);
            verticalGroup.space(sim.space);
            verticalGroup.wrap(sim.wrap);
            verticalGroup.wrapSpace(sim.wrapSpace);
            for (var child : sim.children) {
                verticalGroup.addActor(createPreviewWidget(child));
            }
            actor = verticalGroup;
        }
        
        return actor;
    }
    
    public Tree.Node createPreviewNode(SimNode simNode) {
        var node = new GenericNode();
        node.setActor(createPreviewWidget(simNode.actor));
        node.setIcon(main.getAtlasData().drawablePairs.get(simNode.icon));
        node.setSelectable(simNode.selectable);
        for (var child : simNode.nodes) {
            node.add(createPreviewNode(child));
        }
        return node;
    }
    
    public class GenericNode extends Tree.Node {
    
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
        public boolean checked;
    
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
            checked = false;
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
        public char passwordCharacter = '*';
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
            passwordCharacter = '*';
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
        public char passwordCharacter = '*';
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
            passwordCharacter = '*';
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
        public DrawableData icon;
        public boolean selectable = true;
    
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
            icon = null;
            selectable = true;
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
