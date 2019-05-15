package apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Common;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.Interface.DialogClickListener;
import apps.abhibhardwaj.com.doctriod.doctroiddoctorapp.R;

public class CustomLoginDialog {

  private TextView tvTitle;
  private TextInputEditText edtUserName, edtPassword;
  private Button btnLogin, btnCancel;


  public static CustomLoginDialog customLoginDialog;
  public DialogClickListener dialogClickListener;

  public static CustomLoginDialog getInstance() {

    if (customLoginDialog == null) {
      customLoginDialog = new CustomLoginDialog();
    }
    return customLoginDialog;

  }


  public void showLoginDialog(String title,
      String positiveText,
      String negativeText,
      Context context,
      final DialogClickListener dialogClickListener)

  {
    this.dialogClickListener = dialogClickListener;

    final Dialog dialog = new Dialog(context);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

    dialog.setContentView(R.layout.layout_dialog_login);

    tvTitle = dialog.findViewById(R.id.login_txt_title);
    edtUserName = dialog.findViewById(R.id.login_edt_username);
    edtPassword = dialog.findViewById(R.id.login_edt_password);
    btnLogin = dialog.findViewById(R.id.btn_login);
    btnCancel = dialog.findViewById(R.id.btn_cancel);


    if (!title.isEmpty())
    {
      tvTitle.setText(title);
      tvTitle.setVisibility(View.VISIBLE);
    }

    btnLogin.setText(positiveText);
    btnCancel.setText(negativeText);

    dialog.setCancelable(false);
    dialog.show();

    Window window = dialog.getWindow();
    window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

    btnLogin.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dialogClickListener.onClickPositiveButton(dialog, edtUserName.getText().toString(),
            edtPassword.getText().toString());
      }
    });


    btnCancel.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dialogClickListener.onClickNegativeButton(dialog);
      }
    });

  }


}
