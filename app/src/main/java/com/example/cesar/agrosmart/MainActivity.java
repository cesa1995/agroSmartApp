package com.example.cesar.agrosmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cesar.agrosmart.admin.Index.IndexAdmin;
import com.example.cesar.agrosmart.admin.ListasGenerales.fincasListaGetID;
import com.example.cesar.agrosmart.admin.admin.adminConfiEquipos;
import com.example.cesar.agrosmart.admin.admin.adminEquipos;
import com.example.cesar.agrosmart.admin.admin.adminFincas;
import com.example.cesar.agrosmart.admin.admin.adminParcela;
import com.example.cesar.agrosmart.admin.admin.adminUsuarios;
import com.example.cesar.agrosmart.agrono.IndexAgrono;
import com.example.cesar.agrosmart.client.IndexClient;
import com.example.cesar.agrosmart.interfaces.IFragments;
import com.example.cesar.agrosmart.session.sessionPrefs;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,IFragments {

    String nombre,apellido,nivel,jwt;
    Toolbar toolbar;
    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = bundle();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (!sessionPrefs.get(this).isLoggedIn()){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        Index();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getText(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "hola",Toast.LENGTH_SHORT).show();
                searchView.setQuery("",false);
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.salir:{
                sessionPrefs.get(getApplicationContext()).logOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }break;
        }

        return true;
    }

    private Bundle bundle(){
        SharedPreferences preferences = getSharedPreferences("SESSION_USUARIO", MODE_PRIVATE);
        this.nombre = preferences.getString("nombre", null);
        this.nivel = preferences.getString("nivel",null);
        this.apellido = preferences.getString("apellido", null);
        this.jwt = preferences.getString("jwt", null);

        Bundle bundle = new Bundle();
        bundle.putString("nombre", nombre);
        bundle.putString("apellido", apellido);
        bundle.putString("nivel", nivel);
        bundle.putString("jwt", jwt);

        return bundle;
    }

    private void Index(){

        Fragment fragment = null;
        boolean fragmentSelect = false;

        switch (nivel){
            case "0": {
                fragment = new IndexAdmin();
                fragmentSelect = true;

                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.inflateMenu(R.menu.activity_main_drawer);
                navigationView.setNavigationItemSelectedListener(this);
            }break;
            case "1": {
                fragment = new IndexAgrono();
                fragmentSelect = true;
            }break;
            case "2": {
                fragment = new IndexClient();
                fragmentSelect = true;
            }break;
        }

        if (fragmentSelect){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment.setArguments(bundle);
            transaction.replace(R.id.content_main, fragment);
            transaction.commit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment fragment=null;
        boolean fragmentSelect=false;

        switch (id){
            case R.id.inicio:{
                fragment = new IndexAdmin();
                fragmentSelect=true;
            }break;
            case R.id.fincas:{
                fragment = new adminFincas();
                fragmentSelect = true;
            }break;
            case R.id.parcelas:{
                fragment = new adminParcela();
                fragmentSelect = true;
            }break;
            case R.id.equipos:{
                fragment = new adminEquipos();
                fragmentSelect = true;
            }break;
            case R.id.usuarios:{
                fragment = new adminUsuarios();
                fragmentSelect = true;
            }break;
            case R.id.config_equipos:{
                fragment = new adminConfiEquipos();
                fragmentSelect = true;
            }break;
        }

        if (fragmentSelect){
            fragment.setArguments(bundle);
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).addToBackStack(null).commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
