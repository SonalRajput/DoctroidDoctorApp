package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface;

import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Model.DocType;
import java.util.List;

public interface DocTypeLoadListener {
    void onDocTypeLoadSuccess (List<DocType> docTypeList);
    void onDocTypeFailed (String message);
}
