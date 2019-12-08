package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneChildFirstUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimActor childFirst;
    private DialogSceneComposerModel.SimActor previousChildFirst;
    
    public SplitPaneChildFirstUndoable(DialogSceneComposerEvents.WidgetType widgetType) {
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        
        previousChildFirst = splitPane.childFirst;
        
        switch (widgetType) {
            case BUTTON:
                childFirst = new DialogSceneComposerModel.SimButton();
                break;
            case CHECK_BOX:
                childFirst = new DialogSceneComposerModel.SimCheckBox();
                break;
            case IMAGE:
                childFirst = new DialogSceneComposerModel.SimImage();
                break;
            case IMAGE_BUTTON:
                childFirst = new DialogSceneComposerModel.SimImageButton();
                break;
            case IMAGE_TEXT_BUTTON:
                childFirst = new DialogSceneComposerModel.SimImageTextButton();
                break;
            case LABEL:
                childFirst = new DialogSceneComposerModel.SimLabel();
                break;
            case LIST:
                childFirst = new DialogSceneComposerModel.SimList();
                break;
            case PROGRESS_BAR:
                childFirst = new DialogSceneComposerModel.SimProgressBar();
                break;
            case SELECT_BOX:
                childFirst = new DialogSceneComposerModel.SimSelectBox();
                break;
            case SLIDER:
                childFirst = new DialogSceneComposerModel.SimSlider();
                break;
            case TEXT_BUTTON:
                childFirst = new DialogSceneComposerModel.SimTextButton();
                break;
            case TEXT_FIELD:
                childFirst = new DialogSceneComposerModel.SimTextField();
                break;
            case TEXT_AREA:
                childFirst = new DialogSceneComposerModel.SimTextArea();
                break;
            case TOUCH_PAD:
                childFirst = new DialogSceneComposerModel.SimTouchPad();
                break;
            case CONTAINER:
                childFirst = new DialogSceneComposerModel.SimContainer();
                break;
            case HORIZONTAL_GROUP:
                childFirst = new DialogSceneComposerModel.SimHorizontalGroup();
                break;
            case SCROLL_PANE:
                childFirst = new DialogSceneComposerModel.SimScrollPane();
                break;
            case STACK:
                childFirst = new DialogSceneComposerModel.SimStack();
                break;
            case SPLIT_PANE:
                childFirst = new DialogSceneComposerModel.SimSplitPane();
                break;
            case TABLE:
                childFirst = new DialogSceneComposerModel.SimTable();
                break;
            case TREE:
                childFirst = new DialogSceneComposerModel.SimTree();
                break;
            case VERTICAL_GROUP:
                childFirst = new DialogSceneComposerModel.SimVerticalGroup();
                break;
        }
        childFirst.parent = splitPane;
    }
    
    @Override
    public void undo() {
        splitPane.childFirst = previousChildFirst;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.childFirst = childFirst;
        
        if (dialog.simActor != splitPane.childFirst) {
            dialog.simActor = splitPane.childFirst;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Set first child for SplitPane\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Set first child for SplitPane\"";
    }
}
