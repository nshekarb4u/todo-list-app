package com.nice.todolist.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nice.todolist.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	@Query("select o from Task o where o.id=?1")
	Task fetchByTaskId(Long taskId);
		
	Task findByStatus(String status);
	
}
