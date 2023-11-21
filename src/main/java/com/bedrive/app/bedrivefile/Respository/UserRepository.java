package com.bedrive.app.bedrivefile.Respository;

import com.bedrive.app.bedrivefile.Model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,String> {
}
