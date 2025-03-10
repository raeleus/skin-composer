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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin.TintedDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;
import com.ray3k.skincomposer.data.DrawableData.DrawableType;
import com.ray3k.skincomposer.dialog.DialogFactory;
import com.ray3k.skincomposer.dialog.DialogTVG.TvgData;
import com.ray3k.skincomposer.dialog.tenpatch.DialogTenPatch;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.tenpatch.TenPatchDrawable;
import com.ray3k.tenpatch.TenPatchDrawable.CrushMode;
import dev.lyze.gdxtinyvg.scene2d.TinyVGDrawable;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.data.DrawableData.DrawableType.*;

import java.io.StringWriter;

public class JsonData implements Json.Serializable {
    private Array<ColorData> colors;
    private Array<FontData> fonts;
    private Array<FreeTypeFontData> freeTypeFonts;
    private OrderedMap<Class, Array<StyleData>> classStyleMap;
    private Array<CustomClass> customClasses;
    
    public static enum ExportFormat {
        MINIMAL("Minimal", JsonWriter.OutputType.minimal), JAVASCRIPT("JavaScript", JsonWriter.OutputType.javascript), JSON("JSON", JsonWriter.OutputType.json);
        
        private final String name;
        private final JsonWriter.OutputType outputType;
        
        ExportFormat(String name, JsonWriter.OutputType outputType) {
            this.name = name;
            this.outputType = outputType;
        }

        @Override
        public String toString() {
            return name;
        }

        public JsonWriter.OutputType getOutputType() {
            return outputType;
        }
    }

    public JsonData() {
        colors = new Array<>();
        fonts = new Array<>();
        freeTypeFonts = new Array<>();

        initializeClassStyleMap();
        customClasses = new Array<>();
    }

    public void clear() {
        colors.clear();
        fonts.clear();
        for (FreeTypeFontData font : freeTypeFonts) {
            if (font.bitmapFont != null) font.bitmapFont.dispose();
        }
        freeTypeFonts.clear();
        initializeClassStyleMap();
        customClasses.clear();
    }

    /**
     * Imports skin data from a JSON file. Supports skins from libGDX 1.9.10
     * @param fileHandle
     * @return
     * @throws Exception 
     */
    public Array<String> readFile(FileHandle fileHandle) throws Exception {
        Array<String> warnings = new Array<>();
        
        projectData.setChangesSaved(false);
        
        //read drawables from texture atlas file
        FileHandle atlasHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".atlas");
        if (atlasHandle.exists()) {
            projectData.getAtlasData().readAtlas(atlasHandle);
        } else {
            warnings.add("[RED]ERROR:[] Atlas file [BLACK]" + atlasHandle.name() + "[] does not exist.");
        }

        //folder for critical files to be copied to
        FileHandle saveFile = projectData.getSaveFile();
        FileHandle targetDirectory;
        if (saveFile != null) {
            targetDirectory = saveFile.sibling(saveFile.nameWithoutExtension() + "_data");
        } else {
            targetDirectory = Main.appFolder.child("temp/" + projectData.getId() + "_data");
        }

        //read json file and create styles
        JsonReader reader = new JsonReader();
        JsonValue val = reader.parse(fileHandle.reader("UTF-8"));

