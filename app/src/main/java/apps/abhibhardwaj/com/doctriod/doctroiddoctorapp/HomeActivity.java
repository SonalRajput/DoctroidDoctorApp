package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp;

import static java.security.AccessController.getContext;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Adapter.TimeSlotAdapter;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.SpaceItemDecoration;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.TimeSlotLoadListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.AppointmentInformation;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.TimeSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendar.Mode;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements TimeSlotLoadListener {


  private DrawerLayout drawerLayout;
  private NavigationView navigationView;
  private ActionBarDrawerToggle actionBarDrawerToggle;
  private RecyclerView recyclerView;


  private TimeSlotLoadListener timeSlotLoadListener;
  private DocumentReference doctorRef;
  private android.app.AlertDialog dialog;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    initViews();
  }

  private void initViews() {
    navigationView = findViewById(R.id.navigation_view);
    drawerLayout = findViewById(R.id.drawer_layout);
    timeSlotLoadListener = this;

    actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
        R.string.open,
        R.string.close);

    drawerLayout.addDrawerListener(actionBarDrawerToggle);
    actionBarDrawerToggle.syncState();
    getSupportActionBar().setDisplayHomeAsUpEnabled (true);


    navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.item_exit)
        {
          logout();
        }


        return true;
      }
    });









    dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

    Calendar date = Calendar.getInstance();
    date.add(Calendar.DATE, 0);
    loadAvailableTimeSlotOfDoctor(Common.currentDoctor.getId(),
        Common.simpleDateFormat.format(date.getTime()));


    recyclerView = findViewById(R.id.recycler_view_time_slot);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    recyclerView.addItemDecoration(new SpaceItemDecoration(8));

    //calendar
    Calendar startDate = Calendar.getInstance();
    startDate.add(Calendar.DATE, 0);
    Calendar endDate = Calendar.getInstance();
    endDate.add(Calendar.DATE, 2); // 2 day left


    HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this,
        R.id.horizontal_calendar_view)
        .range(startDate, endDate)
        .datesNumberOnScreen(1)
        .mode(Mode.DAYS)
        .defaultSelectedDate(startDate)
        .build();


    horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
      @Override
      public void onDateSelected(Calendar date, int position) {
        if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis())
        {
          Common.bookingDate = date;
          loadAvailableTimeSlotOfDoctor(Common.currentDoctor.getId(), Common.simpleDateFormat.format(date.getTime()));
        }
      }
    });
  }

  private void logout() {
    Paper.init(this);
    Paper.book().delete(Common.DOCTYPE_KEY);
    Paper.book().delete(Common.DOCTOR_KEY);
    Paper.book().delete(Common.CITY_KEY);
    Paper.book().delete(Common.LOGGED_KEY);


    new AlertDialog.Builder(this)
        .setMessage("Are you sure you want to Logout ??")
        .setCancelable(false)
        .setPositiveButton("Yes", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
          }
        })
        .setNegativeButton("Cancel", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

          }
        }).show();


  }

  private void loadAvailableTimeSlotOfDoctor(final String doctorID, final String bookDate) {
     dialog.show();

    doctorRef = FirebaseFirestore.getInstance()
        .collection("AllDoctors")
        .document(Common.city_name)
        .collection("DocType")
        .document(Common.selectedDocType.getId())
        .collection("AvailableDoctors")
        .document(doctorID);


    doctorRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful())
        {
          DocumentSnapshot documentSnapshot = task.getResult();
          if (documentSnapshot.exists())
          {
            CollectionReference date = FirebaseFirestore.getInstance()
                .collection("AllDoctors")
                .document(Common.city_name)
                .collection("DocType")
                .document(Common.selectedDocType.getId())
                .collection("AvailableDoctors")
                .document(doctorID)
                .collection(bookDate);


            date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful())
                {
                  QuerySnapshot querySnapshot = task.getResult();

                  if (querySnapshot.isEmpty())
                  {
                    timeSlotLoadListener.onTimeSlotLoadEmpty();
                  }
                  else
                  {
                    List<AppointmentInformation> timeSlots = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult())
                      timeSlots.add(document.toObject(AppointmentInformation.class));
                    timeSlotLoadListener.onTimeSlotLoadSuccess(timeSlots);
                  }
                }

              }
            }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                timeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
              }
            });
          }

          else
          {
            //
          }

        }
      }
    });


  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (actionBarDrawerToggle.onOptionsItemSelected(item))
      return true;
    return super.onOptionsItemSelected(item);

  }

  @Override
  public void onBackPressed() {
    new AlertDialog.Builder(this)
        .setMessage("Are you sure you want to exit ??")
        .setCancelable(false)
        .setPositiveButton("Yes", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Common.makeToast(HomeActivity.this, "Yes clicked");
          }
        })
        .setNegativeButton("Cancel", new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Common.makeToast(HomeActivity.this, "Cancel Clicked");
          }
        }).show();
  }

  @Override
  public void onTimeSlotLoadSuccess(List<AppointmentInformation> timeSlotList) {

    TimeSlotAdapter adapter = new TimeSlotAdapter(this, timeSlotList);
    recyclerView.setAdapter(adapter);
    dialog.dismiss();
  }

  @Override
  public void onTimeSlotLoadFailed(String message) {
    dialog.dismiss();
    Common.makeToast(this, message);
  }

  @Override
  public void onTimeSlotLoadEmpty() {
    TimeSlotAdapter adapter = new TimeSlotAdapter(this);
    recyclerView.setAdapter(adapter);
    dialog.dismiss();
  }
}
