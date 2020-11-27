package textextraction.visualizer;

import java.awt.Color;


/**
 * Some settings required by the PDF drawer.
 *
 * @author Claudius Korzen
 */
public class DocumentVisualizerSettings {
  /**
   * The default border width of a rectangle.
   */
  public static final float DEFAULT_RECT_BORDER_WIDTH = 1f;

  /**
   * The default border color of a rectangle.
   */
  public static final Color DEFAULT_RECT_BORDER_COLOR = Color.BLACK;

  /**
   * The default opacity of the border of a rectangle.
   */
  public static final float DEFAULT_RECT_BORDER_OPACITY = 1f;

  /**
   * The default filling color of a rectangle.
   */
  public static final Color DEFAULT_RECT_FILLING_COLOR = null;

  /**
   * The default opacity of the filling of a rectangle.
   */
  public static final float DEFAULT_RECT_FILLING_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The default width of a line.
   */
  public static final float DEFAULT_LINE_WIDTH = 1f;

  /**
   * The default color of a line.
   */
  public static final Color DEFAULT_LINE_COLOR = Color.BLACK;

  /**
   * The default opacity of a line.
   */
  public static final float DEFAULT_LINE_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The default border width of a circle.
   */
  public static final float DEFAULT_CIRCLE_BORDER_WIDTH = 1f;

  /**
   * The default border color of a circle.
   */
  public static final Color DEFAULT_CIRCLE_BORDER_COLOR = Color.BLACK;

  /**
   * The default opacity of the border of a circle.
   */
  public static final float DEFAULT_CIRCLE_BORDER_OPACITY = 1f;

  /**
   * The default filling color of a circle.
   */
  public static final Color DEFAULT_CIRCLE_FILLING_COLOR = null;

  /**
   * The default opacity of the filling of a circle.
   */
  public static final float DEFAULT_CIRCLE_FILLING_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The default font.
   */
  public static final String DEFAULT_FONT_NAME = "helvetica";

  /**
   * The default font size.
   */
  public static final float DEFAULT_TEXT_FONT_SIZE = 12f;

  /**
   * The default font color.
   */
  public static final Color DEFAULT_TEXT_FONT_COLOR = Color.BLACK;

  /**
   * The default font size.
   */
  public static final float DEFAULT_TEXT_OPACITY = 1f;

  // ==============================================================================================

  /**
   * The string used in a field of an instruction in an instruction file to reference a null value.
   */
  public static final String INSTRUCTION_NULL_IDENTIFIER = "-";
}