        for (JsonValue child : val.iterator()) {
            //fonts
            if (child.name().equals(BitmapFont.class.getName()) || child.name().equals(BitmapFont.class.getSimpleName())) {
                for (JsonValue font : child.iterator()) {
                    if (font.get("file") != null) {
                        FileHandle fontFile = fileHandle.sibling(font.getString("file"));
                        if (!fontFile.exists()) {
                            warnings.add("[RED]ERROR:[] Font file [BLACK]" + fontFile.name() + "[] does not exist.");
                            return warnings;
                        }
                        FileHandle fontCopy = targetDirectory.child(font.getString("file"));
                        if (!fontCopy.parent().equals(fontFile.parent())) {
                            fontFile.copyTo(fontCopy);
                        }
                        FontData fontData = new FontData(font.name(), font.getInt("scaledSize", -1), font.getBoolean("markupEnabled", false), font.getBoolean("flip", false), fontCopy);

                        //delete fonts with the same name
                        for (FontData originalData : new Array<>(fonts)) {
                            if (originalData.getName().equals(fontData.getName())) {
                                fonts.removeValue(originalData, true);
                            }
                        }

                        fonts.add(fontData);

                        BitmapFont.BitmapFontData bitmapFontData = new BitmapFont.BitmapFontData(fontCopy, false);
                        for (String path : bitmapFontData.imagePaths) {
                            FileHandle file = new FileHandle(path);
                            
                            var drawable = projectData.getAtlasData().getDrawable(file.nameWithoutExtension());
                            if(drawable == null) {
                                drawable = projectData.getAtlasData().getFontDrawable(file.nameWithoutExtension());
                                if (drawable == null) {
                                    Gdx.app.error(getClass().getName(), "The Drawable for a BitmapFont was missing.");
                                    warnings.add("[RED]In font: [BLACK]" + bitmapFontData.name + "[], with path: [BLACK]" + path + "[], drawable: [BLACK]" + file.nameWithoutExtension() + "[] was not found!");
                                    continue; // ignores this drawable rather than throwing an NPE
                                }
                            }
                            drawable.type = DrawableType.FONT;
                            
                            projectData.getAtlasData().getDrawables().removeValue(drawable, false);
                            projectData.getAtlasData().getFontDrawables().add(drawable);
                        }
                    }
                }
            } //FreeType fonts
            else if (child.name().equals(FreeTypeFontGenerator.class.getName())) {
                for (JsonValue font : child.iterator()) {
                    if (font.get("font") != null) {
                        FreeTypeFontData data = new FreeTypeFontData();
                        data.name = font.name;
                        data.previewTTF = font.getString("previewTTF", null);
                        data.useCustomSerializer= font.getBoolean("useCustomSerializer", true);
                        data.size = font.getInt("size", 16);
                        data.mono = font.getBoolean("mono", false);
                        data.hinting = font.getString("hinting", "AutoMedium");
                        data.color = font.getString("color", null);
                        data.gamma = font.getFloat("gamma", 1.8f);
                        data.renderCount = font.getInt("renderCount", 2);
                        data.borderWidth = font.getFloat("borderWidth", 0);
                        data.borderColor = font.getString("borderColor", null);
                        data.borderStraight = font.getBoolean("borderStraight", false);
                        data.borderGamma = font.getFloat("borderGamma", 1.8f);
                        data.shadowOffsetX = font.getInt("shadowOffsetX", 0);
                        data.shadowOffsetY = font.getInt("shadowOffsetY", 0);
                        data.shadowColor = font.getString("shadowColor", null);
                        data.spaceX = font.getInt("spaceX", 0);
                        data.spaceY = font.getInt("spaceY", 0);
                        data.characters = font.getString("characters", "");
                        data.kerning = font.getBoolean("kerning", true);
                        data.flip = font.getBoolean("flip", false);
                        data.genMipMaps = font.getBoolean("genMipMaps", false);
                        data.minFilter = font.getString("minFilter", "Nearest");
                        data.magFilter = font.getString("magFilter", "Nearest");
                        data.incremental = font.getBoolean("bitmapFont", false);

                        FileHandle fontFile = fileHandle.sibling(font.getString("font"));
                        if (!fontFile.exists()) {
                            warnings.add("[RED]ERROR:[] Font file [BLACK]" + fontFile.name() + "[] does not exist.");
                            return warnings;
                        }
                        FileHandle fontCopy = targetDirectory.child(font.getString("font"));
                        if (!fontCopy.parent().equals(fontFile.parent()) && !fontCopy.exists()) {
                            fontFile.copyTo(fontCopy);
                        }
                        data.file = fontCopy;
                        data.createBitmapFont();

                        if (data.bitmapFont != null) {
                            //delete fonts with the same name
                            for (FontData duplicate : new Array<>(fonts)) {
                                if (duplicate.getName().equals(data.name)) {
                                    fonts.removeValue(duplicate, false);
                                }
                            }

                            for (FreeTypeFontData duplicate : new Array<>(freeTypeFonts)) {
                                if (duplicate.name.equals(data.name)) {
                                    freeTypeFonts.removeValue(duplicate, false);
                                }
                            }

                            freeTypeFonts.add(data);
                        }
                    }
                }
            } //colors
            else if (child.name().equals(Color.class.getName()) || child.name().equals(Color.class.getSimpleName())) {
                for (JsonValue color : child.iterator()) {
                    var colorValue = new Color();
                    if (color.has("hex")) {
                        colorValue.set(Color.valueOf(color.getString("hex")));
                    } else {
                        colorValue.set(color.getFloat("r", 1.0f), color.getFloat("g", 1.0f), color.getFloat("b", 1.0f), color.getFloat("a", 1.0f));
                    }
                    ColorData colorData = new ColorData(color.name, colorValue);
                    
                    //delete colors with the same name
                    for (ColorData originalData : new Array<>(colors)) {
                        if (originalData.getName().equals(colorData.getName())) {
                            colors.removeValue(originalData, true);
                        }
                    }
                    
                    colors.add(colorData);
                }
            }
            //drawables
            else if (child.name().equals(TextureRegionDrawable.class.getName()) || child.name().equals(TextureRegionDrawable.class.getSimpleName()) || child.name().equals(NinePatchDrawable.class.getName()) || child.name().equals(NinePatchDrawable.class.getSimpleName())) {
                for (JsonValue jsonValue : child.iterator()) {
                    DrawableData drawableData = projectData.getAtlasData().getDrawable(jsonValue.name);
                    if (jsonValue.has("minWidth")) drawableData.minWidth = jsonValue.getFloat("minWidth");
                    if (jsonValue.has("minHeight")) drawableData.minHeight = jsonValue.getFloat("minHeight");
                }
            } //tiled drawables
            else if (child.name().equals(TiledDrawable.class.getName()) || child.name().equals(TiledDrawable.class.getSimpleName())) {
                for (JsonValue tiledDrawable : child.iterator()) {
                    DrawableData drawableData = new DrawableData(projectData.getAtlasData().getDrawable(tiledDrawable.getString("region")).file);
                    drawableData.type = DrawableType.TILED;
                    drawableData.name = tiledDrawable.name;
                    
                    drawableData.tiled = true;
                    drawableData.tintName = tiledDrawable.getString("color");
                    drawableData.minWidth = tiledDrawable.getFloat("minWidth", 0.0f);
                    drawableData.minHeight = tiledDrawable.getFloat("minHeight", 0.0f);
   
                    //delete drawables with the same name
                    for (DrawableData originalData : new Array<>(projectData.getAtlasData().getDrawables())) {
                        if (originalData.name.equals(drawableData.name)) {
                            projectData.getAtlasData().getDrawables().removeValue(originalData, true);
                        }
                    }
                    
                    projectData.getAtlasData().getDrawables().add(drawableData);
                }
            } //tinted drawables
            else if (child.name().equals(TintedDrawable.class.getName()) || child.name().equals(TintedDrawable.class.getSimpleName())) {
                for (JsonValue tintedDrawable : child.iterator()) {
                    DrawableData drawableData = new DrawableData(projectData.getAtlasData().getDrawable(tintedDrawable.getString("name")).file);
                    drawableData.name = tintedDrawable.name;
                    
                    if (!tintedDrawable.get("color").isString()) {
                        drawableData.type = TINTED;
                        drawableData.tint = new Color();
                        if (tintedDrawable.get("color").has("hex")) {
                            drawableData.tint.set(Color.valueOf(tintedDrawable.get("color").getString("hex")));
                        } else {
                            drawableData.tint.set(tintedDrawable.get("color").getFloat("r", 1.0f), tintedDrawable.get("color").getFloat("g", 1.0f), tintedDrawable.get("color").getFloat("b", 1.0f), tintedDrawable.get("color").getFloat("a", 1.0f));
                        }
                    } else {
                        drawableData.type = TINTED_FROM_COLOR_DATA;
                        drawableData.tintName = tintedDrawable.getString("color");
                    }
    
                    if (tintedDrawable.has("minWidth")) drawableData.minWidth = tintedDrawable.getFloat("minWidth");
                    if (tintedDrawable.has("minHeight")) drawableData.minHeight = tintedDrawable.getFloat("minHeight");
                    
                    //delete drawables with the same name
                    for (DrawableData originalData : new Array<>(projectData.getAtlasData().getDrawables())) {
                        if (originalData.name.equals(drawableData.name)) {
                            projectData.getAtlasData().getDrawables().removeValue(originalData, true);
                        }
                    }
                    
                    projectData.getAtlasData().getDrawables().add(drawableData);
                }
            } //ten patch drawables
            else if (child.name().equals(TenPatchDrawable.class.getName()) || child.name().equals(TenPatchDrawable.class.getSimpleName())) {
                for (JsonValue value : child.iterator()) {
                    DrawableData drawableData = new DrawableData(projectData.getAtlasData().getDrawable(value.getString("region")).file);
                    drawableData.type = DrawableType.TENPATCH;
                    drawableData.name = value.name();
                    drawableData.tenPatchData = new DialogTenPatch.TenPatchData();
                    drawableData.tenPatchData.colorName = value.getString("color", null);
                    drawableData.tenPatchData.horizontalStretchAreas = new IntArray(value.get("horizontalStretchAreas").asIntArray());
                    drawableData.tenPatchData.verticalStretchAreas = new IntArray(value.get("verticalStretchAreas").asIntArray());
                    drawableData.tenPatchData.tile = value.getBoolean("tiling");
                    drawableData.minWidth = value.getFloat("minWidth", 0f);
                    drawableData.minHeight = value.getFloat("minHeight", 0f);
                    drawableData.tenPatchData.contentRight = value.getInt("rightWidth", 0);
                    drawableData.tenPatchData.contentLeft = value.getInt("leftWidth", 0);
                    drawableData.tenPatchData.contentBottom = value.getInt("bottomHeight", 0);
                    drawableData.tenPatchData.contentTop = value.getInt("topHeight", 0);
                    drawableData.tenPatchData.color1Name = value.getString("color1", null);
                    drawableData.tenPatchData.color2Name = value.getString("color2", null);
                    drawableData.tenPatchData.color3Name = value.getString("color3", null);
                    drawableData.tenPatchData.color4Name = value.getString("color4", null);
                    drawableData.tenPatchData.offsetX = value.getInt("offsetX", 0);
                    drawableData.tenPatchData.offsetY = value.getInt("offsetY", 0);
                    drawableData.tenPatchData.offsetXspeed = value.getInt("offsetXspeed", 0);
                    drawableData.tenPatchData.offsetYspeed = value.getInt("offsetYspeed", 0);
                    drawableData.tenPatchData.frameDuration = value.getFloat("frameDuration", 0);
                    drawableData.tenPatchData.regionNames = new Array<>(value.get("regions").asStringArray());
                    drawableData.tenPatchData.playMode = value.getInt("playMode", TenPatchDrawable.PlayMode.LOOP);
                    drawableData.tenPatchData.crushMode = value.getInt("crushMode", CrushMode.SHRINK);
    
                    //delete drawables with the same name
                    for (DrawableData originalData : new Array<>(projectData.getAtlasData().getDrawables())) {
                        if (originalData.name.equals(drawableData.name)) {
                            projectData.getAtlasData().getDrawables().removeValue(originalData, true);
                        }
                    }
    
                    projectData.getAtlasData().getDrawables().add(drawableData);
                }
            } //TVG drawables
            else if (child.name().equals(TinyVGDrawable.class.getName()) || child.name().equals(TinyVGDrawable.class.getSimpleName())) {
                for (JsonValue value : child.iterator()) {
                    DrawableData drawableData = new DrawableData();
                    
                    FileHandle tvgFile = fileHandle.sibling(value.getString("file"));
                    System.out.println("tvgFile.path() = " + tvgFile.path());
                    if (!tvgFile.exists()) {
                        warnings.add("[RED]ERROR:[] TVG file [BLACK]" + tvgFile.name() + "[] does not exist.");
                        return warnings;
                    }
                    FileHandle tvgCopy = targetDirectory.child(value.getString("file"));
                    if (!tvgCopy.parent().equals(tvgFile.parent()) && !tvgCopy.exists()) {
                        tvgFile.copyTo(tvgCopy);
                    }
                    drawableData.file = tvgCopy;
                    
                    drawableData.type = TVG;
                    drawableData.name = value.name();
                    drawableData.tvgData = new TvgData();
                    drawableData.tvgData.clipBasedOnTVGsize = value.getBoolean("clipBasedOnTVGsize");
        
                    //delete drawables with the same name
                    for (DrawableData originalData : new Array<>(projectData.getAtlasData().getDrawables())) {
                        if (originalData.name.equals(drawableData.name)) {
                            projectData.getAtlasData().getDrawables().removeValue(originalData, true);
                        }
                    }
        
                    projectData.getAtlasData().getDrawables().add(drawableData);
                }
            } //styles
            else {
                int classIndex = 0;
                
                Class matchClass = findStyleClassByName(child.name);
                
                if (matchClass != null) {
                    for (Class clazz : Main.STYLE_CLASSES) {
                        if (clazz.equals(matchClass)) {
                            break;
                        } else {
                            classIndex++;
                        }
                    }

                    Class clazz = Main.BASIC_CLASSES[classIndex];
                    for (JsonValue style : child.iterator()) {
                        StyleData data = newStyle(clazz, style.name);
                        for (JsonValue property : style.iterator()) {
                            if (property.name.equals("parent")) {
                                data.parent = property.asString();
                            } else {
                                StyleProperty styleProperty = data.properties.get(property.name);
                                if (styleProperty.type.equals(Float.TYPE)) {
                                    styleProperty.value = (double) property.asFloat();
                                } else if (styleProperty.type.equals(Color.class)) {
                                    if (property.isString()) {
                                        styleProperty.value = property.asString();
                                    } else {
                                        Gdx.app.error(getClass().getName(), "Can't import JSON files that do not use predefined colors.");
                                        warnings.add("Property [BLACK]" + styleProperty.name + "[] value cleared for [BLACK]" + clazz.getSimpleName() + ": " + data.name + "[] (Unsupported color definition)");
                                    }
                                } else {
                                    if (property.isString()) {
                                        styleProperty.value = property.asString();
                                    } else {
                                        Gdx.app.error(getClass().getName(), "Can't import JSON files that do not use String names for field values.");
                                        warnings.add("Property [BLACK]" + styleProperty.name + "[] value cleared for [BLACK]" + clazz.getSimpleName() + ": " + data.name + "[] (Unsupported property value)");
                                    }
                                }
                            }
                        }
                    }
                } else { //custom classes
                    CustomClass customClass = new CustomClass(child.name, child.name.replaceFirst(".*(\\.|\\$)", ""));
                    
                    CustomClass existingClass = getCustomClass(customClass.getDisplayName());
                    if (existingClass != null) {
                        customClasses.removeValue(existingClass, true);
                    }
                    
                    customClasses.add(customClass);
                    for (JsonValue style : child.iterator()) {
                        CustomStyle customStyle = new CustomStyle(style.name);
                        customStyle.setParentClass(customClass);
                        
                        CustomStyle existingStyle = customClass.getStyle(style.name);
                        if (existingStyle != null) {
                            customClass.getStyles().removeValue(existingStyle, true);
                        }
                        
                        if (customStyle.getName().equals("default")) {
                            customStyle.setDeletable(false);
                        }
                        
                        customClass.getStyles().add(customStyle);
                        
                        for (JsonValue property : style.iterator()) {
                            CustomProperty customProperty = new CustomProperty();
                            customProperty.setName(property.name);
                            customProperty.setParentStyle(customStyle);
                            
                            CustomProperty existingProperty = customStyle.getProperty(property.name);
                            if (existingProperty != null) {
                                customStyle.getProperties().removeValue(existingProperty, true);
                            }
                            
                            
                            if (property.isNumber()) {
                                customProperty.setType(PropertyType.NUMBER);
                                customProperty.setValue(property.asDouble());
                            } else if (property.isString()) {
                                customProperty.setType(PropertyType.TEXT);
                                customProperty.setValue(property.asString());
                            } else if (property.isBoolean()) {
                                customProperty.setType(PropertyType.BOOL);
                                customProperty.setValue(property.asBoolean());
                            } else if (property.isObject()) {
                                customProperty.setType(PropertyType.RAW_TEXT);
                                customProperty.setValue(property.toJson(OutputType.minimal));
                            } else if (property.isArray()) {
                                customProperty.setType(PropertyType.RAW_TEXT);
                                customProperty.setValue(property.toJson(OutputType.minimal));
                            } else {
                                customProperty = null;
                            }
                            
                            if (customProperty != null) {
                                customStyle.getProperties().add(customProperty);

                                //add to template style as necessary
                                if (customClass.getTemplateStyle().getProperty(customProperty.getName()) == null) {
                                    CustomProperty dupeProperty = customProperty.copy();
                                    dupeProperty.setValue(null);
                                    customClass.getTemplateStyle().getProperties().add(dupeProperty);
                                }
                            }
                        }
                    }
                    
                    //ensure default style has all the template styles.
                    for (CustomStyle style : customClass.getStyles()) {
                        if (style.getName().equals("default")) {
                            for (CustomProperty templateProperty : customClass.getTemplateStyle().getProperties()) {
                                boolean hasProperty = false;
                                for (CustomProperty customProperty : style.getProperties()) {
                                    if (customProperty.getName().equals(templateProperty.getName())) {
                                        hasProperty = true;
                                        break;
                                    }
                                }
                                
                                if (!hasProperty) {
                                    style.getProperties().add(templateProperty.copy());
                                }
                            }
                            
                            break;
                        }
                    }
                }
            }
        }
        
