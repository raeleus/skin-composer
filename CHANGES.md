### Skin Composer Version 34 ###
**Please use TenPatch 5.0.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Fixed save files with Ten Patches accidentally saving TextureRegions, causing a crash on load.
* Fixed content padding not showing in main preview for Ten Patches.
* Fixed export of PlayMode for animated Ten Patches.

### Skin Composer Version 33 ###
**Please use TenPatch 4.2.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Added PlayMode option to Ten Patch animations. Use this to single play, loop, reverse, and randomize your animations.
* Added option to duplicate an existing Ten Patch in the Drawables dialog.
* Allow copy/paste of Ten Patch data by pressing Ctrl+C/Ctrl+V respectively in the TenPatchDialog.
* Added option for preview background color in Ten Patch Animation dialog.
* Changed keyboard shortcut for moving frames to left/right arrow. No longer necessary to hold control.
* Improved performance of TenPatches when rebuilding the TextureAtlas in the Drawable dialogs and in the main menu.
* Fixed dark preview of texture and staggered scrolling at slow speeds in Ten Patch Dialog.
* Fixed keyboard shortcuts not working in Ten Patch Animation dialog.
* Fixed Ten Patch export not setting default stretch regions if they are not specified in the editor.
* Fixed renaming colors causing Ten Patches to break.
* Fixed deleting drawables causing Ten Patches to break.
* Minor UI Tweaks and bug fixes.

### Skin Composer Version 32 ###
**Please use TenPatch 4.1.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Fixed keyboard focus issue in Ten Patch Animation dialog.
* Minor UI Tweaks and bug fixes.

### Skin Composer Version 31 ###
**Please use TenPatch 4.1.0 with your projects to enable TenPatch functionality:** https://github.com/raeleus/TenPatch

* Added gradient and offset options for scrolling background effects to TenPatch. See "More settings..." in the editor.
* Added animation editor for TenPatch.
* Added number labels for the TenPatch editor handles.
* Added right click panning and double click content/padding shortcut to NinePatch editor for parity with the TenPatch editor.
* Fixed TenPatchDrawable content padding not being displayed in the preview.
* Added filter option for TenPatches in the drawables dialog.
* TenPatches with no stretch areas now preview correctly outside of the editor.
* Pressing escape in drawables dialog disables the filter.
* Prevent small fonts from crashing FreeType and Bitmap Font dialogs.
* Prevent crash when creating a font that requires more than one texture page in Bitmap Font dialog.
* Fixed being unable to set content if handles are squished to the far right or bottom in TenPatch editor.
* Fixed being able to make a new stretch area while in content mode in Ten Patch.
* Minor UI Tweaks and bug fixes.

### Skin Composer Version 30 ###

* Integrated TenPatch functionality as an alternative to 9patch. See https://github.com/raeleus/TenPatch
* The last zoom level of the drawables dialog will be persisted to the next time you open it.
* Allow for drag and drop of JSON files into import dialog.
* minWidth and minHeight values are now correctly exported for Tinted drawables
* minWidth and minHeight values are now imported from JSON files.
* Added dialog to fix the minWidth and minHeight settings of drawables when opening old projects.
* Added tooltips when hovering over items in the preview for custom classes.
* Added option to change preview texture packer settings. See Project >> Settings.
* Fixed nine patch editor not showing drawables when selected from Content in Preview area.
* Added wrapping to Style Property entries to prevent overflow.
* Fixed character filtering in Spinner so that negative values can be entered properly.
* Fixed losing focus when typing a new value for number fields in Style Properties.
* Various UI tweaks and bug fixes.
* Source updated to JDK 11

### Skin Composer Version 29 ###

* Improved Windows distribution with single file EXE
* Added option to set minWidth and minHeight of any drawable.
* Fixed issues with pressed, unpressed, and checked offsets in buttons.

### Skin Composer Version 28 ###
* BREAKING CHANGE: Bitmap/Freetype Font settings files may not be compatible with this version of Skin Composer unless converted to UTF-8.

