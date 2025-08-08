package ru.vddmit.telegramaichatprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vddmit.telegramaichatprovider.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
