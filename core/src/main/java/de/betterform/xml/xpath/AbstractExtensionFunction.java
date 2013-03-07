/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xpath;

import net.sf.saxon.s9api.ExtensionFunction;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SequenceType;

/**
 * Abstract Implementation of an Extension Function. 
 *
 * @author Tobias Krebs
 * @author Robert Netzschwitz
 * @see ExtensionFunction
 */
public abstract class AbstractExtensionFunction implements ExtensionFunction {

  private QName qname;
  private SequenceType[] argumentTypes;
  private SequenceType resultType;

  
  /**
   * @param qname
   * @param arguments
   * @param resultType
   */
  public AbstractExtensionFunction(QName qname, SequenceType[] arguments, SequenceType resultType) {
    this.qname = qname;
    this.argumentTypes = arguments;
    this.resultType = resultType;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return argumentTypes;
  }

  @Override
  public QName getName() {
    return qname;
  }

  @Override
  public SequenceType getResultType() {
    return resultType;
  }

}