* Skin Composer now has a wiki: https://github.com/raeleus/skin-composer/wiki
* Resolved release issues with Mac and Windows.
* Dialogs now highlight which field needs to be completed in order to continue.
* Added option to not show warnings when exporting a skin.
* Added option to import/export colors in a skin JSON as hexadecimal.
* Default export path is now a per project setting to prevent unfortunate accidents.
* Allow loading characters from a text file (UTF-8) in Font dialogs. This allows for any Unicode characters to be included in your fonts.
* Added detail view to Drawables dialog.
* Improved Image Font dialog's calculation of default baseline.
* Fixed pressing enter while typing in the preview dismisses the Image Font dialog.
* Fixed Create Bitmap Font dialog not closing file stream.
* Fixed errors when creating new font that overrides existing drawables.
* Fixed errors when opening a project with missing assets on Mac.
* Fixed unable to import FreeType fonts when importing a skin.
* Fixed filter error in native dialogs on Mac.
* Changes to texture packer settings no longer influence internal rendering of assets. This prevents "useIndexes" from crashing skin composer.
* Minor bug fixes and interface improvements.

### Skin Composer Version 27 ###
* Added filter option for Drawables dialog. Click the filter button or type text directly in the dialog.
* Allow drag and drop of folders into Drawables and Fonts dialogs.
* Fixed crash bug when editing the preview in Nine Patch Editor without an image loaded.
* Fixed crash when erasing a value in the Nine Patch Editor.
* Fixed preview properties for SelectBox: alignment and max items count.
* Fixed issue with TextField messageFontColor not working.
* Various formatting fixes.

### Skin Composer Version 26 ###
* Fixed export creating JSON with null values for Styles that have parents
* Allow drag and drop of SCMP files into main screen to load project.
* Created export dialog and moved export settings to this window.
* Created import dialog with options to import into a new project or the current one.
* Added options to not generate a texture atlas and to not copy font FNT/TTF files on export.
* Improved appearance of Settings dialog.
* Updated Tiny File Dialogs to 3.2.0. Resolves #45
* Added Style property type to Custom Classes. Resolves #46
* Fixed crashes when changing a custom style property's type.

### Skin Composer Version 25 ###
* Fixed exception when opening an old project.
* Improved error message when trying to add a font that is too large for the texture settings.

### Skin Composer Version 24 ###
* Updated to LibGDX 1.9.9.
* Added setting for export with simple names per 1.9.9. See Project > Settings.
* Allows import of 1.9.9 skins with simple names.
* Added a parent setting to each style to support 1.9.9 cascading styles. Selectable parent options respects class inheritance.
* Resolved issue with Create Bitmap Font dialog creating fonts with incorrect baseline values.
* Resolved drag and drop broken after using Create Bitmap Font dialog.
* Added drag and drop to Create Bitmap Font and Freetype Font dialogs.
* Fixed SplitPane preview not allowing for adjustments.
* Added indent spacing and tied missing properties to the Tree preview.
* Prioritized nine patches over standard graphics when images of the same name are dragged into the Drawables dialog.
* Auto zoom and recenter in Nine Patch Editor now considers width in addition to height.
* Resolved auto patches not functioning correctly for fully opaque, single-colored images in Nine Patch Editor.
* Added save/load settings for bitmap font and Freetype Font dialogs.
* Added check for preview fonts upon project load.
* Added option to change preview background color in Nine Patch Editor.
* Added option to change preview background to bitmap font and Freetype font dialogs.
* Improved appearance of handles in Nine Patch Editor when zoomed out.
* Fixed crash when trying to move handles in Nine Patch Editor when no image is loaded.
* Resolved update button disappearing when a new project is started.
* Replacing a drawable with a new drawable no longer breaks the link with styles using the original.
* Proper replacement of custom drawables with a standard drawable.
* Replacing a drawable no longer deletes tinted drawables based on that file.
* Fixed crash when viewing fonts with multiple images after importing skin on an unsaved, new project.
* Added UI support for ISO-8859-1: Western European and ISO-8859-2: Central European.
* Fixed characters select box value incorrectly set when editing a Freetype font.
* Fixed "duplicate custom class" creating an extra default style.
* Minor UI fixes.

### Skin Composer Version 23 ###
* Added 9-Patch Editor. See Project >> Drawables >> Create 9-Patch.
* Fixed crash bug when adding a drawable after creating a custom drawable.
* Fixed crash bug when saving a project with relative paths and a FreeType font with no custom serializer.

### Skin Composer Version 22 ###
* Added setting to check for updates. Update button appears on top right of main screen.
* Added dialog to create Bitmap Fonts as an alternative to Hiero.
* Fixed imported skin files having darkened transparent images.
* Added preview bg color setting for Image Font dialog.
* Temporary files and program settings are now saved in folder ".skincomposer" of the user's home directory.
* Added Windows installer and file associations for ".scmp" files.
* Improved layout and applied fixes to FreeType dialog.

