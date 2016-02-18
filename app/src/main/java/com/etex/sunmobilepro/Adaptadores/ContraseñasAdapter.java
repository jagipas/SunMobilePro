package com.etex.sunmobilepro.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.etex.sunmobilepro.Fragments.ContraseñasFragment;
import com.etex.sunmobilepro.MainActivity;
import com.etex.sunmobilepro.R;
import java.util.Collections;
import java.util.List;

/**
 * Created by javi on 6/08/15.
 */
public class ContraseñasAdapter extends RecyclerView.Adapter<ContraseñasAdapter.ViewHolder>{

    private final static String TAG = ContraseñasFragment.class.getSimpleName();

    private static final int TYPE_NORMAL = 1;
    private static final int TYPE_SELECCIONADO = 2;

    private LayoutInflater inflater;
    Context context;
    // lista de objetos a mostrar en la lista
    List<EntradaListaCont> data = Collections.emptyList();
    // lista de objetos seleccionados
    private SparseBooleanArray seleccionados;
    private boolean modoSeleccion;
    private int tipoView;
    private ContraseñasFragment cf;

    public ContraseñasAdapter(Context context, List<EntradaListaCont> data){

        inflater= LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        seleccionados = new SparseBooleanArray();
        tipoView=1;
        try {
            if(context instanceof Activity){

                MainActivity main = (MainActivity) context;
                cf = (ContraseñasFragment)main.pagesAdapter.getFragment(2);
                main = null;


            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }




    }

    @Override
    public int getItemViewType(int position) {

        int ret = seleccionados.get(position) ? TYPE_SELECCIONADO : TYPE_NORMAL;
        //Log.d("SunApp", "getItemViewType() position:" + position + " return: " + ret);
        return ret;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder()");

        ViewGroup view;
        switch (viewType){

            case TYPE_SELECCIONADO:
                view = (ViewGroup)inflater.inflate(R.layout.custom_row_edit, parent, false);
                MyViewHolderS vhSeleccionado = new MyViewHolderS(view);
                return vhSeleccionado;
            case TYPE_NORMAL:
                view = (ViewGroup)inflater.inflate(R.layout.custom_row, parent, false);
                MyViewHolderN vhNormal = new MyViewHolderN(view);
                return vhNormal;
            default:
                view = (ViewGroup)inflater.inflate(R.layout.custom_row, parent, false);
                MyViewHolderN vhNormal0 = new MyViewHolderN(view);
                return vhNormal0;

        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        //Log.d("SunApp", "onBindViewHolder() position:" + position);

        EntradaListaCont current = data.get(position);

        switch (viewHolder.getItemViewType()){

            case TYPE_SELECCIONADO:
                MyViewHolderS vhSeleccionado = (MyViewHolderS) viewHolder;
                vhSeleccionado.bindView(current);
                break;

            case TYPE_NORMAL:
                MyViewHolderN vhNormal = (MyViewHolderN) viewHolder;
                vhNormal.bindView(current);
                break;
        }
        /*holder.password.setText(current.contraseña);
        holder.icon.setImageResource(current.iconId);*/

    }

    @Override
    public int getItemCount() {

        return data.size();
    }


    public void delete(int position){                    // metodo para borrar contraseña
       /* EntradaListaCont borrado = data.get(position);  // guradamos el item
        borrado.contraseña = "borrado";                 // lo borramos

        data.set(position, borrado);*/
        seleccionados.put(position, false);
        notifyItemChanged(position);

        char[]contBorrada = {'F','F','F','F'};
        cf.enviarContraseña(contBorrada, position);
    }

    public void modificarCont(CharSequence newCont, int position) {   // metodo para modificar contraseña
        /*EntradaListaCont itemActivado = data.get(position);  // guradamos el item
        itemActivado.contraseña=newCont.toString();
        data.set(position, itemActivado);*/
        seleccionados.put(position, false);

        notifyItemChanged(position);
        char [] contChar = newCont.toString().toCharArray();
        cf.enviarContraseña(contChar, position);

    }

    public void contraseñaModificadaOk(char[] modCont , int position){
        EntradaListaCont itemActivado = data.get(position);  // guradamos el item
        itemActivado.contraseña=String.copyValueOf(modCont);
        data.set(position, itemActivado);
        notifyItemChanged(position);

    }

    public void actualizarConstraseñas(String[] cont){
        for(int i = 0; i<data.size();i++){
            EntradaListaCont entrada = data.get(i);
            if(cont[i].equals("FFFF")){
                entrada.contraseña="vacia";
            }else
                entrada.contraseña=cont[i];
            data.set(i, entrada);

        }
            notifyDataSetChanged();


    }








    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder ( View itemView ) {
            super ( itemView );
        }
    }







    class MyViewHolderN extends ViewHolder {

        TextView password;
        ImageView icon;
        //ImageView iconoBorrar;
        //ImageView iconoMod;
        private View item;    // objeto de la lista, custom_row

        public MyViewHolderN(View itemView) {

            super(itemView);

            this.item = itemView;

            password = (TextView)itemView.findViewById(R.id.entrada_contraseña);

            icon = (ImageView)itemView.findViewById(R.id.listIcon);

            //icon.setOnClickListener(this);

        }

        public void bindView(EntradaListaCont ent){
            password.setText(ent.contraseña);
            icon.setImageResource(ent.iconId);
            //if(ent.iconBorrar==1){iconoBorrar.setVisibility(View.VISIBLE);}
            //if(ent.iconMod==1){iconoMod.setVisibility(View.VISIBLE);}
            //
            //
            // aqui si esta seleccionado la cosa seria posar la creu y el llapis rollo if(seleccionado.get(gespos posa les imatges
            if(seleccionados.get(getAdapterPosition())){
                item.setSelected(true);

                //iconoBorrar.setImageResource(ent.iconBorrar);
                //iconoMod.setImageResource(ent.iconMod);

            }else
                item.setSelected(false);

            // activa el modo seleccion
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //if (!modoSeleccion) {
                        //modoSeleccion = true;
                        v.setSelected(true);
                        seleccionados.put(getAdapterPosition(), true);
                        //itemActivo(getAdapterPosition());
                        notifyItemChanged(getAdapterPosition());

                   // }
                    return true;
                }
            });

