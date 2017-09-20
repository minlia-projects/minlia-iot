package com.minlia.iot.processor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Optional;
import javax.xml.bind.annotation.XmlElement;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 抽象的Api组件
 */
@Slf4j
public abstract class AbstractApiComponent {

    /**
     * 打印XML
     *
     * @param document
     */
    protected void printXML(Document document) {
        if (log.isInfoEnabled()) {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setExpandEmptyElements(true);
            format.setSuppressDeclaration(true);
            StringWriter stringWriter = new StringWriter();
            XMLWriter writer = new XMLWriter(stringWriter, format);
            try {
                writer.write(document);
                log.info(stringWriter.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取节点文本
     *
     * @param root
     * @param name
     * @return
     */
    private Optional<String> elementText(Element root, String name) {
        Element element = root.element(name);
        return Objects.nonNull(element) ? Optional.of(element.getTextTrim()) : Optional.empty();
    }

    /**
     * 获取节点值
     * @param root
     * @param clazz
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Optional<Object> elementValue(Element root, Class<?> clazz, String name) {
        Optional<String> elementTextOptional = elementText(root, name);
        if (elementTextOptional.isPresent()) {
            if (clazz == Integer.class) {
                return Optional.of(new Integer(elementTextOptional.get()));
            } else if (clazz == Long.class) {
                return Optional.of(new Long(elementTextOptional.get()));
            } else if (Enum.class.isAssignableFrom(clazz)) {
                try {
                    Class enumClass = Class.forName(clazz.getName());
                    return Optional.of(Enum.valueOf(enumClass, elementTextOptional.get()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            } else {
                return Optional.of(elementTextOptional.get());
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * 获取数组节点值
     * @param root
     * @param clazz
     * @param xmlElement
     * @param index
     * @return
     */
    protected Optional<Object> elementArrayValue(Element root, Class<?> clazz, XmlElement xmlElement, int index) {
        String name = xmlElement.name().replaceFirst("$n", String.valueOf(index));
        return elementValue(root, clazz, name);
    }
}
