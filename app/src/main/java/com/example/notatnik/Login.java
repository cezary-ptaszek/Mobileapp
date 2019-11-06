//package com.example.notatnik;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//
//public class Login extends AsyncTask<String, Void, String> {
//
//    @Override
//    protected String doInBackground(String... strings) {
//        String Email = strings[0];
//        String Password = strings[1];
//
//        OkHttpClient okHttpClient = new OkHttpClient();
//        RequestBody formBody = new FormBody.Builder()
//                .add("user_id", Email)
//                .add("user_password", Password)
//                .build();
//
//        Request request = new Request.Builder()
//                .url(url_Login)
//                .post(formBody)
//                .build();
//
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            if (response.isSuccessful()) {
//                String result = response.body().string();
//                if (result.equalsIgnoreCase("login")) {
//                    Intent i = new Intent(LoginActivity.this,
//                            DashboardActivity.class);
//                    startActivity(i);
//                    finish();
//                } else {
//                    showToast("Email or Password mismatched!");
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}