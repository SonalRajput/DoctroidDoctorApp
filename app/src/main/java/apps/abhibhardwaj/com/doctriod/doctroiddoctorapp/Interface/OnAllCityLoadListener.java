package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface;

import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.City;
import java.util.List;

public interface OnAllCityLoadListener {
  void onAllCityLoadSuccess(List<City> cityList);
  void onAllStateLoadFailed(String message);
}
