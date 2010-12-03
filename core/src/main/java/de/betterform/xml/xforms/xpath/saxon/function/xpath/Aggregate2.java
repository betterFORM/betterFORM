/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function.xpath;


import javax.xml.transform.SourceLocator;

import net.sf.saxon.expr.ArithmeticExpression;
import net.sf.saxon.expr.Atomizer;
import net.sf.saxon.expr.Calculator;
import net.sf.saxon.expr.LastPositionFinder;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.functions.SystemFunction;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.Type;
import net.sf.saxon.type.TypeHierarchy;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.Cardinality;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.DurationValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.UntypedAtomicValue;

/**
* This class implements the sum(), avg(), count() functions,
*/

public class Aggregate2 extends SystemFunction {

    public static final int SUM = 0;
    public static final int AVG = 1;
    public static final int COUNT = 4;


    /**
     * Determine the item type of the value returned by the function
     * @param th
     */

    public ItemType getItemType(TypeHierarchy th) {
        switch (operation) {
            case COUNT:
                return super.getItemType(th);
            case SUM: {
                //ItemType base = argument[0].getItemType();
                ItemType base = Atomizer.getAtomizedItemType(argument[0], false, th);
                if (base.equals(BuiltInAtomicType.UNTYPED_ATOMIC)) {
                    base = BuiltInAtomicType.DOUBLE;
                }
                if (Cardinality.allowsZero(argument[0].getCardinality())) {
                    if (argument.length == 1) {
                        return Type.getCommonSuperType(base, BuiltInAtomicType.INTEGER, th);
                    } else {
                        return Type.getCommonSuperType(base, argument[1].getItemType(th), th);
                    }
                } else {
                    return base;
                }
            }
            case AVG: {
                ItemType base = Atomizer.getAtomizedItemType(argument[0], false, th);
                if (base.equals(BuiltInAtomicType.UNTYPED_ATOMIC)) {
                    return BuiltInAtomicType.DOUBLE;
                } else if (base.getPrimitiveType() == StandardNames.XS_INTEGER) {
                    return BuiltInAtomicType.DECIMAL;
                } else {
                    return base;
                }
            }
            default:
                throw new AssertionError("Unknown aggregate operation");
        }
    }

    /**
     * Determine the cardinality of the function.
     */

    public int computeCardinality() {
        if (operation == AVG && !Cardinality.allowsZero(argument[0].getCardinality())) {
            return StaticProperty.EXACTLY_ONE;
        } else {
            return super.computeCardinality();
        }
    }


    /**
    * Evaluate the function
    */

    public Item evaluateItem(XPathContext context) throws XPathException {
        // Note: these functions do not need to sort the underlying sequence,
        // but they do need to de-duplicate it
        switch (operation) {
            case COUNT:
                SequenceIterator iter = argument[0].iterate(context);
                return new Int64Value(count(iter));
            case SUM:
                AtomicValue sum = total(argument[0].iterate(context), context, this);
                if (sum != null) {
                    return sum;
                } else {
                    // the sequence was empty
                    if (argument.length == 2) {
                        return (AtomicValue)argument[1].evaluateItem(context);
                    } else {
                        return Int64Value.ZERO;
                    }
                } 
            case AVG:
                return average(argument[0].iterate(context), context, this);
            default:
                throw new UnsupportedOperationException("Unknown aggregate function");
        }
    }

    /**
     * Calculate the total of a sequence.
     * @return the total, according to the rules of the XPath sum() function, but returning null
     * if the sequence is empty. (It's then up to the caller to decide what the correct result is
     * for an empty sequence.
    */

