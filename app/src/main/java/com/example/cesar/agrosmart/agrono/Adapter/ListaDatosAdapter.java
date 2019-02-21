package com.example.cesar.agrosmart.agrono.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesar.agrosmart.MainActivity;
import com.example.cesar.agrosmart.R;
import com.example.cesar.agrosmart.models.datos.variables;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;


public class ListaDatosAdapter extends RecyclerView.Adapter<ListaDatosAdapter.ViewHolder> {

    private ArrayList<variables> dataset;
    private ArrayList<variables> datasetFiltered;
    private String jwt, idEquipo, dataMqtt;
    private Context context;

    public ListaDatosAdapter(String jwt, String idEquipo, Context context) {
        this.jwt = jwt;
        this.idEquipo = idEquipo;
        this.context = context;
        dataset=new ArrayList<>();
        datasetFiltered=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.agronomo_item_datos,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        variables V=datasetFiltered.get(position);
        holder.mVariableView.setText(V.getVariables());

        if (dataMqtt!=null){
            int result;
            try {
                JSONObject datos = new JSONObject(dataMqtt);
                holder.data=datos.getString(V.getVariables());
                Double dom=Double.parseDouble(holder.data);
                result=(int)dom.doubleValue();
                holder.reloj.setProgress(result);
                holder.mDatoView.setText(holder.data);
                holder.asy();
            }catch (Exception e){
                Log.d("REad", e.getLocalizedMessage());
            }
        }
    }

    public void dataMqtt(String data){
        dataMqtt=data;
        notifyDataSetChanged();
    }

    public void adicionarListaVariablesDatos(ArrayList<variables> listaVariables){
        dataset.addAll(listaVariables);
        datasetFiltered=dataset;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datasetFiltered.size();
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString=constraint.toString();
                if (charString.isEmpty()){
                    datasetFiltered=dataset;
                }else{
                    ArrayList<variables> filteredList=new ArrayList<>();
                    for(variables row : dataset){
                        if (row.getVariables().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    datasetFiltered=filteredList;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=datasetFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                datasetFiltered=(ArrayList<variables>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private static final String TAG="charts";

        private TextView mDatoView, mVariableView;
        private ProgressBar reloj;
        private GraphView grafica;
        private LineGraphSeries<DataPoint> mSerie1;
        private String data;
        private int i;

        public ViewHolder(View itemView) {
            super(itemView);
            mVariableView=itemView.findViewById(R.id.variable);
            mDatoView=itemView.findViewById(R.id.dato);
            reloj=itemView.findViewById(R.id.reloj);

            grafica=itemView.findViewById(R.id.grafica);

            mSerie1=new LineGraphSeries<>();
            grafica.getGridLabelRenderer().setHorizontalLabelsVisible(false);
            grafica.getViewport().setXAxisBoundsManual(true);
            grafica.getViewport().setMinX(0);
            grafica.getViewport().setMaxX(100);
            grafica.getViewport().setYAxisBoundsManual(true);
            grafica.getViewport().setMaxY(100);
            grafica.getViewport().setMinY(0);


        }

        public void asy(){
            new addData().execute();
        }

        private class addData extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                mSerie1.appendData(new DataPoint(i,Double.parseDouble(data)),true, 100);
                i++;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                grafica.addSeries(mSerie1);
            }
        }

    }
}
