package application.controller;

import application.AbstractIntegrationTest;
import application.api.request.LikeRequest;
import application.api.request.PostRequest;
import application.api.response.PostResponse;
import application.api.response.PostsListResponse;
import application.api.response.ResultResponse;
import application.exception.EntNotFoundException;
import application.exception.apierror.ApiError;
import application.exception.apierror.ApiValidationError;
import application.mapper.PostMapper;
import application.model.Post;
import application.model.PostComment;
import application.repository.PostRepository;
import application.repository.PostVoteRepository;
import application.repository.TagRepository;
import application.service.LoginServiceImpl;
import application.service.PostServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApiPostControllerTest extends AbstractIntegrationTest {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostServiceImpl postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;
    @Autowired
    private LoginServiceImpl loginService;

    private MockHttpSession session;

    @Before
    public void setUp() {
        session = new MockHttpSession();
    }

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
    public void shouldReturnMyPosts() throws Exception {
        //authentication of user with userId = 1,  to get user posts
        loginService.addSessionId(session.getId(), 1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "published")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getMyPosts(session, "published")))));
    }

    @Test
    public void whenDoesNotReturnMyPosts_ThenStatus200AndEmptyResponse() throws Exception {
        //authentication of user with userId = 1,  to get user posts
        loginService.addSessionId(session.getId(), 1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/my")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "invalid")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    @Transactional
    public void shouldReturnPostsForModeration() throws Exception {
        //authentication of user with userId = 2 (Moderator), to get user posts
        loginService.addSessionId(session.getId(), 2);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "accepted")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(postMapper.convertToDto(0, 10,
                                postService.getPostsForModeration(session, "accepted")))));
    }

    @Test
    public void whenDoesNotReturnPostsForModeration_ThenStatus200AndEmptyResponse() throws Exception {
        //authentication of user with userId = 2 (Moderator) , to get user posts
        loginService.addSessionId(session.getId(), 2);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/moderation")
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "invalid")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper
                        .writeValueAsString(new PostsListResponse(0, new PostResponse[0]))));
    }

    @Test
    @Transactional
    public void shouldReturnPostByID() throws Exception {
        long postId = 1;
        //get post by id
        Post post = postRepository.findById(postId).orElseThrow(EntNotFoundException::new);
        //authentication of post author, not to increase view counter
        loginService.addSessionId(session.getId(), post.getUser().getId());
        //get post comments sorted by id
        List<PostComment> comments = post.getPostComments().stream().sorted(Comparator.comparing(PostComment::getId)).collect(Collectors.toList());
        //get post tags
        List<String> tags = tagRepository.findAllByPost(postId);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + postId)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(
                        postMapper.convertToDto(post, tags, comments, session))));
    }

    @Test
    public void whenDoesNotReturnPostByID_ThenStatus404anExceptionThrown() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/10000")
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldUpdatePost() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Test title", null, "Test text Test text Test text Test text Test text Test text ");
        ResultResponse resultResponse = new ResultResponse(true);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    public void ifSomethingWrong_ThenShouldNotUpdatePostStatus200AndAppropriateResponse() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);
        //text is shorter than 50 characters
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Test title", null, "Test");
        ApiValidationError errors = new ApiValidationError();
        errors.setText("Text is too short");
        ApiError apiError = new ApiError(false, errors);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(apiError)));
    }

    @Test
    public void shouldPlaceNewPost() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);
        //create request
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Test title", new String[0],
                "Test text Test text Test text Test text Test text Test text ");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(new ResultResponse(true))));
    }

    @Test
    public void ifSomethingWrongByNewPosting_ThenShouldNotUpdatePostStatus200AndAppropriateResponse() throws Exception {
        //authenticate user
        loginService.addSessionId(session.getId(), 1);
        //title is shorter than 3 characters
        PostRequest postRequest = new PostRequest(1122233934L, 1,
                "Hm", new String[0], "Test text Test text Test text Test text Test text Test text ");
        ApiValidationError errors = new ApiValidationError();
        errors.setTitle("Title is too short");
        ApiError apiError = new ApiError(false, errors);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(apiError)));
    }

    @Test
    public void shouldLikePost() throws Exception {
        //authenticate the user who hasn't like the post yet
        loginService.addSessionId(session.getId(), 1);
        LikeRequest postRequest = new LikeRequest(1L);
        //result should be true
        ResultResponse resultResponse = new ResultResponse(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    public void ifPostHasAlreadyBeenLikedByUser_Then200AndFalseResponse() throws Exception {
        //authenticate the user who already liked the post
        loginService.addSessionId(session.getId(), 2);
        LikeRequest postRequest = new LikeRequest(1L);
        //result should be false
        ResultResponse resultResponse = new ResultResponse(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    public void shouldDislikePost() throws Exception {
        //authenticate the user who hasn't dislike the post yet
        loginService.addSessionId(session.getId(), 2);
        LikeRequest postRequest = new LikeRequest(2L);
        //result should be true
        ResultResponse resultResponse = new ResultResponse(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        mapper.writeValueAsString(resultResponse)));
    }

    @Test
    public void ifPostHasAlreadyBeenDislikedByUser_Then200AndFalseResponse() throws Exception {
        //authenticate the user who already disliked the post
        loginService.addSessionId(session.getId(), 1);
        LikeRequest postRequest = new LikeRequest(2L);
        //result should be false
        ResultResponse resultResponse = new ResultResponse(false);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/post/dislike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(postRequest))
                .session(session))
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
