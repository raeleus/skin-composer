package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerEvents;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimActor;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimHorizontalGroup;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel.SimVerticalGroup;

public class VerticalGroupAddChildUndoable implements SceneComposerUndoable {
    private SimVerticalGroup verticalGroup;
    private DialogSceneComposer dialog;
    private SimActor newWidget;
    
    public VerticalGroupAddChildUndoable(DialogSceneComposerEvents.WidgetType widgetType) {
        dialog = DialogSceneComposer.dialog;
        verticalGroup = (SimVerticalGroup) dialog.simActor;
        
        switch (widgetType) {
            case BUTTON:
                newWidget = new DialogSceneComposerModel.SimButton();
                break;
            case CHECK_BOX:
                newWidget = new DialogSceneComposerModel.SimCheckBox();
                break;
            case IMAGE:
                newWidget = new DialogSceneComposerModel.SimImage();
                break;
            case IMAGE_BUTTON:
                newWidget = new DialogSceneComposerModel.SimImageButton();
                break;
            case IMAGE_TEXT_BUTTON:
                newWidget = new DialogSceneComposerModel.SimImageTextButton();
                break;
            case LABEL:
                newWidget = new DialogSceneComposerModel.SimLabel();
                break;
            case LIST:
                newWidget = new DialogSceneComposerModel.SimList();
                break;
            case PROGRESS_BAR:
                newWidget = new DialogSceneComposerModel.SimProgressBar();
                break;
            case SELECT_BOX:
                newWidget = new DialogSceneComposerModel.SimSelectBox();
                break;
            case SLIDER:
                newWidget = new DialogSceneComposerModel.SimSlider();
                break;
            case TEXT_BUTTON:
                newWidget = new DialogSceneComposerModel.SimTextButton();
                break;
            case TEXT_FIELD:
                newWidget = new DialogSceneComposerModel.SimTextField();
                break;
            case TEXT_AREA:
                newWidget = new DialogSceneComposerModel.SimTextArea();
                break;
            case TOUCH_PAD:
                newWidget = new DialogSceneComposerModel.SimTouchPad();
                break;
            case CONTAINER:
                newWidget = new DialogSceneComposerModel.SimContainer();
                break;
            case HORIZONTAL_GROUP:
                newWidget = new DialogSceneComposerModel.SimHorizontalGroup();
                break;
            case SCROLL_PANE:
                newWidget = new DialogSceneComposerModel.SimScrollPane();
                break;
            case STACK:
                newWidget = new DialogSceneComposerModel.SimStack();
                break;
            case SPLIT_PANE:
                newWidget = new DialogSceneComposerModel.SimSplitPane();
                break;
            case TABLE:
                newWidget = new DialogSceneComposerModel.SimTable();
                break;
            case TREE:
                newWidget = new DialogSceneComposerModel.SimTree();
                break;
            case VERTICAL_GROUP:
                newWidget = new DialogSceneComposerModel.SimVerticalGroup();
                break;
        }
        newWidget.parent = verticalGroup;
    }
    
    @Override
    public void undo() {
        verticalGroup.children.removeValue(newWidget, true);
        
        if (dialog.simActor != verticalGroup) {
            dialog.simActor = verticalGroup;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        verticalGroup.children.add(newWidget);
        
        if (dialog.simActor != newWidget) {
            dialog.simActor = newWidget;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Widget for VerticalGroup\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Widget for VerticalGroup\"";
    }
}