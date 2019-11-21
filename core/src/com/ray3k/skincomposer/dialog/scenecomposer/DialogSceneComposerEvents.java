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
        processUndoable(new MenuClearUndoable());
    }
    
    public void menuUndo() {
        dialog.model.undo();
        dialog.updateUndoRedo();
    }
    
    public void menuRedo() {
        dialog.model.redo();
        dialog.updateUndoRedo();
    }
    
    public void menuMode() {
    
    }
    
    public void menuHelp() {
        dialog.showHelpDialog();
    }
    
    private void processUndoable(SceneComposerUndoable undoable) {
        dialog.model.undoables.add(undoable);
        dialog.model.redoables.clear();
        undoable.redo();
        dialog.updateUndoRedo();
    }
    
    public void rootAddTable(int columns, int rows) {
        processUndoable(new RootAddTableUndoable(columns, rows));
    }
    
    public void tableName(String name) {
        processUndoable(new TableNameUndoable(name));
    }
    
    public void tableBackground(DrawableData background) {
        processUndoable(new TableBackgroundUndoable(background));
    }
    
    public void tableColor(ColorData color) {
        processUndoable(new TableColorUndoable(color));
    }
    
    public void tablePadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new TablePaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void tableAlignment(int alignment) {
        processUndoable(new TableAlignmentUndoable(alignment));
    }
    
    public void tableReset() {
        processUndoable(new TableResetUndoable());
    }
    
    public void cellAddWidget(WidgetType widgetType) {
        processUndoable(new CellAddWidgetUndoable(widgetType));
    }
    
    public void cellAddCellToLeft() {
        processUndoable(new CellAddCellToLeftUndoable());
    }
    
    public void cellAddCellToRight() {
        processUndoable(new CellAddCellToRightUndoable());
    }
    
    public void cellAddRowAbove() {
        processUndoable(new CellAddRowAboveUndoable());
    }
    
    public void cellAddRowBelow() {
        processUndoable(new CellAddRowBelowUndoable());
    }
    
    public void cellPaddingSpacing(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom, float spaceLeft, float spaceRight, float spaceTop, float spaceBottom) {
        processUndoable(new CellPaddingSpacingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom, spaceLeft, spaceRight, spaceTop, spaceBottom));
    }
    
    public void cellExpandFillGrow(boolean expandX, boolean expandY, boolean fillX, boolean fillY, boolean growX, boolean growY) {
        processUndoable(new CellExpandFillGrowUndoable(expandX, expandY, fillX, fillY, growX, growY));
    }
    
    public void cellAlignment(int alignment) {
        processUndoable(new CellAlignmentUndoable(alignment));
    }
    
    public void cellSize(float minWidth, float minHeight, float maxWidth, float maxHeight, float preferredWidth, float preferredHeight) {
        processUndoable(new CellSizeUndoable(minWidth, minHeight, maxWidth, maxHeight, preferredWidth, preferredHeight));
    }
    
    public void cellUniform(boolean uniformX, boolean uniformY) {
        processUndoable(new CellUniformUndoable(uniformX, uniformY));
    }
    
    public void cellReset() {
        processUndoable(new CellResetUndoable());
    }
    
    public void cellDelete() {
        processUndoable(new CellDeleteUndoable());
    }
    
    public void textButtonName(String name) {
        processUndoable(new TextButtonNameUndoable());
    }
    
    public void textButtonText(String text) {
        processUndoable(new TextButtonTextUndoable());
    }
    
    public void textButtonStyle(StyleData styleData) {
        processUndoable(new TextButtonStyleUndoable());
    }
    
    public void textButtonChecked(boolean checked) {
        processUndoable(new TextButtonCheckedUndoable());
    }
    
    public void textButtonDisabled(boolean disabled) {
        processUndoable(new TextButtonDisabledUndoable());
    }
    
    public void textButtonColor(ColorData colorData) {
        processUndoable(new TextButtonColorUndoable());
    }
    
    public void textButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new TextButtonPaddingUndoable());
    }
    
    public void textButtonAlign(int alignment) {
        processUndoable(new TextButtonAlignUndoable());
    }
    
    public void textButtonReset() {
        processUndoable(new TextButtonResetUndoable());
    }
}
