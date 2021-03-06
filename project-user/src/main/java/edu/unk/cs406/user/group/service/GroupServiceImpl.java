package edu.unk.cs406.user.group.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.unk.cs406.user.entity.User;
import edu.unk.cs406.user.group.dto.CreateGroupDTO;
import edu.unk.cs406.user.group.dto.UpdateGroupDTO;
import edu.unk.cs406.user.group.entity.GroupEntity;
import edu.unk.cs406.user.group.repository.GroupRepository;

@Service
public class GroupServiceImpl implements GroupService {
	private static final Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
	private final GroupRepository repository;
	private final Validator validator;
	
	public GroupServiceImpl(GroupRepository repository, Validator validator) {
		this.repository = Objects.requireNonNull(repository);
		this.validator = Objects.requireNonNull(validator);
	}
	
	public GroupEntity create(CreateGroupDTO dto) {
		dto = Objects.requireNonNull(dto);
		
		Set<ConstraintViolation<CreateGroupDTO>> violations = this.validator.validate(dto);
		if(!violations.isEmpty()) {
			logger.error("Constraint Violation {}", dto.getClass().getName());
			throw new ConstraintViolationException(violations);
		}
		String id = UUID.randomUUID().toString();
		while(this.repository.exists(id)) {
			id = UUID.randomUUID().toString();
		}
		GroupEntity entity = new GroupEntity();
		
		GroupEntity UPE = new GroupEntity();
		UPE.setId(id);
		UPE.setLabel(dto.getLabel());
		UPE.setDescription(dto.getDescription());

		
		return this.repository.save(entity);
	}

	public GroupEntity get(String id) {
		logger.info("getting id {}", id);
		
		GroupEntity user = this.repository.findOne(id);
		
		if(user == null) {
			logger.warn("Unable to get {} id {} not found", GroupEntity.class.getName(), id);
			return null;
		} else {
			return user;
		}
	}

	public User find(String label) {
		User user = this.repository.findByLabel(label);
		
		if(user == null) {
			logger.warn("Unable to find {} label {} not found", GroupEntity.class.getName(), label);
			return null;
		} else {
			return user;
		}
	}

	public List<GroupEntity> find() {
		
		return this.repository.findAll();
	}

	public User update(UpdateGroupDTO dto) {
		//validate arguments
		dto = Objects.requireNonNull(dto,"argument zero must not be null");
		Set<ConstraintViolation<UpdateGroupDTO>> violations = this.validator.validate(dto);
		if(violations.size() > 0) {
			ConstraintViolationException e = new ConstraintViolationException("dto is invalid", violations);
			logger.error("Failed to validate {}", CreateGroupDTO.class.getName(), e);
			throw e;
		}
		
		//find entity
		GroupEntity entity;
		try {
			entity = (GroupEntity) Objects.requireNonNull(this.repository.findOne(dto.getId()));
		} catch(NullPointerException e) {
			logger.error("unable to update {} id {} does not exist", GroupEntity.class.getName(), dto.getId(), e);
			return null;
		}
		
		//Save Changes
		entity = this.repository.save(entity);
		
		logger.info("Updated {} id {}", GroupEntity.class.getName(), entity.getId());
		
		return entity;
	}

	public boolean delete(String id) {
		User user = this.repository.findOne(id);
		
		if(user == null) {
			logger.warn("Unable to delete {} id {} not found", GroupEntity.class.getName(), id);
			return false;
		} else {
			try {
				this.repository.delete(id);
				logger.info("deleted {} id {}", GroupEntity.class.getName(), id);
				return true;
			} catch(Exception e) {
				logger.error("Unable to delete {} id {}", GroupEntity.class.getName(), id, e);
			}
		}
		return false;
	}

	@Override
	public void deleteAll() {
		this.repository.deleteAll();
		
	}
}
