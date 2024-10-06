package com.rorschach.zcyislim.Util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rorschach.zcyislim.Entity.Exercise;
import com.rorschach.zcyislim.Entity.PointLongiLati;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class UtilMethod {

    public static SharedPreferences preferences;
    public final static String TAG = "ZaoYi";
    private static final int MIN_DELAY_TIME = 500;  // 两次点击间隔
    private static long lastClickTime = 0;

    //设置状态栏字体颜色
    public static void setStatusBarTrans(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static void changeStatusBarFrontColor(boolean isBlack, Activity activity, int colorId) {

        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, colorId));

        changeFrontColor(isBlack, activity);
    }

    public static void changeFrontColor(boolean isBlack, Activity activity) {
        if (isBlack) {
            //设置状态栏黑色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public static boolean checkValidEmail(String email) {
        if (email.isEmpty()) {
            return false;
        }
        return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
    }

    public static boolean isEnglish(String p) {
        byte[] bytes = p.getBytes();
        int i = bytes.length;//i为字节长度
        int j = p.length();//j为字符长度
        return i == j;
    }

    //隐藏软键盘
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static String getPath(Context context, Uri srcUri) {
        String path = context.getCacheDir() + "/" + System.currentTimeMillis() + ".png";
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);//context的方法获取URI文件输入流
            if (inputStream == null) return "null";
            OutputStream outputStream = Files.newOutputStream(Paths.get(path));
            copyStream(inputStream, outputStream);//调用下面的方法存储
            inputStream.close();
            outputStream.close();
            return path;//成功返回路径
        } catch (Exception e) {
            e.printStackTrace();
            return "null";//失败返回路径null
        }
    }

    public static void saveFile(Context context, File file, String type, String dir, String fileName) {
        File directory = new File(context.getExternalFilesDir(type), dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File destFile = new File(directory, fileName);
        try {
            InputStream in = new FileInputStream(file);
            OutputStream out = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Exercise> combineDistances(ArrayList<Exercise> objects) {
        Map<String, Double> distanceMap = new HashMap<>();
        // 遍历数组，将相同日期的距离长度相加
        for (Exercise obj : objects) {
            String date = obj.getStartTime().substring(0, 10); // 仅保留年月日部分
            double distance = obj.getExerciseDistance();
            if (distanceMap.containsKey(date)) {
                double totalDistance = distanceMap.get(date) + distance;
                distanceMap.put(date, totalDistance);
            } else {
                distanceMap.put(date, distance);
            }
        }

        // 根据HashMap中的键值对重新创建ArrayList对象数组
        List<Exercise> combinedList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : distanceMap.entrySet()) {
            String date = entry.getKey();
            double distance = entry.getValue();
            combinedList.add(new Exercise(distance, date));
        }
        Collections.sort(combinedList);
        return combinedList;
    }

    private static void copyStream(InputStream input, OutputStream output) {//文件存储
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int n;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getPreferences(Application application) {
        if (preferences == null) {
            preferences = application.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static void showToast(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public static void DeleteFile(String FileName) {
        File file = new File(FileName);
        if (!file.exists()) {
            System.out.println("文件" + FileName + "不存在，删除失败！");

        } else {
            if (file.isFile()) {
                System.out.println(file.delete() + "!!!");
            }
        }
    }

    public static <T extends Serializable> void setObject(Context context, String key, T t) {
        if (t != null) {
            SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            String s = serializeObjectToString(t);
            //创建字节输出流
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, s);
            editor.apply();
        } else {
            SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            sp.edit().putString(key, "").apply();
        }

    }

    /**
     * 获取SharedPreference保存的对象
     * 使用Base64解密String，返回Object对象
     *
     * @param context 上下文
     * @param key     储存对象的key
     * @param <T>     泛型
     * @return 返回保存的对象
     */
    public static <T extends Serializable> T getObject(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectValue = sp.getString(key, null);
            return deserializeStringToObject(objectValue);
        }
        return null;
    }

    // 将ArrayList对象序列化成Base64编码的字符串
    public static <T extends Serializable> String serializeObjectToString(T t) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(t);
            out.flush();
            byte[] bytes = bos.toByteArray();
            out.close();
            bos.close();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将Base64编码的字符串反序列化为ArrayList对象
    public static <T extends Serializable> T deserializeStringToObject(String object) {
        try {
            byte[] bytes = Base64.decode(object, Base64.DEFAULT);
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            T result = (T) in.readObject();
            in.close();
            bis.close();
            return result;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveArrExerciseData(Context context, String key, ArrayList<Exercise> exercises) {
        if (exercises != null && exercises.size() > 0) {
            SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            Gson gson = new Gson();
            String json = gson.toJson(exercises);
            // 存储 JSON 字符串到 SharedPreferences
            edit.putString(key, json);
            edit.apply();
            edit.apply();
        } else {
            SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            sp.edit().putString(key, "").apply();
        }
    }

    public static ArrayList<Exercise> loadArrExerciseData(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String json = sp.getString(key, null);
        if (json != null) {
            // 将 JSON 字符串转换为对象数组
            Gson gson = new Gson();
            Type type = new TypeToken<List<Exercise>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static String transPointsToString(ArrayList<PointLongiLati> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public static ArrayList<PointLongiLati> transStringToPoints(String list) {
        if (list != null) {
            // 将 JSON 字符串转换为对象数组
            Gson gson = new Gson();
            Type type = new TypeToken<List<PointLongiLati>>() {
            }.getType();
            return gson.fromJson(list, type);
        }
        return new ArrayList<>();
    }

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            // 检查 GPS 定位是否开启
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 检查网络定位是否开启
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            // 如果 GPS 或网络定位任一开启，返回 true
            return isGpsEnabled || isNetworkEnabled;
        }
        return false;
    }

    public static double calculateSpeed(double distanceInMeters, long startTimeStamp, long endTimeStamp) {
        // 将距离从米（m）转换为公里（km）
        double distanceInKm = distanceInMeters / 1000.0;

        // 计算时间差（单位为毫秒）
        long timeDifferenceInMillis = endTimeStamp - startTimeStamp;

        // 将时间差转换为小时
        double timeInHours = timeDifferenceInMillis / (1000.0 * 60.0 * 60.0); // 将毫秒转换为小时

        // 计算公里每小时的速度

        return distanceInKm / timeInHours;
    }

    public static double calculateRunningCalories(double weight, long startTimeStamp, long endTimeStamp, double speedInKph) {
        if (startTimeStamp == endTimeStamp) {
            return 0;
        }
        // 计算时间差（单位为小时）
        double timeInHours = (endTimeStamp - startTimeStamp) / (1000.0 * 60.0 * 60.0);

        // 计算指数K
        double kValue = 30 / (speedInKph * 60.0 / 0.4); // 将速度从公里每小时转换为分钟每400米

        // 计算跑步热量
        return weight * timeInHours * kValue;
    }

    public static ArrayList<LatLng> LongiLatiToLatLng(ArrayList<PointLongiLati> pointLongiLatis) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        if (pointLongiLatis != null) {
            for (int i = 0; i < pointLongiLatis.size(); i++) {
                latLngs.add(new LatLng(pointLongiLatis.get(i).getLatitude(), pointLongiLatis.get(i).getLongitude()));
            }
        }
        return latLngs;
    }

    public static String sha1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

