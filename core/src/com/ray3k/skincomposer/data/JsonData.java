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
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin.TintedDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.CustomProperty.PropertyType;
import com.ray3k.skincomposer.dialog.DialogFactory;
import java.io.StringWriter;

public class JsonData implements Json.Serializable {
    private Array<ColorData> colors;
    private Array<FontData> fonts;
    private Array<FreeTypeFontData> freeTypeFonts;
    private OrderedMap<Class, Array<StyleData>> classStyleMap;
    private Array<CustomClass> customClasses;
    private Main main;

    public JsonData() {
        this.main = Main.main;
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
            font.bitmapFont.dispose();
        }
        freeTypeFonts.clear();
        initializeClassStyleMap();
        customClasses.clear();
    }

    /**
     * Imports skin data from a JSON file. Supports skins from LibGDX 1.9.9
     * @param fileHandle
     * @return
     * @throws Exception 
     */
    public Array<String> readFile(FileHandle fileHandle) throws Exception {
        Array<String> warnings = new Array<>();
        
        main.getProjectData().setChangesSaved(false);
        
        //read drawables from texture atlas file
        FileHandle atlasHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".atlas");
        if (atlasHandle.exists()) {
            main.getProjectData().getAtlasData().readAtlas(atlasHandle);
        } else {
            warnings.add("[RED]ERROR:[] Atlas file [BLACK]" + atlasHandle.name() + "[] does not exist.");
            return warnings;
        }

        //folder for critical files to be copied to
        FileHandle saveFile = main.getProjectData().getSaveFile();
        FileHandle targetDirectory;
        if (saveFile != null) {
            targetDirectory = saveFile.sibling(saveFile.nameWithoutExtension() + "_data");
        } else {
            targetDirectory = new FileHandle(Main.appFolder.child("temp/" + main.getProjectData().getId() + "_data").file());
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
                        FontData fontData = new FontData(font.name(), fontCopy);

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
                            main.getProjectData().getAtlasData().getDrawable(file.nameWithoutExtension()).visible = false;
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
                        data.useCustomSerializer= font.getBoolean("useCustomSerializer", false);
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
                        if (!fontCopy.parent().equals(fontFile.parent())) {
                            fontFile.copyTo(fontCopy);
                        }
                        data.file = fontCopy;
                        data.createBitmapFont(main);

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
                    ColorData colorData = new ColorData(color.name, new Color(color.getFloat("r", 0.0f), color.getFloat("g", 0.0f), color.getFloat("b", 0.0f), color.getFloat("a", 0.0f)));
                    
                    //delete colors with the same name
                    for (ColorData originalData : new Array<>(colors)) {
                        if (originalData.getName().equals(colorData.getName())) {
                            colors.removeValue(originalData, true);
                        }
                    }
                    
                    colors.add(colorData);
                }
            } //tiled drawables
            else if (child.name().equals(TiledDrawable.class.getName()) || child.name().equals(TiledDrawable.class.getSimpleName())) {
                for (JsonValue tiledDrawable : child.iterator()) {
                    DrawableData drawableData = new DrawableData(main.getProjectData().getAtlasData().getDrawable(tiledDrawable.getString("region")).file);
                    drawableData.name = tiledDrawable.name;
                    
                    drawableData.tiled = true;
                    drawableData.visible = true;
                    drawableData.tintName = tiledDrawable.getString("color");
                    drawableData.minWidth = tiledDrawable.getFloat("minWidth", 0.0f);
                    drawableData.minHeight = tiledDrawable.getFloat("minHeight", 0.0f);
   
                    //delete drawables with the same name
                    for (DrawableData originalData : new Array<>(main.getProjectData().getAtlasData().getDrawables())) {
                        if (originalData.name.equals(drawableData.name)) {
                            main.getProjectData().getAtlasData().getDrawables().removeValue(originalData, true);
                        }
                    }
                    
                    main.getProjectData().getAtlasData().getDrawables().add(drawableData);
                }
            } //tinted drawables
            else if (child.name().equals(TintedDrawable.class.getName()) || child.name().equals(TintedDrawable.class.getSimpleName())) {
                for (JsonValue tintedDrawable : child.iterator()) {
                    DrawableData drawableData = new DrawableData(main.getProjectData().getAtlasData().getDrawable(tintedDrawable.getString("name")).file);
                    drawableData.name = tintedDrawable.name;
                    
                    if (!tintedDrawable.get("color").isString()) {
                        drawableData.tint = new Color(tintedDrawable.get("color").getFloat("r", 0.0f), tintedDrawable.get("color").getFloat("g", 0.0f), tintedDrawable.get("color").getFloat("b", 0.0f), tintedDrawable.get("color").getFloat("a", 0.0f));
                    } else {
                        drawableData.tintName = tintedDrawable.getString("color");
                    }
                    
                    //delete drawables with the same name
                    for (DrawableData originalData : new Array<>(main.getProjectData().getAtlasData().getDrawables())) {
                        if (originalData.name.equals(drawableData.name)) {
                            main.getProjectData().getAtlasData().getDrawables().removeValue(originalData, true);
                        }
                    }
                    
                    main.getProjectData().getAtlasData().getDrawables().add(drawableData);
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
                                        warnings.add("Property [BLACK]" + styleProperty.name + "[] value cleared for [BLACK]" + clazz.getSimpleName() + ": " + data.name + "[] (Unsupported propety value)");
                                    }
                                }
                            }
                        }
                    }
                } else { //custom classes
                    CustomClass customClass = new CustomClass(child.name, child.name.replaceFirst(".*(\\.|\\$)", ""));
                    customClass.setMain(main);
                    
                    CustomClass existingClass = getCustomClass(customClass.getDisplayName());
                    if (existingClass != null) {
                        customClasses.removeValue(existingClass, true);
                    }
                    
                    customClasses.add(customClass);
                    for (JsonValue style : child.iterator()) {
                        CustomStyle customStyle = new CustomStyle(style.name);
                        customStyle.setParentClass(customClass);
                        customStyle.setMain(main);
                        
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
                            customProperty.setMain(main);
                            
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
                        } else if (property.type == Drawable.class) {
                            for (DrawableData drawable : main.getAtlasData().getDrawables()) {
                                if (property.value.equals(drawable.name)) {
                                    keep = true;
                                    break;
                                }
                            }
                            
                            if (!keep) {
                                keep = true;
                                DrawableData customDrawable = new DrawableData((String) property.value);
                                main.getAtlasData().getDrawables().add(customDrawable);
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
                                for (DrawableData drawable : main.getAtlasData().getDrawables()) {
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
     * Exports skin data to a JSON file to be loaded by LibGDX.
     * @param fileHandle
     * @return 
     */
    public Array<String> writeFile(FileHandle fileHandle) {
        Array<String> warnings = new Array<>();
        
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        jsonWriter.setOutputType(main.getProjectData().getExportFormat().getOutputType());
        
        Json json = new Json(main.getProjectData().getExportFormat().getOutputType());
        json.setWriter(jsonWriter);
        json.writeObjectStart();

        //fonts
        if (fonts.size > 0) {
            String className = main.getProjectData().isUsingSimpleNames() ? BitmapFont.class.getSimpleName() : BitmapFont.class.getName();
            json.writeObjectStart(className);
            for (FontData font : fonts) {
                json.writeObjectStart(font.getName());
                json.writeValue("file", font.file.name());
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }

        //colors
        if (colors.size > 0) {
            String className = main.getProjectData().isUsingSimpleNames() ? Color.class.getSimpleName() : Color.class.getName();
            json.writeObjectStart(className);
            for (ColorData color : colors) {
                json.writeObjectStart(color.getName());
                json.writeValue("r", color.color.r);
                json.writeValue("g", color.color.g);
                json.writeValue("b", color.color.b);
                json.writeValue("a", color.color.a);
                json.writeObjectEnd();
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
        
        Array<DrawableData> tintedDrawables = new Array<>();
        Array<DrawableData> tiledDrawables = new Array<>();
        for (DrawableData drawable : main.getProjectData().getAtlasData().getDrawables()) {
            if (drawable.tiled) {
                tiledDrawables.add(drawable);
            } else if (drawable.tint != null || drawable.tintName != null) {
                tintedDrawables.add(drawable);
            }
        }
        
        //tinted drawables
        if (tintedDrawables.size > 0) {
            String className = main.getProjectData().isUsingSimpleNames() ? TintedDrawable.class.getSimpleName() : TintedDrawable.class.getName();
            json.writeObjectStart(className);
            for (DrawableData drawable : tintedDrawables) {
                json.writeObjectStart(drawable.name);
                json.writeValue("name", DrawableData.proper(drawable.file.name()));
                if (drawable.tint != null) {
                    json.writeObjectStart("color");
                    json.writeValue("r", drawable.tint.r);
                    json.writeValue("g", drawable.tint.g);
                    json.writeValue("b", drawable.tint.b);
                    json.writeValue("a", drawable.tint.a);
                    json.writeObjectEnd();
                } else if (drawable.tintName != null) {
                    json.writeValue("color", drawable.tintName);
                }
                json.writeObjectEnd();
            }
            json.writeObjectEnd();
        }
        
        //tiled drawables
        if (tiledDrawables.size > 0) {
            String className = main.getProjectData().isUsingSimpleNames() ? TiledDrawable.class.getSimpleName() : TiledDrawable.class.getName();
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
        Array<Array<StyleData>> valuesArray = classStyleMap.values().toArray();
        for (int i = 0; i < Main.STYLE_CLASSES.length; i++) {
            Class clazz = Main.STYLE_CLASSES[i];
            Array<StyleData> styles = valuesArray.get(i);

            //check if any style has the mandatory fields necessary to write
            boolean hasMandatoryStyles = false;
            for (StyleData style : styles) {
                if (style.hasMandatoryFields() && ! style.hasAllNullFields()) {
                    hasMandatoryStyles = true;
                    break;
                }
            }

            if (hasMandatoryStyles) {
                String className = main.getProjectData().isUsingSimpleNames() ? clazz.getSimpleName() : clazz.getName();
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
                                json.writeValue(property.name, property.value);
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
                    for (DrawableData data : main.getAtlasData().getDrawables()) {
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
    
    public Array<ColorData> getColors() {
        return colors;
    }
    
    public ColorData getColorByName(String tintName) {
        ColorData returnValue = null;
        
        for (ColorData color : colors) {
            if (color.getName().equals(tintName)) {
                returnValue = color;
                break;
            }
        }
        
        return returnValue;
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

    @Override
    public void write(Json json) {
        json.writeValue("colors", colors);
        json.writeValue("fonts", fonts);
        json.writeValue("freeTypeFonts", freeTypeFonts);
        json.writeValue("classStyleMap", classStyleMap);
        json.writeValue("customClasses", customClasses, Array.class, CustomClass.class);
    }

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
            for (CustomClass customClass : customClasses) {
                customClass.setMain(main);
            }
        } catch (ReflectionException e) {
            Gdx.app.log(getClass().getName(), "Error parsing json data during file read", e);
            main.getDialogFactory().showDialogError("Error while reading file...", "Error while attempting to read save file.\nPlease ensure that file is not corrupted.\n\nOpen error log?");
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
        classStyleMap.putAll(jsonData.classStyleMap);
        
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
