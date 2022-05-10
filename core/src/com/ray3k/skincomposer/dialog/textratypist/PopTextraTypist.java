package com.ray3k.skincomposer.dialog.textratypist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.Font.DistanceFieldType;
import com.github.tommyettinger.textra.Font.FontFamily;
import com.github.tommyettinger.textra.KnownFonts;
import com.github.tommyettinger.textra.TypingLabel;
import com.ray3k.skincomposer.SpineDrawable;
import com.ray3k.skincomposer.dialog.textratypist.PopColorPicker.PopColorPickerListener;
import com.ray3k.skincomposer.dialog.textratypist.PopEffects.PopEffectsListener;
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
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, this::showFileTable);
        
        textButton = new TextButton("Home", skin, "tt-file");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        textButton.setChecked(true);
        onChange(textButton, this::showHomeTable);
    
        textButton = new TextButton("Help", skin, "tt-file");
        textButton.setProgrammaticChangeEvents(false);
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, this::showHelpTable);
        
        root.row();
        contentTable = new Table();
        root.add(contentTable).grow().spaceTop(10);
        showHomeTable();
    }
    
    private void showFileTable() {
        contentTable.clearChildren();
    }
    
    private void showHelpTable() {
        contentTable.clearChildren();
    
        var table = new Table();
        table.left();
        table.setBackground(skin.getDrawable("tt-ribbon-10"));
        contentTable.add(table).growX();
    
        table.defaults().space(10);
        var textButton = new TextButton("TextraTypist", skin, "tt-link");
        table.add(textButton).padLeft(10);
        textButton.addListener(handListener);
    
        textButton = new TextButton("TypingLabel", skin, "tt-link");
        table.add(textButton);
        textButton.addListener(handListener);
    
        textButton = new TextButton("Playground Wiki", skin, "tt-link");
        table.add(textButton);
        textButton.addListener(handListener);
        
        contentTable.defaults().space(10);
        contentTable.row();
        spine = new SpineDrawable(skeletonRenderer, textraTypistLogoSkeletonData, textraTypistLogoAnimationStateData);
        spine.getAnimationState().setAnimation(0, "animation", false);
        spine.getAnimationState().addAnimation(0, "loop", true, 0);
        var image = new Image(spine);
        contentTable.add(image);
        
        contentTable.row();
        var label = new TypingLabel(Gdx.files.internal("AboutTextraTypist").readString(), skin, "tt");
        label.setWrap(true);
        label.setAlignment(Align.topLeft);
        contentTable.add(label).grow();
    }
    
    private void showHomeTable() {
        contentTable.clearChildren();
        
        var table = new Table();
        table.left();
        table.setBackground(skin.getDrawable("tt-ribbon-10"));
        contentTable.add(table).growX();
    
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
            var pop = PopEffects.showPopEffects();
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
                insertTag("[@]");
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
                insertTag("[%]");
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
                var pop = PopColorPicker.showColorPicker(null, stage);
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
                insertTag("{CLEARCOLOR}");
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
        codeTextArea = new TextArea("", skin, "tt-page");
        codeTextArea.setName("code");
        contentTable.add(codeTextArea).grow().uniformY();
        codeTextArea.addListener(ibeamListener);
        onChange(codeTextArea, () -> {
            previewTypingLabel.setText(codeTextArea.getText());
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
    
        previewTypingLabel = new TypingLabel("", masterFont);
        previewTypingLabel.setWrap(true);
        previewScrollPane = new ScrollPane(previewTypingLabel, skin, "tt");
        previewTable.add(previewScrollPane).grow();
    
        contentTable.row();
        table = new Table();
        contentTable.add(table).right().spaceTop(5).padBottom(20);
    
        table.defaults().space(5);
        imageButton = new ImageButton(skin, "tt-color");
        table.add(imageButton);
        imageButton.addListener(handListener);
        onChange(imageButton, () -> {
            var picker = new PopColorPicker(previewTable.getColor());
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
        previewScrollPane.setActor(previewTypingLabel);
    }
    
    @Override
    public void hide() {
        stage.setKeyboardFocus(null);
        super.hide();
    }
}