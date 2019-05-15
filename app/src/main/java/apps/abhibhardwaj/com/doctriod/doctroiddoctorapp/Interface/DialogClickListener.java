package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface;

import android.content.DialogInterface;

public interface DialogClickListener {
  void onClickPositiveButton(DialogInterface dialogInterface, String username, String password);
  void onClickNegativeButton(DialogInterface dialogInterface);
}
