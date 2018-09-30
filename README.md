# README #

This project is programmed in Java and depends on LibGDX and the LWJGL3 backend.

### Skin Composer ###

* Use Skin Composer to create skins for scene2d.ui
* Version 23

### Changes ###

* Added 9-Patch Editor. See Project >> Drawables >> Create 9-Patch.
* Fixed crash bug when adding a drawable after creating a custom drawable.
* Fixed crash bug when saving a project with relative paths and a FreeType font with no custom serializer.

### Contact ###

* This project is maintained by Raymond "Raeleus" Buckley
* http://ray3k.wordpress.com
* raymond.ray3k (at) gmail.com

### Notes ###

To run Skin Composer on OSX, use the executable Mac release. If that fails, download the skin_composer_mac.jar version of the app and run it via terminal with the following command: **java -XstartOnFirstThread -jar skin_composer_mac.jar**

To run/build the project in Idea/Android Studio, do the following:

* In 'File/Project Structure...' set the project language level to 10
* Update run configuration with the following VM Option: -XstartOnFirstThread

Thanks to contributer RaimundWege for this information.

### License ###
MIT License

Copyright (c) 2018 Raymond Buckley

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
