package com.ray3k.skincomposer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class PanelMenuBar {

    public PanelMenuBar(final Table table, final Skin skin, final Stage stage) {
        final Array<TextButton> menuButtons = new Array<TextButton>();
        
        table.defaults().padTop(1.0f).padBottom(1.0f);
        table.setBackground("dark-orange");
        TextButton textButton = new TextButton("File", skin, "menu");
        Table menuItemTable = new Table();
        menuItemTable.defaults().growX();
        TextButton menuItemTextButton = new TextButton("New", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("New From Template...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Open...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save As...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save As Template...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Import...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Export...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList1 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList1.show(stage);
                } else {
                    menuList1.hide();
                }
            }
        });
        table.add(textButton).padLeft(1.0f);
        menuButtons.add(textButton);
        textButton = new TextButton("Edit", skin, "menu");
        menuItemTable = new Table();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Undo", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        menuItemTable.row();
        menuItemTextButton = new TextButton("Redo", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList2 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList2.show(stage);
                } else {
                    menuList2.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        table.add().growX();

        //deselect menu buttons if escape is pressed or if stage is clicked anywhere else
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    for (TextButton button : menuButtons) {
                        button.setChecked(false);
                    }
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                for (TextButton textButton : menuButtons) {
                    if (!textButton.isAscendantOf(event.getTarget())) {
                        textButton.setChecked(false);
                    }
                }
                return false;
            }
        });
    }
}
