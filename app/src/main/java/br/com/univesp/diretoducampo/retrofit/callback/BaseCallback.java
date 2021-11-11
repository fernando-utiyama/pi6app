package br.com.univesp.diretoducampo.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class BaseCallback <T> implements Callback<T> {

    private final ResponseCallback<T> callback;

    public BaseCallback(ResponseCallback<T> callback) {
        this.callback = callback;
    }

    @EverythingIsNonNull
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            T body = response.body();
            if (body != null) {
                callback.sucess(body);
            }
        } else {
            callback.fail("Erro de comunicação: " + response.message());
        }
    }

    @EverythingIsNonNull
    @Override
    public void onFailure(Call<T> call, Throwable t) {
       callback.fail("Erro de comunicação: " + t.getMessage());
    }

    public interface ResponseCallback<T> {

        void sucess(T products);

        void fail(String error);

    }
}
