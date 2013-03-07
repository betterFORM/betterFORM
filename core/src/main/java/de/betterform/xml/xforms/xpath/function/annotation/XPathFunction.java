package de.betterform.xml.xforms.xpath.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface XPathFunction {

  String prefix() default "";
  String namespace() default "";
  String localname();
  
  
}
