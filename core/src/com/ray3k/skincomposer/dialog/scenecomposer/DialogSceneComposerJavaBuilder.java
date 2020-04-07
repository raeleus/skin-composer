package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimRootGroup;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimTable;
import com.ray3k.skincomposer.utils.Utils;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

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
        if (simActor instanceof SimRootGroup) {
            var simRootGroup = (SimRootGroup) simActor;
            var builder = CodeBlock.builder();
            
            for (var child : simRootGroup.children) {
                var pair = createWidget(child, variables);
                builder.add(pair.codeBlock);
                builder.addStatement("stage.addActor($L)", pair.name);
            }
            
            return new WidgetNamePair(builder.build(), null);
        } else if (simActor instanceof SimTable) {
            var simTable = (SimTable) simActor;
            var builder = CodeBlock.builder();
            var variableName = createVariableName("table", variables);
            builder.addStatement("$1T $2L = new $1T()", Table.class, variableName);
            if (simTable.name != null) builder.addStatement("$L.setName($L)", variableName, simTable.name);
            if (simTable.background != null) builder.addStatement("$L.setBackground(skin.getDrawable($S))", variableName, simTable.background.name);
            if (simTable.color != null) builder.addStatement("$L.setColor(skin.getColor($S))", variableName, simTable.color.getName());
            
            if (!Utils.isEqual(0, simTable.padLeft, simTable.padRight, simTable.padTop, simTable.padBottom)) {
                if (Utils.isEqual(simTable.padLeft, simTable.padRight, simTable.padTop, simTable.padBottom)) {
                    builder.addStatement("$L.pad($L)", variableName, simTable.padLeft);
                } else {
                    if (!MathUtils.isZero(simTable.padLeft)) {
                        builder.addStatement("$L.padLeft($L)", variableName, simTable.padLeft);
                    }
                    if (!MathUtils.isZero(simTable.padRight)) {
                        builder.addStatement("$L.padRight($L)", variableName, simTable.padRight);
                    }
                    if (!MathUtils.isZero(simTable.padTop)) {
                        builder.addStatement("$L.padTop($L)", variableName, simTable.padTop);
                    }
                    if (!MathUtils.isZero(simTable.padBottom)) {
                        builder.addStatement("$L.padBottom($L)", variableName, simTable.padBottom);
                    }
                }
            }
            
            if (simTable.alignment != Align.center) {
                builder.addStatement("$L.align($T.$L)", variableName, Align.class, alignmentToName(simTable.alignment));
            }
            if (simTable.fillParent) builder.addStatement("$L.setFillParent(true)", variableName);
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
        return name;
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
