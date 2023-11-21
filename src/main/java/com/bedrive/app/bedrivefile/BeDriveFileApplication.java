package com.bedrive.app.bedrivefile;

import com.bedrive.app.bedrivefile.Model.UserEntity;

import com.bedrive.app.bedrivefile.Service.AuthService;
import com.bedrive.app.bedrivefile.Service.FilesAndFolderService;
import com.bedrive.app.bedrivefile.Service.UploadService;
import com.bedrive.app.bedrivefile.Service.UserService;
import com.bedrive.app.bedrivefile.Thread.UploadUserThread;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@Component
public class BeDriveFileApplication {
    public AuthService authService;
	public UploadService uploadService;
	public FilesAndFolderService filesAndFolderService;
	protected Logger logger= LoggerFactory.getLogger(BeDriveFileApplication.class);

	public UserService userService;
	// thay thế bằng đường dẫn đến thư mục gốc chứa các file của người dùng : RootFolder\{UserEmail}\*
	private  String pathRootFolder="F:\\Edusoft\\BeDrive\\RootFolder";



	@Autowired
	public BeDriveFileApplication(AuthService authService, UploadService uploadService,
								  FilesAndFolderService filesAndFolderService, UserService userService) {
		this.authService = authService;
		this.uploadService = uploadService;
		this.filesAndFolderService = filesAndFolderService;
		this.userService = userService;
	}

	public File getFolderByUser(String email){
		File rootFolder= new File(this.pathRootFolder);
		File[] folders= rootFolder.listFiles();
		for(File folder : folders){
			if(folder.isDirectory() && folder.getName().equals(email)){
				return folder;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BeDriveFileApplication.class, args);

		BeDriveFileApplication main = context.getBean(BeDriveFileApplication.class);

		ArrayList<UserEntity> listUser= main.userService.getAllUser();
		// multil thread. Đang để là 5 luồng ( upload, chạy 5 tài khoản cùng lúc). C thể thay đổi tuỳ vào điều kiện ram, cpu máy chủ
		ExecutorService executor= Executors.newFixedThreadPool(5);
         for(UserEntity user: listUser){
			 File folder= main.getFolderByUser(user.getEmail());
			 if(folder!=null){
				 try {

					 executor.submit(new UploadUserThread(user,folder));
				 }catch (Exception ex){
					 main.logger.warn("Lỗi thực thi chương trình :"+ex.getMessage());

				 }
			 }else{
				 main.logger.warn("không tìm thấy folder của người dùng: "+user.email);

			 }

		 }
		executor.shutdown();


	}

}
