package com.example.cesar.agrosmart.agrono.listas;

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

import com.example.cesar.agrosmart.MainActivity;
import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.comentarios.agronomoComentarios;
import com.example.cesar.agrosmart.agrono.tareas.agronomoTareas;

public class dashboard extends Fragment implements View.OnClickListener{

    private String idParcela, jwt, ubicacion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            idParcela=getArguments().getString("idParcela", "vacio");
            jwt=getArguments().getString("jwt", "vacio");
            ubicacion=getArguments().getString("Ubicacion", "vacio");
        }
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.dashboard));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.dashboard));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CardView mComentariosButton = view.findViewById(R.id.comentarios);
        CardView mTareasButton = view.findViewById(R.id.tareas);
        CardView mDatosButton = view.findViewById(R.id.datos);
        TextView mUbicacionView=view.findViewById(R.id.ubicacion);

        mUbicacionView.setText(ubicacion);

        mComentariosButton.setOnClickListener(this);
        mTareasButton.setOnClickListener(this);
        mDatosButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Bundle bundle=new Bundle();
        bundle.putString("jwt", jwt);
        bundle.putString("idParcela", idParcela);
        bundle.putString("Ubicacion", ubicacion);

        Fragment fragment=null;
        boolean fragmentSelect=false;

        switch (v.getId()){
            case R.id.comentarios:{
                fragment= new agronomoComentarios();
                fragmentSelect=true;
            }break;
            case R.id.tareas:{
                fragment=new agronomoTareas();
                fragmentSelect=true;
            }break;
            case R.id.datos:{
                fragment=new equiposSelect();
                fragmentSelect=true;
            }break;
        }
        if (fragmentSelect){
            fragment.setArguments(bundle);
            ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
