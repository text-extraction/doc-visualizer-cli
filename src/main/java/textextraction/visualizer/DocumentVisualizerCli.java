package textextraction.visualizer;

import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_CIRCLE_BORDER_COLOR;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_CIRCLE_BORDER_OPACITY;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_CIRCLE_BORDER_WIDTH;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_CIRCLE_FILLING_COLOR;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_CIRCLE_FILLING_OPACITY;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_FONT_NAME;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_LINE_COLOR;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_LINE_OPACITY;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_LINE_WIDTH;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_RECT_BORDER_COLOR;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_RECT_BORDER_OPACITY;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_RECT_BORDER_WIDTH;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_RECT_FILLING_COLOR;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_RECT_FILLING_OPACITY;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_TEXT_FONT_COLOR;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_TEXT_FONT_SIZE;
import static textextraction.visualizer.DocumentVisualizerSettings.DEFAULT_TEXT_OPACITY;
import static textextraction.visualizer.DocumentVisualizerSettings.INSTRUCTION_NULL_IDENTIFIER;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * The main class to run the document visualizer from the command line.
 * 
 * @author Claudius Korzen
 */
public class DocumentVisualizerCli {
  /**
   * The logger.
   */
  protected static final Logger LOG = LogManager.getLogger(DocumentVisualizerCli.class);

  /**
   * The main method.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    // Parse the command line arguments.
    Namespace arguments = parseCommandLineArguments(args);

    // Translate each string path to an Path object.
    String inputPdfFilePath = arguments.get("inputPdfFilePath");
    Path inputPdfFile = Paths.get(inputPdfFilePath);

    String drawingInstructionsFilePath = arguments.get("instructionsFilePath");
    Path drawingInstructionsFile = Paths.get(drawingInstructionsFilePath);

    String outputPdfFilePath = arguments.get("outputPdfFilePath");
    Path outputPdfFile = Paths.get(outputPdfFilePath);

    // Run the PDF drawer.
    PdfDrawer drawer = new PdfDrawer(inputPdfFile);
    processDrawingInstructionFromFile(drawingInstructionsFile, drawer);

    byte[] visualization = null;
    try {
      visualization = drawer.complete();
    } catch (Exception e) {
      System.err.println("Error on visualizing: " + e.getMessage());
      e.printStackTrace();
    }

    if (visualization != null) {
      try (OutputStream os = Files.newOutputStream(outputPdfFile)) {
        os.write(visualization);
      } catch (IOException e) {
        System.err.println("Error on writing visualization to file: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  // ==============================================================================================

  /**
   * Processes the drawing instrcutions from the given file.
   *
   * @param file   The file to read the instructions from.
   * @param drawer The drawer to be used on handling the instructions.
   */
  protected static void processDrawingInstructionFromFile(Path file, PdfDrawer drawer) {
    if (file == null) {
      throw new IllegalArgumentException("No drawing instructions file given.");
    }

    // Check if the file is readable.
    if (!Files.isReadable(file)) {
      String msg = String.format("The drawing instructions file '%s' isn't readable", file);
      throw new IllegalArgumentException(msg);
    }

    LOG.info("Processing the drawing instructions from file '{}' ...", file);

    // Read the file line by line.
    try (Stream<String> stream = Files.lines(file)) {
      stream.forEach(line -> {
        // Ignore empty lines.
        if (line.isEmpty()) {
          return;
        }

        // Ignore comment lines.
        char firstChar = Character.toUpperCase(line.charAt(0));
        if (firstChar == '#' || firstChar == '%') {
          return;
        }

        LOG.debug("Parsing line '{}'", line);

        // Split the line into the individual arguments.
        String[] args = line.split("\t");

        // The first argument specifies the type of the drawing instruction.
        switch (firstChar) {
          case 'R':
            parseAndProcessDrawRectangleArguments(args, drawer);
            break;
          case 'L':
            parseAndProcessDrawLineArguments(args, drawer);
            break;
          case 'C':
            parseAndProcessDrawCircleArguments(args, drawer);
            break;
          case 'T':
            parseAndProcessDrawTextArguments(args, drawer);
            break;
          default:
            String msg = String.format("Unknown drawing instruction '%s'", args[0]);
            throw new IllegalArgumentException(msg);
        }
      });
    } catch (IOException e) {
      String msg = String.format("Error on reading drawing instructions from file '%s'.", file);
      throw new IllegalStateException(msg, e);
    }
  }

