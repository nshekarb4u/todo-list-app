package com.nice.todolist.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nice.todolist.entities.User;
import com.nice.todolist.services.UserService;

@RestController
@RequestMapping(value="/api/users",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces=MediaType.APPLICATION_JSON_VALUE)

public class UserController {
    
	private UserService userService;
	
	@Autowired
	public UserController(UserService userService){
		this.userService = userService;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public User createNewUser(@RequestBody @Valid User user,BindingResult bindingResult){
		if(bindingResult.hasErrors()){
			
		}
		return userService.createUser(user);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public Iterable<User> getAllUsers(){
		return userService.getAllUsers();
	}

	@RequestMapping(value = "/{id}", method=RequestMethod.GET)
    public User getByIdOrName(@PathVariable("id") String idOrUserName) {
        return userService.findUserByIdOrName(idOrUserName);
    }
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
    public User updateUser(@PathVariable("id") Long id, @RequestBody @Valid User userRequest) {
        return userService.updateUser(id, userRequest);
    }
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
    }
}