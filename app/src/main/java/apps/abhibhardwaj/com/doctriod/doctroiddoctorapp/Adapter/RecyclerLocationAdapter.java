package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.DoctorTypeListActivity;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.RecyclerItemSelectedListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.City;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.R;
import java.util.List;


public class RecyclerLocationAdapter extends RecyclerView.Adapter<RecyclerLocationAdapter.ViewHolder> {

  Context context;
  List<City> cityList;
  int lastPosition = -1;

  public RecyclerLocationAdapter(Context context,
      List<City> cityList) {
    this.context = context;
    this.cityList = cityList;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_view_location, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
    viewHolder.tvLocation.setText(cityList.get(i).getName());


    setAnimation(viewHolder.itemView, i);


    viewHolder.setItemSelectedListener(new RecyclerItemSelectedListener() {
      @Override
      public void onItemSelectedListener(View view, int position) {
        Common.city_name = cityList.get(i).getName();
        context.startActivity(new Intent(context, DoctorTypeListActivity.class));


      }
    });
  }

  private void setAnimation(View itemView, int position) {
    if (position > lastPosition)
    {
      Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
      itemView.setAnimation(animation);
      lastPosition = position;
    }
  }


  @Override
  public int getItemCount() {
    return cityList.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    TextView tvLocation;

    RecyclerItemSelectedListener itemSelectedListener;

    public void setItemSelectedListener(
        RecyclerItemSelectedListener itemSelectedListener) {
      this.itemSelectedListener = itemSelectedListener;
    }

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      tvLocation = itemView.findViewById(R.id.tv_location);

      itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
      itemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
    }
  }
}
