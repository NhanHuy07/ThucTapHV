package com.dev.dungcony.modules.notifications.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.notifications.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "tbl_notifications")
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 30)
    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @Size(max = 100)
    @Column(name = "title", length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "type", nullable = false, length = 20)
    private NotificationType type;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "sender_id")
    private UUID senderId;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "for_admin", nullable = false)
    private Boolean forAdmin = true;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "readed", nullable = false)
    private Boolean readed = false;
}
