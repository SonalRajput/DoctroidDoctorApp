package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface;

import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.AppointmentInformation;
import java.util.List;

public interface TimeSlotLoadListener {

  void onTimeSlotLoadSuccess(List<AppointmentInformation> timeSlotList);
  void onTimeSlotLoadFailed(String message);
  void onTimeSlotLoadEmpty();

}
