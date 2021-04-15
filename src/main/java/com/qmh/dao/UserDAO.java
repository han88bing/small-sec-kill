package com.qmh.dao;

import com.qmh.entity.User;

public interface UserDAO {
    User findById(Integer id);
}
