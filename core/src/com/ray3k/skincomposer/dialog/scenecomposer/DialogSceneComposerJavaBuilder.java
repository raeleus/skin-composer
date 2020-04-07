package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimButton;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTable;
import com.ray3k.skincomposer.utils.Utils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import jdk.jshell.execution.Util;

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
                .addCode(createWidget(rootActor, new Array<>()).codeBlock)
                .returns(void.class).build();
    }
    
    private static WidgetNamePair createWidget(SimActor simActor, Array<String> variables) {
        if (simActor == null) return null;
        else if (simActor instanceof SimRootGroup) {
            var simRootGroup = (SimRootGroup) simActor;
            var builder = CodeBlock.builder();
            
            for (var child : simRootGroup.children) {
                var pair = createWidget(child, variables);
                builder.add(pair.codeBlock);
                builder.addStatement("stage.addActor($L)", pair.name);
            }
            
            return new WidgetNamePair(builder.build(), null);
        } else if (simActor instanceof SimTable) {
            var table = (SimTable) simActor;
            var builder = CodeBlock.builder();
            var variableName = createVariableName("table", variables);
            builder.addStatement("$1T $2L = new $1T()", Table.class, variableName);
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
                
                WidgetNamePair pair = createWidget(cell.child, variables);
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
            }
            
            return new WidgetNamePair(builder.build(), variableName);
        } else if (simActor instanceof SimButton) {
            var button = (SimButton) simActor;
            if (button.style == null) return null;
            
            var builder = CodeBlock.builder();
            var variableName = createVariableName("button", variables);
            builder.addStatement("$1T $2L = new $1T(skin$3L)", Button.class, variableName, button.style.name.equals("default") ? "" : ", \"" + button.style.name + "\"");
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
