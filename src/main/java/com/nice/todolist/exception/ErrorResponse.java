package com.nice.todolist.exception;

import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
	
	private boolean success;
	private String message;
	private List<String> errors;
}
