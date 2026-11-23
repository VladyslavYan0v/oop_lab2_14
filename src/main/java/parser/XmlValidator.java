package parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class XmlValidator {
    private static final Logger logger = LoggerFactory.getLogger(XmlValidator.class);

    public static boolean validate(String xmlPath, String xsdPath) {
        try {
            SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new File(xsdPath))
                    .newValidator()
                    .validate(new StreamSource(new File(xmlPath)));
            logger.info("Валідація XSD пройдена успішно.");
            return true;
        } catch (Exception e) {
            logger.error("Помилка валідації: {}", e.getMessage());
            return false;
        }
    }
}