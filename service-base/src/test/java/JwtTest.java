
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTHeader;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Xqf
 * @version 1.0
 */
@Slf4j
public class JwtTest {

    //过期时间24小时
    private static long tokenExpiration=24*60*60*1000;

    private static String tokenSignKey = "atxqf";

    @Test
   public void test(){
        HashMap<String, Object> haders = new HashMap<>();
        //hader
        haders.put("typ","JWT");
        haders.put("alg", "HS256");
        //payload
        HashMap<String, Object> payloads = new HashMap<>();
        payloads.put("sub","guli-user");
        payloads.put("iss","guli-user");
        payloads.put("iat",new Date());
        payloads.put("exp",new Date(System.currentTimeMillis() + tokenExpiration));
        payloads.put("nbf",new Date(System.currentTimeMillis() +20*1000));
        payloads.put("jti", UUID.randomUUID());
        payloads.put("nickname", "xqf");
        //签名哈希
        JWTSigner signer = JWTSignerUtil.hs256(tokenSignKey.getBytes());
        String token = JWTUtil.createToken(haders,payloads,signer);
        log.info(token.toString());
    }
    @Test
    public void test2(){
        String token= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndWxpLXVzZXIiLCJuYmYiOjE2NzM2ODIzMzYsImlzcyI6Imd1bGktdXNlciIsIm5pY2tuYW1lIjoieHFmIiwiZXhwIjoxNjczNzY4NzE2LCJpYXQiOjE2NzM2ODIzMTYsImp0aSI6ImI4YWI0YjczLTVmNTctNDFlMy04NjI5LWRlMjg5NGZjMTZhNiJ9.ZFgl5c2KJaNuxEn2VG2yTFd1Or2zIz6wpG35Eg2vyUU";

        JWT jwt = JWTUtil.parseToken(token);
        JWTHeader header = jwt.getHeader();
        Object nickname = jwt.getPayload("nickname");
        JWTSigner signer = jwt.getSigner();
        log.info(header.toString());
        log.info(nickname.toString());
        log.info(signer.toString());
    }
}
