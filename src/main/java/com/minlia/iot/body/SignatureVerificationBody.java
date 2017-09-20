package com.minlia.iot.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Created by will on 9/10/17.
 */
@Data
@ApiModel(value = "签名校验体")
public class SignatureVerificationBody extends SignatureBody{


  @ApiModelProperty(value = "校验签名的实体类型")
  @JsonProperty
  @XmlElement
  private Class<?> entityClass;

}
