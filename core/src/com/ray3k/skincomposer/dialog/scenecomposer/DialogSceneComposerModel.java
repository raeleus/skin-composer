package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer.View;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.SceneComposerUndoable;
import com.ray3k.stripe.scenecomposer.SimMultipleChildren;
import com.ray3k.stripe.scenecomposer.SimSingleChild;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer.dialog;

public class DialogSceneComposerModel {
    public transient Array<SceneComposerUndoable> undoables;
    public transient Array<SceneComposerUndoable> redoables;
    public static SimRootGroup rootActor;
    public transient Stack preview;
    private static Json json;
    private final static Vector2 temp = new Vector2();
    private final static int EDIT_EMPTY_WIDGET_SIZE = 50;
    
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
    
        json = new Json();
        json.setSerializer(ColorData.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, ColorData object, Class knownType) {
                json.writeValue(object.getName());
            }

            @Override
            public ColorData read(Json json, JsonValue jsonData, Class type) {
                return Main.jsonData.getColorByName(jsonData.asString());
            }
        });
        json.setSerializer(DrawableData.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, DrawableData object, Class knownType) {
                System.out.println("drawable");
                json.writeValue(object.name);
            }

            @Override
            public DrawableData read(Json json, JsonValue jsonData, Class type) {
                return Main.atlasData.getDrawable(jsonData.asString());
            }
        });
        json.setSerializer(StyleData.class, new Json.Serializer<>() {
            @Override
            public void write(Json json, StyleData object, Class knownType) {
                System.out.println("style");
                json.writeObjectStart();
                json.writeValue("clazz", object.clazz.getName());
                json.writeValue("name", object.name);
                json.writeObjectEnd();
            }

            @Override
            public StyleData read(Json json, JsonValue jsonData, Class type) {
                try {
                    return Main.jsonData.findStyle(ClassReflection.forName(jsonData.getString("clazz")), jsonData.getString("name"));
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
                    if (style != null) {
                        StyleData foundStyle = jsonData.findStyle(style.clazz, style.name);
                        if (foundStyle == null) foundStyle = jsonData.findStyle(style.clazz, "default");
                        if (foundStyle == null)
                            foundStyle = jsonData.findStyle(style.clazz, "default-horizontal");
                        field.set(simActor, foundStyle);
                    }
                } catch (ReflectionException e) {
                    e.printStackTrace(System.out);
                }
            } else if (field.getType() == DrawableData.class) {
                try {
                    var drawable = (DrawableData) field.get(simActor);
                    if (drawable != null) {
                        var foundDrawable = atlasData.getDrawable(drawable.name);
                        field.set(simActor, foundDrawable);
                    }
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
        saveFile.writeString(json.prettyPrint(rootActor), false, "utf-8");
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
            var fadeLabel = new FadeLabel(undoable.getUndoString(), skin, "scene-edit-tip");
            temp.set(dialog.previewTable.getWidth() / 2, dialog.previewTable.getHeight() / 2);
            dialog.previewTable.localToStageCoordinates(temp);
            fadeLabel.setPosition(temp.x - (int) fadeLabel.getWidth() / 2, temp.y - (int) fadeLabel.getHeight() / 2);
            stage.addActor(fadeLabel);
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
    
    private Cell findCell(Table table, int row, int column) {
        for (var cell : table.getCells()) {
            if (cell.getColumn() == column && cell.getRow() == row) {
                return cell;
            } else if (cell.getRow() == row) {
                column += cell.getColspan() - 1;
            }
        }
        return null;
    }
    
    public SimActor findSimActorByName(String name) {
        return findSimActorByName(name, rootActor);
    }
    
    public SimActor findSimActorByName(String name, SimActor parent) {
        if (parent instanceof SimNamed) {
            var compareName = ((SimNamed) parent).getName();
            if (compareName != null && compareName.toLowerCase(Locale.ROOT).equals(name)) {
                return parent;
            }
        }
        
        if (parent instanceof SimSingleChild) {
            var child = ((SimSingleChild) parent).getChild();
            if (child != null) return findSimActorByName(name, child);
        }
    
        if (parent instanceof SimMultipleChildren) {
            for (var child : ((SimMultipleChildren) parent).getChildren()) {
                var found = findSimActorByName(name, child);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    private void createEditWidgets() {
        if (dialog.simActor.parent != null) {
            var edit = new EditWidget(skin, "scene-select-back");
            edit.setFillParent(true);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
        }
        
        if (dialog.simActor instanceof SimRootGroup) {
            var simGroup = (SimRootGroup) dialog.simActor;
            var edit = new EditWidget(skin, "scene-selection");
            edit.setFillParent(true);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
    
            if (simGroup.children.size > 0) {
                edit = new EditWidget(skin, "scene-selector");
                edit.setFillParent(true);
                edit.setSimActorTarget(simGroup.children.peek());
                preview.add(edit);
            }
        } else if (dialog.simActor instanceof SimTable) {
            var edit = new EditWidget(skin, "scene-selection");
            edit.setFollowActor(dialog.simActor.previewActor);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
            
            var simTable = (SimTable) dialog.simActor;
            for (var simCell : simTable.cells) {
                var table = (Table) simTable.previewActor;
                var cell = findCell(table, simCell.row, simCell.column);
                
                edit = new EditWidget(skin, "scene-selector");
                edit.setCell(cell);
                edit.setSimActorTarget(simCell);
                preview.add(edit);
            }
        } else if (dialog.simActor instanceof SimCell) {
            var simCell = (SimCell) dialog.simActor;
            var table = (Table) ((SimTable) simCell.parent).previewActor;
            var cell = findCell(table, simCell.row, simCell.column);
            
            var edit = new EditWidget(skin, "scene-selection");
            edit.setCell(cell);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
            
            if (simCell.child != null) {
                edit = new EditWidget(skin, "scene-selector");
                edit.setFollowActor(simCell.child.previewActor);
                edit.setSimActorTarget(simCell.child);
                preview.add(edit);
            }
        }  else if (dialog.simActor.previewActor != null) {
            var edit = new EditWidget(skin, "scene-selection");
            edit.setFollowActor(dialog.simActor.previewActor);
            edit.setSimActorTarget(dialog.simActor.parent);
            preview.add(edit);
    
            if (dialog.simActor instanceof SimSingleChild) {
                var child = ((SimSingleChild) dialog.simActor).getChild();
                if (child != null) {
                    edit = new EditWidget(skin, "scene-selector");
                    edit.setFollowActor(child.previewActor);
                    edit.setSimActorTarget(child);
                    preview.add(edit);
                }
            }
    
            if (dialog.simActor instanceof SimMultipleChildren) {
                var children = ((SimMultipleChildren) dialog.simActor).getChildren();
                for (var child : children) {
                    edit = new EditWidget(skin, "scene-selector");
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
                preview.add(actor);
            }
        }
    }
    
    public static String convertEscapedCharacters(String string) {
        if (string != null) {
            string = string.replaceAll("(?<!\\\\)\\\\n", "\n")
                    .replace("(?<!\\\\)\\\\t", "\t")
                    .replace("(?<!\\\\)\\\\r", "\r");
            var result = string;
            Pattern pattern = Pattern.compile("(?<!\\\\)(\\\\u[\\d,a-f,A-F]{4})");
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                result = result.replaceFirst("(?<!\\\\)(\\\\u[\\d,a-f,A-F]{4})",
                        new String(Character.toChars(Integer.parseInt(matcher.group().substring(2), 16))));
            }
            return result.replace("\\\\", "\\");
        } else {
            return null;
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
                table.setBackground(atlasData.getDrawablePairs().get(simTable.background));
            }
        
            if (simTable.color != null) {
                table.setColor(simTable.color.color);
            }
            
            if (simTable.paddingEnabled) {
                table.pad(simTable.padTop, simTable.padLeft, simTable.padBottom, simTable.padRight);
            }
            
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
            
            if (dialog.view == View.EDIT && table.getCells().size == 0) table.add().size(EDIT_EMPTY_WIDGET_SIZE, EDIT_EMPTY_WIDGET_SIZE);
        } else if (simActor instanceof SimTextButton) {
            var simTextButton = (SimTextButton) simActor;
            if (simTextButton.style != null && simTextButton.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(TextButton.TextButtonStyle.class, simTextButton.style);
                var textButton = new TextButton(simTextButton.text == null ? "" : convertEscapedCharacters(simTextButton.text), style);
                textButton.setName(simTextButton.name);
                textButton.setChecked(simTextButton.checked);
                textButton.setDisabled(simTextButton.disabled);
                if (simTextButton.color != null) {
                    textButton.setColor(simTextButton.color.color);
                }
                
                if (!MathUtils.isZero(simTextButton.padTop)) textButton.padTop(simTextButton.padTop);
                if (!MathUtils.isZero(simTextButton.padBottom)) textButton.padTop(simTextButton.padBottom);
                if (!MathUtils.isZero(simTextButton.padLeft)) textButton.padTop(simTextButton.padLeft);
                if (!MathUtils.isZero(simTextButton.padRight)) textButton.padTop(simTextButton.padRight);
                
                textButton.addListener(handListener);
                actor = textButton;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimButton) {
            var simButton = (SimButton) simActor;
            if (simButton.style != null && simButton.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(Button.ButtonStyle.class, simButton.style);
                var button = new Button(style);
                button.setName(simButton.name);
                button.setChecked(simButton.checked);
                button.setDisabled(simButton.disabled);
                if (simButton.color != null) {
                    button.setColor(simButton.color.color);
                }
                button.pad(simButton.padTop, simButton.padLeft, simButton.padBottom, simButton.padRight);
                button.addListener(handListener);
                actor = button;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        }  else if (simActor instanceof SimImageButton) {
            var simImageButton = (SimImageButton) simActor;
            if (simImageButton.style != null && simImageButton.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(ImageButton.ImageButtonStyle.class, simImageButton.style);
                var imageButton = new ImageButton(style);
                imageButton.setName(simImageButton.name);
                imageButton.setChecked(simImageButton.checked);
                imageButton.setDisabled(simImageButton.disabled);
                if (simImageButton.color != null) {
                    imageButton.setColor(simImageButton.color.color);
                }
    
                if (!MathUtils.isZero(simImageButton.padTop)) imageButton.padTop(simImageButton.padTop);
                if (!MathUtils.isZero(simImageButton.padBottom)) imageButton.padTop(simImageButton.padBottom);
                if (!MathUtils.isZero(simImageButton.padLeft)) imageButton.padTop(simImageButton.padLeft);
                if (!MathUtils.isZero(simImageButton.padRight)) imageButton.padTop(simImageButton.padRight);
                
                imageButton.addListener(handListener);
                actor = imageButton;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimImageTextButton) {
            var simImageTextButton = (SimImageTextButton) simActor;
            if (simImageTextButton.style != null && simImageTextButton.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(ImageTextButton.ImageTextButtonStyle.class, simImageTextButton.style);
                var imageTextButton = new ImageTextButton(simImageTextButton.text == null ? "" : convertEscapedCharacters(simImageTextButton.text), style);
                imageTextButton.setName(simImageTextButton.name);
                imageTextButton.setChecked(simImageTextButton.checked);
                imageTextButton.setDisabled(simImageTextButton.disabled);
                if (simImageTextButton.color != null) {
                    imageTextButton.setColor(simImageTextButton.color.color);
                }
    
                if (!MathUtils.isZero(simImageTextButton.padTop)) imageTextButton.padTop(simImageTextButton.padTop);
                if (!MathUtils.isZero(simImageTextButton.padBottom)) imageTextButton.padTop(simImageTextButton.padBottom);
                if (!MathUtils.isZero(simImageTextButton.padLeft)) imageTextButton.padTop(simImageTextButton.padLeft);
                if (!MathUtils.isZero(simImageTextButton.padRight)) imageTextButton.padTop(simImageTextButton.padRight);
                
                imageTextButton.addListener(handListener);
                actor = imageTextButton;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimCheckBox) {
            var simCheckBox = (SimCheckBox) simActor;
            if (simCheckBox.style != null && simCheckBox.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(CheckBox.CheckBoxStyle.class, simCheckBox.style);
                var checkBox = new CheckBox(simCheckBox.text == null ? "" : convertEscapedCharacters(simCheckBox.text), style);
                checkBox.setName(simCheckBox.name);
                checkBox.setChecked(simCheckBox.checked);
                checkBox.setDisabled(simCheckBox.disabled);
                if (simCheckBox.color != null) {
                    checkBox.setColor(simCheckBox.color.color);
                }
    
                if (!MathUtils.isZero(simCheckBox.padTop)) checkBox.padTop(simCheckBox.padTop);
                if (!MathUtils.isZero(simCheckBox.padBottom)) checkBox.padTop(simCheckBox.padBottom);
                if (!MathUtils.isZero(simCheckBox.padLeft)) checkBox.padTop(simCheckBox.padLeft);
                if (!MathUtils.isZero(simCheckBox.padRight)) checkBox.padTop(simCheckBox.padRight);
                
                checkBox.addListener(handListener);
                actor = checkBox;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimImage) {
            var simImage = (SimImage) simActor;
            if (simImage.drawable != null) {
                var image = new Image(atlasData.getDrawablePairs().get(simImage.drawable));
                try {
                    image.setScaling((Scaling) Scaling.class.getField(simImage.scaling).get(null));
                } catch (Exception e) {}
                actor = image;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimLabel) {
            var simLabel = (SimLabel) simActor;
            if (simLabel.style != null && simLabel.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(Label.LabelStyle.class, simLabel.style);
                var label = new Label(simLabel.text == null ? "" : convertEscapedCharacters(simLabel.text), style);
                label.setName(simLabel.name);
                label.setAlignment(simLabel.textAlignment);
                if (simLabel.ellipsis) {
                    label.setEllipsis(simLabel.ellipsisString);
                }
                label.setWrap(simLabel.wrap);
                if (simLabel.color != null) label.setColor(simLabel.color.color);
                actor = label;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimList) {
            var simList = (SimList) simActor;
            if (simList.style != null && simList.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(List.ListStyle.class, simList.style);
                var list = new List<String>(style);
                list.setName(simList.name);
                var newList = new Array<String>();
                for (var item : simList.list) {
                    newList.add(convertEscapedCharacters(item));
                }
                list.setItems(newList);
                list.addListener(handListener);
                actor = list;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimProgressBar) {
            var sim = (SimProgressBar) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(ProgressBar.ProgressBarStyle.class, sim.style);
                var progressBar = new ProgressBar(sim.minimum, sim.maximum, sim.increment, sim.vertical, style);
                progressBar.setName(sim.name);
                progressBar.setDisabled(sim.disabled);
                progressBar.setValue(sim.value);
                progressBar.setAnimateDuration(sim.animationDuration);
                progressBar.setAnimateInterpolation(sim.animateInterpolation.interpolation);
                progressBar.setRound(sim.round);
                progressBar.setVisualInterpolation(sim.visualInterpolation.interpolation);
                actor = progressBar;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimSelectBox) {
            var sim = (SimSelectBox) simActor;
            if (sim.list.size > 0 && sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(SelectBox.SelectBoxStyle.class, sim.style);
                var selectBox = new SelectBox<String>(style);
                selectBox.setName(sim.name);
                selectBox.setDisabled(sim.disabled);
                selectBox.setMaxListCount(sim.maxListCount);
                var newList = new Array<String>();
                for (var item : sim.list) {
                    newList.add(convertEscapedCharacters(item));
                }
                selectBox.setItems(newList);
                selectBox.setAlignment(sim.alignment);
                selectBox.setSelectedIndex(sim.selected);
                selectBox.setScrollingDisabled(sim.scrollingDisabled);
                selectBox.addListener(handListener);
                selectBox.getList().addListener(handListener);
                actor = selectBox;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimSlider) {
            var sim = (SimSlider) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(Slider.SliderStyle.class, sim.style);
                var slider = new Slider(sim.minimum, sim.maximum, sim.increment, sim.vertical, style);
                slider.setName(sim.name);
                slider.setDisabled(sim.disabled);
                slider.setValue(sim.value);
                slider.setAnimateDuration(sim.animationDuration);
                slider.setAnimateInterpolation(sim.animateInterpolation.interpolation);
                slider.setRound(sim.round);
                slider.setVisualInterpolation(sim.visualInterpolation.interpolation);
                actor = slider;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimTextField) {
            var sim = (SimTextField) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(TextField.TextFieldStyle.class, sim.style);
                var textField = new TextField(sim.text == null ? "" : convertEscapedCharacters(sim.text), style);
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
                textField.addListener(ibeamListener);
                actor = textField;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimTextArea) {
            var sim = (SimTextArea) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(TextField.TextFieldStyle.class, sim.style);
                var textArea = new TextArea(sim.text == null ? "" : convertEscapedCharacters(sim.text), style);
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
                textArea.addListener(ibeamListener);
                actor = textArea;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimTouchPad) {
            var sim = (SimTouchPad) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(Touchpad.TouchpadStyle.class, sim.style);
                var touchPad = new Touchpad(sim.deadZone, style);
                touchPad.setResetOnTouchUp(sim.resetOnTouchUp);
                actor = touchPad;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimContainer) {
            var sim = (SimContainer) simActor;
            var container = new Container();
            container.align(sim.alignment);
            if (sim.background != null) {
                container.setBackground(atlasData.drawablePairs.get(sim.background));
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
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                horizontalGroup.addActor(container);
            }
            
            actor = horizontalGroup;
        } else if (simActor instanceof SimScrollPane) {
            var sim = (SimScrollPane) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields() && !sim.style.hasAllNullFields()) {
                var style = rootTable.createPreviewStyle(ScrollPane.ScrollPaneStyle.class, sim.style);
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
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimStack) {
            var sim = (SimStack) simActor;
            if (sim.children.size > 0) {
                var stack = new Stack();
                stack.setName(sim.name);
                for (var child : sim.children) {
                    var childActor = createPreviewWidget(child);
                    if (childActor != null) stack.add(childActor);
                }
                actor = stack;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimSplitPane) {
            var sim = (SimSplitPane) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(SplitPane.SplitPaneStyle.class, sim.style);
                var splitPane = new SplitPane(createPreviewWidget(sim.childFirst), createPreviewWidget(sim.childSecond), sim.vertical, style);
                splitPane.setName(sim.name);
                splitPane.setSplitAmount(sim.split);
                splitPane.setMinSplitAmount(sim.splitMin);
                splitPane.setMaxSplitAmount(sim.splitMax);
                actor = splitPane;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
            }
        } else if (simActor instanceof SimTree) {
            var sim = (SimTree) simActor;
            if (sim.style != null && sim.style.hasMandatoryFields()) {
                var style = rootTable.createPreviewStyle(Tree.TreeStyle.class, sim.style);
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
                    container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                    node.setActor(container);
                    tree.add(node);
                }
                tree.addListener(handListener);
                actor = tree;
            } else if (dialog.view == View.EDIT) {
                var container = new Container();
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                actor = container;
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
                container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
                verticalGroup.addActor(container);
            }
            
            actor = verticalGroup;
        } else if (dialog.view == View.EDIT) {
            var container = new Container();
            container.prefSize(EDIT_EMPTY_WIDGET_SIZE);
            actor = container;
        }
        
        if (simActor != null) {
            if (actor != null) {
                if (simActor instanceof SimTouchable) actor.setTouchable(((SimTouchable) simActor).getTouchable());
                if (simActor instanceof SimVisible) actor.setVisible(((SimVisible) simActor).isVisible());
            }
            simActor.previewActor = actor;
        }
        return actor;
    }
    
    public Tree.Node createPreviewNode(SimNode simNode) {
        if (simNode.actor != null || dialog.view == View.EDIT) {
            var node = new GenericNode();
            Actor actor = createPreviewWidget(simNode.actor == null  && dialog.view == View.EDIT ? new SimActor() : simNode.actor);
            if (actor == null) return null;
            node.setActor(actor);
            if (simNode.icon != null) node.setIcon(atlasData.drawablePairs.get(simNode.icon));
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
        
        public SimActor duplicate() {
            var simActor = new SimActor();
            
            simActor.parent = parent;
            
            return parent;
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
    
    public interface SimNamed {
        String getName();
    }
    
    public interface SimTouchable {
        Touchable getTouchable();
    }
    
    public interface SimVisible {
        boolean isVisible();
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
    
        @Override
        public SimRootGroup duplicate() {
            var simRootGroup = new SimRootGroup();

            simRootGroup.parent = parent;
            
            for (var actor : children) {
                simRootGroup.children.add(actor.duplicate());
                simRootGroup.children.peek().parent = simRootGroup;
            }
    
            simRootGroup.backgroundColor = backgroundColor;
            simRootGroup.skinPath = skinPath;
            simRootGroup.packageString = packageString;
            simRootGroup.classString = classString;
            
            return simRootGroup;
        }
    }
    
    public static class SimTable extends SimActor implements SimMultipleChildren, SimNamed, SimTouchable, SimVisible {
        public Array<SimCell> cells = new Array<>();
        public String name;
        public DrawableData background;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public boolean paddingEnabled;
        public int alignment = Align.center;
        public boolean fillParent;
        public Touchable touchable = Touchable.childrenOnly;
        public boolean visible = true;
    
        @Override
        public SimTable duplicate() {
            var simTable = new SimTable();
            
            for (var cell : cells) {
                simTable.cells.add(cell.duplicate());
                simTable.cells.peek().parent = simTable;
            }
            
            simTable.parent = parent;
            simTable.name = name;
            simTable.background = background;
            simTable.color = color;
            simTable.padLeft = padLeft;
            simTable.padRight = padRight;
            simTable.padTop = padTop;
            simTable.padBottom = padBottom;
            simTable.paddingEnabled = paddingEnabled;
            simTable.alignment = alignment;
            simTable.fillParent = fillParent;
            simTable.touchable = touchable;
            simTable.visible = visible;
            
            return simTable;
        }
    
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
            paddingEnabled = false;
            alignment = Align.center;
            touchable = Touchable.enabled;
            visible = true;
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
    
        public int getColumns(int row) {
            int columns = 0;
            for (var cell : cells) {
                if (cell.row == row) {
                    if (cell.column > columns - 1) columns = cell.column + 1;
                }
            }
            return columns;
        }
        
        public int getRows() {
            int rows = 0;
            for (var cell : cells) {
                if (cell.row > rows -1) rows = cell.row + 1;
            }
            return rows;
        }
        
        @Override
        public void addChild(SimActor simActor) {
            cells.add((SimCell) simActor);
        }
    
        @Override
        public void removeChild(SimActor simActor) {
            cells.removeValue((SimCell) simActor, true);
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        public SimCell getCell(int column, int row) {
            SimCell cell = null;
            for (var testCell : cells) {
                if (testCell.column == column && testCell.row == row) {
                    cell = testCell;
                    break;
                }
            }
            
            return cell;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
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
        public SimCell duplicate() {
            var simCell = new SimCell();
            
            simCell.parent = parent;
            if (child != null) {
                simCell.child = child.duplicate();
                simCell.child.parent = simCell;
            }
            simCell.row = row;
            simCell.column = column;
            simCell.padLeft = padLeft;
            simCell.padRight = padRight;
            simCell.padTop = padTop;
            simCell.padBottom = padBottom;
            simCell.spaceLeft = spaceLeft;
            simCell.spaceRight = spaceRight;
            simCell.spaceTop = spaceTop;
            simCell.spaceBottom = spaceBottom;
            simCell.expandX = expandX;
            simCell.expandY = expandY;
            simCell.fillX = fillX;
            simCell.fillY = fillY;
            simCell.growX = growX;
            simCell.growY = growY;
            simCell.alignment = alignment;
            simCell.minWidth = minWidth;
            simCell.minHeight = minHeight;
            simCell.maxWidth = maxWidth;
            simCell.maxHeight = maxHeight;
            simCell.preferredWidth = preferredWidth;
            simCell.preferredHeight = preferredHeight;
            simCell.uniformX = uniformX;
            simCell.uniformY = uniformY;
            simCell.colSpan = colSpan;
            
            return simCell;
        }
    
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
    
    public static class SimButton extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public boolean checked;
        public boolean disabled;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimButton duplicate() {
            var simButton = new SimButton();
            
            simButton.parent = parent;
            simButton.name = name;
            simButton.style = style;
            simButton.checked = checked;
            simButton.disabled = disabled;
            simButton.color = color;
            simButton.padLeft = padLeft;
            simButton.padRight = padRight;
            simButton.padTop = padTop;
            simButton.padBottom = padBottom;
            simButton.touchable = touchable;
            simButton.visible = visible;
            
            return simButton;
        }
    
        public SimButton() {
            var styles = Main.jsonData.getClassStyleMap().get(Button.class);
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
            var styles = Main.jsonData.getClassStyleMap().get(Button.class);
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
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimCheckBox extends SimActor implements SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimCheckBox duplicate() {
            var simCheckBox = new SimCheckBox();
            
            simCheckBox.parent = parent;
            simCheckBox.name = name;
            simCheckBox.style = style;
            simCheckBox.disabled = disabled;
            simCheckBox.text = text;
            simCheckBox.color = color;
            simCheckBox.padLeft = padLeft;
            simCheckBox.padRight = padRight;
            simCheckBox.padTop = padTop;
            simCheckBox.padBottom = padBottom;
            simCheckBox.checked = checked;
            simCheckBox.touchable = touchable;
            simCheckBox.visible = visible;
            
            return simCheckBox;
        }
    
        public SimCheckBox() {
            var styles = Main.jsonData.getClassStyleMap().get(CheckBox.class);
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
            var styles = Main.jsonData.getClassStyleMap().get(CheckBox.class);
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
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimImage extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public DrawableData drawable;
        public String scaling = "stretch";
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimImage duplicate() {
            var simImage = new SimImage();
            
            simImage.parent = parent;
            simImage.name = name;
            simImage.drawable = drawable;
            simImage.scaling = scaling;
            simImage.touchable = touchable;
            simImage.visible = visible;
            
            return simImage;
        }
    
        @Override
        public String toString() {
            return name == null ? "Image" : name + " (Image)";
        }
    
        public void reset() {
            name = null;
            drawable = null;
            scaling  = "stretch";
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimImageButton extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public boolean checked;
        public boolean disabled;
        public ColorData color;
        public float padLeft;
        public float padRight;
        public float padTop;
        public float padBottom;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimImageButton duplicate() {
            var simImageButton = new SimImageButton();
            
            simImageButton.parent = parent;
            simImageButton.name = name;
            simImageButton.style = style;
            simImageButton.checked = checked;
            simImageButton.disabled = disabled;
            simImageButton.color = color;
            simImageButton.padLeft = padLeft;
            simImageButton.padRight = padRight;
            simImageButton.padTop = padTop;
            simImageButton.padBottom = padBottom;
            simImageButton.touchable = touchable;
            simImageButton.visible = visible;
            
            return simImageButton;
        }
    
        public SimImageButton() {
            var styles = Main.jsonData.getClassStyleMap().get(ImageButton.class);
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
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimImageTextButton extends SimActor implements SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimImageTextButton duplicate() {
            var simImageTextButton = new SimImageTextButton();
            
            simImageTextButton.parent = parent;
            simImageTextButton.name = name;
            simImageTextButton.text = text;
            simImageTextButton.style = style;
            simImageTextButton.checked = checked;
            simImageTextButton.disabled = disabled;
            simImageTextButton.color = color;
            simImageTextButton.padLeft = padLeft;
            simImageTextButton.padRight = padRight;
            simImageTextButton.padTop = padTop;
            simImageTextButton.padBottom = padBottom;
            simImageTextButton.touchable = touchable;
            simImageTextButton.visible = visible;
            
            return simImageTextButton;
        }
    
        public SimImageTextButton() {
            var styles = Main.jsonData.getClassStyleMap().get(ImageTextButton.class);
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
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimLabel extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public String text = "Lorem ipsum";
        public int textAlignment = Align.left;
        public boolean ellipsis;
        public String ellipsisString = "...";
        public boolean wrap;
        public ColorData color;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimLabel duplicate() {
            var simLabel = new SimLabel();
            
            simLabel.parent = parent;
            simLabel.name = name;
            simLabel.style = style;
            simLabel.text = text;
            simLabel.textAlignment = textAlignment;
            simLabel.ellipsis = ellipsis;
            simLabel.ellipsisString = ellipsisString;
            simLabel.wrap = wrap;
            simLabel.color = color;
            simLabel.touchable = touchable;
            simLabel.visible = visible;
            
            return simLabel;
        }
    
        public SimLabel() {
            var styles = Main.jsonData.getClassStyleMap().get(Label.class);
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
            var styles = Main.jsonData.getClassStyleMap().get(Label.class);
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
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimList extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public Array<String> list = new Array<>();
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimList duplicate() {
            var simList = new SimList();
            
            simList.parent = parent;
            simList.name = name;
            simList.style = style;
            simList.list.addAll(list);
            simList.touchable = touchable;
            simList.visible = visible;
            
            return simList;
        }
    
        public SimList() {
            var styles = Main.jsonData.getClassStyleMap().get(List.class);
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
            touchable = Touchable.enabled;
            visible = true;
            
            var styles = Main.jsonData.getClassStyleMap().get(List.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
            list.clear();
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimProgressBar extends SimActor implements SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimProgressBar duplicate() {
            var simProgressBar = new SimProgressBar();
            
            simProgressBar.parent = parent;
            simProgressBar.name = name;
            simProgressBar.style = style;
            simProgressBar.disabled = disabled;
            simProgressBar.value = value;
            simProgressBar.minimum = minimum;
            simProgressBar.maximum = maximum;
            simProgressBar.increment = increment;
            simProgressBar.vertical = vertical;
            simProgressBar.animationDuration = animationDuration;
            simProgressBar.animateInterpolation = animateInterpolation;
            simProgressBar.round = round;
            simProgressBar.visualInterpolation = visualInterpolation;
            simProgressBar.touchable = touchable;
            simProgressBar.visible = visible;
            
            return simProgressBar;
        }
    
        public SimProgressBar() {
            var styles = Main.jsonData.getClassStyleMap().get(ProgressBar.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(ProgressBar.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
        
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimSelectBox extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public boolean disabled;
        public int maxListCount;
        public Array<String> list = new Array<>();
        public int alignment = Align.center;
        public int selected;
        public boolean scrollingDisabled;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimSelectBox duplicate() {
            var simSelectBox = new SimSelectBox();
            
            simSelectBox.parent = parent;
            simSelectBox.name = name;
            simSelectBox.style = style;
            simSelectBox.disabled = disabled;
            simSelectBox.maxListCount = maxListCount;
            simSelectBox.list.addAll(list);
            simSelectBox.alignment = alignment;
            simSelectBox.selected = selected;
            simSelectBox.scrollingDisabled = scrollingDisabled;
            simSelectBox.touchable = touchable;
            simSelectBox.visible = visible;
            
            return simSelectBox;
        }
    
        public SimSelectBox() {
            var styles = Main.jsonData.getClassStyleMap().get(SelectBox.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(SelectBox.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimSlider extends SimActor implements SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimSlider duplicate() {
            var simSlider = new SimSlider();
            
            simSlider.parent = parent;
            simSlider.name = name;
            simSlider.style = style;
            simSlider.disabled = disabled;
            simSlider.value = value;
            simSlider.minimum = minimum;
            simSlider.maximum = maximum;
            simSlider.increment = increment;
            simSlider.vertical = vertical;
            simSlider.animationDuration = animationDuration;
            simSlider.animateInterpolation = animateInterpolation;
            simSlider.round = round;
            simSlider.visualInterpolation = visualInterpolation;
            simSlider.touchable = touchable;
            simSlider.visible = visible;
            
            return simSlider;
        }
    
        public SimSlider() {
            var styles = Main.jsonData.getClassStyleMap().get(Slider.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(Slider.class);
            for (var style : styles) {
                if (style.name.equals("default-horizontal")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimTextButton extends SimActor implements SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimTextButton duplicate() {
            var sim = new SimTextButton();
            
            sim.parent = parent;
            sim.name = name;
            sim.text = text;
            sim.style = style;
            sim.checked = checked;
            sim.disabled = disabled;
            sim.color = color;
            sim.padLeft = padLeft;
            sim.padRight = padRight;
            sim.padTop = padTop;
            sim.padBottom = padBottom;
            sim.touchable = touchable;
            sim.visible = visible;
            
            return sim;
        }
    
        public SimTextButton() {
            var styles = Main.jsonData.getClassStyleMap().get(TextButton.class);
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
            var styles = Main.jsonData.getClassStyleMap().get(TextButton.class);
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
            touchable = Touchable.enabled;
            visible = true;
        }
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimTextField extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public String text;
        public char passwordCharacter = '•';
        public boolean passwordMode;
        public int alignment = Align.left;
        public boolean disabled;
        public int cursorPosition;
        public int selectionStart;
        public int selectionEnd;
        public boolean selectAll;
        public boolean focusTraversal = true;
        public int maxLength;
        public String messageText;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimTextField duplicate() {
            var simTextField = new SimTextField();
            
            simTextField.parent = parent;
            simTextField.name = name;
            simTextField.style = style;
            simTextField.text = text;
            simTextField.passwordCharacter = passwordCharacter;
            simTextField.passwordMode = passwordMode;
            simTextField.alignment = alignment;
            simTextField.disabled = disabled;
            simTextField.cursorPosition = cursorPosition;
            simTextField.selectionStart = selectionStart;
            simTextField.selectionEnd = selectionEnd;
            simTextField.selectAll = selectAll;
            simTextField.focusTraversal = focusTraversal;
            simTextField.maxLength = maxLength;
            simTextField.messageText = messageText;
            simTextField.touchable = touchable;
            simTextField.visible = visible;
            
            return simTextField;
        }
    
        public SimTextField() {
            var styles = Main.jsonData.getClassStyleMap().get(TextField.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(TextField.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimTextArea extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public String text;
        public char passwordCharacter = '•';
        public boolean passwordMode;
        public int alignment = Align.left;
        public boolean disabled;
        public int cursorPosition;
        public int selectionStart;
        public int selectionEnd;
        public boolean selectAll;
        public boolean focusTraversal = true;
        public int maxLength;
        public String messageText;
        public int preferredRows;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimTextArea duplicate() {
            var simTextArea = new SimTextArea();
            
            simTextArea.parent = parent;
            simTextArea.name = name;
            simTextArea.style = style;
            simTextArea.text = text;
            simTextArea.passwordCharacter = passwordCharacter;
            simTextArea.passwordMode = passwordMode;
            simTextArea.alignment = alignment;
            simTextArea.disabled = disabled;
            simTextArea.cursorPosition = cursorPosition;
            simTextArea.selectionStart = selectionStart;
            simTextArea.selectionEnd = selectionEnd;
            simTextArea.selectAll = selectAll;
            simTextArea.focusTraversal = focusTraversal;
            simTextArea.maxLength = maxLength;
            simTextArea.messageText = messageText;
            simTextArea.preferredRows = preferredRows;
            simTextArea.touchable = touchable;
            simTextArea.visible = visible;
            
            return simTextArea;
        }
    
        public SimTextArea() {
            var styles = Main.jsonData.getClassStyleMap().get(TextField.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(TextField.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimTouchPad extends SimActor implements SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public float deadZone;
        public boolean resetOnTouchUp = true;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimTouchPad duplicate() {
            var simTouchPad = new SimTouchPad();
            
            simTouchPad.parent = parent;
            simTouchPad.style = style;
            simTouchPad.deadZone = deadZone;
            simTouchPad.resetOnTouchUp = resetOnTouchUp;
            simTouchPad.touchable = touchable;
            simTouchPad.visible = visible;
            
            return simTouchPad;
        }
    
        public SimTouchPad() {
            var styles = Main.jsonData.getClassStyleMap().get(Touchpad.class);
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
            touchable = Touchable.enabled;
    
            var styles = Main.jsonData.getClassStyleMap().get(Touchpad.class);
            for (var style : styles) {
                if (style.name.equals("default")) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        this.style = style;
                    }
                }
            }
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimContainer extends SimActor implements SimSingleChild, SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimContainer duplicate() {
            var simContainer = new SimContainer();
            simContainer.name = name;
            simContainer.alignment = alignment;
            simContainer.background = background;
            simContainer.fillX = fillX;
            simContainer.fillY = fillY;
            simContainer.minWidth = minWidth;
            simContainer.minHeight = minHeight;
            simContainer.maxWidth = maxWidth;
            simContainer.maxHeight = maxHeight;
            simContainer.preferredWidth = preferredWidth;
            simContainer.preferredHeight = preferredHeight;
            simContainer.padLeft = padLeft;
            simContainer.padRight = padRight;
            simContainer.padTop = padTop;
            simContainer.padBottom = padBottom;
            simContainer.touchable = touchable;
            simContainer.visible = visible;
            
            if (child != null) {
                simContainer.child = child.duplicate();
                simContainer.child.parent = simContainer;
            }
            
            return simContainer;
        }
    
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
            touchable = Touchable.enabled;
            visible = true;
        }
    
        @Override
        public SimActor getChild() {
            return child;
        }
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimHorizontalGroup extends SimActor implements SimMultipleChildren, SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimHorizontalGroup duplicate() {
            var simHorizontalGroup = new SimHorizontalGroup();
            
            simHorizontalGroup.parent = parent;
            simHorizontalGroup.name = name;
            simHorizontalGroup.alignment = alignment;
            simHorizontalGroup.expand = expand;
            simHorizontalGroup.fill = fill;
            simHorizontalGroup.padLeft = padLeft;
            simHorizontalGroup.padRight = padRight;
            simHorizontalGroup.padTop = padTop;
            simHorizontalGroup.padBottom = padBottom;
            simHorizontalGroup.reverse = reverse;
            simHorizontalGroup.rowAlignment =  rowAlignment;
            simHorizontalGroup.space = space;
            simHorizontalGroup.wrap = wrap;
            simHorizontalGroup.wrapSpace = wrapSpace;
            simHorizontalGroup.touchable = touchable;
            simHorizontalGroup.visible = visible;
            
            for (var actor : children) {
                simHorizontalGroup.children.add(actor.duplicate());
                simHorizontalGroup.children.peek().parent = simHorizontalGroup;
            }
            
            return simHorizontalGroup;
        }
    
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
            touchable = Touchable.enabled;
            visible = true;
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
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimScrollPane extends SimActor implements SimSingleChild, SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimScrollPane duplicate() {
            var simScrollPane = new SimScrollPane();
            
            simScrollPane.parent = parent;
            simScrollPane.name = name;
            simScrollPane.style = style;
            simScrollPane.fadeScrollBars = fadeScrollBars;
            
            if (child != null) {
                simScrollPane.child = child.duplicate();
                simScrollPane.child.parent = simScrollPane;
            }
            
            simScrollPane.clamp = clamp;
            simScrollPane.flickScroll = flickScroll;
            simScrollPane.flingTime = flingTime;
            simScrollPane.forceScrollX = forceScrollX;
            simScrollPane.forceScrollY = forceScrollY;
            simScrollPane.overScrollX = overScrollX;
            simScrollPane.overScrollY = overScrollY;
            simScrollPane.overScrollDistance = overScrollDistance;
            simScrollPane.overScrollSpeedMin = overScrollSpeedMin;
            simScrollPane.overScrollSpeedMax = overScrollSpeedMax;
            simScrollPane.scrollBarBottom = scrollBarBottom;
            simScrollPane.scrollBarRight = scrollBarRight;
            simScrollPane.scrollBarsOnTop = scrollBarsOnTop;
            simScrollPane.scrollBarsVisible = scrollBarsVisible;
            simScrollPane.scrollBarTouch = scrollBarTouch;
            simScrollPane.scrollingDisabledX = scrollingDisabledX;
            simScrollPane.scrollingDisabledY = scrollingDisabledY;
            simScrollPane.smoothScrolling = smoothScrolling;
            simScrollPane.variableSizeKnobs = variableSizeKnobs;
            simScrollPane.touchable = touchable;
            simScrollPane.visible = visible;
            
            return simScrollPane;
        }
    
        public SimScrollPane() {
            var styles = Main.jsonData.getClassStyleMap().get(ScrollPane.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(ScrollPane.class);
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
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimStack extends SimActor implements SimMultipleChildren, SimNamed, SimTouchable, SimVisible {
        public String name;
        public Array<SimActor> children = new Array<>();
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimStack duplicate() {
            var simStack = new SimStack();
            
            simStack.parent = parent;
            simStack.touchable = touchable;
            simStack.visible = visible;
            
            for (var actor : children) {
                simStack.children.add(actor.duplicate());
                simStack.children.peek().parent = simStack;
            }
            
            return simStack;
        }
    
        public SimStack() {
        
        }
    
        @Override
        public String toString() {
            return name == null ? "Stack" : name + " (Stack)";
        }
    
        public void reset() {
            name = null;
            touchable = Touchable.enabled;
            visible = true;
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
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimSplitPane extends SimActor implements SimMultipleChildren, SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public SimActor childFirst;
        public SimActor childSecond;
        public boolean vertical;
        public float split = .5f;
        public float splitMin;
        public float splitMax = 1;
        public transient Array<SimActor> tempChildren = new Array<>();
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimSplitPane duplicate() {
            var simSplitPane = new SimSplitPane();
            
            simSplitPane.parent = parent;
            simSplitPane.name = name;
            simSplitPane.style = style;
            if (childFirst != null) {
                simSplitPane.childFirst = childFirst;
                simSplitPane.childFirst.parent = simSplitPane;
            }
            if (childSecond != null) {
                simSplitPane.childSecond = childSecond.duplicate();
                simSplitPane.childSecond.parent = simSplitPane;
            }
            simSplitPane.vertical = vertical;
            simSplitPane.split = split;
            simSplitPane.splitMin = splitMin;
            simSplitPane.splitMax = splitMax;
            simSplitPane.touchable = touchable;
            simSplitPane.visible = visible;
            
            return simSplitPane;
        }
    
        public SimSplitPane() {
            var styles = Main.jsonData.getClassStyleMap().get(SplitPane.class);
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
            touchable = Touchable.enabled;
            visible = true;
            tempChildren.clear();
    
            var styles = Main.jsonData.getClassStyleMap().get(Stack.class);
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
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimNode extends SimActor implements SimMultipleChildren {
        public SimActor actor;
        public Array<SimNode> nodes = new Array<>();
        public boolean expanded;
        public DrawableData icon;
        public boolean selectable = true;
    
        @Override
        public SimNode duplicate() {
            var simNode = new SimNode();
            
            simNode.parent = parent;
            if (actor != null) {
                simNode.actor = actor.duplicate();
                simNode.actor.parent = simNode;
            }
            
            for (var node : nodes) {
                simNode.nodes.add(node.duplicate());
                simNode.nodes.peek().parent = simNode;
            }
            
            simNode.expanded = expanded;
            simNode.icon = icon;
            simNode.selectable = selectable;
            
            return simNode;
        }
    
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
    
    public static class SimTree extends SimActor implements SimMultipleChildren, SimNamed, SimTouchable, SimVisible {
        public String name;
        public StyleData style;
        public Array<SimNode> children = new Array<>();
        public float padLeft;
        public float padRight;
        public float iconSpaceLeft = 2;
        public float iconSpaceRight = 2;
        public float indentSpacing;
        public float ySpacing = 4;
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimTree duplicate() {
            var simTree = new SimTree();
            
            simTree.parent = parent;
            simTree.name = name;
            simTree.style = style;
            
            for (var node : children) {
                simTree.children.add(node.duplicate());
                simTree.children.peek().parent = simTree;
            }
            
            simTree.padLeft = padLeft;
            simTree.padRight = padRight;
            simTree.iconSpaceLeft = iconSpaceLeft;
            simTree.iconSpaceRight = iconSpaceRight;
            simTree.indentSpacing = indentSpacing;
            simTree.ySpacing = ySpacing;
            simTree.touchable = touchable;
            simTree.visible = visible;
            
            return simTree;
        }
    
        public SimTree() {
            var styles = Main.jsonData.getClassStyleMap().get(Tree.class);
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
            touchable = Touchable.enabled;
            visible = true;
    
            var styles = Main.jsonData.getClassStyleMap().get(Tree.class);
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
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
    
    public static class SimVerticalGroup extends SimActor implements SimMultipleChildren, SimNamed, SimTouchable, SimVisible {
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
        public Touchable touchable = Touchable.enabled;
        public boolean visible = true;
    
        @Override
        public SimVerticalGroup duplicate() {
            var simVerticalGroup = new SimVerticalGroup();
            
            simVerticalGroup.parent = parent;
            simVerticalGroup.name = name;
            simVerticalGroup.alignment = alignment;
            simVerticalGroup.expand = expand;
            simVerticalGroup.fill = fill;
            simVerticalGroup.padLeft = padLeft;
            simVerticalGroup.padRight = padRight;
            simVerticalGroup.padTop = padTop;
            simVerticalGroup.padBottom = padBottom;
            simVerticalGroup.reverse = reverse;
            simVerticalGroup.columnAlignment = columnAlignment;
            simVerticalGroup.space = space;
            simVerticalGroup.wrap = wrap;
            simVerticalGroup.wrapSpace = wrapSpace;
            simVerticalGroup.touchable = touchable;
            simVerticalGroup.visible = visible;
            
            for (var actor : children) {
                simVerticalGroup.children.add(actor.duplicate());
                simVerticalGroup.children.peek().parent = simVerticalGroup;
            }
            
            return simVerticalGroup;
        }
    
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
            touchable = Touchable.enabled;
            visible = true;
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
    
        @Override
        public String getName() {
            return name;
        }
    
        @Override
        public Touchable getTouchable() {
            return touchable;
        }
    
        @Override
        public boolean isVisible() {
            return visible;
        }
    }
}
