package com.ray3k.skincomposer.dialog.textratypist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.github.tommyettinger.textra.Font;
import com.ray3k.skincomposer.Main;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.PopTableHoverListener;
import com.ray3k.stripe.PopTableTooltipListener;

import java.util.Comparator;

import static com.ray3k.skincomposer.Main.*;
import static com.ray3k.skincomposer.utils.Utils.onChange;

public class PopTextraEmoji extends PopTable {
    private Font masterFont;
    private HorizontalGroup horizontalGroup;
    private enum Lister {
        SMILEYS, PEOPLE, COMPONENT, NATURE, FOOD, ACTIVITIES, TRAVEL, OBJECTS, SYMBOLS, FLAGS, ALL
    }
    private Lister lister = Lister.SMILEYS;
    private String search = "";
    private TextField searchField;
    
    public PopTextraEmoji(Font masterFont) {
        this.masterFont = masterFont;
        var style = new PopTableStyle();
        style.background = skin.getDrawable("tt-bg");
        style.stageBackground = skin.getDrawable("tt-stage-background");
    
        setStyle(style);
        setModal(true);
        setKeepSizedWithinStage(true);
        setKeepCenteredInWindow(true);
        setAutomaticallyResized(false);
        pad(10);
        
        defaults().space(5);
        
        var table = new Table();
        add(table);
        
        table.defaults().space(5);
        table.pad(5);
        var buttonGroup = new ButtonGroup<TextButton>();
        var textButton = new TextButton("Smileys", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.SMILEYS;
            refreshGroup();
        });
    
        textButton = new TextButton("People", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.PEOPLE;
            refreshGroup();
        });
    
        textButton = new TextButton("Component", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.COMPONENT;
            refreshGroup();
        });
    
        textButton = new TextButton("Nature", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.NATURE;
            refreshGroup();
        });
    
        textButton = new TextButton("Food", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.FOOD;
            refreshGroup();
        });
    
        textButton = new TextButton("Activities", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.ACTIVITIES;
            refreshGroup();
        });
    
        textButton = new TextButton("Travel", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.TRAVEL;
            refreshGroup();
        });
    
        textButton = new TextButton("Objects", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.OBJECTS;
            refreshGroup();
        });
    
        textButton = new TextButton("Symbols", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.SYMBOLS;
            refreshGroup();
        });
    
        textButton = new TextButton("Flags", skin, "tt-toggle");
        table.add(textButton);
        buttonGroup.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            lister = Lister.FLAGS;
            refreshGroup();
        });
    
        var allTextButton = new TextButton("All", skin, "tt-toggle");
        table.add(allTextButton);
        buttonGroup.add(allTextButton);
        allTextButton.addListener(handListener);
        onChange(allTextButton, () -> {
            lister = Lister.ALL;
            refreshGroup();
        });
        
        row();
        searchField = new TextField("", skin, "tt");
        add(searchField).growX();
        onChange(searchField, () -> {
            search = searchField.getText();
            if (allTextButton.isChecked()) refreshGroup();
            else allTextButton.setChecked(true);
        });
        searchField.addListener(ibeamListener);
        
        row();
        horizontalGroup = new HorizontalGroup();
        horizontalGroup.wrap();
        horizontalGroup.space(5);
        horizontalGroup.wrapSpace(5);
        horizontalGroup.rowAlign(Align.left);
        var scrollPane = new ScrollPane(horizontalGroup, skin, "tt");
        scrollPane.setFadeScrollBars(false);
        add(scrollPane).grow();
        scrollPane.addListener(scrollFocusListener);
        
        refreshGroup();
        
        row();
        table = new Table();
        table.right();
        add(table);
        
        textButton = new TextButton("Cancel", skin, "tt");
        table.add(textButton);
        textButton.addListener(handListener);
        onChange(textButton, () -> {
            hide();
            fire(new PopEmojiEvent());
        });
    }
    
    private void refreshGroup() {
        horizontalGroup.clear();
        var buttonListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
                fire(new PopEmojiEvent((String) actor.getUserObject()));
            }
        };
    
        var imageButtons = new Array<ImageButton>();
        
        var jsonReader = new JsonReader();
        var jsonValue = jsonReader.parse(Gdx.files.internal("emoji-info.json"));
    
        switch (lister) {
            case SMILEYS:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Smileys & Emotion")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case PEOPLE:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("People & Body")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case NATURE:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Animals & Nature")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case FOOD:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Food & Drink")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case TRAVEL:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Travel & Places")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case ACTIVITIES:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Activities")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case OBJECTS:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Objects")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case COMPONENT:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Component")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case SYMBOLS:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Symbols")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case FLAGS:
                for (var child : jsonValue.iterator()) {
                    if (child.getString("group").equals("Flags")) {
                        var text = child.getString("name");
                        var value = masterFont.nameLookup.get(text, -1);
                        if (value != -1) {
                            var imageButton = createImageButton(value, text);
                            imageButtons.add(imageButton);
                            imageButton.addListener(handListener);
                            imageButton.addListener(buttonListener);
                        }
                    }
                }
                break;
            case ALL:
                for (var child : jsonValue.iterator()) {
                    var text = child.getString("name");
                    var value = masterFont.nameLookup.get(text, -1);
                    if (value != -1) {
                        var imageButton = createImageButton(value, text);
                        imageButtons.add(imageButton);
                        imageButton.addListener(handListener);
                        imageButton.addListener(buttonListener);
                    }
                }
    
                var newList = new Array<ImageButton>();
                for (var imageButton : imageButtons) {
                    if (search.equals("") || ((String) imageButton.getUserObject()).contains(search)) {
                        newList.add(imageButton);
                    }
                }
                imageButtons = newList;
                
