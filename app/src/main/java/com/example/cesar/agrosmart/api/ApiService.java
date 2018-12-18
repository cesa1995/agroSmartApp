package com.example.cesar.agrosmart.api;



import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.apiBody.loginBody;
import com.example.cesar.agrosmart.models.LoginRespuesta;
import com.example.cesar.agrosmart.models.fincas.ReadEquiposRespuesta;
import com.example.cesar.agrosmart.models.fincas.ReadFincasRespuesta;
import com.example.cesar.agrosmart.models.fincas.ReadParcelasRespuesta;
import com.example.cesar.agrosmart.models.fincas.ReadUsuariosRespuesta;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("usuarios/login.php")
    Call<LoginRespuesta>obtenerJWT(@Body loginBody loginBody);

    @POST("fincas/read.php")
    Call<ReadFincasRespuesta>obtenerFincas(@Body jwtOnlyBody jwtOnlyBody);

    @POST("usuarios/read.php")
    Call<ReadUsuariosRespuesta>obtenerUsuarios(@Body jwtOnlyBody jwtOnlyBody);

    @POST("equipos/read.php")
    Call<ReadEquiposRespuesta>obtenerEquipos(@Body jwtOnlyBody jwtOnlyBody);

    @POST("parcelas/read.php")
    Call<ReadParcelasRespuesta>obtenerParcelas(@Body jwtOnlyBody jwtOnlyBody);
}
