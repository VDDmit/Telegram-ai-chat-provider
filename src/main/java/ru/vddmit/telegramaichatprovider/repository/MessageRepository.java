package ru.vddmit.telegramaichatprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vddmit.telegramaichatprovider.entity.Message;
import ru.vddmit.telegramaichatprovider.entity.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findTop10ByUserOrderByCreatedAtDesc(User user);
}
