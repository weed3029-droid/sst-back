package sst.global.files.core;

@FunctionalInterface
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;
}