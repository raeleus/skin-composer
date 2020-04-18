package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer.View;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.SceneComposerUndoable;

import javax.xml.crypto.Data;
import java.util.Locale;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer.dialog;

public class DialogSceneComposerModel {
    private transient Main main;
    public transient Array<SceneComposerUndoable> undoables;
    public transient Array<SceneComposerUndoable> redoables;
    public static SimRootGroup rootActor;
    public transient Stack preview;
    private static Json json;
    private final static Vector2 temp = new Vector2();
    
    public enum Interpol {
        LINEAR(Interpolation.linear, "Linear", "linear"), SMOOTH(Interpolation.smooth, "Smooth", "smooth"), SMOOTH2(
                Interpolation.smooth2, "Smooth 2", "smooth2"),
        SMOOTHER(Interpolation.smoother, "Smoother", "smoother"), FADE(Interpolation.fade, "Fade", "fade"), POW2(
                Interpolation.pow2, "Pow 2", "pow2"),
        POW2IN(Interpolation.pow2In, "Pow 2 In", "pow2In"), SLOW_FAST(Interpolation.slowFast, "Slow Fast",
                "slowFast"), POW2OUT(Interpolation.pow2Out, "Pow 2 Out", "pow2Out"),
        FAST_SLOW(Interpolation.fastSlow, "Fast Slow", "fastSlow"), POW2IN_INVERSE(Interpolation.pow2In,
                "Pow 2 In Inverse", "pow2In"),
        POW2OUT_INVERSE(Interpolation.pow2OutInverse, "Pow 2 Out Inverse", "pow2OutInverse"), POW3(Interpolation.pow3,
                "Pow 3", "pow3"), POW3IN(Interpolation.pow3In, "Pow 3 In", "pow3In"),
        POW3OUT(Interpolation.pow3Out, "Pow 3 Out", "pow3Out"), POW3IN_INVERSE(Interpolation.pow3InInverse,
                "Pow 3 In Inverse", "pow3InInverse"),
        POW3OUT_INVERSE(Interpolation.pow3OutInverse, "Pow 3 Out Inverse", "pow3OutInverse"), POW4(Interpolation.pow4,
                "Pow 4", "pow4"), POW4IN(Interpolation.pow4In, "Pow 4 In", "pow4In"),
        POW4OUT(Interpolation.pow4Out, "Pow 4 Out", "pow4Out"), POW5(Interpolation.pow5, "Pow 5", "pow5"), POW5IN(
                Interpolation.pow5In, "Pow 5 In", "pow5In"),
        POW5OUT(Interpolation.pow5Out, "Pow 5 Out", "pow5Out"), SINE(Interpolation.sine, "Sine", "sine"), SINE_IN(
                Interpolation.sineIn, "Sine In", "sineIn"),
        SINE_OUT(Interpolation.sineOut, "Sine Out", "sineOut"), EXP10(Interpolation.exp10, "Exp 10", "exp10"), EXP10_IN(
                Interpolation.exp10In, "Exp 10 In", "exp10In"),
        EXP10_OUT(Interpolation.exp10Out, "Exp 10 Out", "exp10Out"), EXP5(Interpolation.exp5, "Exp 5", "exp5"), EXP5IN(
                Interpolation.exp5In, "Exp 5 In", "exp5In"),
        EXP5OUT(Interpolation.exp5Out, "Exp 5 Out", "exp5Out"), CIRCLE(Interpolation.circle, "Circle",
                "circle"), CIRCLE_IN(Interpolation.circleIn, "Circle In", "circleIn"),
        CIRCLE_OUT(Interpolation.circleOut, "Circle Out", "circleOut"), ELASTIC(Interpolation.elastic, "Elastic",
                "elastic"), ELASTIC_IN(Interpolation.elasticIn, "Elastic In", "elasticIn"),
        ELASTIC_OUT(Interpolation.elasticOut, "Elastic Out", "elasticOut"), SWING(Interpolation.swing, "Swing",
                "swing"), SWING_IN(Interpolation.swingIn, "Swing In", "swingIn"),
        SWING_OUT(Interpolation.swingOut, "Swing Out", "swingOut"), BOUNCE(Interpolation.bounce, "Bounce",
                "bounce"), BOUNCE_IN(Interpolation.bounceIn, "Bounce In", "bounceIn"),
        BOUNCE_OUT(Interpolation.bounceOut, " Bounce Out", "bounceOut");
        
