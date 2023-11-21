package com.bedrive.app.bedrivefile.Thread;

import com.bedrive.app.bedrivefile.Model.UserEntity;
import com.bedrive.app.bedrivefile.Service.AuthService;
import com.bedrive.app.bedrivefile.Service.FilesAndFolderService;
import com.bedrive.app.bedrivefile.Service.UploadService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class UploadUserThread implements Runnable {
    private final UserEntity user;
    private final File folder;
    private String access_token = null;
    private final AuthService authService;
    private final FilesAndFolderService filesAndFolderService;
    private final UploadService uploadService;
    private int countsFile = 0;
    private int currentCountFile = 0;
    private final Logger logger = LoggerFactory.getLogger(UploadUserThread.class);

    public UploadUserThread(UserEntity user, File folder) {
        this.user = user;
        this.folder = folder;
        this.authService = new AuthService();
        this.filesAndFolderService = new FilesAndFolderService();
        this.uploadService = new UploadService();
        // thực hiện setup các thuộc tính cần thiết
        this.countFileInFolder(this.folder);
        this.getAccessTokenUser(this.user);
        this.filesAndFolderService.setAccessToken(this.access_token);
        this.uploadService.setAccessToken(this.access_token);
    }

    private void getAccessTokenUser(UserEntity user) {
        this.access_token = this.authService.Login(user);
        if(this.access_token==null) {
            this.logger.info(this.user.email+":"+"Đang tạo tài khoản cho người dùng ");
              if(     this.authService.Register(user)){
                  this.logger.info(this.user.email+":"+"Tạo taì khoản thành công  ");
                  this.access_token=this.authService.Login(user);
              }else{
                  this.logger.warn(this.user.email+": Đăng ký tài khoản người dùng thất bại ");
                  this.access_token=null;
              }

        }
    }

    private boolean uploadFile(File file, String parentId, String relativePath, UserEntity user) {
        if (file.isDirectory()) {
            String folderId= null;
            if(!checkExistedEntrie(file,parentId)){
               folderId = createFolder(file, parentId);

            }else{
                this.logger.info(this.user.email+": không thể tạo folder "+ file.getName()+" do đã tồn tại trên hệ thống");
                folderId= this.getExistedFolderId(file,parentId);
            }
            if (folderId == null) {
                logger.info(this.user.email+" :"+"Không thể tiếp tục upload các file trong thư mục : " + folder.getName());
                return false;
            }
            for (File f : file.listFiles()) {
                uploadFile(f, folderId, "relative", user);
            }
        } else {
            if (!checkExistedEntrie(file, parentId)) {
                this.uploadService.Upload(file, parentId, "relative");
                this.currentCountFile++;
                DecimalFormat format = new DecimalFormat("#.##");
                logger.info( this.user.email + ": " + format.format((double) this.currentCountFile / countsFile * 100) + "%");

                return true;
            } else {
                logger.info(this.user.email+" :"+file.getName() + " đã tồn tại trên hệ thống ");
                this.currentCountFile++;
                DecimalFormat format = new DecimalFormat("#.##");
                logger.info( this.user.email + ": " + format.format((double) this.currentCountFile / countsFile * 100) + "%");
                return false;
            }
        }
        return false;
    }
   private  String getExistedFolderId(File file, String parentId){
       String fileName = file.getName();
       Map<String, Object> constraint = new HashMap<>();
       String[] parentIdList = new String[]{parentId};
       constraint.put("query", fileName);
       constraint.put("parentIds", parentIdList);
       JsonNode jsonRoot = this.filesAndFolderService.getFileEntries(constraint);
       JsonNode data= jsonRoot.path("data");
       if (data.isArray()) {
           ObjectMapper objectMapper = new ObjectMapper();
           JsonNode[] jsonNodes = objectMapper.convertValue(data, JsonNode[].class);
           JsonNode folder= jsonNodes[0];
           String id= folder.path("id").toString();
           return id ;
       } else {
           logger.warn(this.user.email+" :"+"Lỗi đọc d liệu khi checkfile trên hệ thống  : " + file.getName());
           return null;
       }
   }
    private boolean checkExistedEntrie(File file, String parentId) {
        String fileName = file.getName();
        Map<String, Object> constraint = new HashMap<>();
        String[] parentIdList = new String[]{parentId};
        constraint.put("query", fileName);
        constraint.put("parentIds", parentIdList);
        JsonNode jsonRoot = this.filesAndFolderService.getFileEntries(constraint);
        JsonNode data = jsonRoot.path("data");
        if (data.isArray()) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode[] jsonNodes = objectMapper.convertValue(data, JsonNode[].class);
            return jsonNodes.length > 0;
        } else {
            logger.warn(this.user.email+" :"+"Lỗi đọc d liệu khi checkfile trên hệ thống  : " + file.getName());
            return true;
        }

    }

    private void countFileInFolder(File file) {

            if (file.isDirectory()){
                    for(File f : file.listFiles()){
                       this.countFileInFolder(f);
                    }
            }
            else{
                this.countsFile++;
            }
    }

    private String createFolder(File folder, String parentId) {
        logger.info(this.user.email+" :"+"Đang tạo thư mục : " + folder.getName());
        JsonNode jsonRoot = this.filesAndFolderService.createFolder(folder.getName(), parentId);
        if (jsonRoot != null) {

                JsonNode jsonFolder = jsonRoot.path("folder");
                this.logger.info(this.user.email+" | Tạo folder "+folder.getName());
                return jsonFolder.path("id").toString();


        } else {
            logger.warn(this.user.email+" :"+"Lỗi không tạo được folder : " + folder.getName());
            return null;
        }
    }

    @Override
    public void run() {
        System.out.println("Bắt đầu thực hiện tiến trình upload cho tài khoản : "+this.user.email);
        if (this.access_token == null) {
            logger.warn(this.user.email+" :"+"Lỗi! Không lấy được access_token của người dùng : " + this.user.getEmail());
            return;
        }

        for (File file : this.folder.listFiles()) {
            uploadFile(file, "null", "root", this.user);
        }
        System.out.println("Hoàn tất việc Upload cho tài khoản: "+this.user.email+" (100%)");
    }
}