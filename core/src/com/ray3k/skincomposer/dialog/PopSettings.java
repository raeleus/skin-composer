package com.ray3k.skincomposer.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.RootTable;
import com.ray3k.skincomposer.dialog.PopRevertUIscale.PopRevertEventListener;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.Spinner;
import com.ray3k.stripe.Spinner.Orientation;

import static com.ray3k.skincomposer.Main.*;

public class PopSettings extends PopTable {
    private float uiScale;
    private Integer maxUndos;
    private boolean resourcesRelative;
    private boolean allowingWelcome;
    private boolean exportWarnings;
    private boolean recentFullPath;
    private boolean allowingUpdates;
    private boolean changedUIscale;
    private boolean resetTips;
    
    public PopSettings() {
        super(skin, "dialog");
    
        setKeepCenteredInWindow(true);
        setModal(true);
        setHideOnUnfocus(true);
        
        uiScale = projectData.getUiScale();
        maxUndos = projectData.getMaxUndos();
        resourcesRelative = projectData.areResourcesRelative();
        allowingWelcome = projectData.isAllowingWelcome();
        exportWarnings = projectData.isShowingExportWarnings();
        recentFullPath = projectData.isFullPathInRecentFiles();
        allowingUpdates = projectData.isCheckingForUpdates();
        
        populate();
    }
    
    @Override
    public void show(Stage stage, Action action) {
        fire(new DialogEvent(DialogEvent.Type.OPEN));
        super.show(stage, action);
    }
    
    @Override
    public boolean remove() {
        fire(new DialogEvent(DialogEvent.Type.CLOSE));
        return super.remove();
    }
    
    public void populate() {
        pad(10);
        
        defaults().space(15);
        var label = new Label("Settings", skin, "title");
        add(label);
        
        row();
        var table = new Table();
        add(table);
        
        table.defaults().growX().space(5);
        if (!Utils.isMac()) {
            var textButton = new TextButton("Open temp/log directory", skin);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    try {
                        Utils.openFileExplorer(Main.appFolder.child("temp/"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening temp folder", e);
                        dialogFactory.showDialogError("Folder Error...", "Error opening temp folder.\n\nOpen log?");
                    }
                }
            });
            textButton.addListener(handListener);
            table.add(textButton);
    
            table.row();
            textButton = new TextButton("Open preferences directory", skin);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    try {
                        Utils.openFileExplorer(Gdx.files.external(".prefs/"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening preferences folder", e);
                        dialogFactory.showDialogError("Folder Error...",
                                "Error opening preferences folder.\n\nOpen log?");
                    }
                }
            });
            textButton.addListener(handListener);
            table.add(textButton);
    
