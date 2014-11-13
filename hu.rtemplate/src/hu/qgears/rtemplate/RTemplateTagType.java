package hu.qgears.rtemplate;

/**
 * Custom tag descriptor.
 * @author rizsi
 *
 */
public class RTemplateTagType {
	private String javaPre;
	private String javaPost;
	private String templatePre;
	private String templatePost;

	public RTemplateTagType(String javaPre, String javaPost,
			String templatePre, String templatePost) {
		super();
		this.javaPre = javaPre;
		this.javaPost = javaPost;
		this.templatePre = templatePre;
		this.templatePost = templatePost;
	}

	public String getJavaPre() {
		return javaPre;
	}

	public String getJavaPost() {
		return javaPost;
	}

	public String getTemplatePre() {
		return templatePre;
	}

	public String getTemplatePost() {
		return templatePost;
	}
	
}
