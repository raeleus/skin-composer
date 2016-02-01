package com.ray3k.skincomposer;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                 return c >= 48 && c <= 57 || c == 45 || c == 46;
            }
        });
        updateText();
        add(buttonLeft);
        add(textField).prefWidth(35.0f).minWidth(35.0f).growX();
        add(buttonRight);
        
        buttonLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner parent = (Spinner) actor;
                parent.value = parent.value.subtract(parent.increment);
                if (usingMinimum && parent.value.doubleValue() < parent.minimum) parent.value = BigDecimal.valueOf(parent.minimum);
                parent.updateText();
            }
        });
        
        buttonRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner parent = (Spinner) actor;
                parent.value = parent.value.add(parent.increment);
                if (usingMaximum && parent.value.doubleValue() > parent.maximum) parent.value = BigDecimal.valueOf(parent.maximum);
                parent.updateText();
            }
        });
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Spinner parent = (Spinner) actor;
                String text = textField.getText();
                if (text.matches(".*\\d.*")) {
                    parent.value = BigDecimal.valueOf(Double.parseDouble(text));
                } else {
                    parent.value = BigDecimal.valueOf(0);
                }
            }
        });
        
        final Spinner spinner = this;
        addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                event.setTarget(spinner);
            }
        });
    }
    
    public Double getValue() {
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
        if (rounding) {
            textField.setText(Integer.toString((int)MathUtils.round(value.floatValue())));
        } else {
            textField.setText(value.toString());
        }
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
}
