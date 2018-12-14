package com.jsti.pile.collector.utils;

import com.jsti.pile.collector.App;
import com.jsti.pile.collector.common.CommonParams;

public class UrlUtils {




    public static String getBaseUrl() {
//        http://218.2.99.139:21121/roadpatrol_server/intf/collector/login.intf?username=1111&password=121&bdv=1.0
        if (null != App.SP_Data.getString("ipAddress")) {
//            return "http://"+App.SP_Data.getString("ipAddress")+"/roadpatrol_server";//zhang
            return "http://"+App.SP_Data.getString("ipAddress");
        } else {
            return CommonParams.SERVER_ADDRESS;
        }
    }
}
