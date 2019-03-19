package cwh.com.ballcollision;

import com.het.basic.constact.ComParamContact;
import com.het.basic.data.api.token.TokenManager;
import com.het.basic.utils.MD5;
import com.het.http.interceptor.BaseDynamicInterceptor;
import com.het.http.utils.HttpLog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;


public class SignInterceptor extends BaseDynamicInterceptor<SignInterceptor> {
    public SignInterceptor() {
    }

    @Override
    public TreeMap<String, String> dynamic(TreeMap<String, String> dynamicMap) {
        //dynamicMap:是原有的全局参数+局部参数
        if (!isTimeStamp()) {//是否添加时间戳，因为你的字段key可能不是timestamp,这种动态的自己处理
            dynamicMap.put(ComParamContact.Common.TIMESTAMP, String.valueOf(TokenManager.getInstance().getTimestamp()));
        }
        if (!isAccessToken()) {//是否添加token
            String acccess = TokenManager.getInstance().getAuthModel().getAccessToken();
            dynamicMap.put(ComParamContact.Common.ACCESSTOKEN, acccess);
        }
        if (isSign()) {//是否签名,因为你的字段key可能不是sign，这种动态的自己处理
            dynamicMap.put(ComParamContact.Common.SIGN, sign(dynamicMap));
        }
        return dynamicMap;//dynamicMap:是原有的全局参数+局部参数+新增的动态参数
    }

    //签名规则：POST+url+参数的拼装+secret
    private String sign(TreeMap<String, String> dynamicMap) {
        String url = getHttpUrl().url().toString();
        StringBuilder sb = new StringBuilder("POST");
        sb.append(url);
        for (Map.Entry<String, String> entry : dynamicMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        sb.append("afd55f877bad4aaeab45fb4ca567d234");
        String sign = sb.toString();
        try {
            sign = URLDecoder.decode(sb.toString(), Charset.forName("UTF-8").name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        HttpLog.i(sign);
        return MD5.encode(sign);
    }
}
