package com.minlia.iot.signature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldPaired {

  private String property;

  private Object value;

  @Override
  public String toString() {
    return String.format("%s=%s", property, value.toString());
  }
}