           /* item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    *//*if(modoSeleccion){
                        if(!v.isSelected()){
                            v.setSelected(true);
                            seleccionados.put(getAdapterPosition(), true);

                        }else {*//*
                            v.setSelected(false);
                            seleccionados.put(getAdapterPosition(), false);
                    notifyItemChanged(getAdapterPosition());
                            *//*if (!haySeleccionados())
                                modoSeleccion = false;
                        }
                    }*//*
                }
            });*/
        }

        public boolean haySeleccionados() {
            for (int i = 0; i <= data.size(); i++) {
                if (seleccionados.get(i))
                    return true;
            }
            return false;
        }


    }









    class MyViewHolderS extends ViewHolder {

        TextView password;
        ImageView icon;
        ImageView iconoBorrar;
        ImageView iconoMod;
        private View item;    // objeto de la lista, custom_row

        public MyViewHolderS(View itemView) {
            super(itemView);

            this.item = itemView;      // linearlayout de la entrada de la lista

            password = (TextView)itemView.findViewById(R.id.entrada_contraseña1);
            icon = (ImageView)itemView.findViewById(R.id.listIcon1);

            //icon.setOnClickListener(this);

            iconoBorrar =(ImageView)itemView.findViewById(R.id.iconBorrar);
            iconoMod = (ImageView)itemView.findViewById(R.id.iconMod);

        }

        public void bindView(EntradaListaCont ent){

            password.setText("pass");
            icon.setImageResource(ent.iconId);

            // aqui si esta seleccionado la cosa seria posar la creu y el llapis rollo if(seleccionado.get(gespos posa les imatges
            if(seleccionados.get(getAdapterPosition())){
                item.setSelected(true);

            }else
                item.setSelected(false);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setSelected(false);
                    seleccionados.put(getAdapterPosition(), false);
                    notifyItemChanged(getAdapterPosition());

                }
            });


            iconoBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    delete(getAdapterPosition());
                }
            });

            iconoMod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new MaterialDialog.Builder(context)
                            .title("Introduce nueva contraseña")
                            .inputRangeRes(4, 4, R.color.material_red_error)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {

                                    modificarCont(charSequence,getAdapterPosition());

                                }
                            }).show();
                }
            });
        }

    }
}
