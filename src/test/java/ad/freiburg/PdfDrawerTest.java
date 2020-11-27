// package ad.freiburg;

// import static ad.freiburg.PdfDrawerSettings.INSTRUCTION_NULL_IDENTIFIER;

// import java.awt.Color;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;

// import org.apache.pdfbox.pdmodel.font.PDFont;
// import org.apache.pdfbox.pdmodel.font.PDType1Font;
// import org.junit.Assert;
// import org.junit.BeforeClass;
// import org.junit.Test;

// /**
// * Unit-Tests for the PDF drawer.
// *
// * @author Claudius Korzen
// */
// public class PdfDrawerTest {
// /**
// * The path to the resources directory.
// */
// protected static final Path RES_DIR = Paths.get("src", "test", "resources");

// /**
// * The path to the example input PDF file.
// */
// protected static final Path INPUT_PDF = RES_DIR.resolve("example-input.pdf");

// /**
// * The path to the first example drawing instructions file.
// */
// protected static final Path INSTRUCTIONS_FILE_1 = RES_DIR.resolve("example-instructions-1.tsv");

// /**
// * The path to the second example drawing instructions file.
// */
// protected static final Path INSTRUCTIONS_FILE_2 = RES_DIR.resolve("example-instructions-2.tsv");

// /**
// * The path to the first example output PDF file.
// */
// protected static final Path OUTPUT_PDF_1 = RES_DIR.resolve("example-output-1.pdf");

// /**
// * The path to the second example output PDF file.
// */
// protected static final Path OUTPUT_PDF_2 = RES_DIR.resolve("example-output-2.pdf");

// /**
// * The path to the first expected output PDF file.
// */
// protected static final Path EXPECTED_OUTPUT_PDF_1 = RES_DIR.resolve("expected-output-1.pdf");

// /**
// * The path to the second expected output PDF file.
// */
// protected static final Path EXPECTED_OUTPUT_PDF_2 = RES_DIR.resolve("expected-output-2.pdf");

// /**
// * The path to a non-existing file.
// */
// protected static final Path NON_EXISTING_FILE = RES_DIR.resolve("non-existing-file.txt");

// // ==============================================================================================

// /**
// * Checks if all required files are there and deletes all temporary output files.
// *
// * @throws Exception
// * If deleting the temporary output files fails.
// */
// @BeforeClass
// public static void beforeClass() throws Exception {
// // Make sure that the example input PDF is readable.
// Assert.assertTrue(Files.isReadable(INPUT_PDF));
// // Make sure that the first example file with drawing instructions is readable.
// Assert.assertTrue(Files.isReadable(INSTRUCTIONS_FILE_1));
// // Make sure that the second example file with drawing instructions is readable.
// Assert.assertTrue(Files.isReadable(INSTRUCTIONS_FILE_2));
// // Make sure that the first expected output PDF is readable.
// Assert.assertTrue(Files.isReadable(EXPECTED_OUTPUT_PDF_1));
// // Make sure that the second expected output PDF is readable.
// Assert.assertTrue(Files.isReadable(EXPECTED_OUTPUT_PDF_2));
// // Make sure that the first output PDF does *not* exist.
// if (Files.exists(OUTPUT_PDF_1)) {
// Assert.assertTrue(Files.deleteIfExists(OUTPUT_PDF_1));
// }
// // Make sure that the second output PDF does *not* exist.
// if (Files.exists(OUTPUT_PDF_2)) {
// Assert.assertTrue(Files.deleteIfExists(OUTPUT_PDF_2));
// }
// // Make sure that NON_EXISTING_FILE does *not* exist.
// Assert.assertFalse(Files.isRegularFile(NON_EXISTING_FILE));
// }

// // ==============================================================================================
// // Tests for the constructor.

// @Test(expected = IllegalArgumentException.class)
// public void testConstructorWithNullFile() {
// new PdfDrawer(null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testConstructorWithNonExistingFile() {
// new PdfDrawer(NON_EXISTING_FILE);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testConstructorWithNonPdfFile() {
// new PdfDrawer(INSTRUCTIONS_FILE_1);
// }

// @Test
// public void testConstructor() {
// PdfDrawer drawer = new PdfDrawer(INPUT_PDF);
// Assert.assertEquals(drawer.pdf, INPUT_PDF);
// Assert.assertNotNull(drawer.pdDoc);
// Assert.assertEquals(10, drawer.pdDoc.getNumberOfPages());
// Assert.assertNotNull(drawer.streams);
// Assert.assertEquals(10, drawer.streams.size());
// drawer.close();
// }

// // ==============================================================================================
// // Tests for the method processDrawingInstructionFromFile().

// @Test(expected = IllegalArgumentException.class)
// public void testProcessDrawingInstructionFromFileWithNullFile() {
// PdfDrawer drawer = null;
// try {
// drawer = new PdfDrawer(INPUT_PDF);
// drawer.processDrawingInstructionFromFile(null);
// } finally {
// if (drawer != null) {
// drawer.close();
// }
// }
// }

