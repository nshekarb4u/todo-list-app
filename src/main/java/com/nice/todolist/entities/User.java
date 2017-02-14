package com.nice.todolist.entities;

import static com.nice.todolist.util.Constants.USERNAME_MAX_LENGTH;
import static com.nice.todolist.util.Constants.REGEX_USER_NAME;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nice.todolist.util.JsonDateSerializer;

import lombok.Data;

@Entity
@Table(name="USER",schema="TODODB")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	@Column(name="USER_NAME", nullable = false, length = USERNAME_MAX_LENGTH)
	@Pattern(regexp = REGEX_USER_NAME, message = "{userName_Invalid}")
	private String userName;
	
	@Column(name="FIRST_NAME", nullable = true, length = 50)
	private String firstName;
	
	@Column(name="MIDDLE_NAME", nullable = true, length = 50)
	private String middleName;
	
	@Column(name="LAST_NAME", nullable = true, length = 50)
	private String lastName;
	
	@Size(max=120, message = "{email_Size}")
	@Column(name="EMAIL", nullable = true)
	@Email(message = "{email_Pattern}")
	private String email;
	
	@Column(name="ROLE_ID")
	private String roleId;
	
	@JsonSerialize(using=JsonDateSerializer.class)
	@Column(name="CREATED_DATE")
	private Date createdDate;
	
	@JsonSerialize(using=JsonDateSerializer.class)
	@Column(name="MODIFIED_DATE")
	private Date modifiedDate;	
}
