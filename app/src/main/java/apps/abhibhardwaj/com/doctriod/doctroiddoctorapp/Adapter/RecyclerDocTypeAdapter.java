package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.CustomLoginDialog;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.HomeActivity;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.DialogClickListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.GetDoctorListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.RecyclerItemSelectedListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.UserLoginRememberListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.DocType;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.Doctor;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import dmax.dialog.SpotsDialog;
import java.util.ArrayList;
import java.util.List;

public class RecyclerDocTypeAdapter extends RecyclerView.Adapter<RecyclerDocTypeAdapter.ViewHolder> implements
    DialogClickListener {

  private Context context;
  private List<DocType> doctorTypeList;
  List<CardView> cardViewList;

  UserLoginRememberListener userLoginRememberListener;
  GetDoctorListener getDoctorListener;


  public RecyclerDocTypeAdapter (Context context, List<DocType> doctorTypeList,
      UserLoginRememberListener userLoginRememberListener, GetDoctorListener getDoctorListener) {
    this.context = context;
    this.doctorTypeList = doctorTypeList;
    cardViewList = new ArrayList<>();
    this.userLoginRememberListener = userLoginRememberListener;
    this.getDoctorListener = getDoctorListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

    View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_view_location, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

    viewHolder.tvDocType.setText(doctorTypeList.get(i).getDocType());

    if (!cardViewList.contains(viewHolder.cardView))
      cardViewList.add(viewHolder.cardView);

    viewHolder.setRecyclerItemSelectedListener(new RecyclerItemSelectedListener() {
      @Override
      public void onItemSelectedListener(View view, int position) {

        Common.selectedDocType = doctorTypeList.get(position);
        showLoginDialog();

      }
    });
  }

  private void showLoginDialog() {

    CustomLoginDialog.getInstance().showLoginDialog("DOCTOR LOGIN",
        "LOGIN",
        "CANCEL",
        context,
        this);
  }

  @Override
  public int getItemCount() {
    return doctorTypeList.size();
  }

  @Override
  public void onClickPositiveButton(final DialogInterface dialogInterface, final String username,
      String password) {
    final AlertDialog alertDialog = new SpotsDialog.Builder().setContext(context).setCancelable(false).build();

    alertDialog.show();



    FirebaseFirestore.getInstance()
        .collection("AllDoctors")
        .document(Common.city_name)
        .collection("DocType")
        .document(Common.selectedDocType.getId())
        .collection("AvailableDoctors")
        .whereEqualTo("username", username)
        .whereEqualTo("password", password)
        .limit(1)
        .get()
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            alertDialog.dismiss();
            Common.makeToast(context, e.getMessage());

          }
        })
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful())
            {
              if (task.getResult().size() > 0)
              {
                  dialogInterface.dismiss();
                  alertDialog.dismiss();


                  userLoginRememberListener.onUserLoginSuccess(username);

                Doctor doctor = new Doctor();

                for (DocumentSnapshot documentSnapshot : task.getResult())
                {
                  doctor = documentSnapshot.toObject(Doctor.class);
                  doctor.setId(documentSnapshot.getId());
                }

                getDoctorListener.onGetBarberSuccess(doctor);



                  Intent homeActivity = new Intent(context, HomeActivity.class);
                  homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  context.startActivity(homeActivity);
              }
              else
              {
                alertDialog.dismiss();
                Common.makeToast(context, "Wrong username / password");
              }
            }
          }
        });
  }

  @Override
  public void onClickNegativeButton(DialogInterface dialogInterface) {
      dialogInterface.dismiss();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    TextView tvDocType;
    CardView cardView;

    RecyclerItemSelectedListener recyclerItemSelectedListener;

    public void setRecyclerItemSelectedListener(
        RecyclerItemSelectedListener recyclerItemSelectedListener) {
      this.recyclerItemSelectedListener = recyclerItemSelectedListener;
    }

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      tvDocType = itemView.findViewById(R.id.tv_location);
      cardView = itemView.findViewById(R.id.card_view_holder);

      itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
      recyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
    }


  }
}