        public Interpolation interpolation;
        public String text;
        public String code;
        
        Interpol(Interpolation interpolation, String text, String code) {
            this.interpolation = interpolation;
            this.text = text;
            this.code = code;
        }
    
        @Override
        public String toString() {
            return text;
        }
    }
    
    public DialogSceneComposerModel() {
        undoables = new Array<>();
        redoables = new Array<>();
        preview = new Stack();
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
                json.writeValue("clazz", object.clazz.getName());
                json.writeValue("name", object.name);
                json.writeObjectEnd();
            }

            @Override
            public StyleData read(Json json, JsonValue jsonData, Class type) {
                try {
                    return Main.main.getJsonData().findStyle(ClassReflection.forName(jsonData.getString("clazz")), jsonData.getString("name"));
                } catch (ReflectionException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        });
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        json.addClassTag("Actor", SimActor.class);
        json.addClassTag("Button", SimButton.class);
        json.addClassTag("Cell", SimCell.class);
        json.addClassTag("CheckBox", SimCheckBox.class);
        json.addClassTag("Container", SimContainer.class);
        json.addClassTag("HorizontalGroup", SimHorizontalGroup.class);
        json.addClassTag("Image", SimImage.class);
        json.addClassTag("ImageButton", SimImageButton.class);
        json.addClassTag("ImageTextButton", SimImageTextButton.class);
        json.addClassTag("Label", SimLabel.class);
        json.addClassTag("List", SimList.class);
        json.addClassTag("Node", SimNode.class);
        json.addClassTag("ProgressBar", SimProgressBar.class);
        json.addClassTag("Root", SimRootGroup.class);
        json.addClassTag("ScrollPane", SimScrollPane.class);
        json.addClassTag("SelectBox", SimSelectBox.class);
        json.addClassTag("Slider", SimSlider.class);
        json.addClassTag("SplitPane", SimSplitPane.class);
        json.addClassTag("Stack", SimStack.class);
        json.addClassTag("Table", SimTable.class);
        json.addClassTag("TextArea", SimTextArea.class);
        json.addClassTag("TextButton", SimTextButton.class);
        json.addClassTag("TextField", SimTextField.class);
        json.addClassTag("TouchPad", SimTouchPad.class);
        json.addClassTag("Tree", SimTree.class);
        json.addClassTag("VerticalGroup", SimVerticalGroup.class);
        
