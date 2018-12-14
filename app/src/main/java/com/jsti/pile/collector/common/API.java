//package com.jsti.pile.collector.common;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import java.nio.charset.Charset;
//
//
//import static android.content.Context.MODE_PRIVATE;
//
///**
// * 服务器接口
// */
//public class API {
//
//    private Context context;
//    private static SharedPreferences pref;
//
//    public API(Context context) {
//        this.context = context;
//        pref = context.getSharedPreferences("data", MODE_PRIVATE);
//    }
//
//    public static final String CHARSET_STR = "UTF-8";
//    public static final Charset CHARSET = Charset.forName(CHARSET_STR);
//    public static final int PER_REQUEST_ITEMS_COUNT = 20;
//    //
//    public static final String platform_name = "Android";
//
//    public static String getBaseUrl() {
//        //
//        //
//        if (null != pref.getString("ipAddress", "")) {
//            return pref.getString("ipAddress", "");
//        } else {
//            return CommonParams.SERVER_ADDRESS;
//        }
//    }
//
//    /**
//     * 拦截返回，添加公用数据
//     *
//     * @param e
//     * @return
//     */
//    private static RequestEntity perReturn(RequestEntity e) {
//        return e;
//    }
//
//    private static RequestEntity newRequestEntity(RequestEntity.Method method) {
//        RequestEntityImpl ok = new RequestEntityImpl();
//        ok.setMethod(method);
//        return ok;
//    }
//
//    /**
//     * 获取验证码功能
//     */
//    public static RequestEntity login(String uid, String pwd) {
//        RequestEntity req = newRequestEntity(Method.POST_JSON);
//        req.setAction(getBaseUrl(), "/intf/collector/login.intf");
//        req.addQuery("username", uid);
//        req.addQuery("password", pwd);
//        return perReturn(req);
//    }
//
//    /**
//     * 同步基础数据
//     */
//    public static RequestEntity syncBaseData(String localDataVersion, String uid) {
//        RequestEntity req = newRequestEntity(Method.GET);
//        req.setAction(getBaseUrl(), "/intf/collector/syncBaseData.intf");
//        req.addQuery("ver", localDataVersion);
//        req.addQuery("uid", uid);
//        return perReturn(req);
//    }
//
//    /**
//     * 提交桩号收集结果
//     */
//    public static RequestEntity postCollectResult(String uid, String roadId, long finishTime, String fileName,
//                                                  String filePath) {
//        RequestEntity req = newRequestEntity(Method.POST);
//        req.setAction(getBaseUrl(), "/intf/collector/pile/postCollectResult.intf");
////		req.addQuery("uid", uid);
////		req.addQuery("roadId", roadId);
////		req.addQuery("finishTime", finishTime);
//        req.addFile("file", filePath, fileName, RequestEntity.MIME_TXT);
//        return perReturn(req);
//    }
//    //
//    //
//    // 4.HTTP接口
//    // 4.1用户登录
//    // 接口名：/intf/collector/login.intf
//    // 方法：POST
//    // 参数名称 参数含义 参数类型 是否必填
//    // 用户id string 必选
//    // 密码 string 必选
//    //
//    // 返回数据:
//    // {
//    // "result": //通用result json结构,
//    // "user": User //User json结构
//    // }
//    //
//    // 4.2同步基础数据
//    // 接口名：/intf/collector/syncBaseData.intf
//    // 方法：GET
//    // 参数名称 参数含义 参数类型 是否必填
//    // ver 本地的基础数据版本 String 必选
//    // uid 用户ID String 必选
//    //
//    // 返回数据:
//    // {
//    // "result":Result, //通用Result JSON结构
//    // "expressways":[
//    // Expressway, // Expressway JSON结构
//    // ]
//    // " ver":"120000xxxx" //string/服务器上最新版本号
//    // }
//    // 备注:version可以不支持
//    // 4.3提交收集结果
//    // 接口名：/intf/collector/pile /postCollectResult.intf
//    // 方法：POST
//    // 参数名称 参数含义 参数类型 是否必填
//    // roadId 高速公ID string 必填
//    // finishTime 收集完成时间 long 必填
//    // uid 负责收集的人 string 必填
//    // data 巡查结果json字符串,见如下说明 string 必填
//    //
//    // 注：Data为Pile
//    // JSON结构数据转移后的字符串，中个post当作普通post处理，拿出data（string），然后用json解析内部数据列表。
//    //
//    // 返回数据:
//    // {
//    // "result"://通用result json结构,
//    // }
//    // 4.4查询路段信息（）
//    // 接口：/intf/collector/roadSection/all
//}