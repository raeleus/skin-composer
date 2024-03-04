/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2024 Raymond Buckley
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
package com.ray3k.skincomposer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.stripe.Spinner.Orientation;
import com.ray3k.stripe.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.utils.Utils;

import java.math.BigDecimal;

public class BigSpinner extends Table {
    private BigDecimal value;
    private BigDecimal minimum;
    private BigDecimal maximum;
    private boolean usingMinimum, usingMaximum;
    private BigDecimal increment;
    private boolean rounding;
    private TextField textField;
    private Button buttonMinus;
    private Button buttonPlus;
    private Actor transversalNext, transversalPrevious;
    private Orientation orientation;
    private SpinnerStyle style;
    
    public BigSpinner(BigDecimal value, BigDecimal increment, boolean round, Orientation orientation, SpinnerStyle style) {
        this.value = value;
        rounding = round;
        this.orientation = orientation;
        usingMinimum = false;
        usingMaximum = false;
        this.increment = increment;
        this.style = style;
        
        addWidgets();
    }
    
    public BigSpinner(BigDecimal value, BigDecimal increment, boolean round, Orientation orientation, Skin skin, String style) {
        this(value, increment, round, orientation, skin.get(style, SpinnerStyle.class));
    }
    
    public BigSpinner(BigDecimal value, BigDecimal increment, boolean round, Orientation orientation, Skin skin) {
        this(value, increment, round, orientation, skin, "default");
    }
    
    private void addWidgets() {
        buttonMinus = new Button(style.buttonMinusStyle);
        buttonPlus = new Button(style.buttonPlusStyle);
        textField = new TextField("", style.textFieldStyle) {
            @Override
            public void next(boolean up) {
                if (up) {
                    if (transversalPrevious != null) {
                        getStage().setKeyboardFocus(transversalPrevious);
                        if (transversalPrevious instanceof TextField) {
                            ((TextField) transversalPrevious).selectAll();
                        }
                    } else {
                        super.next(up);
                    }
                } else {
                    if (transversalNext != null) {
                        getStage().setKeyboardFocus(transversalNext);
                        if (transversalNext instanceof TextField) {
                            ((TextField) transversalNext).selectAll();
                        }
                    } else {
                        super.next(up);
                    }
                }
            }
            
        };

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
            public boolean acceptChar(TextField textField1, char c) {
                boolean returnValue = false;
                if ((c >= 48 && c <= 57) || c == 45 || (!rounding && c == 46)) {
                    returnValue = true;
                }
                return returnValue;
            }
        });
        updateText();
        
        if (null != orientation) switch (orientation) {
            case HORIZONTAL:
                add(buttonMinus);
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                add(buttonPlus);
                break;
            case HORIZONTAL_FLIPPED:
                add(buttonPlus);
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                add(buttonMinus);
                break;
            case VERTICAL:
                add(buttonPlus);
                row();
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                row();
                add(buttonMinus);
                break;
            case VERTICAL_FLIPPED:
                add(buttonMinus);
                row();
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                row();
                add(buttonPlus);
                break;
            case RIGHT_STACK:
            {
                add(textField).prefWidth(style.textFieldStyle.background.getMinWidth()).minWidth(style.textFieldStyle.background.getMinWidth()).growX();
                
                VerticalGroup group = new VerticalGroup();
                add(group);
                
                group.addActor(buttonPlus);
                group.addActor(buttonMinus);
                break;
            }
            case LEFT_STACK:
            {
                VerticalGroup group = new VerticalGroup();
                add(group);
                
                group.addActor(buttonPlus);
                group.addActor(buttonMinus);
                
                add(textField).prefWidth(35.0f).minWidth(35.0f).growX();
                break;
            }
            default:
                break;
        }
        
        buttonMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                subtractValue();
            }
        });
        
        buttonPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addValue();
            }
        });
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                BigSpinner parent = (BigSpinner) actor;
                String text = textField.getText();
                
                if (text.matches("\\-?(\\d+\\.?\\d*)|(\\.\\d+)")) {
                    BigDecimal value = new BigDecimal(text);
                    if (usingMinimum && value.compareTo(parent.value) < 0) {
                        value = parent.minimum;
                    } else if (usingMaximum && value.compareTo(parent.value) > 0) {
                        value = parent.maximum;
                    }
                    parent.value = value;
                } else {
                    if (usingMinimum) {
                        parent.value = parent.minimum;
                    } else {
                        parent.value = BigDecimal.valueOf(0);
                    }
                }
            }
        });
        
        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                BigSpinner parent = (BigSpinner) textField.getParent();
                if (usingMinimum && parent.value.compareTo(parent.minimum) < 0) {
                    parent.value = parent.minimum;
                }
                if (usingMaximum && parent.value.compareTo(parent.maximum) > 0) {
                    parent.value = parent.maximum;
                }
                parent.updateText();
            }
            
        });
        
        final BigSpinner spinner = this;
        addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                event.setTarget(spinner);
            }
        });
    }
    
    private void subtractValue() {
        value = value.subtract(increment);
        if (usingMinimum && value.compareTo(minimum) < 0) {
            value = minimum;
        }
        if (usingMaximum && value.compareTo(maximum) > 0) {
            value = maximum;
        }
        updateText();
    }
    
    private void addValue() {
        value = value.add(increment);
        if (usingMinimum && value.compareTo(minimum) < 0) {
            value = minimum;
        }
        if (usingMaximum && value.compareTo(maximum) > 0) {
            value = maximum;
        }
        updateText();
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public boolean isInt() {
        return Utils.isIntegerValue(value);
    }
    
    public int getValueAsInt() {
        return value.intValue();
    }
    
    public void setValue(BigDecimal value) {
        setValue(value, true);
    }
    
    private void setValue(BigDecimal value, boolean updateText) {
        this.value = value;
        if (updateText) {
            updateText();
        }
    }

    public BigDecimal getMinimum() {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
        usingMinimum = true;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
        usingMaximum = true;
    }
    
    public void clearMinMax() {
        usingMinimum = false;
        usingMaximum = false;
    }
    
    private void updateText() {
        if (rounding) {
            textField.setText(Integer.toString((int)MathUtils.round(value.floatValue())));
        } else {
            textField.setText(value.toString());
        }
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButtonMinus() {
        return buttonMinus;
    }

    public Button getButtonPlus() {
        return buttonPlus;
    }

    public Actor getTransversalNext() {
        return transversalNext;
    }

    public void setTransversalNext(Actor transversalNext) {
        this.transversalNext = transversalNext;
    }

    public Actor getTransversalPrevious() {
        return transversalPrevious;
    }

    public void setTransversalPrevious(Actor transversalPrevious) {
        this.transversalPrevious = transversalPrevious;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        
        clear();
        addWidgets();
    }

    public SpinnerStyle getStyle() {
        return style;
    }

    public void setStyle(SpinnerStyle style) {
        this.style = style;
        
        clear();
        addWidgets();
    }
}
