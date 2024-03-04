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
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.graphics.Color;

public class ColorData {
    public Color color;
    private String name;

    public ColorData(String name, Color color) throws NameFormatException {
        setName(name);
        this.color = color;
    }
    
    public ColorData() {
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws NameFormatException {
        if (!validate(name)) {
            throw new NameFormatException();
        } else {
            this.name = name;
        }
    }
    
    public static class NameFormatException extends Exception {

        public NameFormatException() {
            super("Color names must be non-null and not include white spaces, start with a digit, or include non alpha-numeric characters");
        }
        
    }

    @Override
    public String toString() {
        return name;
    }
    
    public static boolean validate(String name) {
        return name != null && !name.matches("^\\d.*|^-.*|.*\\s.*|.*[^a-zA-Z\\d\\s-_ñáéíóúüÑÁÉÍÓÚÜ].*|^$");
    }
}
