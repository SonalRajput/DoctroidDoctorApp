package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppointmentDetailsActivity extends AppCompatActivity {

  private CircleImageView userImage;
  private TextView userName, userMail, userPhone, userAddress, aptTiming;
  private User user;
  private Button btnAddPrescription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_appointment_details);

    initViews();
    setCustomerInformation();
  }

  private void initViews() {
    userImage = findViewById(R.id.user_img_view);
    userName = findViewById(R.id.user_name);
    userMail = findViewById(R.id.user_email);
    userAddress = findViewById(R.id.user_address);
    userPhone = findViewById(R.id.user_phone);
    aptTiming = findViewById(R.id.apt_timing);
    btnAddPrescription = findViewById(R.id.add_pres);

    btnAddPrescription.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AppointmentDetailsActivity.this, AddPrescriptionActivity.class));
      }
    });


  }

  private void setCustomerInformation() {

    String userID = Common.currentAppointmentInformation.getUserID();

    FirebaseFirestore.getInstance()
        .collection("users")
        .document(userID)
        .get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
              user = task.getResult().toObject(User.class);

              userMail.setText(user.getEmail());
              userAddress.setText(user.getAddress());

              userName.setText(Common.currentAppointmentInformation.getCustomerName());
              userPhone.setText(Common.currentAppointmentInformation.getCustomerPhone());

              aptTiming.setText(Common.currentAppointmentInformation.getTime());
              Picasso.get().load(user.getProfileImageURL()).into(userImage);
            }
          }
        });



  }
}
