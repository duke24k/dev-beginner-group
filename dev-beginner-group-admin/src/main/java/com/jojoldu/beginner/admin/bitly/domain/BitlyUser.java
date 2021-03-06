package com.jojoldu.beginner.admin.bitly.domain;

import com.jojoldu.beginner.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "UNI_BITLY_USERNAME", columnNames = "username"),
                @UniqueConstraint(name = "UNI_BITLY_EMAIL", columnNames = "email")
        }
)
public class BitlyUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessToken;
    private String name;    // 본명

    @Column(nullable = false)
    private String username;    // login (계정명 혹은 유니크명)

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public BitlyUser(String accessToken, @Nonnull String username, String name, @Nonnull String email, @Nonnull Role role) {
        this.accessToken = accessToken;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void refreshToken(String accessToken){
        this.accessToken = accessToken;
    }
}
