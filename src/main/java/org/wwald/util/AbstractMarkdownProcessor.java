package org.wwald.util;

import com.cforcoding.jmd.MarkDown;
import com.petebevin.markdown.MarkdownProcessor;

public abstract class AbstractMarkdownProcessor {

	static class JMDMarkdownProcessor extends AbstractMarkdownProcessor {
		MarkDown markdown = new MarkDown();
		@Override
		public String process(String mardownText) {
			return markdown.transform(mardownText);
		}
		
	}
	
	static class MarkdownJMarkdownProcessor extends AbstractMarkdownProcessor {
		MarkdownProcessor markdown = new MarkdownProcessor();
		@Override
		public String process(String mardownText) {
			return markdown.markdown(mardownText);
		}
		
	}

	public static AbstractMarkdownProcessor JMD_MARKDOWN_PROCESSOR = 
												new JMDMarkdownProcessor();
	
	public static AbstractMarkdownProcessor MARKDOWNJ_PROCESSOR = 
												new MarkdownJMarkdownProcessor();
	
	public abstract String process(String mardownText);

}



