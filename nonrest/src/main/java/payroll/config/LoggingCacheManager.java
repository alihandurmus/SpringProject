package payroll.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class LoggingCacheManager implements CacheManager {
    private final SimpleCacheManager delegate;

    public LoggingCacheManager() {
        this.delegate = new SimpleCacheManager();
        this.delegate.setCaches(List.of(new ConcurrentMapCache("employees")));
        this.delegate.afterPropertiesSet();
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = delegate.getCache(name);
        return new LoggingCache(cache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return delegate.getCacheNames();
    }
    public static class LoggingCache implements Cache {
        private final Cache delegate;

        public LoggingCache(Cache delegate) {
            this.delegate = delegate;
        }


        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public Object getNativeCache() {
            return delegate.getNativeCache();
        }

        @Override
        public ValueWrapper get(Object key) {
            ValueWrapper valueWrapper = delegate.get(key);
            System.out.println("Cache GET: key" + key + " value: " + (valueWrapper == null ? null : valueWrapper.get()));
            return valueWrapper;
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            T value = delegate.get(key, type);
            System.out.println("Cache GET: Key = " + key + ", Value = " + value);
            return value;
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            T value = delegate.get(key, valueLoader);
            System.out.println("Cache GET: Key = " + key + ", Value = " + value);
            return value;
        }

        @Override
        public void put(Object key, Object value) {
            delegate.put(key, value);
            System.out.println("Cache PUT: Key = " + key + ", Value = " + value);

        }

        @Override
        public void evict(Object key) {
            delegate.evict(key);
            System.out.println("Cache EVICT: Key = " + key);

        }

        @Override
        public void clear() {
            delegate.clear();
            System.out.println("Cache CLEAR");

        }
    }
}
