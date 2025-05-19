package io.twogether.nbe_5_7_2_02team.oauth.dto.common;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetails implements OAuth2User, UserDetails {

    @Setter private Long id;

    private String name;
    @Setter private String email;

    @Setter private Role role;

    @Setter private Map<String, Object> attributes;

    private String githubId;

    private String avatarUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    public static MemberDetails from(Member member) {
        MemberDetails memberDetails = new MemberDetails();

        memberDetails.id = member.getId();
        memberDetails.name = member.getName();
        memberDetails.email = member.getEmail();
        memberDetails.role = member.getRole();

        return memberDetails;
    }

    @Builder
    public MemberDetails(
            String name,
            String email,
            Map<String, Object> attributes,
            String githubId,
            String avatarUrl) {
        this.name = name;
        this.email = email;
        this.attributes = attributes;
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
    }
}
