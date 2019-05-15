package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp;

import android.Manifest;
import android.Manifest.permission;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common.Common;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.Prescription;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

public class AddPrescriptionActivity extends AppCompatActivity implements OnClickListener {

  private Button btnSendPrescription;
  private ImageButton btnPicImage;
  private EditText edtDescription;

  private static final int GALLERY_REQUEST = 1;
  private static final int CAMERA_REQUEST = 2;

  private Uri capImageURI = null;
  private String userID;

  private StorageReference storageReference;

  ProgressDialog dialog;




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_prescription);


    initViews();
    initListeners();
    initFireBase();



  }

  private void initFireBase() {
    userID = Common.currentAppointmentInformation.getUserID();
    storageReference = FirebaseStorage.getInstance().getReference().child("Database").child("Users").child(userID);

  }

  private void initViews() {
    dialog = new ProgressDialog(this);
    btnPicImage = findViewById(R.id.ibtn_select_image);
    edtDescription = findViewById(R.id.edt_description);
    btnSendPrescription = findViewById(R.id.btn_send_prescription);
  }

  private void initListeners() {
    btnPicImage.setOnClickListener(this);
    btnSendPrescription.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {

    switch (v.getId())
    {
      case R.id.ibtn_select_image:
      {
        showAddPhotoDialog();
        break;
      }

      case R.id.btn_send_prescription:
      {
        savePrescription();
        break;
      }


    }



  }

  private void savePrescription() {


    final String description = edtDescription.getText().toString().trim();


    if (description.isEmpty())
    {
      edtDescription.setError("Enter description");
      return;
    }


    if (capImageURI == null)
    {
      Common.makeToast(this, "Please select an image to continue");
      return;
    }


    final String currentTime = String.valueOf(System.currentTimeMillis());

    dialog.setMessage("Sending Prescription... ");
    dialog.show();
    final StorageReference filePath = storageReference.child("Prescriptions").child(currentTime);
    filePath.putFile(capImageURI).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
      @Override
      public void onSuccess(TaskSnapshot taskSnapshot) {

        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {

            Prescription prescription = new Prescription();
            prescription.setDescription(description);
            prescription.setPrescriptionID(currentTime);
            prescription.setDoctorAddres(Common.currentDoctor.getAddress());
            prescription.setDoctorID(Common.currentAppointmentInformation.getDoctorID());
            prescription.setDoctorName(Common.currentAppointmentInformation.getDoctorName());
            prescription.setDoctorSpecialization(Common.currentDoctor.getSpecialization());
            prescription.setImageName(currentTime);
            prescription.setImageURL(String.valueOf(uri));


            FirebaseFirestore.getInstance().collection("users").document(userID).collection("prescriptions")
                .document(currentTime)
                .set(prescription)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    Common.makeToast(AddPrescriptionActivity.this, "Sent Successfully");
                    startActivity(new Intent(AddPrescriptionActivity.this, AppointmentDetailsActivity.class));
                    finish();
                  }
                })
                .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                    // failure listener for file path fireStore database
                    dialog.dismiss();
                    Common.makeToast(AddPrescriptionActivity.this, "FireStore Database Error " + e.getMessage());

                  }
                });



          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            // failure listener for file path download url
            dialog.dismiss();
            Common.makeToast(AddPrescriptionActivity.this, "FireStore URL Error " + e.getMessage());
          }
        });

      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception e) {
        // failure listener for storage
        dialog.dismiss();
        Common.makeToast(AddPrescriptionActivity.this, "FireStore Storage Error " + e.getMessage());
      }
    });













  }

  private void showAddPhotoDialog() {
    final CharSequence[] items = { "Take a new photo", "Choose from gallery", "Cancel"};

    AlertDialog.Builder builder = new Builder(this);
    builder.setTitle("Add Photo");
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int i) {
        
        if (items[i].equals("Take a new photo"))
        {
          requestCameraPermission();
        }
        else if (items[i].equals("Choose from gallery"))
        {
          requestGalleryPermission();
        }
        else if (items[i].equals("Cancel"))
        {
          dialog.dismiss();
        }
        
        
      }
    });
    builder.show();
    



  }

  private void requestCameraPermission() {
    int result = ContextCompat.checkSelfPermission(AddPrescriptionActivity.this, permission.READ_EXTERNAL_STORAGE);

    if (result == PackageManager.PERMISSION_GRANTED)
    {
      startCamera();
    }
    else
    {
      ActivityCompat.requestPermissions(AddPrescriptionActivity.this, new String[] {permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST);
    }


  }

  private void requestGalleryPermission() {
    int result = ContextCompat.checkSelfPermission(AddPrescriptionActivity.this, permission.CAMERA);

    if (result == PackageManager.PERMISSION_GRANTED)
    {
      openGallery();
    }
    else
    {
      ActivityCompat.requestPermissions(AddPrescriptionActivity.this, new String[] {permission.CAMERA, permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode)
    {
      case CAMERA_REQUEST: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
        {
          startCamera();
        }
        else
        {
          Common.makeToast(AddPrescriptionActivity.this, "Permission denied to open Camera");
        }
        break;
      }

      case GALLERY_REQUEST:
      {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
          openGallery();
        }
        else
        {
          Common.makeToast(AddPrescriptionActivity.this, "Permission denied to open Gallery");
        }

        break;
      }

    }

  }

  private void startCamera() {
    StrictMode.VmPolicy.Builder builder = new VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());

    Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (takePicIntent.resolveActivity(getPackageManager()) != null)
    {
      String fileName = "temp.jpg";
      ContentValues values = new ContentValues();
      values.put(Media.TITLE, fileName);
      capImageURI = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
      takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, capImageURI);
      startActivityForResult(takePicIntent, CAMERA_REQUEST);
    }



  }

  private void openGallery() {
    Intent pickPhoto = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(pickPhoto, GALLERY_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode)
    {
      case GALLERY_REQUEST:
      {
        if (resultCode == RESULT_OK && data.getData() != null)
        {
          capImageURI = data.getData();
          loadImageIntoView(data.getData());
        }
        else
        {
          Common.makeToast(AddPrescriptionActivity.this, "No Image Selected! Try Again");
        }
        break;
      }

      case CAMERA_REQUEST:
      {
        if (resultCode == RESULT_OK)
        {
          String[] projection = {Media.DATA};
          Cursor cursor = managedQuery(capImageURI, projection, null, null, null);
          int column_index_data = cursor.getColumnIndexOrThrow(Media.DATA);
          cursor.moveToFirst();
          String picturePath = cursor.getString(column_index_data);
          capImageURI = Uri.parse("file://" + picturePath);
          loadImageIntoView(capImageURI);
        }
        else
        {
          Common.makeToast(AddPrescriptionActivity.this, "No Image Captured! Try Again");
        }
        break;
      }
    }

  }

  private void loadImageIntoView(Uri data) {
    btnPicImage.setImageURI(data);
  }

}
