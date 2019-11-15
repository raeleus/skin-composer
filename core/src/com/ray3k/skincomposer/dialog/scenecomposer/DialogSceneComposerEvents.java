package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.PopTable;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;

public class DialogSceneComposerEvents {
    private DialogSceneComposer dialog;
    
    public DialogSceneComposerEvents(DialogSceneComposer dialog) {
        this.dialog = dialog;
    }
    
    public void menuImport() {
        dialog.showImportDialog();
    }
    
    public void menuExport() {
        dialog.showExportDialog();
    }
    
    public void menuSettings() {
        dialog.showSettingsDialog();
    }
    
    public void menuQuit() {
        dialog.hide();
    }
    
    public void menuRefresh() {
    }
    
    public void menuClear() {
        dialog.model.clear();
    }
    
    public void menuUndo() {
        dialog.model.undo();
    }
    
    public void menuRedo() {
        dialog.model.redo();
    }
    
    public void menuMode() {
    
    }
    
    public void menuHelp() {
        dialog.showHelpDialog();
    }
}
