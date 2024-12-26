package at.dici.shade.core.cache;

import at.dici.shade.core.mysql.MySqlClass;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.ForwardingCache;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutionException;
@Getter( AccessLevel.PRIVATE )
public abstract class AsyncMySqlCache<K, V> extends ForwardingCache<K, V> {
    private final Cache<K, V> kvCache = CacheBuilder.newBuilder().build();
    private final String query = this.query();
    private final Function<ResultSet, Map<K, V>> function = this.function();
    private final Object[] parameters = this.parameters();
    private final Queue<Runnable> listeners = Queues.newConcurrentLinkedQueue();
    private CacheStatusType currentState;
    private static final Set<AsyncMySqlCache<?, ?>> caches = Sets.newConcurrentHashSet();
    private static final Queue<Runnable> globalListeners = Queues.newConcurrentLinkedQueue();

    /**
     * Call to start initialisation.
     */
    public AsyncMySqlCache() {
        this.currentState = AsyncMySqlCache.CacheStatusType.WORKING;
        getCaches().add(this);
        if (this.getQuery() != null && !this.getQuery().isEmpty() && this.getFunction() != null) {
            MySqlClass.getInstance().queryAsync(this.getQuery(), this.getFunction(), this::initComplete, this.getParameters() != null ? this.getParameters() : new Object[0]);
        } else {
            this.initComplete(Collections.emptyMap());
        }

    }

    /**
     * Get extended Guava-Cache.
     *
     * @return Delegating Cache
     */
    protected Cache<K, V> delegate() {
        return this.getKvCache();
    }


    /**
     * Internal Method to complete MySql Fetching.
     *
     * @param map Fetched Data.
     */
    private void initComplete(Map<K, V> map) {
        this.putAll(map);

        Runnable runnable;
        while((runnable = this.getListeners().poll()) != null) {
            runnable.run();
        }

        this.currentState = AsyncMySqlCache.CacheStatusType.FINISHED;
        triggerUpdate();
    }

    /**
     * Simplify default Get Method.
     * Using null returning ValueLoader
     *
     * @param key To find Data in CacheStore.
     * @return Dedicated Value.
     */
    public V get(K key) {
        try {
            return super.get(key, () -> null);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Listener executed on initialisation complete.
     *
     * @param runnable To execute.
     */
    public void addListener(Runnable runnable) {
        if (this.getCurrentState() == AsyncMySqlCache.CacheStatusType.FINISHED) {
            runnable.run();
        } else {
            this.getListeners().add(runnable);
        }

    }

    /**
     * Pass MySql Query executed on initialisation.
     *
     * @return MySql Query.
     */
    protected abstract String query( );

    /**
     * Pass Guava Function, to Transform fetched Result to Cache Format.
     *
     * @return Function (MySql ResultSet to Cache Map).
     */
    protected abstract Function<ResultSet, Map<K, V>> function( );

    /**
     * Pass Parameters to specify MySql Query.
     *
     * @return Parameters.
     */
    protected Object[] parameters( ) {
        return new Object[0];
    }

    /**
     * To check for global Cache completion.
     */
    private static synchronized void triggerUpdate() {
        if (allFinished()) {
            Runnable runnable;
            while((runnable = getGlobalListeners().poll()) != null) {
                runnable.run();
            }

        }
    }

    /**
     * Check if all Caches are finished.
     *
     * @return true if, false if not.
     */
    private static synchronized boolean allFinished() {
        Iterator<AsyncMySqlCache<?, ?>> var0 = getCaches().iterator();

        AsyncMySqlCache cache;
        do {
            if (!var0.hasNext()) {
                return true;
            }

            cache = var0.next();
        } while(cache.getCurrentState() == AsyncMySqlCache.CacheStatusType.FINISHED);

        return false;
    }

    /**
     * Add global Cache Completion Listener.
     *
     * @param runnable Called on global Completion.
     */
    public static void addGlobalListener(Runnable runnable) {
        if (allFinished()) {
            runnable.run();
        } else {
            getGlobalListeners().add(runnable);
        }

    }


    private Cache<K, V> getKvCache() {
        return this.kvCache;
    }

    private String getQuery() {
        return this.query;
    }

    private Function<ResultSet, Map<K, V>> getFunction() {
        return this.function;
    }

    private Object[] getParameters() {
        return this.parameters;
    }

    private Queue<Runnable> getListeners() {
        return this.listeners;
    }

    private CacheStatusType getCurrentState() {
        return this.currentState;
    }

    private static Set<AsyncMySqlCache<?, ?>> getCaches() {
        return caches;
    }

    private static Queue<Runnable> getGlobalListeners() {
        return globalListeners;
    }

    private static enum CacheStatusType {
        WORKING,
        FINISHED;

        private CacheStatusType() {
        }
    }
}