  // ==============================================================================================

  /**
   * Parses the given array of arguments and passes them to the drawRectangle() method of the given
   * drawer.
   *
   * @param args   The arguments to parse.
   * @param drawer The drawer to use on processing the drawing instruction.
   */
  protected static void parseAndProcessDrawRectangleArguments(String[] args, PdfDrawer drawer) {
    if (args.length < 6) {
      LOG.warn("Ignoring instruction '{}' (not enough arguments).", Arrays.toString(args));
      return;
    }

    LOG.debug("Parsing 'draw rectangle' arguments '{}'.", Arrays.toString(args));

    // Parse the page number.
    int pageNum = parseInteger(args, 1);
    // Parse the minX value.
    float minX = parseFloat(args, 2);
    // Parse the minY value.
    float minY = parseFloat(args, 3);
    // Parse the maxX value.
    float maxX = parseFloat(args, 4);
    // Parse the maxY value.
    float maxY = parseFloat(args, 5);
    // Parse the border width.
    float borderWidth = parseFloat(args, 6, DEFAULT_RECT_BORDER_WIDTH);
    // Parse the border color.
    Color borderColor = parseColor(args, 7, DEFAULT_RECT_BORDER_COLOR);
    // Parse the opacity of the border.
    float borderOpacity = parseFloat(args, 8, DEFAULT_RECT_BORDER_OPACITY);
    // Parse the filling color.
    Color fillingColor = parseColor(args, 9, DEFAULT_RECT_FILLING_COLOR);
    // Parse the opacity of the filling.
    float fillingOpacity = parseFloat(args, 10, DEFAULT_RECT_FILLING_OPACITY);

    // Pass the arguments to the drawRectangle() method.
    drawer.drawRectangle(pageNum, minX, minY, maxX, maxY, borderWidth, borderColor, borderOpacity,
            fillingColor, fillingOpacity);
  }

  /**
   * Parses the given array of arguments and passes them to the drawLine() method of the given
   * drawer.
   *
   * @param args   The arguments to parse.
   * @param drawer The drawer to use on processing the drawing instruction.
   */
  protected static void parseAndProcessDrawLineArguments(String[] args, PdfDrawer drawer) {
    if (args.length < 6) {
      LOG.warn("Ignoring instruction '{}' (not enough arguments).", Arrays.toString(args));
      return;
    }

    LOG.debug("Parsing 'draw line' arguments '{}'.", Arrays.toString(args));

    // Parse the page number.
    int pageNum = parseInteger(args, 1);
    // Parse the x0 value.
    float x0 = parseFloat(args, 2);
    // Parse the y0 value.
    float y0 = parseFloat(args, 3);
    // Parse the x1 value.
    float x1 = parseFloat(args, 4);
    // Parse the y1 value.
    float y1 = parseFloat(args, 5);
    // Parse the line width.
    float lineWidth = parseFloat(args, 6, DEFAULT_LINE_WIDTH);
    // Parse the line color.
    Color lineColor = parseColor(args, 7, DEFAULT_LINE_COLOR);
    // Parse the opacity of the line.
    float lineOpacity = parseFloat(args, 8, DEFAULT_LINE_OPACITY);

    // Pass the arguments to the drawLine() method.
    drawer.drawLine(pageNum, x0, y0, x1, y1, lineWidth, lineColor, lineOpacity);
  }

