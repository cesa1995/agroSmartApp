package com.example.cesar.agrosmart.interfaces;

import com.example.cesar.agrosmart.admin.Index.IndexAdmin;
import com.example.cesar.agrosmart.admin.add.addEquipos;
import com.example.cesar.agrosmart.admin.add.addFincas;
import com.example.cesar.agrosmart.admin.add.addParcelas;
import com.example.cesar.agrosmart.admin.add.addUsuarios;
import com.example.cesar.agrosmart.admin.admin.adminConfiEquipos;
import com.example.cesar.agrosmart.admin.admin.adminEquipos;
import com.example.cesar.agrosmart.admin.admin.adminFincas;
import com.example.cesar.agrosmart.admin.admin.adminParcela;
import com.example.cesar.agrosmart.admin.admin.adminUsuarios;
import com.example.cesar.agrosmart.admin.update.UpdateEquipo;
import com.example.cesar.agrosmart.admin.update.UpdateParcela;
import com.example.cesar.agrosmart.admin.update.UpdateUsuario;
import com.example.cesar.agrosmart.admin.update.UpdateUsuariopass;

public interface IFragments extends
        IndexAdmin.OnFragmentInteractionListener,
        adminFincas.OnFragmentInteractionListener,
        adminUsuarios.OnFragmentInteractionListener,
        adminEquipos.OnFragmentInteractionListener,
        adminParcela.OnFragmentInteractionListener,
        adminConfiEquipos.OnFragmentInteractionListener,
        addUsuarios.OnFragmentInteractionListener,
        addFincas.OnFragmentInteractionListener,
        addEquipos.OnFragmentInteractionListener,
        addParcelas.OnFragmentInteractionListener,
        UpdateUsuario.OnFragmentInteractionListener,
        UpdateUsuariopass.OnFragmentInteractionListener,
        UpdateEquipo.OnFragmentInteractionListener,
        UpdateParcela.OnFragmentInteractionListener
        {

}