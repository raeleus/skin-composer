package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.MenuList;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.utils.SynchronousJFXFileChooser;
import java.io.File;
import javafx.stage.FileChooser;

public class PanelMenuBar {
    private Skin skin;
    private Stage stage;
    public PanelMenuBar(final Table table, final Skin skin, final Stage stage) {
        this.skin = skin;
        this.stage = stage;
        final Array<TextButton> menuButtons = new Array<TextButton>();
        
        table.defaults().padTop(1.0f).padBottom(1.0f);
        table.setBackground("dark-orange");
        
        TextButton textButton = new TextButton("File", skin, "menu");
        Table menuItemTable = new Table();
        menuItemTable.defaults().growX();
        
        TextButton menuItemTextButton = new TextButton("New", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Open...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                openDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                save(null);
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save As...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                saveAsDialog(null);
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Import...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                importDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Export...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                exportDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Exit", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
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
        menuItemTextButton.setDisabled(true);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Redo", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.setDisabled(true);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                
            }
        });
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
        
        textButton = new TextButton("Project", skin, "menu");
        menuItemTable = new Table();
        
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Settings...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogSettings();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Colors...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogColors();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Fonts...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogFonts();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Drawables...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogDrawables();
            }
        });
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList3 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList3.show(stage);
                } else {
                    menuList3.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Help", skin, "menu");
        menuItemTable = new Table();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("About", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance.showDialogAbout();
            }
        });
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList4 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList4.show(stage);
                } else {
                    menuList4.hide();
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
    
    public void newDialog() {
        Dialog dialog = new Dialog("Save Changes?", skin) {
            @Override
            protected void result(Object object) {
                if ((int) object == 1) {
                    save(() -> {
                        ProjectData.instance().clear();
                    });
                } else if ((int) object == 2) {
                    ProjectData.instance().clear();
                }
            }
        };
        dialog.text("Do you want to save changes to the existing project?\nAll unsaved changes will be lost.");
        dialog.button("Yes", 1);
        dialog.button("No", 2);
        dialog.button("Cancel", 3);
        dialog.key(Keys.ESCAPE, 3);
        dialog.show(stage);
    }
    
    public void openDialog() {
        Runnable runnable = () -> {
            SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
                FileChooser ch = new FileChooser();
                FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Skin Composer files", "*.scmp");
                ch.getExtensionFilters().add(ex);
                ch.setTitle("Open skin file...");
                ch.setInitialDirectory(new File(ProjectData.instance().getBestSaveDirectory()));
                return ch;
            });
            File file = chooser.showOpenDialog();
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().load(fileHandle);
            }
        };
        
        Dialog dialog = new Dialog("Save Changes?", skin) {
            @Override
            protected void result(Object object) {
                if ((int) object == 1) {
                    save(runnable);
                } else if ((int) object == 2) {
                    runnable.run();
                }
            }
        };
        dialog.text("Do you want to save changes to the existing project?\nAll unsaved changes will be lost.");
        dialog.button("Yes", 1);
        dialog.button("No", 2);
        dialog.button("Cancel", 3);
        dialog.key(Keys.ESCAPE, 3);
        dialog.show(stage);
    }
    
    public void save(Runnable runnable) {
        if (ProjectData.instance().getSaveFile() != null) {
            ProjectData.instance().save();
        } else {
            saveAsDialog(runnable);
        }
    }
    
    public void saveAsDialog(Runnable runnable) {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Skin Composer files", "*.scmp");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Save skin file as...");
            ch.setInitialDirectory(new File(ProjectData.instance().getBestSaveDirectory()));
            return ch;
        });
        File file = chooser.showSaveDialog();
        if (file != null) {
            FileHandle fileHandle = new FileHandle(file);
            ProjectData.instance().save(fileHandle);
            if (runnable != null) {
                runnable.run();
            }
        }
    }
    
    public void importDialog() {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Json files", "*.json");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Import skin...");
            if (ProjectData.instance().getLastDirectory() != null) {
                ch.setInitialDirectory(new File(ProjectData.instance().getLastDirectory()));
            }
            return ch;
        });
        File file = chooser.showOpenDialog();
        if (file != null) {
            FileHandle fileHandle = new FileHandle(file);
            ProjectData.instance().setLastDirectory(fileHandle.parent().path());
            JsonData.getInstance().readFile(fileHandle);
        }
    }
    
    public void exportDialog() {
        SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
            FileChooser ch = new FileChooser();
            FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("Json files", "*.json");
            ch.getExtensionFilters().add(ex);
            ch.setTitle("Export skin...");
            
            if (ProjectData.instance().getLastDirectory() != null) {
                ch.setInitialDirectory(new File(ProjectData.instance().getLastDirectory()));
            }
            return ch;
        });
        File file = chooser.showSaveDialog();
        if (file != null) {
            FileHandle fileHandle = new FileHandle(file);
            ProjectData.instance().setLastDirectory(fileHandle.parent().path());
            JsonData.getInstance().writeFile(fileHandle);
        }
    }
}
