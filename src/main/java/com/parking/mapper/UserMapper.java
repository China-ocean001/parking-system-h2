package com.parking.mapper;

import com.parking.entity.User;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface UserMapper {
    User findByUsername(@Param("username") String username);
    int insert(User user);
    List<User> findAll();
    int deleteById(@Param("id") Long id);
}
