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
package com.ray3k.skincomposer.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.ResizeFourArrowListener;
import com.ray3k.stripe.ResizeWidget;
import regexodus.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.ray3k.skincomposer.Main.*;

public class Utils {
    public static String os;
    public static Color averageColor(FileHandle file) {
        Pixmap pixmap = new Pixmap(file);
        Color returnValue = averageColor(pixmap);
        pixmap.dispose();
        return returnValue;
    }
    
    /**
     * Does not dispose pixmap.
     * @param pixmap
     * @return 
     */
    public static Color averageColor(Pixmap pixmap) {
        Color temp = new Color();
        float sumR = 0.0f;
        float sumG = 0.0f;
        float sumB = 0.0f;
        int count = 0;
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                }
            }
        }
        
        if (count == 0) {
            return new Color(Color.BLACK);
        } else {
            return new Color(sumR / count, sumG / count, sumB / count, 1.0f);
        }
    }
    
    public static Color averageEdgeColor(FileHandle file) {
        Pixmap pixmap = new Pixmap(file);
        Color returnValue = averageEdgeColor(pixmap, file.name().matches("(?i).*\\.9\\.png$"));
        pixmap.dispose();
        return returnValue;
    }
    
    public static Color averageEdgeColor(FileHandle file, Color color) {
        Pixmap pixmap = new Pixmap(file);
        pixmap = tintPixmap(pixmap, color);
        Color returnValue = averageEdgeColor(pixmap, file.name().matches("(?i).*\\.9\\.png$"));
        pixmap.dispose();
        return returnValue;
    }
    
    public static Vector2 imageDimensions(FileHandle file) {
        Vector2 vector = new Vector2();
        Pixmap pixmap = new Pixmap(file);
        vector.x = pixmap.getWidth();
        vector.y = pixmap.getHeight();
        if (file.name().matches("(?i).*\\.9\\.png$")) {
            vector.x = MathUtils.clamp(vector.x - 2, 0.0f, vector.x);
            vector.y = MathUtils.clamp(vector.y - 2, 0.0f, vector.y);
        }
        pixmap.dispose();
        return vector;
    }
    
    /**
     * Does not dispose pixmap
     * @param pixmap
     * @return 
     */
    public static Pixmap tintPixmap(Pixmap pixmap, Color color) {
        Color tempColor = new Color();
        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                tempColor.set(pixmap.getPixel(x, y));
                float a = tempColor.a;
                tempColor.mul(color);
                tempColor.a = a;
                pixmap.setColor(tempColor);
                pixmap.drawPixel(x, y);
                tempColor.set(pixmap.getPixel(x, y));
            }
        }
        return pixmap;
    }
    
    /**
     * Does not dispose pixmap.
     * @param pixmap
     * @param ninePatch
     * @return 
     */
    public static Color averageEdgeColor(Pixmap pixmap, boolean ninePatch) {
        int border = 0;
        if (ninePatch) {
            border = 1;
        }
        
        Color temp = new Color();
        float sumR = 0.0f;
        float sumG = 0.0f;
        float sumB = 0.0f;
        int count = 0;

        //left edge
        for (int y = border; y < pixmap.getHeight() - border; y++) {
            for (int x = border; x < pixmap.getWidth() - border; x++) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        //right edge
        for (int y = border; y < pixmap.getHeight() - border; y++) {
            for (int x = pixmap.getWidth() - 1 - border; x > border; x--) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        //top edge
        for (int x = border; x < pixmap.getWidth() - border; x++) {
            for (int y = border; y < pixmap.getHeight() - border; y++) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        //bottom edge
        for (int x = border; x < pixmap.getWidth() - border; x++) {
            for (int y = pixmap.getHeight() - 1 - border; y > border; y--) {
                temp.set(pixmap.getPixel(x, y));
                if (temp.a > 0) {
                    sumR += temp.r;
                    sumG += temp.g;
                    sumB += temp.b;
                    count++;
                    break;
                }
            }
        }
        
        if (count == 0) {
            return new Color(Color.BLACK);
        } else {
            return new Color(sumR / count, sumG / count, sumB / count, 1.0f);
        }
    }
    
    public static Color inverseColor(Color color) {
        return new Color(1 - color.r, 1 - color.g, 1 - color.b, color.a);
    }
    
    public static Color blackOrWhiteBgColor(Color color) {
        return brightness(color) > .5f ? new Color(Color.BLACK) : new Color(Color.WHITE);
    }
    
    public static float brightness(Color color) {
        return (float) (Math.sqrt(0.299f * Math.pow(color.r, 2) + 0.587 * Math.pow(color.g, 2) + 0.114 * Math.pow(color.b, 2)));
    }
    
    public static void openFileExplorer(FileHandle startDirectory) throws IOException {
        if (startDirectory.exists()) {
            File file = startDirectory.file();
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } else {
            throw new IOException("Directory doesn't exist: " + startDirectory.path());
        }
    }
    
    public static boolean isWindows() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        
        return os.startsWith("Windows");
    }
    
    public static boolean isLinux() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        return os.startsWith("Linux");
    }
    
    public static boolean isMac() {
        if (os == null) {
            os = System.getProperty("os.name");
        }
        return os.startsWith("Mac");
    }
    
    public static float floorPot(float value) {
        float returnValue = 0.0f;
        for (float newValue = 2.0f; newValue < value; newValue *= 2.0f) {
            returnValue = newValue;
        }
        
        return returnValue;
    }
    
    public static boolean doesImageFitBox(FileHandle fileHandle, float width, float height) {
        boolean result = false;
        String suffix = fileHandle.extension();
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try (var stream = new FileImageInputStream(fileHandle.file())) {
                reader.setInput(stream);
                int imageWidth = reader.getWidth(reader.getMinIndex());
                int imageHeight = reader.getHeight(reader.getMinIndex());
                result = imageWidth < width && imageHeight < height;
            } catch (IOException e) {
                Gdx.app.error(Utils.class.getName(), "error checking image dimensions", e);
            } finally {
                reader.dispose();
            }
        } else {
            Gdx.app.error(Utils.class.getName(), "No reader available to check image dimensions");
        }
        return result;
    }
    
    public static void writeWarningsToFile(Array<String> warnings, FileHandle file) {
        for (String warning : warnings) {
            String formatted = warning.replaceAll("(?<!\\[)\\[(?!\\[).*?\\]", "") + "\n";
            file.writeString(formatted, true);
        }
    }
    
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFile
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(FileHandle zipFile, FileHandle destDirectory) throws IOException {
        destDirectory.mkdirs();
        
        InputStream is = zipFile.read();
        ZipInputStream zis = new ZipInputStream(is);
        
        ZipEntry entry = zis.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zis, destDirectory.child(entry.getName()));
            } else {
                // if the entry is a directory, make the directory
                destDirectory.child(entry.getName()).mkdirs();
            }
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
        is.close();
        zis.close();
    }
    
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, FileHandle filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(filePath.write(false));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    
    public static Pixmap textureRegionToPixmap(TextureRegion textureRegion) {
        var texture = textureRegion.getTexture();
        if (!texture.getTextureData().isPrepared()) {
            texture.getTextureData().prepare();
        }
        
        var pixmap = texture.getTextureData().consumePixmap();
        var returnValue = new Pixmap(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), Pixmap.Format.RGBA8888);
        returnValue.setBlending(Pixmap.Blending.None);
        
        for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
            for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                int colorInt = pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y);
                returnValue.drawPixel(x, y, colorInt);
            }
        }
        
        pixmap.dispose();
        
        return returnValue;
    }
    
    public static Cursor textureRegionToCursor(TextureRegion textureRegion, int xHotspot, int yHotspot) {
        return Gdx.graphics.newCursor(textureRegionToPixmap(textureRegion), xHotspot, yHotspot);
    }
    
    public static int colorToInt(Color color) {
        return ((int)(255 * color.r) << 24) | ((int)(255 * color.g) << 16) | ((int)(255 * color.b) << 8) | ((int)(255 * color.a));
    }
    
    public static String removeDuplicateCharacters(String string) {
        char[] chars = string.toCharArray();
        Set<Character> charSet = new LinkedHashSet<Character>();
        for (char c : chars) {
            charSet.add(c);
        }

        var sb = new StringBuilder();
        charSet.forEach((character) -> {
            sb.append(character);
        });
        return sb.toString();
    }
    
    public static String sanitizeFilePath(String path) {
        var file = new FileHandle(path);
        return file.path();
    }
    
    public static boolean isIntegerValue(BigDecimal bigDecimal) {
        return bigDecimal.signum() == 0 || bigDecimal.scale() <= 0 || bigDecimal.stripTrailingZeros().scale() <= 0;
    }
    
    public static boolean isEqual(float... values) {
        if (values.length > 0) {
            float first = values[0];
            for (int i = 1; i < values.length; i++) {
                float value = values[i];
                if (!MathUtils.isEqual(first, value)) return false;
            }
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isNinePatch(String name) {
        return name.matches(".*\\.9\\.[a-zA-Z0-9]*$");
    }
    
    private static Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return numericPattern.matcher(strNum).matches();
    }
    
    public static boolean isTvg(String name) {
        return name.toLowerCase(Locale.ROOT).matches(".*\\.tvg$");
    }
    
    private final static GlyphLayout glyphLayout = new GlyphLayout();
    
    public static String leadingTruncate(BitmapFont font, CharSequence text, String truncate, float width) {
        if (text.length() == 0) return text.toString();
        var stringBuilder = new StringBuilder(text);
        stringBuilder = stringBuilder.reverse();
        
        glyphLayout.setText(font, stringBuilder, 0, stringBuilder.length(), Color.WHITE, width, Align.left, false, truncate);
    
        Array<Glyph> glyphs = glyphLayout.runs.first().glyphs;
        stringBuilder = new StringBuilder(glyphs.size);
        for (int i = glyphs.size - 1; i >= 0; i--) {
            Glyph g = glyphs.get(i);
            stringBuilder.append((char)g.id);
        }
        
        return stringBuilder.toString();
    }
    
    public static boolean isBitmap(String fileName) {
        return fileName.toLowerCase(Locale.ROOT).matches("^.*\\.(jpg)|(jpeg)|(png)|(bmp)|(gif)");
    }
    
    public static class PatchDefinition {
        public int left;
        public int right;
        public int top;
        public int bottom;
    }
    
    public static PatchDefinition calculatePatches(Pixmap pixmap) {
        var patchDefinition = new PatchDefinition();
    
        var startX = pixmap.getWidth() / 2;
        var color = new Color();
        var colorPrevious = new Color();
        var foundBreak = false;
    
        for (var x = startX - 1; x >= 0 && !foundBreak; x--) {
            for (var y = 0; y < pixmap.getHeight(); y++) {
                color.set(pixmap.getPixel(x, y));
                colorPrevious.set(pixmap.getPixel(x + 1, y));
            
                if (!color.equals(colorPrevious)) {
                    patchDefinition.left = x + 1;
                    foundBreak = true;
                    break;
                }
            }
        }
        if (!foundBreak) {
            patchDefinition.left = 0;
        }
        
        foundBreak = false;
        for (var x = startX + 1; x < pixmap.getWidth() && !foundBreak; x++) {
            for (var y = 0; y < pixmap.getHeight(); y++) {
                color.set(pixmap.getPixel(x, y));
                colorPrevious.set(pixmap.getPixel(x - 1, y));
            
                if (!color.equals(colorPrevious)) {
                    patchDefinition.right = pixmap.getWidth() - x;
                    foundBreak = true;
                    break;
                }
            }
        }
        if (!foundBreak) {
            patchDefinition.right = 0;
        }
    
        var startY = pixmap.getHeight() / 2;
        foundBreak = false;
        for (var y = startY - 1; y >= 0 && !foundBreak; y--) {
            for (var x = 0; x < pixmap.getWidth(); x++) {
                color.set(pixmap.getPixel(x, y));
                colorPrevious.set(pixmap.getPixel(x, y + 1));
            
                if (!color.equals(colorPrevious)) {
                    patchDefinition.top = y + 1;
                    foundBreak = true;
                    break;
                }
            }
        }
        if (!foundBreak) {
            patchDefinition.top = 0;
        }
    
        foundBreak = false;
        for (var y = startY + 1; y < pixmap.getHeight() && !foundBreak; y++) {
            for (var x = 0; x < pixmap.getWidth(); x++) {
                color.set(pixmap.getPixel(x, y));
                colorPrevious.set(pixmap.getPixel(x, y - 1));
            
                if (!color.equals(colorPrevious)) {
                    patchDefinition.bottom = pixmap.getHeight() - y;
                    foundBreak = true;
                    break;
                }
            }
        }
        if (!foundBreak) {
            patchDefinition.bottom = 0;
        }
        return patchDefinition;
    }
    
    public static boolean fontHasAllChars(BitmapFontData data, String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (data.getGlyph(c) == null) {
                return false;
            }
        }
        return true;
    }
    
    public static void onChange(Actor actor, Runnable runnable) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                runnable.run();
            }
        });
    }
    
    public static void onEnter(Actor actor, Runnable runnable) {
        actor.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1 && fromActor != event.getListenerActor() && !fromActor.isDescendantOf(event.getListenerActor())) runnable.run();
            }
        });
    }
    
    public static void applyResizeArrowListener(ResizeWidget resizeWidget) {
        var resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.NESWResize);
        resizeWidget.getBottomLeftHandle().addListener(resizeFourArrowListener);
        resizeWidget.getTopRightHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.NWSEResize);
        resizeWidget.getTopLeftHandle().addListener(resizeFourArrowListener);
        resizeWidget.getBottomRightHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.VerticalResize);
        resizeWidget.getBottomHandle().addListener(resizeFourArrowListener);
        resizeWidget.getTopHandle().addListener(resizeFourArrowListener);
        
        resizeFourArrowListener = new ResizeFourArrowListener(SystemCursor.HorizontalResize);
        resizeWidget.getLeftHandle().addListener(resizeFourArrowListener);
        resizeWidget.getRightHandle().addListener(resizeFourArrowListener);
    }
}