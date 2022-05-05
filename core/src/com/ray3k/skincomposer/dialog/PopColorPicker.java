package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.github.tommyettinger.textra.utils.ColorUtils;
import com.ray3k.stripe.PopTable;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.util.Locale;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.utils.Utils.onChange;

public class PopColorPicker extends PopTable {
    private float r, g, b, a, h, s, br;
    private Color oldColor;
    private final Image squareModelImage, verticalModelImage, redImage, greenImage, blueImage, hueImage, saturationImage, brightnessImage, alphaImage, swatchNewImage;
    private final Stack squareModelCircleStack;
    private final Image squareModelCircleImage, verticalModelArrowImage, redArrowImage, greenArrowImage, blueArrowImage, hueArrowImage, saturationArrowImage, brightnessArrowImage, alphaArrowImage;
    private final ImageButton redRadio, greenRadio, blueRadio, hueRadio, saturationRadio, brightnessRadio;
    private final TextButton hsbTextButton, hslTextButton, swatchesTextButton;
    private final Label brightnessLabel;
    private final ButtonGroup<ImageButton> radioGroup;
    private final TextField redTextField, greenTextField, blueTextField, hueTextField, saturationTextField, brightnessTextField, alphaTextField, hexTextField;
    private static final int SHIFT_AMOUNT = 10;
    
