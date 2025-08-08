package ru.vddmit.telegramaichatprovider.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "prompts")
public class Prompt {
    @Id
    UUID id;

    @Column(nullable = false, length = 100)
    String name;

    @Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
    String systemPrompt;

    @Column(name = "is_public", nullable = false)
    boolean isPublic = false;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", foreignKey = @ForeignKey(name = "fk_prompt_user"))
    User createdByUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    OffsetDateTime createdAt;
}
