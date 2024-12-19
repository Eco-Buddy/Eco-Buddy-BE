package com.Son.EcoBuddy.Login;

import com.Son.EcoBuddy.JPA.Entity.Token;
import com.Son.EcoBuddy.JPA.Entity.User;
import com.Son.EcoBuddy.Utility.AESUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;

import org.apache.commons.text.StringEscapeUtils;

@Getter
@Slf4j
@Controller
@PropertySource("classpath:application-private.properties")
public class NaverAPI {
    @Value("${naver.client_id}")
    private String naverClientId;

    @Value("${naver.client_secret}")
    private String naverClientSecret;

    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;

    private String state = URLEncoder.encode("1234", StandardCharsets.UTF_8);

    public HashMap<String, String> getAccessToken(String code) {
        HashMap<String, String> tokens = new HashMap<>();
        String reqUrl = "https://nid.naver.com/oauth2.0/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            //필수 쿼리 파라미터 세팅
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=").append(naverClientId);
            sb.append("&client_secret=").append(naverClientSecret);
            sb.append("&code=").append(code);
            sb.append("&state=").append(state);

            // 작성한 쿼리 전송
            bw.write(sb.toString());
            bw.flush();

            // 응답을 받아와 출력
            int responseCode = conn.getResponseCode();
            log.info("[NaverApi.getAccessToken] responseCode = {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
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

            // 받아온 토큰 정보들을 저장
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            tokens.put("access_token", element.getAsJsonObject().get("access_token").getAsString());
            tokens.put("acc_exp_period", element.getAsJsonObject().get("expires_in").getAsString());
            tokens.put("refresh_token", element.getAsJsonObject().get("refresh_token").getAsString());
            tokens.put("ref_exp_period", "31536000");

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public HashMap<String, String> refreshAccessToken(Token old_token) {
        HashMap<String, String> new_token = new HashMap<>();
        String reqUrl = "https://nid.naver.com/oauth2.0/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            sb.append("grant_type=refresh_token");
            sb.append("&client_id=").append(naverClientId);
            sb.append("&client_secret=").append(naverClientSecret);
            sb.append("&refresh_token=").append(AESUtil.decrypt(old_token.getRefreshToken()));

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("[NaverApi.getAccessToken] responseCode = {}", responseCode);

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

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_token;
    }

    public User getUserInfo(String accessToken) {
        User user = null;
        String reqUrl = "https://openapi.naver.com/v1/nid/me";
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 헤더 설정
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            // 응답을 받아와 출력
            int responseCode = conn.getResponseCode();
            log.info("[naverApi.getUserInfo] responseCode : {}", responseCode);

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

            JsonObject properties = element.getAsJsonObject().get("response").getAsJsonObject();

            String id = properties.getAsJsonObject().get("id").getAsString();
            String nickname = StringEscapeUtils.unescapeJava(properties.getAsJsonObject().get("nickname").getAsString());
            String imageUrl = properties.getAsJsonObject().get("profile_image").getAsString();

            user = User.builder()
                    .id(id)
                    .name(nickname)
                    .joinDate(LocalDate.now())
                    .imageUrl(imageUrl)
                    .platform("naver")
                    .build();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public void naverLogout(String accessToken) {
        String reqUrl = "https://nid.naver.com/oauth2.0/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //필수 헤더 세팅
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setDoOutput(true); //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();

            //필수 쿼리 파라미터 세팅
            sb.append("grant_type=delete");
            sb.append("&client_id=").append(naverClientId);
            sb.append("&client_secret=").append(naverClientSecret);
            sb.append("&access_token=").append(accessToken);
            sb.append("&service_provider=NAVER");

            // 작성한 쿼리 전송
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            log.info("[NaverApi.getAccessToken] responseCode = {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
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

            br.close();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}