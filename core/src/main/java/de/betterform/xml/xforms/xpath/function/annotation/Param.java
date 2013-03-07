package de.betterform.xml.xforms.xpath.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Param {

  public static int REQUIRED = 10;
  public static int OPTIONAL = 11;
  public static int DEFAULT = REQUIRED;
  
  int value() default DEFAULT;

}
