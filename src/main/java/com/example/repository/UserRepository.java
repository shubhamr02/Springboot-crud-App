package com.example.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.model.UserModel;

public interface UserRepository extends CrudRepository<UserModel, Integer>{

}
