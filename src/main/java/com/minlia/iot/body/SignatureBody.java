package com.minlia.iot.body;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minlia.cloud.body.Body;
import com.minlia.iot.signature.CaseControl;
import com.minlia.iot.signature.SignatureAlgorithmic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;

/**
 * Created by will on 9/10/17.
 */
@Data
@ApiModel(value = "签名体")
public class SignatureBody<T> implements Body {

  @ApiModelProperty(value = "需要进行签名的源字串")
  @JsonProperty
  @XmlElement
  private T raw;

  @ApiModelProperty(value = "签名算法")
  @JsonProperty
  @XmlElement
  private SignatureAlgorithmic algorithmic;


  @ApiModelProperty(value = "需要混合的盐值")
  @JsonProperty
  @XmlElement
  private String salt;


  @ApiModelProperty(value = "是否要排除掉盐值参数, 当为false时必须指定saltParameterPrefix")
  @JsonProperty
  @XmlElement
  private Boolean excludeSaltParameter;

  @ApiModelProperty(value = "盐值参数前缀, 如果不需要加值到前面则无需填入, 微信API需要指定一个key=到key值的前面(value2=111&key=000234) ,而BRCB不需要指定,成为(value2=111000234)")
  @JsonProperty
  @XmlElement
  private String saltParameterPrefix;


  @ApiModelProperty(value = "大小写转换")
  @JsonProperty
  @XmlElement
  private CaseControl caseControl;


  @ApiModelProperty(value = "参数间的分隔符, 默认为&")
  @JsonProperty
  @XmlElement
  private String delimiter;


  @ApiModelProperty(value = "签名时使用的字符集")
  @JsonProperty
  @XmlElement
  private String charset;


  @ApiModelProperty(value = "签名: 外部实现签名算法, 直接将值传入即可",example = "000")
  @JsonProperty
  @XmlElement
  private String signature;

//  @ApiModelProperty(value = "校验签名的实体类型")
//  @JsonProperty
//  @XmlElement
//  private Class<?> entity;

}