            if (projectData.areChangesSaved() && projectData.getSaveFile().exists()) {
                table.row();
                textButton = new TextButton("Open project/import directory", skin);
                textButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event,
                                        Actor actor) {
                        try {
                            Utils.openFileExplorer(projectData.getSaveFile().sibling(
                                    projectData.getSaveFile().nameWithoutExtension() + "_data"));
                        } catch (Exception e) {
                            Gdx.app.error(getClass().getName(), "Error opening project folder", e);
                            dialogFactory.showDialogError("Folder Error...",
                                    "Error opening project folder\n\nOpen log?");
                        }
                    }
                });
                textButton.addListener(handListener);
                table.add(textButton);
            }
    
            table.row();
            textButton = new TextButton("Open texture packer settings file for export", skin);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    try {
                        Utils.openFileExplorer(Main.appFolder.child("texturepacker/atlas-export-settings.json"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening atlas-export-settings.json", e);
                        dialogFactory.showDialogError("File Error...",
                                "Error opening atlas-export-settings.json\n\nOpen log?");
                    }
                }
            });
            textButton.addListener(handListener);
            table.add(textButton);
    
            table.row();
            textButton = new TextButton("Open texture packer settings file for preview", skin);
            textButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                    try {
                        Utils.openFileExplorer(Main.appFolder.child("texturepacker/atlas-internal-settings.json"));
                    } catch (Exception e) {
                        Gdx.app.error(getClass().getName(), "Error opening atlas-internal-settings.json", e);
                        dialogFactory.showDialogError("File Error...",
                                "Error opening atlas-internal-settings.json\n\nOpen log?");
                    }
                }
            });
            textButton.addListener(handListener);
            table.add(textButton);
            
            table.row();
            textButton = new TextButton("Reset all tips", skin);
            Utils.onChange(textButton, () -> {
                resetTips = true;
            });
            textButton.addListener(handListener);
            table.add(textButton);
        }
        
        row();
        table = new Table();
        add(table);
        
        table.defaults().space(5);
        label = new Label("UI Scale:", skin);
        table.add(label);
        
        var slider = new Slider(1, 3, 0.25f, false, skin);
        slider.setValue(uiScale);
        table.add(slider);
        slider.addListener(handListener);
        
        var scaleLabel = new Label(uiScale + "x", skin);
        table.add(scaleLabel).width(25);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                uiScale =  slider.getValue();
                scaleLabel.setText(uiScale + "x");
                
                if (!slider.isDragging()) {
                    main.resizeUiScale(uiScale, uiScale > 1);
                    setHideOnUnfocus(false);
                    dialogFactory.showRevertUIscale(new PopRevertEventListener() {
                        @Override
                        public void accepted() {
                            changedUIscale = true;
                            setHideOnUnfocus(true);
                        }
    
                        @Override
                        public void reverted() {
                            setHideOnUnfocus(true);
                            main.resizeUiScale(projectData.getUiScale());
                            slider.setProgrammaticChangeEvents(false);
                            slider.setValue(projectData.getUiScale());
                            uiScale = (int) slider.getValue();
                            scaleLabel.setText(uiScale + "x");
                            slider.setProgrammaticChangeEvents(true);
                        }
                    });
                }
            }
        });
        
        var textButton = new TextButton("Auto", skin);
        table.add(textButton);
        textButton.addListener(handListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var display = Gdx.graphics.getDisplayMode();
                uiScale = display.height >= 1440 ? 2 : 1;
                slider.setValue(uiScale);
                scaleLabel.setText(uiScale + "x");
                main.resizeUiScale(uiScale, uiScale > 1);
                changedUIscale = true;
            }
        });
        
        row();
        table = new Table();
        add(table);
        
        table.defaults().space(5);
        label = new Label("Max Number of Undos: ", skin);
        table.add(label);
        
        var spinner = new Spinner(projectData.getMaxUndos(), 1.0, true, Orientation.HORIZONTAL, getSkin());
        spinner.setMinimum(1.0);
        spinner.setMaximum(100.0);
        spinner.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                maxUndos = (int) spinner.getValue();
            }
        });
        spinner.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusListener.FocusEvent event,
                                             Actor actor, boolean focused) {
                maxUndos = (int) spinner.getValue();
            }
            
        });
        spinner.getTextField().addListener(ibeamListener);
        spinner.getButtonMinus().addListener(handListener);
        spinner.getButtonPlus().addListener(handListener);
        table.add(spinner).minWidth(100.0f);
        
        row();
        table = new Table();
        add(table);
        
        table.defaults().expandX().left().space(5);
        var relativeCheckBox = new ImageTextButton("Keep resources relative", getSkin(), "checkbox");
        relativeCheckBox.setChecked(resourcesRelative);
        relativeCheckBox.addListener(handListener);
        relativeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                resourcesRelative = relativeCheckBox.isChecked();
            }
        });
        table.add(relativeCheckBox);
        
        table.row();
        var welcomeCheckBox = new ImageTextButton("Show welcome screen", getSkin(), "checkbox");
        welcomeCheckBox.setChecked(allowingWelcome);
        welcomeCheckBox.addListener(handListener);
        welcomeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingWelcome = welcomeCheckBox.isChecked();
            }
        });
        table.add(welcomeCheckBox);
        
        table.row();
        var exportWarningsCheckBox = new ImageTextButton("Show export warnings", getSkin(), "checkbox");
        exportWarningsCheckBox.setChecked(exportWarnings);
        exportWarningsCheckBox.addListener(handListener);
        exportWarningsCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                exportWarnings = exportWarningsCheckBox.isChecked();
            }
        });
        table.add(exportWarningsCheckBox);
        
        table.row();
        var fullPathCheckBox = new ImageTextButton("Show full path in recent files", getSkin(), "checkbox");
        fullPathCheckBox.setChecked(recentFullPath);
        fullPathCheckBox.addListener(handListener);
        fullPathCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                recentFullPath = fullPathCheckBox.isChecked();
            }
        });
        table.add(fullPathCheckBox);

        table.row();
        var updatesCheckBox = new ImageTextButton("Check for updates", getSkin(), "checkbox");
        updatesCheckBox.setChecked(allowingUpdates);
        updatesCheckBox.addListener(handListener);
        updatesCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                allowingUpdates = updatesCheckBox.isChecked();
            }
        });
        table.add(updatesCheckBox);
        
        row();
        var buttonTable = new Table();
        buttonTable.pad(5);
        add(buttonTable);
        
        buttonTable.defaults().minWidth(75).space(5);
        textButton = new TextButton("OK", getSkin());
        textButton.addListener(handListener);
        buttonTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                approve();
            }
        });
        
        textButton = new TextButton("CANCEL", getSkin());
        textButton.addListener(handListener);
        buttonTable.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cancel();
            }
        });
    
        key(Keys.ESCAPE,() -> {
            cancel();
        });
    }
    
    private void approve() {
        projectData.setChangesSaved(false);
        projectData.setMaxUndos(maxUndos);
        projectData.setResourcesRelative(resourcesRelative);
        projectData.setAllowingWelcome(allowingWelcome);
        projectData.setUiScale(uiScale);
        projectData.setShowingExportWarnings(exportWarnings);
        projectData.setFullPathInRecentFiles(recentFullPath);
        projectData.setCheckingForUpdates(allowingUpdates);
        undoableManager.clearUndoables();
    
        if (resetTips) {
            projectData.setTipTVG(true);
            projectData.setTipFreeType(true);
            projectData.setTipTenPatch(true);
        }
        
        if (allowingUpdates) {
            Main.checkForUpdates(main);
        } else {
            Main.newVersion = Main.VERSION;
            rootTable.fire(new RootTable.RootTableEvent(RootTable.RootTableEnum.CHECK_FOR_UPDATES_COMPLETE));
        }
        Main.rootTable.updateRecentFiles();

        hide();
    }
    
    private void cancel() {
        if (changedUIscale) {
            main.resizeUiScale(projectData.getUiScale());
        }
        hide();
    }
}