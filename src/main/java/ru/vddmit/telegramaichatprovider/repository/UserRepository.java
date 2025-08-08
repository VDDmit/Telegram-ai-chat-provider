package ru.vddmit.telegramaichatprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vddmit.telegramaichatprovider.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
