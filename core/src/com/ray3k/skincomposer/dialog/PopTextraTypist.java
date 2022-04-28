package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.KnownFonts;
import com.github.tommyettinger.textra.TypingLabel;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopTable;

import static com.ray3k.skincomposer.Main.*;

public class PopTextraTypist extends PopTable {
    private TextArea codeTextArea;
    private TypingLabel previewTypingLabel;
    
    public PopTextraTypist() {
        super(new PopTableStyle());
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
        
        var fontSelectBox = new SelectBox<String>(skin, "tt");
        table.add(fontSelectBox);
        fontSelectBox.addListener(handListener);
        fontSelectBox.getList().addListener(handListener);
    
        var items = new Array<String>();
        items.add("Select a font...");
        for (var font : KnownFonts.getAllStandard()) {
            items.add(font.name);
        }
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
        
        selectBox = new SelectBox<>(skin, "tt");
        table.add(selectBox);
        selectBox.addListener(handListener);
        selectBox.getList().addListener(handListener);
        
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
        table = new Table();
        table.setBackground(skin.getDrawable("tt-page-10"));
        root.add(table).grow().uniformY();
    
        previewTypingLabel = new TypingLabel("", KnownFonts.getStandardFamily());
        previewTypingLabel.setWrap(true);
        var scrollPane = new ScrollPane(previewTypingLabel, skin, "tt");
        table.add(scrollPane).grow();
    
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
    }
    
    @Override
    public void hide() {
        stage.setKeyboardFocus(null);
        super.hide();
    }
}