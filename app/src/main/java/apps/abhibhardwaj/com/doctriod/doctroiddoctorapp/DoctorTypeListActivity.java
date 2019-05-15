package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Adapter.RecyclerDocTypeAdapter;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.SpaceItemDecoration;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.DocTypeLoadListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.GetDoctorListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.OnLoadCountDocType;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.UserLoginRememberListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.DocType;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.Doctor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import java.util.ArrayList;
import java.util.List;

public class DoctorTypeListActivity extends AppCompatActivity implements OnLoadCountDocType,
    DocTypeLoadListener, GetDoctorListener, UserLoginRememberListener {


  private RecyclerView recyclerView;


  private OnLoadCountDocType onLoadCountDocType;
  private DocTypeLoadListener docTypeLoadListener;

  AlertDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_doctor_type_list);

    initViews();
    initFireStore();
    loadDocTypeBasedOnCity(Common.city_name);


  }

  private void loadDocTypeBasedOnCity(String name) {

    dialog.show();

    FirebaseFirestore.getInstance().collection("AllDoctors")
        .document(name)
        .collection("DocType")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
          @Override
          public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if (task.isSuccessful())
              {
                List<DocType> docTypeList = new ArrayList<>();
                onLoadCountDocType.onLoadCountDocTypeSuccess(task.getResult().size());
                for (DocumentSnapshot docTypeSnapShot : task.getResult())
                {
                  DocType docType = docTypeSnapShot.toObject(DocType.class);
                  docType.setId(docTypeSnapShot.getId());
                  docTypeList.add(docType);
                }
                docTypeLoadListener.onDocTypeLoadSuccess(docTypeList);
              }
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        docTypeLoadListener.onDocTypeFailed(e.getMessage());
      }
    });



  }

  private void initViews() {
    recyclerView = findViewById(R.id.recycler_view_doctype);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    recyclerView.addItemDecoration(new SpaceItemDecoration(8));

    dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

    onLoadCountDocType = this;
    docTypeLoadListener = this;
  }

  private void initFireStore() {
  }

  @Override
  public void onLoadCountDocTypeSuccess(int count) {

  }

  @Override
  public void onDocTypeLoadSuccess(List<DocType> docTypeList) {
    RecyclerDocTypeAdapter adapter = new RecyclerDocTypeAdapter(this, docTypeList, this, this);
    recyclerView.setAdapter(adapter);
    dialog.dismiss();
  }

  @Override
  public void onDocTypeFailed(String message) {
    Common.makeToast(this, message);
    dialog.dismiss();
  }

  @Override
  public void onGetBarberSuccess(Doctor doctor) {
    Common.currentDoctor = doctor;
    Paper.book().write(Common.DOCTOR_KEY, new Gson().toJson(doctor));
  }

  @Override
  public void onUserLoginSuccess(String user) {
    // save user
    Paper.init(this);
    Paper.book().write(Common.LOGGED_KEY, user);
    Paper.book().write(Common.CITY_KEY, Common.city_name);
    Paper.book().write(Common.DOCTYPE_KEY, new Gson().toJson(Common.selectedDocType));
  }
}
