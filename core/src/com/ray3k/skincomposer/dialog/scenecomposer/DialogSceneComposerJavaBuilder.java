package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.StringBuilder;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.*;
import com.ray3k.skincomposer.utils.Utils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import jdk.jshell.execution.Util;
import space.earlygrey.shapedrawer.ShapeDrawer;

import javax.lang.model.element.Modifier;

import static com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.rootActor;

public class DialogSceneComposerJavaBuilder {
    public static String generateJavaFile() {
        TypeSpec typeSpec = TypeSpec.classBuilder(rootActor.classString)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ApplicationAdapter.class)
                .addField(Skin.class, "skin", javax.lang.model.element.Modifier.PRIVATE)
                .addField(Stage.class, "stage", Modifier.PRIVATE)
                .addMethod(createMethod())
                .addMethod(renderMethod())
                .addMethod(resizeMethod())
                .addMethod(disposeMethod())
                .build();
    
        JavaFile javaFile = JavaFile.builder(rootActor.packageString, typeSpec).build();
        
        return javaFile.toString();
    }
    
    private static MethodSpec createMethod() {
        return MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("stage = new Stage(new ScreenViewport())")
                .addStatement("skin = new Skin(Gdx.files.internal($S))", rootActor.skinPath)
                .addStatement("Gdx.input.setInputProcessor(stage)")
                .addCode(createWidget(rootActor, new Array<>(), new ObjectSet<>()).codeBlock)
                .returns(void.class).build();
    }
    
    private static WidgetNamePair createWidget(SimActor actor, Array<String> variables, ObjectSet<String> usedVariables) {
        if (actor == null) return null;
        else if (actor instanceof SimRootGroup) {
            var simRootGroup = (SimRootGroup) actor;
            var builder = CodeBlock.builder();
            
            for (var child : simRootGroup.children) {
                var pair = createWidget(child, variables, usedVariables);
                builder.add(pair.codeBlock);
                builder.addStatement("stage.addActor($L)", pair.name);
                variables.removeValue(pair.name, false);
                usedVariables.add(pair.name);
            }
            
            return new WidgetNamePair(builder.build(), null);
        } else if (actor instanceof SimTable) {
            var table = (SimTable) actor;
            var builder = CodeBlock.builder();
            var variableName = createVariableName("table", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T ", Table.class);
            builder.addStatement("$L = new $T()", variableName, Table.class);
            if (table.name != null) builder.addStatement("$L.setName($L)", variableName, table.name);
            if (table.background != null) builder.addStatement("$L.setBackground(skin.getDrawable($S))", variableName, table.background.name);
            if (table.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, table.color.getName());
            
            if (!Utils.isEqual(0, table.padLeft, table.padRight, table.padTop, table.padBottom)) {
                if (Utils.isEqual(table.padLeft, table.padRight, table.padTop, table.padBottom)) {
                    builder.addStatement("$L.pad($L)", variableName, table.padLeft);
                } else {
                    if (!MathUtils.isZero(table.padLeft)) {
                        builder.addStatement("$L.padLeft($L)", variableName, table.padLeft);
                    }
                    if (!MathUtils.isZero(table.padRight)) {
                        builder.addStatement("$L.padRight($L)", variableName, table.padRight);
                    }
                    if (!MathUtils.isZero(table.padTop)) {
                        builder.addStatement("$L.padTop($L)", variableName, table.padTop);
                    }
                    if (!MathUtils.isZero(table.padBottom)) {
                        builder.addStatement("$L.padBottom($L)", variableName, table.padBottom);
                    }
                }
            }
            
            if (table.alignment != Align.center) {
                builder.addStatement("$L.align($T.$L)", variableName, Align.class, alignmentToName(table.alignment));
            }
            if (table.fillParent) builder.addStatement("$L.setFillParent(true)", variableName);
            
            int row = 0;
            for (var cell : table.getChildren()) {
                if (cell.row > row) {
                    row = cell.row;
                    builder.addStatement("$L.row()", variableName);
                }
                
                WidgetNamePair pair = createWidget(cell.child, variables, usedVariables);
                if (pair != null) builder.add(pair.codeBlock);
                
                builder.add("$L.add($L)", variableName, pair == null ? "" : pair.name);
    
                if (!Utils.isEqual(0, cell.padLeft, cell.padRight, cell.padTop, cell.padBottom)) {
                    if (Utils.isEqual(cell.padLeft, cell.padRight, cell.padTop, cell.padBottom)) {
                        builder.add(".pad($L)", cell.padLeft);
                    } else {
                        if (!MathUtils.isZero(cell.padLeft)) {
                            builder.add(".padLeft($Lf)", cell.padLeft);
                        }
                        if (!MathUtils.isZero(cell.padRight)) {
                            builder.add(".padRight($Lf)", cell.padRight);
                        }
                        if (!MathUtils.isZero(cell.padTop)) {
                            builder.add(".padTop($Lf)", cell.padTop);
                        }
                        if (!MathUtils.isZero(cell.padBottom)) {
                            builder.add(".padBottom($Lf)", cell.padBottom);
                        }
                    }
                }
    
                if (!Utils.isEqual(0, cell.spaceLeft, cell.spaceRight, cell.spaceTop, cell.spaceBottom)) {
                    if (Utils.isEqual(cell.spaceLeft, cell.spaceRight, cell.spaceTop, cell.spaceBottom)) {
                        builder.add(".space($L)", cell.spaceLeft);
                    } else {
                        if (!MathUtils.isZero(cell.spaceLeft)) {
                            builder.add(".spaceLeft($Lf)", cell.spaceLeft);
                        }
                        if (!MathUtils.isZero(cell.spaceRight)) {
                            builder.add(".spaceRight($Lf)", cell.spaceRight);
                        }
                        if (!MathUtils.isZero(cell.spaceTop)) {
                            builder.add(".spaceTop($Lf)", cell.spaceTop);
                        }
                        if (!MathUtils.isZero(cell.spaceBottom)) {
                            builder.add(".spaceBottom($Lf)", cell.spaceBottom);
                        }
                    }
                }
    
                if (cell.growX || cell.growY) {
                    if (cell.growX && cell.growY) {
                        builder.add(".grow(true)");
                    } else if (cell.growX) {
                        builder.add(".growX(true)");
                    } else if (cell.growY) {
                        builder.add(".grow(true)");
                    }
                } else {
                    if (cell.expandX && cell.expandY) {
                        builder.add(".expand(true)");
                    } else if (cell.expandX) {
                        builder.add(".expandX(true)");
                    } else if (cell.expandY) {
                        builder.add(".expand(true)");
                    }
        
                    if (cell.fillX && cell.fillY) {
                        builder.add(".fill(true)");
                    } else if (cell.fillX) {
                        builder.add(".fillX(true)");
                    } else if (cell.fillY) {
                        builder.add(".fill(true)");
                    }
                }
    
                if (cell.alignment != Align.center) {
                    builder.add(".align($T.$L)", Align.class, alignmentToName(table.alignment));
                }
    
                boolean minWidth = false, minHeight = false, maxWidth = false, maxHeight = false, preferredWidth = false, preferredHeight = false;
                if (Utils.isEqual(cell.minWidth, cell.minHeight, cell.maxWidth, cell.maxHeight, cell.preferredWidth,
                        cell.preferredHeight) && !Utils.isEqual(-1, cell.minWidth)) {
                    builder.add(".size($L)", cell.minWidth);
                    minWidth = true; minHeight = true; maxWidth = true; maxHeight = true; preferredWidth = true; preferredHeight = true;
                }
                if (!minWidth && !maxWidth && !preferredWidth && Utils.isEqual(cell.minWidth, cell.maxWidth, cell.preferredWidth) && !Utils.isEqual(-1, cell.minWidth)) {
                    builder.add(".width($L)", cell.minWidth);
                    minWidth = true; maxWidth = true; preferredWidth = true;
                }
                if (!minHeight && !maxHeight && !preferredHeight && Utils.isEqual(cell.minHeight, cell.maxHeight, cell.preferredHeight) && !Utils.isEqual(-1, cell.minHeight)) {
                    builder.add(".height($L)", cell.minHeight);
                    minHeight = true; maxHeight = true; preferredHeight = true;
                }
                if (!minWidth && !minHeight && Utils.isEqual(cell.minWidth, cell.minHeight) && !Utils.isEqual(-1, cell.minWidth)) {
                    builder.add(".minSize($L)", cell.minWidth);
                    minWidth = true; minHeight = true;
                }
                if (!maxWidth && !maxHeight && Utils.isEqual(cell.maxWidth, cell.maxHeight) && !Utils.isEqual(-1, cell.maxWidth)) {
                    builder.add(".maxSize($L)", cell.maxWidth);
                    maxWidth = true; maxHeight = true;
                }
                if (!preferredWidth && !preferredHeight && Utils.isEqual(cell.preferredWidth, cell.preferredHeight) && !Utils.isEqual(-1,
                        cell.preferredWidth)) {
                    builder.add(".preferredSize($L)", cell.preferredWidth);
                    preferredWidth = true; preferredHeight = true;
                }
                if (!minWidth && !Utils.isEqual(-1, cell.minWidth)) {
                    builder.add(".minWidth($L)", cell.minWidth);
                }
                if (!minHeight && !Utils.isEqual(-1, cell.minHeight)) {
                    builder.add(".minHeight($L)", cell.minHeight);
                }
                if (!maxWidth && !Utils.isEqual(-1, cell.maxWidth)) {
                    builder.add(".maxWidth($L)", cell.maxWidth);
                }
                if (!maxHeight && !Utils.isEqual(-1, cell.maxHeight)) {
                    builder.add(".maxHeight($L)", cell.maxHeight);
                }
                if (!preferredWidth && !Utils.isEqual(-1, cell.preferredWidth)) {
                    builder.add(".preferredWidth($L)", cell.preferredWidth);
                }
                if (!preferredHeight && !Utils.isEqual(-1, cell.preferredHeight)) {
                    builder.add(".preferredHeight($L)", cell.preferredHeight);
                }
    
                if (cell.uniformX && cell.uniformY) {
                    builder.add(".uniform(true)");
                } else if (cell.uniformX) {
                    builder.add(".uniformX(true)");
                } else if (cell.uniformY) {
                    builder.add(".uniformY(true)");
                }
                
                if (cell.colSpan > 1) builder.add(".colSpan($L)", cell.colSpan);
                
                builder.addStatement("");
                if (pair != null) {
                    variables.removeValue(pair.name, false);
                    usedVariables.add(pair.name);
                }
            }
            
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimButton) {
            var button = (SimButton) actor;
            if (button.style == null) return null;
            
            var builder = CodeBlock.builder();
            var variableName = createVariableName("button", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T ", Button.class);
            builder.addStatement("$L = new $T(skin$L)", variableName, Button.class,
                    button.style.name.equals("default") ? "" : ", \"" + button.style.name + "\"");
            
            if (button.name != null) builder.addStatement("$L.setName($L)", variableName, button.name);
            if (button.checked) builder.addStatement("$L.setChecked($L)", variableName, true);
            if (button.disabled) builder.addStatement("$L.setDisabled($L)", variableName, true);
            if (button.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, button.color.getName());
            
            if (!Utils.isEqual(0, button.padLeft, button.padRight, button.padTop, button.padBottom)) {
                if (Utils.isEqual(button.padLeft, button.padRight, button.padTop, button.padBottom)) {
                    builder.addStatement("$L.pad($L)", variableName, button.padLeft);
                } else {
                    if (!MathUtils.isZero(button.padLeft)) {
                        builder.addStatement("$L.padLeft($L)", variableName, button.padLeft);
                    }
                    if (!MathUtils.isZero(button.padRight)) {
                        builder.addStatement("$L.padRight($L)", variableName, button.padRight);
                    }
                    if (!MathUtils.isZero(button.padTop)) {
                        builder.addStatement("$L.padTop($L)", variableName, button.padTop);
                    }
                    if (!MathUtils.isZero(button.padBottom)) {
                        builder.addStatement("$L.padBottom($L)", variableName, button.padBottom);
                    }
                }
            }
            
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimCheckBox) {
            var checkBox = (SimCheckBox) actor;
            if (checkBox.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("checkBox", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T ", CheckBox.class);
            builder.addStatement("$L = new $T($S, skin$L)", variableName, CheckBox.class, checkBox.text,
                    checkBox.style.name.equals("default") ? "" : ", \"" + checkBox.style.name + "\"");
            
            if (checkBox.name != null) builder.addStatement("$L.setName($L)", variableName, checkBox.name);
            if (checkBox.checked) builder.addStatement("$L.setChecked($L)", variableName, true);
            if (checkBox.disabled) builder.addStatement("$L.setDisabled($L)", variableName, true);
            if (checkBox.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, checkBox.color.getName());
    
            if (!Utils.isEqual(0, checkBox.padLeft, checkBox.padRight, checkBox.padTop, checkBox.padBottom)) {
                if (Utils.isEqual(checkBox.padLeft, checkBox.padRight, checkBox.padTop, checkBox.padBottom)) {
                    builder.addStatement("$L.pad($L)", variableName, checkBox.padLeft);
                } else {
                    if (!MathUtils.isZero(checkBox.padLeft)) {
                        builder.addStatement("$L.padLeft($L)", variableName, checkBox.padLeft);
                    }
                    if (!MathUtils.isZero(checkBox.padRight)) {
                        builder.addStatement("$L.padRight($L)", variableName, checkBox.padRight);
                    }
                    if (!MathUtils.isZero(checkBox.padTop)) {
                        builder.addStatement("$L.padTop($L)", variableName, checkBox.padTop);
                    }
                    if (!MathUtils.isZero(checkBox.padBottom)) {
                        builder.addStatement("$L.padBottom($L)", variableName, checkBox.padBottom);
                    }
                }
            }
            
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimImage) {
            var image = (SimImage) actor;
            if (image.drawable == null) return null;
            
            var builder = CodeBlock.builder();
            var variableName = createVariableName("image", variables);
            if (usedVariables.contains(variableName)) builder.add("$T", Image.class);
            builder.addStatement("$L = new $T($S)", variableName, Image.class, image.drawable.name);
            if (image.name != null) builder.addStatement("$L.setName($L)", variableName, image.name);
            if (image.scaling != null) builder.addStatement("$L.setScaling($L)", variableName, image.scaling.name());
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimImageButton) {
            var imageButton = (SimImageButton) actor;
            if (imageButton.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("imageButton", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T", ImageButton.class);
            builder.addStatement("$L = new $T(skin$L)", variableName, ImageButton.class,
                    imageButton.style.name.equals("default") ? "" : ", \"" + imageButton.style.name + "\"");
            
            if (imageButton.name != null) builder.addStatement("$L.setName($L)", variableName, imageButton.name);
            if (imageButton.checked) builder.addStatement("$L.setChecked($L)", variableName, true);
            if (imageButton.disabled) builder.addStatement("$L.setDisabled($L)", variableName, true);
            if (imageButton.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, imageButton.color.getName());
    
            if (!Utils.isEqual(0, imageButton.padLeft, imageButton.padRight, imageButton.padTop, imageButton.padBottom)) {
                if (Utils.isEqual(imageButton.padLeft, imageButton.padRight, imageButton.padTop, imageButton.padBottom)) {
                    builder.addStatement("$L.pad($L)", variableName, imageButton.padLeft);
                } else {
                    if (!MathUtils.isZero(imageButton.padLeft)) {
                        builder.addStatement("$L.padLeft($L)", variableName, imageButton.padLeft);
                    }
                    if (!MathUtils.isZero(imageButton.padRight)) {
                        builder.addStatement("$L.padRight($L)", variableName, imageButton.padRight);
                    }
                    if (!MathUtils.isZero(imageButton.padTop)) {
                        builder.addStatement("$L.padTop($L)", variableName, imageButton.padTop);
                    }
                    if (!MathUtils.isZero(imageButton.padBottom)) {
                        builder.addStatement("$L.padBottom($L)", variableName, imageButton.padBottom);
                    }
                }
            }
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimImageTextButton) {
            var imageTextButton = (SimTextButton) actor;
            if (imageTextButton.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("imageTextButton", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T ", ImageTextButton.class);
            builder.addStatement("$L = new $T($S, skin$3L)", variableName, ImageTextButton.class, imageTextButton.text,
                    imageTextButton.style.name.equals("default") ? "" : ", \"" + imageTextButton.style.name + "\"");
            
            if (imageTextButton.name != null) builder.addStatement("$L.setName($L)", variableName, imageTextButton.name);
            if (imageTextButton.checked) builder.addStatement("$L.setChecked($L)", variableName, true);
            if (imageTextButton.disabled) builder.addStatement("$L.setDisabled($L)", variableName, true);
            if (imageTextButton.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, imageTextButton.color.getName());
    
            if (!Utils.isEqual(0, imageTextButton.padLeft, imageTextButton.padRight, imageTextButton.padTop, imageTextButton.padBottom)) {
                if (Utils.isEqual(imageTextButton.padLeft, imageTextButton.padRight, imageTextButton.padTop, imageTextButton.padBottom)) {
                    builder.addStatement("$L.pad($L)", variableName, imageTextButton.padLeft);
                } else {
                    if (!MathUtils.isZero(imageTextButton.padLeft)) {
                        builder.addStatement("$L.padLeft($L)", variableName, imageTextButton.padLeft);
                    }
                    if (!MathUtils.isZero(imageTextButton.padRight)) {
                        builder.addStatement("$L.padRight($L)", variableName, imageTextButton.padRight);
                    }
                    if (!MathUtils.isZero(imageTextButton.padTop)) {
                        builder.addStatement("$L.padTop($L)", variableName, imageTextButton.padTop);
                    }
                    if (!MathUtils.isZero(imageTextButton.padBottom)) {
                        builder.addStatement("$L.padBottom($L)", variableName, imageTextButton.padBottom);
                    }
                }
            }
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimLabel) {
            var label = (SimLabel) actor;
            if (label.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("label", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T ", Label.class);
            builder.addStatement("$L = new $T($S, skin$L)", variableName, Label.class, label.text,
                    label.style.name.equals("default") ? "" : ", \"" + label.style.name + "\"");
                
            if (label.name != null) builder.addStatement("$L.setName($L)", variableName, label.name);
            
            if (label.textAlignment != Align.left) {
                builder.add(".align($T.$L)", Align.class, alignmentToName(label.textAlignment));
            }
    
            if (label.ellipsis) builder.addStatement("$L.setEllipsis($L)", variableName, true);
            if (label.ellipsisString != null) builder.addStatement("$L.setEllipsis($L)", variableName, label.ellipsisString);
            if (label.wrap) builder.addStatement("$L.setWrap($L)", variableName, true);
            if (label.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, label.color.getName());
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimList) {
            var list = (SimList) actor;
            if (list.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("list", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T<String> ", List.class);
            builder.addStatement("$L = new $T<String>(skin$L)", variableName, List.class,
                    list.style.name.equals("default") ? "" : ", \"" + list.style.name + "\"");
            
            if (list.name != null) builder.addStatement("$L.setName($L)", variableName, list.name);
            if (list.list.size > 0) {
                builder.add("$L.setItems(", variableName);
                boolean addComma = false;
                for (var item : list.list) {
                    builder.add((addComma ? ", " : "") + "$S", item);
                    addComma = true;
                }
                builder.addStatement(")");
            }
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimProgressBar) {
            var progressBar = (SimProgressBar) actor;
            if (progressBar.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("progressBar", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T ", ProgressBar.class);
            builder.addStatement("$L = new $T($L, $L, $L, $L, skin$L)", variableName, ProgressBar.class,
                    progressBar.minimum, progressBar.maximum, progressBar.increment, progressBar.increment,
                    progressBar.style.name.equals("default") ? "" : ", \"" + progressBar.style.name + "\"");
            
            if (progressBar.name != null) builder.addStatement("$L.setName($L)", variableName, progressBar.name);
            if (MathUtils.isZero(progressBar.value)) builder.addStatement("$L.setValue($Lf)", variableName, progressBar.value);
            if (progressBar.vertical) builder.addStatement("$L.setVertical($L)", variableName, progressBar.vertical);
            if (MathUtils.isZero(progressBar.animationDuration)) builder.addStatement("$L.setAnimationDuration($Lf)", variableName, progressBar.animationDuration);
            if (progressBar.animateInterpolation != null) builder.addStatement("$L.setAnimateInterpolation($T.$L)", variableName,
                    Interpolation.class, progressBar.animateInterpolation.code);
            if (progressBar.round) builder.addStatement("$L.setRound($L)", variableName, progressBar.round);
            if (progressBar.visualInterpolation != null) builder.addStatement("$L.setVisualInterpolation($T.$L)", variableName,
                    Interpolation.class, progressBar.visualInterpolation.code);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimSelectBox) {
            var selectBox = (SimSelectBox) actor;
            if (selectBox.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("selectBox", variables);
            if (!usedVariables.contains(variableName)) builder.add("$T<String> ", SelectBox.class);
            builder.addStatement("$L = new $T(skin$L)", variableName, SelectBox.class,
                    selectBox.style.name.equals("default") ? "" : ", \"" + selectBox.style.name + "\"");
            
            if (selectBox.name != null) builder.addStatement("$L.setName($L)", variableName, selectBox.name);
            if (selectBox.disabled) builder.addStatement("$L.setDisabled($L)", variableName, selectBox.disabled);
            if (selectBox.maxListCount != 0) builder.addStatement("$L.setMaxListCount($L)", variableName, selectBox.maxListCount);
    
            if (selectBox.list.size > 0) {
                builder.add("$L.setItems(", variableName);
                boolean addComma = false;
                for (var item : selectBox.list) {
                    builder.add((addComma ? ", " : "") + "$S", item);
                    addComma = true;
                }
                builder.addStatement(")");
            }
    
            if (selectBox.alignment != Align.center) {
                builder.add(".align($T.$L)", Align.class, alignmentToName(selectBox.alignment));
            }
    
            if (selectBox.selected != 0) builder.addStatement("$L.setSelected($L)", variableName, selectBox.selected);
            if (selectBox.scrollingDisabled) builder.addStatement("$L.setScrollingDisabled($L)", variableName, selectBox.scrollingDisabled);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimSlider) {
            var slider = (SimSlider) actor;
            if (slider.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("slider", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", Slider.class, variableName,
                        slider.style.name.equals("default") ? "" : ", \"" + slider.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", Slider.class, variableName,
                        slider.style.name.equals("default") ? "" : ", \"" + slider.style.name + "\"");
            }
            if (slider.name != null) builder.addStatement("$L.setName($L)", variableName, slider.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimTextButton) {
            var textButton = (SimTextButton) actor;
            if (textButton.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("textButton", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", TextButton.class, variableName,
                        textButton.style.name.equals("default") ? "" : ", \"" + textButton.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", TextButton.class, variableName,
                        textButton.style.name.equals("default") ? "" : ", \"" + textButton.style.name + "\"");
            }
            if (textButton.name != null) builder.addStatement("$L.setName($L)", variableName, textButton.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimTextField) {
            var textField = (SimTextField) actor;
            if (textField.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("textField", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", TextField.class, variableName,
                        textField.style.name.equals("default") ? "" : ", \"" + textField.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", TextField.class, variableName,
                        textField.style.name.equals("default") ? "" : ", \"" + textField.style.name + "\"");
            }
            if (textField.name != null) builder.addStatement("$L.setName($L)", variableName, textField.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimTextArea) {
            var textArea = (SimTextArea) actor;
            if (textArea.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("textArea", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", TextArea.class, variableName,
                        textArea.style.name.equals("default") ? "" : ", \"" + textArea.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", TextArea.class, variableName,
                        textArea.style.name.equals("default") ? "" : ", \"" + textArea.style.name + "\"");
            }
            if (textArea.name != null) builder.addStatement("$L.setName($L)", variableName, textArea.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimTouchPad) {
            var touchPad = (SimTouchPad) actor;
            if (touchPad.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("touchPad", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", Touchpad.class, variableName,
                        touchPad.style.name.equals("default") ? "" : ", \"" + touchPad.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", Touchpad.class, variableName,
                        touchPad.style.name.equals("default") ? "" : ", \"" + touchPad.style.name + "\"");
            }
            if (touchPad.name != null) builder.addStatement("$L.setName($L)", variableName, touchPad.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimContainer) {
            var container = (SimContainer) actor;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("container", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", Container.class);
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", Container.class);
            }
            if (container.name != null) builder.addStatement("$L.setName($L)", variableName, container.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimHorizontalGroup) {
            var horizontalGroup = (SimHorizontalGroup) actor;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("horizontalGroup", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", HorizontalGroup.class, variableName);
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", HorizontalGroup.class, variableName);
            }
            if (horizontalGroup.name != null) builder.addStatement("$L.setName($L)", variableName, horizontalGroup.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimScrollPane) {
            var scrollPane = (SimScrollPane) actor;
            if (scrollPane.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("scrollPane", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", ScrollPane.class, variableName,
                        scrollPane.style.name.equals("default") ? "" : ", \"" + scrollPane.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", ScrollPane.class, variableName,
                        scrollPane.style.name.equals("default") ? "" : ", \"" + scrollPane.style.name + "\"");
            }
            if (scrollPane.name != null) builder.addStatement("$L.setName($L)", variableName, scrollPane.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimStack) {
            var stack = (SimStack) actor;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("stack", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T()", Stack.class, variableName);
            } else {
                builder.addStatement("$1T $2L = new $1T()", Stack.class, variableName);
            }
            if (stack.name != null) builder.addStatement("$L.setName($L)", variableName, stack.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimSplitPane) {
            var splitPane = (SimSplitPane) actor;
            if (splitPane.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("splitPane", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", SplitPane.class, variableName,
                        splitPane.style.name.equals("default") ? "" : ", \"" + splitPane.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", SplitPane.class, variableName,
                        splitPane.style.name.equals("default") ? "" : ", \"" + splitPane.style.name + "\"");
            }
            if (splitPane.name != null) builder.addStatement("$L.setName($L)", variableName, splitPane.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimTree) {
            var tree = (SimTree) actor;
            if (tree.style == null) return null;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("tree", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T(skin$3L)", Tree.class, variableName,
                        tree.style.name.equals("default") ? "" : ", \"" + tree.style.name + "\"");
            } else {
                builder.addStatement("$1T $2L = new $1T(skin$3L)", Tree.class, variableName,
                        tree.style.name.equals("default") ? "" : ", \"" + tree.style.name + "\"");
            }
            if (tree.name != null) builder.addStatement("$L.setName($L)", variableName, tree.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else if (actor instanceof SimVerticalGroup) {
            var verticalGroup = (SimVerticalGroup) actor;
    
            var builder = CodeBlock.builder();
            var variableName = createVariableName("verticalGroup", variables);
            if (!usedVariables.contains(variableName)) {
                builder.addStatement("$2L = new $1T()", VerticalGroup.class, variableName);
            } else {
                builder.addStatement("$1T $2L = new $1T()", VerticalGroup.class, variableName);
            }
            if (verticalGroup.name != null) builder.addStatement("$L.setName($L)", variableName, verticalGroup.name);
    
            return new WidgetNamePair(builder.build(), variableName);
        } else {
            return null;
        }
    }
    
    private static class WidgetNamePair {
        CodeBlock codeBlock;
        String name;
    
        public WidgetNamePair(CodeBlock codeBlock, String name) {
            this.codeBlock = codeBlock;
            this.name = name;
        }
    }
    
    private static String createVariableName(String name, Array<String> variables) {
        String returnValue = name;
        int index = 0;
        while (variables.contains(returnValue, false)) {
            index++;
            returnValue = name + index;
        }
        variables.add(returnValue);
        return returnValue;
    }
    
    private static String alignmentToName(int align) {
        StringBuilder buffer = new StringBuilder(13);
        if ((align & Align.top) != 0)
            buffer.append("top");
        else if ((align & Align.bottom) != 0)
            buffer.append("bottom");
        else {
            if ((align & Align.left) != 0)
                buffer.append("left");
            else if ((align & Align.right) != 0)
                buffer.append("right");
            else
                buffer.append("center");
            return buffer.toString();
        }
        if ((align & Align.left) != 0)
            buffer.append("Left");
        else if ((align & Align.right) != 0)
            buffer.append("Right");
        return buffer.toString();
    }
    
    private static MethodSpec renderMethod() {
        return MethodSpec.methodBuilder("render")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("Gdx.gl.glClearColor(1, 1, 1, 1)")
                .addStatement("Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)")
                .addStatement("stage.act()")
                .addStatement("stage.draw()")
                .returns(void.class).build();
    }
    
    private static MethodSpec resizeMethod() {
        return MethodSpec.methodBuilder("resize")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Integer.TYPE, "width")
                .addParameter(Integer.TYPE, "height")
                .addStatement("stage.getViewport().update(width, height, true)")
                .returns(void.class).build();
    }
    
    private static MethodSpec disposeMethod() {
        return MethodSpec.methodBuilder("dispose")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("stage.dispose()")
                .addStatement("skin.dispose")
                .returns(void.class).build();
    }
}