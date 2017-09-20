package com.minlia.iot.marshal;

import com.minlia.cloud.body.Body;
import com.minlia.iot.scope.HttpMediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by will on 9/11/17.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiMarshalWrappedBody implements Body {

  /**
   * 返回的原始内容
   */
  private String raw;

  /**
   * 状态化的返回体类型, 大的返回体类型
   */
  private Class<?> statefulResponseBodyClass;

  /**
   * 请求类型
   */
  private HttpMediaType httpMediaType;


  /**
   * 业务返回体类型
   */
  private Class<?> businessResponseBodyClass;


}