        if (rootActor == null) rootActor = new SimRootGroup();
        assignParentRecursive(rootActor);
        primeStyles();
    }
    
    private void primeStyles() {
        primeStyles(rootActor);
    }
    
    private void primeStyles(SimActor simActor) {
        for (var field : ClassReflection.getFields(simActor.getClass())) {
            if (field.getType() == StyleData.class) {
                try {
                    var style = (StyleData) field.get(simActor);
                    var foundStyle = main.getJsonData().findStyle(style.clazz, style.name);
                    if (foundStyle == null) foundStyle = main.getJsonData().findStyle(style.clazz, "default");
                    if (foundStyle == null) foundStyle = main.getJsonData().findStyle(style.clazz, "default-horizontal");
                    field.set(simActor, foundStyle);
                } catch (ReflectionException e) {
                    e.printStackTrace(System.out);
                }
            } else if (field.getType() == DrawableData.class) {
                try {
                    var drawable = (DrawableData) field.get(simActor);
                    var foundDrawable = main.getAtlasData().getDrawable(drawable.name);
                    field.set(simActor, foundDrawable);
                } catch (ReflectionException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        
        if (simActor instanceof SimMultipleChildren) {
            for (var child : ((SimMultipleChildren) simActor).getChildren()) {
                if (child != null) primeStyles(child);
            }
        }
        
        if (simActor instanceof SimSingleChild) {
            var child = ((SimSingleChild) simActor).getChild();
            if (child != null) primeStyles(child);
        }
    }
    
    public static void saveToJson(FileHandle saveFile) {
        if (!saveFile.extension().toLowerCase(Locale.ROOT).equals("json")) {
            saveFile = saveFile.sibling(saveFile.nameWithoutExtension() + ".json");
        }
        saveFile.writeString(json.prettyPrint(rootActor), false);
    }
    
    public static void loadFromJson(FileHandle loadFile) {
        rootActor = json.fromJson(SimRootGroup.class, loadFile);
        assignParentRecursive(rootActor);
    }
    
    public void undo() {
        if (undoables.size > 0) {
            var undoable = undoables.pop();
            redoables.add(undoable);
            
            undoable.undo();
            var fadeLabel = new FadeLabel(undoable.getUndoString(), main.getSkin(), "scene-edit-tip");
            temp.set(dialog.previewTable.getWidth() / 2, dialog.previewTable.getHeight() / 2);
            dialog.previewTable.localToStageCoordinates(temp);
            fadeLabel.setPosition(temp.x - (int) fadeLabel.getWidth() / 2, temp.y - (int) fadeLabel.getHeight() / 2);
            main.getStage().addActor(fadeLabel);
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
        
        switch (dialog.view) {
            case LIVE:
                createPreviewWidgets();
                preview.setDebug(false, true);
                break;
            case EDIT:
                createPreviewWidgets();
                createEditWidgets();
                preview.setDebug(false, true);
                break;
            case OUTLINE:
                createPreviewWidgets();
                preview.debugAll();
                break;
        }
    }
    
    private void createEditWidgets() {
        if (dialog.simActor.parent != null) {
            var edit = new EditWidget(main.getSkin(), "scene-select-back");
            edit.setFillParent(true);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
        }
        
        if (dialog.simActor instanceof SimRootGroup) {
            var simGroup = (SimRootGroup) dialog.simActor;
            var edit = new EditWidget(main.getSkin(), "scene-selection");
            edit.setFillParent(true);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
    
            if (simGroup.children.size > 0) {
                edit = new EditWidget(main.getSkin(), "scene-selector");
                edit.setFillParent(true);
                edit.setSimActorTarget(simGroup.children.peek());
                preview.add(edit);
            }
        } else if (dialog.simActor instanceof SimTable) {
            var edit = new EditWidget(main.getSkin(), "scene-selection");
            edit.setFollowActor(dialog.simActor.previewActor);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
            
            var simTable = (SimTable) dialog.simActor;
            for (var simCell : simTable.cells) {
                var table = (Table) simTable.previewActor;
                var cell = table.getCells().get(simCell.row * table.getColumns() + simCell.column);
                
                edit = new EditWidget(main.getSkin(), "scene-selector");
                edit.setCell(cell);
                edit.setSimActorTarget(simCell);
                preview.add(edit);
            }
        } else if (dialog.simActor instanceof SimCell) {
            var simCell = (SimCell) dialog.simActor;
            var table = (Table) ((SimTable) simCell.parent).previewActor;
            var cell = table.getCells().get(simCell.row * table.getColumns() + simCell.column);
            
            var edit = new EditWidget(main.getSkin(), "scene-selection");
            edit.setCell(cell);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
            
            if (simCell.child != null) {
                edit = new EditWidget(main.getSkin(), "scene-selector");
                edit.setFollowActor(simCell.child.previewActor);
                edit.setSimActorTarget(simCell.child);
                preview.add(edit);
            }
        }  else if (dialog.simActor.previewActor != null) {
            var edit = new EditWidget(main.getSkin(), "scene-selection");
            edit.setFollowActor(dialog.simActor.previewActor);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
    
            if (dialog.simActor instanceof SimSingleChild) {
                var child = ((SimSingleChild) dialog.simActor).getChild();
                if (child != null) {
                    edit = new EditWidget(main.getSkin(), "scene-selector");
                    edit.setFollowActor(child.previewActor);
                    edit.setSimActorTarget(child);
                    preview.add(edit);
                }
            }
    
            if (dialog.simActor instanceof SimMultipleChildren) {
                var children = ((SimMultipleChildren) dialog.simActor).getChildren();
                for (var child : children) {
                    edit = new EditWidget(main.getSkin(), "scene-selector");
                    edit.setFollowActor(child.previewActor);
                    edit.setSimActorTarget(child);
                    preview.add(edit);
                }
            }
        }
    }
    
    private void createPreviewWidgets() {
        for (var simActor : rootActor.children) {
            var actor = createPreviewWidget(simActor);
            if (actor != null) {
                preview.addActor(actor);
            }
        }
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
            
            table.setFillParent(simTable.fillParent);
            
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
                    cell.minHeight(simCell.minHeight);
                }
    
                if (simCell.maxWidth >= 0) {
                    cell.maxWidth(simCell.maxWidth);
                }
    
                if (simCell.maxHeight >= 0) {
                    cell.maxHeight(simCell.maxHeight);
                }
    
                if (simCell.preferredWidth >= 0) {
                    cell.prefWidth(simCell.preferredWidth);
                }
    
                if (simCell.preferredHeight >= 0) {
                    cell.prefHeight(simCell.preferredHeight);
                }
            }
            
            if (dialog.view == View.EDIT && table.getCells().size == 0) table.add().size(50, 50);
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
            if (simImage.drawable != null) {
                var image = new Image(main.getAtlasData().getDrawablePairs().get(simImage.drawable));
                image.setScaling(simImage.scaling);
                actor = image;
            }
        } else if (simActor instanceof SimLabel) {
            var simLabel = (SimLabel) simActor;
            if (simLabel.style != null && simLabel.style.hasMandatoryFields()) {
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
            }
        } else if (simActor instanceof SimList) {
            var simList = (SimList) simActor;
            if (simList.style != null && simList.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(List.ListStyle.class, simList.style);
                var list = new List<String>(style);
                list.setName(simList.name);
                list.setItems(simList.list);
                actor = list;
            }
        } else if (simActor instanceof SimProgressBar) {
            var sim = (SimProgressBar) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
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
            }
        } else if (simActor instanceof SimSelectBox) {
            var sim = (SimSelectBox) simActor;
            if (sim.list.size > 0 && sim.style != null && sim.style.hasMandatoryFields()) {
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
            }
        } else if (simActor instanceof SimSlider) {
            var sim = (SimSlider) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
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
            }
        } else if (simActor instanceof SimTextField) {
            var sim = (SimTextField) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(TextField.TextFieldStyle.class, sim.style);
                var textField = new TextField(sim.text == null ? "" : sim.text, style);
                textField.setName(sim.name);
                textField.setPasswordCharacter(sim.passwordCharacter);
                textField.setPasswordMode(sim.passwordMode);
                textField.setAlignment(sim.alignment);
                textField.setDisabled(sim.disabled);
                textField.setCursorPosition(sim.cursorPosition);
                if (sim.selectAll) {
                    textField.setSelection(0, Math.max(textField.getText().length() - 1, 0));
                } else {
                    textField.setSelection(sim.selectionStart, sim.selectionEnd);
                }
                textField.setFocusTraversal(sim.focusTraversal);
                textField.setMaxLength(sim.maxLength);
                textField.setMessageText(sim.messageText);
                actor = textField;
            }
        } else if (simActor instanceof SimTextArea) {
            var sim = (SimTextArea) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
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
            }
        } else if (simActor instanceof SimTouchPad) {
            var sim = (SimTouchPad) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(Touchpad.TouchpadStyle.class, sim.style);
                var touchPad = new Touchpad(sim.deadZone, style);
                touchPad.setResetOnTouchUp(sim.resetOnTouchUp);
                actor = touchPad;
            }
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
            if (sim.child != null || dialog.view == View.EDIT) container.setActor(createPreviewWidget(sim.child));
            actor = container;
        } else if (simActor instanceof SimHorizontalGroup) {
            var sim = (SimHorizontalGroup) simActor;
            var horizontalGroup = new HorizontalGroup();
            horizontalGroup.align(sim.alignment);
            horizontalGroup.expand(sim.expand);
            horizontalGroup.fill(sim.fill ? 1f : 0f);
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
                var widget = createPreviewWidget(child);
                if ( widget != null) {
                    horizontalGroup.addActor(widget);
                }
            }
            
            if (dialog.view == View.EDIT && sim.children.size == 0) {
                var container = new Container();
                container.prefSize(50);
                horizontalGroup.addActor(container);
            }
            
            actor = horizontalGroup;
        } else if (simActor instanceof SimScrollPane) {
            var sim = (SimScrollPane) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
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
            }
        } else if (simActor instanceof SimStack) {
            var sim = (SimStack) simActor;
            var stack = new Stack();
            stack.setName(sim.name);
            for (var child : sim.children) {
                stack.add(createPreviewWidget(child));
            }
            if (sim.children.size == 0) {
                var container = new Container();
                container.prefSize(50);
                stack.add(container);
            }
            actor = stack;
        } else if (simActor instanceof SimSplitPane) {
            var sim = (SimSplitPane) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(SplitPane.SplitPaneStyle.class, sim.style);
                var splitPane = new SplitPane(createPreviewWidget(sim.childFirst), createPreviewWidget(sim.childSecond), sim.vertical, style);
                splitPane.setName(sim.name);
                splitPane.setSplitAmount(sim.split);
                splitPane.setMinSplitAmount(sim.splitMin);
                splitPane.setMaxSplitAmount(sim.splitMax);
                actor = splitPane;
            }
        } else if (simActor instanceof SimTree) {
            var sim = (SimTree) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = main.getRootTable().createPreviewStyle(Tree.TreeStyle.class, sim.style);
                var tree = new Tree(style);
                tree.setName(sim.name);
                tree.setPadding(sim.padLeft, sim.padRight);
                tree.setIconSpacing(sim.iconSpaceLeft, sim.iconSpaceRight);
                tree.setIndentSpacing(sim.indentSpacing);
                tree.setYSpacing(sim.ySpacing);
                for (var child : sim.children) {
                    Tree.Node node = createPreviewNode(child);
                    if (node != null) {
                        tree.add(node);
                    }
                }
                if (sim.children.size == 0) {
                    Tree.Node node = new GenericNode();
                    var container = new Container();
                    container.prefSize(50);
                    node.setActor(container);
                    tree.add(node);
                }
                actor = tree;
            }
        } else if (simActor instanceof SimVerticalGroup) {
            var sim = (SimVerticalGroup) simActor;
            var verticalGroup = new VerticalGroup();
            verticalGroup.align(sim.alignment);
            verticalGroup.expand(sim.expand);
            verticalGroup.fill(sim.fill ? 1f : 0f);
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
                var widget = createPreviewWidget(child);
                if (widget != null) verticalGroup.addActor(widget);
            }
    
            if (dialog.view == View.EDIT && sim.children.size == 0) {
                var container = new Container();
                container.prefSize(50);
                verticalGroup.addActor(container);
            }
            
            actor = verticalGroup;
        } else if (dialog.view == View.EDIT) {
            var container = new Container();
            container.prefSize(50);
            actor = container;
        }
        
        if (simActor != null) simActor.previewActor = actor;
        return actor;
    }
    
    public Tree.Node createPreviewNode(SimNode simNode) {
        if (simNode.actor != null || dialog.view == View.EDIT) {
            var node = new GenericNode();
            Actor actor = createPreviewWidget(simNode.actor == null  && dialog.view == View.EDIT ? new SimActor() : simNode.actor);
            if (actor == null) return null;
            node.setActor(actor);
            if (simNode.icon != null) node.setIcon(main.getAtlasData().drawablePairs.get(simNode.icon));
            node.setSelectable(simNode.selectable);
            for (var child : simNode.nodes) {
                Tree.Node newNode = createPreviewNode(child);
                if (newNode != null) {
                    node.add(newNode);
                }
            }
            simNode.previewActor = actor;
            return node;
        }
        return null;
    }
    
    public class GenericNode extends Tree.Node {
    
    }
    
    public static void assignParentRecursive(SimActor parent) {
        if (parent instanceof SimSingleChild) {
            var child = ((SimSingleChild) parent).getChild();
            if (child != null) {
                child.parent = parent;
                assignParentRecursive(child);
            }
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
        public transient Actor previewActor;
        
        public boolean hasChildOfTypeRecursive(Class type) {
            boolean returnValue = false;
            if (this instanceof SimSingleChild) {
                SimActor child = ((SimSingleChild) this).getChild();
                if (child != null) {
                    returnValue = ClassReflection.isInstance(type, child) || child.hasChildOfTypeRecursive(type);
                }
            }
            
            if (!returnValue && this instanceof SimMultipleChildren) {
                for (var child : ((SimMultipleChildren) this).getChildren()) {
                    if (child != null) {
                        if (returnValue = ClassReflection.isInstance(type, child) || child.hasChildOfTypeRecursive(type)) {
                            break;
                        }
                        
                    }
                }
            }
            
            return returnValue;
        }
    }
    
    public interface SimSingleChild {
        SimActor getChild();
    }
    
    public interface SimMultipleChildren {
        Array<? extends SimActor> getChildren();
        void addChild(SimActor simActor);
        void removeChild(SimActor simActor);
    }
    
    public static class SimRootGroup extends SimActor implements SimMultipleChildren {
        public Array<SimActor> children = new Array<>();
        public ColorData backgroundColor;
        public String skinPath = "skin.json";
        public String packageString = "com.mygdx.game";
        public String classString = "Core";
    
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
    
        @Override
        public void addChild(SimActor simActor) {
            children.add(simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            children.removeValue(simActor, true);
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
        public boolean fillParent;
    
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
    
        @Override
        public void addChild(SimActor simActor) {
            cells.add((SimCell) simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            cells.removeValue((SimCell) simActor, true);
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(Button.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(CheckBox.class);
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(CheckBox.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(Label.class);
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(Label.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(List.class);
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(List.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
        public boolean round = true;
        public Interpol visualInterpolation = Interpol.LINEAR;
    
        public SimProgressBar() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(ProgressBar.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(ProgressBar.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(SelectBox.class);
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(SelectBox.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
        public boolean round = true;
        public Interpol visualInterpolation = Interpol.LINEAR;
    
        public SimSlider() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Slider.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(Slider.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextButton.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
        public char passwordCharacter = '•';
        public boolean passwordMode;
        public int alignment = Align.center;
        public boolean disabled;
        public int cursorPosition;
        public int selectionStart;
        public int selectionEnd;
        public boolean selectAll;
        public boolean focusTraversal = true;
        public int maxLength;
        public String messageText;
    
        public SimTextField() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextField.class);
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
            passwordCharacter = '•';
            passwordMode = false;
            alignment = Align.center;
            disabled = false;
            cursorPosition = 0;
            selectionStart = 0;
            selectionEnd = 0;
            selectAll = false;
            focusTraversal = true;
            maxLength = 0;
            messageText = null;
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextField.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    }
    
    public static class SimTextArea extends SimActor {
        public String name;
        public StyleData style;
        public String text;
        public char passwordCharacter = '•';
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextField.class);
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
            passwordCharacter = '•';
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(TextField.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    }
    
    public static class SimTouchPad extends SimActor {
        public String name;
        public StyleData style;
        public float deadZone;
        public boolean resetOnTouchUp = true;
    
        public SimTouchPad() {
            var styles = Main.main.getJsonData().getClassStyleMap().get(Touchpad.class);
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(Touchpad.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
        public boolean fill;
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
            fill = false;
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
    
        @Override
        public void addChild(SimActor simActor) {
            children.add(simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            children.removeValue(simActor, true);
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(ScrollPane.class);
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(ScrollPane.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
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
    
        @Override
        public void addChild(SimActor simActor) {
            children.add(simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            children.removeValue(simActor, true);
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(SplitPane.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(Stack.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public Array<SimActor> getChildren() {
            tempChildren.clear();
            if (childFirst != null) {
                tempChildren.add(childFirst);
            }
            if (childSecond != null) {
                tempChildren.add(childSecond);
            }
            return tempChildren;
        }
    
        @Override
        public void addChild(SimActor simActor) {
            if (childFirst == null) childFirst = simActor;
            else if (childSecond == null) childSecond = simActor;
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            if (childFirst == simActor) childFirst = null;
            else if (childSecond == simActor) childSecond = null;
        }
    }
    
    public static class SimNode extends SimActor implements SimMultipleChildren {
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
        public Array<SimActor> getChildren() {
            Array<SimActor> actors = new Array<>();
            actors.addAll(nodes);
            if (actor != null) actors.add(actor);
            return actors;
        }
    
        @Override
        public void addChild(SimActor simActor) {
            if (simActor instanceof SimNode) nodes.add((SimNode) simActor);
            else if (actor == null) actor = simActor;
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            if (simActor instanceof SimNode) nodes.removeValue((SimNode) simActor, true);
            else if (actor == simActor) actor = null;
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
            var styles = Main.main.getJsonData().getClassStyleMap().get(Tree.class);
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
    
            var styles = Main.main.getJsonData().getClassStyleMap().get(Tree.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public Array<SimNode> getChildren() {
            return children;
        }
    
        @Override
        public void addChild(SimActor simActor) {
            children.add((SimNode) simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            children.removeValue((SimNode) simActor, true);
        }
    }
    
    public static class SimVerticalGroup extends SimActor implements SimMultipleChildren {
        public String name;
        public int alignment = Align.center;
        public boolean expand;
        public boolean fill;
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
            fill = false;
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
    
        @Override
        public void addChild(SimActor simActor) {
            children.add(simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            children.removeValue(simActor, true);
        }
    }
}
