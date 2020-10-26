package application.controller;

import application.AbstractIntegrationTest;
import application.api.request.LikeRequest;
import application.api.request.PostRequest;
import application.api.response.PostResponse;
import application.api.response.PostsListResponse;
import application.api.response.ResultResponse;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.persistence.model.Post;
import application.persistence.model.PostComment;
import application.persistence.repository.PostVoteRepository;
import application.persistence.repository.TagRepository;
import application.service.impl.PostServiceImpl;
import application.service.mapper.PostMapper;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApiPostControllerTest extends AbstractIntegrationTest {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostServiceImpl postService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;
    @MockBean
    Principal mockPrincipal;

    @Test
    @Transactional
    public void shouldReturnAllPosts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "recent"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getSortedPosts("recent")))));
    }

    @Test
    @Transactional
    public void shouldReturnPostsOnQuery() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/search")
                .param("offset", "0")
                .param("limit", "10")
                .param("query", "test02"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getQueriedPosts("test02")))));
    }

    @Test
    public void whenReturnNothingOnQuery_ThenStatus200AndEmptyResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/search")
                .param("offset", "0")
                .param("limit", "10")
                .param("query", "consensus"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(mapper.writeValueAsString(
                                new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    public void shouldReturnPostsByTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byTag")
                .param("offset", "0")
                .param("limit", "10")
                .param("tag", "JAVA"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getPostsByTag("JAVA")))));
    }

    @Test
    public void whenReturnNothingByTag_ThenStatus200AndEmptyResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byTag")
                .param("offset", "0")
                .param("limit", "10")
                .param("tag", "TAG"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(mapper.writeValueAsString(
                                new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    @Transactional
    public void shouldReturnPostsByDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byDate")
                .param("offset", "0")
                .param("limit", "10")
                .param("date", "2019-10-02"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getPostsByDate("2019-10-02")))));
    }

    @Test
    public void whenReturnNothingByDate_ThenStatus200AndEmptyResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/byDate")
                .param("offset", "0")
                .param("limit", "10")
                .param("date", "2000-01-01"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string(mapper.writeValueAsString(
                                new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    @Transactional
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void shouldReturnMyPosts() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "published")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getMyPosts(mockPrincipal, "published")))));
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void whenParameterIsInvalid_ThenStatus200AndEmptyResponse() throws Exception {
        //authenticate user by
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "invalid")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    @Transactional
    @WithMockUser(username = "petrov@mail.ru", authorities = {"user:moderate"})
    public void shouldReturnPostsForModeration() throws Exception {
        //authenticate user as moderator to get posts for moderation
        Mockito.when(mockPrincipal.getName()).thenReturn("petrov@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "accepted")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getPostsForModeration(mockPrincipal.getName(), "accepted")))));
    }

    @Test
    @WithMockUser(username = "petrov@mail.ru", authorities = {"user:moderate"})
    public void whenParameterIsInvalidByPostsForModeration_ThenStatus200AndEmptyResponse() throws Exception {
        //authenticate user as moderator to get posts for moderation
        Mockito.when(mockPrincipal.getName()).thenReturn("petrov@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "invalid")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    @Transactional
    public void shouldReturnPostByID() throws Exception {
        long postId = 1;
        //get post by id
        Post post = postService.getPostByID(postId);
        //authentication of post author (Ivanov), not to increase view counter
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        //get post comments sorted by id
        List<PostComment> comments = post.getPostComments().stream().sorted(Comparator.comparing(PostComment::getId)).collect(Collectors.toList());
        //get post tags
        List<String> tags = tagRepository.findAllByPost(postId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + postId)
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(
                        postMapper.convertToDto(post, tags, comments))));
    }

    @Test
    public void whenDoesNotReturnPostByID_ThenStatus404anExceptionThrown() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/10000")
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void shouldUpdatePost() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        //create request
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Test title", new String[0], "Test text Test text Test text Test text Test text Test text ");
        //create response
        ResultResponse resultResponse = new ResultResponse(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void ifSomethingWrong_ThenShouldNotUpdatePostStatus200AndAppropriateResponse() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        //text is shorter than 50 characters
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Test title", new String[0], "Test");
        ApiValidationError errors = new ApiValidationError();
        errors.setText("Text is too short");
        ApiError apiError = new ApiError(false, errors);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(apiError)));
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void shouldPlaceNewPost() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        //create request
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Test title", new String[0],
                "Test text Test text Test text Test text Test text Test text ");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void ifSomethingWrong_ThenShouldNotPlaceNewPostStatus200AndAppropriateResponse() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        //title is shorter than 3 characters
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Hm", new String[0], "Test text Test text Test text Test text Test text Test text ");
        ApiValidationError errors = new ApiValidationError();
        errors.setTitle("Title is too short");
        ApiError apiError = new ApiError(false, errors);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(apiError)));
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void shouldLikePost() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        LikeRequest postRequest = new LikeRequest(1L);
        //result should be true
        ResultResponse resultResponse = new ResultResponse(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    @WithMockUser(username = "petrov@mail.ru", authorities = {"user:write"})
    public void ifPostHasAlreadyBeenLikedByUser_Then200AndFalseResponse() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("petrov@mail.ru");
        LikeRequest postRequest = new LikeRequest(1L);
        //result should be false
        ResultResponse resultResponse = new ResultResponse(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    @WithMockUser(username = "petrov@mail.ru", authorities = {"user:write"})
    public void shouldDislikePost() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("petrov@mail.ru");
        LikeRequest request = new LikeRequest(2L);
        //result should be true
        ResultResponse resultResponse = new ResultResponse(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    @WithMockUser(username = "ivanov@mail.ru", authorities = {"user:write"})
    public void ifPostHasAlreadyBeenDislikedByUser_Then200AndFalseResponse() throws Exception {
        //authenticate user
        Mockito.when(mockPrincipal.getName()).thenReturn("ivanov@mail.ru");
        LikeRequest postRequest = new LikeRequest(2L);
        //result should be false
        ResultResponse resultResponse = new ResultResponse(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .principal(mockPrincipal))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @After
    public void tearDown() {
        //clear PostVoteRepository
        postVoteRepository.deleteAllByPostIdAndByUserId(1, 1);
        postVoteRepository.deleteAllByPostIdAndByUserId(2, 2);
    }
}
