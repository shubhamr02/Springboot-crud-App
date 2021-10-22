package com.example.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.model.UserModel;
import com.example.repository.UserRepository;


@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	public List<UserModel> getAllUsers(){
		return (List<UserModel>) userRepository.findAll();
	}
	
	public void addUser(UserModel userRecord) {
		userRepository.save(userRecord);
	}
	
	public UserModel getUserId(int id) {
		return userRepository.findById(id).get();
	}
	public void saveUser(UserModel user) {
        this.userRepository.save(user);
    }
	
	public void delete(int id) {
		this.userRepository.deleteById(id);
	}
}
