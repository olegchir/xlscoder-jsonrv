package com.xlscoder.jsonrv;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T, U, P> {
    void accept(T t, U u, P p);

    default TriConsumer<T, U, P> andThen(TriConsumer<? super T, ? super U, ? super P> after) {
        Objects.requireNonNull(after);

        return (l, r, n) -> {
            accept(l, r, n);
            after.accept(l, r, n);
        };
    }
}
