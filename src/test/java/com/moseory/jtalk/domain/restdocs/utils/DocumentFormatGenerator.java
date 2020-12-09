package com.moseory.jtalk.domain.restdocs.utils;

import static org.springframework.restdocs.snippet.Attributes.Attribute;
import static org.springframework.restdocs.snippet.Attributes.key;

public interface DocumentFormatGenerator {

    static Attribute getDataFormat() {
        return key("format").value("yyyy-MM-dd");
    }

}
