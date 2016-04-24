package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.Spinner;
import com.ray3k.skincomposer.Spinner.SpinnerStyle;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.panel.PanelStatusBar;
import com.ray3k.skincomposer.Undoable;
import com.ray3k.skincomposer.utils.Utils;

public class DialogSettings extends Dialog {
    private Skin skin;
    private SpinnerStyle spinnerStyle;
    private Integer textureWidth;
    private Integer textureHeight;

    public DialogSettings(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        this.skin = skin;
        
        spinnerStyle = new Spinner.SpinnerStyle(skin.get("spinner-minus", Button.ButtonStyle.class), skin.get("spinner-plus", Button.ButtonStyle.class), skin.get("spinner", TextField.TextFieldStyle.class));
        
        textureWidth = ProjectData.instance().getMaxTextureWidth();
        textureHeight = ProjectData.instance().getMaxTextureHeight();
        
        populate();
    }

    @Override
    protected void result(Object object) {
        super.result(object);
        
        if ((boolean) object) {
            ProjectSettingsUndoable undoable = new ProjectSettingsUndoable(ProjectData.instance().getMaxTextureWidth(), ProjectData.instance().getMaxTextureHeight(), textureWidth, textureHeight);
            undoable.redo();
            Main.instance.addUndoable(undoable);
        }
    }

    public void populate() {
        Table t = getContentTable();
        
        Label label = new Label("Settings", skin, "title");
        label.setAlignment(Align.center);
        t.add(label).growX().colspan(2);
        
        t.row();
        TextButton textButton = new TextButton("Open temp/log directory", skin, "orange-small");
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Gdx.files.local("temp/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening temp folder", e);
                }
            }
        });
        t.add(textButton).colspan(2);
        
        t.row();
        textButton = new TextButton("Open preferences directory", skin, "orange-small");
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                try {
                    Utils.openFileExplorer(Gdx.files.external(".prefs/"));
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error opening temp folder", e);
                }
            }
        });
        t.add(textButton).colspan(2);
        
        if (ProjectData.instance().areChangesSaved() && ProjectData.instance().getSaveFile().exists()) {
            t.row();
            textButton = new TextButton("Open project/import directory", skin, "orange-small");
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    try {
                        Utils.openFileExplorer(ProjectData.instance().getSaveFile().sibling(ProjectData.instance().getSaveFile().nameWithoutExtension() + "_data"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening temp folder", e);
                    }
                }
            });
            t.add(textButton).colspan(2);
        }
        
        t.row();
        label = new Label("Max Texture Width: ", skin);
        t.add(label).right();
        Spinner spinner = new Spinner(ProjectData.instance().getMaxTextureWidth(), 1.0, true, spinnerStyle);
        spinner.setMinimum(256.0);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                textureWidth = (int) spinner.getValue();
            }
        });
        spinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                textureWidth = (int) spinner.getValue();
            }
            
        });
        t.add(spinner).growX();
        
        t.row();
        label = new Label("Max Texture Height: ", skin);
        t.add(label).right();
        Spinner spinner2 = new Spinner(ProjectData.instance().getMaxTextureHeight(), 1.0, true, spinnerStyle);
        spinner2.setMinimum(256.0);
        spinner2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                textureWidth = (int) spinner2.getValue();
            }
        });
        spinner2.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event, Actor actor, boolean focused) {
                textureWidth = (int) spinner2.getValue();
            }
            
        });
        t.add(spinner2).growX();
        
        button("OK", true);
        button ("Cancel", false);
        key(Keys.ESCAPE, false);
    }

    private static class ProjectSettingsUndoable implements Undoable {

        private int oldTextureWidth, oldTextureHeight, textureWidth, textureHeight;

        public ProjectSettingsUndoable(int oldTextureWidth, int oldTextureHeight, int textureWidth, int textureHeight) {
            this.oldTextureWidth = oldTextureWidth;
            this.oldTextureHeight = oldTextureHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
        }

        @Override
        public void undo() {
            ProjectData.instance().setMaxTextureDimensions(oldTextureWidth, oldTextureHeight);
            PanelStatusBar.instance.message("Changed max texture settings: " + oldTextureWidth + " " + oldTextureHeight);
        }

        @Override
        public void redo() {
            ProjectData.instance().setMaxTextureDimensions(textureWidth, textureHeight);
            PanelStatusBar.instance.message("Changed max texture settings: " + textureWidth + " " + textureHeight);
        }

        @Override
        public String getUndoText() {
            return "Modify Settings";
        }
    }
}
