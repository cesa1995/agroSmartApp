package com.example.cesar.agrosmart.api;

import com.example.cesar.agrosmart.apiBody.addEquipoBody;
import com.example.cesar.agrosmart.apiBody.addFincaBody;
import com.example.cesar.agrosmart.apiBody.addParcelaBody;
import com.example.cesar.agrosmart.apiBody.addUsuariosBody;
import com.example.cesar.agrosmart.apiBody.deleteBody;
import com.example.cesar.agrosmart.apiBody.jwtOnlyBody;
import com.example.cesar.agrosmart.apiBody.loginBody;
import com.example.cesar.agrosmart.apiBody.update.updateEquipoBody;
import com.example.cesar.agrosmart.apiBody.update.updateParcelaBody;
import com.example.cesar.agrosmart.apiBody.update.updateUsuarioBody;
import com.example.cesar.agrosmart.apiBody.update.updateUsuariopassBody;
import com.example.cesar.agrosmart.models.LoginRespuesta;
import com.example.cesar.agrosmart.models.equipos.ReadEquiposRespuesta;
import com.example.cesar.agrosmart.models.fincas.ReadFincasRespuesta;
import com.example.cesar.agrosmart.models.parcelas.ReadParcelasRespuesta;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.usuarios.ReadUsuariosRespuesta;

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

    @POST("usuarios/create.php")
    Call<Respuesta>guardasUsuario(@Body addUsuariosBody addUsuariosBody);

    @POST("parcelas/create.php")
    Call<Respuesta>guardarParcela(@Body addParcelaBody addParcelaBody);

    @POST("fincas/create.php")
    Call<Respuesta>guardarFinca(@Body addFincaBody addFincaBody);

    @POST("equipos/create.php")
    Call<Respuesta>guardarEquipo(@Body addEquipoBody addEquipoBody);

    @POST("usuarios/delete.php")
    Call<Respuesta>eliminarUsuario(@Body deleteBody deleteBody);

    @POST("parcelas/delete.php")
    Call<Respuesta>eliminarParcela(@Body deleteBody deleteBody);

    @POST("fincas/delete.php")
    Call<Respuesta>eliminarFinca(@Body deleteBody deleteBody);

    @POST("equipos/delete.php")
    Call<Respuesta>eliminarEquipo(@Body deleteBody deleteBody);

    @POST("usuarios/update.php")
    Call<Respuesta>updateUsuario(@Body updateUsuarioBody updateUsuarioBody);

    @POST("usuarios/updatePass.php")
    Call<Respuesta>updateUsuariopass(@Body updateUsuariopassBody updateUsuariopassBody);

    @POST("equipos/update.php")
    Call<Respuesta>updateEquipo(@Body updateEquipoBody updateEquipoBody);

    @POST("parcelas/update.php")
    Call<Respuesta>updateParcela(@Body updateParcelaBody updateParcelaBody);
}
