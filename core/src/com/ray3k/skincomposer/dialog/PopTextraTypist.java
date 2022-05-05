package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Font.DistanceFieldType;
import com.github.tommyettinger.textra.Font.FontFamily;
import com.github.tommyettinger.textra.KnownFonts;
import com.github.tommyettinger.textra.TypingLabel;
import com.github.tommyettinger.textra.utils.ColorUtils;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.AspectRatioContainer;
import com.ray3k.stripe.PopTable;
import com.ray3k.tenpatch.TenPatchDrawable;

import static com.ray3k.skincomposer.Main.*;

public class PopTextraTypist extends PopTable {
    private TextArea codeTextArea;
    private TypingLabel previewTypingLabel;
    private ScrollPane previewScrollPane;
    private Table previewTable;
    private SelectBox<String> fontSelectBox;
    private Font masterFont;
    
    public PopTextraTypist() {
        super(new PopTableStyle());
        
        masterFont = KnownFonts.getStandardFamily();
        
        setFillParent(true);
        setBackground(skin.getDrawable("tt-bg"));
        
        var root = this;
        root.pad(10);
        root.top().left();
        
        var label = new Label("TextraTypist Playground", skin, "tt-title");
        root.add(label).left();
        
        root.row();
        var table = new Table();
        table.left();
        root.add(table).growX().spaceTop(15);
        
        var buttonGroup = new ButtonGroup<TextButton>();
        table.defaults().space(20);
        var textButton = new TextButton("File", skin, "tt-file");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        
        textButton = new TextButton("Home", skin, "tt-file");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
    
        textButton = new TextButton("Help", skin, "tt-file");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        
        root.row();
        table = new Table();
        table.left();
        table.setBackground(skin.getDrawable("tt-ribbon-10"));
        root.add(table).growX().spaceTop(10);
        
        table.defaults().space(10);
        var imageButton = new ImageButton(skin, "tt-bold");
        table.add(imageButton).padLeft(10);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[*]"));
    
        imageButton = new ImageButton(skin, "tt-italics");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[/]"));
    
        imageButton = new ImageButton(skin, "tt-superscript");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[^]"));
    
        imageButton = new ImageButton(skin, "tt-subscript");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[.]"));
        
        imageButton = new ImageButton(skin, "tt-midscript");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[=]"));
    
        imageButton = new ImageButton(skin, "tt-underline");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[_]"));
    
        imageButton = new ImageButton(skin, "tt-strike");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[~]"));
    
        imageButton = new ImageButton(skin, "tt-caps");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[!]"));
    
        imageButton = new ImageButton(skin, "tt-lower");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[,]"));
    
        imageButton = new ImageButton(skin, "tt-each");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[;]"));
    
        imageButton = new ImageButton(skin, "tt-square");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[]", ""));
    
        imageButton = new ImageButton(skin, "tt-square-at");
        table.add(imageButton);
        imageButton.addListener(handListener);
        Utils.onChange(imageButton, () -> insertTag("[@]", ""));
    
        imageButton = new ImageButton(skin, "tt-emoji");
        table.add(imageButton);
        imageButton.addListener(handListener);
//        Utils.onChange(imageButton, () -> insertTag("[]", ""));
        
        fontSelectBox = new SelectBox<String>(skin, "tt");
        table.add(fontSelectBox);
        fontSelectBox.addListener(handListener);
        fontSelectBox.getList().addListener(handListener);
    
        var items = new Array<String>();
        items.add("Select a font...");
        fontSelectBox.setItems(items);
        Utils.onChange(fontSelectBox, () -> {
            if (fontSelectBox.getSelectedIndex() != 0) {
                insertTag("[@" + fontSelectBox.getSelected() + "]", "[@]");
                fontSelectBox.setSelectedIndex(0);
            }
        });
    
        var selectBox = new SelectBox<>(skin, "tt");
        table.add(selectBox);
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        
        var colorSelectBox = new SelectBox<String>(skin, "tt");
        items = new Array<>();
        for (var color : Colors.getColors()) {
            items.add(color.key);
        }
        items.sort();
        items.insert(0, "Select a color...");
        items.insert(1, "More colors...");
        colorSelectBox.setItems(items);
        table.add(colorSelectBox);
        colorSelectBox.addListener(handListener);
        colorSelectBox.getList().addListener(handListener);
        Utils.onChange(colorSelectBox, () -> {
            if (colorSelectBox.getSelectedIndex() == 1) {
                PopColorPicker.showColorPicker(Color.RED, stage);
                colorSelectBox.setSelectedIndex(0);
            } else if (colorSelectBox.getSelectedIndex() > 1) {
                insertTag("[" + colorSelectBox.getSelected() + "]", "");
                colorSelectBox.setSelectedIndex(0);
            }
        });
        
        root.defaults().padLeft(20).padRight(20);
    
        root.row();
        label = new Label("CODE", skin, "tt-subtitle");
        root.add(label).left().spaceTop(15);
    
        root.row();
        codeTextArea = new TextArea("", skin, "tt-page");
        codeTextArea.setName("code");
        root.add(codeTextArea).grow().uniformY();
        codeTextArea.addListener(ibeamListener);
        Utils.onChange(codeTextArea, () -> {
            previewTypingLabel.setText(codeTextArea.getText());
            previewTypingLabel.restart();
        });
    
        root.row();
        imageButton = new ImageButton(skin, "tt-copy");
        root.add(imageButton).right().spaceTop(5);
        imageButton.addListener(handListener);
    
        root.row();
        label = new Label("PREVIEW", skin, "tt-subtitle");
        root.add(label).left();
        
        root.row();
        previewTable = new Table();
        previewTable.setBackground(skin.getDrawable("tt-page-10"));
        previewTable.setColor(Color.BLACK);
        root.add(previewTable).grow().uniformY();
    
        previewTypingLabel = new TypingLabel("", masterFont);
        previewTypingLabel.setWrap(true);
        previewScrollPane = new ScrollPane(previewTypingLabel, skin, "tt");
        previewTable.add(previewScrollPane).grow();
    
        root.row();
        table = new Table();
        root.add(table).right().spaceTop(5).padBottom(20);
        
        table.defaults().space(5);
        imageButton = new ImageButton(skin, "tt-color");
        table.add(imageButton);
        imageButton.addListener(handListener);
    
        imageButton = new ImageButton(skin, "tt-copy");
        table.add(imageButton);
        imageButton.addListener(handListener);
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
        Utils.onChange(textButton, () -> {
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
        Utils.onChange(textButton, () -> {
            activateSkinFontFamily();
            pop.hide();
        });
        
        pop.show(stage);
        pop.pad(50);
    }
    
    private void activateStandardFontFamily() {
        masterFont.dispose();
        var items = new Array<String>();
        items.add("Select a font...");
        
        masterFont = KnownFonts.getStandardFamily();
        for (var font : KnownFonts.getAllStandard()) {
            items.add(font.name);
        }
        
        fontSelectBox.setItems(items);
        
        previewTypingLabel = new TypingLabel(previewTypingLabel.getOriginalText().toString(), masterFont);
        previewTypingLabel.setWrap(true);
        previewScrollPane.setActor(previewTypingLabel);
    }
    
    private void activateSkinFontFamily() {
        masterFont.dispose();
        var items = new Array<String>();
        items.add("Select a font...");
        
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
        previewScrollPane.setActor(previewTypingLabel);
    }
    
    @Override
    public void hide() {
        stage.setKeyboardFocus(null);
        super.hide();
    }
}