  /**
   * Parses the given array of arguments and passes them to the drawCircle() method of the given
   * drawer.
   *
   * @param args   The arguments to parse.
   * @param drawer The drawer to use on processing the drawing instruction.
   */
  protected static void parseAndProcessDrawCircleArguments(String[] args, PdfDrawer drawer) {
    if (args.length < 5) {
      LOG.warn("Ignoring instruction '{}' (not enough arguments).", Arrays.toString(args));
      return;
    }

    LOG.debug("Parsing 'draw circle' arguments '{}'.", Arrays.toString(args));

    // Parse the page number.
    int pageNum = parseInteger(args, 1);
    // Parse the x value.
    float x = parseFloat(args, 2);
    // Parse the y value.
    float y = parseFloat(args, 3);
    // Parse the radius value.
    float r = parseFloat(args, 4);
    // Parse the border width.
    float borderWidth = parseFloat(args, 5, DEFAULT_CIRCLE_BORDER_WIDTH);
    // Parse the border color.
    Color borderColor = parseColor(args, 6, DEFAULT_CIRCLE_BORDER_COLOR);
    // Parse the border opacity.
    float borderOpacity = parseFloat(args, 7, DEFAULT_CIRCLE_BORDER_OPACITY);
    // Parse the filling color.
    Color fillingColor = parseColor(args, 8, DEFAULT_CIRCLE_FILLING_COLOR);
    // Parse the filling opacity.
    float fillingOpacity = parseFloat(args, 9, DEFAULT_CIRCLE_FILLING_OPACITY);

    // Pass the arguments to the drawCircle() method.
    drawer.drawCircle(pageNum, x, y, r, borderWidth, borderColor, borderOpacity, fillingColor,
            fillingOpacity);
  }

  /**
   * Parses the given array of arguments and passes them to the drawText() method of the given
   * drawer.
   *
   * @param args   The arguments to parse.
   * @param drawer The drawer to use on processing the drawing instruction.
   * 
   */
  protected static void parseAndProcessDrawTextArguments(String[] args, PdfDrawer drawer) {
    if (args.length < 5) {
      LOG.warn("Ignoring instruction '{}' (not enough arguments).", Arrays.toString(args));
      return;
    }

    LOG.debug("Parsing 'draw text' arguments '{}'.", Arrays.toString(args));

    // Parse the page number.
    int pageNum = parseInteger(args, 1);
    // Parse the x value.
    float x = parseFloat(args, 2);
    // Parse the minY value.
    float y = parseFloat(args, 3);
    // Parse the text to draw.
    String text = args[4];
    // Parse the font.
    String fontName = parseString(args, 5, DEFAULT_FONT_NAME);
    // Parse the font size.
    float fontSize = parseFloat(args, 6, DEFAULT_TEXT_FONT_SIZE);
    // Parse the font color.
    Color color = parseColor(args, 7, DEFAULT_TEXT_FONT_COLOR);
    // Parse the opacity.
    float opacity = parseFloat(args, 8, DEFAULT_TEXT_OPACITY);

    // Pass the arguments to the drawText() method.
    drawer.drawText(pageNum, x, y, text, fontName, fontSize, color, opacity);
  }

  // ==============================================================================================

  /**
   * Parses the i-th element in the given array to a string. Returns the given default string if
   * there is no such element or if the element is empty. Returns null if the element is equal to
   * the NULL_IDENTIFIER. Throws an exception if the value of the element couldn't be parsed to a
   * string.
   *
   * @param args          The array of arguments.
   * @param i             The index of the element to parse.
   * @param defaultString The default value to return if no such element exists or if the element is
   *                      empty.
   *
   * @return The i-th element of the array as a string.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to a string.
   */
  protected static String parseString(String[] args, int i) throws IllegalArgumentException {
    return parseString(args, i, null);
  }

  /**
   * Parses the i-th element in the given array to a string. Returns the given default string if
   * there is no such element or if the element is empty. Returns null if the element is equal to
   * the NULL_IDENTIFIER. Throws an exception if the value of the element couldn't be parsed to a
   * string.
   *
   * @param args          The array of arguments.
   * @param i             The index of the element to parse.
   * @param defaultString The default value to return if no such element exists or if the element is
   *                      empty.
   *
   * @return The i-th element of the array as a string.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to a string.
   */
  protected static String parseString(String[] args, int i, String defaultString)
          throws IllegalArgumentException {
    if (args == null) {
      return defaultString;
    }

    if (i >= args.length) {
      return defaultString;
    }

    if (args[i] == null || args[i].trim().isEmpty()) {
      return defaultString;
    }

    if (args[i].equals(INSTRUCTION_NULL_IDENTIFIER)) {
      return null;
    }

    return args[i];
  }

