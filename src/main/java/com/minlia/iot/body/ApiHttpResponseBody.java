package com.minlia.iot.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minlia.cloud.body.Body;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by will on 9/10/17.
 */
@XmlRootElement(name = "xml")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiHttpResponseBody implements Body {

}
