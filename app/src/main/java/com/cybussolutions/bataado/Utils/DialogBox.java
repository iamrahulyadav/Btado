package com.cybussolutions.bataado.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cybussolutions.bataado.Activities.HomeScreen;
import com.cybussolutions.bataado.Activities.Login;
import com.cybussolutions.bataado.Activities.SignUp;
import com.cybussolutions.bataado.R;

import static android.content.Context.MODE_PRIVATE;

public class DialogBox {
    final Dialog dialog;
    SharedPreferences pref ;
    public static Bundle c;
    public DialogBox(final Activity context, final String message, final String type, String title) {
        dialog = new Dialog(context);
        // TODO Auto-generated constructor stub
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // dialog.setTitle(title);
        dialog.setContentView(R.layout.activity_dialog_box);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /*if (type.equals("invalidpin")) {
           TextView b = (TextView) dialog.findViewById(R.id.additionalButton);
            b.setVisibility(View.VISIBLE);
            b.setText("Forgot Pincode ?");
            b.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent("android.intent.action.Forgot_PinCode");
                    context.startActivity(intent);
                }
            });
        }*/
        TextView t = (TextView) dialog.findViewById(R.id.lotsoftext);
        if (message.contains("$.mobile.changePage(")) {
            t.setText("Your session has either been expired or exists. Please try later.");
        } else {
            t.setText(message);
        }
        t = (TextView) dialog.findViewById(R.id.titleofDialog);
        t.setText(title);
        Button b = (Button) dialog.findViewById(R.id.okbtn);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.contains("$.mobile.changePage(")) {
                    Intent intent = new Intent("android.intent.action.LoginActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if(type.equals("")){
                    Intent intent= new Intent(context, HomeScreen.class);
                    context.finish();
                    context.startActivity(intent);
                }

                if(type.equals("ok"))
                {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }if(type.equals("nothing"))
                {

                }if(type.equals("signup"))
                {
                    Intent intent=new Intent(context, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }if(type.equals("Error"))
                {

                }
                if(type.equals("trans_not_complete"))
                {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }   if(type.equals("trans_not_complete"))
                {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type.equals("1")) {
                    Intent intent = new Intent("android.intent.action.pinCode");
                    context.startActivity(intent);
                }
                if (type.equals("0")) {
                    Intent intent = new Intent("android.intent.action.SearchTransaction");
                    context.startActivity(intent);
                }
                if (type.equals("3")) {
                    Intent intent = new Intent("android.intent.action.verifypinCode");
                    context.startActivity(intent);
                }

                if (type.equals("4")) {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type.equals("6")) {
                    new Intent("android.intent.action.Profile_View");
                }
                if (type.equals("7")) {
                     Intent intent=new Intent("android.intent.action.LoginActivity");
                    context.startActivity(intent);
                }
                if (type.equals("8") || type.equals("invalidpin")) {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
               /* if (type.equals("99")) {
                    Intent edit_Intent = new Intent(context, EditProfile.class);
                    context.startActivity(edit_Intent);
                }*/
                if (type.equals("007")) {
                    Intent forgotintent = new Intent("android.intent.action.Forgot_Password");
                    context.startActivity(forgotintent);
                }
                if (type.equals("sendMoney")) {
                    Intent intent = new Intent("android.intent.action.SendMoney1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type.equals("sendMoney1")) {
                    Intent intent = new Intent("android.intent.action.SendMoney1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type.equals("registersucess")) {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type.equals("editaccount")) {
                    Intent intent = new Intent("android.intent.action.SendMoney4");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtras(c);
                    context.startActivity(intent);
                }
                if (type.equals("passwordchange") || type.equals("successdocs")) {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
              /*  if (type.equals("747")) {
                    Intent intent = new Intent("android.intent.action.SendMoney3");
                    intent.putExtras(SendMoney3.b);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }*/
                if (type.equals("1011")) {
                    Intent intent = new Intent("android.intent.action.PinCode_Registration");
                    context.startActivity(intent);
                }
                if (type.equals("adddocsucess")) {
                    Intent intent = new Intent("android.intent.action.PinCode_Registration");
                    context.startActivity(intent);
                }
                if (type.equals("3033")) {
                    Intent intent = new Intent("android.intent.action.VerifyPinCode_Registration");
                    context.startActivity(intent);
                }
               /* if (type.equals("registersucessincomplete")) {

                    Intent intent= new Intent(context,MainActivity.class);

                    pref  = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("registered","not_completed");
                    editor.commit();

                    context.startActivity(intent);

                }*/
                if (type.equals("select")) {
                    Intent intent = new Intent("android.intent.action.PaymentMethod");
                    context.startActivity(intent);
                }
                if (type.equals("imageyaay")) {
                    Intent intent = new Intent("android.intent.action.Profile_View");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if (type.equals("exception")) {
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                if(type.equals("hold")){
                    Intent intent = new Intent("android.intent.action.Main_Activity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
               /* if(type.equals("noDoc")){
                    Intent main = new Intent(context, UploadDocuments.class);
                    context.startActivity(main);
                }*/
                dialog.dismiss();
            }
        });
        // Create the AlertDialog object and return it
        dialog.show();
    }
    public void onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
    }
}