  /**
   * Parses the i-th element in the given array to an integer. Returns 0 if there is no such
   * element, the element is empty or if the element is equal to the NULL_IDENTIFIER. Throws an
   * exception if the value of the element couldn't be parsed to an integer.
   *
   * @param args The array of arguments.
   * @param i    The index of the element to parse.
   *
   * @return The i-th element of the array as an integer.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to an integer.
   */
  protected static int parseInteger(String[] args, int i) throws IllegalArgumentException {
    return parseInteger(args, i, 0);
  }

  /**
   * Parses the i-th element in the given array to an integer. Returns the given default integer if
   * there is no such element or if the element is empty. Returns 0 if the element is equal to the
   * NULL_IDENTIFIER. Throws an exception if the value of the element couldn't be parsed to an
   * integer.
   *
   * @param args       The array of arguments.
   * @param i          The index of the element to parse.
   * @param defaultInt The default value to return if no such element exists or if the element is
   *                   empty.
   *
   * @return The i-th element of the array as an integer.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to an integer.
   */
  protected static int parseInteger(String[] args, int i, int defaultInt)
          throws IllegalArgumentException {
    if (args == null) {
      return defaultInt;
    }

    if (i >= args.length) {
      return defaultInt;
    }

    if (args[i] == null || args[i].trim().isEmpty()) {
      return defaultInt;
    }

    if (args[i].equals(INSTRUCTION_NULL_IDENTIFIER)) {
      return 0;
    }

    try {
      return Integer.parseInt(args[i]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(String.format("'%s' isn't a valid integer.", args[i]));
    }
  }

  /**
   * Parses the i-th element in the given array to a float. Returns 0 if there is no such element,
   * the element is empty or if the element is equal to the NULL_IDENTIFIER. Throws an exception if
   * the value of the element couldn't be parsed to a float.
   *
   * @param args The array of arguments.
   * @param i    The index of the element to parse.
   *
   * @return The i-th element of the array as a float.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to a float.
   */
  protected static float parseFloat(String[] args, int i) throws IllegalArgumentException {
    return parseFloat(args, i, 0f);
  }

  /**
   * Parses the i-th element in the given array to a float. Returns the given default float if there
   * is no such element or if the element is empty. Returns 0 if the element is equal to the
   * NULL_IDENTIFIER. Throws an exception if the value of the element couldn't be parsed to a float.
   *
   * @param args         The array of arguments.
   * @param i            The index of the element to parse.
   * @param defaultFloat The default value to return if no such element exists or if the element is
   *                     empty.
   *
   * @return The i-th element of the array as a float.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to a float.
   */
  protected static float parseFloat(String[] args, int i, float defaultFloat)
          throws IllegalArgumentException {
    if (args == null) {
      return defaultFloat;
    }

    if (i >= args.length) {
      return defaultFloat;
    }

    if (args[i] == null || args[i].trim().isEmpty()) {
      return defaultFloat;
    }

    if (args[i].equals(INSTRUCTION_NULL_IDENTIFIER)) {
      return 0f;
    }

    try {
      return Float.parseFloat(args[i]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(String.format("'%s' isn't a valid number.", args[i]));
    }
  }

  /**
   * Parses the i-th element in the given array to a color. Returns null if there is no such
   * element, the element is empty or if the element is equal to the NULL_IDENTIFIER. Throws an
   * exception if the value of the element couldn't be parsed to a color.
   *
   * @param args The array of arguments.
   * @param i    The index of the element to parse.
   *
   * @return The i-th element of the array as a color.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to a color.
   */
  protected static Color parseColor(String[] args, int i) throws IllegalArgumentException {
    return parseColor(args, i, null);
  }

  /**
   * Parses the i-th element in the given array to a color. Returns the given default color if there
   * is no such element or if the element is empty. Returns null if the element is equal to the
   * NULL_IDENTIFIER. Throws an exception if the value of the element couldn't be parsed to a color.
   *
   * @param args         The array of arguments.
   * @param i            The index of the element to parse.
   * @param defaultColor The default value to return if no such element exists or if the element is
   *                     empty.
   *
   * @return The i-th element of the array as a color.
   *
   * @throws IllegalArgumentException If the element couldn't be parsed to a color.
   */
  protected static Color parseColor(String[] args, int i, Color defaultColor)
          throws IllegalArgumentException {
    if (args == null) {
      return defaultColor;
    }

    if (i >= args.length) {
      return defaultColor;
    }

    if (args[i] == null || args[i].trim().isEmpty()) {
      return defaultColor;
    }

    if (args[i].equals(INSTRUCTION_NULL_IDENTIFIER)) {
      return null;
    }

    // Check if the color is provided by an RGB value, in the form "rgb(R,G,B)".
    if (args[i].startsWith("rgb(")) {
      // Extract the "R,G,B" part
      String rgbStr = args[i].substring(4, args[i].length() - 1);
      // Translate it to an array [R, G, B]
      String[] rgbArray = rgbStr.split(",");
      if (rgbArray.length != 3) {
        String msg = String.format("'%s' isn't a valid RGB specification.", args[i]);
        throw new IllegalArgumentException(msg);
      }
      try {
        // Compute the integer values of R, G and B.
        int r = Integer.parseInt(rgbArray[0].trim());
        int g = Integer.parseInt(rgbArray[1].trim());
        int b = Integer.parseInt(rgbArray[2].trim());
        return new Color(r, g, b);
      } catch (NumberFormatException e) {
        String msg = String.format("'%s' isn't a valid RGB specification.", args[i]);
        throw new IllegalArgumentException(msg);
      }
    }

    // Check if we have to return a random color.
    if (args[i].toLowerCase().equals("random")) {
      // Return a random color.
      int r = ThreadLocalRandom.current().nextInt(256);
      int g = ThreadLocalRandom.current().nextInt(256);
      int b = ThreadLocalRandom.current().nextInt(256);
      return new Color(r, g, b);
    }

    // Consider the argument as a "human-readable color name" and try to translate it to the
    // respective color. Note: Color.getColor() doesn't work here, the reason is described here:
    // https://stackoverflow.com/questions/3772098/how-does-java-awt-color-getcolorstring
    // -colorname-work
    try {
      Field field = Color.class.getField(args[i]);
      return (Color) field.get(null);
    } catch (Exception e) {
      // There is no such color.
      String msg = String.format("'%s' isn't a valid color name.", args[i]);
      throw new IllegalArgumentException(msg);
    }
  }

  // ==============================================================================================

  /**
   * Parses the command line arguments.
   *
   * @param args The command line arguments to parse.
   *
   * @return The parse command line arguments in a namespace object.
   */
  protected static Namespace parseCommandLineArguments(String[] args) {
    // Build a new command line arguments parser.
    ArgumentParser parser =
            ArgumentParsers.newFor(DocumentVisualizerCli.class.getSimpleName()).build();

    // Add the project description.
    parser.description("A simple tool that can be used to add drawings "
            + "(e.g., rectangles, lines or circles) and/or texts to an existing PDF file.");

    // Add an argument for specifying the path to the input PDF file.
    parser.addArgument("-i", "--inputPdf")
            .help("The path to the PDF file to which the drawings should be added.")
            .type(String.class).required(true).dest("inputPdfFilePath").metavar("path");

    // Add an argument for specifying the path to the drawing instructions file.
    parser.addArgument("-j", "--instructionFile")
            .help("The path to the file that contains drawing instructions.").type(String.class)
            .required(true).dest("instructionsFilePath").metavar("path");

    // Add an argument for specifying the path to the output PDF file.
    parser.addArgument("-o", "--outputPdf")
            .help("The path where the final PDF file (with added drawings) should be stored.")
            .type(String.class).required(true).dest("outputPdfFilePath").metavar("path");

    Namespace namespace = null;
    try {
      // Parse the command line arguments using the above parser.
      namespace = parser.parseArgs(args);
    } catch (HelpScreenException e) {
      // The help screen was requested. Let the parser handle the error itself.
      parser.handleError(e);
      System.exit(0);
    } catch (ArgumentParserException e) {
      // There are some invalid arguments. Let the parser handle the error itself.
      parser.handleError(e);
      System.exit(1);
    }
    return namespace;
  }
}
