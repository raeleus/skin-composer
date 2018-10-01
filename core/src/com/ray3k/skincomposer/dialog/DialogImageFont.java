/*
 * The MIT License
 *
 * Copyright 2018 Raymond Buckley.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.CharArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.ray3k.skincomposer.FilesDroppedListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.StyleProperty;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.io.FilenameFilter;
import java.util.stream.Stream;

/**
 *
 * @author Raymond
 */
public class DialogImageFont extends Dialog {
    private Main main;
    private Skin skin;
    private Table root;
    private static final String NUMBERS = "0123456789";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA_NUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String ALL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~`!@#$%^&*()_-+=[{]}\\|;:'\",<.>/?•©¿¡áéíóúüñÑÁÉÍÓÚÜ";
    private static final String SAMPLE_TEXT = "abcdefghijklmnopqrstuvwxyz\nABCDEFGHIJKLMNOPQRSTUVWXYZ\n0123456789\n~`!@#$%^&*()_-+=[{]}\\|;:'\",<.>/?•©\n¿¡áéíóúüñÑÁÉÍÓÚÜ\n\nThe most merciful thing in the world, I think, is the inability of the human mind to correlate all its contents.\n\n\"That is not dead which can eternal lie, And with strange aeons even death may die.\"\n\nIn his house at R'lyeh dead Cthulhu waits dreaming.\n\nThe Thing cannot be described - there is no language for such abysms of shrieking and immemorial lunacy, such eldritch contradictions of all matter, force, and cosmic order. A mountain walked or stumbled.\n\nWe live on a placid island of ignorance in the midst of black seas of infinity, and it was not meant that we should voyage far.";
    private static final String KERNING_DEFAULTS = "A' AC AG AO AQ AT AU AV AW AY BA BE BL BP BR BU BV BW BY CA CO CR DA DD DE DI DL DM DN DO DP DR DU DV DW DY EC EO FA FC FG FO F. F, GE GO GR GU HO IC IG IO JA JO KO L' LC LT LV LW LY LG LO LU M MG MO NC NG NO OA OB OD OE OF OH OI OK OL OM ON OP OR OT OU OV OW OX OY PA PE PL PO PP PU PY P. P, P; P: QU RC RG RY RT RU RV RW RY SI SM ST SU TA TC TO UA UC UG UO US VA VC VG VO VS WA WC WG WO YA YC YO YS ZO Ac Ad Ae Ag Ao Ap Aq At Au Av Aw Ay Bb Bi Bk Bl Br Bu By B. B, Ca Cr C. C, Da D. D, Eu Ev Fa Fe Fi Fo Fr Ft Fu Fy F. F, F; F: Gu He Ho Hu Hy Ic Id Iq Io It Ja Je Jo Ju J. J, Ke Ko Ku Lu Ly Ma Mc Md Me Mo Nu Na Ne Ni No Nu N. N, Oa Ob Oh Ok Ol O. O, Pa Pe Po Rd Re Ro Rt Ru Si Sp Su S. S, Ta Tc Te Ti To Tr Ts Tu Tw Ty T. T, T; T: Ua Ug Um Un Up Us U. U, Va Ve Vi Vo Vr Vu V. V, V; V: Wd Wi Wm Wr Wt Wu Wy W. W, W; W: Xa Xe Xo Xu Xy Yd Ye Yi Yp Yu Yv Y. Y, Y; Y: ac ad ae ag ap af at au av aw ay ap bl br bu by b. b, ca ch ck da dc de dg do dt du dv dw dy d. d, ea ei el em en ep er et eu ev ew ey e. e, fa fe ff fi fl fo f. f, ga ge gh gl go gg g. g, hc hd he hg ho hp ht hu hv hw hy ic id ie ig io ip it iu iv ja je jo ju j. j, ka kc kd ke kg ko la lc ld le lf lg lo lp lq lu lv lw ly ma mc md me mg mn mo mp mt mu mv my nc nd ne ng no np nt nu nv nw ny ob of oh oj ok ol om on op or ou ov ow ox oy o. o, pa ph pi pl pp pu p. p, qu t. ra rd re rg rk rl rm rn ro rq rr rt rv ry r. r, sh st su s. s, td ta te to t. t, ua uc ud ue ug uo up uq ut uv uw uy va vb vc vd ve vg vo vv vy v. v, wa wx wd we wg wh wo w. w, xa xe xo y. y, ya yc yd ye yo 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61 62 63 64 65 66 67 68 69 70 71 72 73 74 75 76 77 78 79 80 81 82 83 84 85 86 87 88 89 90 91 92 93 94 95 96 97 98 99";
    private static final CharArray BASELINE_EXCLUSION = new CharArray(new char[] {'C', 'G', 'J', 'O', 'Q', 'U', '0', '3', '4', '5', '6', '7', '8', '9', 'c', 'o', 'g', 'j', 'p', 'q', 'y'});
    private Array<BitmapCharacter> bitmapCharacters;
    private BitmapFont previewFont;
    private TextFieldStyle previewStyle;
    private FilesDroppedListener filesDroppedListener;
    private Array<Actor> fadables;
    private static final int AUTO_GAP_LIMIT = 5;
    private Array<KerningPair> kerningPairValues;
    private ImageFontSettings settings;
    private Json json;
    private ImageFontListener imageFontListener;
    
