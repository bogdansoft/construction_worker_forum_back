package com.construction_worker_forum_back.validation;

import com.construction_worker_forum_back.model.entity.IEntity;
import com.construction_worker_forum_back.model.entity.User;
import com.construction_worker_forum_back.model.security.Role;
import com.construction_worker_forum_back.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Slf4j
public class EntityUpdateUtil {

    public static void setEntityLastEditor(UserRepository userRepository, IEntity entityToUpdate, Long editorId) {
        User author = entityToUpdate.getUser();

        if (Objects.equals(editorId, author.getId())) {
            entityToUpdate.setLastEditor(author);
        } else {
            User editor = userRepository.findById(editorId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            entityToUpdate.setLastEditor(editor);
        }
    }

    public static void throwIfEditorIsUserAndTimeIsExpired(IEntity entityToUpdate) {
        if (entityToUpdate.getUser().getUserRoles() != Role.USER) return;

        var entityCreatedDate = entityToUpdate.getCreatedAt().toInstant()
                .truncatedTo(ChronoUnit.MINUTES);
        var entityUpdateExpirationTime = entityCreatedDate.plus(15L, ChronoUnit.MINUTES)
                .truncatedTo(ChronoUnit.MINUTES);

        if (Instant.now().isAfter(entityUpdateExpirationTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
