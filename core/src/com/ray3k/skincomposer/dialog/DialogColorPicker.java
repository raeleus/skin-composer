/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.CheckerDrawable;
import com.ray3k.skincomposer.GradientDrawable;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.Orientation;
import com.ray3k.skincomposer.StackedDrawable;

public class DialogColorPicker extends Dialog {
    private Skin skin;
    private ColorListener listener;
    private Table content;
    private GradientDrawable gradientS, gradientB, gradientAlpha;
    private Array<GradientDrawable> hueGradient;
    private CheckerDrawable checker;
    private StackedDrawable gradientSB;
    private StackedDrawable alphaStack;
    private static final float SIZE = 300.0f;
    private Color previousColor, selectedColor;
    private Spinner redSpinner;
    private Main main;
    
    public DialogColorPicker(Main main, String style, ColorListener listener, Color previousColor) {
        super("", main.getSkin(), style);
        
        if (previousColor == null) {
            selectedColor = new Color(Color.RED);
        } else {
            this.previousColor = new Color(previousColor);
            selectedColor = new Color(previousColor);
        }
        
        this.skin = main.getSkin();
        this.main = main;
        this.listener = listener;
        
        gradientAlpha = new GradientDrawable(selectedColor, selectedColor, selectedColor, selectedColor);
        gradientAlpha.getCol1().a = 0;
        gradientAlpha.getCol2().a = 0;
        gradientAlpha.getCol3().a = 1;
        gradientAlpha.getCol4().a = 1;
        
        Vector3 v = rgbToHsb(selectedColor.r, selectedColor.g, selectedColor.b);
        Color temp = hsbToRgb(v.x * 360.0f, 1.0f, 1.0f);
        gradientS = new GradientDrawable(Color.WHITE, temp, temp, Color.WHITE);
        gradientB = new GradientDrawable(Color.BLACK, Color.BLACK, Color.CLEAR, Color.CLEAR);
        gradientSB = new StackedDrawable(gradientS, gradientB);
        
        hueGradient = new Array<>();
        hueGradient.add(new GradientDrawable(Color.MAGENTA, Color.MAGENTA, Color.RED, Color.RED));
        hueGradient.add(new GradientDrawable(Color.BLUE, Color.BLUE, Color.MAGENTA, Color.MAGENTA));
        hueGradient.add(new GradientDrawable(Color.CYAN, Color.CYAN, Color.BLUE, Color.BLUE));
        hueGradient.add(new GradientDrawable(Color.GREEN, Color.GREEN, Color.CYAN, Color.CYAN));
        hueGradient.add(new GradientDrawable(Color.YELLOW, Color.YELLOW, Color.GREEN, Color.GREEN));
        hueGradient.add(new GradientDrawable(Color.RED, Color.RED, Color.YELLOW, Color.YELLOW));
        
        Drawable tinted = ((TextureRegionDrawable) skin.getDrawable("white")).tint(Color.LIGHT_GRAY);
        checker = new CheckerDrawable(skin.getDrawable("white"), tinted, 10.0f, 10.0f);
        alphaStack = new StackedDrawable(checker, gradientAlpha);
        
        Table root = getContentTable();
        Label label = new Label("Choose a Color", skin, "title");
        label.setAlignment(Align.center);
        root.add(label).growX();
        
        root.row();
        content = new Table(skin);
        root.add(content);
        
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ESCAPE) {
                    if (listener != null) {
                        listener.handle(new ColorListener.ColorEvent(null));
                    }
                    hide();
                }
                return false;
            }
        });
        
        populate();
    }

    @Override
    public Dialog show(Stage stage) {
        Dialog dialog = super.show(stage);
        
        stage.setKeyboardFocus(redSpinner.getTextField());
        redSpinner.getTextField().selectAll();
        
        return dialog;
    }
    
    private static Vector3 tempVector;
    
    public static Vector3 rgbToHsb(float r, float g, float b) {
        if (tempVector == null) {
            tempVector = new Vector3();
        }
        float[] hsb = new float[3];
        java.awt.Color.RGBtoHSB((int) (255.0f * r), (int) (255.0f * g), (int) (255.0f * b), hsb);
        tempVector.x = hsb[0];
        if (MathUtils.isEqual(tempVector.x, 1.0f)) {
            tempVector.x = 0;
        }
        tempVector.y = hsb[1];
        tempVector.z = hsb[2];
        return tempVector;
    }
    
    public static Color hsbToRgb(float h, float s, float b) {
        Color color = new Color();
        
        float c = b * s;
        float hi = h / 60.0f;
        float x = c * (1.0f - Math.abs(hi % 2.0f - 1.0f));
        
        if (h < 0) {
          color.r = 0;
          color.g = 0;
          color.b = 0;
        } else if (h < 60) {
            color.r = c;
            color.g = x;
            color.b = 0;
        } else if (h < 120) {
            color.r = x;
            color.g = c;
            color.b = 0;
        } else if (h < 180) {
            color.r = 0;
            color.g = c;
            color.b = x;
        } else if (h < 240) {
            color.r = 0;
            color.g = x;
            color.b = c;
        } else if (h < 300) {
            color.r = x;
            color.g = 0;
            color.b = c;
        } else {
            color.r = c;
            color.g = 0;
            color.b = x;
        }
        
        float m = b - c;
        color.r = color.r + m;
        color.g = color.g + m;
        color.b = color.b + m;
        color.a = 1.0f;
        
        return color;
    }
    
    public static float max(float x, float y, float z) {
        float returnValue = Math.max(x, y);
        return Math.max(returnValue, z);
    }
    
    public static float min(float x, float y, float z) {
        float returnValue = Math.min(x, y);
        return Math.min(returnValue, z);
    }
    
    public void populate() {
        content.clear();
        content.defaults().padLeft(10.0f);
        
        Image cursor = new Image(skin.getDrawable("color-picker"));
        cursor.setTouchable(Touchable.enabled);
        Image hueKnob = new Image(skin, "color-scale");
        hueKnob.setTouchable(Touchable.enabled);
        Image hueKnob2 = new Image(skin, "color-scale-flipped");
        hueKnob2.setTouchable(Touchable.enabled);
        Image alphaKnob = new Image(skin, "color-scale");
        alphaKnob.setTouchable(Touchable.enabled);
        Image alphaKnob2 = new Image(skin, "color-scale-flipped");
        alphaKnob2.setTouchable(Touchable.enabled);
        
        Container selectedColorCont = new Container();
        selectedColorCont.setBackground(skin.getDrawable("white"));
        selectedColorCont.setColor(selectedColor);
        
        Vector3 v = rgbToHsb(selectedColor.r, selectedColor.g, selectedColor.b);
        
        Spinner greenSpinner, blueSpinner, alphaSpinner, hueSpinner, saturationSpinner, brightnessSpinner;
        
        hueSpinner = new Spinner(v.x * 359.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        hueSpinner.setMinimum(0.0);
        hueSpinner.setMaximum(359.0);
        hueSpinner.getTextField().addListener(main.getIbeamListener());
        hueSpinner.getButtonMinus().addListener(main.getHandListener());
        hueSpinner.getButtonPlus().addListener(main.getHandListener());
        
        saturationSpinner = new Spinner(v.y * 100.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        saturationSpinner.setMinimum(0.0);
        saturationSpinner.setMaximum(100.0);
        saturationSpinner.getTextField().addListener(main.getIbeamListener());
        saturationSpinner.getButtonMinus().addListener(main.getHandListener());
        saturationSpinner.getButtonPlus().addListener(main.getHandListener());
        
        brightnessSpinner = new Spinner(v.z * 100.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        brightnessSpinner.setMinimum(0.0);
        brightnessSpinner.setMaximum(100.0);
        brightnessSpinner.getTextField().addListener(main.getIbeamListener());
        brightnessSpinner.getButtonMinus().addListener(main.getHandListener());
        brightnessSpinner.getButtonPlus().addListener(main.getHandListener());
        
        redSpinner = new Spinner(selectedColor.r * 255.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        redSpinner.setMinimum(0.0);
        redSpinner.setMaximum(255.0);
        redSpinner.getTextField().addListener(main.getIbeamListener());
        redSpinner.getButtonMinus().addListener(main.getHandListener());
        redSpinner.getButtonPlus().addListener(main.getHandListener());
        
        greenSpinner = new Spinner(selectedColor.g * 255.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        greenSpinner.setMinimum(0.0);
        greenSpinner.setMaximum(255.0);
        greenSpinner.getTextField().addListener(main.getIbeamListener());
        greenSpinner.getButtonMinus().addListener(main.getHandListener());
        greenSpinner.getButtonPlus().addListener(main.getHandListener());
        
        blueSpinner = new Spinner(selectedColor.b * 255.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        blueSpinner.setMinimum(0.0);
        blueSpinner.setMaximum(255.0);
        blueSpinner.getTextField().addListener(main.getIbeamListener());
        blueSpinner.getButtonMinus().addListener(main.getHandListener());
        blueSpinner.getButtonPlus().addListener(main.getHandListener());
        
        alphaSpinner = new Spinner(selectedColor.a * 255.0f, 1.0, true, Orientation.HORIZONTAL, skin);
        alphaSpinner.setMinimum(0.0);
        alphaSpinner.setMaximum(255.0);
        alphaSpinner.getTextField().addListener(main.getIbeamListener());
        alphaSpinner.getButtonMinus().addListener(main.getHandListener());
        alphaSpinner.getButtonPlus().addListener(main.getHandListener());
        
        final TextField hexField = new TextField(selectedColor.toString(), skin);
        hexField.setMaxLength(8);
        hexField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
            }
        });
        hexField.addListener(main.getIbeamListener());
        
        redSpinner.setTransversalNext(greenSpinner.getTextField());
        greenSpinner.setTransversalNext(blueSpinner.getTextField());
        blueSpinner.setTransversalNext(alphaSpinner.getTextField());
        alphaSpinner.setTransversalNext(hueSpinner.getTextField());
        hueSpinner.setTransversalNext(saturationSpinner.getTextField());
        saturationSpinner.setTransversalNext(brightnessSpinner.getTextField());
        brightnessSpinner.setTransversalNext(redSpinner.getTextField());
        
        redSpinner.setTransversalPrevious(brightnessSpinner.getTextField());
        greenSpinner.setTransversalPrevious(redSpinner.getTextField());
        blueSpinner.setTransversalPrevious(greenSpinner.getTextField());
        alphaSpinner.setTransversalPrevious(blueSpinner.getTextField());
        hueSpinner.setTransversalPrevious(alphaSpinner.getTextField());
        saturationSpinner.setTransversalPrevious(hueSpinner.getTextField());
        brightnessSpinner.setTransversalPrevious(saturationSpinner.getTextField());
        
        ChangeListener rgbListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                selectedColor.set((float) redSpinner.getValue() / 255.0f, (float) greenSpinner.getValue() / 255.0f, (float) blueSpinner.getValue() / 255.0f, (float) alphaSpinner.getValue() / 255.0f);
                Vector3 v = rgbToHsb(selectedColor.r, selectedColor.g, selectedColor.b);
                hueSpinner.setValue(v.x * 359.0f);
                saturationSpinner.setValue(v.y * 100.0f);
                brightnessSpinner.setValue(v.z * 100.0f);
                selectedColorCont.setColor(selectedColor);
                
                Color color = hsbToRgb((float) hueSpinner.getValue(), 1.0f, 1.0f);
                gradientS.setCol2(color);
                gradientS.setCol3(color);
                gradientAlpha.setCol3(color);
                gradientAlpha.setCol4(color);
                color = new Color(color);
                color.a = 0.0f;
                gradientAlpha.setCol1(color);
                gradientAlpha.setCol2(color);
                
                cursor.setX(v.y * SIZE - cursor.getWidth() / 2.0f);
                cursor.setY(v.z * SIZE - cursor.getHeight() / 2.0f);
                hueKnob.setY(v.x * SIZE - hueKnob.getHeight() / 2.0f);
                hueKnob2.setY(hueKnob.getY());
                
                hexField.setText(selectedColor.toString());
            }
        };
        redSpinner.addListener(rgbListener);
        greenSpinner.addListener(rgbListener);
        blueSpinner.addListener(rgbListener);
        
        ChangeListener hsbListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Color color = hsbToRgb((float) hueSpinner.getValue(), (float) saturationSpinner.getValue() / 100.0f, (float) brightnessSpinner.getValue() / 100.0f);
                color.a = (float) alphaSpinner.getValue() / 255.0f;
                redSpinner.setValue(color.r * 255.0f);
                greenSpinner.setValue(color.g * 255.0f);
                blueSpinner.setValue(color.b * 255.0f);
                selectedColor.set(color);
                selectedColorCont.setColor(selectedColor);
                
                color = hsbToRgb((float) hueSpinner.getValue(), 1.0f, 1.0f);
                gradientS.setCol2(color);
                gradientS.setCol3(color);
                gradientAlpha.setCol3(color);
                gradientAlpha.setCol4(color);
                color = new Color(color);
                color.a = 0.0f;
                gradientAlpha.setCol1(color);
                gradientAlpha.setCol2(color);
                
                cursor.setX((float) saturationSpinner.getValue() / 100.0f * SIZE - cursor.getWidth() / 2.0f);
                cursor.setY((float) brightnessSpinner.getValue() / 100.0f * SIZE - cursor.getHeight() / 2.0f);
                hueKnob.setY((float) hueSpinner.getValue() / 359.0f * SIZE - hueKnob.getHeight() / 2.0f);
                hueKnob2.setY(hueKnob.getY());
                
                hexField.setText(selectedColor.toString());
            }
        };
        hueSpinner.addListener(hsbListener);
        saturationSpinner.addListener(hsbListener);
        brightnessSpinner.addListener(hsbListener);
        
        ChangeListener hexListener = new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (hexField.getText().length() == 6 || hexField.getText().length() == 8) {
                    Color color = Color.valueOf(hexField.getText());

                    redSpinner.setValue(color.r * 255);
                    greenSpinner.setValue(color.g * 255);
                    blueSpinner.setValue(color.b * 255);
                    alphaSpinner.setValue(color.a * 255);

                    if (hexField.getText().length() == 6) {
                        selectedColor.set(color.r, color.g, color.b, (float) alphaSpinner.getValue() / 255.0f);
                    } else {
                        selectedColor.set(color.r, color.g, color.b, color.a);
                    }
                    Vector3 v = rgbToHsb(selectedColor.r, selectedColor.g, selectedColor.b);
                    hueSpinner.setValue(v.x * 359.0f);
                    saturationSpinner.setValue(v.y * 100.0f);
                    brightnessSpinner.setValue(v.z * 100.0f);
                    selectedColorCont.setColor(selectedColor);

                    color = hsbToRgb((float) hueSpinner.getValue(), 1.0f, 1.0f);
                    gradientS.setCol2(color);
                    gradientS.setCol3(color);
                    gradientAlpha.setCol3(color);
                    gradientAlpha.setCol4(color);
                    color = new Color(color);
                    color.a = 0.0f;
                    gradientAlpha.setCol1(color);
                    gradientAlpha.setCol2(color);

                    cursor.setX(v.y * SIZE - cursor.getWidth() / 2.0f);
                    cursor.setY(v.z * SIZE - cursor.getHeight() / 2.0f);
                    hueKnob.setY(v.x * SIZE - hueKnob.getHeight() / 2.0f);
                    hueKnob2.setY(hueKnob.getY());
                    
                    alphaKnob.setY(selectedColor.a * SIZE - alphaKnob.getHeight() / 2.0f);
                    alphaKnob2.setY(alphaKnob.getY());
                }
            }
        };
        hexField.addListener(hexListener);
        
        alphaSpinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                selectedColor.set((float) redSpinner.getValue() / 255.0f, (float) greenSpinner.getValue() / 255.0f, (float) blueSpinner.getValue() / 255.0f, (float) alphaSpinner.getValue() / 255.0f);
                selectedColorCont.setColor(selectedColor);
                
                alphaKnob.setY(selectedColor.a * SIZE - alphaKnob.getHeight() / 2.0f);
                alphaKnob2.setY(alphaKnob.getY());
                
                hexField.setText(selectedColor.toString());
            }
        });
        
        Table panel = new Table(skin);
        panel.setBackground("color-box");
        Table t = new Table(skin);
        t.setClip(true);
        t.setBackground(gradientSB);
        t.setTouchable(Touchable.enabled);
        cursor.setPosition(v.y * SIZE - cursor.getWidth() / 2.0f, v.z * SIZE - cursor.getHeight() / 2.0f);
        t.addActor(cursor);
        DragListener dragListener = new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                saturationSpinner.setValue(MathUtils.clamp(x / SIZE * 100.0f, 0, 100));
                brightnessSpinner.setValue(MathUtils.clamp(y / SIZE * 100.0f, 0, 100));
                saturationSpinner.fire(new ChangeListener.ChangeEvent());
            }
        };
        dragListener.setTapSquareSize(1.0f);
        t.addListener(dragListener);
        
        t.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                saturationSpinner.setValue(MathUtils.clamp(x / SIZE * 100.0f, 0, 100));
                brightnessSpinner.setValue(MathUtils.clamp(y / SIZE * 100.0f, 0, 100));
                saturationSpinner.fire(new ChangeListener.ChangeEvent());
                
                return false;
            }
        });
        panel.add(t).size(SIZE, SIZE);
        content.add(panel);
        
        panel = new Table(skin);
        panel.setBackground("color-box");
        t = new Table(skin);
        t.setTouchable(Touchable.enabled);
        t.setClip(true);
        for (GradientDrawable gradient : hueGradient) {
            Container container = new Container();
            container.background(gradient);
            t.add(container).growX().height(50.0f);
            t.row();
        }
        t.addActor(hueKnob);
        t.addActor(hueKnob2);
        hueKnob.setY(v.x * SIZE - hueKnob.getHeight() / 2.0f);
        hueKnob2.setX(30.0f - hueKnob2.getWidth());
        hueKnob2.setY(v.x * SIZE - hueKnob2.getHeight() / 2.0f);
        dragListener = new DragListener(){
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                hueSpinner.setValue(MathUtils.clamp(y / SIZE * 359.0f, 0.0f, 359.0f));
                
                hueSpinner.fire(new ChangeListener.ChangeEvent());
            }
        };
        dragListener.setTapSquareSize(1.0f);
        t.addListener(dragListener);
        t.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hueSpinner.setValue(MathUtils.clamp(y / SIZE * 359.0f, 0.0f, 359.0f));
                
                hueSpinner.fire(new ChangeListener.ChangeEvent());
                return false;
            }
            
        });
        panel.add(t).minWidth(30.0f).height(SIZE);
        content.add(panel);
        
        panel = new Table(skin);
        panel.setBackground("color-box");
        t = new Table();
        t.setTouchable(Touchable.enabled);
        t.setBackground(alphaStack);
        t.setClip(true);
        t.addActor(alphaKnob);
        t.addActor(alphaKnob2);
        alphaKnob.setY(selectedColor.a * SIZE - alphaKnob.getHeight() / 2.0f);
        alphaKnob2.setX(30.0f - alphaKnob2.getWidth());
        alphaKnob2.setY(selectedColor.a * SIZE - alphaKnob2.getHeight() / 2.0f);
        dragListener = new DragListener(){
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                alphaSpinner.setValue(MathUtils.clamp(y / SIZE * 255.0f, 0.0f, 255.0f));
                
                alphaSpinner.fire(new ChangeListener.ChangeEvent());
            }
        };
        dragListener.setTapSquareSize(1.0f);
        t.addListener(dragListener);
        t.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                alphaSpinner.setValue(MathUtils.clamp(y / SIZE * 255.0f, 0.0f, 255.0f));
                
                alphaSpinner.fire(new ChangeListener.ChangeEvent());
                return false;
            }
            
        });
        panel.add(t).minWidth(30.0f).height(SIZE);
        content.add(panel);
        
        t = new Table();
        t.defaults().pad(10.0f);

        Table table = new Table(skin);
        Label label = new Label("new", skin);
        label.setAlignment(Align.center);
        table.add(label).growX();
        table.row();
        Container bg = new Container();
        bg.setBackground(checker);
        Stack stack = new Stack(bg, selectedColorCont);
        panel = new Table(skin);
        panel.setBackground("color-box");
        panel.add(stack).grow();
        table.add(panel).grow();
        if (previousColor != null) {
            Container cont = new Container();
            cont.setBackground(skin.getDrawable("white"));
            cont.setColor(previousColor);
            bg = new Container();
            bg.setBackground(checker);
            stack = new Stack(bg, cont);
            panel.row();
            panel.add(stack).grow();
            table.row();
            label = new Label("current", skin);
            label.setAlignment(Align.center);
            table.add(label).growX();
            t.add(table).minWidth(80.0f).minHeight(150.0f);
        } else {
            t.add(table).minWidth(80.0f).minHeight(100.0f);
        }
        
        table = new Table();
        table.setName("confirmTable");
        table.defaults().space(10.0f).minWidth(80.0f);
        TextButton textButton = new TextButton("OK", skin);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (listener != null) {
                    listener.handle(new ColorListener.ColorEvent(selectedColor));
                }
                hide();
            }
        });
        table.add(textButton);
        textButton = new TextButton("Cancel", skin);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (listener != null) {
                    listener.handle(new ColorListener.ColorEvent(null));
                }
                hide();
            }
        });
        table.add(textButton);
        t.add(table);
        
        table.row();
        table = new Table();
        ((Table) t.findActor("confirmTable")).add(table).colspan(2);
        
        table.defaults().space(10.0f);
        label = new Label("#", skin, "required");
        table.add(label);
        
        table.add(hexField).width(75.0f);
        
        t.row();
        table = new Table();
        label = new Label("R", skin, "required");
        table.add(label);
        table.add(redSpinner).padLeft(10.0f).minWidth(90.0f);
        t.add(table);
        
        table = new Table();
        label = new Label("H", skin, "required");
        table.add(label);
        table.add(hueSpinner).padLeft(10.0f).minWidth(90.0f);
        t.add(table);
        
        t.row();
        table = new Table();
        label = new Label("G", skin, "required");
        table.add(label);
        table.add(greenSpinner).padLeft(10.0f).minWidth(90.0f);
        t.add(table);
        
        table = new Table();
        label = new Label("S", skin, "required");
        table.add(label);
        table.add(saturationSpinner).padLeft(10.0f).minWidth(90.0f);
        t.add(table);
        
        t.row();
        table = new Table();
        label = new Label("B", skin, "required");
        table.add(label);
        table.add(blueSpinner).padLeft(10.0f).minWidth(90.0f);
        t.add(table);
        
        table = new Table();
        label = new Label("B", skin, "required");
        table.add(label);
        table.add(brightnessSpinner).padLeft(10.0f).minWidth(90.0f);
        t.add(table);
        
        t.row();
        table = new Table();
        label = new Label("A", skin, "required");
        table.add(label);
        t.add(table);
        table.add(alphaSpinner).padLeft(10.0f).minWidth(90.0f);
        content.add(t).growY();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }
    
    public static abstract class ColorListener implements EventListener {

        @Override
        public boolean handle(Event event) {
            if (event instanceof ColorEvent) {
                selected(((ColorEvent) event).color);
            }
            return false;
        }
        
        abstract public void selected(Color color);
        
        public static class ColorEvent extends Event {
            Color color;
            
            public ColorEvent(Color color) {
                this.color = color;
            }
        }
    }
}
