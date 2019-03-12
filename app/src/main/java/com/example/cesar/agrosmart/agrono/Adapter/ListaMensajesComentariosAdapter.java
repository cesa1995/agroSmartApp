package com.example.cesar.agrosmart.agrono.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.agrono.comentarios.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class ListaMensajesComentariosAdapter extends RecyclerView.Adapter<ListaMensajesComentariosAdapter.ViewHolder> {

    private List<Mensaje> mensajeList=new ArrayList<>();
    private Context context;

    public ListaMensajesComentariosAdapter(Context context) {
        this.context = context;
    }

    public void addMensaje(Mensaje mensajeList){
        this.mensajeList.add(mensajeList);
        notifyItemInserted(this.mensajeList.size());
    }

    public void adicionarMensajes(List<Mensaje> listMensajes){
        this.mensajeList.addAll(0,listMensajes);
        notifyDataSetChanged();
    }

    public void deleteMensajes(){
        this.mensajeList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.agronomo_item_comentaios,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje m=mensajeList.get(position);
        holder.nombre.setText(m.getUsuario());
        holder.hora.setText(m.getHora());
        holder.mensaje.setText(m.getMensaje());

    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre, mensaje, hora;

        public ViewHolder(View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.usuario);
            hora=itemView.findViewById(R.id.hora);
            mensaje=itemView.findViewById(R.id.mensaje);



        }
    }
}
