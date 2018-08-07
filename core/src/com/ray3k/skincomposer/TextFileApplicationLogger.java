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

package com.ray3k.skincomposer;

import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.files.FileHandle;
import java.io.PrintStream;

public class TextFileApplicationLogger implements ApplicationLogger {
    private final FileHandle log;

    public TextFileApplicationLogger() {
        log = Main.appFolder.child("temp/log.txt");
    }
    
    @Override
    public void log(String tag, String message) {
        log.writeString("\n" + tag + ": " + message + "\n", true);
        
        System.out.println(tag + ": " + message);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        log.writeString("\n" + tag + ": " + message + "\n", true);
        printException(exception);
        
        System.out.println(tag + ": " + message);
        exception.printStackTrace(System.out);
    }

    @Override
    public void error(String tag, String message) {
        log.writeString("\n" + tag + ": " + message + "\n", true);
        
        System.err.println(tag + ": " + message);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        log.writeString("\n" + tag + ": " + message + "\n", true);
        printException(exception);
        
        System.err.println(tag + ": " + message);
        exception.printStackTrace(System.err);
    }

    @Override
    public void debug(String tag, String message) {
        log.writeString("\n" + tag + ": " + message + "\n", true);
        
        System.out.println(tag + ": " + message);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        log.writeString("\n" + tag + ": " + message + "\n", true);
        printException(exception);
        
        System.out.println(tag + ": " + message);
        exception.printStackTrace(System.out);
    }
    
    private void printException(Throwable exception) {
        PrintStream printStream = new PrintStream(log.write(true));
        exception.printStackTrace(printStream);
        printStream.close();
    }
}
