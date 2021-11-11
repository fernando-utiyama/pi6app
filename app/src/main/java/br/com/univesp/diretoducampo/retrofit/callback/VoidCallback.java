package br.com.univesp.diretoducampo.retrofit.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class VoidCallback implements Callback<Void> {

    private final ResponseCallback callback;

    public VoidCallback(ResponseCallback callback) {
        this.callback = callback;
    }

    @Override
    @EverythingIsNonNull
    public void onResponse(Call<Void> call, Response<Void> response) {
        if (response.isSuccessful()) {
            callback.sucess();
        } else {
            callback.fail("Erro de comunicação: " + response.message());
        }
    }

    @Override
    @EverythingIsNonNull
    public void onFailure(Call<Void> call, Throwable t) {
        callback.fail("Erro de comunicação: " + t.getMessage());
    }

    public interface ResponseCallback {
        void sucess();
        void fail(String error);
    }
}