        return warnings;
    }
    
    public void checkForPropertyConsistency() {
        for (Class clazz : classStyleMap.keys()) {
            for (StyleData styleData : classStyleMap.get(clazz)) {
                for (StyleProperty property : styleData.properties.values()) {
                    if (property.value != null) {
                        boolean keep = false;
                        if (property.type == Color.class) {
                            for (ColorData color : colors) {
                                if (property.value.equals(color.getName())) {
                                    keep = true;
                                    break;
                                }
                            }
                        } else if (property.type == BitmapFont.class) {
                            for (FontData font : fonts) {
                                if (property.value.equals(font.getName())) {
                                    keep = true;
                                    break;
                                }
                            }
                            for (FreeTypeFontData font : freeTypeFonts) {
                                if (property.value.equals(font.name)) {
                                    keep = true;
                                    break;
                                }
                            }
                        } else if (property.type == Drawable.class) {
                            for (DrawableData drawable : atlasData.getDrawables()) {
                                if (property.value.equals(drawable.name)) {
                                    keep = true;
                                    break;
                                }
                            }
                            
                            if (!keep) {
                                keep = true;
                                DrawableData customDrawable = new DrawableData((String) property.value);
                                customDrawable.type = DrawableType.CUSTOM;
                                atlasData.getDrawables().add(customDrawable);
                            }
                        } else {
                            keep = true;
                        }

                        if (!keep) {
                            property.value = null;
                        }
                    }
                }
            }
        }
        
        for (CustomClass customClass : customClasses) {
            for (CustomStyle customStyle: customClass.getStyles()) {
                for (CustomProperty customProperty : customStyle.getProperties()) {
                    if (customProperty.getValue() != null) {
                        boolean keep = false;
                        if (null == customProperty.getType()) {
                            keep = true;
                        } else switch (customProperty.getType()) {
                            case COLOR:
                                for (ColorData color : colors) {
                                    if (customProperty.getValue().equals(color.getName())) {
                                        keep = true;
                                        break;
                                    }
                                }   break;
                            case DRAWABLE:
                                for (DrawableData drawable : atlasData.getDrawables()) {
                                    if (customProperty.getValue().equals(drawable.name)) {
                                        keep = true;
                                        break;
                                    }
                                }   break;
                            case FONT:
                                for (FontData font : fonts) {
                                    if (customProperty.getValue().equals(font.getName())) {
                                        keep = true;
                                        break;
                                    }
                                }   break;
                            default:
                                keep = true;
                                break;
                        }

                        if (!keep) {
                            customProperty.setValue(null);
                        }
                    }
                }
            }
        }
    }
    
    public CustomClass getCustomClass(String name) {
        for (CustomClass customClass : customClasses) {
            if (customClass.getDisplayName().equals(name)) {
                return customClass;
            }
        }
        
        return null;
    }
    
    /**
     * Will take a fully qualified class name or simple name and return true if it matches a style class.
     * @param name
     * @return 
     */
    private Class findStyleClassByName(String name) {
        Class returnValue = null;
        
        for (Class clazz : Main.STYLE_CLASSES) {
            if (name.equals(clazz.getName()) || name.equals(clazz.getSimpleName())) {
                returnValue = clazz;
                break;
            }
        }
        
        return returnValue;
    }

    /**
     * Exports skin data to a JSON file to be loaded by libGDX.
     * @param fileHandle
     * @return 
     */
    public Array<String> writeFile(FileHandle fileHandle) {
        Array<String> warnings = new Array<>();
        
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setOutputType(projectData.getExportFormat().getOutputType());
        
        Json json = new Json(projectData.getExportFormat().getOutputType());
        json.setWriter(jsonWriter);
        json.writeObjectStart();

        //fonts
        if (fonts.size > 0) {
            String className = projectData.isUsingSimpleNames() ? BitmapFont.class.getSimpleName() : BitmapFont.class.getName();
            json.writeObjectStart(className);
            for (FontData font : fonts) {
                json.writeObjectStart(font.getName());
                json.writeValue("file", font.file.name());
                json.writeValue("scaledSize", font.getScaling());
                json.writeValue("markupEnabled", font.isMarkupEnabled());
                json.writeValue("flip", font.isFlip());
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }

        //colors
        if (colors.size > 0) {
            String className = projectData.isUsingSimpleNames() ? Color.class.getSimpleName() : Color.class.getName();
            json.writeObjectStart(className);
            for (ColorData color : colors) {
                json.writeObjectStart(color.getName());
                if (projectData.isExportingHex()) {
                    json.writeValue("hex", color.color.toString());
                    json.writeObjectEnd();
                } else {
                    json.writeValue("r", color.color.r);
                    json.writeValue("g", color.color.g);
                    json.writeValue("b", color.color.b);
                    json.writeValue("a", color.color.a);
                    json.writeObjectEnd();
                }
            }
            json.writeObjectEnd();
        }
        
        //FreeType fonts
        boolean exportFreeType = false;
        for (FreeTypeFontData font : freeTypeFonts) {
            if (font.useCustomSerializer) {
                exportFreeType = true;
                break;
            }
        }

        if (exportFreeType) {
            json.writeObjectStart(FreeTypeFontGenerator.class.getName());
            for (FreeTypeFontData font : freeTypeFonts) {
                if (font.useCustomSerializer) {
                    json.writeObjectStart(font.name);
                    json.writeValue("font", font.file.name());
                    json.writeValue("size", font.size);
                    json.writeValue("mono", font.mono);
                    if (font.color != null) json.writeValue("color", font.color);
                    json.writeValue("gamma", font.gamma);
                    json.writeValue("renderCount", font.renderCount);
                    json.writeValue("borderWidth", font.borderWidth);
                    if (font.borderColor != null) json.writeValue("borderColor", font.borderColor);
                    json.writeValue("borderStraight", font.borderStraight);
                    json.writeValue("borderGamma", font.borderGamma);
                    json.writeValue("shadowOffsetX", font.shadowOffsetX);
                    json.writeValue("shadowOffsetY", font.shadowOffsetY);
                    if (font.shadowColor != null) json.writeValue("shadowColor", font.shadowColor);
                    json.writeValue("spaceX", font.spaceX);
                    json.writeValue("spaceY", font.spaceY);
                    json.writeValue("kerning", font.kerning);
                    json.writeValue("flip", font.flip);
                    json.writeValue("genMipMaps", font.genMipMaps);
                    json.writeValue("incremental", font.incremental);
                    json.writeValue("hinting", font.hinting);
                    json.writeValue("minFilter", font.minFilter);
                    json.writeValue("magFilter", font.magFilter);
                    json.writeValue("characters", font.characters.equals("") ? FreeTypeFontData.DEFAULT_CHARS : font.characters);
                    json.writeObjectEnd();
                }
            }
            json.writeObjectEnd();
        }
        
        var tintedDrawables = new Array<DrawableData>();
        var tiledDrawables = new Array<DrawableData>();
        var pixelDrawables = new Array<DrawableData>();
        var textureRegionDrawables = new Array<DrawableData>();
        var ninePatchDrawables = new Array<DrawableData>();
        var tenPatchDrawables = new Array<DrawableData>();
        var tvgDrawables = new Array<DrawableData>();
        for (DrawableData drawable : projectData.getAtlasData().getDrawables()) {
            switch (drawable.type) {
                case TILED:
                    tiledDrawables.add(drawable);
                    break;
                case TINTED:
                case TINTED_FROM_COLOR_DATA:
                    tintedDrawables.add(drawable);
                    break;
                case TENPATCH:
                    tenPatchDrawables.add(drawable);
                    break;
                case TVG:
                    tvgDrawables.add(drawable);
                    break;
                case PIXEL:
                    pixelDrawables.add(drawable);
                    break;
                case TEXTURE:
                    //only include if minSize has been modified
                    if (!MathUtils.isEqual(drawable.minWidth, -1f) || !MathUtils.isEqual(drawable.minHeight, -1f)) {
                        textureRegionDrawables.add(drawable);
                    }
                    break;
                case NINE_PATCH:
                    //only include if minSize has been modified
                    if (!MathUtils.isEqual(drawable.minWidth, -1f) || !MathUtils.isEqual(drawable.minHeight, -1f)) {
                        ninePatchDrawables.add(drawable);
                    }
                    break;
            }
        }
        
        //texture region drawables for minWidth and minHeight definition
        if (textureRegionDrawables.size > 0) {
            var className = TextureRegionDrawable.class.getName();
            json.writeObjectStart(className);
            for (var drawable : textureRegionDrawables) {
                json.writeObjectStart(drawable.file.nameWithoutExtension());
                json.writeValue("region", drawable.file.nameWithoutExtension());
                if (!MathUtils.isEqual(drawable.minWidth, -1)) json.writeValue("minWidth", drawable.minWidth);
                else json.writeValue("minWidth", Utils.imageDimensions(drawable.file).x);
                if (!MathUtils.isEqual(drawable.minHeight, -1)) json.writeValue("minHeight", drawable.minHeight);
                else json.writeValue("minHeight", Utils.imageDimensions(drawable.file).y);
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }
        
        //ninepatch drawables for minWidth and minHeight definition
        if (ninePatchDrawables.size > 0) {
            var className = NinePatchDrawable.class.getName();
            json.writeObjectStart(className);
            for (var drawable : ninePatchDrawables) {
                var name = drawable.name;
                json.writeObjectStart(name);
                json.writeValue("patch", drawable.file.name().replaceAll("\\.9.*$", ""));
                if (!MathUtils.isEqual(drawable.minWidth, -1)) json.writeValue("minWidth", drawable.minWidth);
                else json.writeValue("minWidth", Utils.imageDimensions(drawable.file).x);
                if (!MathUtils.isEqual(drawable.minHeight, -1)) json.writeValue("minHeight", drawable.minHeight);
                else json.writeValue("minHeight", Utils.imageDimensions(drawable.file).y);
                if (drawable.tintName != null) json.writeValue("color", drawable.tintName);
                if (drawable.tint != null) {
                    json.writeObjectStart("color");
                    if (projectData.isExportingHex()) {
                        json.writeValue("hex", drawable.tint.toString());
                    } else {
                        json.writeValue("r", drawable.tint.r);
                        json.writeValue("g", drawable.tint.g);
                        json.writeValue("b", drawable.tint.b);
                        json.writeValue("a", drawable.tint.a);
                    }
                    json.writeObjectEnd();
                }
                
                var atlasDrawable = atlasData.getDrawablePairs().get(drawable);
                json.writeValue("leftWidth", atlasDrawable.getLeftWidth());
                json.writeValue("rightWidth", atlasDrawable.getRightWidth());
                json.writeValue("topHeight", atlasDrawable.getTopHeight());
                json.writeValue("bottomHeight", atlasDrawable.getBottomHeight());
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }
        
        //tinted drawables
        if (tintedDrawables.size > 0 || pixelDrawables.size > 0) {
            String className = projectData.isUsingSimpleNames() ? TintedDrawable.class.getSimpleName() : TintedDrawable.class.getName();
            json.writeObjectStart(className);
            var drawables = new Array<>(tintedDrawables);
            drawables.addAll(pixelDrawables);
            for (DrawableData drawable : drawables) {
                json.writeObjectStart(drawable.name);
    
                json.writeValue("name", drawable.type == PIXEL ? "white-pixel" : DrawableData.proper(drawable.file.name()));
                if (drawable.type == TINTED) {
                    json.writeObjectStart("color");
                    if (projectData.isExportingHex()) {
                        json.writeValue("hex", drawable.tint.toString());
                    } else {
                        json.writeValue("r", drawable.tint.r);
                        json.writeValue("g", drawable.tint.g);
                        json.writeValue("b", drawable.tint.b);
                        json.writeValue("a", drawable.tint.a);
                    }
                    json.writeObjectEnd();
                } else if (drawable.type == TINTED_FROM_COLOR_DATA || drawable.type == PIXEL) {
                    json.writeValue("color", drawable.tintName);
                }
                
                if (drawable.minWidth >= 0) {
                    json.writeValue("minWidth", drawable.minWidth);
                }
                if (drawable.minHeight >= 0) {
                    json.writeValue("minHeight", drawable.minHeight);
                }
                
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }
        
        //tiled drawables
        if (tiledDrawables.size > 0) {
            String className = projectData.isUsingSimpleNames() ? TiledDrawable.class.getSimpleName() : TiledDrawable.class.getName();
            json.writeObjectStart(className);
            for (DrawableData drawable : tiledDrawables) {
                json.writeObjectStart(drawable.name);
                json.writeValue("region", DrawableData.proper(drawable.file.name()));
                json.writeValue("color", drawable.tintName);
                json.writeValue("minWidth", drawable.minWidth);
                json.writeValue("minHeight", drawable.minHeight);
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }
        
        //ten patch drawables
        if (tenPatchDrawables.size > 0) {
            String className = TenPatchDrawable.class.getName();
            json.writeObjectStart(className);
            for (DrawableData drawable : tenPatchDrawables) {
                json.writeObjectStart(drawable.name);
                json.writeValue("region", DrawableData.proper(drawable.file.name()));
                if (drawable.tenPatchData.colorName != null) {
                    json.writeValue("color", drawable.tenPatchData.colorName);
                }
                
                if (drawable.tenPatchData.horizontalStretchAreas.size > 0) {
                    json.writeValue("horizontalStretchAreas", drawable.tenPatchData.horizontalStretchAreas.toArray());
                } else {
                    var region = atlasData.getAtlas().findRegion(drawable.file.nameWithoutExtension());
                    json.writeValue("horizontalStretchAreas", new int[]{0, region.getRegionWidth() - 1});
                }
                
                if (drawable.tenPatchData.verticalStretchAreas.size > 0) {
                    json.writeValue("verticalStretchAreas", drawable.tenPatchData.verticalStretchAreas.toArray());
                } else {
                    var region = atlasData.getAtlas().findRegion(drawable.file.nameWithoutExtension());
                    json.writeValue("verticalStretchAreas", new int[]{0, region.getRegionHeight() - 1});
                }
                
                json.writeValue("tiling", drawable.tenPatchData.tile);
                if (!MathUtils.isEqual(drawable.minWidth, -1)) json.writeValue("minWidth", drawable.minWidth);
                else json.writeValue("minWidth", Utils.imageDimensions(drawable.file).x);
                if (!MathUtils.isEqual(drawable.minHeight, -1)) json.writeValue("minHeight", drawable.minHeight);
                else json.writeValue("minHeight", Utils.imageDimensions(drawable.file).y);
                json.writeValue("rightWidth", drawable.tenPatchData.contentRight);
                json.writeValue("leftWidth", drawable.tenPatchData.contentLeft);
                json.writeValue("bottomHeight", drawable.tenPatchData.contentBottom);
                json.writeValue("topHeight", drawable.tenPatchData.contentTop);
                if (drawable.tenPatchData.color1Name != null) {
                    json.writeValue("color1", drawable.tenPatchData.color1Name);
                }
                if (drawable.tenPatchData.color2Name != null) {
                    json.writeValue("color2", drawable.tenPatchData.color2Name);
                }
                if (drawable.tenPatchData.color3Name != null) {
                    json.writeValue("color3", drawable.tenPatchData.color3Name);
                }
                if (drawable.tenPatchData.color4Name != null) {
                    json.writeValue("color4", drawable.tenPatchData.color4Name);
                }
                json.writeValue("offsetX", drawable.tenPatchData.offsetX);
                json.writeValue("offsetY", drawable.tenPatchData.offsetY);
                json.writeValue("offsetXspeed", drawable.tenPatchData.offsetXspeed);
                json.writeValue("offsetYspeed", drawable.tenPatchData.offsetYspeed);
                json.writeValue("frameDuration", drawable.tenPatchData.frameDuration);
                json.writeValue("regions", drawable.tenPatchData.regionNames, Array.class, String.class);
                json.writeValue("playMode", drawable.tenPatchData.playMode);
                json.writeValue("crushMode", drawable.tenPatchData.crushMode);
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }
        
        //tvg drawables
        if (tvgDrawables.size > 0) {
            String className = TinyVGDrawable.class.getName();
            json.writeObjectStart(className);
            for (DrawableData drawable : tvgDrawables) {
                json.writeObjectStart(drawable.name);
                json.writeValue("file", drawable.file.name());
                json.writeValue("clipBasedOnTVGsize", drawable.tvgData.clipBasedOnTVGsize);
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }

        // dependency check for custom classes
        for (CustomClass customClass : customClasses) {
            if (!customClass.isDeclareAfterUIclasses()) {
                if (customClassHasFields(customClass)) {
                    for (CustomStyle customStyle : customClass.getStyles()) {
                        if (customStyleHasFields(customStyle)) {
                            for (CustomProperty customProperty : customStyle.getProperties()) {
                                //only write value if it is valid
                                if (customPropertyIsNotNull(customProperty)) {
                                    String fieldName = customProperty.getName();
                                    if (fieldName.equals("scrollPaneStyle")
                                            ||fieldName.equals("scrollStyle")
                                            ||fieldName.equals("listStyle") ) {
                                        customClass.setDeclareAfterUIclasses(true);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //custom classes declared before UI classes
        for (CustomClass customClass : customClasses) {
            if (!customClass.isDeclareAfterUIclasses()) {
                if (customClassHasFields(customClass)) {
                    json.writeObjectStart(customClass.getFullyQualifiedName());
                    for (CustomStyle customStyle : customClass.getStyles()) {
                        if (customStyleHasFields(customStyle)) {
                            json.writeObjectStart(customStyle.getName());

                            for (CustomProperty customProperty : customStyle.getProperties()) {
                                //only write value if it is valid
                                if (customPropertyIsNotNull(customProperty)) {
                                    if (customProperty.getType().equals(CustomProperty.PropertyType.RAW_TEXT)) {
                                        try {
                                            json.getWriter().json(customProperty.getName(), (String)customProperty.getValue());
                                        } catch (Exception e) {
                                            DialogFactory.showDialogErrorStatic("Error writing custom property.", "Error writing custom property " + customProperty.getName() + " for custom class " + customClass.getDisplayName() + ".");
                                        }
                                    } else {
                                        json.writeValue(customProperty.getName(), customProperty.getValue());
                                    }
                                }
                            }
                            json.writeObjectEnd();
                        } else {
                            warnings.add("Did not export custom style [BLACK]" + customStyle.getName() + "[] for class [BLACK]" + customClass.getDisplayName() + "[] (All fields null)");
                        }
                    }
                    json.writeObjectEnd();
                } else {
                    warnings.add("Did not export custom class [BLACK]" + customClass.getDisplayName() + "[] (No valid styles)");
                }
            }
        }

        //styles
        for (int i = 0; i < Main.STYLE_CLASSES.length; i++) {
            Class clazz = Main.STYLE_CLASSES[i];
            Array<StyleData> styles = classStyleMap.get(Main.styleToBasicClass(clazz));

            //check if any style has the mandatory fields necessary to write
            boolean hasMandatoryStyles = false;
            for (StyleData style : styles) {
                if (style.hasMandatoryFields() && ! style.hasAllNullFields()) {
                    hasMandatoryStyles = true;
                    break;
                }
            }

            if (hasMandatoryStyles) {
                String className = projectData.isUsingSimpleNames() ? clazz.getSimpleName() : clazz.getName();
                json.writeObjectStart(className);
                for (StyleData style : styles) {
                    if (style.hasMandatoryFields() && !style.hasAllNullFields()) {
                        json.writeObjectStart(style.name);
                        if (style.parent != null) {
                            json.writeValue("parent", style.parent);
                        }
                        for (StyleProperty property : style.properties.values()) {

                            //if not optional, null, or zero
                            if (!property.optional || property.value != null
                                    && !(property.value instanceof Number
                                    && MathUtils.isZero((float) (double) property.value))) {
                                if (property.value != null) {
                                    json.writeValue(property.name, property.value);
                                }
                            }
                        }
                        json.writeObjectEnd();
                    } else {
                        if (style.hasAllNullFields()) {
                            warnings.add("Did not export style [BLACK]" + style.name + "[] for class [BLACK]" + clazz.getSimpleName() + " (All fields null)");
                        } else if (!style.hasMandatoryFields()) {
                            warnings.add("Did not export style [BLACK]" + style.name + "[] for class [BLACK]" + clazz.getSimpleName() + " (All fields null)");
                        }
                    }
                }
                json.writeObjectEnd();
            } else {
                warnings.add("Did not export class [BLACK]" + clazz.getSimpleName() + "[] (No valid styles)");
            }
        }
        
        //custom classes declared after UI classes
        for (CustomClass customClass : customClasses) {
            if (customClass.isDeclareAfterUIclasses()) {
                if (customClassHasFields(customClass)) {
                    json.writeObjectStart(customClass.getFullyQualifiedName());
                    for (CustomStyle customStyle : customClass.getStyles()) {
                        if (customStyleHasFields(customStyle)) {
                            json.writeObjectStart(customStyle.getName());

                            for (CustomProperty customProperty : customStyle.getProperties()) {
                                //only write value if it is valid
                                if (customPropertyIsNotNull(customProperty)) {
                                    if (customProperty.getType().equals(CustomProperty.PropertyType.RAW_TEXT)) {
                                        try {
                                            json.getWriter().json(customProperty.getName(), (String)customProperty.getValue());
                                        } catch (Exception e) {
                                            DialogFactory.showDialogErrorStatic("Error writing custom property.", "Error writing custom property " + customProperty.getName() + " for custom class " + customClass.getDisplayName() + ".");
                                        }
                                    } else {
                                        json.writeValue(customProperty.getName(), customProperty.getValue());
                                    }
                                }
                            }
                            json.writeObjectEnd();
                        } else {
                            warnings.add("Did not export custom style [BLACK]" + customStyle.getName() + "[] for class [BLACK]" + customClass.getDisplayName() + "[] (All fields null)");
                        }
                    }
                    json.writeObjectEnd();
                } else {
                    warnings.add("Did not export custom class [BLACK]" + customClass.getDisplayName() + "[] (No valid styles)");
                }
            }
        }

        json.writeObjectEnd();
        fileHandle.writeString(json.prettyPrint(stringWriter.toString()), false, "UTF-8");
        
        return warnings;
    }
    
    private boolean customPropertyIsNotNull(CustomProperty customProperty) {
        boolean returnValue = false;
        if (customProperty.getValue() instanceof Float && customProperty.getType() == PropertyType.NUMBER
                || customProperty.getValue() instanceof Double && customProperty.getType() == PropertyType.NUMBER
                || customProperty.getValue() instanceof Boolean && customProperty.getType() == PropertyType.BOOL) {
            returnValue = true;
        } else if (customProperty.getValue() instanceof String && !((String) customProperty.getValue()).equals("")) {
            if (null != customProperty.getType()) switch (customProperty.getType()) {
                case TEXT:
                case RAW_TEXT:
                case STYLE:
                    returnValue = true;
                    break;
                case COLOR:
                    for (ColorData data : getColors()) {
                        if (data.getName().equals(customProperty.getValue())) {
                            returnValue = true;
                            break;
                        }
                    }   break;
                case DRAWABLE:
                    for (DrawableData data : atlasData.getDrawables()) {
                        if (data.name.equals(customProperty.getValue())) {
                            returnValue = true;
                            break;
                        }
                    }   break;
                case FONT:
                    for (FontData data : getFonts()) {
                        if (data.getName().equals(customProperty.getValue())) {
                            returnValue = true;
                            break;
                        }
                    }
                    
                    for (FreeTypeFontData data : freeTypeFonts) {
                        if (data.name.equals(customProperty.getValue())) {
                            returnValue = true;
                            break;
                        }
                    }    break;
            }
        }
        return returnValue;
    }
    
    private boolean customStyleHasFields(CustomStyle customStyle) {
        boolean returnValue = false;
        
        for (CustomProperty customProperty : customStyle.getProperties()) {
            if (customPropertyIsNotNull(customProperty)) {
                returnValue = true;
                break;
            }
        }
        
        return returnValue;
    }

    private boolean customClassHasFields(CustomClass customClass) {
        for (CustomStyle style : customClass.getStyles()) {
            if (customStyleHasFields(style)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean classHasValidStyles(Class clazz) {
        for (var style : classStyleMap.get(clazz)) {
            if (style.hasAllNullFields() || !style.hasMandatoryFields()) {
                return false;
            }
        }
        
        return true;
    }
    
    public Array<ColorData> getColors() {
        return colors;
    }
    
    public ColorData getColorByName(String tintName) {
        return getColorByName(tintName, null);
    }
    
    public ColorData getColorByName(String tintName, ColorData defaultValue) {
        ColorData returnValue = null;
    
        for (ColorData color : colors) {
            if (color.getName().equals(tintName)) {
                returnValue = color;
                break;
            }
        }
    
        return returnValue != null ? returnValue : defaultValue;
    }

    public Array<FontData> getFonts() {
        return fonts;
    }

    public Array<FreeTypeFontData> getFreeTypeFonts() {
        return freeTypeFonts;
    }

    public OrderedMap<Class, Array<StyleData>> getClassStyleMap() {
        return classStyleMap;
    }

    private void initializeClassStyleMap() {
        classStyleMap = new OrderedMap();
        for (Class clazz : Main.BASIC_CLASSES) {
            Array<StyleData> array = new Array<>();
            classStyleMap.put(clazz, array);
            if (clazz.equals(Slider.class) || clazz.equals(ProgressBar.class) || clazz.equals(SplitPane.class)) {
                StyleData data = new StyleData(clazz, "default-horizontal", main);
                data.jsonData = this;
                data.deletable = false;
                array.add(data);
                data = new StyleData(clazz, "default-vertical", main);
                data.jsonData = this;
                data.deletable = false;
                array.add(data);
            } else {
                StyleData data = new StyleData(clazz, "default", main);
                data.jsonData = this;
                data.deletable = false;
                array.add(data);
            }
        }
    }
    
    /**
     * Called when project is saved to an SCMP file.
     * @param json
     */
    @Override
    public void write(Json json) {
        json.writeValue("colors", colors);
        json.writeValue("fonts", fonts);
        json.writeValue("freeTypeFonts", freeTypeFonts);
        json.writeValue("classStyleMap", classStyleMap);
        json.writeValue("customClasses", customClasses, Array.class, CustomClass.class);
    }
    
    /**
     * Called when project is opened from an SCMP file.
     * @param json
     * @param jsonData
     */
    @Override
    public void read(Json json, JsonValue jsonData) {
        try {
            colors = json.readValue("colors", Array.class, jsonData);
            fonts = json.readValue("fonts", Array.class, jsonData);
            
            freeTypeFonts = json.readValue("freeTypeFonts", Array.class, new Array<FreeTypeFontData>(),jsonData);
            FileHandle previewFontsPath = Main.appFolder.child("preview fonts");
            var fontsList = previewFontsPath.list();
            
            for (var freeTypeFont : freeTypeFonts) {
                if (freeTypeFont.previewTTF != null) {
                    
                    boolean foundMatch = false;
                    for (var previewFile : fontsList) {
                        if (freeTypeFont.previewTTF.equals(previewFile.nameWithoutExtension())) {
                            foundMatch = true;
                            break;
                        }
                    }
                    
                    if (!foundMatch) {
                        freeTypeFont.previewTTF = previewFontsPath.list()[0].nameWithoutExtension();
                    }
                }
            }
            
            classStyleMap = new OrderedMap<>();
            for (JsonValue data : jsonData.get("classStyleMap").iterator()) {
                classStyleMap.put(ClassReflection.forName(data.name), json.readValue(Array.class, data));
            }
            
            for (Array<StyleData> styleDatas : classStyleMap.values()) {
                for (StyleData styleData : styleDatas) {
                    styleData.jsonData = this;
                }
            }
            
            customClasses = json.readValue("customClasses", Array.class, CustomClass.class, new Array<>(), jsonData);
        } catch (ReflectionException e) {
            Gdx.app.log(getClass().getName(), "Error parsing json data during file read", e);
            dialogFactory.showDialogError("Error while reading file...", "Error while attempting to read save file.\nPlease ensure that file is not corrupted.\n\nOpen error log?");
        }
    }
    
    /**
     * Moves font drawables to the appropriate list when reading from legacy save files.
     */
    public void translateFontDrawables(AtlasData atlasData) {
        
        for (var font : fonts) {
            if (font.file.exists()) {
                var bitmapFontData = new BitmapFontData(font.file, false);
                for (String path : bitmapFontData.imagePaths) {
                    FileHandle file = new FileHandle(path);

                    var drawable = atlasData.getDrawable(file.nameWithoutExtension());

                    if (drawable != null) {
                        atlasData.getDrawables().removeValue(drawable, false);
                        atlasData.getFontDrawables().add(drawable);
                        drawable.type = DrawableType.FONT;
                    }
                }
            }
        }
    }

    /**
     * Creates a new StyleData object if one with the same name currently does not exist. If it does exist
     * it is returned and the properties are wiped. ClassName and deletable flag is retained.
     * @param className
     * @param styleName
     * @return 
     */
    public StyleData newStyle(Class className, String styleName) {
        Array<StyleData> styles = getClassStyleMap().get(className);
        
        StyleData data = null;
        
        for (StyleData tempStyle : styles) {
            if (tempStyle.name.equals(styleName)) {
                data = tempStyle;
                data.resetProperties();
            }
        }
        
        if (data == null) {
            data = new StyleData(className, styleName, main);
            data.jsonData = this;
            styles.add(data);
        }
        
        return data;
    }
    
    public StyleData findStyle(Class clazz, String styleName) {
        var styles = getClassStyleMap().get(clazz);
        for (var style : styles) {
            if (style.name.equals(styleName)) {
                return style;
            }
        }
        return null;
    }
    
    public StyleData copyStyle(StyleData original, String styleName) {
        Array<StyleData> styles = getClassStyleMap().get(original.clazz);
        StyleData data = new StyleData(original, styleName, main);
        data.jsonData = this;
        styles.add(data);
        
        return data;
    }
    
    public void deleteStyle(StyleData styleData) {
        Array<StyleData> styles = getClassStyleMap().get(styleData.clazz);
        styles.removeValue(styleData, true);
        
        //reset any properties pointing to this style to the default style
        if (styleData.clazz.equals(Label.class)) {
            for (StyleData data : getClassStyleMap().get(TextTooltip.class)) {
                StyleProperty property = data.properties.get("label");
                if (property != null && property.value.equals(styleData.name)) {
                    property.value = "default";
                }
            }
        } else if (styleData.clazz.equals(List.class)) {
            for (StyleData data : getClassStyleMap().get(SelectBox.class)) {
                StyleProperty property = data.properties.get("listStyle");
                if (property != null && property.value.equals(styleData.name)) {
                    property.value = "default";
                }
            }
        } else if (styleData.clazz.equals(ScrollPane.class)) {
            for (StyleData data : getClassStyleMap().get(SelectBox.class)) {
                StyleProperty property = data.properties.get("scrollStyle");
                if (property != null && property.value.equals(styleData.name)) {
                    property.value = "default";
                }
            }
        }
    }

    public void set(JsonData jsonData) {
        colors.clear();
        colors.addAll(jsonData.colors);
        
        fonts.clear();
        fonts.addAll(jsonData.fonts);
        
        classStyleMap.clear();
        classStyleMap.putAll((ObjectMap<? extends Class, ? extends Array<StyleData>>) jsonData.classStyleMap);
        
        customClasses.clear();
        customClasses.addAll(jsonData.customClasses);
        
        for (FreeTypeFontData font : freeTypeFonts) {
            font.bitmapFont.dispose();
        }
        freeTypeFonts.clear();
        freeTypeFonts.addAll(jsonData.freeTypeFonts);
    }

    public Array<CustomClass> getCustomClasses() {
        return customClasses;
    }
}