    public DialogImageFont(Main main, ImageFontListener imageFontListener) {
        super("Create Font from Image", main.getSkin(), "bg");
        this.imageFontListener = imageFontListener;
        json = new Json(JsonWriter.OutputType.json);
        
        settings = new ImageFontSettings();
        
        kerningPairValues = new Array<>();
        
        this.main = main;
        skin = main.getSkin();
        
        filesDroppedListener = (Array<FileHandle> files) -> {
            if (files.size > 0 && files.first().extension().equalsIgnoreCase("png")) {
                Runnable runnable = () -> {
                    processSourceFile(files.first(), true);
                };
                
                main.getDialogFactory().showDialogLoading(runnable);
            }
        };
        
        main.getDesktopWorker().addFilesDroppedListener(filesDroppedListener);
        
        getTitleTable().getCells().first().padLeft(10.0f);
        
        root = getContentTable();
        root.pad(20.0f);
        
        refreshTable();
        
        getButtonTable().pad(25.0f).padTop(0.0f);
        getButtonTable().defaults().minWidth(125.0f);

        var textButton = new TextButton("Generate Font", skin);
        textButton.setName("generate");
        textButton.setDisabled(true);
        button(textButton, true);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Generate bitmap font, save to specified file, and add to list of fonts", main.getTooltipManager(), skin));
        
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ENTER) {
                    var textButton = ((TextButton) findActor("generate"));
                    if (!textButton.isDisabled()) {
                        textButton.fire(new ChangeListener.ChangeEvent());
                    }
                }
                return false;
            }
        });
        
        textButton = new TextButton("Save Settings", skin);
        getButtonTable().add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Save dialog settings to JSON", main.getTooltipManager(), skin));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                saveSettingsBrowse();
            }
        });
        
        textButton = new TextButton("Load Settings", skin);
        getButtonTable().add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Load dialog settings to JSON", main.getTooltipManager(), skin));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                loadSettingsBrowse();
            }
        });
        
        textButton = new TextButton("Reset", skin);
        getButtonTable().add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Reset all settings to defaults", main.getTooltipManager(), skin));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings = new ImageFontSettings();
                refreshTable();
            }
        });
        
        key(Keys.ESCAPE, false);
        textButton = new TextButton("Cancel", skin);
        button(textButton, false);
        textButton.addListener(main.getHandListener());
    }

    public static interface ImageFontListener {
        public void fontGenerated(FileHandle savePath);
    }
    
    @Override
    public boolean remove() {
        main.getDesktopWorker().removeFilesDroppedListener(filesDroppedListener);
        if (previewFont != null) {
            previewFont.dispose();
        }
        
        return super.remove();
    }

    @Override
    protected void result(Object object) {
        if ((Boolean) object) {
            var textField = (TextField) findActor("targetpath");
            writeFNT(Gdx.files.absolute(textField.getText()), true);
            if (imageFontListener != null) {
                imageFontListener.fontGenerated(Gdx.files.absolute(textField.getText()));
            }
        }
    }

    private void refreshTable() {
        previewStyle = new TextFieldStyle(skin.get(TextFieldStyle.class));
        
        fadables = new Array<Actor>();
        root.clearChildren();
        
        var scrollTable = new Table();
        scrollTable.defaults().space(10.0f);
        var scrollPane = new ScrollPane(scrollTable, skin);
        scrollPane.setName("scroll");
        scrollPane.setFlickScroll(false);
        scrollPane.setFadeScrollBars(false);
        root.add(scrollPane).growY().growX();
        
        var label = new Label("Settings", skin, "title-no-line");
        scrollTable.add(label);
        
        scrollTable.row();
        var content = new Table();
        content.defaults().space(10.0f).spaceBottom(15.0f);
        scrollTable.add(content).minWidth(500).growX();
        
        //characters and copy to clipboard
        label = new Label("Characters", skin);
        content.add(label).right().right();
        float width = label.getWidth();
        
        var table = new Table();
        table.defaults().space(10.0f);
        content.add(table).growX().colspan(3);
        
        var textField = new TextField(settings.characters, skin);
        textField.setName("characters");
        table.add(textField).growX();
        textField.addListener(new TextTooltip("Characters to be included in font", main.getTooltipManager(), skin));
        textField.addListener(main.getIbeamListener());
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return textField.getText().indexOf(c) == -1;
            }
        });
        
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.characters = ((TextField) actor).getText();
                ((SelectBox) findActor("characters select")).setSelected("custom");
            }
        });
        
        var selectBox = new SelectBox<String>(skin);
        selectBox.setItems("0-9", "a-zA-Z", "a-zA-Z0-9", "a-zA-Z0-9!-?*", "custom");
        if (settings.characters.equals(NUMBERS)) {
            selectBox.setSelectedIndex(0);
        } else if (settings.characters.equals(ALPHA)) {
            selectBox.setSelectedIndex(1);
        } else if (settings.characters.equals(ALPHA_NUMERIC)) {
            selectBox.setSelectedIndex(2);
        } else if (settings.characters.equals(ALL)) {
            selectBox.setSelectedIndex(3);
        } else {
            selectBox.setSelectedIndex(4);
        }
        selectBox.setName("characters select");
        table.add(selectBox);
        selectBox.addListener(new TextTooltip("Character Presets", main.getTooltipManager(), skin));
        selectBox.addListener(main.getHandListener());
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var textField = (TextField) findActor("characters");
                switch (selectBox.getSelectedIndex()) {
                    case 0:
                        textField.setText(NUMBERS);
                        break;
                    case 1:
                        textField.setText(ALPHA);
                        break;
                    case 2:
                        textField.setText(ALPHA_NUMERIC);
                        break;
                    case 3:
                        textField.setText(ALL);
                        break;
                }
                settings.characters = ((TextField) findActor("characters")).getText();
            }
        });
        
        var textButton = new TextButton("Copy", skin);
        table.add(textButton);
        textButton.addListener(new TextTooltip("Copy characters to clipboard", main.getTooltipManager(), skin));
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var text = settings.characters;
                var append = "    ";
                for (int i = text.length() - 1; i > 0; i--) {
                    text = text.substring(0, i) + append + text.substring(i);
                }
                
                Gdx.app.getClipboard().setContents(text);
            }
        });
        
        content.add().width(width);
        
        //source file
        content.row();
        label = new Label("Image File", skin);
        content.add(label).right();
        width = label.getWidth();
        
        table = new Table();
        table.defaults().space(10.0f);
        content.add(table).growX().colspan(3);
        
        textField = new TextField("", skin);
        textField.setName("imagepath");
        textField.setDisabled(true);
        table.add(textField).growX();
        textField.addListener(new TextTooltip("Path to source image", main.getTooltipManager(), skin));
        textField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sourceFileBrowse();
            }
        });
        textField.addListener(main.getHandListener());
        
        textButton = new TextButton("Browse", skin);
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                sourceFileBrowse();
            }
        });
        textButton.addListener(main.getHandListener());
        
        content.add().width(width);
        
        //target file
        content.row();
        label = new Label("Save File", skin);
        content.add(label).right();
        fadables.add(label);
        width = label.getWidth();
        
        table = new Table();
        table.defaults().space(10.0f);
        content.add(table).growX().colspan(3);
        fadables.add(table);
        
        textField = new TextField("", skin);
        textField.setName("targetpath");
        textField.setDisabled(true);
        table.add(textField).growX();
        textField.addListener(new TextTooltip("Path to save file", main.getTooltipManager(), skin));
        textField.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveFileBrowse();
            }
        });
        textField.addListener(main.getHandListener());
        
        textButton = new TextButton("Browse", skin);
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                saveFileBrowse();
            }
        });
        
        content.add().width(width);
        
        //gap size
        content.row();
        label = new Label("Gap Detection Size", skin);
        content.add(label).right();
        fadables.add(label);
        width = label.getWidth();
        
        var spinner = new Spinner(settings.gap, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("gap");
        spinner.setMinimum(0);
        content.add(spinner).left().minWidth(100.0f);
        fadables.add(spinner);
        spinner.addListener(new TextTooltip("Minimum distance between opaque pixels to split characters", main.getTooltipManager(), skin));
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.gap = ((Spinner) actor).getValueAsInt();
                
                var fileHandle = Gdx.files.absolute(((TextField) findActor("imagepath")).getText());
                try {
                    loadPixmap(fileHandle, false);
                    preview(true);
                } catch (InvalidFontImageException e) {
                    Gdx.app.error(getClass().getName(), "Error processing font source image: " + fileHandle, e);
                    main.getDialogFactory().showDialogError("Error creating font from image...", "Error creating font from image.\nPlease ensure that the image has 100% transparent pixels\nto break the different characters.\nOpen log?");
                    refreshTable();
                }
            }
        });
        
        textButton = new TextButton(settings.kerningPairsActivated ? "Kerning Pairs: ON" : "Kerning Pairs: OFF", skin);
        textButton.setName("kerning button");
        content.add(textButton).colspan(2).expandX().padLeft(10.0f).padRight(10.0f).minWidth(200.0f).left();
        fadables.add(textButton);
        textButton.addListener(new TextTooltip("Adjust auto kerning settings", main.getTooltipManager(), skin));
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                showKerningDialog();
            }
        });
        
        content.add().width(width);
        
        //kerning
        content.row();
        label = new Label("Kerning", skin);
        content.add(label).right();
        fadables.add(label);
        width = label.getWidth();
        
        spinner = new Spinner(settings.kerning, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("kerning");
        content.add(spinner).left().minWidth(100.0f);
        fadables.add(spinner);
        spinner.addListener(new TextTooltip("The horizontal spacing between characters", main.getTooltipManager(), skin));
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.kerning = ((Spinner) actor).getValueAsInt();
                
                preview(false);
            }
        });
        
        //leading
        label = new Label("Line Height", skin);
        content.add(label).right();
        fadables.add(label);
        
        spinner = new Spinner(settings.leading, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("leading");
        content.add(spinner).expandX().left().minWidth(100.0f);
        fadables.add(spinner);
        spinner.addListener(new TextTooltip("The vertical spacing between each line", main.getTooltipManager(), skin));
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.leading = ((Spinner) actor).getValueAsInt();
                
                preview(false);
            }
        });
        
        content.add().width(width);
        
        //baseline
        content.row();
        label = new Label("Baseline", skin);
        content.add(label).right();
        fadables.add(label);
        width = label.getWidth();
        
        spinner = new Spinner(settings.baseline, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("baseline");
        content.add(spinner).expandX().left().minWidth(100.0f).colspan(3);
        fadables.add(spinner);
        spinner.addListener(new TextTooltip("The distance to the line that the text rests on.", main.getTooltipManager(), skin));
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.baseline = ((Spinner) actor).getValueAsInt();
                
                preview(false);
            }
        });
        
        content.add().width(width);
        
        //space width
        content.row();
        label = new Label("Space Width", skin);
        content.add(label).right();
        fadables.add(label);
        width = label.getWidth();
        
        spinner = new Spinner(settings.spaceWidth, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("space width");
        spinner.setMinimum(0);
        content.add(spinner).left().minWidth(100.0f);
        fadables.add(spinner);
        spinner.addListener(new TextTooltip("Set the width of a single space", main.getTooltipManager(), skin));
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.spaceWidth = ((Spinner) actor).getValueAsInt();
                
                preview(false);
            }
        });
        
        //tab space count
        label = new Label("Tab Space Count", skin);
        content.add(label).right();
        fadables.add(label);
        
        spinner = new Spinner(settings.tabSpace, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        spinner.setName("tab space count");
        spinner.setMinimum(0);
        content.add(spinner).expandX().left().minWidth(100.0f);
        fadables.add(spinner);
        spinner.addListener(new TextTooltip("The number of space characters to make a tab", main.getTooltipManager(), skin));
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.tabSpace = ((Spinner) actor).getValueAsInt();
                
                preview(false);
            }
        });
        
        content.add().width(width);
        
        for (var fadable : fadables) {
            fadable.setColor(1.0f, 1.0f, 1.0f, .25f);
            fadable.setTouchable(Touchable.disabled);
        }
        
        //preview text
        scrollTable.row();
        var image = new Image(skin, "pressed");
        scrollTable.add(image).growX();
        
        scrollTable.row();
        label = new Label("Preview", skin, "title-no-line");
        scrollTable.add(label);
        
        scrollTable.row();
        var textArea = new TextArea(settings.preview, previewStyle);
        textArea.setName("preview");
        scrollTable.add(textArea).grow().minHeight(100.0f);
        textArea.addListener(main.getIbeamListener());
        textArea.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.preview = ((TextArea) actor).getText();
            }
        });
        
        scrollTable.row();
        table = new Table();
        table.defaults().space(10.0f);
        scrollTable.add(table).growX();
        
        textButton = new TextButton("View Characters", skin);
        textButton.setName("view characters");
        textButton.setDisabled(true);
        table.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Main.appFolder.child("imagefont/characters/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening characters folder", e);
                    main.getDialogFactory().showDialogError("Folder Error...", "Error opening characters folder.\n\nOpen log?");
                }
            }
        });
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Reset Text", skin);
        table.add(textButton).expandX().right();
        textButton.addListener(new TextTooltip("Reset preview text to default", main.getTooltipManager(), skin));
        textButton.addListener(main.getHandListener());
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                var textArea = (TextArea) findActor("preview");
                textArea.setText(SAMPLE_TEXT);
            }
        });
        
        var imageButton = new ImageButton(skin, "color");
        table.add(imageButton);
        imageButton.addListener(new TextTooltip("Change preview color", main.getTooltipManager(), skin));
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        var textArea = (TextArea) findActor("preview");
                        previewStyle.fontColor = colorData.color;
                        settings.previewColor = colorData.color;
                    }
                });
            }
        });
        
        imageButton = new ImageButton(skin, "color-bg");
        table.add(imageButton);
        imageButton.addListener(new TextTooltip("Change background color", main.getTooltipManager(), skin));
        imageButton.addListener(main.getHandListener());
        imageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                main.getDialogFactory().showDialogColors(new StyleProperty(), (ColorData colorData) -> {
                    if (colorData != null) {
                        var textArea = (TextArea) findActor("preview");
                        previewStyle.background = ((NinePatchDrawable) previewStyle.background).tint(colorData.color);
                        settings.previewBackgroundColor = colorData.color;
                    }
                });
            }
        });
        
    }
 
    private void sourceFileBrowse() {
        Runnable runnable = () -> {
              var textField = (TextField) findActor("imagepath");

            String defaultPath = main.getProjectData().getLastFontPath();
            FileHandle currentPath = Gdx.files.absolute(textField.getText());
            if (currentPath.exists()) {
                defaultPath = textField.getText();
            }

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[]{"*.png;*.jpg"};
            }

            File file = main.getDesktopWorker().openDialog("Select image file...", defaultPath, filterPatterns, "Image files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                processSourceFile(fileHandle, true);
            }
        };

        main.getDialogFactory().showDialogLoading(runnable);
    }
    
    private void processSourceFile(FileHandle fileHandle, boolean setDefaults) {
        try {
            loadPixmap(fileHandle, setDefaults);
            preview(true);
            main.getProjectData().setLastFontPath(fileHandle.parent().path() + "/");
            
            var textField = (TextField) findActor("imagepath");
            textField.setText(fileHandle.path());
            textField.setCursorPosition(textField.getText().length() - 1);
            
            textField = (TextField) findActor("targetpath");
            textField.setText(fileHandle.parent() + "/" + fileHandle.nameWithoutExtension() + " export.fnt");
            textField.setCursorPosition(textField.getText().length() - 1);
            
            ((TextButton) findActor("generate")).setDisabled(false);
            
            for (var fadable : fadables) {
                fadable.addAction(Actions.fadeIn(1.0f, Interpolation.fade));
                fadable.setTouchable(Touchable.enabled);
            }
            ((TextButton) findActor("view characters")).setDisabled(false);
        } catch (InvalidFontImageException e) {
            Gdx.app.error(getClass().getName(), "Error processing font source image: " + fileHandle, e);
            main.getDialogFactory().showDialogError("Error creating font from image...", "Error creating font from image.\nPlease ensure that the image has 100% transparent pixels\nto break the different characters.\nOpen log?");
            refreshTable();
        }
    }
    
    private void saveFileBrowse() {
        Runnable runnable = () -> {
            var textField = (TextField) findActor("targetpath");

            String defaultPath = main.getProjectData().getLastFontPath();
            FileHandle currentPath = Gdx.files.absolute(textField.getText());
            if (currentPath.exists()) {
                defaultPath = textField.getText();
            }

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[]{"*.fnt"};
            }

            File file = main.getDesktopWorker().saveDialog("Save as font file...", defaultPath, filterPatterns, "Font files");
            if (file != null) {
                var fileHandle = new FileHandle(file);
                if (!fileHandle.extension().equalsIgnoreCase("fnt")) {
                    fileHandle = fileHandle.parent().child(fileHandle.name() + ".fnt");
                }
                
                processSaveFile(fileHandle.path());
                
                main.getProjectData().setLastFontPath(fileHandle.parent().path() + "/");
            }
        };

        main.getDialogFactory().showDialogLoading(runnable);
    }
    
    private void processSaveFile(String path) {
        var textField = (TextField) findActor("targetpath");
        
        textField.setText(path);
        textField.setCursorPosition(textField.getText().length() - 1);

        ((TextButton) findActor("generate")).setDisabled(false);
    }
    
    private void saveSettingsBrowse() {
        Runnable runnable = () -> {
            String defaultPath = main.getProjectData().getLastFontPath();

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[]{"*.imagefont"};
            }

            File file = main.getDesktopWorker().saveDialog("Save Image Font Settings...", defaultPath, filterPatterns, "Imagefont files");
            if (file != null) {
                var fileHandle = new FileHandle(file);
                if (!fileHandle.extension().equalsIgnoreCase("imagefont")) {
                    fileHandle = fileHandle.parent().child(fileHandle.name() + ".imagefont");
                }
                
                saveSettings(fileHandle);
                
                main.getProjectData().setLastFontPath(fileHandle.parent().path() + "/");
            }
        };

        main.getDialogFactory().showDialogLoading(runnable);
    }
    
    private static class ImageFontSettings {
        String characters;
        int gap;
        int kerning;
        int baseline;
        int spaceWidth;
        int leading;
        int tabSpace;
        String kerningPairs;
        boolean kerningPairsActivated;
        int kerningPairsOffset;
        String preview;
        Color previewColor;
        Color previewBackgroundColor;
        
        public ImageFontSettings() {
            characters = ALL;
            gap = 3;
            kerningPairsActivated = false;
            kerningPairs = KERNING_DEFAULTS;
            kerningPairsOffset = 1;
            kerning = 1;
            leading = 0;
            baseline = 0;
            spaceWidth = 4;
            tabSpace = 8;
            preview = SAMPLE_TEXT;
            previewColor = Color.WHITE;
            previewBackgroundColor = Color.WHITE;
        }
    }
    
    private void saveSettings(FileHandle file) {
        file.writeString(json.prettyPrint(settings), false);
    }
    
    private void loadSettingsBrowse() {
        Runnable runnable = () -> {
            String defaultPath = main.getProjectData().getLastFontPath();

            String[] filterPatterns = null;
            if (!Utils.isMac()) {
                filterPatterns = new String[]{"*.imagefont"};
            }

            File file = main.getDesktopWorker().openDialog("Load Image Font Settings...", defaultPath, filterPatterns, "Imagefont files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                loadSettings(fileHandle);
            }
        };

        main.getDialogFactory().showDialogLoading(runnable);
    }
    
    private void loadSettings(FileHandle file) {
        var imagePath = ((TextField) findActor("imagepath")).getText();
        var targetPath = ((TextField) findActor("targetpath")).getText();
        
        json.setOutputType(JsonWriter.OutputType.json);
        settings = json.fromJson(ImageFontSettings.class, file);
        refreshTable();
        previewStyle.fontColor = settings.previewColor;
        previewStyle.background = ((NinePatchDrawable) previewStyle.background).tint(settings.previewBackgroundColor);
        
        if (targetPath != null && !targetPath.equals("")) {
            processSaveFile(targetPath);
        }
        
        if (imagePath != null && !imagePath.equals("")) {
            processSourceFile(Gdx.files.absolute(imagePath), false);
        }
    }
    
    private void showKerningDialog() {
        Dialog dialog = new Dialog("Auto Kerning Pairs", skin) {
            @Override
            protected void result(Object object) {
                var textButton = (TextButton) DialogImageFont.this.findActor("kerning button");
                var fileHandle = Gdx.files.absolute(((TextField) DialogImageFont.this.findActor("imagepath")).getText());
                if ((Boolean) object) {
                    textButton.setText("Kerning Pairs: ON");
                    settings.kerningPairsActivated = true;
                    
                    try {
                        loadPixmap(fileHandle, false);
                        preview(true);
                    } catch (InvalidFontImageException e) {
                        Gdx.app.error(getClass().getName(), "Error processing font source image: " + fileHandle, e);
                        main.getDialogFactory().showDialogError("Error creating font from image...", "Error creating font from image.\nPlease ensure that the image has 100% transparent pixels\nto break the different characters.\nOpen log?");
                        refreshTable();
                    }
                } else {
                    textButton.setText("Kerning Pairs: OFF");
                    settings.kerningPairsActivated = false;
                    
                    try {
                        loadPixmap(fileHandle, false);
                        preview(true);
                    } catch (InvalidFontImageException e) {
                        Gdx.app.error(getClass().getName(), "Error processing font source image: " + fileHandle, e);
                        main.getDialogFactory().showDialogError("Error creating font from image...", "Error creating font from image.\nPlease ensure that the image has 100% transparent pixels\nto break the different characters.\nOpen log?");
                        refreshTable();
                    }
                }
            }
        };
        
        dialog.getTitleTable().padLeft(10.0f);
        
        var table = dialog.getContentTable();
        table.pad(10.0f);
        
        var label = new Label("Kerning Pairs", skin);
        table.add(label).right();
        
        var textField = new TextField(settings.kerningPairs, skin);
        textField.setName("pairs");
        table.add(textField).growX();
        textField.addListener(main.getIbeamListener());
        textField.addListener(new TextTooltip("Space separated list of kerning pairs to optimize spacing", main.getTooltipManager(), skin));
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.kerningPairs = textField.getText();
            }
        });
        
        var textButton = new TextButton("Reset", skin);
        table.add(textButton);
        textButton.addListener(main.getHandListener());
        textButton.addListener(new TextTooltip("Reset kerning pairs to defaults", main.getTooltipManager(), skin));
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                ((TextField) dialog.findActor("pairs")).setText(KERNING_DEFAULTS);
                settings.kerningPairs = KERNING_DEFAULTS;
            }
        });
        
        table.row();
        label = new Label("Offset", skin);
        table.add(label).right();
        
        var spinner = new Spinner(settings.kerningPairsOffset, 1.0, true, Spinner.Orientation.HORIZONTAL, skin);
        table.add(spinner).left();
        spinner.getButtonMinus().addListener(main.getHandListener());
        spinner.getTextField().addListener(main.getIbeamListener());
        spinner.getButtonPlus().addListener(main.getHandListener());
        spinner.addListener(new TextTooltip("Offset to add to each kerning pair", main.getTooltipManager(), skin));
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                settings.kerningPairsOffset = spinner.getValueAsInt();
            }
        });
        
        dialog.key(Keys.ESCAPE, false).key(Keys.ENTER, true);
        
        textButton = new TextButton("Turn ON Kern Pairs", skin);
        dialog.button(textButton, true);
        textButton.addListener(main.getHandListener());
        
        textButton = new TextButton("Turn OFF Kern Pairs", skin);
        dialog.button(textButton, false);
        textButton.addListener(main.getHandListener());
        
        dialog.getButtonTable().pad(10.0f);
        
        dialog.show(getStage());
        getStage().setKeyboardFocus(((TextField) dialog.findActor("pairs")));
    }
    
    private class InvalidFontImageException extends Exception {
        
    }
    
    private void loadPixmap(FileHandle fileHandle, boolean setDefaults) throws InvalidFontImageException {
        var fontPixmap = new Pixmap(fileHandle);
        var tempColor = new Color();
        
        bitmapCharacters = new Array<>();
        var yBreaks = new IntArray();
        var characterList = settings.characters;
        
        var gapSize = settings.gap;
        var findGapSize = false;
        if (setDefaults) {
            gapSize = 0;
            findGapSize = true;
        }
        var averageWidth = 0;
        
        boolean failure;
        do {
            failure = false;
            bitmapCharacters.clear();
            yBreaks.clear();
            var lookingForBreak = false;
            
            //find vertical breaks for separate rows
            for (int y = 0; y < fontPixmap.getHeight(); y++) {
                var foundLine = false;
                for (int x = 0; x < fontPixmap.getWidth(); x++) {
                    tempColor.set(fontPixmap.getPixel(x, y));

                    if (tempColor.a > 0) {
                        foundLine = true;
                        break;
                    }
                }

                if (!lookingForBreak) {
                    if (foundLine) {
                        yBreaks.add(y);
                        lookingForBreak = true;
                    }
                } else {
                    if (!foundLine) {
                        yBreaks.add(y);
                        lookingForBreak = false;
                    }
                }
            }
            
            if (yBreaks.size < 2) {
                throw new InvalidFontImageException();
            }
            
            for (int i = 1; i + 1 < yBreaks.size; i += 2) {
                if (yBreaks.get(i + 1) - yBreaks.get(i) < gapSize) {
                    yBreaks.removeIndex(i + 1);
                    yBreaks.removeIndex(i);
                    i -= 2;
                }
            }

            int nameCounter = 0;
            
            //find characters for each row
            for (int i = 0; i < yBreaks.size && !failure; i += 2) {
                BitmapCharacter bitmapCharacter = null;
                lookingForBreak = false;

                var gapCounter = 0;

                for (int x = 0; x < fontPixmap.getWidth(); x++) {
                    var foundCharacter = false;
                    for (int y = yBreaks.get(i); y < yBreaks.get(i + 1); y++) {
                        tempColor.set(fontPixmap.getPixel(x, y));

                        if (tempColor.a > 0) {
                            foundCharacter = true;
                            break;
                        }
                    }

                    if (!lookingForBreak) {
                        if (foundCharacter) {
                            if (nameCounter >= characterList.length()) {
                                failure = true;
                                break;
                            }
                            bitmapCharacter = new BitmapCharacter();
                            bitmapCharacter.character = characterList.charAt(nameCounter);
                            bitmapCharacter.name = Integer.toString(nameCounter) + " " + bitmapCharacter.character + " " + (int) bitmapCharacter.character;
                            bitmapCharacter.name = sanitizeFileName(bitmapCharacter.name);
                            bitmapCharacter.x = x;
                            bitmapCharacter.y = yBreaks.get(i);
                            bitmapCharacter.height = yBreaks.get(i + 1) - bitmapCharacter.y;
                            lookingForBreak = true;
                        }
                    } else {
                        if (!foundCharacter) {
                            gapCounter++;
                            if (gapCounter == 1) {
                                bitmapCharacter.width = x - bitmapCharacter.x;
                            }

                            if (gapCounter > gapSize) {
                                lookingForBreak = false;
                                averageWidth += bitmapCharacter.width;
                                bitmapCharacters.add(bitmapCharacter);
                                nameCounter++;
                                gapCounter = 0;
                            }
                        } else {
                            gapCounter = 0;
                        }
                    }
                }

                if (!failure && lookingForBreak) {
                    lookingForBreak = false;
                    bitmapCharacter.width = fontPixmap.getWidth() - 1 - bitmapCharacter.x;
                    averageWidth += bitmapCharacter.width;
                    bitmapCharacters.add(bitmapCharacter);
                    nameCounter++;
                }
            }
            
            if (bitmapCharacters.size == 0) {
                failure = true;
            }
        } while (findGapSize && failure && ++gapSize <= AUTO_GAP_LIMIT);
        
        if (bitmapCharacters.size > 0) {
            averageWidth /= bitmapCharacters.size;
        } else {
            averageWidth = 1;
        }
        
        //find crop y and crop height
        for (var character : bitmapCharacters) {
            for (int y = character.y; y < character.y + character.height; y++) {
                var lineEmpty = true;
                for (int x = character.x; x < character.x + character.width; x++) {
                    tempColor.set(fontPixmap.getPixel(x, y));
                    if (tempColor.a > 0) {
                        lineEmpty = false;
                        break;
                    }
                }
                if (!lineEmpty) {
                    character.cropY = y - 1;
                    break;
                }
            }
        }
        
        for (var character : bitmapCharacters) {
            for (int y = character.cropY + character.height; y >= character.cropY; y--) {
                var lineEmpty = true;
                for (int x = character.x; x < character.x + character.width; x++) {
                    tempColor.set(fontPixmap.getPixel(x, y));
                    if (tempColor.a > 0) {
                        lineEmpty = false;
                        break;
                    }
                }
                if (!lineEmpty) {
                    character.cropHeight = y - character.cropY + 1;
                    character.yoffset = character.cropY - character.y;
                    character.baseline = y - character.y;
                    break;
                }
            }
        }
        
        //write characters to temporary PNGs
        Main.appFolder.child("imagefont/characters").emptyDirectory();
        for (var character : bitmapCharacters) {
            var pixmap = new Pixmap(character.width, character.cropHeight, Pixmap.Format.RGBA8888);
            pixmap.setBlending(Pixmap.Blending.None);
            pixmap.drawPixmap(fontPixmap, 0, 0, character.x, character.cropY, character.width, character.cropHeight);
            PixmapIO.writePNG(Main.appFolder.child("imagefont/characters/" + character.name + ".png"), pixmap);
            pixmap.dispose();
        }
        
        //calculate auto kerning pairs
        if (settings.kerningPairsActivated) {
            var pairs = settings.kerningPairs.trim().split(" ");
            pairs = Stream.of(pairs).filter((t) -> {
                return t.length() == 2;
            }).toArray(String[]::new);
            
            var charFolder = Main.appFolder.child("imagefont/characters");
            var pixmaps = new Array<Pixmap>();
            var testColor = new Color();
            kerningPairValues.clear();
            for (var pair : pairs) {
                pixmaps.clear();
                
                //find images for each character
                var fileHandles = charFolder.list((File dir, String name1) -> name1.matches(".* (" + (int) pair.charAt(0) + "|" + (int) pair.charAt(1) + ").png"));
                
                //create pixmaps
                if (fileHandles.length == 1 && pair.charAt(0) == pair.charAt(1) || fileHandles.length == 2) for (var imageFileHandle : fileHandles) {
                    var testPixmap = new Pixmap(imageFileHandle);
                    pixmaps.add(testPixmap);
                } else {
                    continue;
                }
                
                Pixmap compilationPixmap;
                if (fileHandles.length == 1) {
                    compilationPixmap = new Pixmap(pixmaps.get(0).getWidth() + pixmaps.get(0).getWidth(), pixmaps.get(0).getHeight(), Pixmap.Format.RGBA8888);
                } else {
                    compilationPixmap = new Pixmap(pixmaps.get(0).getWidth() + pixmaps.get(1).getWidth(), pixmaps.get(0).getHeight(), Pixmap.Format.RGBA8888);
                }
                
                //convert each pixmap to 50% transparency on every opaque character
                for (int i = 0; i < pixmaps.size; i++) {
                    var testPixmap = pixmaps.get(i);
                    var temp = new Pixmap(testPixmap.getWidth(), testPixmap.getHeight(), Pixmap.Format.RGBA8888);
                    
                    for (int y = 0; y < testPixmap.getHeight(); y++) {
                        for (int x = 0; x < testPixmap.getWidth(); x++) {
                            testColor.set(testPixmap.getPixel(x, y));
                            
                            if (testColor.a > 0) {
                                testColor.set(1, 1, 1, .5f);
                                temp.setColor(testColor);
                                temp.drawPixel(x, y);
                            }
                        }
                    }
                    
                    testPixmap.dispose();
                    pixmaps.set(i, temp);
                }
                
                var testX = pixmaps.get(0).getWidth();
                float alpha = 0.0f;
                while (testX >= 0.0f && alpha < .6f) {
                    //overlap pixmaps and test transparency, 100% means fail
                    compilationPixmap.setColor(Color.CLEAR);
                    compilationPixmap.fill();
                    
                    if (fileHandles.length == 1) {
                        compilationPixmap.drawPixmap(pixmaps.get(0), 0, 0);
                        compilationPixmap.drawPixmap(pixmaps.get(0), testX, 0);
                    } else {
                        compilationPixmap.drawPixmap(pixmaps.get(0), 0, 0);
                        compilationPixmap.drawPixmap(pixmaps.get(1), testX, 0);
                    }
                    
                    for (int y = 0; y < compilationPixmap.getHeight(); y++) {
                        for (int x = 0; x < compilationPixmap.getWidth(); x++) {
                            testColor.set(compilationPixmap.getPixel(x, y));

                            if (testColor.a > alpha) {
                                alpha = testColor.a;
                            }
                        }
                    }
                    
                    testX--;
                }
                
                testX -= pixmaps.get(0).getWidth() - settings.kerningPairsOffset;
                
                //reset
                for (var testPixmap : pixmaps) {
                    testPixmap.dispose();
                }
                compilationPixmap.dispose();
                
                kerningPairValues.add(new KerningPair(pair.charAt(0), pair.charAt(1), testX));
            }
        }
        
        //set defaults for fields
        if (setDefaults) {
            ((Spinner) findActor("gap")).setValue(gapSize);
            settings.gap = gapSize;
            
            int height = 0;
            for (var character : bitmapCharacters) {
                if (character.cropHeight > height) {
                    height = character.cropHeight;
                }
            }
            ((Spinner) findActor("leading")).setValue(height);
            settings.leading = height;

            var baseline = 0;
            var baselineCount = 0;

            for (var bitmapCharacter : bitmapCharacters) {
                if (!BASELINE_EXCLUSION.contains(bitmapCharacter.character) && Character.toString(bitmapCharacter.character).matches("[A-Z]+|\\d+")) {
                    baseline += bitmapCharacter.baseline;
                    baselineCount++;
                }
            }

            if (baselineCount == 0) {
                baseline = settings.leading;
            } else {
                baseline = baseline / baselineCount + 1;
            }
            ((Spinner) findActor("baseline")).setValue(baseline);
            settings.baseline = baseline;
            
            ((Spinner) findActor("space width")).setValue(MathUtils.round(averageWidth * .28f));
            settings.spaceWidth = MathUtils.round(averageWidth * .28f);
        }
        
        fontPixmap.dispose();
    }
    
    private static class BitmapCharacter {
        int x;
        int y;
        int width;
        int height;
        int cropY;
        int cropHeight;
        int yoffset;
        String name;
        char character;
        int baseline;

        @Override
        public String toString() {
            return name + ": x-" + x + " y-" + y + " width-" + width + " height-" + height;
        }
    }
    
    private static class KerningPair {
        char character1;
        char character2;
        int value;

        public KerningPair(char character1, char character2, int value) {
            this.character1 = character1;
            this.character2 = character2;
            this.value = value;
        }
    }

    private void writeFNT(FileHandle saveFile, boolean texturePack) {
        if (texturePack) {
            //delete existing PNG's
            var deleteFiles = saveFile.parent().list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches(saveFile.nameWithoutExtension() + "\\d*?\\.png|" + saveFile.nameWithoutExtension() + "\\.atlas|" + saveFile.nameWithoutExtension() + "\\.fnt");
                }
            });
            for (var file : deleteFiles) {
                file.delete();
            }
            saveFile.delete();


            //texturepack images
            main.getDesktopWorker().packFontImages(new Array<>(Main.appFolder.child("imagefont/characters").list()), saveFile);
            
            var atlas = new TextureAtlas(saveFile.sibling(saveFile.nameWithoutExtension() + ".atlas"));

            for (var bitmapCharacter : bitmapCharacters) {
                var number = (int) bitmapCharacter.character;

                for (  var testRegion : atlas.getRegions()) {
                    if (testRegion.name.matches(".* " + number + "$")) {
                        var region = testRegion;
                        bitmapCharacter.x = region.getRegionX();
                        bitmapCharacter.y = region.getRegionY();
                        bitmapCharacter.width = region.getRegionWidth();
                        bitmapCharacter.height = region.getRegionHeight();
                        break;
                    }
                }
            }
            
            atlas.dispose();
        }
        
        String imageFileName = saveFile.nameWithoutExtension() + ".png";
        var pixmap = new Pixmap(saveFile.sibling(imageFileName));
        var width = pixmap.getWidth();
        var height = pixmap.getHeight();
        pixmap.dispose();
        
        //add extra characters
        var characters = new Array<BitmapCharacter>(bitmapCharacters);
        var bitmapCharacter = new BitmapCharacter();
        bitmapCharacter.character = ' ';
        bitmapCharacter.width = settings.spaceWidth;
        characters.add(bitmapCharacter);
        
        bitmapCharacter = new BitmapCharacter();
        bitmapCharacter.character = '	';
        bitmapCharacter.width = settings.spaceWidth * settings.tabSpace;
        characters.add(bitmapCharacter);
        
        //write fnt file
        var fntText = "";
        fntText += "info face=\"" + saveFile.nameWithoutExtension() + "\" size=12 bold=0 italic=0 charset=\"\" unicode=0 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1\n";
        fntText += "common lineHeight=" + settings.leading + " base=" + settings.baseline + " scaleW=" + width + " scaleH=" + height + " pages=1 packed=0 alphaChnl=1 redChnl=0 greenChnl=0 blueChnl=0\n";
        fntText += "page id=0 file=\"" + imageFileName + "\"\n";

        fntText += "chars count=" + bitmapCharacters.size + "\n";
        for (var character : characters) {
            fntText += "char id=" + (int) character.character + " x=" + character.x + " y=" + character.y + " width=" + character.width + " height=" + character.height + " xoffset=0 yoffset=" + character.yoffset + " xadvance=" + (character.width + settings.kerning) + " page=0 chnl=0" + " letter=\"" + character.character + "\"\n";
        }
        
        if (!settings.kerningPairsActivated) {
            fntText += "\nkernings count=0\n";
        } else {
            fntText += "\nkernings count=" + kerningPairValues.size + "\n";
            
            for (var pair : kerningPairValues) {
                fntText += "kerning first=" + (int) pair.character1 + "  second=" + (int) pair.character2 + "  amount=" + pair.value + "\n";
            }
        }
        
        saveFile.writeString(fntText, false);

        if (texturePack) {
            //delete texture atlas
            saveFile.sibling(saveFile.nameWithoutExtension() + ".atlas").delete();
        }
    }
    
    private void preview(boolean texturePack) {
        var file = Main.appFolder.child("imagefont/preview/preview.fnt");
        
        try {
            if (texturePack) {
                file.parent().emptyDirectory();
            }
            writeFNT(file, texturePack);
            if (previewFont != null) {
                previewFont.dispose();
            }
            previewFont = new BitmapFont(file);
            ((TextArea) findActor("preview")).getStyle().font = previewFont;
            if (((TextArea) findActor("preview")).getStyle().fontColor.equals(skin.get(TextFieldStyle.class).fontColor)) {
                ((TextArea) findActor("preview")).getStyle().fontColor = new Color(Color.WHITE);
            }

            var oldTextArea = (TextArea) findActor("preview");
            var textArea = new TextArea(oldTextArea.getText(), previewStyle);
            textArea.setCursorPosition(oldTextArea.getCursorPosition());
            textArea.setName("preview");
            ((Table) oldTextArea.getParent()).getCell(oldTextArea).setActor(textArea);
            textArea.addListener(main.getIbeamListener());
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error generating preview...", e);
            main.getDialogFactory().showDialogError("Error generating preview...", "Error generating preview\nOpen log?");
        }
    }
    
    private String sanitizeFileName(String name) {
        name = name.replace("\\", "backslash");
        name = name.replace("/", "forwardslash");
        name = name.replace(":", "colon");
        name = name.replace("*", "asterisk");
        name = name.replace("?", "question");
        name = name.replace("\"", "double quote");
        name = name.replace("<", "less than");
        name = name.replace(">", "greater than");
        name = name.replace("|", "pipe");
        return name;
    }
}