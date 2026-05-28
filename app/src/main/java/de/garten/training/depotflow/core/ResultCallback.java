package de.garten.training.depotflow.core;

public interface ResultCallback<T> {
    void onSuccess(T value);

    void onError(Throwable error);
}
