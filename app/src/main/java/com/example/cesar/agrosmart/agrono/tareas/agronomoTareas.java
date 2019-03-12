package com.example.cesar.agrosmart.agrono.tareas;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cesar.agrosmart.R;

public class agronomoTareas extends Fragment {

    private static final String TAG = "tarea";


    private String jwt, idparcela, ubicacion;
    private Bundle bundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jwt=getArguments().getString("jwt", "vacio");
            idparcela=getArguments().getString("idParcela", "vacio");
            ubicacion=getArguments().getString("Ubicacion", "vacio");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.agronomo_fragment_tareas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle=new Bundle();
        bundle.putString("idParcela", idparcela);
        bundle.putString("Ubicacion",idparcela);
        bundle.putString("jwt", jwt);
        bundle.putString("estado", "1");
        Fragment fragment=new agronomoTareaTodo();
        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        transaction.replace(R.id.content_tareas, fragment);
        transaction.commit();

        BottomNavigationView mTareaNavigationView = view.findViewById(R.id.menuTarea);
        mTareaNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment=null;
                boolean fragmentSelect=false;

                switch (item.getItemId()){
                    case R.id.todo:{
                        bundle.putString("estado", "1");
                        fragment=new agronomoTareaTodo();
                        fragmentSelect=true;
                    }break;
                    case R.id.done:{
                        bundle.putString("estado", "2");
                        fragment=new agronomoTareaDone();
                        fragmentSelect=true;
                    }break;
                    case R.id.dont:{
                        bundle.putString("estado", "0");
                        fragment=new agronomoTareaDont();
                        fragmentSelect=true;
                    }break;
                    case R.id.add:{
                        bundle.putString("tipo", "0");
                        fragment=new agronomoTareaAdd();
                        fragmentSelect=true;
                    }break;
                }
                if (fragmentSelect){
                    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_tareas, fragment);
                    transaction.commit();
                }
                return true;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
