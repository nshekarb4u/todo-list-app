package com.nice.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nice.todolist.dto.UserDto;

@SpringBootApplication
public class TodoListAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoListAppApplication.class, args);
	}
}
