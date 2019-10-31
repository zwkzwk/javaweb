package io.github.dunwu.javaee.filter;

import io.github.dunwu.javaee.filter.wrapper.UploadRequestWrapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2017/3/27.
 */
public class CharacterEncodingFilter extends MyFilter {

	private String characterEncoding;

	private boolean enabled;

	@Override
	public void init(FilterConfig config) throws ServletException {
		super.init(config);

		characterEncoding = config.getInitParameter("characterEncoding");
		enabled = "true".equalsIgnoreCase(characterEncoding.trim()) || "1".equalsIgnoreCase(characterEncoding.trim());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		logger.info("{} 开始做过滤处理", this.getClass().getName());

		if (enabled || StringUtils.isNotBlank(characterEncoding)) {
			request.setCharacterEncoding(characterEncoding);
			response.setCharacterEncoding(characterEncoding);
		}

		logger.info("系统设置HTTP请求和应答的默认编码为 {}", characterEncoding);
		chain.doFilter(request, response);
	}

	public static class UploadFilter implements Filter {

		public void destroy() {

		}

		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

			UploadRequestWrapper uploadRequest = new UploadRequestWrapper((HttpServletRequest) request);

			chain.doFilter(uploadRequest, response);
		}

		public void init(FilterConfig filterConfig) throws ServletException {

		}

	}

}
