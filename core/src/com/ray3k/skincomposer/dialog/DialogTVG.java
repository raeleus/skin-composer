/*
 * The MIT License
 *
 * Copyright 2024 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ResizeWidget;
import dev.lyze.gdxtinyvg.TinyVG;
import dev.lyze.gdxtinyvg.scene2d.TinyVGDrawable;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.utils.Utils.*;

/**
 * @author Raymond Buckley
 */
public class DialogTVG extends PopTable {
    private DrawableData drawableData;
    private TinyVGDrawable drawable;
    private TinyVG tvg;
    
    public DialogTVG(DrawableData drawableData) {
        super(skin, "dialog");
        this.drawableData = drawableData;
        drawable = new TinyVGDrawable((TinyVGDrawable) atlasData.getDrawablePairs().get(drawableData));
        tvg = drawable.tvg;
        
        populate();
    }
    
    private void populate() {
        pad(20);
        setKeepCenteredInWindow(true);
        setModal(true);
        
        defaults().space(10);
        var table = new Table();
        add(table);
        
        table.defaults().space(10);
        table.add().uniformX();
        
        var label = new Label("TinyVGDrawable", skin);
        table.add(label);
    
        var textButton = new TextButton("More info...", skin);
        table.add(textButton).uniformX();
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showAboutDialog();
            }
        });
        
        row();
        var image = new Image(drawable);
        var resizer = new ResizeWidget(image, skin);
        add(resizer).size(300, 300);
        Utils.applyResizeArrowListener(resizer);
        
        defaults().left().fillX();
        row();
        table = new Table();
        add(table);
    
        table.defaults().space(10);
        label = new Label("Name:", skin);
        table.add(label);
        
        var nameTextField = new TextField("", skin);
        nameTextField.setText(drawableData.name);
        table.add(nameTextField).growX();
        nameTextField.addListener(ibeamListener);
        
//        row();
//        table = new Table();
//        add(table);
//
//        table.defaults().space(10);
//        label = new Label("Origin X:", skin);
//        table.add(label);
//
//        var originXspinner = new Spinner(0, 1, true, Orientation.HORIZONTAL, skin);
//        originXspinner.setValue(drawableData.tvgData.originX);
//        table.add(originXspinner);
//        onChange(originXspinner, () -> {
//            drawableData.tvgData.originX = originXspinner.getValueAsInt();
//            tvg.setOriginX(drawableData.tvgData.originX);
//        });
//        originXspinner.getButtonMinus().addListener(handListener);
//        originXspinner.getButtonPlus().addListener(handListener);
//        originXspinner.getTextField().addListener(ibeamListener);
//
//        label = new Label("Origin Y:", skin);
//        table.add(label);
//
//        var originYspinner = new Spinner(0, 1, true, Orientation.HORIZONTAL, skin);
//        originYspinner.setValue(drawableData.tvgData.originY);
//        table.add(originYspinner);
//        onChange(originYspinner, () -> {
//            drawableData.tvgData.originY = originYspinner.getValueAsInt();
//            tvg.setOriginY(drawableData.tvgData.originY);
//        });
//        originYspinner.getButtonMinus().addListener(handListener);
//        originYspinner.getButtonPlus().addListener(handListener);
//        originYspinner.getTextField().addListener(ibeamListener);
//
//        var textButton = new TextButton("Center", skin);
//        table.add(textButton);
//        onChange(textButton, () -> {
//            float x = tvg.getUnscaledWidth() / 2;
//            float y = tvg.getUnscaledHeight() / 2;
//
//            originXspinner.setValue(x);
//            drawableData.tvgData.originX = x;
//            tvg.setOriginX(x);
//
//            originYspinner.setValue(y);
//            drawableData.tvgData.originY = y;
//            tvg.setOriginY(y);
//        });
//        textButton.addListener(handListener);
//
//        row();
//        table = new Table();
//        add(table);
//
//        table.defaults().space(10);
//        label = new Label("Rotation:", skin);
//        table.add(label);
//
//        var rotationSlider = new Slider(0, 360, 1, false, skin);
//        rotationSlider.setValue(drawableData.tvgData.rotation);
//        table.add(rotationSlider).growX();
//        rotationSlider.addListener(handListener);
//
//        var rotationValue = new Label("", skin);
//        rotationValue.setText(MathUtils.round(drawableData.tvgData.rotation));
//        rotationValue.setAlignment(Align.center);
//        table.add(rotationValue).width(100);
//
//        onChange(rotationSlider, () -> {
//            drawableData.tvgData.rotation = rotationSlider.getValue();
//            tvg.setRotation(drawableData.tvgData.rotation);
//            rotationValue.setText(MathUtils.round(drawableData.tvgData.rotation));
//        });
    
