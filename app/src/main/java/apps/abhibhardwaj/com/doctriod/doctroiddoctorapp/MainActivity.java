package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Adapter.RecyclerLocationAdapter;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.SpaceItemDecoration;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.OnAllCityLoadListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.City;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.DocType;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnAllCityLoadListener {


  private RecyclerView recyclerView;

  private CollectionReference allCityRef;

  private OnAllCityLoadListener onAllCityLoadListener;

  private RecyclerLocationAdapter adapter;

  private AlertDialog dialog;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Paper.init(this);
    String user = Paper.book().read(Common.LOGGED_KEY);
    if (TextUtils.isEmpty(user))
    {
      setContentView(R.layout.activity_main);

      initViews();
      iniFireStore();
      loadAllCitiesFromFireStore();
    }
    else
    {
      Gson gson = new Gson();
      Common.city_name = Paper.book().read(Common.CITY_KEY);

      Common.selectedDocType = gson.fromJson(Paper.book().read(Common.DOCTYPE_KEY, ""),
          new TypeToken<DocType>(){}.getType());

      Common.currentDoctor = gson.fromJson(Paper.book().read(Common.DOCTOR_KEY, ""),
          new TypeToken<Doctor>(){}.getType());

      Intent intent = new Intent(this, HomeActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finish();
    }

  }



  private void iniFireStore() {
    allCityRef = FirebaseFirestore.getInstance().collection("AllDoctors");
    onAllCityLoadListener = this;
    dialog = new SpotsDialog.Builder().setCancelable(false)
        .setContext(this)
        .build();

  }

  private void loadAllCitiesFromFireStore() {

    dialog.show();

    allCityRef.get().addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        onAllCityLoadListener.onAllStateLoadFailed(e.getMessage());
      }
    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {

        if (task.isSuccessful())
        {

          List<City> cityList = new ArrayList<>();

          for (QueryDocumentSnapshot citySnapShot : task.getResult())
          {
            City city = citySnapShot.toObject(City.class);
            cityList.add(city);
          }
          onAllCityLoadListener.onAllCityLoadSuccess(cityList);
        }

      }
    });



  }



  private void initViews() {
    recyclerView = findViewById(R.id.recycler_view_location);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    recyclerView.addItemDecoration(new SpaceItemDecoration(8));

  }

  @Override
  public void onAllCityLoadSuccess(List<City> cityList) {
    adapter = new RecyclerLocationAdapter(MainActivity.this, cityList);
    recyclerView.setAdapter(adapter);
    Common.makeToast(MainActivity.this, "Adapter set ho gya");
    dialog.dismiss();
  }

  @Override
  public void onAllStateLoadFailed(String message) {
    Common.makeToast(MainActivity.this, message);
    dialog.dismiss();
  }
}