    public static AtomicValue total(SequenceIterator iter, XPathContext context, SourceLocator location)
            throws XPathException {
        AtomicValue sum = (AtomicValue)iter.next();
        if (sum == null) {
            // the sequence is empty
           return DoubleValue.NaN;
        }
//        if (!sum.hasBuiltInType()) {
//            sum = sum.getPrimitiveValue();
//        }
        if (sum instanceof UntypedAtomicValue) {
            try {
                sum = sum.convert(BuiltInAtomicType.DOUBLE, context);
            } catch (XPathException e) {
                return DoubleValue.NaN;
            }
        }

        if (sum instanceof NumericValue) {
            AtomicValue next;
            while ( (next = (AtomicValue)iter.next()) != null) {
                if ( next instanceof UntypedAtomicValue ) {
                    try {
                        next = next.convert(BuiltInAtomicType.DOUBLE, context);
                    }  catch (XPathException e) {
                        return DoubleValue.NaN;
                    }
                } else {
                    return DoubleValue.NaN;
                }
                sum = ArithmeticExpression.compute(sum, Calculator.PLUS, (NumericValue)next, context);
                if (((NumericValue)sum).isNaN()) {
                    // take an early bath, once we've got a NaN it's not going to change
                    // TODO: it could change from a float NaN to a double NaN...
                    return sum;
                }
            }
            return sum;
        } else if (sum instanceof DurationValue) {
            AtomicValue next;
            while ( (next = (AtomicValue)iter.next()) != null) {
                if (!(next instanceof DurationValue)) {
                    return DoubleValue.NaN;
                }
                sum = ((DurationValue)sum).add((DurationValue)next);
            }

            return sum;
        } else {
            return DoubleValue.NaN;
        }
    }

    /**
    * Calculate average
    */

    public static AtomicValue average(SequenceIterator iter, XPathContext context, SourceLocator location)
            throws XPathException {
        int count = 0;
        AtomicValue item = (AtomicValue)iter.next();
        if (item == null) {
            // the sequence is empty
            return DoubleValue.NaN;
        }
        count++;
        if (item instanceof UntypedAtomicValue) {
            try {
                item = item.convert(BuiltInAtomicType.DOUBLE, context);
            } catch (XPathException e) {
                if (e.getLocator() == null || e.getLocator().getLineNumber() == -1) {
                    e.setLocator(location);
                }
                return DoubleValue.NaN;
            }
        }
        if (item instanceof NumericValue) {
            while (true) {
                AtomicValue next = (AtomicValue)iter.next();
                if (next == null) {
                    return ArithmeticExpression.compute(item, Calculator.DIV, new Int64Value(count), context);
                }
                count++;
                if (next instanceof UntypedAtomicValue) {
                    try {
                        next = next.convert(BuiltInAtomicType.DOUBLE, context);
                    } catch (XPathException e) {
                        return DoubleValue.NaN;
                    }
                } else if (!(next instanceof NumericValue)) {
                    return DoubleValue.NaN;
                }
                item = ArithmeticExpression.compute(item, Calculator.PLUS, (NumericValue)next, context);
                if (((NumericValue)item).isNaN()) {
                    // take an early bath, once we've got a NaN it's not going to change
                    // TODO: it could change from a float NaN to a double NaN...
                    return item;
                }
            }
        } else if (item instanceof DurationValue) {
            while (true) {
                AtomicValue next = (AtomicValue)iter.next();
                if (next == null) {
                    return ((DurationValue)item).multiply(1.0/count);
                }
                count++;
                if (!(next instanceof DurationValue)) {
                    return DoubleValue.NaN;
                }
                item = ((DurationValue)item).add((DurationValue)next);
            }
        } else {
            return DoubleValue.NaN;
        }
    }


    /**
     * Get the number of items in a sequence identified by a SequenceIterator
     * @param iter The SequenceIterator. This method moves the current position
     * of the supplied iterator; if this isn't safe, make a copy of the iterator
     * first by calling getAnother(). The supplied iterator must be positioned
     * before the first item (there must have been no call on next()).
     * @return the number of items in the underlying sequence
     * @throws XPathException if a failure occurs reading the input sequence
     */

    public static int count(SequenceIterator iter) throws XPathException {
        if ((iter.getProperties() & SequenceIterator.LAST_POSITION_FINDER) != 0) {
            return ((LastPositionFinder)iter).getLastPosition();
        } else {
            int n = 0;
            while (iter.next() != null) {
                n++;
            }
            return n;
        }
    }
}


