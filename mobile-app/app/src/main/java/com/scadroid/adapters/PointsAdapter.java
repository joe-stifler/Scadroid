package com.scadroid.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;
import com.rey.material.widget.ImageButton;
import com.scadroid.R;
import com.scadroid.domain.ContextMenuItem;
import com.scadroid.domain.Point;
import com.scadroid.database.DataBase;
import com.scadroid.domain.Profile;
import com.scadroid.interfaces.RecyclerViewOnClickListenerHack;
import com.scadroid.interfaces.UpdatePointInterface;
import com.scadroid.webservice.request.ItemStringValue;
import com.scadroid.webservice.tasks.AsyncTasks;

/**
 * Created by joe on 16/05/15.
 */

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.PointsViewHolder> implements UpdatePointInterface{
    private List<Point> mList; // m é usado para identificar variáveis de instâcia
    //private int[] arrayIcon; // criado para contar um problema onde estava sendo retornado somente um valor de getIcon justamente nos métodos onBindViewHolder e onCreateViewHolder apenas.
    private LayoutInflater mLayoutInflater;
    private Context context;
    private static Handler handler;
    private float scale;
    private int width, height, roundPixels;

    // Este construtor será usado para quando o usuário estiver iniciando o aplciativo e existir banco de dados com pontos
    public PointsAdapter(Context c){
        this.context = c;

        mList = new ArrayList<Point>();

        DataBase db = new DataBase(context);
            for(int i = 0; i < db.searchPoints().size(); i++) {
                mList.add(db.searchPoints().get(i));
            }
        db.closeDataBase();

        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scale = context.getResources().getDisplayMetrics().density;
        width = context.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;

        roundPixels = (int)(2 * scale + 0.5f);
    }

    public PointsAdapter(Context c, Point p){
        this.context = c;

        DataBase db = new DataBase(context);
            mList = new ArrayList<Point>();
        db.insertPoint(p);
            // pegando o id que é definido automaticamente pelo banco de dados
                p.setId(db.searchPoints().get(db.searchPoints().size()-1).getId());
            mList.add(p);
        db.closeDataBase();
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scale = context.getResources().getDisplayMetrics().density;
        width = context.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width / 16) * 9;

        roundPixels = (int)(2 * scale + 0.5f);
    }

    // só é chamado quando é necessário criar uma nova view
    @Override
    public PointsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_point, viewGroup, false);
        PointsViewHolder mvh = new PointsViewHolder(v);

        // debug: Cannot call this method while RecyclerView is computing a layout or scrolling
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                PointsAdapter.this.notifyItemChanged(msg.arg1);
            }
        };

        return mvh;
    }

    // Este método vincula todos os dados com a view
    @Override
    public void onBindViewHolder(PointsViewHolder myViewHolder, final int position) {
        final Point point = mList.get(position);

        myViewHolder.ivPoint.setImageResource(Integer.parseInt(point.getIconString()));
            myViewHolder.ivPoint.setEnabled(point.getWritable());
        if(point.getValue() != null) {
            double val;

            switch(point.getCategoryNumber()){
                case 0:
                    if (point.getType().equals("DOUBLE")) {
                        val = Double.parseDouble(point.getValue());
                        myViewHolder.tvValue.setText("" + String.format("%.2f", val));
                    } else
                        myViewHolder.tvValue.setText(point.getValue());
                    break;
                case 1:
                    val = Double.parseDouble(point.getValue());
                        myViewHolder.tvValue.setText(String.format("%.2f", val) + " L/h");
                    break;
                case 2:
                    val = Double.parseDouble(point.getValue());
                        myViewHolder.tvValue.setText(String.format("%.2f", val) + " ºC");
                    break;
                case 3:
                    if (point.getValue() != null && point.getValue().equals("true"))
                        myViewHolder.tvValue.setText("Active");
                    else
                        myViewHolder.tvValue.setText("Inactive");
                    break;
                case 4:
                    val = Double.parseDouble(point.getValue());
                        myViewHolder.tvValue.setText(String.format("%.2f", val) + " mA");
                    break;
                case 5:
                    val = Double.parseDouble(point.getValue());
                        myViewHolder.tvValue.setText(String.format("%.2f", val) + " lumens");
                    break;
            }
        } else
            myViewHolder.tvValue.setText("Without connection or update.");

        myViewHolder.tvName.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    // adicionando novo ponto ao array para assim atualizar na view
    public void addListItem(Point p){
        DataBase db = new DataBase(context);
            db.insertPoint(p);
        DataBase db2 = new DataBase(context);
        List<Point> array = db2.searchPoints();
            // pegando o id que é definido automaticamente pelo banco de dados
                p.setId(array.get(array.size() -1).getId());
         db.closeDataBase();
        db2.closeDataBase();
            mList.add(p);

        notifyItemInserted(mList.size() - 1);
    }

    public void removeListItem(int position, Point p){
        mList.remove(position);

        DataBase db = new DataBase(context);
        db.deletePoint(p);
            db.closeDataBase();

        notifyItemRemoved(position);
    }

    // modificar o valor de um ponto
    public void setListItem(ItemStringValue item){
        int position = 0;
        Point point;

        for(int i = 0; i < mList.size(); i++){
            if (item.getItemName().equals(mList.get(i).getName())){
                point = mList.get(i);
                point.setType(item.getDataType().toString());
                if(item.getValue() != null)
                    point.setValue(item.getValue());

                position = i;
                mList.set(position, point);
            }
        }

        Message message = Message.obtain();
        message.arg1 = position;

        if(handler != null)
            handler.sendMessage(message);
    }

    // update the point value
    public void updatePoint(ItemStringValue value, String description){
        if(value != null)
            setListItem(value);
    }

    // used to update all points when the fragment returns from a pause
    public void updatePoints(){
        DataBase db = new DataBase(context);
            for(Point p : db.searchPoints())
                for(int x = 0; x < mList.size(); x++)
                    if(mList.get(x).getName().equals(p.getName()))
                        mList.set(x, p);

        db.closeDataBase();
    }

    public List<Point> getListItem(){
        return mList;
    }

    public Point getListItem(int position){
        return mList.get(position);
    }

    public class PointsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

        public ImageButton ivPoint;
        public TextView tvName;
        public TextView tvValue;
        public ImageView ivSettings;

        public PointsViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivPoint = (ImageButton) itemView.findViewById(R.id.iv_point);
                ivPoint.setOnClickListener(this);
            tvValue = (TextView) itemView.findViewById(R.id.tv_value);
            ivSettings = (ImageView) itemView.findViewById(R.id.setting);
                ivSettings.setOnClickListener(this);
        }

        private boolean controllerIv = true;

        @Override
        public void onClick(View v) {

            if( v.getId() == R.id.iv_point){
                ivPoint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // the next condition is used to know if the point is boolean or other kind of object. If it is not boolean, then the dialog its't going to appear
                        if(mList.get(getPosition()).getType().equals("BOOLEAN"))
                            new AsyncTasks(context, mList.get(getPosition()), PointsAdapter.this, mList.get(getPosition()).getValue()).execute(1); // changing the point value
//                        else {
//                            // dialog used to catch the new point value defined by the user
//                            Dialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialog){
//
//                                @Override
//                                protected void onBuildDone(Dialog dialog) {
//                                    dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                }
//
//                                @Override
//                                public void onPositiveActionClicked(DialogFragment fragment) {
//                                    new AsyncTasks(context, mList.get(getPosition()), PointsAdapter.this, ((EditText) fragment.getDialog().findViewById(R.id.custom_et_value)).getText().toString()).execute(1); // changing the point value
//
//                                    super.onPositiveActionClicked(fragment);
//                                }
//
//                                @Override
//                                public void onNegativeActionClicked(DialogFragment fragment) {
//                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
//                                    super.onNegativeActionClicked(fragment);
//                                }
//                            };
//
//                            builder.title("Set point '"+mList.get(getPosition()).getName()+"' value")
//                                    .positiveAction("SET")
//                                    .negativeAction("CANCEL")
//                                    .style(R.style.SimpleDialog)
//                                    .contentView(R.layout.set_value_point);
//
//                            DialogFragment fragment = DialogFragment.newInstance(builder);
//                                fragment.show(((ActionBarActivity) context).getSupportFragmentManager(), null);
//                        }
                    }
                });

                // used to know if it's the firt click or not. If it's, then you call again the click
                if(controllerIv) {
                    ivPoint.performClick();
                    controllerIv = false;
                }
            } else {
                List<ContextMenuItem> itens = new ArrayList<>();
                    itens.add(new ContextMenuItem(R.drawable.ic_setting, "Settings"));
                    itens.add(new ContextMenuItem(R.drawable.ic_chart, "Chart"));
                    itens.add(new ContextMenuItem(R.drawable.ic_delete, "Remove"));

                ContextMenuAdapter adapter = new ContextMenuAdapter(context, itens);

                final com.rey.material.widget.ListPopupWindow listPopupWindow = new com.rey.material.widget.ListPopupWindow(context);
                listPopupWindow.setAnchorView(ivSettings);
                listPopupWindow.setAdapter(adapter);
                listPopupWindow.setWidth((int) (240 * scale + 0.5f));
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent;
                        if (position == 0) {
                            intent = new Intent(context, com.scadroid.activities.PointConfigActivity.class);
                            intent.putExtra("point", mList.get(getPosition()));
                            context.startActivity(intent);
                        } else if (position == 1) {
                            intent = new Intent(context, com.scadroid.activities.PointHistoryActivity.class);
                            intent.putExtra("point", mList.get(getPosition()));
                            context.startActivity(intent);
                        } else {
                            DataBase db = new DataBase(context);
                            db.deletePoint(mList.get(getPosition()));
                            db.closeDataBase();
                            mList.remove(getPosition());
                            notifyItemRemoved(getPosition());
                        }

                        listPopupWindow.dismiss();
                    }
                });
                listPopupWindow.setModal(true);
                listPopupWindow.getBackground().setAlpha(0);
                listPopupWindow.show();
            }
        }

    }
}
