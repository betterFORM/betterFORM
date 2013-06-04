package de.betterform.agent.web.cache;

import org.infinispan.Cache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * XFSessionCache Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>May 21, 2013</pre>
 */
public class XFSessionCacheTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
        XFSessionCache.unloadCache();
    }

    /**
     * Method: getCache()
     */
    @Test
    public void testGetCache() throws Exception {
        Cache cache = XFSessionCache.getCache();
        assert(cache != null);
        assert ("xfSessionCache".equals(cache.getName()));
    }

    /**
     * Method: getCache(String cacheName)
     */
    @Test
    public void testGetCacheCacheName() throws Exception {
        Cache cache = XFSessionCache.getCache("xfTestConfigOneElementInMemory");
        assert(cache != null);
        assert ("xfTestConfigOneElementInMemory".equals(cache.getName()));
    }

    @Test
    public void testGetDoesNotExist() throws Exception{
        Cache cache = XFSessionCache.getCache("gaga");
        assert(cache != null);
    }

    @Test
    public void testUnloadCache() throws Exception {
        XFSessionCache.getCache();
        XFSessionCache.unloadCache();
    }

    /**
     * Method: main(String argv[])
     */
    @Test
    public void testMain() throws Exception {
//TODO: Test goes here...
    }


}