//        row();
//        table = new Table();
//        add(table);
//
//        table.defaults().space(10);
//        label = new Label("Shear X:", skin);
//        table.add(label);
//
//        var shearXspinner = new Spinner(0, .1f, false, Orientation.HORIZONTAL, skin);
//        table.add(shearXspinner);
//        onChange(shearXspinner, () -> {
//            drawableData.tvgData.shearX = (float) shearXspinner.getValue();
//            tvg.setShearX(drawableData.tvgData.shearX);
//        });
//
//        label = new Label("Shear Y:", skin);
//        table.add(label);
//        shearXspinner.getButtonMinus().addListener(handListener);
//        shearXspinner.getButtonPlus().addListener(handListener);
//        shearXspinner.getTextField().addListener(ibeamListener);
//
//        var shearYspinner = new Spinner(0, .1f, false, Orientation.HORIZONTAL, skin);
//        table.add(shearYspinner);
//        onChange(shearYspinner, () -> {
//            drawableData.tvgData.shearY = (float) shearYspinner.getValue();
//            tvg.setShearY(drawableData.tvgData.shearY);
//        });
//        shearYspinner.getButtonMinus().addListener(handListener);
//        shearYspinner.getButtonPlus().addListener(handListener);
//        shearYspinner.getTextField().addListener(ibeamListener);
        
        row();
        var checkBox = new CheckBox("Clip based on TVG Size", skin);
        checkBox.setChecked(drawableData.tvgData.clipBasedOnTVGsize);
        add(checkBox);
        onChange(checkBox, () -> {
            drawableData.tvgData.clipBasedOnTVGsize = checkBox.isChecked();
            tvg.setClipBasedOnTVGSize(drawableData.tvgData.clipBasedOnTVGsize);
        });
        checkBox.addListener(handListener);
        
        row();
        table = new Table();
        add(table);
        
        defaults().clearActor();
        table.defaults().uniformX().fillX().space(30);
        var okButton = new TextButton("OK", skin);
        table.add(okButton);
        onChange(okButton, this::hide);
        okButton.addListener(handListener);
        onChange(nameTextField, () -> {
            drawableData.name = nameTextField.getText();
            okButton.setDisabled(!atlasData.checkIfDrawableNameIsValid(drawableData.name, drawableData));

            fire(new DialogTvgEvent(drawableData, true));
        });
    
        textButton = new TextButton("Cancel", skin);
        table.add(textButton);
        onChange(textButton, () -> {
            hide();
            fire(new DialogTvgEvent(drawableData, false));
        });
        textButton.addListener(handListener);
    }
    
    public static class TvgData {
//        public float originX;
//        public float originY;
//        public float rotation;
//        public float shearX;
//        public float shearY;
        public boolean clipBasedOnTVGsize = true;
    
        public void set(TvgData tvgData) {
//            originX = tvgData.originX;
//            originY = tvgData.originY;
//            rotation = tvgData.rotation;
//            shearX = tvgData.shearX;
//            shearY = tvgData.shearY;
            clipBasedOnTVGsize = tvgData.clipBasedOnTVGsize;
        }
    }
    
    public static abstract class DialogTvgListener implements EventListener {
        public abstract void selected(DrawableData drawableData);
        
        public abstract void cancelled();
        
        @Override
        public boolean handle(Event event) {
            if (event instanceof DialogTvgEvent) {
                if (((DialogTvgEvent) event).accepted) {
                    selected(((DialogTvgEvent) event).drawableData);
                } else {
                    cancelled();
                }
                return true;
            } else {
                return false;
            }
        }
    }
    
    private static class DialogTvgEvent extends Event {
        public DrawableData drawableData;
        public boolean accepted;
        
        public DialogTvgEvent(DrawableData drawableData, boolean accepted) {
            this.drawableData = drawableData;
            this.accepted = accepted;
        }
    }
    
    public void showAboutDialog() {
        var pop = new PopTable(skin, "dialog");
        pop.setHideOnUnfocus(true);
        pop.setKeepCenteredInWindow(true);
        pop.pad(10);
        
        var label = new Label("About TVG Drawable", skin, "title-no-line");
        pop.add(label);
        
        pop.row();
        label = new Label("TinyVGDrawable is an efficient alternative to SVG and allows you to implement vector artwork in your libGDX games. You must use a custom serializer and implement the TinyVG dependency in your project.", skin);
        label.setWrap(true);
        pop.add(label).growX();
        
        pop.row();
        var subTable = new Table();
        pop.add(subTable);
        
        label = new Label("See the ", skin);
        subTable.add(label);
        
        var textButton = new TextButton("TinyVG Guide", skin, "link");
        subTable.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> Gdx.net.openURI("https://github.com/raeleus/skin-composer/wiki/TinyVG"));
        
        label = new Label(" for more information", skin);
        subTable.add(label);
        
        pop.row();
        var buttonTable = new Table();
        pop.add(buttonTable);
        buttonTable.pad(5);
        
        textButton = new TextButton("OK", skin);
        buttonTable.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, pop::hide);
        
        pop.key(Keys.ENTER, pop::hide);
        pop.key(Keys.NUMPAD_ENTER, pop::hide);
        pop.key(Keys.ESCAPE, pop::hide);
        
        pop.show(getStage());
        pop.setWidth(425);
    }
}