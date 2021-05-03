package io.alerium.chocolate.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TemporarySet<E> extends AbstractSet<E> implements Set<E>, Serializable {
    private static final Object PRESENT = new Object();
    private final Cache<E, Object> cache;

    public TemporarySet(int size, long expireMillis) {
        this.cache = CacheBuilder.newBuilder().maximumSize(size).expireAfterWrite(expireMillis, TimeUnit.MILLISECONDS).build();
    }

    @NotNull
    public Iterator<E> iterator() {
        return this.cache.asMap().keySet().iterator();
    }

    public int size() {
        return this.cache.asMap().size();
    }

    @Override
    public boolean add(E e) {
        this.cache.put(e, PRESENT);
        return true;
    }
}
