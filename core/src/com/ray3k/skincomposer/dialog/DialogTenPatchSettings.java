package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;

import javax.xml.crypto.Data;

public class DialogTenPatchSettings extends Dialog {
    private DrawableData drawableData;
    private DrawableData workingData;
    private Skin skin;
    private Main main;
    
    public DialogTenPatchSettings(DrawableData drawableData, Skin skin, Main main) {
        super("TenPatch Settings", skin, "bg");
        this.drawableData = drawableData;
        workingData = new DrawableData(drawableData);
        this.skin = skin;
        this.main = main;
    
        getTitleTable().padLeft(5);
        
        var root = getContentTable();
        root.pad(10);
        
        root.defaults().growX();
        var label = new Label("Bounds", skin, "black-underline");
        root.add(label);
        
        root.row();
        var table = new Table();
        root.add(table);
        
        table.defaults().space(5);
        label = new Label("Min Width:", skin);
        table.add(label).right();
        
        var spinner = new Spinner(0, 1, true, com.ray3k.skincomposer.Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.minWidth);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.minWidth = ((Spinner) actor).getValueAsInt();
            }
        });
    
        label = new Label("Min Height:", skin);
        table.add(label).right();
    
        spinner = new Spinner(0, 1, true, com.ray3k.skincomposer.Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.minHeight);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.minHeight = ((Spinner) actor).getValueAsInt();
            }
        });
    
        root.row();
        label = new Label("Colors", skin, "black-underline");
        root.add(label);
    
        root.row();
        table = new Table();
        root.add(table);
    
        table.defaults().space(5);
        label = new Label("Color:", skin);
        table.add(label).right();
    
        var imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceRight(15);
        imageButton.addListener(main.getHandListener());
        var image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.colorName == null ? Color.WHITE : main.getJsonData().getColorByName(workingData.tenPatchData.colorName).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {

                    if (colorData == null) {
                        workingData.tenPatchData.colorName = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                    } else {
                        workingData.tenPatchData.colorName = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                    }
                }, null);
            }
        });
    
        table.row();
        label = new Label("Gradient Upper Left:", skin);
        table.add(label).right();
        var textTooltip = new TextTooltip("Gradient colors override Color", main.getTooltipManager(), skin);
        label.addListener(textTooltip);
    
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceRight(15);
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(textTooltip);
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color2Name == null ? Color.WHITE : main.getJsonData().getColorByName(workingData.tenPatchData.color2Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color2Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color2Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                    }
                }, null);
            }
        });
    
        label = new Label("Gradient Upper Right:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceRight(15);
        imageButton.addListener(textTooltip);
        imageButton.addListener(main.getHandListener());
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color3Name == null ? Color.WHITE : main.getJsonData().getColorByName(workingData.tenPatchData.color3Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color3Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color3Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                    }
                }, null);
            }
        });
    
        table.row();
        label = new Label("Gradient Lower Left:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceRight(15);
        imageButton.addListener(textTooltip);
        imageButton.addListener(main.getHandListener());
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color1Name == null ? Color.WHITE : main.getJsonData().getColorByName(workingData.tenPatchData.color1Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color1Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color1Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                    }
                }, null);
            }
        });
    
        label = new Label("Gradient Lower Right:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceRight(15);
        imageButton.addListener(textTooltip);
        imageButton.addListener(main.getHandListener());
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color4Name == null ? Color.WHITE : main.getJsonData().getColorByName(workingData.tenPatchData.color4Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), colorData -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color4Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color4Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                    }
                }, null);
            }
        });
    
        root.row();
        label = new Label("Tiling", skin, "black-underline");
        root.add(label);
    
        root.row();
        table = new Table();
        root.add(table);
    
        table.defaults().space(5);
        label = new Label("Tiling:", skin);
        table.add(label).right();
        
        var button = new Button(skin, "switch");
        button.setChecked(workingData.tenPatchData.tile);
        table.add(button).left();
        button.addListener(main.getHandListener());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.tile = ((Button) actor).isChecked();
            }
        });
        
        table.row();
        label = new Label("Offset X (Start):", skin);
        table.add(label).right();
        textTooltip = new TextTooltip("Only relevant if Tiling is enabled.", main.getTooltipManager(), skin);
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, com.ray3k.skincomposer.Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetX);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetX = ((Spinner) actor).getValueAsInt();
            }
        });
    
        label = new Label("Offset Y (Start):", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, com.ray3k.skincomposer.Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetY);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetY = ((Spinner) actor).getValueAsInt();
            }
        });
    
        table.row();
        label = new Label("Offset X Speed:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, com.ray3k.skincomposer.Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetXspeed);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetXspeed = ((Spinner) actor).getValueAsInt();
            }
        });
    
        label = new Label("Offset Y Speed:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, com.ray3k.skincomposer.Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetYspeed);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetYspeed = ((Spinner) actor).getValueAsInt();
            }
        });
    
        getButtonTable().pad(10);
        getButtonTable().defaults().uniform().fill();
        var textButton = new TextButton("OK", skin);
        button(textButton, true);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(main.getHandListener());
        
        key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true);
    }
    
    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            drawableData.set(workingData);
            fire(new DialogTenPatchSettingsEvent(drawableData));
        }
    }
    
    public static class DialogTenPatchSettingsEvent extends Event {
        public DrawableData drawableData;
    
        public DialogTenPatchSettingsEvent(DrawableData drawableData) {
            this.drawableData = drawableData;
        }
    }
    
    public abstract static class DialogTenPatchSettingsListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof DialogTenPatchSettingsEvent) {
                settingsUpdated((DialogTenPatchSettingsEvent) event);
            }
            return false;
        }
        
        public abstract void settingsUpdated(DialogTenPatchSettingsEvent event);
    }
}
