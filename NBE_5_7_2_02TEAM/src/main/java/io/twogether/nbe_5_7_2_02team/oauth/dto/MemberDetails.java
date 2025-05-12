package io.twogether.nbe_5_7_2_02team.oauth.dto;


import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberDetails implements OAuth2User {

    @Setter
    private Long id;

    private String name;
    private String githubId;

    @Setter
    private Role role;

    @Setter
    private Map<String, Object> attributes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public static MemberDetails from(Member member) {
        MemberDetails memberDetails = new MemberDetails();

        memberDetails.id = member.getId();

        memberDetails.githubId = member.getGithubId();
        memberDetails.role = member.getRole();

        return memberDetails;
    }


    @Builder
    public MemberDetails(String name, String githubId, Map<String, Object> attributes) {
        this.name = name;
        this.githubId = githubId;
        this.attributes = attributes;
    }

}
