package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.AppointmentDetailsActivity;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.RecyclerItemSelectedListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.AppointmentInformation;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.TimeSlot;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

  Context context;
  List<AppointmentInformation> timeSlotList;
  List<CardView> cardViewList;

  public TimeSlotAdapter(Context context) {
    this.context = context;
    this.timeSlotList = new ArrayList<>();
    cardViewList = new ArrayList<>();
  }

  public TimeSlotAdapter(Context context,
      List<AppointmentInformation> timeSlotList) {
    this.context = context;
    this.timeSlotList = timeSlotList;
    cardViewList = new ArrayList<>();
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context)
        .inflate(R.layout.layout_recycler_view_time_slot, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

    viewHolder.tvTime.setText(new StringBuilder(Common.convertTimeSlotToString(i)));
    if (timeSlotList.size() == 0) {
      viewHolder.cardTimeSlot
          .setCardBackgroundColor(context.getResources().getColor(R.color.colorWhite));
      viewHolder.tvDesc.setText("Available");
      viewHolder.tvDesc.setTextColor(context.getResources().getColor(R.color.colorGreen));
      viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.colorBlue));
      //viewHolder.cardTimeSlot.setEnabled(true);

      viewHolder.setRecyclerItemSelectedListener(new RecyclerItemSelectedListener() {
        @Override
        public void onItemSelectedListener(View view, int position) {

        }
      });




    } else {
      for (final AppointmentInformation slotValue : timeSlotList) {
        int slot = Integer.parseInt(slotValue.getSlot().toString());
        if (slot == i) {
          viewHolder.cardTimeSlot.setTag(Common.DISABLE_TAG);
          viewHolder.cardTimeSlot
              .setCardBackgroundColor(context.getResources().getColor(R.color.colorGrey));
          viewHolder.tvDesc.setText("Full");
          viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.colorWhite));
          viewHolder.tvDesc.setTextColor(context.getResources().getColor(R.color.colorWhite));
          //viewHolder.cardTimeSlot.setEnabled(false);


          viewHolder.setRecyclerItemSelectedListener(new RecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {

              FirebaseFirestore.getInstance()
                  .collection("AllDoctors")
                  .document(Common.city_name)
                  .collection("DocType")
                  .document(Common.selectedDocType.getId())
                  .collection("AvailableDoctors")
                  .document(Common.currentDoctor.getId())
                  .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                  .document(slotValue.getSlot().toString())
                  .get()
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Common.makeToast(context, e.getMessage());
                    }
                  })
                  .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                      if (task.isSuccessful())
                      {
                        if (task.getResult().exists())
                        {
                          Common.currentAppointmentInformation = task.getResult().toObject(AppointmentInformation.class);
                          context.startActivity(new Intent(context, AppointmentDetailsActivity.class));

                        }

                      }


                    }
                  });

            }
          });

        }
        else
        {
          viewHolder.setRecyclerItemSelectedListener(new RecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {

            }
          });
        }
      }
    }

    if (!cardViewList.contains(viewHolder.cardTimeSlot))
      cardViewList.add(viewHolder.cardTimeSlot);



  }

  @Override
  public int getItemCount() {
    return Common.TIME_SLOT_TOTAL;
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{

    CardView cardTimeSlot;
    TextView tvTime, tvDesc;

    RecyclerItemSelectedListener recyclerItemSelectedListener;

    public void setRecyclerItemSelectedListener(
        RecyclerItemSelectedListener recyclerItemSelectedListener) {
      this.recyclerItemSelectedListener = recyclerItemSelectedListener;
    }

    public ViewHolder(@NonNull View itemView) {
      super(itemView);

      cardTimeSlot = itemView.findViewById(R.id.card_time);
      tvTime = itemView.findViewById(R.id.time_slot_time);
      tvDesc = itemView.findViewById(R.id.time_slot_desc);

      itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
      recyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
    }
  }
}

