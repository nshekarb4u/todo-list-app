/**
 * 
 */
package com.nice.todolist.services.impl;

import static com.nice.todolist.util.Constants.REGEX_NUMERIC;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nice.todolist.dto.TaskDto;
import com.nice.todolist.entities.Task;
import com.nice.todolist.entities.User;
import com.nice.todolist.exception.TodoException;
import com.nice.todolist.exception.TodoNotFoundException;
import com.nice.todolist.repositories.TaskRepository;
import com.nice.todolist.services.TaskService;
import com.nice.todolist.util.PropertyUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nshek
 *
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    private TaskRepository taskRepository;
	
	@Autowired
	public TaskServiceImpl(TaskRepository taskRepository){
		this.taskRepository = taskRepository;
	}
	
	@Transactional
	@Override
	public Task createTask(TaskDto added) {
		Task taskEntity = new Task();
		BeanUtils.copyProperties(added, taskEntity);
		taskEntity.setCreatedDate(new Date());
        return taskRepository.save(taskEntity);
	}

	@Transactional(rollbackFor = {TodoNotFoundException.class})
	@Override
	public Task deleteById(Long id) throws TodoNotFoundException {
		Task deleted = taskRepository.fetchByTaskId(id);
		log.debug("Found the user entry: {}", deleted);
		
		if(Objects.isNull(deleted)){
		   throw new TodoNotFoundException("No record found with given id:"+id+".Please verify and retry again." );
		}
		
		try{
			taskRepository.delete(deleted);
		}catch(Exception exp){
			throw new TodoException("Could not able to remove the user. Reason:"+exp.getMessage());
		}
		return deleted;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	}

	@Override
	public Task findById(Long id) throws TodoNotFoundException {
		Task found = null;
		
		found = taskRepository.fetchByTaskId(id);
		log.debug("Got the user entry by Id: {}", found);
				
		if(Objects.isNull(found)){
			throw new TodoNotFoundException("No data exists for the given userId:"+id);
		}
		return found;
	}

	@Override
	public Task update(TaskDto updatedDto) throws TodoNotFoundException {
		Task found = taskRepository.fetchByTaskId(updatedDto.getId());
		log.debug("Found the task entry: {}", found);
		
		if(Objects.isNull(found)){
			throw new TodoNotFoundException("No data exists with given userId:"+updatedDto.getId());
		}
		
		BeanUtils.copyProperties(updatedDto, found,PropertyUtil.getNullPropertiesString(updatedDto));
        return taskRepository.save(found);
	}

}
