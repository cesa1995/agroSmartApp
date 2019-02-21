package com.example.cesar.agrosmart.admin.Index;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.admin.admin.adminEquipos;
import com.example.cesar.agrosmart.admin.admin.adminFincas;
import com.example.cesar.agrosmart.admin.admin.adminParcela;
import com.example.cesar.agrosmart.admin.admin.adminUsuarios;
import com.example.cesar.agrosmart.admin.asociar.adminAsociarFinca;

public class adminIndex extends Fragment implements View.OnClickListener {

    private String jwt, nombre;
    private CardView finca, parcela, usuario, equipo, vincular;
    private TextView bienvenida;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt","vacio");
            nombre=getArguments().getString("nombre", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.admin_fragment_index, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        finca=view.findViewById(R.id.finca);
        parcela=view.findViewById(R.id.parcela);
        usuario=view.findViewById(R.id.usuario);
        equipo=view.findViewById(R.id.equipo);
        vincular=view.findViewById(R.id.vincular);
        bienvenida=view.findViewById(R.id.bienvenida);

        bienvenida.setText("Bienvenido "+nombre);

        finca.setOnClickListener(this);
        parcela.setOnClickListener(this);
        usuario.setOnClickListener(this);
        equipo.setOnClickListener(this);
        vincular.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle=new Bundle();
        bundle.putString("jwt",jwt);
        Fragment fragment=null;
        boolean fragmentSelect= false;

        switch (v.getId()){
            case R.id.finca:{
                fragment=new adminFincas();
                fragmentSelect=true;
            }break;
            case R.id.parcela:{
                fragment=new adminParcela();
                fragmentSelect=true;
            }break;
            case R.id.usuario:{
                fragment=new adminUsuarios();
                fragmentSelect=true;
            }break;
            case R.id.equipo:{
                fragment=new adminEquipos();
                fragmentSelect=true;
            }break;
            case R.id.vincular:{
                fragment=new adminAsociarFinca();
                fragmentSelect=true;
            }break;
        }
        if (fragmentSelect){
            fragment.setArguments(bundle);
            ((AppCompatActivity)getContext()).getSupportFragmentManager().popBackStack();
            ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
