package de.betterform.agent.web.atmosphere;

import org.atmosphere.cpr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by joern on 02.06.14.
 */
public class UTF8Interceptor  extends AtmosphereInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(UTF8Interceptor.class);

    @Override
    public Action inspect(final AtmosphereResource r) {
        logger.debug("UTF8Interceptor called");
        logger.debug("char encoding:" + r.getRequest().getCharacterEncoding());
        r.getResponse().setCharacterEncoding("UTF-8");
        return super.inspect(r);
    }
}
