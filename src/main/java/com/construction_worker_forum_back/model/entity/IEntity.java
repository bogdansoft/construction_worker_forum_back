package com.construction_worker_forum_back.model.entity;

import java.util.Date;

public interface IEntity {

    User getUser();
    Date getCreatedAt();

    void setLastEditor(User user);
}
