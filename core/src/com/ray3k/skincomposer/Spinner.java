package com.ray3k.skincomposer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import java.math.BigDecimal;

public class Spinner extends Table {
    private BigDecimal value;
    private Double minimum;
    private Double maximum;
    private boolean usingMinimum, usingMaximum;
    private BigDecimal increment;
    private boolean rounding;
    private TextField textField;

    public Spinner(double value, double increment, boolean round, SpinnerStyle style) {
        this.value = BigDecimal.valueOf(value);
        rounding = round;
        usingMinimum = false;
        usingMaximum = false;
        this.increment = BigDecimal.valueOf(increment);
        
        Button buttonLeft = new Button(style.buttonLeftStyle);
        Button buttonRight = new Button(style.buttonRightStyle);
        textField = new TextField("", style.textFieldStyle);
        textField.setAlignment(Align.center);
        
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.UP) {
                    addValue();
                    fire(new ChangeListener.ChangeEvent());
                } else if (keycode == Keys.DOWN) {
                    subtractValue();
                    fire(new ChangeListener.ChangeEvent());
                }
                return false;
            }
        });
        
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                boolean returnValue = false;
                if ((c >= 48 && c <= 57) || c == 45 || (!rounding && c == 46)) {
                    String text = textField.getText();
                    if (textField.getCursorPosition() <= text.length()) {
                        text = text.substring(0, textField.getCursorPosition());
                        text += c + textField.getText().substring(textField.getCursorPosition());
                    }
                    if (text.matches("-?\\d*\\.?\\d*")) {
                        returnValue = true;
                    }
                }
                
                return returnValue;
            }
        });
        updateText();
        add(buttonLeft);
        add(textField).prefWidth(35.0f).minWidth(35.0f).growX();
        add(buttonRight);
        
        buttonLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                subtractValue();
            }
        });
        
        buttonRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                addValue();
            }
        });
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner parent = (Spinner) actor;
                String text = textField.getText();
                
                if (text.matches("-?\\d+\\.?\\d*")) {
                    double value = Double.parseDouble(text);
                    parent.value = BigDecimal.valueOf(value);
                } else {
                    parent.value = BigDecimal.valueOf(0);
                }
            }
        });
        
        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                Spinner parent = (Spinner) textField.getParent();
                if (usingMinimum && parent.value.doubleValue() < parent.minimum) {
                    parent.value = BigDecimal.valueOf(parent.minimum);
                }
                if (usingMaximum && parent.value.doubleValue() > parent.maximum) {
                    parent.value = BigDecimal.valueOf(parent.maximum);
                }
                parent.updateText();
            }
            
        });
        
        textField.addListener(IbeamListener.get());
        
        final Spinner spinner = this;
        addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                event.setTarget(spinner);
            }
        });
    }
    
    private void subtractValue() {
        value = value.subtract(increment);
        if (usingMinimum && value.doubleValue() < minimum) {
            value = BigDecimal.valueOf(minimum);
        }
        if (usingMaximum && value.doubleValue() > maximum) {
            value = BigDecimal.valueOf(maximum);
        }
        updateText();
    }
    
    private void addValue() {
        value = value.add(increment);
        if (usingMinimum && value.doubleValue() < minimum) {
            value = BigDecimal.valueOf(minimum);
        }
        if (usingMaximum && value.doubleValue() > maximum) {
            value = BigDecimal.valueOf(maximum);
        }
        updateText();
    }
    
    public double getValue() {
        return value.doubleValue();
    }
    
    public void setValue(double value) {
        setValue(value, true);
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
        usingMinimum = true;
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
        usingMaximum = true;
    }
    
    public void clearMinMax() {
        usingMinimum = false;
        usingMaximum = false;
    }
    
    private void setValue(double value, boolean updateText) {
        this.value = BigDecimal.valueOf(value);
        if (updateText) {
            updateText();
        }
    }
    
    private void updateText() {
        int pos = textField.getCursorPosition();
        int startLength = textField.getText().length();
        if (rounding) {
            textField.setText(Integer.toString((int)MathUtils.round(value.floatValue())));
        } else {
            textField.setText(value.toString());
        }
        int length = textField.getText().length();
        
        pos += length - startLength;
        textField.setCursorPosition(Math.min(pos, length));
    }
    
    static public class SpinnerStyle {
        public ButtonStyle buttonLeftStyle, buttonRightStyle;
        public TextFieldStyle textFieldStyle;

        public SpinnerStyle(ButtonStyle buttonLeftStyle, ButtonStyle buttonRightStyle, TextFieldStyle textFieldStyle) {
            this.buttonLeftStyle = buttonLeftStyle;
            this.buttonRightStyle = buttonRightStyle;
            this.textFieldStyle = textFieldStyle;
        }

        public SpinnerStyle(SpinnerStyle style) {
            buttonLeftStyle = style.buttonLeftStyle;
            buttonRightStyle = style.buttonRightStyle;
            textFieldStyle = style.textFieldStyle;
        }
    }

    public TextField getTextField() {
        return textField;
    }
}
