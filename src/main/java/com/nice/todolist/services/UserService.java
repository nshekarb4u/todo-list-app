package com.nice.todolist.services;

import static com.nice.todolist.util.Constants.REGEX_NUMERIC;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nice.todolist.entities.User;
import com.nice.todolist.exception.TodoException;
import com.nice.todolist.exception.TodoNotFoundException;
import com.nice.todolist.repositories.UserRepository;
import com.nice.todolist.util.PropertyUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private UserRepository userRepository;
	
	@Autowired
	public UserService(UserRepository userRepository){
		this.userRepository = userRepository;
	}
	
	@Transactional
	public User createUser(User user) {
		user.setCreatedDate(new Date());
        return userRepository.save(user);
	}
	
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
        return userRepository.findAll();
	}
	
	@Transactional(readOnly = true)
	public User findUserByIdOrName(String idOrName) throws TodoException{
		User found = null;
		
		if(idOrName.matches(REGEX_NUMERIC)){ //If the given idOrName is numberic value
			found = userRepository.findOne(Long.parseLong(idOrName));
			log.debug("Got the user entry by Id: {}", found);
		}else{
			found = userRepository.findByUserName(idOrName);
			log.debug("Got the user entry by userName: {}", found);
		}
		
		if(Objects.isNull(found)){
			throw new TodoNotFoundException("No data exists for the given userId:"+idOrName);
		}
		return found;
	}
	
	@Transactional(rollbackFor = {TodoNotFoundException.class})
	public User updateUser(Long userId, User newUserDtls) {
		User user = userRepository.findOne(userId);
		log.debug("Found the user entry: {}", user);
		
		if(Objects.isNull(user)){
			throw new TodoNotFoundException("No data exists with given userId:"+userId);
		}
		
		BeanUtils.copyProperties(newUserDtls, user,PropertyUtil.getNullPropertiesString(newUserDtls));
		user.setModifiedDate(new Date());
        return userRepository.save(user);
	}
	
	@Transactional(rollbackFor = {TodoNotFoundException.class})
	public User deleteUserById(Long userId) {
		User deleted = userRepository.findOne(userId);
		log.debug("Found the user entry: {}", deleted);
		
		if(Objects.isNull(deleted)){
		   throw new TodoNotFoundException("No record found with given id:"+userId+".Please verify and retry again." );
		}
		
		try{
			userRepository.delete(deleted);
		}catch(Exception exp){
			throw new TodoException("Could not able to remove the user. Reason:"+exp.getMessage());
		}
		return deleted;
	}
}
