package com.nice.todolist.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nice.todolist.util.JsonDateSerializer;

import lombok.Data;

@Data
@Entity
@Table(name="TASK",schema="TODODB")
public class Task {

	@Id
	@GeneratedValue
	@Column(name="ID")
	private Long id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="STATUS")
	private String status;
	
	@JsonSerialize(using=JsonDateSerializer.class)
	@Column(name="CREATED_DATE")
	private Date createdDate;
	
	@JsonIgnore
	@OneToOne(cascade=CascadeType.ALL)
    @JoinTable(name="TASK_ASSIGNMENT",schema="TODODB",
               joinColumns={@JoinColumn(name="task_id", referencedColumnName="id")},
               inverseJoinColumns={@JoinColumn(name="user_id", referencedColumnName="id")})
    private User user;
}
