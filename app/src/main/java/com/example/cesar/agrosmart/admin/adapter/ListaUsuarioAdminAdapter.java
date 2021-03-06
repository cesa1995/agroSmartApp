package com.example.cesar.agrosmart.admin.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.admin.update.UpdateUsuario;
import com.example.cesar.agrosmart.api.ApiService;
import com.example.cesar.agrosmart.apiBody.idANDjwtBody;
import com.example.cesar.agrosmart.models.ApiError;
import com.example.cesar.agrosmart.models.respuesta.Respuesta;
import com.example.cesar.agrosmart.models.usuarios.Usuarios;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaUsuarioAdminAdapter extends RecyclerView.Adapter<ListaUsuarioAdminAdapter.ViewHolder> {

    private ArrayList<Usuarios> dataset;
    private ArrayList<Usuarios> datasetFiltered;
    private Context context;
    private String jwt;

    public ListaUsuarioAdminAdapter(Context context, String jwt){
        this.context = context;
        this.jwt = jwt;
        dataset = new ArrayList<>();
        datasetFiltered = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_admin_usuarios,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuarios U = datasetFiltered.get(position);
        holder.Nombre = U.getNombre();
        holder.Apellido = U.getApellido();
        holder.Email = U.getEmail();
        holder.Nivel = U.getNivel();
        holder.id = U.getId();
        holder.position = holder.getAdapterPosition();
        String nombreapellido=holder.Nombre+" "+holder.Apellido;
        holder.mNombreApellidoView.setText(nombreapellido);
        holder.mEmailView.setText(holder.Email);
        switch (holder.Nivel){
            case "0":{
                holder.mNivelView.setText(R.string.administrador);
            }break;
            case "1":{
                holder.mNivelView.setText(R.string.agronomo);
            }break;
            case "2":{
                holder.mNivelView.setText(R.string.cliente);
            }break;
        }
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public void adicionarListaUsuarios(ArrayList<Usuarios> listaUsuarios){
        dataset.addAll(listaUsuarios);
        datasetFiltered = dataset;
        notifyDataSetChanged();
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    datasetFiltered = dataset;
                }else{
                    ArrayList<Usuarios> filteredList = new ArrayList<>();
                    for(Usuarios row : dataset){
                        if (row.getNombre().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getApellido().toLowerCase().contains(charString.toLowerCase())||
                                row.getEmail().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    datasetFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = datasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datasetFiltered = (ArrayList<Usuarios>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public static final String TAG = "recycleview";

        private TextView mNombreApellidoView, mEmailView, mNivelView;
        private Button borrar;
        private String id, Nombre, Apellido, Email, Nivel;
        private int position;
        private View item;
        Retrofit retrofit;

        public ViewHolder(View itemView){
            super(itemView);

            mNombreApellidoView = itemView.findViewById(R.id.UsuarioNombreApellido);
            mEmailView = itemView.findViewById(R.id.UsuarioEmail);
            mNivelView = itemView.findViewById(R.id.UsuarioNivel);
            borrar = itemView.findViewById(R.id.borrarUsuario);
            item = itemView.findViewById(R.id.item);

            borrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    borrarUsuarios();
                }
            });

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle=new Bundle();
                    bundle.putString("jwt", jwt);
                    bundle.putString("id", id);
                    bundle.putString("nombre", Nombre);
                    bundle.putString("apellido", Apellido);
                    bundle.putString("email", Email);
                    bundle.putString("nivel", Nivel);
                    Fragment fragment = new UpdateUsuario();
                    FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.content_main, fragment).addToBackStack(null);
                    transaction.commit();
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://3.16.180.219/agroSmart/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        private void borrarUsuarios(){
            ApiService service = retrofit.create(ApiService.class);
            Call<Respuesta> respuestaCall = service.eliminarUsuario(new idANDjwtBody(id,jwt));

            respuestaCall.enqueue(new Callback<Respuesta>() {
                @Override
                public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                    if (!response.isSuccessful()){
                        String error = "Ha ocurrido un error. Contacte al administrador";
                        if (response.errorBody().contentType().subtype().equals("json")){
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());
                            error = apiError.getMessage();
                            Log.e(TAG, error);
                        }else{
                            try {
                                Log.d(TAG, response.errorBody().toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        showMessage(error);
                        return;
                    }
                    Respuesta respuesta = response.body();
                    dataset.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position,dataset.size());
                    showMessage(respuesta.getMessage());
                }

                @Override
                public void onFailure(Call<Respuesta> call, Throwable t) {

                }
            });
        }

        private void showMessage(String message){
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }
    }
}
