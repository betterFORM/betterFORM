package de.betterform.xml.xforms.xpath.function;

import net.sf.saxon.value.SequenceType;
import de.betterform.xml.xforms.xpath.function.annotation.Param;
import de.betterform.xml.xforms.xpath.function.annotation.XPathFunction;
import static de.betterform.xml.xforms.xpath.function.annotation.Param.OPTIONAL;

public class Power {
  
  @XPathFunction(localname="power")
  public static SequenceType power(@Param Object requiredArg, @Param(OPTIONAL) Object optionalArg){
    return null;
  }

}