### Skin Composer Version 21 ###
* Fixed blending issue causing white fonts to have dark outlines in Image Font.
* Automatically suggest target file name in Image Font.
* Fixed SplashScreen.

### Skin Composer Version 20 ###
* Upgraded to JDK 10
* Added ImageFont dialog to create Bitmap Fonts from images designed in an image editing software.
* Updated UI to use FreeType font.
* Fixed FreeType fonts not loading correctly from SCMP file.
* Fixed FreeType font selections in custom classes not saving with SCMP file.
* Fixed editing FreeType fonts removes them from custom classes.
* New executable releases are packaged by JavaPackager and JLink

### Skin Composer Version 19 ###
* Fixed locale error causing crash bug in the FreeType dialog.
* Fixed crash with font in custom class when rendering table.
* Fixed export not acknowledging custom class with font set to a FreeType font.

### Skin Composer Version 18 ###
* Added FreeType font option. Users can generate their own fonts or build their fonts in the editor.
* Added option to export custom classes before or after the standard UI classes are defined.

### Skin Composer Version 17 ###
* Added a hexadecimal field to the color picker.
* Added custom drawable button to drawables dialog. Use this to refer to your classes that inherit from Drawable specified in the new custom class dialog.
* Fixed alpha slider being initialized with the incorrect color in color picker.
* Fixed keyboard shortcuts being incorrectly configured on alternative keyboard layouts.
* Fixed crash bug on Mac.
* Fixed spinner not accepting numbers starting with a decimal point.
* Fixed removing a custom property does not remove it from new custom styles.
* Reordered JSON export so that custom classes can be implemented by styles.

### Skin Composer Version 16 ###
* Added Ctrl+E shortcut for export.
* Added F5 shortcut for refreshing the texture atlas.
* Moved refresh texture atlas option to project menu.
* Added TiledDrawable functionality. See Project >> Drawables.

### Skin Composer Version 15 ###
* Added Export Format option to allow the user to select between Minimal, JavaScript, and JSON output types when exporting a skin JSON. See Project >> Settings.

### Skin Composer Version 14 ###
* Added "Raw Text" type for custom class properties to allow manual typing of arrays and objects in JSON format. Ex. ["banana", "strawberry", "grape"]
* Allow import of objects and arrays in custom classes as "Raw Text" types.
* Added a splash screen to show while Java is loading.
* Added support for Spanish characters: ñ, ¿, ¡, á, é, í, ó, ú, ü.
* All JSON exports are now encoded in UTF-8.

### Skin Composer Version 13 ###
* Fixed crashes in Progress Bar and Slider when changing spinner values to invalid values.
* Added prompt for user to browse for missing files when opening a project.

### Skin Composer Version 12 ###
* Fixed font dialog not allowing import of fonts larger than 1024x1024
* Possible fix for Mac version

### Skin Composer Version 11 ###
* Fixed unable to hide welcome screen setting
* Enforce using "_data" folder when "Keep resources relative" is selected. Solves #20
* Fixed alignment property of the TextField preview having no effect.
* Fixed renaming a style selects a different style afterwards
* Fixed wrong warnings when exporting imported skins

### Skin Composer Version 10 ###
* Added Welcome Screen with templates and sample files to load.
* Added a warnings dialog to alert the user with what failed to load in import/export. Can be exported to text file.
* stageBackground for Window class is now properly previewed in the preview pane.
* Various bug fixes.

### Skin Composer Version 9 ###
* Added a pseudo preview for custom classes. Drawables, fonts, colors, etc. are laid out one after the other.
* Solved dialog file filter issue on OSX affecting Open and Import.
* Can import existing VisUI skins without crashing.
* Sort selection is persistent in Drawables dialog.
* Various bug fixes.

### Skin Composer Version 8 ###
* Added "Custom Class" option to include styles for classes not part of Scene2D.ui. Press the "plus" button next to the class select box.
* Defaults for exported texture atlasses are now editable via a defaults.json file. See "Project >> Settings >> Open texture packer settings file".
* File resources (Drawables and Fonts) can be kept relative to the save file. See "Project >> Settings >> Keep resources relative?".
* Fixed errors with default paths for drawables and fonts.
* Added tooltips to buttons as necessary.

### Skin Composer Version 7 ###
* Switched to TinyFileDialogs for all file dialogs across all platforms.
* Updated user interface to Skin Composer UI.
* Recent files menu option.
* Refactored all of the interface code to allow for new features.
* Multiple bug fixes and improvements.