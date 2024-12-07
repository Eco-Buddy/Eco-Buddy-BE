package com.Lobo.KakaoLoginTest.Login;

import com.Lobo.KakaoLoginTest.JPA.Entity.Token;
import com.Lobo.KakaoLoginTest.JPA.Entity.User;
import com.Lobo.KakaoLoginTest.Utility.AESUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;

@Getter
@Slf4j
@Controller
@PropertySource("classpath:application-private.properties")
public class KakaoAPI {
    @Value("${kakao.api_key}")
    private String kakaoApiKey;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;

    public HashMap<String, String> getAccessToken(String code) {
        HashMap<String, String> tokens = new HashMap<>();
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            //필수 쿼리 파라미터 세팅
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(kakaoApiKey);
            sb.append("&redirect_uri=").append(kakaoRedirectUri);
            sb.append("&code=").append(code);

            // 작성한 쿼리 전송
            bw.write(sb.toString());
            bw.flush();

            // 응답을 받아와 출력
            int responseCode = conn.getResponseCode();
            log.info("[KakaoApi.getAccessToken] responseCode = {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            // 받아온 토큰 정보들을 저장
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            tokens.put("access_token", element.getAsJsonObject().get("access_token").getAsString());
            tokens.put("acc_exp_period", element.getAsJsonObject().get("expires_in").getAsString());
            tokens.put("refresh_token", element.getAsJsonObject().get("refresh_token").getAsString());
            tokens.put("ref_exp_period", element.getAsJsonObject().get("refresh_token_expires_in").getAsString());

            br.close();
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return tokens;
    }

    public HashMap<String, String> refreshAccessToken(Token old_token) {
        HashMap<String, String> new_token = new HashMap<>();
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            sb.append("grant_type=refresh_token");
            sb.append("&client_id=").append(kakaoApiKey);
            sb.append("&refresh_token=").append(AESUtil.decrypt(old_token.getRefreshToken()));

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("[KakaoApi.getAccessToken] responseCode = {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            new_token.put("access_token", element.getAsJsonObject().get("access_token").getAsString());
            new_token.put("acc_exp_period", element.getAsJsonObject().get("expires_in").getAsString());
            if(element.getAsJsonObject().has("refresh_token")) {
                new_token.put("refresh_token", element.getAsJsonObject().get("refresh_token").getAsString());
                new_token.put("ref_exp_period", element.getAsJsonObject().get("refresh_token_expires_in").getAsString());
            }

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_token;
    }

    public User getUserInfo(String accessToken) {
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        User user = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 헤더 설정
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // 응답을 받아와 출력
            int responseCode = conn.getResponseCode();
            log.info("[KakaoApi.getUserInfo] responseCode : {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("responseBody = {}", result);

            // 받아온 유저 정보들을 저장
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();

            String id = element.getAsJsonObject().get("id").getAsString();
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String imageUrl = properties.getAsJsonObject().get("profile_image").getAsString();

            user = User.builder()
                    .id(id)
                    .name(nickname)
                    .joinDate(LocalDate.now())
                    .imageUrl(imageUrl)
                    .platform("kakao")
                    .build();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public void kakaoLogout(String accessToken) {
        String reqUrl = "https://kapi.kakao.com/v1/user/logout";

        try{
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 헤더 설정
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            // 응답을 받아와 출력
            int responseCode = conn.getResponseCode();
            log.info("[KakaoApi.kakaoLogout] responseCode : {}",  responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode <= 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line = "";
            StringBuilder responseSb = new StringBuilder();
            while((line = br.readLine()) != null){
                responseSb.append(line);
            }
            String result = responseSb.toString();
            log.info("kakao logout - responseBody = {}", result);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
