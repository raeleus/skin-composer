package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class ScrollPaneResetUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimScrollPane scrollPane;
    private DialogSceneComposer dialog;
    private String previousName;
    private StyleData previousStyle;
    private boolean previousFadeScrollBars;
    private DialogSceneComposerModel.SimActor previousChild;
    private boolean previousClamp;
    private boolean previousFlickScroll;
    private float previousFlingTime;
    private boolean previousForceScrollX;
    private boolean previousForceScrollY;
    private boolean previousOverScrollX;
    private boolean previousOverScrollY;
    private float previousOverScrollDistance;
    private float previousOverScrollSpeedMin;
    private float previousOverScrollSpeedMax;
    private boolean previousScrollBarBottom;
    private boolean previousScrollBarRight;
    private boolean previousScrollBarsOnTop;
    private boolean previousScrollBarsVisible;
    private boolean previousScrollBarTouch;
    private boolean previousScrollingDisabledX;
    private boolean previousScrollingDisabledY;
    private boolean previousSmoothScrolling;
    private boolean previousVariableSizeKnobs;
    
    public ScrollPaneResetUndoable() {
        dialog = DialogSceneComposer.dialog;
        scrollPane = (DialogSceneComposerModel.SimScrollPane) dialog.simActor;
        previousName = scrollPane.name;
        previousStyle = scrollPane.style;
        previousFadeScrollBars = scrollPane.fadeScrollBars;
        previousChild = scrollPane.child;
        previousClamp = scrollPane.clamp;
        previousFlickScroll = scrollPane.flickScroll;
        previousFlingTime = scrollPane.flingTime;
        previousForceScrollX = scrollPane.forceScrollX;
        previousForceScrollY = scrollPane.forceScrollY;
        previousOverScrollX = scrollPane.overScrollX;
        previousOverScrollY = scrollPane.overScrollY;
        previousOverScrollDistance = scrollPane.overScrollDistance;
        previousOverScrollSpeedMin = scrollPane.overScrollSpeedMin;
        previousOverScrollSpeedMax = scrollPane.overScrollSpeedMax;
        previousScrollBarBottom = scrollPane.scrollBarBottom;
        previousScrollBarRight = scrollPane.scrollBarRight;
        previousScrollBarsOnTop = scrollPane.scrollBarsOnTop;
        previousScrollBarsVisible = scrollPane.scrollBarsVisible;
        previousScrollBarTouch = scrollPane.scrollBarTouch;
        previousScrollingDisabledX = scrollPane.scrollingDisabledX;
        previousScrollingDisabledY = scrollPane.scrollingDisabledY;
        previousSmoothScrolling = scrollPane.smoothScrolling;
        previousVariableSizeKnobs = scrollPane.variableSizeKnobs;
    }
    
    @Override
    public void undo() {
        scrollPane.name = previousName;
        scrollPane.style = previousStyle;
        scrollPane.fadeScrollBars = previousFadeScrollBars;
        scrollPane.child = previousChild;
        scrollPane.clamp = previousClamp;
        scrollPane.flickScroll = previousFlickScroll;
        scrollPane.flingTime = previousFlingTime;
        scrollPane.forceScrollX = previousForceScrollX;
        scrollPane.forceScrollY = previousForceScrollY;
        scrollPane.overScrollX = previousOverScrollX;
        scrollPane.overScrollY = previousOverScrollY;
        scrollPane.overScrollDistance = previousOverScrollDistance;
        scrollPane.overScrollSpeedMin = previousOverScrollSpeedMin;
        scrollPane.overScrollSpeedMax = previousOverScrollSpeedMax;
        scrollPane.scrollBarBottom = previousScrollBarBottom;
        scrollPane.scrollBarRight = previousScrollBarRight;
        scrollPane.scrollBarsOnTop = previousScrollBarsOnTop;
        scrollPane.scrollBarsVisible = previousScrollBarsVisible;
        scrollPane.scrollBarTouch = previousScrollBarTouch;
        scrollPane.scrollingDisabledX = previousScrollingDisabledX;
        scrollPane.scrollingDisabledY = previousScrollingDisabledY;
        scrollPane.smoothScrolling = previousSmoothScrolling;
        scrollPane.variableSizeKnobs = previousVariableSizeKnobs;
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        scrollPane.reset();
        
        if (dialog.simActor != scrollPane) {
            dialog.simActor = scrollPane;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Reset ScrollPane\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Reset ScrollPane\"";
    }
}
