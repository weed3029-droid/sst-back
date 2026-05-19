package sst.global.files.core;

@FunctionalInterface
public interface ThrowableFunction<T, R> {
    // 💡 이 스펙은 람다 내부에서 '반드시' 체크 예외 처리를 하거나 밖으로 throws 해야만 통과하도록 자바 컴파일러를 강제합니다.
    R apply(T t) throws Exception;
}