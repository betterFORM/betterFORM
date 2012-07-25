package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.exist.xmldb.XQueryService;
import org.exist.xmldb.XmldbURI;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;

import javax.xml.transform.OutputKeys;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 24.07.12
 * Time: 15:54
 */
public class ExistDBQuery extends XFormsFunction {
    protected static String URI = "xmldb:exist://";

    protected static String driver = "org.exist.xmldb.DatabaseImpl";

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        if (argument.length != 1) {
            throw new XPathException("There must be 1 argument (query) for this function");
        }

        final Expression keyExpression = argument[0];
        final String query = keyExpression.evaluateAsString(xpathContext).toString();


        return new StringValue(query(query));
    }


    private String query( String query) {
        try {
            Class<?> cl = Class.forName( driver );
            Database database = (Database) cl.newInstance();
            database.setProperty( "create-database", "true" );
            DatabaseManager.registerDatabase( database );

            // get root-collection
            Collection col = DatabaseManager.getCollection(URI + XmldbURI.ROOT_COLLECTION);
            // get query-service
            XQueryService service = (XQueryService) col.getService( "XQueryService", "1.0" );

            // set pretty-printing on
            service.setProperty( OutputKeys.INDENT, "yes" );
            service.setProperty( OutputKeys.ENCODING, "UTF-8" );

            CompiledExpression compiled = service.compile( query );

            // execute query and get results in ResourceSet
            ResourceSet result = service.execute( compiled );

            ResourceIterator iterator =  result.getIterator();

            while (iterator.hasMoreResources()) {
                Resource resource = iterator.nextResource();

                Object object = resource.getContent();
                if (object instanceof String) {
                    return (String) object;
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return "";
    }
}