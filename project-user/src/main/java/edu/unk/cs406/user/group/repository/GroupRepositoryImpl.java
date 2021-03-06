package edu.unk.cs406.user.group.repository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

import edu.unk.cs406.user.group.entity.GroupEntity;

public class GroupRepositoryImpl implements GroupRepositoryCustom {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public GroupEntity updateGroupEntity(GroupEntity user) {
		Query query =  new Query(Criteria.where("id").is(user.getId()));
		Update update =  new Update();
		update.set("label", user.getLabel());
		update.set("description", user.getDescription());
		update.set("likes", user.getLikes());
		update.set("comments", user.getComments());
		update.set("location", user.getLocations());
		update.set("events", user.getEvents());
		update.set("lastModifiedDate", LocalDateTime.now());
		update.set("content", user.getContent());
		WriteResult result = null;
		
		if (user instanceof GroupEntity) {
			GroupEntity groupUser = (GroupEntity) user;	
			update.set("admins", groupUser.getAdmins());
			update.set("moderators", groupUser.getModerators());
			update.set("members", groupUser.getMembers());
			result = mongoTemplate.updateFirst(query, update, GroupEntity.class);
			if(result == null) {
				return null;
			}
			return (GroupEntity) user;
		}
		
		return null;
	}
}
