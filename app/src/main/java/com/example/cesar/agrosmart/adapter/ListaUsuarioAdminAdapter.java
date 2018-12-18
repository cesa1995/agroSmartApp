package com.example.cesar.agrosmart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.models.fincas.Usuarios;

import java.util.ArrayList;

public class ListaUsuarioAdminAdapter extends RecyclerView.Adapter<ListaUsuarioAdminAdapter.ViewHolder> {

    private ArrayList<Usuarios> dataset;
    private Context context;

    public ListaUsuarioAdminAdapter(Context context){
        this.context = context;
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_usuarios,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuarios U = dataset.get(position);
        String nombreApellido = U.getNombre()+" "+U.getApellido();
        holder.nombreApellido.setText(nombreApellido);
        holder.email.setText(U.getEmail());
        switch (U.getNivel()){
            case "0":{
                holder.nivel.setText(R.string.administrador);
            }break;
            case "1":{
                holder.nivel.setText(R.string.agronomo);
            }break;
            case "2":{
                holder.nivel.setText(R.string.cliente);
            }break;
        }
        holder.id = U.getId();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaUsuarios(ArrayList<Usuarios> listaUsuarios){
        dataset.addAll(listaUsuarios);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nombreApellido, email, nivel;
        private Button borrar;
        private String id;

        public ViewHolder(View itemView){
            super(itemView);

            nombreApellido = itemView.findViewById(R.id.UsuarioNombreApellido);
            email = itemView.findViewById(R.id.UsuarioEmail);
            nivel = itemView.findViewById(R.id.UsuarioNivel);
            borrar = itemView.findViewById(R.id.borrarUsuario);

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
