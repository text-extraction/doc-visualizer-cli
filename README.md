# PDF Drawer

A simple tool that can be used to add drawings (i.e., rectangles, lines and circles) and texts to existing PDF files.

## Installation
Clone this repository by typing 
```bash
git clone https://ad-git.informatik.uni-freiburg.de/ck1028/pdf-drawer.git
cd pdf-drawer
```
To use the tool, you can either directly use `bin/pdf-drawer-0.1.jar` or build the JAR-file on your own by typing: 
```bash
mvn install
```

## Usage
The basic usage of the JAR file is as follows:

```bash
java -jar bin/pdf-drawer-0.1.jar -i <input-PDF> -o <output-PDF> -j <instructions-file> 
```

where

* `<input-PDF>` is the path to the PDF file to which the drawings should be added,
* `<output-PDF>` is the path to the file to which the PDF with the added drawings should be stored,
* `<instructions-file>` is the path to a TSV file containing *instructions* (one per line) that define which drawings should be added to which position in the PDF.
The syntax of an instruction depends on the type of drawing you want to add to the PDF:
  * **Rectangle**: The syntax for adding a rectangle to the PDF is as follows (the first line is a comment line providing the names of the fields of the instruction and doesn't belong to any instruction):
    ```bash
    # type  pageNum  minX  minY  maxX   maxY   borderWidth  borderColor  borderOpacity  fillingColor  fillingOpacity
    R       1        87.4  65.6  198.0  654.8  5            red          1              blue          0.5
    ```
    * The **type** stands for the type of drawing you want to add. Allowed values are: *R*, *RECT* and *RECTANGLE* (and the lowercase equivalents). **Required**.
    * The **page number** defines the page to which you want to add the rectangle (Note: the page numbers are 1-based). **Required**.
    * **minX**, **minY** are the x/y-coordinates of the lower left corner of the rectangle you want to add. **Required**.
    * **maxX**, **maxY** are the x/y-coordinates of the upper right corner of the rectangle you want to add. **Required**.
    * The **border width** is the width of the border of the rectangle. **Optional**, default: *1*.
    * The **border color** is the color of the border of the rectangle. You can use human-readable color names (e.g., *black*, *red*, *green*, etc.); an RGB color specification in the form *rgb(R,G,B)* (with the three values R, G and B in the range [0, 255]); or the keyword *random* to use a random color. **Optional**; default: *black*.
    * The **border opacity** is the opacity of the border of the rectangle, given as a value between 0 and 1 (where 0 means *invisible* and *1* means *totally visible*). **Optional**; default: *1*. 
    * The **filling color** is the color of the filling of the rectangle. **Optional**; default: not set (= no filling).
    * The **filling opacity** the opacity of the filling of the rectangle, given as a value between 0 and 1 (where 0 means *invisible* and *1* means *totally visible*). **Optional**; default: *1*.
    
    The first 6 fields are required, all other fields are optional and can be omitted (instead, the provided default values are used).
  
  * **Line**: The syntax for adding a line to the PDF is as follows (again, the first line is a comment line providing the names of the fields of the instruction and doesn't belong to any instruction):
      ```bash
      # type  pageNum  x0    y0    x1     y1     width color opacity
      L       1        87.4  65.6  198.0  654.8  5     red   1
      ```
      * The **type** stands for the type of drawing you want to add. Allowed values are: *L* and *LINE* (and the lowercase equivalents). **Required**.
      * The **page number** defines the page to which you want to add the line (Note: the page numbers are 1-based). **Required**.
      * **x0**, **y0** are the x/y-coordinates of the start point of the line you want to add. **Required**.
      * **x1**, **y1** are the x/y-coordinates of the end point of the line you want to add. **Required**.
      * The **width** is the width of the line. **Optional**, default: *1*.
      * The **color** is the color of the line. You can use human-readable color names (e.g., *black*, *red*, *green*, etc.); an RGB color specification in the form *rgb(R,G,B)* (with the three values R, G and B in the range [0, 255]); or the keyword *random* to use a random color. **Optional**; default: *black*.
      * The **opacity** is the opacity of the line, given as a value between 0 and 1 (where 0 means *invisible* and *1* means *totally visible*). **Optional**; default: *1*. 
      
      The first 6 fields are required, all other fields are optional and can be omitted (instead, the provided default values are used).

  * **Circle**: The syntax for adding a circle to the PDF is as follows (again, the first line is a comment line and doesn't belong to any instruction):
    ```bash
    # type  pageNum  x      y      radius  borderWidth  borderColor  borderOpacity  fillingColor  fillingOpacity
    C       3        287.4  265.6  5.0     2            red          1              black         0.5
    ```
    * The **type** stands for the type of drawing you want to add. Allowed values are: *C*, and *CIRCLE* (and the lowercase equivalents). **Required**.
    * The **page number** defines the page to which you want to add the circle (Note: the page numbers are 1-based). **Required**.
    * **x**, **y** are the x/y-coordinates of midpoint of the circle. **Required**.
    * **radius** is the radius of the circle. **Required**.
    * The **border width** is the width of the border of the circle. **Optional**, default: *1*.
    * The **border color** is the color of the border of the circle. You can use the same syntax as described for the border color of a rectangle above.. **Optional**; default: *black*.
    * The **border opacity** is the opacity of the border of the circle, given as a value between 0 and 1 (where 0 means *invisible* and *1* means *totally visible*). **Optional**; default: *1*. 
    * The **filling color** is the color of the filling of the circle. **Optional**; default: not set (= no filling).
    * The **filling opacity** the opacity of the filling of the circle, given as a value between 0 and 1 (where 0 means *invisible* and *1* means *totally visible*). **Optional**; default: *1*.
    
    The first 5 fields are required, all other fields are optional and can be omitted (instead, the provided default values are used).
 
  * **Text**: The syntax for adding text to the PDF is as follows (again, the first line is a comment line and doesn't belong to any instruction):
    ```bash
    # type  pageNum  x      y      text         font       fontsize  color  opacity
    T       3        287.4  265.6  Hello World  helvetica  12        red    1
    ```
    * The **type** stands for the type of drawing you want to add. Allowed values are: *T*, and *TEXT* (and the lowercase equivalents). **Required**.
    * The **page number** defines the page to which you want to add the text (Note: the page numbers are 1-based). **Required**.
    * **x**, **y**, are the x/y-coordinates of the lower left of the first character of the text you want to add. **Required**.
    * **text** is the text you want to add. **Required**.
    * The **font** is the font you want to use. Allowed fonts are: "times-roman", "helvetica", "courier", "symbol" and "zapfdingbats". **Optional**, default: *helvetica*.
    * The **fontsize** is the size of the text, in pt. **Optional**; default: *12*. 
    * The **color** is the color of the text, with the same syntax as described above. **Optional**; default: *black*.
    * The **opacity** the opacity of the text, given as a value between 0 and 1 (where 0 means *invisible* and *1* means *totally visible*). **Optional**; default: *1*.
    
    The first 5 fields are required, all other fields are optional and can be omitted (instead, the provided default values are used).
    
    In all optionals field you can also use "-", standing for a "null value" and meaning that **no** value should be applied to the respective drawing in the respective field.
    For example, you can use "-" for the filling of a rectangle to specify that the rectangle should not have any filling.
    
    For an example instruction file, see [src/test/resources/example-instructions-1.tsv](src/test/resources/example-instructions-1.tsv)
