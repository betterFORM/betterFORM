package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 16.04.12
 * Time: 16:10
 */
public class KnownDataType {
    private KnownDataType() {}

    public static final String ANYURI = "anyURI";

    public static final String BOOLEAN = "boolean";
    public static final String BASE64BINARY = "base64Binary";
    public static final String BYTE = "byte";

    public static final String CARDNUMBER = "card-number";

    public static final String DATE = "date";
    public static final String DATETIME = "dateTime";
    public static final String DAYTIMEDURATION = "dayTimeDuration";
    public static final String DECIMAL = "decimal";
    public static final String DOUBLE = "double";


    public static final String EMAIL = "email";

    public static final String FLOAT = "float";

    public static final String GDAY = "gDay";
    public static final String GMONTH = "gMonth";
    public static final String GMONTHDAY = "gMonthDay";
    public static final String GYEAR = "gYear";
    public static final String GYEARMONTH = "gYearMonth";

    public static final String HEXBINARY = "hexBinary";

    public static final String ID = "ID";
    public static final String IDREF = "IDREF";
    public static final String IDREFS = "IDREFS";
    public static final String INT = "int";
    public static final String INTEGER = "integer";

    public static final String LANGUAGE = "language";
    public static final String LISTITEM = "listItem";
    public static final String LISTITEMS = "listItems";
    public static final String LONG = "long";

    public static final String NAME = "name";
    public static final String NCNAME = "NCName";
    public static final String NEGATIVEINTEGER = "negativeInteger";
    public static final String NMTOKEN = "NMTOKEN";
    public static final String NMTOKENS = "NMTOKENS";
    public static final String NONPOSITIVEINTEGER = "nonPositiveInteger";
    public static final String NONNEGATIVEINTEGER = "nonNegativeInteger";
    public static final String NORMALIZEDSTRING = "normalizedString";

    public static final String POSITIVEINTEGER = "positiveInteger";

    public static final String QNAME = "QName";

    public static final String SHORT = "short";
    public static final String STRING = "string";

    public static final String TIME = "time";
    public static final String TOKEN = "token";

    public static final String UNSIGNEDBYTE = "unsignedInt";
    public static final String UNSIGNEDINT = "unsignedInt";
    public static final String UNSIGNEDLONG = "unsignedLong";
    public static final String UNSIGNEDSHORT = "unsignedShort";

    public static final String YEARMONTHDURATION = "yearMonthDuration";

    private static final String[] VALS = {ANYURI, BOOLEAN, BASE64BINARY, BYTE, CARDNUMBER, DATE, DATETIME, DAYTIMEDURATION, DECIMAL, DOUBLE, EMAIL, FLOAT, GDAY, GMONTH, GMONTHDAY, GYEAR, GYEARMONTH, HEXBINARY, ID, IDREF, IDREFS, INT, INTEGER, LANGUAGE, LISTITEM, LISTITEMS, LONG, NAME, NCNAME, NEGATIVEINTEGER, NMTOKEN, NMTOKENS, NONNEGATIVEINTEGER, NONPOSITIVEINTEGER, NORMALIZEDSTRING, POSITIVEINTEGER, QNAME, SHORT, STRING, TIME, TOKEN, UNSIGNEDBYTE, UNSIGNEDINT, UNSIGNEDLONG, UNSIGNEDSHORT, YEARMONTHDURATION};
    public static final List<String> VALUES = Collections.unmodifiableList(Arrays.asList(VALS));


    public static boolean isKnownDataType(String dataType, Element context) {
        if (dataType != null && dataType.contains(":")) {
            String namespacePrefix = dataType.substring(0, dataType.indexOf(':'));
            dataType = dataType.substring(dataType.indexOf(':')+1,dataType.length());

            String namespaceURI = NamespaceResolver.getNamespaceURI(context, namespacePrefix);
            if ( namespaceURI != null &&
                 (namespaceURI.equals(NamespaceConstants.XFORMS_NS) || namespaceURI.equals(NamespaceConstants.XMLSCHEMA_NS))) {
                return VALUES.contains(dataType) ? true: false;
            }
            //Datatype has namespace prefix but is not XFORMS or XML-Schema
            return false;
        }

        //If datatype is not prefixed assume built-in datatype (xsd/xforms)
        return true;
    }
}
