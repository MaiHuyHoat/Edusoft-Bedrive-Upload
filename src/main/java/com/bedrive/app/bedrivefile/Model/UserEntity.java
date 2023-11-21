package com.bedrive.app.bedrivefile.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
// thay ổi tên bảng cho đúng với csdl
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    // có thể thay tên cột tương ứng với tên tài khoản người dùng, email,, khới với tên thư mục
    @Column(name = "email")
    @Id
    public String email;
    @Column(name = "password")
     public String password;
    @Column(name = "token_name")
     public  String token_name;


//    webmaster@edusoft.vn  / password:  R6ZOethcwz4kRMX


}