    public PopColorPicker(Color originalColor) {
        r = originalColor.r;
        g = originalColor.g;
        b = originalColor.b;
        a = originalColor.a;
        var tempColor = new Color();
        tempColor.set(ColorUtils.rgb2hsb(r, g, b, a));
        h = tempColor.r;
        s = tempColor.g;
        br = tempColor.b;
        tempColor.set(ColorUtils.rgb2hsl(r, g, b, a));
        this.oldColor = originalColor;
    
        var style = new PopTableStyle();
        style.background = skin.getDrawable("tt-bg");
        style.stageBackground = skin.getDrawable("tt-stage-background");
    
        setStyle(style);
        setModal(true);
        setKeepSizedWithinStage(true);
        setKeepCenteredInWindow(true);
    
        var table = new Table();
        table.setBackground(skin.getDrawable("white"));
        add(table).growX();
    
        var label = new Label("CHOOSE COLOR", skin, "tt");
        table.add(label).left().expandX().padLeft(10);
    
        table.defaults().space(5);
        var buttonGroup = new ButtonGroup<TextButton>();
        hsbTextButton = new TextButton("HSB", skin, "tt-file");
        table.add(hsbTextButton);
        buttonGroup.add(hsbTextButton);
        hsbTextButton.addListener(handListener);
        onChange(hsbTextButton, () -> {
            updateHSB();
            updateColorDisplay();
        });
    
        hslTextButton = new TextButton("HSL", skin, "tt-file");
        table.add(hslTextButton);
        buttonGroup.add(hslTextButton);
        hslTextButton.addListener(handListener);
        onChange(hslTextButton, () -> {
            updateHSB();
            updateColorDisplay();
        });
    
        swatchesTextButton = new TextButton("SWATCHES", skin, "tt-file");
        table.add(swatchesTextButton).padRight(10);
        buttonGroup.add(swatchesTextButton);
        swatchesTextButton.addListener(handListener);
        onChange(swatchesTextButton, this::updateColorDisplay);
        
        row();
        var subTable = new Table();
        subTable.pad(5);
        add(subTable);
    
        subTable.defaults().space(5);
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        subTable.add(table).size(250);
        
        squareModelImage = new Image();
        table.add(squareModelImage).grow();
        squareModelImage.addListener(handListener);
        squareModelImage.addListener(new SquareModelDragListener());
        
        squareModelCircleStack = new Stack();
        table.addActor(squareModelCircleStack);
        
        var image = new Image(skin, "tt-color-ball");
        image.setTouchable(Touchable.disabled);
        squareModelCircleStack.add(image);
        
        squareModelCircleImage = new Image(skin, "tt-color-ball-interior");
        squareModelCircleImage.setTouchable(Touchable.disabled);
        squareModelCircleStack.add(squareModelCircleImage);
        
        squareModelCircleStack.pack();
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        subTable.add(table).size(30, 250);
    
        verticalModelImage = new Image();
        table.add(verticalModelImage).grow();
        verticalModelImage.addListener(handListener);
        verticalModelImage.addListener(new VerticalModelDragListener());
        
        verticalModelArrowImage = new Image(skin, "tt-slider-knob-vertical");
        verticalModelArrowImage.setTouchable(Touchable.disabled);
        table.addActor(verticalModelArrowImage);
    
        var controlTable = new Table();
        subTable.add(controlTable);
    
        var sliderTable = new Table();
        controlTable.add(sliderTable);
    
        radioGroup = new ButtonGroup<>();
    
        sliderTable.defaults().space(2);
        redRadio = new ImageButton(skin, "tt-radio");
        redRadio.setProgrammaticChangeEvents(false);
        sliderTable.add(redRadio);
        radioGroup.add(redRadio);
        redRadio.addListener(handListener);
        onChange(redRadio, this::updateColorDisplay);
    
        label = new Label("R", skin, "tt");
        sliderTable.add(label);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
        
        redImage = new Image();
        redImage.setScaling(Scaling.stretch);
        table.add(redImage).grow();
        redImage.addListener(handListener);
        redImage.addListener(new ColorDragListener((float value) -> {
            r = value;
            updateHSB();
            updateColorDisplay();
        }));
        
        redArrowImage = new Image(skin, "tt-slider-knob");
        redArrowImage.setTouchable(Touchable.disabled);
        table.addActor(redArrowImage);
    
        redTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        redTextField.setProgrammaticChangeEvents(false);
        redTextField.setTextFieldFilter(new DigitsOnlyFilter());
        sliderTable.add(redTextField).width(50);
        redTextField.addListener(ibeamListener);
        applyFieldListener(redTextField);
        onChange(redTextField, () -> {
            r = Integer.parseInt(redTextField.getText()) / 255f;
            r = MathUtils.clamp(r, 0, 1);
            updateHSB();
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        var imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            r += (isShifting() ? SHIFT_AMOUNT : 1) / 255f;
            if (r > 1) r = 1;
            updateHSB();
            updateColorDisplay();
        });
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            r -= (isShifting() ? SHIFT_AMOUNT : 1) / 255f;
            if (r < 0) r = 0;
            updateHSB();
            updateColorDisplay();
        });
    
        sliderTable.row();
        greenRadio = new ImageButton(skin, "tt-radio");
        greenRadio.setProgrammaticChangeEvents(false);
        sliderTable.add(greenRadio);
        radioGroup.add(greenRadio);
        greenRadio.addListener(handListener);
        onChange(greenRadio, this::updateColorDisplay);
    
        label = new Label("G", skin, "tt");
        sliderTable.add(label);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
        
        greenImage = new Image();
        greenImage.setScaling(Scaling.stretch);
        table.add(greenImage).grow();
        greenImage.addListener(handListener);
        greenImage.addListener(new ColorDragListener((float value) -> {
            g = value;
            updateHSB();
            updateColorDisplay();
        }));
    
        greenArrowImage = new Image(skin, "tt-slider-knob");
        greenArrowImage.setTouchable(Touchable.disabled);
        table.addActor(greenArrowImage);
    
        greenTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        greenTextField.setProgrammaticChangeEvents(false);
        sliderTable.add(greenTextField).width(50);
        greenTextField.addListener(ibeamListener);
        applyFieldListener(greenTextField);
        onChange(greenTextField, () -> {
            g = Integer.parseInt(greenTextField.getText()) / 255f;
            g = MathUtils.clamp(g, 0, 1);
            updateHSB();
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            g += (isShifting() ? SHIFT_AMOUNT : 1) / 255f;
            if (g > 1) g = 1;
            updateHSB();
            updateColorDisplay();
        });
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            g -= (isShifting() ? SHIFT_AMOUNT : 1) / 255f;
            if (g < 0) g = 0;
            updateHSB();
            updateColorDisplay();
        });
    
        sliderTable.row();
        blueRadio = new ImageButton(skin, "tt-radio");
        blueRadio.setProgrammaticChangeEvents(false);
        sliderTable.add(blueRadio);
        radioGroup.add(blueRadio);
        blueRadio.addListener(handListener);
        onChange(blueRadio, this::updateColorDisplay);
    
        label = new Label("B", skin, "tt");
        sliderTable.add(label);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
        
        blueImage = new Image();
        blueImage.setScaling(Scaling.stretch);
        table.add(blueImage).grow();
        blueImage.addListener(handListener);
        blueImage.addListener(new ColorDragListener((float value) -> {
            b = value;
            updateHSB();
            updateColorDisplay();
        }));
    
        blueArrowImage = new Image(skin, "tt-slider-knob");
        blueArrowImage.setTouchable(Touchable.disabled);
        table.addActor(blueArrowImage);
    
        blueTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        blueTextField.setProgrammaticChangeEvents(false);
        sliderTable.add(blueTextField).width(50);
        blueTextField.addListener(ibeamListener);
        applyFieldListener(blueTextField);
        onChange(blueTextField, () -> {
            b = Integer.parseInt(blueTextField.getText()) / 255f;
            b = MathUtils.clamp(b, 0, 1);
            updateHSB();
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            b += (isShifting() ? SHIFT_AMOUNT : 1) / 255f;
            if (b > 1) b = 1;
            updateHSB();
            updateColorDisplay();
        });
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            b -= (isShifting() ? SHIFT_AMOUNT : 1) / 255f;
            if (b < 0) b = 0;
            updateHSB();
            updateColorDisplay();
        });
    
        sliderTable.row();
        hueRadio = new ImageButton(skin, "tt-radio");
        hueRadio.setProgrammaticChangeEvents(false);
        sliderTable.add(hueRadio);
        radioGroup.add(hueRadio);
        hueRadio.setChecked(true);
        hueRadio.addListener(handListener);
        onChange(hueRadio, this::updateColorDisplay);
    
        label = new Label("H", skin, "tt");
        sliderTable.add(label);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
    
        hueImage = new Image(generateHorizontalHue(180));
        table.add(hueImage).grow();
        hueImage.addListener(handListener);
        hueImage.addListener(new ColorDragListener((float value) -> {
            h = value;
            updateRGB();
            updateColorDisplay();
        }));
    
        hueArrowImage = new Image(skin, "tt-slider-knob");
        hueArrowImage.setTouchable(Touchable.disabled);
        table.addActor(hueArrowImage);
        
        hueTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        hueTextField.setProgrammaticChangeEvents(false);
        sliderTable.add(hueTextField).width(50);
        hueTextField.addListener(ibeamListener);
        applyFieldListener(hueTextField);
        onChange(hueTextField, () -> {
            h = Integer.parseInt(hueTextField.getText()) / 360f;
            h = MathUtils.clamp(h, 0, 1);
            updateRGB();
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            h += (isShifting() ? SHIFT_AMOUNT : 1) / 360f;
            if (h > 1) h = 1;
            updateRGB();
            updateColorDisplay();
        });
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            h -= (isShifting() ? SHIFT_AMOUNT : 1) / 360f;
            if (h < 0) h = 0;
            var newColor = new Color(ColorUtils.hsb2rgb(h, s, br, a));
            r = newColor.r;
            g = newColor.g;
            b = newColor.b;
            updateColorDisplay();
        });
    
        sliderTable.row();
        saturationRadio = new ImageButton(skin, "tt-radio");
        saturationRadio.setProgrammaticChangeEvents(false);
        sliderTable.add(saturationRadio);
        radioGroup.add(saturationRadio);
        saturationRadio.addListener(handListener);
        onChange(saturationRadio, this::updateColorDisplay);
    
        label = new Label("S", skin, "tt");
        sliderTable.add(label);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
        
        saturationImage = new Image();
        saturationImage.setScaling(Scaling.stretch);
        table.add(saturationImage).grow();
        saturationImage.addListener(handListener);
        saturationImage.addListener(new ColorDragListener((float value) -> {
            s = value;
            updateRGB();
            updateColorDisplay();
        }));
    
        saturationArrowImage = new Image(skin, "tt-slider-knob");
        saturationArrowImage.setTouchable(Touchable.disabled);
        table.addActor(saturationArrowImage);
        
        saturationTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        saturationTextField.setProgrammaticChangeEvents(false);
        sliderTable.add(saturationTextField).width(50);
        saturationTextField.addListener(ibeamListener);
        applyFieldListener(saturationTextField);
        onChange(saturationTextField, () -> {
            s = Integer.parseInt(saturationTextField.getText()) / 100f;
            s = MathUtils.clamp(s, 0, 1);
            updateRGB();
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            s += (isShifting() ? SHIFT_AMOUNT : 1) / 100f;
            if (s > 1) s = 1;
            updateRGB();
            updateColorDisplay();
        });
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            s -= (isShifting() ? SHIFT_AMOUNT : 1) / 100f;
            if (s < 0) s = 0;
            updateRGB();
            updateColorDisplay();
        });
    
        sliderTable.row();
        brightnessRadio = new ImageButton(skin, "tt-radio");
        brightnessRadio.setProgrammaticChangeEvents(false);
        sliderTable.add(brightnessRadio);
        radioGroup.add(brightnessRadio);
        brightnessRadio.addListener(handListener);
        onChange(brightnessRadio, this::updateColorDisplay);
    
        brightnessLabel = new Label("B", skin, "tt");
        sliderTable.add(brightnessLabel);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
        
        brightnessImage = new Image();
        brightnessImage.setScaling(Scaling.stretch);
        table.add(brightnessImage).grow();
        brightnessImage.addListener(handListener);
        brightnessImage.addListener(new ColorDragListener((float value) -> {
            br = value;
            updateRGB();
            updateColorDisplay();
        }));
    
        brightnessArrowImage = new Image(skin, "tt-slider-knob");
        brightnessArrowImage.setTouchable(Touchable.disabled);
        table.addActor(brightnessArrowImage);
        
        brightnessTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        brightnessTextField.setProgrammaticChangeEvents(false);
        sliderTable.add(brightnessTextField).width(50);
        brightnessTextField.addListener(ibeamListener);
        applyFieldListener(brightnessTextField);
        onChange(brightnessTextField, () -> {
            br = Integer.parseInt(brightnessTextField.getText()) / 100f;
            br = MathUtils.clamp(br, 0, 1);
            updateRGB();
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            br += (isShifting() ? SHIFT_AMOUNT : 1) / 100f;
            if (br > 1) br = 1;
            updateRGB();
            updateColorDisplay();
        });
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            br -= (isShifting() ? SHIFT_AMOUNT : 1) / 100f;
            if (br < 0) br = 0;
            updateRGB();
            updateColorDisplay();
        });
    
        sliderTable.row();
        sliderTable.add();
    
        label = new Label("A", skin, "tt");
        sliderTable.add(label);
    
        table = new Table();
        table.setClip(true);
        table.setBackground(skin.getDrawable("tt-slider-10"));
        sliderTable.add(table).width(180).fillY();
    
        var stack = new Stack();
        table.add(stack).grow();
    
        image = new Image(skin, "tt-checker-10");
        stack.add(image);
        
        alphaImage = new Image();
        alphaImage.setScaling(Scaling.stretch);
        stack.add(alphaImage);
        alphaImage.addListener(handListener);
        alphaImage.addListener(new ColorDragListener((float value) -> {
            a = value;
            updateColorDisplay();
        }));
        
        alphaArrowImage = new Image(skin, "tt-slider-knob");
        alphaArrowImage.setTouchable(Touchable.disabled);
        table.addActor(alphaArrowImage);
    
        alphaTextField = new TextField("", skin, "tt") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        alphaTextField.setProgrammaticChangeEvents(false);
        sliderTable.add(alphaTextField).width(50);
        alphaTextField.addListener(ibeamListener);
        applyFieldListener(alphaTextField);
        onChange(alphaTextField, () -> {
            a = Integer.parseInt(alphaTextField.getText()) / 255f;
            a = MathUtils.clamp(a, 0, 1);
            updateColorDisplay();
        });
    
        table = new Table();
        sliderTable.add(table);
    
        table.defaults().space(3);
        imageButton = new ImageButton(skin, "tt-increase");
        table.add(imageButton);
        imageButton.addListener(handListener);
    
        table.row();
        imageButton = new ImageButton(skin, "tt-decrease");
        table.add(imageButton);
        imageButton.addListener(handListener);
    
        controlTable.row();
        table = new Table();
        controlTable.add(table).growX().space(5);
    
        table.defaults().space(5);
    
        stack = new Stack();
        table.add(stack);
    
        image = new Image(skin, "tt-swatch");
        image.setColor(oldColor);
        image.setScaling(Scaling.none);
        stack.add(image);
    
        swatchNewImage = new Image(skin, "tt-swatch-new");
        swatchNewImage.setScaling(Scaling.none);
        stack.add(swatchNewImage);
    
        hexTextField = new TextField("", skin, "tt-hexfield") {
            @Override
            public void next(boolean up) {
                nextTextField(up, this);
            }
        };
        hexTextField.setProgrammaticChangeEvents(false);
        hexTextField.setMaxLength(8);
        hexTextField.setTextFieldFilter((textField, c) -> {
            if (Character.isDigit(c)) return true;
            else switch (c) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                    return true;
                default:
                    return false;
            }
        });
        table.add(hexTextField).width(80);
        hexTextField.addListener(ibeamListener);
        applyFieldListener(hexTextField);
    
        var textButton = new TextButton("OK", skin, "tt");
        table.add(textButton).width(70);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            hide();
        });
    
        textButton = new TextButton("Cancel", skin, "tt");
        table.add(textButton).width(70);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            hide();
        });

        updateColorDisplay();
    }
    
    private void applyFieldListener(TextField textField) {
        textField.removeListener(textField.getDefaultInputListener());
        textField.addListener(new ClickListener() {
            boolean selectAll;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!selectAll) ((ClickListener)textField.getDefaultInputListener()).clicked(event, x, y);
                else {
                    textField.selectAll();
                }
            }
        
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectAll = stage.getKeyboardFocus() != event.getListenerActor();
                textField.getDefaultInputListener().touchDown(event, x, y, pointer, button);
                return super.touchDown(event, x, y, pointer, button);
            }
        
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                textField.getDefaultInputListener().touchDragged(event, x, y, pointer);
                super.touchDragged(event, x, y, pointer);
            }
        
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                textField.getDefaultInputListener().touchUp(event, x, y, pointer, button);
                super.touchUp(event, x, y, pointer, button);
            }
        
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                return textField.getDefaultInputListener().keyDown(event, keycode);
            }
        
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return textField.getDefaultInputListener().keyUp(event, keycode);
            }
        
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                return textField.getDefaultInputListener().keyTyped(event, character);
            }
        });
    }
    
    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);
        hexTextField.selectAll();
        stage.setKeyboardFocus(hexTextField);
    }
    
    private void updateRGB() {
        var tempColor = new Color();
        if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, s, br, a));
        else tempColor.set(ColorUtils.hsb2rgb(h, s, br, a));
        r = tempColor.r;
        g = tempColor.g;
        b = tempColor.b;
    }
    
    private void updateHSB() {
        var tempColor = new Color();
        if (hslTextButton.isChecked()) tempColor.set(ColorUtils.rgb2hsl(r, g, b, a));
        else tempColor.set(ColorUtils.rgb2hsb(r, g, b, a));
        if (!MathUtils.isZero(r) || !MathUtils.isZero(g) || !MathUtils.isZero(b))
            if (!MathUtils.isEqual(1,r) || !MathUtils.isEqual(1, g) || !MathUtils.isEqual(1, b))
                h = tempColor.r;
        if (!MathUtils.isZero(r) || !MathUtils.isZero(g) || !MathUtils.isZero(b))
            s = tempColor.g;
        br = tempColor.b;
        tempColor.set(ColorUtils.rgb2hsl(r, g, b, a));
    }
    
    private void updateColorDisplay() {
        if (hslTextButton.isChecked()) brightnessLabel.setText("L");
        else brightnessLabel.setText("B");
        
        var tempColor = new Color();
        if (redRadio.isChecked()) {
            squareModelCircleStack.setX(b * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            squareModelCircleStack.setY(g * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            verticalModelArrowImage.setY(r * verticalModelImage.getHeight() - verticalModelArrowImage.getHeight() / 2);
        } else if (greenRadio.isChecked()) {
            squareModelCircleStack.setX(b * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            squareModelCircleStack.setY(r * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            verticalModelArrowImage.setY(g * verticalModelImage.getHeight() - verticalModelArrowImage.getHeight() / 2);
        } else if (blueRadio.isChecked()) {
            squareModelCircleStack.setX(r * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            squareModelCircleStack.setY(g * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            verticalModelArrowImage.setY(b * verticalModelImage.getHeight() - verticalModelArrowImage.getHeight() / 2);
        } else if (hueRadio.isChecked()) {
            squareModelCircleStack.setX(s * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            squareModelCircleStack.setY(br * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            verticalModelArrowImage.setY(h * verticalModelImage.getHeight() - verticalModelArrowImage.getHeight() / 2);
        } else if (saturationRadio.isChecked()) {
            squareModelCircleStack.setX(h * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            squareModelCircleStack.setY(br * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            verticalModelArrowImage.setY(s * verticalModelImage.getHeight() - verticalModelArrowImage.getHeight() / 2);
        } else if (brightnessRadio.isChecked()) {
            squareModelCircleStack.setX(h * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            squareModelCircleStack.setY(s * squareModelImage.getWidth() - squareModelCircleStack.getWidth()/ 2);
            verticalModelArrowImage.setY(br * verticalModelImage.getHeight() - verticalModelArrowImage.getHeight() / 2);
        }
        
        squareModelCircleImage.setColor(tempColor.set(r, g, b, 1f));
        
        var tenPatch = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        tenPatch.setColor1(tempColor.set(0, g, b, 1));
        tenPatch.setColor2(tempColor);
        tenPatch.setColor3(tempColor.set(1, g, b, 1));
        tenPatch.setColor4(tempColor);
        redImage.setDrawable(tenPatch);
        
        redArrowImage.setX(r * redImage.getWidth() - redArrowImage.getWidth() / 2);
    
        tenPatch = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        tenPatch.setColor1(tempColor.set(r, 0, b, 1));
        tenPatch.setColor2(tempColor);
        tenPatch.setColor3(tempColor.set(r, 1, b, 1));
        tenPatch.setColor4(tempColor);
        greenImage.setDrawable(tenPatch);
    
        greenArrowImage.setX(g * greenImage.getWidth() - greenArrowImage.getWidth() / 2);
    
        tenPatch = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        tenPatch.setColor1(tempColor.set(r, g, 0, 1));
        tenPatch.setColor2(tempColor);
        tenPatch.setColor3(tempColor.set(r, g, 1, 1));
        tenPatch.setColor4(tempColor);
        blueImage.setDrawable(tenPatch);
    
        blueArrowImage.setX(b * blueImage.getWidth() - blueArrowImage.getWidth() / 2);
    
        hueArrowImage.setX(h * hueImage.getWidth() - hueArrowImage.getWidth() / 2);
    
        tenPatch = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, 0f, br, 1f));
        else tempColor.set(ColorUtils.hsb2rgb(h, 0f, br, 1f));
        tenPatch.setColor1(tempColor);
        tenPatch.setColor2(tempColor);
        if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, 1f, br, 1f));
        else tempColor.set(ColorUtils.hsb2rgb(h, 1f, br, 1f));
        tenPatch.setColor3(tempColor);
        tenPatch.setColor4(tempColor);
        saturationImage.setDrawable(tenPatch);
    
        saturationArrowImage.setX(s * saturationImage.getWidth() - saturationArrowImage.getWidth() / 2);
    
        tenPatch = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, s, 0f, 1f));
        else tempColor.set(ColorUtils.hsb2rgb(h, s, 0f, 1f));
        tenPatch.setColor1(tempColor);
        tenPatch.setColor2(tempColor);
        if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, s, 1f, 1f));
        else tempColor.set(ColorUtils.hsb2rgb(h, s, 1f, 1f));
        tenPatch.setColor3(tempColor);
        tenPatch.setColor4(tempColor);
        brightnessImage.setDrawable(tenPatch);
    
        brightnessArrowImage.setX(br * brightnessImage.getWidth() - brightnessArrowImage.getWidth() / 2);
    
        tenPatch = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        tenPatch.setColor1(tempColor.set(r, g, b, 0.0f));
        tenPatch.setColor2(tempColor);
        tenPatch.setColor3(tempColor.set(r, g, b, 1.0f));
        tenPatch.setColor4(tempColor);
        alphaImage.setDrawable(tenPatch);
    
        alphaArrowImage.setX(a * alphaImage.getWidth() - alphaArrowImage.getWidth() / 2);
        
        tempColor.set(r, g, b, a);
        swatchNewImage.setColor(tempColor);
        
        if (radioGroup.getChecked() == redRadio) {
            var drawable = (TextureRegionDrawable) squareModelImage.getDrawable();
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            squareModelImage.setDrawable(new TextureRegionDrawable(generateColorVsGreenAndBlue(tempColor, 250, 250)));
    
            drawable = (TextureRegionDrawable) verticalModelImage.getDrawable();
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            verticalModelImage.setDrawable(generateVerticalRed(tempColor));
        } else if (radioGroup.getChecked() == greenRadio) {
            var drawable = ((TextureRegionDrawable) squareModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            squareModelImage.setDrawable(new TextureRegionDrawable(generateColorVsRedAndBlue(tempColor, 250, 250)));
    
            drawable = ((TextureRegionDrawable) verticalModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            verticalModelImage.setDrawable(generateVerticalGreen(tempColor));
        } else if (radioGroup.getChecked() == blueRadio) {
            var drawable = ((TextureRegionDrawable) squareModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            squareModelImage.setDrawable(new TextureRegionDrawable(generateColorVsGreenAndRed(tempColor, 250, 250)));
    
            drawable = ((TextureRegionDrawable) verticalModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            verticalModelImage.setDrawable(generateVerticalBlue(tempColor));
        } else if (radioGroup.getChecked() == hueRadio) {
            var drawable = ((TextureRegionDrawable) squareModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            squareModelImage.setDrawable(new TextureRegionDrawable(generateColorVsBrightnessAndSaturation(tempColor, 250, 250)));
    
            drawable = ((TextureRegionDrawable) verticalModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            verticalModelImage.setDrawable(new TextureRegionDrawable(generateVerticalHue(250)));
        } else if (radioGroup.getChecked() == saturationRadio) {
            var drawable = ((TextureRegionDrawable) squareModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            squareModelImage.setDrawable(new TextureRegionDrawable(generateHueVsBrightness(250, 250)));
    
            drawable = ((TextureRegionDrawable) verticalModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            verticalModelImage.setDrawable(new TextureRegionDrawable(generateVerticalSaturation(tempColor, 250)));
        } else if (radioGroup.getChecked() == brightnessRadio) {
            var drawable = ((TextureRegionDrawable) squareModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            squareModelImage.setDrawable(new TextureRegionDrawable(generateHueVsSaturation(250, 250)));
    
            drawable = ((TextureRegionDrawable) verticalModelImage.getDrawable());
            if (drawable != null && !(drawable instanceof TenPatchDrawable)) drawable.getRegion().getTexture().dispose();
            verticalModelImage.setDrawable(new TextureRegionDrawable(generateVerticalBrightness(tempColor, 250)));
        }
        
        redTextField.setText(Integer.toString(MathUtils.round(r * 255)));
        greenTextField.setText(Integer.toString(MathUtils.round(g * 255)));
        blueTextField.setText(Integer.toString(MathUtils.round(b * 255)));
        
        hueTextField.setText(Integer.toString(MathUtils.round(h * 360)));
        saturationTextField.setText(Integer.toString(MathUtils.round(s * 100)));
        brightnessTextField.setText(Integer.toString(MathUtils.round(br * 100)));
        
        alphaTextField.setText(Integer.toString(MathUtils.round(a * 255)));
        
        tempColor.set(r, g, b, a);
        hexTextField.setText(tempColor.toString().toUpperCase(Locale.ROOT));
    }
    
    private TextureRegion generateHorizontalHue(int width) {
        var color = new Color();
        var pixmap = new Pixmap(width, 1, Format.RGBA8888);
        for (int i = 0; i < width; i++) {
            if (hslTextButton.isChecked()) color.set(ColorUtils.hsl2rgb((float) i / width, 1f, 1f, 1f));
            else color.set(ColorUtils.hsb2rgb((float) i / width, 1f, 1f, 1f));
            pixmap.setColor(color);
            pixmap.drawPixel(i, 0);
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TenPatchDrawable generateVerticalRed(Color color) {
        var tenPatchDrawable = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        
        var tempColor = new Color(0f, color.g, color.b, 1f);
        tenPatchDrawable.setColor1(tempColor);
        tenPatchDrawable.setColor4(tempColor);
    
        tempColor.set(1f, color.g, color.b, 1f);
        tenPatchDrawable.setColor2(tempColor);
        tenPatchDrawable.setColor3(tempColor);
        
        return tenPatchDrawable;
    }
    
    private TenPatchDrawable generateVerticalGreen(Color color) {
        var tenPatchDrawable = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        
        var tempColor = new Color(color.r, 0f, color.b, 1f);
        tenPatchDrawable.setColor1(tempColor);
        tenPatchDrawable.setColor4(tempColor);
        
        tempColor.set(color.r, 1f, color.b, 1f);
        tenPatchDrawable.setColor2(tempColor);
        tenPatchDrawable.setColor3(tempColor);
        
        return tenPatchDrawable;
    }
    
    private TenPatchDrawable generateVerticalBlue(Color color) {
        var tenPatchDrawable = new TenPatchDrawable(skin.get("white-10", TenPatchDrawable.class));
        
        var tempColor = new Color(color.r, color.g, 0f, 1f);
        tenPatchDrawable.setColor1(tempColor);
        tenPatchDrawable.setColor4(tempColor);
        
        tempColor.set(color.r, color.g, 1f, 1f);
        tenPatchDrawable.setColor2(tempColor);
        tenPatchDrawable.setColor3(tempColor);
        
        return tenPatchDrawable;
    }
    
    private TextureRegion generateVerticalHue(int height) {
        var color = new Color();
        var pixmap = new Pixmap(1, height, Format.RGBA8888);
        for (int i = 0; i < height; i++) {
            if (hslTextButton.isChecked()) color.set(ColorUtils.hsl2rgb((float) (height - i) / height, 1f, .5f, 1f));
            else color.set(ColorUtils.hsb2rgb((float) (height - i) / height, 1f, 1f, 1f));
            pixmap.setColor(color);
            pixmap.drawPixel(0, i);
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateVerticalSaturation(Color color, int height) {
        var tempColor = new Color();
        var pixmap = new Pixmap(1, height, Format.RGBA8888);
        for (int i = 0; i < height; i++) {
            var newS = (float) (height - i) / height;
            if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, newS, br, 1f));
            else tempColor.set(ColorUtils.hsb2rgb(h, newS, br, 1f));
            pixmap.setColor(tempColor);
            pixmap.drawPixel(0, i);
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateVerticalBrightness(Color color, int height) {
        var tempColor = new Color();
        var pixmap = new Pixmap(1, height, Format.RGBA8888);
        for (int i = 0; i < height; i++) {
            var newBr = (float) (height - i) / height;
            if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, s, newBr, 1f));
            else tempColor.set(ColorUtils.hsb2rgb(h, s, newBr, 1f));
            pixmap.setColor(tempColor);
            pixmap.drawPixel(0, i);
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateHueVsSaturation(int width, int height) {
        var color = new Color();
        var pixmap = new Pixmap(width, height, Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (hslTextButton.isChecked()) color.set(ColorUtils.hsl2rgb((float) x / width, (float) (height - y) / height, br, 1f));
                else color.set(ColorUtils.hsb2rgb((float) x / width, (float) (height - y) / height, br, 1f));
                pixmap.setColor(color);
                pixmap.drawPixel(x, y);
            }
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateHueVsBrightness(int width, int height) {
        var color = new Color();
        var pixmap = new Pixmap(width, height, Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (hslTextButton.isChecked()) color.set(ColorUtils.hsl2rgb((float) x / width, s, (float) (height - y) / height, 1f));
                else color.set(ColorUtils.hsb2rgb((float) x / width, s, (float) (height - y) / height, 1f));
                pixmap.setColor(color);
                pixmap.drawPixel(x, y);
            }
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateColorVsBrightnessAndSaturation(Color color, int width, int height) {
        var tempColor = new Color();
        var pixmap = new Pixmap(width, height, Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (hslTextButton.isChecked()) tempColor.set(ColorUtils.hsl2rgb(h, (float) x / width, (float) (height - y) / height, 1f));
                else tempColor.set(ColorUtils.hsb2rgb(h, (float) x / width, (float) (height - y) / height, 1f));
                pixmap.setColor(tempColor);
                pixmap.drawPixel(x, y);
            }
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateColorVsGreenAndBlue(Color color, int width, int height) {
        var tempColor = new Color();
        var pixmap = new Pixmap(width, height, Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tempColor.set(color.r, (float) (height - y) / height, (float) x / width, 1f);
                pixmap.setColor(tempColor);
                pixmap.drawPixel(x, y);
            }
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateColorVsRedAndBlue(Color color, int width, int height) {
        var tempColor = new Color();
        var pixmap = new Pixmap(width, height, Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tempColor.set((float) (height - y) / height, color.g, (float) x / width, 1f);
                pixmap.setColor(tempColor);
                pixmap.drawPixel(x, y);
            }
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private TextureRegion generateColorVsGreenAndRed(Color color, int width, int height) {
        var tempColor = new Color();
        var pixmap = new Pixmap(width, height, Format.RGBA8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tempColor.set((float) x / width, (float) (height - y) / height, color.b, 1f);
                pixmap.setColor(tempColor);
                pixmap.drawPixel(x, y);
            }
        }
        return new TextureRegion(new Texture(pixmap));
    }
    
    private void nextTextField(boolean up, TextField current) {
        var textFields = new Array<>(new TextField[] {redTextField, greenTextField, blueTextField, hueTextField, saturationTextField, brightnessTextField, alphaTextField, hexTextField});
        var index = textFields.indexOf(current, true);
        if (up) {
            index--;
        } else {
            index++;
        }
        if (index < 0) index = textFields.size - 1;
        else if (index >= textFields.size) index = 0;
        
        var textField = textFields.get(index);
        stage.setKeyboardFocus(textField);
        textField.selectAll();
    }
    
    private static boolean isShifting() {
        return Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);
    }
    
    private static class ColorDragListener extends DragListener {
        public ColorDraggable draggable;
        public ColorDragListener(ColorDraggable draggable) {
            this.draggable = draggable;
            setTapSquareSize(0);
        }
        
        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            super.drag(event, x, y, pointer);
            var value = x / event.getListenerActor().getWidth();
            value = MathUtils.clamp(value, 0, 1);
            draggable.dragged(value);
        }
    
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            var value = x / event.getListenerActor().getWidth();
            value = MathUtils.clamp(value, 0, 1);
            draggable.dragged(value);
            return super.touchDown(event, x, y, pointer, button);
        }
    }
    
    private interface ColorDraggable {
        void dragged(float value);
    }
    
    private class VerticalModelDragListener extends DragListener {
        public VerticalModelDragListener() {
            setTapSquareSize(0);
        }
        
        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            super.drag(event, x, y, pointer);
            var value = y / event.getListenerActor().getHeight();
            value = MathUtils.clamp(value, 0, 1);
            dragged(value);
        }
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            var value = y / event.getListenerActor().getHeight();
            value = MathUtils.clamp(value, 0, 1);
            dragged(value);
            return super.touchDown(event, x, y, pointer, button);
        }
        
        private void dragged(float value) {
            if (redRadio.isChecked()) {
                r = value;
                updateHSB();
                updateColorDisplay();
            } else if (greenRadio.isChecked()) {
                g = value;
                updateHSB();
                updateColorDisplay();
            } else if (blueRadio.isChecked()) {
                b = value;
                updateHSB();
                updateColorDisplay();
            } else if (hueRadio.isChecked()) {
                h = value;
                updateRGB();
                updateColorDisplay();
            } else if (saturationRadio.isChecked()) {
                s = value;
                updateRGB();
                updateColorDisplay();
            } else if (brightnessRadio.isChecked()) {
                br = value;
                updateRGB();
                updateColorDisplay();
            }
        }
    }
    
    private class SquareModelDragListener extends DragListener {
        public SquareModelDragListener() {
            setTapSquareSize(0);
        }
        
        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            super.drag(event, x, y, pointer);
            dragged(MathUtils.clamp(x / event.getListenerActor().getWidth(), 0, 1), MathUtils.clamp(y / event.getListenerActor().getHeight(), 0, 1));
        }
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            dragged(MathUtils.clamp(x / event.getListenerActor().getWidth(), 0, 1), MathUtils.clamp(y / event.getListenerActor().getHeight(), 0, 1));
            return super.touchDown(event, x, y, pointer, button);
        }
        
        private void dragged(float xValue, float yValue) {
            if (redRadio.isChecked()) {
                g = yValue;
                b = xValue;
                updateHSB();
                updateColorDisplay();
            } else if (greenRadio.isChecked()) {
                r = yValue;
                b = xValue;
                updateHSB();
                updateColorDisplay();
            } else if (blueRadio.isChecked()) {
                r = xValue;
                g = yValue;
                updateHSB();
                updateColorDisplay();
            } else if (hueRadio.isChecked()) {
                s = xValue;
                br = yValue;
                updateRGB();
                updateColorDisplay();
            } else if (saturationRadio.isChecked()) {
                h = xValue;
                br = yValue;
                updateRGB();
                updateColorDisplay();
            } else if (brightnessRadio.isChecked()) {
                h = xValue;
                s = yValue;
                updateRGB();
                updateColorDisplay();
            }
        }
    }
    
    public static void showColorPicker(Color color, Stage stage) {
        var pop = new PopColorPicker(color);
        pop.show(stage);
    }
}