package com.project.cms.repsAndServiesies;

import com.project.cms.entities.Posts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    PostRepository postRepository;
    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    public void save(Posts post){
        postRepository.save(post);
    }
    public List<Posts> findAll(){
        return postRepository.findAll();
    }
    public List<Posts> findPostsByLogin(String login){
        return postRepository.findPostsByLogin(login);
    }
}
