package com.example.cesar.agrosmart.interfaces;

import com.example.cesar.agrosmart.admin.IndexAdmin;
import com.example.cesar.agrosmart.admin.adminConfiEquipos;
import com.example.cesar.agrosmart.admin.adminEquipos;
import com.example.cesar.agrosmart.admin.adminFincas;
import com.example.cesar.agrosmart.admin.adminParcela;
import com.example.cesar.agrosmart.admin.adminUsuarios;

public interface IFragments extends
        IndexAdmin.OnFragmentInteractionListener,
        adminFincas.OnFragmentInteractionListener,
        adminUsuarios.OnFragmentInteractionListener,
        adminEquipos.OnFragmentInteractionListener,
        adminParcela.OnFragmentInteractionListener,
        adminConfiEquipos.OnFragmentInteractionListener{
}
