package ru.vddmit.telegramaichatprovider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vddmit.telegramaichatprovider.entity.Prompt;

import java.util.UUID;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, UUID> {
}
