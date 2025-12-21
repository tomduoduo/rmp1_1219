package com.motionrivalry.rowmasterpro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.util.*;
import okhttp3.*;

/**
 * 网络请求管理器，统一处理HTTP请求
 */
public class NetworkManager {
    private final String uploadURL, updateURL;
    private HttpURLConnection conn;

    /**
     * 构造函数
     * 
     * @param uploadURL 文件上传URL
     * @param updateURL 数据更新URL
     */
    public NetworkManager(String uploadURL, String updateURL) {
        this.uploadURL = uploadURL;
        this.updateURL = updateURL;
    }

    /**
     * 初始化连接
     */
    public void initConnection() {
        new Thread(() -> {
            try {
                conn = (HttpURLConnection) new URL(updateURL).openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(3000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                Map<String, Object> data = new HashMap<>();
                data.put("FieldName", "CacheData");
                data.put("Type", "1");
                for (String k : new String[] { "userName", "sectionTime", "SPM", "boatSpeed", "actualDistance",
                        "latitude", "longitude", "sectionType", "playerType", "boatType", "targetDistance" }) {
                    data.put(k, 0);
                }

                String json = JSON.toJSONString(data);
                conn.setRequestProperty("Content-Length", String.valueOf(json.length()));
                conn.getOutputStream().write(json.getBytes());
                conn.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 发送数据更新
     * 
     * @param d 更新数据对象
     */
    public void sendUpdate(UpdateData d) {
        new Thread(() -> {
            try {
                conn = (HttpURLConnection) new URL(updateURL).openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(3000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

                String json = JSON.toJSONString(d.toMap());
                conn.setRequestProperty("Content-Length", String.valueOf(json.getBytes().length));
                conn.getOutputStream().write(json.getBytes());
                Map<String, Object> responseMap = JSON.parseObject(conn.getInputStream(), Map.class);
                System.out.println("response data:" + responseMap.get("data"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 上传文件
     * 
     * @param path 文件路径
     * @param name 文件名
     * @param cb   上传回调
     */
    public void uploadFile(String path, String name, UploadCallback cb) {
        new Thread(() -> {
            try {
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addPart(
                                Headers.of("Content-Disposition",
                                        "form-data; name=\"originalData\"; filename=\"" + name + "\""),
                                RequestBody.create(MediaType.parse("application/octet-stream"), new File(path)))
                        .build();

                new OkHttpClient().newCall(new Request.Builder().url(uploadURL).post(body).build())
                        .enqueue(new Callback() {
                            public void onFailure(Call c, IOException e) {
                                if (cb != null)
                                    cb.onFailure(e);
                            }

                            public void onResponse(Call c, Response r) {
                                if (cb != null)
                                    cb.onSuccess();
                            }
                        });
            } catch (Exception e) {
                if (cb != null)
                    cb.onFailure(e);
            }
        }).start();
    }

    /**
     * 上传回调接口
     */
    public interface UploadCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    /**
     * 更新数据类
     */
    public static class UpdateData {
        public String userName, sectionTime, displayTime, spm, boatSpeed, actualDistance, sectionType, playerType,
                boatType, targetDistance;
        public BigDecimal latitude, longitude;

        /**
         * 转换为Map对象
         * 
         * @return 数据Map
         */
        public Map<String, Object> toMap() {
            Map<String, Object> m = new HashMap<>();
            m.put("FieldName", "CacheData");
            m.put("Type", "1");
            m.put("userName", userName);
            m.put("sectionTime", sectionTime);
            m.put("displayTime", displayTime);
            m.put("SPM", spm);
            m.put("boatSpeed", boatSpeed);
            m.put("actualDistance", actualDistance);
            m.put("latitude", latitude);
            m.put("longitude", longitude);
            m.put("sectionType", sectionType);
            m.put("playerType", playerType);
            m.put("boatType", boatType);
            m.put("targetDistance", targetDistance);
            return m;
        }
    }
}