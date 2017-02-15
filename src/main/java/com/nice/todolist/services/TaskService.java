/**
 * 
 */
package com.nice.todolist.services;

import java.util.List;

import com.nice.todolist.dto.TaskDto;
import com.nice.todolist.entities.Task;
import com.nice.todolist.exception.TodoNotFoundException;


/**
 * @author nshek
 *
 */
public interface TaskService {

	/**
     * Adds a new task.
     * @param added The information of the added task entry.
     * @return  The added task entry.
     */
    public Task createTask(TaskDto added);

    /**
     * Deletes a task entry.
     * @param id    The id of the deleted task entry.
     * @return  The deleted task entry.
     * @throws TodoNotFoundException    if no task entry is found with the given id.
     */
    public Task deleteById(Long id) throws TodoNotFoundException;

    /**
     * Returns a list of task entries.
     * @return
     */
    public List<Task> getAllTasks();

    /**
     * Finds a task entry.
     * @param id    The id of the wanted task entry.
     * @return  The found task-entry.
     * @throws TodoNotFoundException    if no task entry is found with the given id.
     */
    public Task findById(Long id) throws TodoNotFoundException;

    /**
     * Updates the information of a task entry.
     * @param updated   The information of the updated task entry.
     * @return  The updated task entry.
     * @throws TodoNotFoundException    If no task entry is found with the given id.
     */
    public Task update(TaskDto updated) throws TodoNotFoundException;

}
