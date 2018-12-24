package me.timulys.SpringBootCommunityWeb.resolver;

import me.timulys.SpringBootCommunityWeb.annotation.SocialUser;
import me.timulys.SpringBootCommunityWeb.domain.User;
import me.timulys.SpringBootCommunityWeb.domain.enums.SocialType;
import me.timulys.SpringBootCommunityWeb.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static me.timulys.SpringBootCommunityWeb.domain.enums.SocialType.*;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(SocialUser.class) != null &&
                methodParameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("user");
        return getUser(user, session);
    }

    private User getUser(User user, HttpSession session) {
        if (user == null) {
            try {
                OAuth2Authentication authentication =
                        (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
                Map<String, String> map =
                        (Map<String, String>) authentication.getUserAuthentication().getDetails();
                User convertUser = convertUser(String.valueOf(authentication.getAuthorities().toArray()[0]), map);
                user = userRepository.findByEmail(convertUser.getEmail());
                if (user == null) user = userRepository.save(convertUser);

                setRoleIfNotSame(user, authentication, map);
                session.setAttribute("user", user);
            } catch (ClassCastException e) {
                return user;
            }
        }
        return user;
    }

    private User convertUser(String authority, Map<String, String> map) {
        if (FACEBOOK.isEquals(authority)) return getModernUser(FACEBOOK, map);
        else if (GOOGLE.isEquals(authority)) return getModernUser(GOOGLE, map);
        else if (KAKAO.isEquals(authority)) return getKakaoUser(map);
        return null;
    }

    private User getModernUser(SocialType socialType, Map<String, String> map) {
        return User.builder()
                .name(map.get("name"))
                .email(map.get("email"))
                .principal(map.get("id"))
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .build();
    }

    private User getKakaoUser(Map<String, String> map) {
        HashMap<String, String> propertyMap = (HashMap<String, String>)(Object) map.get("properties");
        return User.builder()
                .name(propertyMap.get("nickname"))
                .email(map.get("kaccount_email"))
                .principal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdDate(LocalDateTime.now())
                .build();
    }

    private void setRoleIfNotSame(User user, OAuth2Authentication authentication, Map<String, String> map) {
        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(map, "N/A",
                    AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
        }
    }
}
