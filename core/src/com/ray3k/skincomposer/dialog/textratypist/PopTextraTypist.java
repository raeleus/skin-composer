package com.ray3k.skincomposer.dialog.textratypist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Font.DistanceFieldType;
import com.github.tommyettinger.textra.Font.FontFamily;
import com.github.tommyettinger.textra.KnownFonts;
import com.github.tommyettinger.textra.TypingAdapter;
import com.github.tommyettinger.textra.TypingLabel;
import com.ray3k.skincomposer.SpineDrawable;
import com.ray3k.skincomposer.dialog.textratypist.PopTextraEffects.PopEffectsListener;
import com.ray3k.stripe.PopColorPicker;
import com.ray3k.stripe.PopColorPicker.PopColorPickerListener;
import com.ray3k.stripe.PopColorPicker.PopColorPickerStyle;
import com.ray3k.stripe.PopTable;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.utils.Utils.onChange;

public class PopTextraTypist extends PopTable {
    private TextArea codeTextArea;
    private TypingLabel previewTypingLabel;
    private ScrollPane previewScrollPane;
    private Table previewTable;
    private SelectBox<String> fontSelectBox;
    private Font masterFont;
    private Table contentTable;
    private enum FontMode {
        STANDARD, SKIN
    }
    private FontMode fontMode = FontMode.STANDARD;
    private SpineDrawable spine;
    public static PopColorPickerStyle ttColorPickerStyle;
    private TypingAdapter typingAdapter;
    private static String codeText = "";
    
    public PopTextraTypist() {
        super(new PopTableStyle());
    
        ttColorPickerStyle = createColorPickerStyle();
        
        masterFont = KnownFonts.getStandardFamily();
        
        typingAdapter = new TypingAdapter() {
            @Override
            public void event(String event) {
                Label label = new Label(event, skin, "tt-white");
                stage.addActor(label);
                label.setPosition(stage.getWidth() / 2, 0, Align.bottom);
                label.setColor(Color.RED);
                
                label.addAction(Actions.sequence(Actions.parallel(Actions.fadeOut(1f), Actions.moveBy(0f, 50f, 1f,
                        Interpolation.fastSlow)), Actions.removeActor()));
            }
        };
        
        setFillParent(true);
        setBackground(skin.getDrawable("tt-bg"));
        
        var root = this;
        root.top().left();
        
        var label = new Label("TextraTypist Playground", skin, "tt-title");
        root.add(label).left().pad(10).padBottom(0);
        
        root.row();
        var table = new Table();
        table.left();
        root.add(table).growX().spaceTop(15).padLeft(10).padRight(10);
        
        var buttonGroup = new ButtonGroup<TextButton>();
        table.defaults().space(20);
        var textButton = new TextButton("File", skin, "tt-file");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> contentTable.addAction(Actions.sequence(Actions.fadeOut(.25f), Actions.run(this::showFileTable), Actions.fadeIn(.25f))));
    
        textButton = new TextButton("Home", skin, "tt-file");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        textButton.setChecked(true);
        onChange(textButton, () -> contentTable.addAction(Actions.sequence(Actions.fadeOut(.25f), Actions.run(this::showHomeTable), Actions.fadeIn(.25f))));
        
