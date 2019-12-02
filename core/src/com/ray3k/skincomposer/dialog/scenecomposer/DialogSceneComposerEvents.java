package com.ray3k.skincomposer.dialog.scenecomposer;

import com.badlogic.gdx.files.FileHandle;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.ColorData;
import com.ray3k.skincomposer.data.DrawableData;
import com.ray3k.skincomposer.data.StyleData;
import com.ray3k.skincomposer.dialog.scenecomposer.undoables.*;

public class DialogSceneComposerEvents {
    public enum WidgetType {
        BUTTON, CHECK_BOX, IMAGE, IMAGE_BUTTON, IMAGE_TEXT_BUTTON, LABEL, LIST, PROGRESS_BAR, SELECT_BOX, SLIDER,
        TEXT_BUTTON, TEXT_FIELD, TEXT_AREA, TOUCH_PAD, CONTAINER, HORIZONTAL_GROUP, SCROLL_PANE, STACK, SPLIT_PANE,
        TABLE, TREE, VERTICAL_GROUP
    }
    
    private DialogSceneComposer dialog;
    
    public DialogSceneComposerEvents() {
        this.dialog = DialogSceneComposer.dialog;
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
        dialog.updateMenuUndoRedo();
    }
    
    public void menuRedo() {
        dialog.model.redo();
        dialog.updateMenuUndoRedo();
    }
    
    public void menuView(DialogSceneComposer.View view) {
        dialog.view = view;
        dialog.updateMenuView();
    }
    
    public void menuHelp() {
        dialog.showHelpDialog();
    }
    
    public void dialogImportImportTemplate(FileHandle loadFile) {
        Main.main.getProjectData().setLastImportExportPath(loadFile.path());
        DialogSceneComposerModel.loadFromJson(loadFile);
        dialog.model.updatePreview();
        dialog.populatePath();
        dialog.populateProperties();
    }
    
    public void dialogExportSaveTemplate(FileHandle saveFile) {
        Main.main.getProjectData().setLastImportExportPath(saveFile.path());
        DialogSceneComposerModel.saveToJson(saveFile);
    }
    
    public void dialogExportSaveJava(FileHandle saveFile) {
        Main.main.getProjectData().setLastImportExportPath(saveFile.path());
    }
    
    public void dialogExportClipboard() {
    
    }
    
    public void dialogSettingsPackage(String packageName) {
    
    }
    
    public void dialogSettingsClass(String className) {
    
    }
    
    public void dialogSettingsSkinPath(String skinPath) {
    
    }
    
    public void dialogSettingsBackgroundColor(ColorData color) {
    
    }
    
    private void processUndoable(SceneComposerUndoable undoable) {
        dialog.model.undoables.add(undoable);
        dialog.model.redoables.clear();
        undoable.redo();
        dialog.updateMenuUndoRedo();
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
    
    public void tableDelete() {
        processUndoable(new TableDeleteUndoable());
    }
    
    public void cellSetWidget(WidgetType widgetType) {
        processUndoable(new CellSetWidgetUndoable(widgetType));
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
    
    public void cellColSpan(int colSpan) {
        processUndoable(new CellColSpanUndoable(colSpan));
    }
    
    public void cellReset() {
        processUndoable(new CellResetUndoable());
    }
    
    public void cellDelete() {
        processUndoable(new CellDeleteUndoable());
    }
    
    public void buttonName(String name) {
        processUndoable(new ButtonNameUndoable(name));
    }
    
    public void buttonStyle(StyleData style) {
        processUndoable(new ButtonStyleUndoable(style));
    }
    
    public void buttonChecked(boolean checked) {
        processUndoable(new ButtonCheckedUndoable(checked));
    }
    
    public void buttonDisabled(boolean disabled) {
        processUndoable(new ButtonDisabledUndoable(disabled));
    }
    
    public void buttonColor(ColorData colorData) {
        processUndoable(new ButtonColorUndoable(colorData));
    }
    
    public void buttonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new ButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void buttonReset() {
        processUndoable(new ButtonResetUndoable());
    }
    
    public void buttonDelete() {
        processUndoable(new ButtonDeleteUndoable());
    }
    
    public void imageButtonName(String name) {
        processUndoable(new ImageButtonNameUndoable(name));
    }
    
    public void imageButtonStyle(StyleData style) {
        processUndoable(new ImageButtonStyleUndoable(style));
    }
    
    public void imageButtonChecked(boolean checked) {
        processUndoable(new ImageButtonCheckedUndoable(checked));
    }
    
    public void imageButtonDisabled(boolean disabled) {
        processUndoable(new ImageButtonDisabledUndoable(disabled));
    }
    
    public void imageButtonColor(ColorData colorData) {
        processUndoable(new ImageButtonColorUndoable(colorData));
    }
    
    public void imageButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new ImageButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void imageButtonReset() {
        processUndoable(new ImageButtonResetUndoable());
    }
    
    public void imageButtonDelete() {
        processUndoable(new ImageButtonDeleteUndoable());
    }
    
    public void imageTextButtonName(String name) {
        processUndoable(new ImageTextButtonNameUndoable(name));
    }
    
    public void imageTextButtonText(String text) {
        processUndoable(new ImageTextButtonTextUndoable(text));
    }
    
    public void imageTextButtonStyle(StyleData style) {
        processUndoable(new ImageTextButtonStyleUndoable(style));
    }
    
    public void imageTextButtonChecked(boolean checked) {
        processUndoable(new ImageTextButtonCheckedUndoable(checked));
    }
    
    public void imageTextButtonDisabled(boolean disabled) {
        processUndoable(new ImageTextButtonDisabledUndoable(disabled));
    }
    
    public void imageTextButtonColor(ColorData colorData) {
        processUndoable(new ImageTextButtonColorUndoable(colorData));
    }
    
    public void imageTextButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new ImageTextButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void imageTextButtonReset() {
        processUndoable(new ImageTextButtonResetUndoable());
    }
    
    public void imageTextButtonDelete() {
        processUndoable(new ImageTextButtonDeleteUndoable());
    }
    
    public void textButtonName(String name) {
        processUndoable(new TextButtonNameUndoable(name));
    }
    
    public void textButtonText(String text) {
        processUndoable(new TextButtonTextUndoable(text));
    }
    
    public void textButtonStyle(StyleData style) {
        processUndoable(new TextButtonStyleUndoable(style));
    }
    
    public void textButtonChecked(boolean checked) {
        processUndoable(new TextButtonCheckedUndoable(checked));
    }
    
    public void textButtonDisabled(boolean disabled) {
        processUndoable(new TextButtonDisabledUndoable(disabled));
    }
    
    public void textButtonColor(ColorData colorData) {
        processUndoable(new TextButtonColorUndoable(colorData));
    }
    
    public void textButtonPadding(float paddingLeft, float paddingRight, float paddingTop, float paddingBottom) {
        processUndoable(new TextButtonPaddingUndoable(paddingLeft, paddingRight, paddingTop, paddingBottom));
    }
    
    public void textButtonReset() {
        processUndoable(new TextButtonResetUndoable());
    }
    
    public void textButtonDelete() {
        processUndoable(new TextButtonDeleteUndoable());
    }
}
