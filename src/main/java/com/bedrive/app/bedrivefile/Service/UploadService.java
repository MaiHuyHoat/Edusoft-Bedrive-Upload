package com.bedrive.app.bedrivefile.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Collections;
import java.util.Objects;

@Service
@Setter
@Getter
public class UploadService {
    private String accessToken;

    public JsonNode Upload (File file,String parentId,String relativePath){
        try {
            String apiUrl="https://cloud.edusoft.vn/api/v1/uploads";
            HttpHeaders headers= new HttpHeaders();

            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("accept", "application/json");
            headers.add("Authorization","Bearer " + this.accessToken);
            MultiValueMap<String, Object> body=  new LinkedMultiValueMap<>();

            body.add("file",new FileSystemResource(file));
            body.add("parentId",parentId);
            body.add("relativePath",relativePath);
            // tạo yêu cầu post
            HttpEntity<MultiValueMap<String, Object>>  requestEntity= new HttpEntity<>(body,headers);
            // thực hiện yêu cầu post
            ResponseEntity<String> responseEntity= new RestTemplate().postForEntity(apiUrl,requestEntity,String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()){
                ObjectMapper objectMapper= new ObjectMapper();
                JsonNode root= objectMapper.readTree(responseEntity.getBody());
                return  root;
            }
            else{
                // Xử lý lỗi ở đây
                System.out.println("HTTP Response Code: " + responseEntity.getStatusCode());
                return null;
            }


        }catch (Exception ex){
            System.out.println("Lỗi upload file cho người dùng: "+ex.getMessage());
            return null;
        }

    }
}
