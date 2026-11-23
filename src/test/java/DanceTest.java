import entity.ConcertNumber;
import entity.Dancer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import parser.DomParser;
import parser.XmlTransformer;
import parser.XmlValidator;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DanceTest {

    private final String VALID_XML = "src/main/resources/dance.xml";
    private final String INVALID_XML = "src/test/resources/invalid_dance.xml";
    private final String XSD_PATH = "src/main/resources/dance.xsd";
    private final String XSLT_PATH = "src/main/resources/transform.xsl";
    private final String OUTPUT_XML = "src/test/resources/test_grouped.xml";

    private DomParser domParser;

    @BeforeEach
    void setUp() {
        domParser = new DomParser();
    }

    @AfterEach
    void cleanUp() {
        File outputFile = new File(OUTPUT_XML);
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Test
    @DisplayName("Валідатор повинен пропускати правильний XML та відхиляти файл з помилками структури")
    void testXmlValidationLogic() {
        assertAll("Перевірка логіки валідатора",
                () -> assertTrue(XmlValidator.validate(VALID_XML, XSD_PATH), "Валідний файл має повертати true"),
                () -> assertFalse(XmlValidator.validate(INVALID_XML, XSD_PATH), "Файл з помилками XSD має повертати false")
        );
    }

    @Test
    @DisplayName("Парсер повинен коректно витягувати не лише кореневі атрибути, а й глибоко вкладені теги")
    void testDeepParsingExtractsNestedElements() {
        List<ConcertNumber> concerts = domParser.parse(VALID_XML);
        assertFalse(concerts.isEmpty(), "Колекція не повинна бути порожньою");

        ConcertNumber firstNumber = concerts.get(0);

        assertAll("Перевірка вкладених об'єктів Dancer",
                () -> assertNotNull(firstNumber.getDancers(), "Список танцівників має бути ініціалізований"),
                () -> assertTrue(firstNumber.getDancers().size() >= 2, "У масовому виступі має бути кілька танцівників"),
                () -> assertNotNull(firstNumber.getDancers().get(0).name(), "Ім'я першого танцівника не має бути null"),
                () -> assertTrue(firstNumber.getDancers().get(0).age() > 0, "Вік танцівника має бути більше 0")
        );
    }

    @Test
    @DisplayName("Сортування Comparator має вишиковувати номери у правильній послідовності")
    void testComparatorSortingLogic() {
        ConcertNumber num3 = new ConcertNumber.Builder().number(3).id("ID_3").build();
        ConcertNumber num1 = new ConcertNumber.Builder().number(1).id("ID_1").build();
        ConcertNumber num2 = new ConcertNumber.Builder().number(2).id("ID_2").build();

        List<ConcertNumber> unsortedList = Arrays.asList(num3, num1, num2);

        unsortedList.sort(Comparator.comparingInt(ConcertNumber::getNumber));

        assertAll("Перевірка порядку після сортування",
                () -> assertEquals(1, unsortedList.get(0).getNumber(), "Першим має бути виступ #1"),
                () -> assertEquals(2, unsortedList.get(1).getNumber(), "Другим має бути виступ #2"),
                () -> assertEquals(3, unsortedList.get(2).getNumber(), "Третім має бути виступ #3")
        );
    }

    @Test
    @DisplayName("XSLT Трансформація повинна успішно створювати новий фізичний файл")
    void testXsltTransformationGeneratesOutputFile() {
        File output = new File(OUTPUT_XML);
        assertFalse(output.exists(), "Перед тестом файлу не повинно існувати");

        XmlTransformer.transform(VALID_XML, XSLT_PATH, OUTPUT_XML);

        assertTrue(output.exists(), "Після виклику трансформації має з'явитися новий XML файл");
        assertTrue(output.length() > 100, "Згенерований файл не повинен бути порожнім");
    }

    @Test
    @DisplayName("Парсер не повинен падати з помилкою (Exception), якщо передано неіснуючий файл")
    void testParserHandlesMissingFileGracefully() {
        List<ConcertNumber> result = domParser.parse("src/main/resources/ghost_file.xml");

        assertNotNull(result, "Парсер має повернути ініціалізований список, а не null");
        assertTrue(result.isEmpty(), "Список має бути порожнім, оскільки файл не знайдено");
    }

    @Test
    @DisplayName("Патерн Builder повинен правильно формувати об'єкт ConcertNumber навіть без додавання танцівників")
    void testConcertNumberBuilderLogic() {
        ConcertNumber testNumber = new ConcertNumber.Builder()
                .id("TEST_01")
                .type("східний")
                .scene("телестудія")
                .number(99)
                .build();

        assertAll("Перевірка правильності роботи Builder",
                () -> assertEquals("TEST_01", testNumber.getId()),
                () -> assertEquals("східний", testNumber.getType()),
                () -> assertEquals(99, testNumber.getNumber()),
                () -> assertNotNull(testNumber.getDancers()),
                () -> assertTrue(testNumber.getDancers().isEmpty())
        );
    }

    @Test
    @DisplayName("StAX парсер повинен точно зчитувати всі текстові дані включно з останнім елементом")
    void testStaxParserDataAccuracy() {
        parser.StaxParser staxParser = new parser.StaxParser();
        List<ConcertNumber> concerts = staxParser.parse(VALID_XML);

        assertFalse(concerts.isEmpty());

        ConcertNumber ballroomDance = concerts.stream()
                .filter(c -> "бальний".equals(c.getType()))
                .findFirst()
                .orElse(null);

        assertNotNull(ballroomDance);
        assertAll("Перевірка точності даних StAX парсера",
                () -> assertEquals("телестудія", ballroomDance.getScene()),
                () -> assertEquals("парний", ballroomDance.getGroup()),
                () -> assertEquals(2, ballroomDance.getDancers().size())
        );
    }

    @Test
    @DisplayName("Файл після XSLT трансформації повинен мати нову структуру з тегами Group")
    void testXsltOutputContainsCorrectNewStructure() throws Exception {
        XmlTransformer.transform(VALID_XML, XSLT_PATH, OUTPUT_XML);
        File outputFile = new File(OUTPUT_XML);
        assertTrue(outputFile.exists());

        javax.xml.parsers.DocumentBuilder builder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(outputFile);
        doc.getDocumentElement().normalize();

        org.w3c.dom.NodeList groupNodes = doc.getElementsByTagName("Group");

        assertAll("Перевірка зміненої структури XML",
                () -> assertEquals("GroupedDances", doc.getDocumentElement().getNodeName()),
                () -> assertTrue(groupNodes.getLength() > 0),
                () -> assertNotNull(((org.w3c.dom.Element) groupNodes.item(0)).getAttribute("type"))
        );
    }
}