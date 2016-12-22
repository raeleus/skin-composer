package com.ray3k.skincomposer;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.panel.PanelMenuBar;

public class UndoableManager {
    private final Array<Undoable> undoables;
    private int undoIndex;
    private ProjectData projectData;

    public UndoableManager(ProjectData projectData) {
        undoables = new Array<>();
        undoIndex = -1;
        this.projectData = projectData;
    }
    
    public void clearUndoables() {
        undoables.clear();
        undoIndex = -1;
        
//        PanelMenuBar.instance().getUndoButton().setDisabled(true);
//        PanelMenuBar.instance().getUndoButton().setText("Undo");
//        
//        PanelMenuBar.instance().getRedoButton().setDisabled(true);
//        PanelMenuBar.instance().getRedoButton().setText("Redo");
    }
    
    public void undo() {
        if (undoIndex >= 0 && undoIndex < undoables.size) {
            projectData.setChangesSaved(false);
            Undoable undoable = undoables.get(undoIndex);
            undoable.undo();
            undoIndex--;

            if (undoIndex < 0) {
//                PanelMenuBar.instance().getUndoButton().setDisabled(true);
//                PanelMenuBar.instance().getUndoButton().setText("Undo");
            } else {
//                PanelMenuBar.instance().getUndoButton().setText("Undo " + undoables.get(undoIndex).getUndoText());
            }

//            PanelMenuBar.instance().getRedoButton().setDisabled(false);
//            PanelMenuBar.instance().getRedoButton().setText("Redo " + undoable.getUndoText());
        }
    }
    
    public void redo() {
        if (undoIndex >= -1 && undoIndex < undoables.size) {
            projectData.setChangesSaved(false);
            if (undoIndex < undoables.size - 1) {
                undoIndex++;
                undoables.get(undoIndex).redo();
            }

            if (undoIndex >= undoables.size - 1) {
//                PanelMenuBar.instance().getRedoButton().setDisabled(true);
//                PanelMenuBar.instance().getRedoButton().setText("Redo");
            } else {
//                PanelMenuBar.instance().getRedoButton().setText("Redo " + undoables.get(undoIndex + 1).getUndoText());
            }

//            PanelMenuBar.instance().getUndoButton().setDisabled(false);
//            PanelMenuBar.instance().getUndoButton().setText("Undo " + undoables.get(undoIndex).getUndoText());
        }
    }
    
    public void addUndoable(Undoable undoable, boolean redoImmediately) {
        projectData.setChangesSaved(false);
        undoIndex++;
        if (undoIndex <= undoables.size - 1) {
            undoables.removeRange(undoIndex, undoables.size - 1);
        }
        undoables.add(undoable);
        
        if (redoImmediately) {
            undoable.redo();
        }
        
//        PanelMenuBar.instance().getRedoButton().setDisabled(true);
//        PanelMenuBar.instance().getRedoButton().setText("Redo");
//        PanelMenuBar.instance().getUndoButton().setDisabled(false);
//        PanelMenuBar.instance().getUndoButton().setText("Undo " + undoable.getUndoText());
        
        if (undoables.size > projectData.getMaxUndos()) {
            int offset = undoables.size - projectData.getMaxUndos();
            
            undoIndex -= offset;
            undoIndex = MathUtils.clamp(undoIndex, -1, undoables.size - 1);
            undoables.removeRange(0, offset - 1);
        }
    }
    
    public void addUndoable(Undoable undoable) {
        addUndoable(undoable, false);
    }
}
