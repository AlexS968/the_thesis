package application.controller;

import application.AbstractIntegrationTest;
import application.exception.EntityNotFoundException;
import application.mapper.PostMapper;
import application.model.*;
import application.repository.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.*;

public class ApiPostControllerTest extends AbstractIntegrationTest {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;

    Post post = new Post();
    List<String> tags;
    Set<PostComment> comments;

    @Before
    public void setUp() {
        tags = Arrays.asList("SPRING", "JAVA");
        post.setTitle("Test PostByID");
        post.setText("Test PostByID Test PostByID Test PostByID Test PostByID Test PostByID Test PostByID");
        post.setUser(userRepository.findById(2L).orElseThrow(EntityNotFoundException::new));
        Set<TagToPost> tagSet = new HashSet<>();
        Tag spring = new Tag("SPRING");
        Tag java = new Tag("JAVA");
        tagRepository.save(spring);
        tagRepository.save(java);
        tagSet.add(new TagToPost(post, spring));
        tagSet.add(new TagToPost(post, java));
        comments = new HashSet<>();
        post.setTagToPosts(tagSet);
        post.setActive(true);
        post.setModerationStatus(ModerationStatus.NEW);
        post.setTime(LocalDateTime.now());
        post.setPostComments(comments);
        post.setViewCount(0);
        postRepository.save(post);
    }

    @Test
    public void shouldReturnPostByID() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/" + post.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(mapper.writeValueAsString(postMapper.convertToDto(
                        post, tags, new ArrayList<>(comments)))));
    }

    @After
    public void tearDown() {
        postRepository.delete(post);
    }
}
