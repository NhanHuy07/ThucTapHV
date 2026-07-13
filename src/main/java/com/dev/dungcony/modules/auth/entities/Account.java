package com.dev.dungcony.modules.auth.entities;

import com.dev.dungcony.commons.entities.BaseEntity;
import com.dev.dungcony.modules.auth.enums.Role;
import com.dev.dungcony.modules.auth.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_accounts", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "username not blank")
    @Size(min = 3, max = 50, message = "username is length from 3 to 50 character")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "password not blank")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role = Role.CUSTOMER;

    @NotBlank(message = "Email not blank")
    @Email(message = "Email incorrect format")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "verify", nullable = false)
    private Boolean verify = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status = Status.ACTIVE;

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
