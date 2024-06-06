package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * init, destroy: default method 구현하지 않아도 괜찮다.
 * httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
 * 인증 x 사용자가 인증이 필요한 페이지에 접근하려고 하면 로그인 페이지로 redirect 만약 추후 로그인 성공하면
 * 다시 처음 요청 페이지(인증 x 상태에서 요청한 페이지)를 보여준다.
 */
@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whiteList =
            {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LoginCheckFilter.init");
    }

    @Override
    public void destroy() {
        log.info("LoginCheckFilter.destroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("LoginCheckFilter.doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("인증 체크 필터 시작{}", requestURI);

            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행{}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null){

                    log.info("미인증 사용자 요청{}", requestURI);
                    // 로그인으로 redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }

            }
            chain.doFilter(request, response);
        }
        catch (Exception e){
            throw e;
        }
        finally {
            log.info("인증 체크 필터 종료{}", requestURI);
        }

    }
    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI){
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
