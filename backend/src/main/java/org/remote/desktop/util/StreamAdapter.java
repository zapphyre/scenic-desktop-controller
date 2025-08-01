//package org.remote.desktop.util;
//
//
//@FunctionalInterface
//public interface ThrowingPredicate<T, X extends Throwable> {
//    public boolean test(T t) throws X;
//}
//
//@FunctionalInterface
//public interface ThrowingFunction<T, R, X extends Throwable> {
//    public R apply(T t) throws X;
//}
//
//@FunctionalInterface
//public interface ThrowingSupplier<R, X extends Throwable> {
//    public R get() throws X;
//}
//
//public interface ThrowingStream<T, X extends Throwable> {
//    public ThrowingStream<T, X> filter(
//            ThrowingPredicate<? super T, ? extends X> predicate);
//
//    public <R> ThrowingStream<T, R> map(
//            ThrowingFunction<? super T, ? extends R, ? extends X> mapper);
//
//    public <A, R> R collect(Collector<? super T, A, R> collector) throws X;
//
//    // etc
//}
//
//class StreamAdapter<T, X extends Throwable> implements ThrowingStream<T, X> {
//    private static class AdapterException extends RuntimeException {
//        public AdapterException(Throwable cause) {
//            super(cause);
//        }
//    }
//
//    private final Stream<T> delegate;
//    private final Class<X> x;
//
//    StreamAdapter(Stream<T> delegate, Class<X> x) {
//        this.delegate = delegate;
//        this.x = x;
//    }
//
//    private <R> R maskException(ThrowingSupplier<R, X> method) {
//        try {
//            return method.get();
//        } catch (Throwable t) {
//            if (x.isInstance(t)) {
//                throw new AdapterException(t);
//            } else {
//                throw t;
//            }
//        }
//    }
//
//    @Override
//    public ThrowingStream<T, X> filter(ThrowingPredicate<T, X> predicate) {
//        return new StreamAdapter<>(
//                delegate.filter(t -> maskException(() -> predicate.test(t))), x);
//    }
//
//    @Override
//    public <R> ThrowingStream<R, X> map(ThrowingFunction<T, R, X> mapper) {
//        return new StreamAdapter<>(
//                delegate.map(t -> maskException(() -> mapper.apply(t))), x);
//    }
//
//    private <R> R unmaskException(Supplier<R> method) throws X {
//        try {
//            return method.get();
//        } catch (AdapterException e) {
//            throw x.cast(e.getCause());
//        }
//    }
//
//    @Override
//    public <A, R> R collect(Collector<T, A, R> collector) throws X {
//        return unmaskException(() -> delegate.collect(collector));
//    }
//}