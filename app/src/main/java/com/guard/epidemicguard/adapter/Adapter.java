package com.guard.epidemicguard.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guard.epidemicguard.R;
import com.guard.epidemicguard.model.Casos;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Casos> listCasos;

    public Adapter(List<Casos> list) {
        this.listCasos = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemCasos = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_casos, parent, false);

        return new MyViewHolder(itemCasos);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Casos casos = listCasos.get(position);
        holder.nome.setText(casos.getNome());
        holder.endereco.setText(casos.getEndereco());
        holder.descricao.setText(casos.getDescricao());

    }

    @Override
    public int getItemCount() {
        return listCasos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome;
        TextView endereco;
        TextView descricao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textNomeList);
            endereco = itemView.findViewById(R.id.textEndereçoList);
            descricao = itemView.findViewById(R.id.textDescricaoList);

        }
    }
}
