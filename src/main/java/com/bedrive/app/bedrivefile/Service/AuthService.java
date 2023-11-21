package com.bedrive.app.bedrivefile.Service;

import com.bedrive.app.bedrivefile.Model.UserEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class AuthService {

    public String Login(UserEntity user) {
        try {
            String apiUrl = "https://cloud.edusoft.vn/api/v1/auth/login";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            HttpEntity<UserEntity> request = new HttpEntity<>(user, headers);

            ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(apiUrl, request, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Xử lý kết quả ở đây
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseEntity.getBody());
                JsonNode userNode = root.path("user");
                JsonNode access_token= userNode.path("access_token");


                return access_token.toString().replace("\"","");
            } else {
                // Xử lý lỗi ở đây
                System.out.println(user.email+" :"+ "HTTP Response Code: " + responseEntity.getStatusCode());
                return null;
            }

        } catch (Exception ex) {
            System.out.println(user.email+" :"+ "Lỗi đăng nhập: " + ex.getMessage());
            return null;
        }
    }
    public boolean Register(UserEntity user) {

            String apiUrl = "https://cloud.edusoft.vn/api/v1/auth/register";

            HttpHeaders headers = new HttpHeaders();
            headers.add("accept", "application/json");
            headers.add("Content-type", "application/json");

            HttpEntity<String> request = new HttpEntity<>("{\"email\": \""+user.getEmail()+"\", \"password\": \""+user.getPassword()+"\", \"password_confirmation\": \""+user.getPassword()+"\"}", headers);
            System.out.println(request.toString());


        try {
            ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(apiUrl, request, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                // Xử lý kết quả ở đây

                return  true;
            } else {
                // Xử lý lỗi ở đây
                System.out.println(user.email+" :"+ "HTTP Response Code: " + responseEntity.getStatusCode());
                return false;
            }

        } catch (Exception ex) {
            System.out.println(user.email+" :"+ "Lỗi tạo tài khoản: " + ex.getMessage());
            return false;
        }
    }
}
