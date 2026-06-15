package com.wara.usermanagement.usermanagement.repository;

import com.wara.usermanagement.usermanagement.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private final Map<Long, User> store = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void save(User user) {
        if (user.getId() == null) {
            user.setId(idSequence.getAndIncrement());
        }
        store.put(user.getId(), user);
    }

    public void delete(User user) {
        store.remove(user.getId());
    }
}
