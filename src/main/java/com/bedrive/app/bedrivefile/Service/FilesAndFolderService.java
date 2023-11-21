package com.bedrive.app.bedrivefile.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Getter
@Setter
public class FilesAndFolderService {
    private String accessToken;

    public JsonNode getFileEntries(Map<String, Object> params) {
        String baseUrl="https://cloud.edusoft.vn/api/v1/drive/file-entries";
        String url = baseUrl + "?";
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");

        headers.add("Authorization","Bearer "+this.accessToken);


        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url += entry.getKey() + "=" + entry.getValue() + "&";
        }

        // Xóa dấu & cuối cùng nếu có
        if (url.endsWith("&")) {
            url = url.substring(0, url.length() - 1);
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

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
            System.out.println("Lỗi lấy thông tin file/thư mục : "+ex.getMessage());
            return  null;
        }

    }
    public JsonNode createFolder(String name, String parentId) {

        String baseUrl="https://cloud.edusoft.vn/api/v1/folders";
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-type", "application/json");
        headers.add("Authorization", "Bearer " + this.accessToken);


        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>("{\"name\":\"" + name + "\",\"parentId\":" + parentId + "}", headers);


         try{

             ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, request, String.class);
             ObjectMapper objectMapper= new ObjectMapper();
             JsonNode root= objectMapper.readTree(responseEntity.getBody());

             return  root;

         }catch (Exception ex){
             System.out.println("Lỗi tạo folder : "+ex.getMessage());
             return  null;
         }

    }


}
