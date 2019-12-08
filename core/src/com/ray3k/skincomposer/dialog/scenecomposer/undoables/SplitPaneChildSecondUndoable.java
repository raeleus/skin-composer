package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class SplitPaneChildSecondUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimSplitPane splitPane;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimActor childSecond;
    private DialogSceneComposerModel.SimActor previousChildSecond;
    
    public SplitPaneChildSecondUndoable(DialogSceneComposerEvents.WidgetType widgetType) {
        dialog = DialogSceneComposer.dialog;
        splitPane = (DialogSceneComposerModel.SimSplitPane) dialog.simActor;
        
        previousChildSecond = splitPane.childSecond;
        
        switch (widgetType) {
            case BUTTON:
                childSecond = new DialogSceneComposerModel.SimButton();
                break;
            case CHECK_BOX:
                childSecond = new DialogSceneComposerModel.SimCheckBox();
                break;
            case IMAGE:
                childSecond = new DialogSceneComposerModel.SimImage();
                break;
            case IMAGE_BUTTON:
                childSecond = new DialogSceneComposerModel.SimImageButton();
                break;
            case IMAGE_TEXT_BUTTON:
                childSecond = new DialogSceneComposerModel.SimImageTextButton();
                break;
            case LABEL:
                childSecond = new DialogSceneComposerModel.SimLabel();
                break;
            case LIST:
                childSecond = new DialogSceneComposerModel.SimList();
                break;
            case PROGRESS_BAR:
                childSecond = new DialogSceneComposerModel.SimProgressBar();
                break;
            case SELECT_BOX:
                childSecond = new DialogSceneComposerModel.SimSelectBox();
                break;
            case SLIDER:
                childSecond = new DialogSceneComposerModel.SimSlider();
                break;
            case TEXT_BUTTON:
                childSecond = new DialogSceneComposerModel.SimTextButton();
                break;
            case TEXT_FIELD:
                childSecond = new DialogSceneComposerModel.SimTextField();
                break;
            case TEXT_AREA:
                childSecond = new DialogSceneComposerModel.SimTextArea();
                break;
            case TOUCH_PAD:
                childSecond = new DialogSceneComposerModel.SimTouchPad();
                break;
            case CONTAINER:
                childSecond = new DialogSceneComposerModel.SimContainer();
                break;
            case HORIZONTAL_GROUP:
                childSecond = new DialogSceneComposerModel.SimHorizontalGroup();
                break;
            case SCROLL_PANE:
                childSecond = new DialogSceneComposerModel.SimScrollPane();
                break;
            case STACK:
                childSecond = new DialogSceneComposerModel.SimStack();
                break;
            case SPLIT_PANE:
                childSecond = new DialogSceneComposerModel.SimSplitPane();
                break;
            case TABLE:
                childSecond = new DialogSceneComposerModel.SimTable();
                break;
            case TREE:
                childSecond = new DialogSceneComposerModel.SimTree();
                break;
            case VERTICAL_GROUP:
                childSecond = new DialogSceneComposerModel.SimVerticalGroup();
                break;
        }
        childSecond.parent = splitPane;
    }
    
    @Override
    public void undo() {
        splitPane.childSecond = previousChildSecond;
        
        if (dialog.simActor != splitPane) {
            dialog.simActor = splitPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        splitPane.childSecond = childSecond;
        
        if (dialog.simActor != splitPane.childSecond) {
            dialog.simActor = splitPane.childSecond;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Set second child for SplitPane\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Set second child for SplitPane\"";
    }
}
