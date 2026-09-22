package id.indogaro.messenger;

public interface GenericProvider<F, T> {
    T provide(F obj);
}