// @Test(expected = IllegalArgumentException.class)
// public void testProcessDrawingInstructionFromFileWithNonExistingFile() {
// PdfDrawer drawer = null;
// try {
// drawer = new PdfDrawer(INPUT_PDF);
// drawer.processDrawingInstructionFromFile(NON_EXISTING_FILE);
// } finally {
// if (drawer != null) {
// drawer.close();
// }
// }
// }

// @Test
// public void testProcessDrawingInstructionFromFile() throws Exception {
// PdfDrawer drawer = new PdfDrawer(INPUT_PDF);
// drawer.processDrawingInstructionFromFile(INSTRUCTIONS_FILE_1);
// drawer.output(OUTPUT_PDF_1);
// drawer.close();
// Assert.assertTrue(Files.isReadable(OUTPUT_PDF_1));
// byte[] expectedBytes = Files.readAllBytes(EXPECTED_OUTPUT_PDF_1);
// byte[] actualBytes = Files.readAllBytes(OUTPUT_PDF_1);
// Assert.assertArrayEquals(expectedBytes, actualBytes);
// }

// @Test
// public void testProcessDrawingInstructionFromFileTwice() throws Exception {
// // Check if processDrawingInstructionFromFile() and output() can be called twice.
// PdfDrawer drawer = new PdfDrawer(INPUT_PDF);
// drawer.processDrawingInstructionFromFile(INSTRUCTIONS_FILE_1);
// drawer.output(OUTPUT_PDF_2);
// drawer.processDrawingInstructionFromFile(INSTRUCTIONS_FILE_2);
// drawer.output(OUTPUT_PDF_2);
// drawer.close();
// Assert.assertTrue(Files.isReadable(OUTPUT_PDF_2));
// byte[] expectedBytes = Files.readAllBytes(EXPECTED_OUTPUT_PDF_2);
// byte[] actualBytes = Files.readAllBytes(OUTPUT_PDF_2);
// Assert.assertArrayEquals(expectedBytes, actualBytes);
// }

// // ==============================================================================================
// // Tests for the method parseInteger().

// @Test
// public void testParseInteger() {
// int i = PdfDrawer.parseInteger(null, 0, 1);
// Assert.assertEquals(1, i);

// i = PdfDrawer.parseInteger(new String[]{ "1", "5" }, 2, 2);
// Assert.assertEquals(2, i);

// i = PdfDrawer.parseInteger(new String[]{ null, "", " " }, 0, 3);
// Assert.assertEquals(3, i);

// i = PdfDrawer.parseInteger(new String[]{ null, "", " " }, 1, 4);
// Assert.assertEquals(4, i);

// i = PdfDrawer.parseInteger(new String[]{ null, "", " " }, 2, 5);
// Assert.assertEquals(5, i);

// i = PdfDrawer.parseInteger(new String[]{ INSTRUCTION_NULL_IDENTIFIER }, 0, 6);
// Assert.assertEquals(0, i);

// i = PdfDrawer.parseInteger(new String[]{ "1", "112" }, 0, 7);
// Assert.assertEquals(1, i);

// i = PdfDrawer.parseInteger(new String[]{ "1", "112" }, 1, 8);
// Assert.assertEquals(112, i);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseIntegerWithStringStr() {
// PdfDrawer.parseInteger(new String[]{ "ABC" }, 0, 11);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseIntegerWithFloatStr() {
// PdfDrawer.parseInteger(new String[]{ "1.2" }, 0, 11);
// }

// // ==============================================================================================
// // Tests for the method parseFloat().

// @Test
// public void testParseFloat() {
// float f = PdfDrawer.parseFloat(null, 0, 1.1f);
// Assert.assertEquals(1.1f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ "1.1", "5.5" }, 2, 2.2f);
// Assert.assertEquals(2.2f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ null, "", " " }, 0, 3.3f);
// Assert.assertEquals(3.3f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ null, "", " " }, 1, 4.4f);
// Assert.assertEquals(4.4f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ null, "", " " }, 2, 5.5f);
// Assert.assertEquals(5.5f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ INSTRUCTION_NULL_IDENTIFIER }, 0, 6.6f);
// Assert.assertEquals(0.0f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ "1.1", "112.112" }, 0, 7.7f);
// Assert.assertEquals(1.1f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ "1.1", "112" }, 1, 8.8f);
// Assert.assertEquals(112f, f, 0.00001);

// f = PdfDrawer.parseFloat(new String[]{ "1", "112" }, 0, 9.9f);
// Assert.assertEquals(1.0f, f, 0.00001);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseFloatWithStringStr() {
// PdfDrawer.parseFloat(new String[]{ "ABC" }, 0, 11);
// }

