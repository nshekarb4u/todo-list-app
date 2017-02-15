/**
 * 
 */
package com.nice.todolist.services;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.nice.todolist.dto.TaskDto;
import com.nice.todolist.entities.Task;
import com.nice.todolist.exception.TodoException;
import com.nice.todolist.exception.TodoNotFoundException;
import com.nice.todolist.repositories.TaskRepository;
import com.nice.todolist.repositories.UserRepository;
import com.nice.todolist.services.impl.TaskServiceImpl;
import com.nice.todolist.services.impl.UserServiceImpl;
import com.nice.todolist.util.TestUtil;

/**
 * @author nshek
 *
 */
public class TaskServiceTest {

	private static final Long ID = 1L;    
    private static final Date CURRENT_DATE = new Date();
    
    private static final SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    
    @Mock private TaskRepository mockTaskRepository;
    private TaskService userService;
    
	@Before
    public void setUp() {
		initMocks(this);
		userService = new TaskServiceImpl(mockTaskRepository);
	}
	
	@Test
	public void add_NewUser_ShouldBeSavedSuccessfully() {
		TaskDto taskDto = TestUtil.getTestTaskDto();
		
		userService.createTask(taskDto);
		
		ArgumentCaptor<Task> userArgument = ArgumentCaptor.forClass(Task.class);
		verify(mockTaskRepository, times(1)).save(userArgument.capture());
		
		verifyNoMoreInteractions(mockTaskRepository);
		
		Task model = userArgument.getValue();

        assertNull(model.getId());
        assertThat(model.getName(), is(taskDto.getName()));
        assertThat(model.getDescription(), is(taskDto.getDescription()));
        assertThat(DATEFORMATTER.format(model.getCreatedDate()),is(DATEFORMATTER.format(CURRENT_DATE)));
	}
	
	@Test
    public void findAll_ShouldReturnListOfUsers() {
        List<Task> models = new ArrayList<>();
        when(mockTaskRepository.findAll()).thenReturn(models);

        List<Task> actual = userService.getAllUsers();

        verify(mockTaskRepository, times(1)).findAll();
        
        verifyNoMoreInteractions(mockTaskRepository);

        assertThat(actual, is(models));
    }
	
	@Test
    public void findById_UserEntryFound_ShouldReturnFoundUserEntry()  {
        Task model = TestUtil.getTestUser();model.setId(ID);

        when(mockTaskRepository.findOne(ID)).thenReturn(model);

        Task actual = userService.findUserByIdOrName(String.valueOf(ID));

        verify(mockTaskRepository, times(1)).findOne(ID);
        verifyNoMoreInteractions(mockTaskRepository);

        assertThat(actual, is(model));
    }

	@Test(expected = TodoNotFoundException.class)
    public void findById_UserEntryNotFound_ShouldThrowException() {
        when(mockTaskRepository.findOne(ID)).thenReturn(null);

        userService.findUserByIdOrName(String.valueOf(ID));

        verify(mockTaskRepository, times(1)).findOne(ID);        
        verifyNoMoreInteractions(mockTaskRepository);
    }
	
	@Test
    public void findByUserName_UserEntryFound_ShouldReturnFoundUserEntry()  {
        Task model = TestUtil.getTestUser();

        when(mockTaskRepository.findByUserName(TestUtil.USERNAME)).thenReturn(model);

        Task actual = userService.findUserByIdOrName(TestUtil.USERNAME);

        verify(mockTaskRepository, times(1)).findByUserName(TestUtil.USERNAME);
        verifyNoMoreInteractions(mockTaskRepository);

        assertThat(actual, is(model));
    }

	@Test(expected = TodoNotFoundException.class)
    public void findByUserName_UserEntryNotFound_ShouldThrowException() {
        when(mockTaskRepository.findByUserName(TestUtil.USERNAME)).thenReturn(null);

        userService.findUserByIdOrName(TestUtil.USERNAME);

        verify(mockTaskRepository, times(1)).findByUserName(TestUtil.USERNAME);        
        verifyNoMoreInteractions(mockTaskRepository);
    }
	
	@Test
    public void deleteById_UserEntryFound_ShouldDeleteUserEntryAndReturnIt() {
        Task model = TestUtil.getTestUser();model.setId(ID);

        when(mockTaskRepository.findOne(ID)).thenReturn(model);

        Task actual = userService.deleteUserById(ID);

        verify(mockTaskRepository, times(1)).findOne(ID);
        verify(mockTaskRepository, times(1)).delete(model);
        
        verifyNoMoreInteractions(mockTaskRepository);

        assertThat(actual, is(model));
    }

	@Test(expected = TodoException.class)
    public void deleteById_UserEntryFound_ShouldDeleteFailAndThrowException() {
        Task model = TestUtil.getTestUser();model.setId(ID);
         
        when(mockTaskRepository.findOne(ID)).thenReturn(model);
        doThrow(new TodoException()).when(mockTaskRepository).delete(model);
        
        userService.deleteUserById(ID);

        verify(mockTaskRepository, times(1)).findOne(ID);
        verify(mockTaskRepository, times(1)).delete(model);
        
        verifyNoMoreInteractions(mockTaskRepository);
    }
	
    @Test(expected = TodoNotFoundException.class)
    public void deleteById_UserEntryNotFound_ShouldThrowException() {
        when(mockTaskRepository.findOne(ID)).thenReturn(null);

        userService.deleteUserById(ID);

        verify(mockTaskRepository, times(1)).findOne(ID);
        verifyNoMoreInteractions(mockTaskRepository);
    }

    @Test
    public void update_UserEntryFound_ShouldUpdateSuccessfully() throws TodoNotFoundException {
        TaskDto dto = TestUtil.getUpdatedTestUserDto();
        
        Task model = TestUtil.getTestUser();model.setId(ID);

        when(mockTaskRepository.findOne(dto.getId())).thenReturn(model);
        ArgumentCaptor<Task> userArgument = ArgumentCaptor.forClass(Task.class);
		

        userService.updateUser(ID, dto);

        verify(mockTaskRepository, times(1)).findOne(dto.getId());
        verify(mockTaskRepository, times(1)).save(userArgument.capture());
        
        verifyNoMoreInteractions(mockTaskRepository);

        Task actual = userArgument.getValue();
        
        assertThat(actual.getId(), is(dto.getId()));
        assertThat(actual.getUserName(), is(dto.getUserName()));
        assertThat(actual.getFirstName(), is(dto.getFirstName()));
        assertThat(DATEFORMATTER.format(model.getModifiedDate()),is(DATEFORMATTER.format(CURRENT_DATE)));
    }

    @Test(expected = TodoNotFoundException.class)
    public void update_UserEntryNotFound_ShouldThrowException() throws TodoNotFoundException {
    	TaskDto dto = TestUtil.getUpdatedTestUserDto();

        when(mockTaskRepository.findOne(dto.getId())).thenReturn(null);

        userService.updateUser(ID,dto);

        verify(mockTaskRepository, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(mockTaskRepository);
    }
}
