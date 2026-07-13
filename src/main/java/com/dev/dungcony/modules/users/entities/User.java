package com.dev.dungcony.modules.users.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "tbl_users")
public class User extends BaseEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    @Column(name = "f_name")
    private String firstName;

    @Column(name = "l_name")
    private String lastName;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "last_purchase_time")
    private Instant lastPurchaseTime;

    @Column(name = "toltal_purchase", nullable = false)
    private BigDecimal toltalPurchase = BigDecimal.ZERO;
    
    @Column(name = "acc_id", unique = true)
    private Integer accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rank_id", nullable = false)
    private Rank rank;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Receiver> receivers = new ArrayList<>();
}
