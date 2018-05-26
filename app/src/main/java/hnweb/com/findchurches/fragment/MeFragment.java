package hnweb.com.findchurches.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hnweb.com.findchurches.MultipartRequest.MultiPart_Key_Value_Model;
import hnweb.com.findchurches.MultipartRequest.MultipartFileUploaderAsync;
import hnweb.com.findchurches.MultipartRequest.OnEventListener;
import hnweb.com.findchurches.R;
import hnweb.com.findchurches.activity.HistoryActivity;
import hnweb.com.findchurches.activity.LoginActivity;
import hnweb.com.findchurches.activity.MainActivity;
import hnweb.com.findchurches.activity.PaymentTotalActivity;
import hnweb.com.findchurches.contants.AppConstant;
import hnweb.com.findchurches.utility.AlertUtility;
import hnweb.com.findchurches.utility.AppUtils;
import hnweb.com.findchurches.utility.LoadingDialog;
import hnweb.com.findchurches.utility.SharedPreference;
import hnweb.com.findchurches.utility.Utilities;
import hnweb.com.findchurches.utility.Utils;
import hnweb.com.findchurches.utility.Validations;

/**
 * Created by PC-21 on 07-May-18.
 */


public class MeFragment extends Fragment {

    EditText et_userName, et_userEmail, et_userNumber;
    LinearLayout ll_paymentHistory, ll_logout, ll_password;
    ImageView user_camerabutton;
    Button btn_edit;
    Dialog dialog;
    View rootView;
    LoadingDialog loadingDialog;
    String str_user_id, str_userName, str_email, str_mobile, str_photo;
    String isclick = "1";
    String user_Id;
    private int GALLERY = 1, CAMERA = 2;
    public static final int REQUEST_CAMERA = 5;
    public static File destination;
    protected static final int REQUEST_STORAGE_ACCESS_PERMISSION = 102;
    String camImage, imagePath12;
    CircleImageView iv_view_profile_detail;
    Drawable drawable;
    EditText et_password, et_confirmpassword;
    TextInputLayout input_confirmpassword, input_password;

    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_me, container, false);
        loadingDialog = new LoadingDialog(getActivity());


        et_userName = (EditText) rootView.findViewById(R.id.et_userName);
        et_userEmail = (EditText) rootView.findViewById(R.id.et_userEmail);
        et_userNumber = (EditText) rootView.findViewById(R.id.et_userNumber);
        ll_paymentHistory = (LinearLayout) rootView.findViewById(R.id.ll_paymentHistory);
        btn_edit = (Button) rootView.findViewById(R.id.btn_edit);
        iv_view_profile_detail = (CircleImageView) rootView.findViewById(R.id.iv_view_profile_detail);
        user_camerabutton = (ImageView) rootView.findViewById(R.id.user_camerabutton);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        ll_logout = (LinearLayout) rootView.findViewById(R.id.ll_logout);
        ll_password = (LinearLayout) rootView.findViewById(R.id.ll_password);

        drawable = ContextCompat.getDrawable(getActivity(), R.drawable.profile);

        SharedPreferences settings = getActivity().getSharedPreferences("AOP_PREFS", Context.MODE_PRIVATE);
        user_Id = settings.getString("user_id", null);


        getProfileDetails();

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isclick.equals("1")) {
                    et_userName.setEnabled(true);
                    // et_userEmail.setEnabled(true);
                    et_userNumber.setEnabled(true);
                    btn_edit.setText("Save");
                    isclick = "0";
                } else {

                    if (checkValidation()) {
                        if (Utilities.isNetworkAvailable(getActivity())) {
                            UpdateData(camImage);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please Connect to internet", Toast.LENGTH_LONG).show();
                    }
                }

            }

        });
        user_camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        ll_paymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreference.logout(getActivity());
            }
        });

        ll_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        return rootView;
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validations.hasText(et_userName, "Please Enter Address"))
            ret = false;
        if (!Validations.isValidEmail(et_userEmail, et_userEmail.getText().toString(), "Please enter valid Email id", true))
            ret = false;
        if (!Validations.hasText(et_userNumber, "Please Enter Contact Number"))
            ret = false;
        return ret;
    }

    public void getProfileDetails() {

        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_PROFILE_DETAILS,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                        Log.d("res_events", response);

                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (message_code == 1) {
                                JSONObject jsonDetails = j.getJSONObject("details");
                                str_user_id = jsonDetails.getString("reg_id");
                                str_userName = jsonDetails.getString("name");
                                str_mobile = jsonDetails.getString("mobno");
                                str_email = jsonDetails.getString("email");
                                str_photo = jsonDetails.getString("profile_photo");

                                try {
                                    Glide.with(getActivity())
                                            .load(str_photo)
                                            .error(drawable)
                                            .centerCrop()
                                            .crossFade()
                                            .listener(new RequestListener<String, GlideDrawable>() {
                                                @Override
                                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .into(iv_view_profile_detail);
                                } catch (Exception e) {
                                    Log.e("Exception", e.getMessage());
                                }

                                et_userName.setText(str_userName);
                                et_userNumber.setText(str_mobile);
                                et_userEmail.setText(str_email);

                            } else {
                                message = j.getString("message");

                                Toast.makeText(getActivity(), "" + message, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }

                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());

                    }
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put(AppConstant.USER_ID, user_Id);

                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }

    public void UpdateData(String camImage) {
        loadingDialog.show();

        MultiPart_Key_Value_Model OneObject = new MultiPart_Key_Value_Model();
        Map<String, String> fileParams = new HashMap<>();
        if (camImage == null) {
            //  fileParams.put("profile_pic", "");

        } else {
            fileParams.put("profile_photo", camImage);

        }

        System.out.println("priya Op" + camImage);

        Map<String, String> stringparam = new HashMap<>();

        stringparam.put(AppConstant.USER_ID, user_Id);
        stringparam.put(AppConstant.KEY_EMAIL, et_userEmail.getText().toString());
        stringparam.put(AppConstant.KEY_PHONE, et_userNumber.getText().toString());
        stringparam.put(AppConstant.KEY_NAME, et_userName.getText().toString());

        OneObject.setUrl(AppConstant.API_PROFILE_EDIT);
        OneObject.setFileparams(fileParams);
        System.out.println("file" + fileParams);
        System.out.println("UTL" + OneObject.toString());
        OneObject.setStringparams(stringparam);
        System.out.println("string" + stringparam);

        MultipartFileUploaderAsync someTask = new MultipartFileUploaderAsync(getActivity(), OneObject, new OnEventListener<String>() {
            @Override
            public void onSuccess(String object) {
                loadingDialog.dismiss();
                System.out.println("Result" + object);
                //    Toast.makeText(getActivity(), "ress" + object, Toast.LENGTH_LONG).show();

                try {
                    JSONObject jsonObject1response = new JSONObject(object);
                    int flag = jsonObject1response.getInt("message_code");

                    if (flag == 1) {


                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                et_userEmail.setEnabled(false);
                                et_userName.setEnabled(false);
                                et_userNumber.setEnabled(false);
                                btn_edit.setText("Edit");
                                isclick = "1";

                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        String message = jsonObject1response.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(message);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSONException" + e);
                }
            }


            @Override
            public void onFailure(Exception e) {
                System.out.println("onFailure" + e);

            }
        });
        someTask.execute();
        return;
    }

    private void showPictureDialog() {
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                isPermissionGrantedImageGallery();
                                break;
                            case 1:
                                isPermissionGrantedImage();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void isPermissionGrantedImageGallery() {

        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            choosePhotoFromGallary();
        }

    }

    public void isPermissionGrantedImage() {
        System.out.println("Click Image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_ACCESS_PERMISSION);
        } else {
            camerImage();
        }

    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            new android.app.AlertDialog.Builder(getActivity())
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }

    public void camerImage() {
        System.out.println("Click Image11");
        String name = AppConstant.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".png");


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".my.package.name.provider", destination));
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    // Log.i("Path", imagePath12);
                    FileOutputStream fo;
                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                    imagePath12 = destination.getAbsolutePath();
                   /* editor = sharedPreferences.edit();
                    editor.putString(Constants.PROFILE_IMAGE, imagePath12);
                    editor.commit();*/
                    camImage = imagePath12;

                    try {
                        Glide.with(getActivity())
                                .load(camImage)
                                .error(drawable)
                                .centerCrop()
                                .crossFade()
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(iv_view_profile_detail);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_CAMERA) {

            System.out.println("REQUEST_CAMERA");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            String imagePath = destination.getAbsolutePath();
            Log.i("Path", imagePath);
            camImage = imagePath;
            Toast.makeText(getActivity(), camImage, Toast.LENGTH_SHORT).show();
            /* editor = sharedPreferences.edit();
            editor.putString(Constants.PROFILE_IMAGE, camImage);
            editor.commit();
*/
            try {
                Glide.with(getActivity())
                        .load(camImage)
                        .error(drawable)
                        .centerCrop()
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(iv_view_profile_detail);
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }
        }
    }

    public void dialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_changepasword);
        et_password = (EditText) dialog.findViewById(R.id.et_password);
        et_confirmpassword = (EditText) dialog.findViewById(R.id.et_confirmpassword);
        input_password = (TextInputLayout) dialog.findViewById(R.id.input_password);
        input_confirmpassword = (TextInputLayout) dialog.findViewById(R.id.input_confirmpassword);
        Button btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        dialog.show();
        dialog.setCancelable(true);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation1()) {
                    if (Utils.isNetworkAvailable(getActivity())) {
                        String password = et_password.getText().toString();
                        String confirm_password = et_confirmpassword.getText().toString();
                        if (password.equals(confirm_password)) {
                            submit(password, confirm_password);
                        } else {
                            Toast.makeText(getActivity(), "Password Does not match with Confirm Password", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Utils.myToast1(getActivity());
                    }
                }
            }
        });
    }

    private boolean checkValidation1() {
        boolean ret = true;

        if (!Validations.hasText_input_layout(et_password, "Please Enter the Password", input_password))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_password, "Password atleast 7 characters", input_password))
            ret = false;
        if (!Validations.hasText_input_layout(et_confirmpassword, "Please Enter the Password", input_confirmpassword))
            ret = false;
        if (!Validations.check_text_length_7_text_layout(et_confirmpassword, "Password atleast 7 characters", input_confirmpassword))
            ret = false;

        return ret;
    }

    private void submit(final String password, final String confirmpassword) {
        loadingDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConstant.API_CHANGE_PASSWORD,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("res_register" + response);
                        try {
                            JSONObject j = new JSONObject(response);
                            int message_code = j.getInt("message_code");
                            String message = j.getString("message");

                            if (loadingDialog.isShowing()) {
                                loadingDialog.dismiss();
                            }
                            if (message_code == 1) {
                                     AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog1, int id) {

                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                message = j.getString("message");
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(message)
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } catch (JSONException e) {
                            System.out.println("jsonexeption" + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String reason = AppUtils.getVolleyError(getActivity(), error);
                        AlertUtility.showAlert(getActivity(), reason);
                        System.out.println("jsonexeption" + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {

                    params.put(AppConstant.KEY_PASSWORD, password);
                    params.put(AppConstant.USER_ID, user_Id);
                } catch (Exception e) {
                    System.out.println("error" + e.toString());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }


}