//                imageButtons.sort(Comparator.comparing(o -> ((String) o.getUserObject())));
                break;
        }
        
        var names = new Array<String>();
        for (var imageButton : imageButtons) {
            var name = (String) imageButton.getUserObject();
            if (!names.contains(name, false)) {
                horizontalGroup.addActor(imageButton);
                names.add(name);
            }
        }
    }
    
    private ImageButton createImageButton(int value, String text) {
        var glyphRegion = masterFont.mapping.get(value);
        var imageButtonStyle = new ImageButtonStyle();
        imageButtonStyle.imageUp = new TextureRegionDrawable(glyphRegion);
        imageButtonStyle.pressedOffsetY = -1;
        var imageButton = new ImageButton(imageButtonStyle);
        imageButton.setUserObject(text);
        
        var style = new PopTableStyle();
        style.background = skin.getDrawable("tt-button-10");
        var pop = new PopTableHoverListener(Align.top, Align.top, style);
        
        var label = new Label(text, skin, "tt");
        pop.getPopTable().add(label);
        
        imageButton.addListener(pop);
        return imageButton;
    }
    
    public static class PopEmojiEvent extends Event {
        String regionName;
    
        public PopEmojiEvent(String regionName) {
            this.regionName = regionName;
        }
    
        public PopEmojiEvent() {
        }
    }
    
    public static abstract class PopEmojiListener implements EventListener {
        @Override
        public boolean handle(Event event) {
            if (event instanceof PopEmojiEvent) {
                var popEffectsEvent = (PopEmojiEvent) event;
                if (popEffectsEvent.regionName != null) accepted(popEffectsEvent.regionName);
                else cancelled();
            }
            return false;
        }
        
        public abstract void accepted(String regionName);
        public abstract void cancelled();
    }
    
    @Override
    public void show(Stage stage, Action action) {
        super.show(stage, action);
        stage.setKeyboardFocus(searchField);
    }
    
    public static PopTextraEmoji showPopEmoji(Font masterFont) {
        var pop = new PopTextraEmoji(masterFont);
        pop.show(stage);
        pop.setSize(600, 350);
        return pop;
    }
}