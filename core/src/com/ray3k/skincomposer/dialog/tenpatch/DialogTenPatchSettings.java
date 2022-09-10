package com.ray3k.skincomposer.dialog.tenpatch;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.ResizeFourArrowListener;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ResizeWidget;
import com.ray3k.stripe.Spinner;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.utils.Utils.onChange;

public class DialogTenPatchSettings extends PopTable {
    private DrawableData drawableData;
    private DrawableData workingData;
    private TenPatchDrawable preview;
    
    public DialogTenPatchSettings(DrawableData drawableData, TenPatchDrawable tenPatchDrawable) {
        super(skin, "dialog");
        this.drawableData = drawableData;
        workingData = new DrawableData(drawableData);
        preview = new TenPatchDrawable(tenPatchDrawable);
        
        setKeepCenteredInWindow(true);
        setKeepSizedWithinStage(true);
        setModal(true);
        
        var root = new Table();
        root.pad(10);
        
        var scrollPane = new ScrollPane(root, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setFlickScroll(false);
        add(scrollPane);
        scrollPane.addListener(scrollFocusListener);
        
        var label = new Label("TenPatch Settings", skin);
        root.add(label);
    
        root.defaults().growX().space(5);
        root.row();
        var container = new Container<>();
        root.add(container).height(250).grow();
        
        var resizer = new ResizeWidget(null, skin);
        resizer.setName("resizer");
        resizer.setTouchable(Touchable.enabled);
        resizer.setResizingFromCenter(true);
        resizer.setAllowDragging(false);
        
        var resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.NESWResize);
        resizer.getBottomLeftHandle().addListener(resizeFourArrowListener);
        resizer.getTopRightHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.NWSEResize);
        resizer.getTopLeftHandle().addListener(resizeFourArrowListener);
        resizer.getBottomRightHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.VerticalResize);
        resizer.getBottomHandle().addListener(resizeFourArrowListener);
        resizer.getTopHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.HorizontalResize);
        resizer.getLeftHandle().addListener(resizeFourArrowListener);
        resizer.getRightHandle().addListener(resizeFourArrowListener);
        container.setActor(resizer);
        container.fill();
        container.setBackground(skin.getDrawable("white"));
        
        var image = new Image(preview);
        resizer.setActor(image);
        
        root.row();
        var table = new Table();
        root.add(table);
        
        var button = new Button(skin, "colorwheel");
        table.add(button).expandX().right();
        button.addListener(handListener);
        onChange(button, () -> dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
            if (colorData == null) {
                container.setColor(Color.WHITE);
            } else {
                container.setColor(colorData.color);
            }
        }, null));
        
        root.row();
        label = new Label("Bounds", skin, "black-underline");
        root.add(label);
        
        root.row();
        table = new Table();
        root.add(table);
        
        table.defaults().space(5);
        label = new Label("Min Width:", skin);
        table.add(label).right();
        
        var spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.minWidth);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.minWidth = ((Spinner) actor).getValueAsInt();
                preview.setMinWidth(workingData.minWidth);
            }
        });
    
        label = new Label("Min Height:", skin);
        table.add(label).right();
    
        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.minHeight);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.minHeight = ((Spinner) actor).getValueAsInt();
                preview.setMinHeight(workingData.minHeight);
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
        imageButton.addListener(handListener);
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.colorName == null ? Color.WHITE : jsonData.getColorByName(workingData.tenPatchData.colorName).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                suppressKeyInputListeners(true);
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                    if (colorData == null) {
                        workingData.tenPatchData.colorName = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                        preview.setColor(Color.WHITE);
                    } else {
                        workingData.tenPatchData.colorName = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                        preview.setColor(colorData.color);
                    }
                    suppressKeyInputListeners(false);
                }, null);
            }
        });
    
        table.row();
        label = new Label("Gradient Upper Left:", skin);
        table.add(label).right();
        var textTooltip = (Main.makeTooltip("Gradient colors override Color", tooltipManager, skin));
        label.addListener(textTooltip);
    
        imageButton = new ImageButton(skin, "color");
        table.add(imageButton).spaceRight(15);
        imageButton.addListener(handListener);
        imageButton.addListener(textTooltip);
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color2Name == null ? Color.WHITE : jsonData.getColorByName(workingData.tenPatchData.color2Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                    if (colorData == null) {
                        workingData.tenPatchData.color2Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                        preview.setColor2(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color2Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                        preview.setColor2(colorData.color);
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
        imageButton.addListener(handListener);
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color3Name == null ? Color.WHITE : jsonData.getColorByName(workingData.tenPatchData.color3Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color3Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                        preview.setColor3(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color3Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                        preview.setColor3(colorData.color);
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
        imageButton.addListener(handListener);
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color1Name == null ? Color.WHITE : jsonData.getColorByName(workingData.tenPatchData.color1Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color1Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                        preview.setColor1(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color1Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                        preview.setColor1(colorData.color);
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
        imageButton.addListener(handListener);
        image = new Image(skin, "white");
        image.setScaling(Scaling.stretch);
        imageButton.add(image).size(15, 15).space(5);
        image.setColor(workingData.tenPatchData.color4Name == null ? Color.WHITE : jsonData.getColorByName(workingData.tenPatchData.color4Name).color);
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialogFactory.showDialogColors(new StyleProperty(), (colorData, pressedCancel) -> {
                
                    if (colorData == null) {
                        workingData.tenPatchData.color4Name = null;
                        ((ImageButton) actor).getCells().peek().getActor().setColor(Color.WHITE);
                        preview.setColor4(Color.WHITE);
                    } else {
                        workingData.tenPatchData.color4Name = colorData.getName();
                        ((ImageButton) actor).getCells().peek().getActor().setColor(colorData.color);
                        preview.setColor4(colorData.color);
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
        
        button = new Button(skin, "switch");
        button.setChecked(workingData.tenPatchData.tile);
        table.add(button).left();
        button.addListener(handListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.tile = ((Button) actor).isChecked();
                preview.setTiling(workingData.tenPatchData.tile);
            }
        });
        
        table.row();
        label = new Label("Offset X (Start):", skin);
        table.add(label).right();
        textTooltip = (Main.makeTooltip("Only relevant if Tiling is enabled.", tooltipManager, skin));
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetX);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetX = ((Spinner) actor).getValueAsInt();
                preview.setOffsetX(workingData.tenPatchData.offsetX);
            }
        });
    
        label = new Label("Offset Y (Start):", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetY);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetY = ((Spinner) actor).getValueAsInt();
                preview.setOffsetY(workingData.tenPatchData.offsetY);
            }
        });
    
        table.row();
        label = new Label("Offset X Speed:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetXspeed);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetXspeed = ((Spinner) actor).getValueAsInt();
                preview.setOffsetXspeed(workingData.tenPatchData.offsetXspeed);
            }
        });
    
        label = new Label("Offset Y Speed:", skin);
        table.add(label).right();
        label.addListener(textTooltip);
    
        spinner = new Spinner(0, 1, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setValue(workingData.tenPatchData.offsetYspeed);
        table.add(spinner).minWidth(100).spaceRight(15);
        spinner.addListener(textTooltip);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        spinner.getTextField().addListener(ibeamListener);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.offsetYspeed = ((Spinner) actor).getValueAsInt();
                preview.setOffsetYspeed(workingData.tenPatchData.offsetYspeed);
            }
        });
    
        root.row();
        label = new Label("Crush Mode", skin, "black-underline");
        root.add(label);
    
        root.row();
        table = new Table();
        root.add(table);
    
        table.defaults().space(5);
        label = new Label("Crush Mode:", skin);
        table.add(label).right();
    
        var selectBox = new SelectBox<String>(skin);
        selectBox.setItems("Shrink", "Crop", "Crop-Reversed", "None");
        selectBox.setSelectedIndex(workingData.tenPatchData.crushMode);
        table.add(selectBox);
        selectBox.addListener(handListener);
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                workingData.tenPatchData.crushMode = selectBox.getSelectedIndex();
                preview.crushMode = workingData.tenPatchData.crushMode;
            }
        });
        
        root.row();
        table = new Table();
        table.pad(10);
        root.add(table);
        
        table.defaults().uniform().fill().space(10);
        var textButton = new TextButton("OK", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                result(true);
            }
        });
        
        textButton = new TextButton("Cancel", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                result(false);
            }
        });
        
        key(Keys.ESCAPE, () -> result(false));
        
        key(Keys.ENTER, () -> result(true));
        key(Keys.NUMPAD_ENTER, () -> result(true));
    }
    
    protected void result(Object object) {
        hide();
        
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
