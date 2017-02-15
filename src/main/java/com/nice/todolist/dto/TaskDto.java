/**
 * 
 */
package com.nice.todolist.dto;

import java.util.Date;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nice.todolist.util.Constants;
import com.nice.todolist.util.JsonDateSerializer;

import lombok.Data;

/**
 * @author nshek
 *
 */
@Data
public class TaskDto {

	private Long id;
	
	@NotEmpty
	@Length(min=Constants.MIN_LENGTH_TASKNAME,max = Constants.MAX_LENGTH_TASKNAME)
	private String name;
	
	@Length(max = Constants.MAX_LENGTH_DESCRIPTION)
	private String description;
	
	@Pattern(regexp = "|Not Started|In Progress|Complete")
	private String status;
	
	@JsonSerialize(using=JsonDateSerializer.class)
	private Date createdDate;
	
}