// // ==============================================================================================
// // Tests for the method parseColor().

// @Test
// public void testParseColor() {
// Color c = PdfDrawer.parseColor(null, 0, Color.BLACK);
// Assert.assertEquals(Color.BLACK, c);

// c = PdfDrawer.parseColor(new String[]{ "green", "red" }, 2, Color.BLACK);
// Assert.assertEquals(Color.BLACK, c);

// c = PdfDrawer.parseColor(new String[]{ null, "", " " }, 0, Color.BLACK);
// Assert.assertEquals(Color.BLACK, c);

// c = PdfDrawer.parseColor(new String[]{ null, "", " " }, 1, Color.BLACK);
// Assert.assertEquals(Color.BLACK, c);

// c = PdfDrawer.parseColor(new String[]{ null, "", " " }, 2, Color.BLACK);
// Assert.assertEquals(Color.BLACK, c);

// c = PdfDrawer.parseColor(new String[]{ INSTRUCTION_NULL_IDENTIFIER }, 0, Color.BLACK);
// Assert.assertNull(c);

// c = PdfDrawer.parseColor(new String[]{ "green", "red" }, 0, Color.BLACK);
// Assert.assertEquals(Color.GREEN, c);

// c = PdfDrawer.parseColor(new String[]{ "green", "red" }, 1, Color.BLACK);
// Assert.assertEquals(Color.RED, c);

// c = PdfDrawer.parseColor(new String[]{ "random" }, 0, null);
// Assert.assertNotNull(c);

// c = PdfDrawer.parseColor(new String[]{ "rgb(255, 0, 0)" }, 0, Color.BLACK);
// Assert.assertEquals(Color.RED, c);

// c = PdfDrawer.parseColor(new String[]{ "rgb(0, 255, 0)" }, 0, Color.BLACK);
// Assert.assertEquals(Color.GREEN, c);

// c = PdfDrawer.parseColor(new String[]{ "rgb(0, 0, 255)" }, 0, Color.BLACK);
// Assert.assertEquals(Color.BLUE, c);

// c = PdfDrawer.parseColor(new String[]{ "rgb(255, 255, 255)" }, 0, null);
// Assert.assertEquals(Color.WHITE, c);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithInvalidRgbSpecification1() {
// PdfDrawer.parseColor(new String[]{ "rgb(255)" }, 0, null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithInvalidRgbSpecification2() {
// PdfDrawer.parseColor(new String[]{ "rgb(255, 0, 12, 13)" }, 0, null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithInvalidRgbSpecification3() {
// PdfDrawer.parseColor(new String[]{ "rgb(0.8, 0.1, 0.0)" }, 0, null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithInvalidRgbSpecification4() {
// PdfDrawer.parseColor(new String[]{ "rgb(-1, -2, -3)" }, 0, null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithInvalidRgbSpecification5() {
// PdfDrawer.parseColor(new String[]{ "rgb(345, 123, 356)" }, 0, null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithInvalidRgbSpecification6() {
// PdfDrawer.parseColor(new String[]{ "rgb(red, green, blue)" }, 0, null);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseColorWithUnknownColorName() {
// PdfDrawer.parseColor(new String[]{ "unknown" }, 0, null);
// }

// // ==============================================================================================
// // Tests for the method parseFont().

// @Test
// public void testParseFont() {
// PDFont f = PdfDrawer.parseFont(null, 0, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.COURIER, f);

// f = PdfDrawer.parseFont(new String[]{ "helvetica", "times" }, 2, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.COURIER, f);

// f = PdfDrawer.parseFont(new String[]{ null, "", " " }, 0, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.COURIER, f);

// f = PdfDrawer.parseFont(new String[]{ null, "", " " }, 1, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.COURIER, f);

// f = PdfDrawer.parseFont(new String[]{ null, "", " " }, 2, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.COURIER, f);

// f = PdfDrawer.parseFont(new String[]{ INSTRUCTION_NULL_IDENTIFIER }, 0, PDType1Font.SYMBOL);
// Assert.assertNull(f);

// f = PdfDrawer.parseFont(new String[]{ "times-roman", "helvetica" }, 0, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.TIMES_ROMAN, f);

// f = PdfDrawer.parseFont(new String[]{ "times-roman", "helvetica" }, 1, PDType1Font.COURIER);
// Assert.assertEquals(PDType1Font.HELVETICA, f);
// }

// @Test(expected = IllegalArgumentException.class)
// public void testParseFontWithInvalidFontName() {
// PdfDrawer.parseFont(new String[]{ "unknown" }, 0, PDType1Font.COURIER);
// }

// // ==============================================================================================
// // Tests for the method close().

// @Test
// public void testClose() {
// PdfDrawer drawer = new PdfDrawer(INPUT_PDF);
// drawer.close();
// Assert.assertTrue(drawer.pdDoc.getDocument().isClosed());
// }
// }
