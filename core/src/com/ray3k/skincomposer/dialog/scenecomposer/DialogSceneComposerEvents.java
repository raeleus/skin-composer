package com.ray3k.skincomposer.dialog.scenecomposer;

import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.*;

public class DialogSceneComposerEvents {
    public enum WidgetType {
        BUTTON, CHECK_BOX, IMAGE, IMAGE_BUTTON, IMAGE_TEXT_BUTTON, LABEL, LIST, PROGRESS_BAR, SELECT_BOX, SLIDER,
        TEXT_BUTTON, TEXT_FIELD, TEXT_AREA, TOUCH_PAD, CONTAINER, HORIZONTAL_GROUP, SCROLL_PANE, STACK, SPLIT_PANE,
        TABLE, TREE, VERTICAL_GROUP
    }
    
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
        var undoable = new MenuClearUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
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
    
    public void rootAddTable(int columns, int rows) {
        var undoable = new RootAddTableUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void tableName(String name) {
        var undoable = new TableNameUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void tableBackground(DrawableData background) {
        var undoable = new TableBackgroundUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void tableColor(ColorData color) {
        var undoable = new TableColorUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void tablePadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        var undoable = new TablePaddingUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void tableAlignment(int alignment) {
        var undoable = new TableAlignmentUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void tableReset() {
        var undoable = new TableResetUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellAddWidget(WidgetType widgetType) {
        var undoable = new CellAddWidgetUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellAddCellToLeft() {
        var undoable = new CellAddWidgetUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellAddCellToRight() {
        var undoable = new CellAddCellToRightUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellAddRowAbove() {
        var undoable = new CellAddRowAboveUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellAddRowBelow() {
        var undoable = new CellAddRowBelowUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellPaddingSpacing(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, float spacingLeft, float spacingRight, float spacingTop, float spacingBottom) {
        var undoable = new CellPaddingSpacingUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellExpandFillGrow(boolean expandX, boolean expandY, boolean fillX, boolean fillY, boolean growX, boolean growY) {
        var undoable = new CellExpandFillGrowUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellAlignment(int alignment) {
        var undoable = new CellAlignmentUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellSize(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
        var undoable = new CellSizeUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellUniform(boolean uniformX, boolean uniformY) {
        var undoable = new CellUniformUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellReset() {
        var undoable = new CellResetUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void cellDelete() {
        var undoable = new CellDeleteUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonName(String name) {
        var undoable = new TextButtonNameUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonText(String text) {
        var undoable = new TextButtonTextUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonStyle(StyleData styleData) {
        var undoable = new TextButtonStyleUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonChecked(boolean checked) {
        var undoable = new TextButtonCheckedUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonDisabled(boolean disabled) {
        var undoable = new TextButtonAlignUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonColor(ColorData colorData) {
        var undoable = new TextButtonColorUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        var undoable = new TextButtonPaddingUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonAlign(int alignment) {
        var undoable = new TextButtonAlignUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
    
    public void textButtonReset() {
        var undoable = new TextButtonResetUndoable();
        dialog.model.undoables.add(undoable);
        undoable.redo();
    }
}
