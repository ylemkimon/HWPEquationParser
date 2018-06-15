package kim.ylem.heparser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HEParserTest {
    @DisplayName("HEParser")
    @ParameterizedTest(name = "\"{0}\" should parse to \"{1}\"")
    @CsvFileSource(resources = "/parse-test.csv", numLinesToSkip = 1)
    void toParse(String expr, String expected) {
        assertEquals(expected, HEParser.parseToLaTeX(expr));
    }
}
