package de.betterform.connector.exist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.exist.EXistException;
import org.exist.security.Subject;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.StringValue;

public class ExistUtils {
  
  public static int calculateArity(Map<String, String> queryParameter) {
    int i = 0;
    for (String key : queryParameter.keySet()) {
      if (key.equals("type") || key.equals("function")) {
        continue;
      }
      ++i;
    }
    return i;
  }
  public static List<Sequence> getFunctionParameters(Map<String, String> queryParameter) {
    if (null == queryParameter) {
      return Collections.emptyList();
    }
    
    List<Sequence> results = new ArrayList<Sequence>();
    for (String key : queryParameter.keySet()) {
      if (key.equals("type") || key.equals("function")) {
        continue;
      }
      results.add(new StringValue(queryParameter.get(key)));
    }
    return results;
  }

  @SuppressWarnings("rawtypes")
  public static DBBroker getBroker(BrokerPool pool, Map context) throws EXistException {
    Subject subject = getSubject(pool, context);
    DBBroker broker = pool.get(subject);
    return broker;
  }

  @SuppressWarnings("rawtypes")
  public static Subject getSubject(BrokerPool pool, Map context) {
    Subject subject = (Subject) context.get("_eXist_xmldb_user");
    if (null != subject) {
      return subject;
    }
    return pool.getSecurityManager().getGuestSubject();
  }

}
