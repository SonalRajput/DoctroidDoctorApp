package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common;

import android.content.Context;
import android.widget.Toast;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.AppointmentInformation;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.DocType;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.Doctor;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Common {


  public static final int TIME_SLOT_TOTAL = 16;
  public static final String DISABLE_TAG = "DISABLE";
  public static final String LOGGED_KEY = "LOGGED";
  public static final String CITY_KEY = "CITY";
  public static final String DOCTYPE_KEY = "DOCTYPE";
  public static final String DOCTOR_KEY = "DOCTOR";

  public static String city_name = "";
  public static DocType selectedDocType;
  public static Doctor currentDoctor;
  public static int currentTimeSlot = -1;
  public static Calendar bookingDate = Calendar.getInstance();
  public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
  public static AppointmentInformation currentAppointmentInformation;


  public static void makeToast(Context context, String text){
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
  }

  public static String convertTimeSlotToString(int slot) {
    switch (slot) {
      case 0:
        return "09:00 - 09:30";
      case 1:
        return "09:30 - 10:00";
      case 2:
        return "10:00 - 10:30";
      case 3:
        return "10:30 - 11:00";
      case 4:
        return "11:00 - 11:30";
      case 5:
        return "11:30 - 12:00";
      case 6:
        return "12:00 - 12:30";
      case 7:
        return "12:30 - 01:00";
      case 8:
        return "01:00 - 01:30";
      case 9:
        return "01:30 - 02:00";
      case 10:
        return "02:00 - 02:30";
      case 11:
        return "02:30 - 03:00";
      case 12:
        return "03:00 - 03:30";
      case 13:
        return "03:30 - 04:00";
      case 14:
        return "04:00 - 04:30";
      case 15:
        return "04:30 - 05:00";
      default:
        return "Closed";
    }
  }

}
