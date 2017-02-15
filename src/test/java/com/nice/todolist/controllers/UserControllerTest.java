package com.nice.todolist.controllers;


import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.nice.todolist.dto.UserDto;
import com.nice.todolist.entities.User;
import com.nice.todolist.exception.TodoNotFoundException;
import com.nice.todolist.services.UserService;
import com.nice.todolist.services.UserServiceImpl;
import com.nice.todolist.util.Constants;
import com.nice.todolist.util.TestUtil;
import com.nice.todolist.util.UserBuilder;
import com.nice.todolist.util.UserDtoBuilder;

/**
 * @author Shekar Nyala
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
	
	private MockMvc mockMvc;

	@Autowired @Qualifier(value="userService") 
    private UserService userServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
    	Mockito.reset(userServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @Test
    public void add_EmptyUserEntry_ShouldReturnValidationErrorForUserName() throws Exception {
        UserDto dto = new UserDto();

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]", containsString("userName:The userName should not be empty.")));
         
        verifyZeroInteractions(userServiceMock);
    }
    
    @Test
    public void add_UserEntryWithTooLongValues_ShouldReturnValidationErrors() throws Exception {
        UserDto dto = new UserDto();
        dto.setUserName(TestUtil.createStringWithLength(Constants.MAX_LENGTH_USERNAME+1));
        dto.setEmail(TestUtil.createStringWithLength(Constants.MAX_LENGTH_EMAIL+1));
        
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errors", hasSize(3)))
                .andExpect(jsonPath("$.errors[*]", containsInAnyOrder(
                		"userName:The maximum length of the userName is 30 characters."
                	   ,"email:length must be between 0 and 120"
                	   ,"email:not a well-formed email address")));
         
        verifyZeroInteractions(userServiceMock);
    }
    
    @Test
    public void add_NewUserEntry_ShouldAddUserEntryAndReturnAddedEntry() throws Exception {
        UserDto dto = getTestUserDto();
        User  added = getTestUser();
        
        when(userServiceMock.createUser(any(UserDto.class))).thenReturn(added);
        
        mockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .accept(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is("nshekarb4u")))
                .andExpect(jsonPath("$.email", is("nshekarb4u@nice.com")));
         
        ArgumentCaptor<UserDto> dtoCaptor = ArgumentCaptor.forClass(UserDto.class);
        verify(userServiceMock, times(1)).createUser(dtoCaptor.capture());
        verifyNoMoreInteractions(userServiceMock);
        
        UserDto dtoArgument = dtoCaptor.getValue();
        assertNull(dtoArgument.getId());
        assertThat(dtoArgument.getUserName(), is("nshekarb4u"));
        assertThat(dtoArgument.getEmail(), is("nshekarb4u@nice.com"));
    }
    
    @Test
    public void deleteById_UserEntryFound_ShouldDeleteUserEntryAndReturnIt() throws Exception {
        User deleted = TestUtil.getTestUser();

        when(userServiceMock.deleteUserById(1L)).thenReturn(deleted);

        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is(TestUtil.USERNAME)))
                .andExpect(jsonPath("$.email", is(TestUtil.EMAIL)));

        verify(userServiceMock, times(1)).deleteUserById(1L);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void deleteById_UserIsNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        when(userServiceMock.deleteUserById(3L)).thenThrow(new TodoNotFoundException(""));

        mockMvc.perform(delete("/api/users/{id}", 3L))
                .andExpect(status().isNotFound());

        verify(userServiceMock, times(1)).deleteUserById(3L);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void findAll_UsersFound_ShouldReturnFoundUserEntries() throws Exception {
        User first  = TestUtil.getTestUser();first.setUserName("userName1");first.setId(1L);
        User second = TestUtil.getTestUser();second.setUserName("userName2");second.setId(2L);
         
        when(userServiceMock.getAllUsers()).thenReturn(Arrays.asList(first, second));
        
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userName", is("userName1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].userName", is("userName2")));
        
        verify(userServiceMock, times(1)).getAllUsers();
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void findById_UserEntryFound_ShouldReturnFoundUserEntry() throws Exception {
        User found = TestUtil.getTestUser();
        String userId = String.valueOf(1L);
        
        when(userServiceMock.findUserByIdOrName(userId)).thenReturn(found);
        
        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is("Lorem ipsum")))
                .andExpect(jsonPath("$.email", is("Foo")));

        verify(userServiceMock, times(1)).findUserByIdOrName(userId);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void findById_UserEntryNotFound_ShouldReturnHttpStatusCode404() throws Exception {
    	String userId = String.valueOf(1L);
        when(userServiceMock.findUserByIdOrName(userId)).thenThrow(new TodoNotFoundException(""));

        mockMvc.perform(get("/api/users/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(userServiceMock, times(1)).findUserByIdOrName(userId);
        verifyNoMoreInteractions(userServiceMock);
    }

    @Test
    public void update_EmptyUserEntry_ShouldReturnValidationErrorForUserName() throws Exception {
        UserDto dto = TestUtil.getTestUserDto();

        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0]", containsString("should not be empty")));

        verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void update_UserNameAndEmailAreTooLong_ShouldReturnValidationErrorsForEmailAndUserName() throws Exception {
    	TestUtil.getTestUserDto();

        UserDto dto = new UserDto();dto.setUserName(TestUtil.createStringWithLength(Constants.MAX_LENGTH_USERNAME + 1));

        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fieldErrors", hasSize(2)))
                .andExpect(jsonPath("$.fieldErrors[*].path", containsInAnyOrder("title", "description")))
                .andExpect(jsonPath("$.fieldErrors[*].message", containsInAnyOrder(
                        "The maximum length of the description is 500 characters.",
                        "The maximum length of the title is 100 characters."
                )));

        verifyZeroInteractions(userServiceMock);
    }

    @Test
    public void update_UserEntryNotFound_ShouldReturnHttpStatusCode404() throws Exception {
        UserDto dto = TestUtil.getUpdatedTestUserDto();

        when(userServiceMock.updateUser(anyLong(), any(UserDto.class))).thenThrow(new TodoNotFoundException(""));

        mockMvc.perform(put("/api/todo/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isNotFound());

        ArgumentCaptor<UserDto> dtoCaptor = ArgumentCaptor.forClass(UserDto.class);
        verify(userServiceMock, times(1)).updateUser(anyLong(),dtoCaptor.capture());
        verifyNoMoreInteractions(userServiceMock);

        UserDto dtoArgument = dtoCaptor.getValue();
        assertThat(dtoArgument.getId(), is(3L));
        assertThat(dtoArgument.getUserName(), is(TestUtil.USERNAME_UPDATED));
        assertThat(dtoArgument.getEmail(), is(TestUtil.EMAIL_UPDATED));
    }

    @Test
    public void update_TodoEntryFound_ShouldUpdateTodoEntryAndReturnIt() throws Exception {
        UserDto dto = TestUtil.getUpdatedTestUserDto();

        User updated = TestUtil.getUpdatedTestUser();

        when(userServiceMock.updateUser(anyLong(), any(UserDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/users/{id}", 1L)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(dto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is(TestUtil.USERNAME_UPDATED)))
                .andExpect(jsonPath("$.email", is(TestUtil.EMAIL_UPDATED)));

        ArgumentCaptor<UserDto> dtoCaptor = ArgumentCaptor.forClass(UserDto.class);
        verify(userServiceMock, times(1)).updateUser(anyLong(),dtoCaptor.capture());
        verifyNoMoreInteractions(userServiceMock);

        UserDto dtoArgument = dtoCaptor.getValue();
        assertThat(dtoArgument.getId(), is(1L));
        assertThat(dtoArgument.getUserName(), is(TestUtil.USERNAME_UPDATED));
        assertThat(dtoArgument.getEmail(), is(TestUtil.EMAIL_UPDATED));
    }


	private UserDto getTestUserDto() {
		return new UserDtoBuilder()
		          .userName("nshekarb4u")
		          .email("nshekarb4u@nice.com")
		          .build();
	}
	
	private User getTestUser() {
		return new UserBuilder()
		          .id(1L)
		          .userName("nshekarb4u")
		          .email("nshekarb4u@nice.com")
		          .build();
	}
}
