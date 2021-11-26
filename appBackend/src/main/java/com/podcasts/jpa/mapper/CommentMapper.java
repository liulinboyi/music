package com.podcasts.jpa.mapper;

import com.podcasts.jpa.pojo.Comment;
import com.podcasts.jpa.pojo.PersonalColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface CommentMapper extends JpaRepository<Comment, Long> {
//    @Transactional
//    @Modifying
//    @Query(value = "update t_comment u set u.content=:#{#comment.content},u.userId=:#{#comment.userId},u.parentId=#{#comment.parentId},u.type=#{#comment.type} where u.id=:#{#comment.id}")
//    void addComments(Comment comment);

    @Query(value = "select t from t_comment t where t.parentId = :parentId and t.type = :type order by t.time asc")
    List<Comment> findAllComment(Long parentId, String type);
}