        root.row();
        contentTable = new Table();
        root.add(contentTable).grow().spaceTop(10);
        showHomeTable();
    }
    
    private PopColorPickerStyle createColorPickerStyle() {
        var style = new PopColorPickerStyle();
        style.background = skin.getDrawable("tt-colorpicker-bg-10");
        style.stageBackground = skin.getDrawable("tt-stage-background");
        style.titleBarBackground = skin.getDrawable("tt-title-bar-10");
        style.labelStyle = skin.get("tt", LabelStyle.class);
        style.fileTextButtonStyle = skin.get("tt-file", TextButtonStyle.class);
        style.scrollPaneStyle = skin.get("tt", ScrollPaneStyle.class);
        style.colorSwatch = skin.getDrawable("tt-color-swatch");
        style.colorSwatchNew = skin.getDrawable("tt-color-swatch-new");
        style.colorSwatchPopBackground = skin.getDrawable("tt-panel-10");
        style.colorSwatchPopPreview = skin.getDrawable("tt-color-swatch-10");
        style.previewSwatchBackground = skin.getDrawable("tt-swatch");
        style.previewSwatchOld = skin.getDrawable("tt-swatch-old");
        style.previewSwatchNew = skin.getDrawable("tt-swatch-new");
        style.previewSwatchSingleBackground = skin.getDrawable("tt-swatch-null");
        style.previewSwatchSingle = skin.getDrawable("tt-swatch-new-null");
        style.textFieldStyle = skin.get("tt", TextFieldStyle.class);
        style.hexTextFieldStyle = skin.get("tt-hexfield", TextFieldStyle.class);
        style.textButtonStyle = skin.get("tt", TextButtonStyle.class);
        style.colorSliderBackground = skin.getDrawable("tt-slider-10");
        style.colorKnobCircleBackground = skin.getDrawable("tt-color-ball");
        style.colorKnobCircleForeground = skin.getDrawable("tt-color-ball-interior");
        style.colorSliderKnobHorizontal = skin.getDrawable("tt-slider-knob");
        style.colorSliderKnobVertical = skin.getDrawable("tt-slider-knob-vertical");
        style.radioButtonStyle = skin.get("tt-radio", ImageButtonStyle.class);
        style.increaseButtonStyle = skin.get("tt-increase", ImageButtonStyle.class);
        style.decreaseButtonStyle = skin.get("tt-decrease", ImageButtonStyle.class);
        style.checkerBackground = skin.getDrawable("tt-checker-10");
        return style;
    }
    
    private void showFileTable() {
        contentTable.clearChildren();
        contentTable.defaults().reset();
        
        var table = new Table();
        table.setBackground(skin.getDrawable("tt-file"));
        table.top();
        contentTable.add(table).growY();
        
        table.defaults().growX();
        var textButton = new TextButton("Quit to Skin Composer", skin, "tt-file-bar");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, this::hide);
    
        table.row();
        textButton = new TextButton("TextraTypist GitHub", skin, "tt-file-bar");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> Gdx.net.openURI("https://github.com/tommyettinger/textratypist#textratypist"));
    
        table.row();
        textButton = new TextButton("TypingLabel Documentation", skin, "tt-file-bar");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> Gdx.net.openURI("https://github.com/rafaskb/typing-label/wiki/Tokens"));
    
        table.row();
        textButton = new TextButton("TextraTypist Playground Wiki", skin, "tt-file-bar");
        textButton.getLabel().setAlignment(Align.left);
        table.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> Gdx.net.openURI("https://github.com/raeleus/skin-composer/wiki/TextraTypist-Playground"));
    
        table = new Table();
        contentTable.add(table).grow().padLeft(10).padRight(10);
        
        table.defaults().space(10);
        spine = new SpineDrawable(skeletonRenderer, textraTypistLogoSkeletonData, textraTypistLogoAnimationStateData);
        spine.getAnimationState().setAnimation(0, "animation", false);
        spine.getAnimationState().addAnimation(0, "loop", true, 0);
        var image = new Image(spine);
        table.add(image);
        
        table.row();
        var label = new TypingLabel(Gdx.files.internal("AboutTextraTypist").readString(), skin, "tt");
        label.setWrap(true);
        label.setAlignment(Align.topLeft);
        table.add(label).grow();
    }
    
    private void showHomeTable() {
        contentTable.clearChildren();
        contentTable.defaults().reset();
        
        var table = new Table();
        table.left();
        table.setBackground(skin.getDrawable("tt-ribbon-10"));
        contentTable.add(table).growX().padLeft(20).padRight(20);
    
        table.defaults().space(10);
        var imageButton = new ImageButton(skin, "tt-bold");
        table.add(imageButton).padLeft(10);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[*]"));
    
        imageButton = new ImageButton(skin, "tt-italics");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[/]"));
    
        imageButton = new ImageButton(skin, "tt-superscript");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[^]"));
    
        imageButton = new ImageButton(skin, "tt-subscript");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[.]"));
    
        imageButton = new ImageButton(skin, "tt-midscript");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[=]"));
    
        imageButton = new ImageButton(skin, "tt-underline");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[_]"));
    
        imageButton = new ImageButton(skin, "tt-strike");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[~]"));
    
        imageButton = new ImageButton(skin, "tt-caps");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[!]"));
    
        imageButton = new ImageButton(skin, "tt-lower");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[,]"));
    
        imageButton = new ImageButton(skin, "tt-each");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[;]"));
    
        imageButton = new ImageButton(skin, "tt-square");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> insertTag("[]", ""));
    
        imageButton = new ImageButton(skin, "tt-fx");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            var pop = PopTextraEffects.showPopEffects();
            pop.addListener(new PopEffectsListener() {
                @Override
                public void accepted(String tagBegin, String tagEnd) {
                    insertTag(tagBegin, tagEnd);
                }
            
                @Override
                public void cancelled() {
                
                }
            });
        });
    
        fontSelectBox = new SelectBox<>(skin, "tt");
        table.add(fontSelectBox);
        fontSelectBox.addListener(handListener);
        fontSelectBox.getList().addListener(handListener);
    
        onChange(fontSelectBox, () -> {
            if (fontSelectBox.getSelectedIndex() == 1) {
                insertTag("[@]", "");
                fontSelectBox.setSelectedIndex(0);
            } else if (fontSelectBox.getSelectedIndex() > 1) {
                insertTag("[@" + fontSelectBox.getSelected() + "]", "[@]");
                fontSelectBox.setSelectedIndex(0);
            }
        });
    
        var sizeSelectBox = new SelectBox<>(skin, "tt");
        table.add(sizeSelectBox);
        sizeSelectBox.addListener(handListener);
        sizeSelectBox.getList().addListener(handListener);
    
        var items = new Array<String>();
        items.add("Select a size...");
        items.add("Default");
        items.add("10");
        items.add("25");
        items.add("50");
        items.add("75");
        items.add("100");
        items.add("125");
        items.add("150");
        items.add("200");
        items.add("250");
        items.add("300");
        items.add("375");
        sizeSelectBox.setItems(items.toArray(String.class));
        onChange(sizeSelectBox, () -> {
            if (sizeSelectBox.getSelectedIndex() == 1) {
                insertTag("[%]", "");
                sizeSelectBox.setSelectedIndex(0);
            } if (sizeSelectBox.getSelectedIndex() > 1) {
                insertTag("[%" + sizeSelectBox.getSelected() + "]", "[%]");
                sizeSelectBox.setSelectedIndex(0);
            }
        });
    
        var colorSelectBox = new SelectBox<String>(skin, "tt");
        items = new Array<>();
        for (var color : Colors.getColors()) {
            items.add(color.key);
        }
        items.sort();
        items.insert(0, "Select a color...");
        items.insert(1, "More colors...");
        items.insert(2, "Default");
        colorSelectBox.setItems(items.toArray(String.class));
        table.add(colorSelectBox);
        colorSelectBox.addListener(handListener);
        colorSelectBox.getList().addListener(handListener);
        onChange(colorSelectBox, () -> {
            var selectedIndex = colorSelectBox.getSelectedIndex();
            if (selectedIndex == 1) {
                var pop = new PopColorPicker(null, ttColorPickerStyle);
                pop.show(stage);
                pop.addListener(new PopColorPickerListener() {
                    @Override
                    public void picked(Color color) {
                        insertTag("[#" + color.toString() + "]", "{CLEARCOLOR}");
                        stage.setKeyboardFocus(codeTextArea);
                    }
                
                    @Override
                    public void cancelled() {
                        stage.setKeyboardFocus(codeTextArea);
                    }
                });
                colorSelectBox.setSelectedIndex(0);
            } else if (selectedIndex == 2) {
                insertTag("{CLEARCOLOR}", "");
                colorSelectBox.setSelectedIndex(0);
            } else if (selectedIndex > 2) {
                insertTag("[" + colorSelectBox.getSelected() + "]", "{CLEARCOLOR}");
                colorSelectBox.setSelectedIndex(0);
            }
        });
    
        contentTable.defaults().padLeft(20).padRight(20);
    
        contentTable.row();
        var label = new Label("CODE", skin, "tt-subtitle");
        contentTable.add(label).left().spaceTop(15);
    
        contentTable.row();
        codeTextArea = new TextArea(codeText, skin, "tt-page");
        codeTextArea.setName("code");
        contentTable.add(codeTextArea).grow().uniformY();
        codeTextArea.addListener(ibeamListener);
        onChange(codeTextArea, () -> {
            codeText = codeTextArea.getText();
            previewTypingLabel.setText(codeText);
            previewTypingLabel.restart();
        });
    
        contentTable.row();
        imageButton = new ImageButton(skin, "tt-copy");
        contentTable.add(imageButton).right().spaceTop(5);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> Gdx.app.getClipboard().setContents(codeTextArea.getText()));
    
        contentTable.row();
        label = new Label("PREVIEW", skin, "tt-subtitle");
        contentTable.add(label).left();
    
        contentTable.row();
        previewTable = new Table();
        previewTable.setBackground(skin.getDrawable("tt-page-10"));
        previewTable.setColor(Color.BLACK);
        contentTable.add(previewTable).grow().uniformY();
    
        previewTypingLabel = new TypingLabel(codeText, masterFont);
        previewTypingLabel.setWrap(true);
        previewTypingLabel.setTypingListener(typingAdapter);
        
        previewScrollPane = new ScrollPane(previewTypingLabel, skin, "tt");
        previewTable.add(previewScrollPane).grow();
        previewScrollPane.addListener(scrollFocusListener);
    
        contentTable.row();
        table = new Table();
        contentTable.add(table).right().spaceTop(5).padBottom(20);
    
        table.defaults().space(5);
        imageButton = new ImageButton(skin, "tt-color");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            var picker = new PopColorPicker(previewTable.getColor(), ttColorPickerStyle);
            picker.addListener(new PopColorPickerListener() {
                @Override
                public void picked(Color color) {
                    previewTable.setColor(color);
                    stage.setKeyboardFocus(codeTextArea);
                }
            
                @Override
                public void cancelled() {
                    stage.setKeyboardFocus(codeTextArea);
                }
            });
            picker.show(stage);
        });
    
        imageButton = new ImageButton(skin, "tt-copy");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> Gdx.app.getClipboard().setContents(previewTypingLabel.toString()));
    
        if (fontMode == FontMode.STANDARD) activateStandardFontFamily();
        else if (fontMode == FontMode.SKIN) activateSkinFontFamily();
    }
    
    private void insertTag(String tag) {
        insertTag(tag, null);
    }
    
    private void insertTag(String tag, String endTag) {
        if (endTag == null) endTag = tag;
        var hasSelection = !codeTextArea.getSelection().equals("");
        var originalText = codeTextArea.getText();
        var selectionStart = hasSelection ? Math.min(codeTextArea.getCursorPosition(), codeTextArea.getSelectionStart()) : codeTextArea.getCursorPosition();
        var selectionEnd = Math.max(codeTextArea.getCursorPosition(), codeTextArea.getSelectionStart());
        if (hasSelection) {
            var insertion = selectionStart != selectionEnd ? tag + codeTextArea.getSelection() + endTag : tag;
            codeTextArea.setText(originalText.substring(0, selectionStart) + insertion + originalText.substring(selectionEnd));
            codeTextArea.setSelection(selectionStart, selectionStart + insertion.length());
        } else {
            codeTextArea.setText(originalText.substring(0, selectionStart) + tag + originalText.substring(selectionStart));
            codeTextArea.setCursorPosition(selectionStart + tag.length());
        }
        stage.setKeyboardFocus(codeTextArea);
        previewTypingLabel.setText(codeTextArea.getText());
        previewTypingLabel.restart();
    }
    
    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);
        TextArea textArea = findActor("code");
        stage.setKeyboardFocus(textArea);
    
        var style = new PopTableStyle();
        style.background = skin.getDrawable("tt-bg");
        style.stageBackground = skin.getDrawable("tt-stage-background");
        var pop = new PopTable(style);
        pop.setModal(true);
        pop.setKeepCenteredInWindow(true);
        pop.setKeepSizedWithinStage(true);
    
        pop.defaults().space(50).size(200);
        var textButton = new TextButton("Standard Font Family", skin, "tt-font");
        textButton.row();
        var label = new Label("Implement the standard font family packaged with TextraTypist", skin, "tt");
        label.setWrap(true);
        label.setAlignment(Align.center);
        textButton.getLabelCell().expand(false, false);
        textButton.add(label).growX().space(15);
        pop.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            activateStandardFontFamily();
            pop.hide();
        });
    
        textButton = new TextButton("Skin Font Family", skin, "tt-font");
        textButton.row();
        label = new Label("Create a font family from the fonts defined in the current skin from Skin Composer", skin, "tt");
        label.setWrap(true);
        label.setAlignment(Align.center);
        textButton.getLabelCell().expand(false, false);
        textButton.add(label).growX().space(15);
        if (jsonData.getFonts().size == 0 && jsonData.getFreeTypeFonts().size == 0) {
            textButton.setDisabled(true);
            label.setStyle(skin.get("tt-disabled", LabelStyle.class));
        }
        pop.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            activateSkinFontFamily();
            pop.hide();
        });
        
        pop.show(stage);
        pop.pad(50);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
        if (spine != null) spine.update(delta);
    }
    
    private void activateStandardFontFamily() {
        fontMode = FontMode.STANDARD;
        masterFont.dispose();
        var items = new Array<String>();
        items.add("Select a font...");
        items.add("Default");
        
        masterFont = KnownFonts.getStandardFamily();
        for (var font : KnownFonts.getAllStandard()) {
            items.add(font.name);
        }
        
        fontSelectBox.setItems(items);
        
        previewTypingLabel = new TypingLabel(previewTypingLabel.getOriginalText().toString(), masterFont);
        previewTypingLabel.setWrap(true);
        previewTypingLabel.setTypingListener(typingAdapter);
        previewTypingLabel.setAlignment(Align.topLeft);
        previewScrollPane.setActor(previewTypingLabel);
    }
    
    private void activateSkinFontFamily() {
        fontMode = FontMode.SKIN;
        masterFont.dispose();
        var items = new Array<String>();
        items.add("Select a font...");
        items.add("Default");
        
        var names = new Array<String>();
        var fonts = new Array<Font>();
        
        for (var fontData : jsonData.getFonts()) {
            var bitmapFont = new BitmapFont(fontData.file);
            var font = new Font(bitmapFont, DistanceFieldType.STANDARD, 0, 0, 0, 0, true);
            names.add(fontData.getName());
            fonts.add(font);
            items.add(fontData.getName());
        }
        
        for (var freetypeFontData : jsonData.getFreeTypeFonts()) {
            var bitmapFont = freetypeFontData.bitmapFont;
            var font = new Font(bitmapFont, DistanceFieldType.STANDARD, 0, 0, 0, 0, true);
            names.add(freetypeFontData.name);
            fonts.add(font);
            items.add(freetypeFontData.name);
        }
        
        fontSelectBox.setItems(items);
        if (names.size > 15) {
            names.removeRange(15, names.size - 1);
            fonts.removeRange(15, fonts.size - 1);
        }
        
        var namesArray = new String[names.size];
        for (int i = 0; i < names.size; i++) {
            namesArray[i] = names.get(i);
        }
    
        var fontsArray = new Font[fonts.size];
        for (int i = 0; i < fonts.size; i++) {
            fontsArray[i] = fonts.get(i);
        }
        
        var fontFamily = new FontFamily(namesArray, fontsArray);
        masterFont = fontFamily.connected[0].setFamily(fontFamily);
    
        previewTypingLabel = new TypingLabel(previewTypingLabel.getOriginalText().toString(), masterFont);
        previewTypingLabel.setWrap(true);
        previewTypingLabel.setAlignment(Align.topLeft);
        previewScrollPane.setActor(previewTypingLabel);
    }
    
    @Override
    public void hide() {
        stage.setKeyboardFocus(null);
        super.hide();
    }
}