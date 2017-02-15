package com.nice.todolist.entities;

import static com.nice.todolist.util.Constants.MAX_LENGTH_USERNAME;
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
	
	@Column(name="USER_NAME", nullable = false, length = MAX_LENGTH_USERNAME)
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
	
	public static Builder getBuilder(String userName) {
        return new Builder(userName);
    }
	
	public static class Builder {

        private User built;
        
        public Builder(String userName) {
            built = new User();
            built.userName = userName;
        }
        
        public User build() {
            return built;
        }
        
        public Builder firstName(String firstName) {
            built.firstName = firstName;
            return this;
        }
        
        public Builder middleName(String middleName) {
            built.middleName = middleName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            built.lastName = lastName;
            return this;
        }
        
        public Builder email(String email) {
            built.email = email;
            return this;
        }
        
        public Builder createdDate(Date createdDate) {
            built.createdDate = createdDate;
            return this;
        }
        
        public Builder modifiedDate(Date modifiedDate) {
            built.modifiedDate = modifiedDate;
            return this;
        }
	}
}
